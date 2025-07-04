
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class FileStoreInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private FileStoreInfoFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Name,FileName,FileSize,FileMinSize,FreeSpace,UsedSpace,InUsedSpace,NotInUseSpace"+
				 ",Fragmentation,MsgBytes,MsgCount,SwappedBytes,SwappedCount,WriteRate,WriteUsage,AvgWriteTime"+
				 ",DestDefrag,CRC,Sync,Trunc"; 
	}
	
	@SuppressWarnings("deprecation")
	public static String toCSV(final String p_name, final FileStoreInfo p_store, final String p_timestamp) {
		if (null==p_store)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,,,,";
		
		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_name)+
			","+StringFormat.toCSV(p_store.getFileName())+
			","+p_store.getSize()+
			","+p_store.getFileMinimum()+
			","+p_store.getFreeSpace()+
			","+p_store.getUsedSpace()+
			","+p_store.getInUseSpace()+
			","+p_store.getNotInUseSpace()+
			","+p_store.getFragmentation()+
			","+p_store.getMsgBytes()+
			","+p_store.getMsgCount()+
			","+p_store.getSwappedBytes()+
			","+p_store.getSwappedCount()+
			","+p_store.getWriteRate()+
			","+p_store.getWriteUsage()+
			","+p_store.getAverageWriteTime()+
			","+p_store.getDestinationDefrag()+
			","+p_store.isCRCEnabled()+
			","+p_store.isSynchronousWriteEnabled()+
			","+p_store.isTruncationEnabled();
			
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

