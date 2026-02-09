package it.tinna.smartdoc.server.util;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class IOUtility {
	
	public static String getValidFilename(String filename) {
		return StringUtils.replaceEach(filename, new String[]{" ", "/", ".", ">", "<", "?", "\t", "&", "\"", "°", ""}, new String[]{"_", "", "", "", "", "", "", "", "", "", ""});
	}
	
	public static String getWarPath() {
		String webInfPath = "";
		URL url = IOUtility.class.getResource("IOUtility.class"); 
		String path = url.getFile();
		webInfPath = path.substring(0,path.indexOf("WEB-INF"));
		
		return webInfPath;
	}
	
	public static String getWebInfPath() {
		String webInfPath = "";
		URL url = IOUtility.class.getResource("IOUtility.class"); 
		String path = url.getFile();
		webInfPath = path.substring(0,path.indexOf("WEB-INF")) + "WEB-INF" + SystemUtils.FILE_SEPARATOR;
		webInfPath = webInfPath.replaceAll("%20", " ");
		webInfPath = webInfPath.replaceAll("%5B", "[");
		webInfPath = webInfPath.replaceAll("%5D", "]");
		return webInfPath;
	}
	
	public static String getFileExtension(String fileName) throws IndexOutOfBoundsException {
		String ext = "";
		int index = fileName.lastIndexOf(".");
		ext = fileName.substring(index + 1);
		
		return ext;
	}
	
	public static String getFileName(String fileName) throws IndexOutOfBoundsException {
		String name = "";
		int index = fileName.lastIndexOf(".");
		name = fileName.substring(0,index);
		
		return name;
	}

}
