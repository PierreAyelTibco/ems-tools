
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class StoreInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private StoreInfoFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		 return (p_timestamp? "Timestamp,":"")+"Name,File Size,Free Space,Used Space,Msg Bytes,Msg Count,Swapped Bytes,Swapped Count,Avg Write Time,Write Usage"; 
	}
	
	@SuppressWarnings("deprecation")
	public static String toCSV(final String p_name, final StoreInfo p_store, final String p_timestamp) {
		if (null==p_store)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,,,,";
		
		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_name)+
			","+p_store.getFileSize()+
			","+p_store.getFreeSpace()+
			","+p_store.getUsedSpace()+
			","+p_store.getMsgBytes()+
			","+p_store.getMsgCount()+
			","+p_store.getSwappedBytes()+
			","+p_store.getSwappedCount()+
			","+p_store.getAverageWriteTime()+
			","+p_store.getWriteUsage(); 
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

