package it.tinna.smartdoc.batch.readers.invio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import com.google.gson.Gson;

import it.tinna.smartdoc.batch.dto.fastorder.comande.PiattoComandaDto;
import it.tinna.smartdoc.server.constants.MimeTypeConstants;
import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import lombok.Setter;

public class InvioFattureElettronicheFastOrderReader implements ItemStreamReader<FatturaElettronicaWrapperDto>
{

    private Logger                             logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FatturaElettronicaDelegate         fatturaelettronicaDelegate;

    @Setter
    private String                             pathFattura;                                      // fattura inviata in formato base64

    @Setter
    private String                             nomeStore;

    @Setter
    private String                             dbKey;

    private List<FatturaElettronicaWrapperDto> elencoFatture;

    @Override
    public void close() throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    private FatturaElettronicaDto creaFatturaElettronica(it.tinna.smartdoc.batch.dto.fastorder.documenti.FatturaDto dto) throws Exception
    {
        FatturaDto feDto = new FatturaDto();
        feDto.setId(new Long(dto.getId()));
        feDto.setTipoFattura(TipoFattura.FATTURA);
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setTipologia(TipologiaClienteFornitore.PRIVATO);
        clienteDto.setPartitaIva(dto.getCliente().getPartitaIva());
        clienteDto.setCodiceFiscale(dto.getCliente().getCodiceFiscale());
        clienteDto.setDenominazione(dto.getCliente().getDenominazione());
        feDto.setClienteDto(clienteDto);
        feDto.setIndirizzoIntestazione(dto.getCliente().getIndirizzo());
        feDto.setCittaIntestazione(dto.getCliente().getCitta());
        feDto.setCapIntestazione(dto.getCliente().getCap());
        feDto.setProvinciaIntestazione(dto.getCliente().getProvincia());
        feDto.setCodiceUfficioDestinazione(dto.getCliente().getCodiceDestinatario());
        feDto.setPec(dto.getCliente().getPec());

        feDto.setDataDocumento(dto.getData());
        feDto.setNumDocumento(dto.getNumero());
        // feDto.setParticella("/" + StringUtils.left(nomeStore.replaceAll(" ", "").toUpperCase(), 20));

        feDto.setTotale(dto.getPagamentoComandaDto().getTotale());
        if ( dto.getPagamentoComandaDto().getSconto() != null && dto.getPagamentoComandaDto().getSconto() != 0d )
        {
            feDto.setTotale(BigDecimal.valueOf(feDto.getTotale()).add(BigDecimal.valueOf(dto.getPagamentoComandaDto().getSconto())).doubleValue());
        }
        List<ProdottoDocumentoDto> articoli = new ArrayList<ProdottoDocumentoDto>();
        feDto.setProdotti(articoli);
        if ( StringUtils.isBlank(dto.getTestoModelloStampa()) )
        {
            for ( int i = 0; i < dto.getPiatti().size(); i++ )
            {
                PiattoComandaDto piattoComandaDto = dto.getPiatti().get(i);
                if ( piattoComandaDto.getFlVariante() != null && piattoComandaDto.getFlVariante().intValue() == 1 )
                {
                    for ( int j = i - 1; j >= 0; j-- )
                    {
                        if ( dto.getPiatti().get(j).getFlVariante().intValue() == 0 )
                        {
                            piattoComandaDto.setQuantita(dto.getPiatti().get(j).getQuantita());
                            break;
                        }
                    }
                }
                ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
                pdDto.setFuoriMagazzino(true);
                pdDto.setFmDescrizione(piattoComandaDto.getDescrizionePiatto());
                pdDto.setQuantita(new Double(piattoComandaDto.getQuantita()));
                if ( piattoComandaDto.getIva().equals(4d) )
                {
                    pdDto.setIdAliquotaIva(11);
                }
                else if ( piattoComandaDto.getIva().equals(22d) )
                {
                    pdDto.setIdAliquotaIva(6);
                }
                else if ( piattoComandaDto.getIva().equals(10d) )
                {
                    pdDto.setIdAliquotaIva(1);
                }
                // da fastorder il prezzo arriva ivato. Quindi, devo calcolare il netto
                BigDecimal prezzoConIva = BigDecimal.valueOf(piattoComandaDto.getPrezzo());
                BigDecimal iva = BigDecimal.valueOf(piattoComandaDto.getIva()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal prezzoSenzaIva = prezzoConIva.divide(BigDecimal.ONE.add(iva), 2, RoundingMode.HALF_UP);
                pdDto.setPrezzo(prezzoSenzaIva.doubleValue());
                BigDecimal totaleCompresoSconto = prezzoSenzaIva.multiply(new BigDecimal(piattoComandaDto.getQuantita(), new MathContext(2, RoundingMode.HALF_UP)));
                pdDto.setTotaleSenzaIva(totaleCompresoSconto.doubleValue());
                // if ( StringUtils.isNotBlank(piattoComandaDto.getSconto()) && (Double.parseDouble(piattoComandaDto.getSconto()) != 0d) )
                // {
                // pdDto.setSconto(piattoComandaDto.getSconto() + "%");
                // }

                articoli.add(pdDto);
            }
        }
        else
        {
            ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
            pdDto.setFuoriMagazzino(true);
            pdDto.setFmDescrizione(StringUtils.left(dto.getTestoModelloStampa().replaceAll("\\n", " "), 1000));
            pdDto.setQuantita(1d);
            pdDto.setIdAliquotaIva(1);
            BigDecimal prezzoConIva = BigDecimal.valueOf(feDto.getTotale());
            BigDecimal iva = BigDecimal.TEN.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal prezzoSenzaIva = prezzoConIva.divide(BigDecimal.ONE.add(iva), 2, RoundingMode.HALF_UP);
            pdDto.setPrezzo(prezzoSenzaIva.doubleValue());
            pdDto.setTotaleSenzaIva(pdDto.getPrezzo());

            articoli.add(pdDto);
        }
        List<ScadenzaPagamentoDocumentoDto> scadenze = new ArrayList<>();
        feDto.setListaScadenzePagamentiDocumento(scadenze);
        ScadenzaPagamentoDocumentoDto scadenzaDto = new ScadenzaPagamentoDocumentoDto();
        scadenze.add(scadenzaDto);
        scadenzaDto.setModalitaPagamento(ModalitaPagamentoEnum.CONTANTI.name());
        scadenzaDto.setDtScadenza(dto.getData());
        scadenzaDto.setImporto(dto.getPagamentoComandaDto().getTotale());

        if ( dto.getPagamentoComandaDto().getSconto() != null && dto.getPagamentoComandaDto().getSconto() != 0d )
        {
            feDto.setSconto(dto.getPagamentoComandaDto().getSconto().toString());
        }
        feDto.setEsigibilitaDifferita(0);
        try
        {
            fatturaelettronicaDelegate.getFatturaElettronica(feDto, nomeStore);
        }
        catch ( Exception e )
        {
            logger.error("Errore nella generazione della fattura elettronica per lo il ristorante/bar {}: {}", nomeStore, e.getMessage());
            feDto.setErroreValidazioneXml(StringUtils.defaultString(feDto.getErroreValidazioneXml()) + "Errore imprevisto generazione: " + e.getMessage());
        }
        return feDto;
    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {
        elencoFatture = new ArrayList<FatturaElettronicaWrapperDto>();
        String base64Fattura = null;
        File fileFattura = new File(pathFattura);
        try
        {
            base64Fattura = FileUtils.readFileToString(fileFattura, StandardCharsets.UTF_8.name());
        }
        catch ( IOException e1 )
        {
            logger.error("Errore nella lettura del file temporaneo della fattura inviata dal ristorante/bar {}", nomeStore, e1);
            throw new ItemStreamException(ExceptionUtils.getMessage(e1));
        }
        finally
        {
            FileUtils.deleteQuietly(fileFattura);
        }
        byte[] b = Base64Utils.decodeFromString(base64Fattura);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(b);
        Tika tika = new Tika();
        String mimeType = null;
        try
        {
            mimeType = tika.detect(inputStream);
            String str = null;
            if ( mimeType.equals(MimeTypeConstants.APPLICATION_GZIP) )
            {
                logger.info("Inviata fattura compressa in formato {}", MimeTypeConstants.APPLICATION_GZIP);
                GZIPInputStream gis = new GZIPInputStream(inputStream);
                StringWriter writer = new StringWriter();
                IOUtils.copy(gis, writer, StandardCharsets.UTF_8);
                str = writer.toString();
            }
            else if ( mimeType.equals(MimeTypeConstants.TEXT_PLAIN) )
            {
                logger.info("Inviata fattura non compressa");
                str = new String(b, StandardCharsets.UTF_8.name());
            }
            else
            {
                logger.error("Formato della fattura inviata non supportato. Formato risposta: {}", mimeType);
                throw new IOException("Formato della fattura inviata non supportato. Formato risposta: " + mimeType);
            }
            Gson gson = new Gson();
            it.tinna.smartdoc.batch.dto.fastorder.documenti.FatturaDto dto = gson.fromJson(str, it.tinna.smartdoc.batch.dto.fastorder.documenti.FatturaDto.class);
            logger.info("Ricevut json da fastorder: {}", str);
            boolean isInviabile = false;
            try
            {
                isInviabile = fatturaelettronicaDelegate.isFatturaInviabile(nomeStore, new Long(dto.getId()));
            }
            catch ( NumberFormatException e1 )
            {
                logger.error("Errore nella verifica della inviabilità della fattura {} per il ristorante/bar {}", dto.getId(), nomeStore, e1);
                throw new ItemStreamException(ExceptionUtils.getRootCause(e1).getMessage());
            }
            catch ( SQLException e2 )
            {
                throw new ItemStreamException(ExceptionUtils.getRootCause(e2).getMessage());
            }
            if ( isInviabile )
            {
                DatabaseContextHolder.set(dbKey);
                // creo la fattura elettronica
                FatturaElettronicaDto feDto;
                try
                {
                    feDto = creaFatturaElettronica(dto);
                }
                catch ( Exception e )
                {
                    throw new ItemStreamException(ExceptionUtils.getMessage(e));
                }
                FatturaElettronicaWrapperDto result = new FatturaElettronicaWrapperDto();
                result.setFattura(feDto);
                if ( StringUtils.isEmpty(feDto.getErroreValidazioneXml()) )
                {
                    result.setFlussoFatturaElettronica(feDto.getXmlFattura().getBytes());
                }
                elencoFatture.add(result);
            }
            else
            {
                logger.info("La fattura {} per lo store {} non è inviabile", dto.getId(), nomeStore);
            }
        }
        catch ( IOException e )
        {
            logger.error("Errore nella determinazione del formato della fattura inviata", e);
            throw new ItemStreamException(ExceptionUtils.getMessage(e));
        }
        finally
        {
            DatabaseContextHolder.clear();
        }
    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public FatturaElettronicaWrapperDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException
    {
        if ( elencoFatture.size() > 0 )
        {
            return elencoFatture.remove(0);
        }
        else
        {
            return null;
        }
    }

}

