
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.StatData;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class StatDataFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private StatDataFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static final String CSV_HEADER = "Byte Rate,Msg Rate,Total Bytes,Total Msg";
	
	public static String toCSV(final StatData p_data) {
		if (null==p_data)
			return ",,,";
		
		return ""+p_data.getByteRate()+","+p_data.getMessageRate()+","+p_data.getTotalBytes()+","+p_data.getTotalMessages(); 
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

