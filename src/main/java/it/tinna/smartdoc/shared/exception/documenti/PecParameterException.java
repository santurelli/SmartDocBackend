package it.tinna.smartdoc.shared.exception.documenti;

import java.io.Serializable;

/**
 * Eccezione sollevata quando i parametri della PEC non sono impostati nella configurazione
 * @author Pietro
 *
 */
@SuppressWarnings("serial")
public class PecParameterException extends Exception implements Serializable {
	
	public PecParameterException() {
	}

}

