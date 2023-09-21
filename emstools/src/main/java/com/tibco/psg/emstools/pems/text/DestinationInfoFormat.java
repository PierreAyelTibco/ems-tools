
package com.tibco.psg.emstools.pems.text;

import java.io.PrintStream;

import com.tibco.tibjms.admin.*;

/**
 * The <code>DestinationInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.DestinationInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class DestinationInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	protected DestinationInfoFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Name,ConsumerCount,PendingMsgCount,PendingMsgSize,PersistentPendingMsgCount,PersistentPendingMsgSize,Store,MaxMsgs,MaxBytes"+
				",InMsgRate,InByteRate,InTotalMsgs,InTotalBaytes"+
				",OutMsgRate,OutByteRate,OutTotalMsgs,OutTotalBaytes";
	}
	
	public static String toCSV(final DestinationInfo p_entry, final String p_timestamp) {
		if (null==p_entry)
			return (p_timestamp!=null? p_timestamp:"")+"";
		
		final StatData in = p_entry.getInboundStatistics();
		final StatData out = p_entry.getOutboundStatistics();
		
		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_entry.getName()) +
			","+p_entry.getConsumerCount()+
			","+p_entry.getPendingMessageCount()+
			","+p_entry.getPendingMessageSize()+
			","+p_entry.getPendingPersistentMessageCount()+
			","+p_entry.getPendingPersistentMessageSize()+
			","+p_entry.getStore()+
			","+p_entry.getMaxMsgs()+
			","+p_entry.getMaxBytes()+
			","+in.getMessageRate()+
			","+in.getByteRate()+
			","+in.getTotalMessages()+
			","+in.getTotalBytes()+
			","+out.getMessageRate()+
			","+out.getByteRate()+
			","+out.getTotalMessages()+
			","+out.getTotalBytes();
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
	public static void printCSV(final PrintStream p_out, final DestinationInfo[] p_entries, boolean p_header, boolean p_timestamp) {
		if (p_header)
			p_out.println(ACLEntryFormat.CSVHeader(p_timestamp));
		
		if (null!=p_entries) {
			final String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_entries.length;i++) {
				p_out.println(toCSV(p_entries[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

