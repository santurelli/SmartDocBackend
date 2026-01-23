package it.tinna.smartdoc.shared.dto.aliquoteiva;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class AliquotaIvaDto extends BaseDto
{

    @Expose
    private String  classe;

    @Expose
    private String  codice;

    private String  descrClasse;

    @Expose
    private String  descrizione;

    @Expose
    private Double  imposta;

    private String  impostaFormattata;

    private Double  indetraibilita;

    private String  note;

    @Expose
    private Integer predefinita;

    public String getClasse()
    {
        return classe;
    }

    public String getCodice()
    {
        return codice;
    }

    public String getDescrClasse()
    {
        return descrClasse;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public Double getImposta()
    {
        return imposta;
    }

    public String getImpostaFormattata()
    {
        return impostaFormattata;
    }

    public Double getIndetraibilita()
    {
        return indetraibilita;
    }

    public String getNote()
    {
        return note;
    }

    public Integer getPredefinita()
    {
        return predefinita;
    }

    public void setClasse(String classe)
    {
        this.classe = classe;
        for ( int i = 0; i < ClasseIvaEnum.values().length; i++ )
        {
            if ( ClasseIvaEnum.values()[i].getValore().equals(classe) )
            {
                setDescrClasse(ClasseIvaEnum.values()[i].getDescrizione());
            }
        }
    }

    public void setCodice(String codice)
    {
        this.codice = codice;
    }

    public void setDescrClasse(String descrClasse)
    {
        this.descrClasse = descrClasse;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setImposta(Double imposta)
    {
        this.imposta = imposta;
    }

    public void setImpostaFormattata(String impostaFormattata)
    {
        this.impostaFormattata = impostaFormattata;
    }

    public void setIndetraibilita(Double indetraibilita)
    {
        this.indetraibilita = indetraibilita;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public void setPredefinita(Integer predefinita)
    {
        this.predefinita = predefinita;
    }

}
