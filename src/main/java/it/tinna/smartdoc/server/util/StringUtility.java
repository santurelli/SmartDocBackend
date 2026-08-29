package it.tinna.smartdoc.server.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StringUtility {
	
	public static String formatForLike(String toSearch) {
		return new StringBuilder("%").append(toSearch).append("%").toString();
	}

	public static String formatForLikeHelper(String toSearch) {
		return formatForLike(toSearch);
	}
	
	public static List<String> readLines(String s) {
		String[] arr = StringUtils.splitByWholeSeparatorPreserveAllTokens(s, "\n");
		return Arrays.asList(arr);
	}

	/**
	 * Ripulisce il campo "stato" (formato interno "descrizione---id---codice", elementi multipli separati da virgola)
	 * per la sola visualizzazione, allo stesso modo di formatStato() lato frontend (documentUtils.js).
	 */
	public static String formatStato(String statusStr) {
		if (statusStr == null || statusStr.trim().isEmpty()) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for (String item : statusStr.split(",")) {
			String desc = item.split("---")[0].replace("&&&", "").trim();
			if (!desc.isEmpty()) {
				if (result.length() > 0) {
					result.append("\n");
				}
				result.append(desc);
			}
		}
		return result.toString();
	}

}

