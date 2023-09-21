
package com.tibco.psg.emstools.pems.text;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class StringFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String toCSV(String p_str) {
		if (null!=p_str) {
			if (p_str.indexOf(',')>=0)
				return "\""+p_str+"\"";
			return p_str;
		}
		return "";
	}
	
	public static String toCSV(String p_str[]) {
		if (null!=p_str && p_str.length>0) {
			StringBuffer i_buffer = new StringBuffer("\"");
			
			for(int i=0;i<p_str.length;i++) {
				if (i>0)
					i_buffer.append(",");
				i_buffer.append(p_str[i]);
			}
			i_buffer.append("\"");
			return i_buffer.toString();
		}
		return "";
	}
	
	/** @since 1.3.7 */
	public static String timestamp() {
		return TimeFormat.toCSV(System.currentTimeMillis())+",";
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

