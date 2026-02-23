package it.tinna.smartdoc.server.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlFieldFormatterBase
{

    private static DecimalFormat df;
    static
    {
        df = new DecimalFormat("0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);
    }

    /*
     * String
     */
    public static String formatString(String value)
    {
        if ( value == null )
            return "";

        return value;
    }

    public static String parseString(String value)
    {
        if ( value == null )
            return "";

        return value;
    }

    /*
     * int
     */
    public static String formatInt(int value)
    {
        return "" + value;
    }

    public static int parseInt(String value)
    {
        if ( value == null )
            return 0;

        try
        {
            return (int) Integer.parseInt(value);
        }
        catch ( NumberFormatException e )
        {
            return 0;
        }

    }

    /*
     * Base Date
     */
    private static String formatDate(Date value,
                                     String pattern)
    {
        String retString = "";
        if ( null != value )
        {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            retString = df.format(value);
        }
        return retString;
    }

    private static Date parseDate(String value,
                                  String pattern)
    {
        Date retData = null;
        if ( null != value )
        {
            try
            {
                SimpleDateFormat df = new SimpleDateFormat(pattern);
                retData = df.parse(value);
            }
            catch ( ParseException e )
            {
            }
        }
        return retData;
    }

    /*
     * Date
     */
    public static String formatDateShort(Date value)
    {
        if ( value == null )
            return null;
        return formatDate(value, "yyyy-MM-dd");
    }

    public static Date parseDateShort(String value)
    {
        return parseDate(value, "yyyy-MM-dd");
    }

    /* DateTime */
    public static String formatDateTime(Date value)
    {
        if ( value == null )
            return null;
        return formatDate(value, "yyyy-MM-dd'T'HH:mm:ss");
    }

    public static Date parseDateTime(String value)
    {
        return parseDate(value, "yyyy-MM-dd'T'HH:mm:ss");
    }

    /*
     * double
     */
    public static String formatDouble(double value)
    {
        return df.format(value);
        // return "" + value;
    }

    public static double parseDouble(String value)
    {
        if ( value == null )
            return 0.0;

        try
        {
            return Double.parseDouble(value);
        }
        catch ( NumberFormatException e )
        {
            return 0.0;
        }

    }

    /*
     * boolean
     */
    public static String formatBoolean(boolean value)
    {
        return "" + value;
    }

    public static boolean parseBoolean(String value)
    {
        if ( value == null )
            return false;

        value = value.toLowerCase().trim();

        if ( "true".equals(value) )
            return true;

        return false;
    }

    /*
     * boolean Si/NO
     */
    public static String formatSiNo(boolean value)
    {
        return value ? "SI" : "NO";
    }

    public static Boolean parseSiNo(String value)
    {
        if ( value == null )
            return false;

        value = value.toUpperCase().trim();

        if ( "SI".equals(value) )
            return true;

        if ( "NO".equals(value) )
            return false;

        return null;
    }

    public static String formatXmlToDb(String value)
    {
        return value.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
    }

    public static String parseXmlFromDb(String value)
    {
        return value += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    }
}

