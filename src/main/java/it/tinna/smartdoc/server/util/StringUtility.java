package it.tinna.smartdoc.server.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class StringUtility {

    public static String formatForLike(String toSearch) {
        return new StringBuilder("%").append(toSearch).append("%").toString();
    }
    
    public static String formatForLikeHelper(String toSearch) {
        if (StringUtils.isEmpty(toSearch)) {
            return "%%";
        }
        return new StringBuilder("%").append(toSearch.trim().toLowerCase()).append("%").toString();
    }

    public static List<String> readLines(String s) {
        String[] arr = StringUtils.splitByWholeSeparatorPreserveAllTokens(s, "\n");
        return Arrays.asList(arr);
    }

}
