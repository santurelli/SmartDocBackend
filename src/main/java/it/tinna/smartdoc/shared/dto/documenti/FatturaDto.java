package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class FatturaDto extends FatturaElettronicaDto implements HasContabilita
{

    @Expose
    private String        dataScontrino;

    private List<Integer> idConfOrdine;

    private List<Integer> idDdt;

    @Expose
    private long          idFatturaCollegata;

    private Integer       idParametrizzazione;

    private List<Integer> idPreventivi;

    @Expose
    private BigDecimal    imponibileContabilita;

    @Expose
    private BigDecimal    impostaContabilita;

    @Expose
    private Integer       numeroScontrino;

    @Expose
    private TipoFattura   tipoFattura;

    @Expose
    private BigDecimal    totaleContabilita;



    @Expose
    private String        erroreConsegna;

    @Expose
    private String        erroreXml;

    // Manual overrides if necessary, but HasContabilita requires these:
    @Override
    public Integer getIdParametrizzazione() { return idParametrizzazione; }
    @Override
    public void setIdParametrizzazione(Integer id) { this.idParametrizzazione = id; }
    
    @Override
    public BigDecimal getImponibileContabilita() { return imponibileContabilita; }
    @Override
    public void setImponibileContabilita(BigDecimal val) { this.imponibileContabilita = val; }
    
    @Override
    public BigDecimal getImpostaContabilita() { return impostaContabilita; }
    @Override
    public void setImpostaContabilita(BigDecimal val) { this.impostaContabilita = val; }
    
    @Override
    public BigDecimal getTotaleContabilita() { return totaleContabilita; }
    @Override
    public void setTotaleContabilita(BigDecimal val) { this.totaleContabilita = val; }

}
