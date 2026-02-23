package it.tinna.smartdoc.shared.dto.documenti;

import java.io.Serializable;
import java.math.BigDecimal;

public interface HasContabilita extends Serializable{
	
	Integer getIdParametrizzazione();
	BigDecimal getImponibileContabilita();
	BigDecimal getImpostaContabilita();
	BigDecimal getTotaleContabilita();
	
	void setIdParametrizzazione(Integer idParametrizzazione);
	void setImponibileContabilita(BigDecimal imponibileContabilita);
	void setImpostaContabilita(BigDecimal impostaContabilita);
	void setTotaleContabilita(BigDecimal totaleContabilita);

}

