package it.tinna.smartdoc.server.service.primanota;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.primanota.PrimaNotaDto;

/**
 * Genera l'export della Prima Nota in formati leggibili dai principali software di studio
 * (Zucchetti, TeamSystem, Datev) per evitare la re-imputazione manuale al commercialista.
 */
public class PrimaNotaExportService
{

    /**
     * CSV generico "prima nota cassa/banca": non è un vero piano dei conti in partita doppia
     * (SmartDoc non gestisce conti/sottoconti), ma espone tutte le informazioni di movimento
     * (data, causale, controparte, entrata/uscita, risorsa, riferimento) in modo che l'operatore
     * possa mappare le colonne nella procedura di import guidata del proprio software.
     */
    public byte[] buildCsv(List<PrimaNotaDto> righe) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try ( PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8) )
        {
            // BOM per corretta apertura in Excel con caratteri accentati
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);

            writer.println(String.join(";",
                "Data", "TipoDocumento", "Causale", "Descrizione", "Controparte",
                "PartitaIva", "CodiceFiscale", "Risorsa", "ModalitaPagamento",
                "Entrata", "Uscita", "RiferimentoPagamento"));

            for ( PrimaNotaDto riga : righe )
            {
                String partitaIva = riga.getObjSoggetto() != null ? riga.getObjSoggetto().getPartitaIva() : null;
                String codiceFiscale = riga.getObjSoggetto() != null ? riga.getObjSoggetto().getCodiceFiscale() : null;

                writer.println(String.join(";",
                    csv(riga.getDtDocumento()),
                    csv(riga.getTipoDocumento()),
                    csv(riga.getDescrTipoPagamento()),
                    csv(riga.getDescrDocumento()),
                    csv(riga.getSoggetto()),
                    csv(partitaIva),
                    csv(codiceFiscale),
                    csv(riga.getRisorsa()),
                    csv(riga.getModalita()),
                    csvImporto(riga.getEntrate()),
                    csvImporto(riga.getUscite()),
                    csv(riga.getRiferimentoPagamento())));
            }
        }
        return baos.toByteArray();
    }

    /**
     * Formato Datev EXTF "Buchungsstapel" (movimenti contabili), versione 700.
     * Specifica pubblica Datev: prima riga di metadati, seconda riga con i nomi colonna,
     * righe successive con i movimenti. Vengono valorizzate solo le colonne obbligatorie
     * (Umsatz, Soll/Haben-Kennzeichen, Konto, Gegenkonto, BU-Schluessel, Belegdatum,
     * Belegfeld 1, Buchungstext); i conti (Konto/Gegenkonto) vanno rivisti dal commercialista
     * in base al piano dei conti del cliente, dato che SmartDoc non gestisce una contabilità
     * in partita doppia.
     */
    public byte[] buildDatev(List<PrimaNotaDto> righe,
                             DatiAziendaDto datiAzienda,
                             String beraterNr,
                             String mandantNr,
                             String dtFrom,
                             String dtTo) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try ( PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.ISO_8859_1) )
        {
            String oggi = new SimpleDateFormat("yyyyMMdd'000000000'").format(new Date());
            String annoEsercizioInizio = datevAnnoEsercizioInizio(dtFrom);
            String beraterEff = StringUtils.defaultIfBlank(beraterNr, "0");
            String mandantEff = StringUtils.defaultIfBlank(mandantNr, "0");
            String belegDa = datevData(dtFrom);
            String belegA = datevData(dtTo);
            String ragioneSociale = datiAzienda != null ? StringUtils.defaultString(datiAzienda.getDenominazione()) : "";

            // Riga 1: metadati header (formato EXTF versione 700, tipo 21 = Buchungsstapel)
            writer.println(String.join(";",
                "\"EXTF\"", "700", "21", "\"Buchungsstapel\"", "7",
                oggi, "", "\"SmartDoc\"", "\"SmartDoc\"",
                beraterEff, mandantEff, annoEsercizioInizio, "4",
                belegDa, belegA, "\"" + escapeDatev(ragioneSociale) + "\"", "",
                "1", "0", "0", "\"EUR\"", "", "", "", "", "", "", "0"));

            // Riga 2: intestazione colonne (sottoinsieme obbligatorio)
            writer.println(String.join(";",
                "\"Umsatz (ohne Soll/Haben-Kz)\"", "\"Soll/Haben-Kennzeichen\"", "\"WKZ Umsatz\"",
                "\"Konto\"", "\"Gegenkonto (ohne BU-Schluessel)\"", "\"BU-Schluessel\"",
                "\"Belegdatum\"", "\"Belegfeld 1\"", "\"Buchungstext\""));

            for ( PrimaNotaDto riga : righe )
            {
                boolean entrata = riga.getEntrate() != null && riga.getEntrate().compareTo(BigDecimal.ZERO) != 0;
                BigDecimal importo = entrata ? riga.getEntrate() : riga.getUscite();
                if ( importo == null )
                {
                    continue;
                }
                String sollHaben = entrata ? "H" : "S"; // Haben = avere (entrata), Soll = dare (uscita)

                writer.println(String.join(";",
                    "\"" + importo.abs().toString().replace('.', ',') + "\"",
                    "\"" + sollHaben + "\"",
                    "\"EUR\"",
                    "0", // Konto: da assegnare dal commercialista in base al piano dei conti
                    "0", // Gegenkonto: da assegnare dal commercialista
                    "",
                    datevData(riga.getDtDocumento()),
                    "\"" + escapeDatev(riga.getRiferimentoPagamento()) + "\"",
                    "\"" + escapeDatev(StringUtils.defaultString(riga.getDescrDocumento()) + " - " + StringUtils.defaultString(riga.getSoggetto())) + "\""));
            }
        }
        return baos.toByteArray();
    }

    private String csv(String value)
    {
        if ( value == null )
        {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if ( escaped.contains(";") || escaped.contains("\"") || escaped.contains("\n") )
        {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private String csvImporto(BigDecimal importo)
    {
        return importo != null ? importo.toString().replace('.', ',') : "";
    }

    private String escapeDatev(String value)
    {
        return value == null ? "" : value.replace("\"", "").replace(";", ",");
    }

    /**
     * Le date in ingresso arrivano nel formato "gg/mm/aaaa" usato dal resto dell'app;
     * Datev richiede "ggMM" (giorno+mese, l'anno è implicito nell'esercizio dichiarato in header).
     */
    private String datevData(String dataItaliana)
    {
        if ( StringUtils.isBlank(dataItaliana) || dataItaliana.length() < 10 )
        {
            return "";
        }
        String giorno = dataItaliana.substring(0, 2);
        String mese = dataItaliana.substring(3, 5);
        return giorno + mese;
    }

    private String datevAnnoEsercizioInizio(String dtFrom)
    {
        if ( StringUtils.isBlank(dtFrom) || dtFrom.length() < 10 )
        {
            return new SimpleDateFormat("yyyy").format(new Date()) + "0101";
        }
        String anno = dtFrom.substring(6, 10);
        return anno + "0101";
    }

}
