
package com.tibco.psg.emstools.pems.text;

import java.io.PrintStream;

import com.tibco.tibjms.admin.*;

/**
 * The <code>TopicInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.TopicInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class TopicInfoFormat extends DestinationInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private TopicInfoFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+DestinationInfoFormat.CSVHeader(false)+/*",SubscriptionCount*/",SubscriberCount,DurableSubscriptionCount,ActiveDurableCount,Channel";
	}
	
	public static String toCSV(final TopicInfo p_entry, final String p_timestamp) {
		if (null==p_entry)
			return (p_timestamp!=null? p_timestamp:"")+"";
		
		return (p_timestamp!=null? p_timestamp:"")+DestinationInfoFormat.toCSV(p_entry, null)+
			//","+p_entry.getSubscriptionCount()+
			","+p_entry.getSubscriberCount()+
			","+p_entry.getDurableCount()+
			","+p_entry.getActiveDurableCount()+
			","+p_entry.getChannel();
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
	public static void printCSV(final PrintStream p_out, final TopicInfo[] p_entries, final boolean p_header, final boolean p_timestamp) {
		if (p_header)
			p_out.println(CSVHeader(p_timestamp));
		
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

