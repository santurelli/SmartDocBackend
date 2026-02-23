package it.tinna.smartdoc.shared.exception.documenti;

import java.io.Serializable;

/**
 * Eccezione sollevata quando nella configurazione non è presente l'indirizzo pec dell'SDI
 * a cui inviare la fattura firmata
 * @author Pietro
 *
 */
@SuppressWarnings("serial")
public class PecDestinationAddressException extends Exception implements Serializable {
	
	public PecDestinationAddressException() {
	}

}

