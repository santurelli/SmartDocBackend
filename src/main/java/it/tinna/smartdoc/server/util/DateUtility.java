package it.tinna.smartdoc.server.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtility {
	
	private static Log _log = LogFactory.getLog(DateUtility.class);
	
	protected static final long MILLISECS_PER_DAY = 24*60*60*1000;
	
	private static String[] parsePatterns;
	
	static {
		parsePatterns = new String[]{"dd/MM/yyyy", "yyyy-MM-dd"};
	}
	
	public static String add(Date date, int quantity) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, quantity);
		return format(cal.getTime());
	}
	
	public static String format(Date date) {
		String result = DateFormatUtils.format(date, "dd/MM/yyyy");
		return result;
	}
	
	public static String format(Date date, String format) {
		String result = DateFormatUtils.format(date, format);
		return result;
	}

	public static String getCurrentDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormatter.format(new Date());
	}
	
	public static String getCurrentDateAndTime() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy H:m:s");
		return dateFormatter.format(new Date());
	}
	
	public static String getCurrentYear() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy");
		return dateFormatter.format(new Date());
	}
	
	public static String getCurrentMonth() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM");
		return dateFormatter.format(new Date());
	}
	
	public static String getDay(String strDate) throws ParseException {
		Date date =  parse(strDate);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd");
		return dateFormatter.format(date);
	}
	
	public static long getDifferenceInDays(Date date1, Date date2) {
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(DateUtils.truncate(date2, Calendar.DAY_OF_MONTH));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(DateUtils.truncate(date1, Calendar.DAY_OF_MONTH));
		long mill2   =  calendar2.getTimeInMillis() +  calendar2.getTimeZone().getOffset(  calendar2.getTimeInMillis() );
        long mill1 = calendar1.getTimeInMillis() + calendar1.getTimeZone().getOffset( calendar1.getTimeInMillis() );
        return (mill2 - mill1) / MILLISECS_PER_DAY;
	}
	
	public static String getEndDayOfMonth(String month,String year) {
		String firstDay = "01/" + month + "/" + year;
		try {
			Date dateFirstDay = DateUtils.parseDate(firstDay, new String[]{"dd/MM/yyyy"});
			Date endDay = DateUtils.addMonths(dateFirstDay, 1);
			endDay = DateUtils.addDays(endDay,-1);
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormatter.format(endDay);
		} catch (ParseException e) {
			_log.error("Errore durante il parsing della data " + firstDay);
			return "";
		}
	}
	
	public static String getFirstDayOfMonth(String month,String year) {
		return "01/" + month + "/" + year;
	}
	
	public static String getHour(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH");
		return dateFormatter.format(date);
	}
	
	public static String getMinute(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("mm");
		return dateFormatter.format(date);
	}
	
	public static String getMonth(String strDate) throws ParseException {
		Date date =  parse(strDate);
		return getMonth(date);
	}
	
	public static String getMonth(Date date) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM");
		return dateFormatter.format(date);
	}
	
	public static String getMonthText(String strDate) throws ParseException {
		Date date =  parse(strDate);
		return getMonthText(date);
	}
	
	public static String getMonthText(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMMM");
		return StringUtils.capitalize(dateFormatter.format(date).toLowerCase());
	}
	
	public static String getYear(String strDate) throws ParseException {
		Date date =  parse(strDate);
		return getYear(date);
	}
	
	public static String getYear(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy");
		return dateFormatter.format(date);
	}
	
	public static Date parse(String strDate) throws ParseException {
		return DateUtils.parseDate(strDate, parsePatterns);
	}
	
	public static Date parse(String strDate,String[] patterns) throws ParseException {
		return DateUtils.parseDate(strDate, patterns);
	}
	
	public static Date parseWithTime(String strDate) throws ParseException {
		return DateUtils.parseDate(strDate, new String[]{"dd/MM/yyyy HH:mm"});
	}
	
	public static Timestamp toTimestamp(String strDate) throws ParseException {
		Date date = DateUtility.parse(strDate);
		return new Timestamp(date.getTime());
	}
	
	public static Timestamp toTimestampWithTime(String strDate) throws ParseException {
		Date date = DateUtility.parse(strDate, new String[]{"dd/MM/yyyy HH:mm"});
		return new Timestamp(date.getTime());
	}
	
	public static String getLastDayOfThisMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		Date lastDayOfMonth = cal.getTime();
		return format(lastDayOfMonth);
	}
	
}

