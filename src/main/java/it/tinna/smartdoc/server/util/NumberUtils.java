package it.tinna.smartdoc.server.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class NumberUtils
{

    public static String formatAsCurrency(double importo)
    {
        return formatAsCurrency((Double)importo);
    }

    public static String formatAsCurrency(Double importo)
    {
        if (importo == null) return "";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("it", "IT"));
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(2);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#,##0.00");
        return df.format(importo);
    }

    public static String formatAsQuantity(Double importo)
    {
        if (importo == null) return "";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("it", "IT"));
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(2);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#,##0.00");
        String result = df.format(importo);
        String[] split = result.split(",");
        if ( split.length == 2 && split[1].replaceAll("0", "").equals("") )
        {
            return split[0];
        }
        return result;
    }

    public static String formatAsPercentage(Double importo)
    {
        if (importo == null) return "";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("it", "IT"));
        nf.setMaximumFractionDigits(3);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("##0.00");
        String result = df.format(importo);
        String[] split = result.split(",");
        if ( split.length == 2 && split[1].replaceAll("0", "").equals("") )
        {
            return split[0];
        }
        return result;
    }

    public static BigDecimal getPrezzoScontato(double prezzoIntero,
                                               String sconto)
    {
        BigDecimal prezzoInteroBD = BigDecimal.valueOf(prezzoIntero);
        if ( sconto.indexOf("+") == -1 )
        {
            if ( sconto.endsWith("%") )
            {
                return prezzoInteroBD.subtract(prezzoInteroBD.multiply(new BigDecimal(sconto.replace("%", StringUtils.EMPTY).trim()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));
            }
            else
            {
                return prezzoInteroBD.subtract(new BigDecimal(sconto));
            }
        }
        else
        {
            String[] tokens = sconto.split("\\+");
            for ( int i = 0; i < tokens.length; i++ )
            {
                BigDecimal scontoBD = BigDecimal.ZERO;
                try
                {
                    scontoBD = new BigDecimal(tokens[i].replace("%", StringUtils.EMPTY).trim());
                }
                catch ( NumberFormatException e )
                {
                }
                prezzoInteroBD = prezzoInteroBD.subtract(prezzoInteroBD.multiply(scontoBD.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)));
            }
            return prezzoInteroBD;
        }
    }
}
