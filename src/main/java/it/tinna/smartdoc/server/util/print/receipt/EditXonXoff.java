package it.tinna.smartdoc.server.util.print.receipt;

import org.apache.commons.lang.StringUtils;

import it.tinna.smartdoc.server.util.print.receipt.factory.IPrinter;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ScontrinoDto;

public class EditXonXoff extends IPrinter {
	
	private final String CHIUSURA_FISCALE = "1F";
	private final String CHIUSURA_VENDITA = "1T";
	private final String CLEAR = "K";
	private final String DESCRIZIONE = "\"";
	private final String PREZZO = "H";
	private final String QUANTITA = "*";
	private final String SCONTO_PERCENTUALE_ARTICOLO = "*1M";
	private final String SCONTO_PERCENTUALE_SUBTOTALE_APPEND = "*2M";
	private final String SCONTO_PERCENTUALE_SUBTOTALE_PREPEND = "=";
	private final String SCONTO_VALORE_ARTICOLO = "H3M";
	private final String SCONTO_VALORE_SUBTOTALE_APPEND = "H4M";
	private final String SCONTO_VALORE_SUBTOTALE_PREPEND = "=";
	private final String VENDITA_REPARTO = "1R";
	
	public String getStringaChiusuraFiscale() {
		StringBuilder sb = new StringBuilder();
		sb.append(CHIUSURA_FISCALE);
		sb.append(CLEAR);
		return sb.toString();
	}
	
	public String getStringaScontrino(ScontrinoDto dto) {
		StringBuilder sb = new StringBuilder();
		if (dto.getProdotti() != null && !dto.getProdotti().isEmpty()) {
			for (ProdottoDocumentoDto pdDto : dto.getProdotti()) {
				sb.append(DESCRIZIONE).append(pdDto.getDescProdotto()).append(DESCRIZIONE);
				sb.append(pdDto.getQuantita()).append(QUANTITA);
				sb.append((int)(pdDto.getPrezzoVenditaIvato() * 100)).append(PREZZO);
				sb.append(VENDITA_REPARTO);
				if (!StringUtils.isEmpty(pdDto.getSconto())) {
					if (pdDto.getSconto().endsWith("%")) {
						sb.append(pdDto.getSconto().substring(0, pdDto.getSconto().length() - 1)).append(SCONTO_PERCENTUALE_ARTICOLO);
					} else {
						sb.append((int)(Double.parseDouble(pdDto.getSconto().replaceAll(",", ".")) * 100)).append(SCONTO_VALORE_ARTICOLO);
					}
				}
			}
		}
		if (!StringUtils.isEmpty(dto.getSconto())) {
			if (dto.getSconto().endsWith("%")) {
				sb.append(SCONTO_PERCENTUALE_SUBTOTALE_PREPEND).append(dto.getSconto().substring(0, dto.getSconto().length() - 1)).append(SCONTO_PERCENTUALE_SUBTOTALE_APPEND);
			} else {
				sb.append(SCONTO_VALORE_SUBTOTALE_PREPEND).append(dto.getSconto()).append(SCONTO_VALORE_SUBTOTALE_APPEND);
			}
		}
		sb.append(CHIUSURA_VENDITA);
		sb.append(CLEAR);
		return sb.toString();
	}
	
}

