package it.tinna.smartdoc.server.util.print.receipt.factory;

import it.tinna.smartdoc.shared.dto.documenti.ScontrinoDto;

public abstract class IPrinter {
	
	public abstract String getStringaChiusuraFiscale();
	
	public abstract String getStringaScontrino(ScontrinoDto dto);

}

