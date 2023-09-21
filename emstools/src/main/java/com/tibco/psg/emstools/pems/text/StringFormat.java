
package com.tibco.psg.emstools.pems.text;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class StringFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private StringFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String toCSV(final String p_str) {
		if (null!=p_str) {
			if (p_str.indexOf(',')>=0)
				return "\""+p_str+"\"";
			return p_str;
		}
		return "";
	}
	
	public static String toCSV(final String[] p_str) {
		if (null!=p_str && p_str.length>0) {
			final StringBuilder i_buffer = new StringBuilder("\"");
			
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

