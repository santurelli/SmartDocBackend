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

}

