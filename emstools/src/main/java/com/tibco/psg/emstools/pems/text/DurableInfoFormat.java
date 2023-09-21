
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * The <code>DurableInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.DurableInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class DurableInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Client ID,Consumer ID,Delivered Msg Count,Name,Pending Msg Count,Pending Msg Size,Selector,Topic,Username,"+
				"isActive,isConnected,isNoLocalEnabled,isStatic";
	}
	
	@SuppressWarnings("deprecation")
	public static String toCSV(DurableInfo p_durable, String p_timestamp) {
		if (null==p_durable)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,,,,,,,";
		
		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_durable.getClientID())+ 
			","+p_durable.getConsumerID()+ 
			","+p_durable.getDeliveredMessageCount()+ 
			","+StringFormat.toCSV(p_durable.getDurableName())+ 
			","+p_durable.getPendingMessageCount() +
			","+p_durable.getPendingMessageSize() +
			","+StringFormat.toCSV(p_durable.getSelector())+ 
			","+StringFormat.toCSV(p_durable.getTopicName())+ 
			","+StringFormat.toCSV(p_durable.getUserName()) +
			","+p_durable.isActive() +
			","+p_durable.isConnected() + //NEW
			","+p_durable.isNoLocalEnabled()+ 
			","+p_durable.isStatic(); 
	}

	/**
	 * Prints out a list of durable info objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_durables the list of durables to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 * @since 0.4
	 */
	public static void printCSV(DurableInfo p_durables[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(CSVHeader(p_timestamp));
		
		if (null!=p_durables) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_durables.length;i++) {
				System.out.println(toCSV(p_durables[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

