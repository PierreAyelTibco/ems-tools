
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class StoreInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		 return (p_timestamp? "Timestamp,":"")+"Name,File Size,Free Space,Used Space,Msg Bytes,Msg Count,Swapped Bytes,Swapped Count,Avg Write Time,Write Usage"; 
	}
	
	@SuppressWarnings("deprecation")
	public static String toCSV(String p_name, StoreInfo p_store, String p_timestamp) {
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

