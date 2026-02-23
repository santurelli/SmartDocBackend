package it.tinna.smartdoc.batch.dto.justdesign.documenti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.justdesign.BaseDto;

public abstract class DocumentoDto extends BaseDto
{

    private String addon;        // eventuale bis,ter, etc...

    @Expose
    private String dataDocumento;

    private String flDeleted;

    @Expose
    private String id;

    @Expose
    private String imponibile;

    @Expose
    private String numDoc;

    @Expose
    private String sconto;

    private String totale;

    public String getAddon()
    {
        return addon;
    }

    public String getDataDocumento()
    {
        return dataDocumento;
    }

    public String getFlDeleted()
    {
        return flDeleted;
    }

    public String getId()
    {
        return id;
    }

    public String getImponibile()
    {
        return imponibile;
    }

    public String getNumDoc()
    {
        return numDoc;
    }

    public String getSconto()
    {
        return sconto;
    }

    public String getTotale()
    {
        return totale;
    }

    public void setAddon(String addon)
    {
        this.addon = addon;
    }

    public void setDataDocumento(String dataVendita)
    {
        this.dataDocumento = dataVendita;
    }

    public void setFlDeleted(String flDeleted)
    {
        this.flDeleted = flDeleted;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setImponibile(String imponibile)
    {
        this.imponibile = imponibile;
    }

    public void setNumDoc(String numDoc)
    {
        this.numDoc = numDoc;
    }

    public void setSconto(String sconto)
    {
        this.sconto = sconto;
    }

    public void setTotale(String totale)
    {
        this.totale = totale;
    }

}

