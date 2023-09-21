
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * The <code>QueueInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.QueueInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class QueueInfoFormat extends DestinationInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+DestinationInfoFormat.CSVHeader(false)+",ReceiverCount,DeliveredMessageCount,InTransitMessageCount,RouteName";
	}
	
	public static String toCSV(QueueInfo p_entry, String p_timestamp) {
		if (null==p_entry)
			return (p_timestamp!=null? p_timestamp:"")+"";
		
		return (p_timestamp!=null? p_timestamp:"")+DestinationInfoFormat.toCSV(p_entry, null)+
			","+p_entry.getReceiverCount()+
			","+p_entry.getDeliveredMessageCount()+
			","+p_entry.getInTransitMessageCount()+
			","+p_entry.getRouteName();
	}
	
	/**
	 * Prints out a list of ACLEntry objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_entries the list of ACLEntries to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 */
	public static void printCSV(QueueInfo p_entries[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(CSVHeader(p_timestamp));
		
		if (null!=p_entries) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_entries.length;i++) {
				System.out.println(toCSV(p_entries[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

