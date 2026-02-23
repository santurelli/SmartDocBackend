package it.tinna.smartdoc.server.util.print.receipt.factory;

import it.tinna.smartdoc.server.util.print.receipt.EditXonXoff;

public class PrinterFactory {
	
	public static IPrinter getPrinter(Integer id) {
		if (id.intValue() == 1) {
			return new EditXonXoff();
		} else {
			return null;
		}
	}

}

