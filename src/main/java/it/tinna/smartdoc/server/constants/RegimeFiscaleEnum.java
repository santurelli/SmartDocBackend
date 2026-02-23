package it.tinna.smartdoc.server.constants;

public enum RegimeFiscaleEnum
{
 ORDINARIO("Regime Ordinario"),
 CONTRIBUENTI_MINIMI("Regime dei contribuenti minimi (art. 1,c.96-117, L. 244/2007)"),
 INIZIATIVE_PRODUTTIVE("Nuove iniziative produttive (art.13, L.388/00)"),
 AGRICOLTURA_PESCA("Agricoltura e attività connesse e pesca (art. 34 e 34-bis, D.P.R. 633/1972)"),
 SALI_TABACCHI("Vendita sali e tabacchi (art. 74, c.1, D.P.R. 633/1972)"),
 FIAMMIFERI("Commercio dei fiammiferi (art. 74, c.1, D.P.R. 633/1972)"),
 EDITORIA("Editoria (art. 74, c.1, D.P.R. 633/1972)"),
 TELEFONIA("Gestione di servizi di telefonia pubblica (art. 74, c.1, D.P.R. 633/1972)"),
 TRASPORTO_PUBBLICO("Rivendita di documenti di trasporto pubblico e di sosta (art. 74, c.1, D.P.R. 633/1972)"),
 INTRATTENIMENTI("Intrattenimenti, giochi e altre attività   di cui alla tariffa allegata al D.P.R. 640/72 (art. 74, c.6, D.P.R. 633/1972)"),
 VIAGGI_TURISMO("Agenzie di viaggi e turismo (art. 74-ter, D.P.R. 633/1972)"),
 AGRITURISMO("Agriturismo (art. 5, c.2, L. 413/1991)"),
 DOMICILIO("Vendite a domicilio (art. 25-bis, c.6, D.P.R. 600/1973)"),
 RIVENDITA_ARTE_COLLEZIONE("Rivendita di beni usati, di oggetti  d’arte, d’antiquariato o da collezione (art. 36, D.L. 41/1995)"),
 AGENZIE_ARTE_COLLEZIONE("Agenzie di vendite all’asta di oggetti d’arte, antiquariato o da collezione (art. 40-bis, D.L. 41/1995)"),
 CASSA_PA("IVA per cassa P.A. (art. 6, c.5, D.P.R. 633/1972)"),
 CASSA("IVA per cassa (art. 32-bis, D.L. 83/2012)"),
 ALTRO("Altro"),
 REGIME_FORFETTARIO("Regime forfettario");

    private String descrizione;

    private RegimeFiscaleEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

}

