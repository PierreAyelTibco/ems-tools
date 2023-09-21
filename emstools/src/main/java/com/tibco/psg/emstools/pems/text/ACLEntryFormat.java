
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * The <code>ACLEntryFormat</code> class formats one or several {@link com.tibco.tibjms.admin.ACLEntry}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class ACLEntryFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Destination,Permissions,"+PrincipalInfoFormat.CSVHeader();
	}
	
	public static String toCSV(ACLEntry p_entry, String p_timestamp) {
		if (null==p_entry)
			return (p_timestamp!=null? p_timestamp:"")+",,"+PrincipalInfoFormat.toCSV(null, null);
		
		if (p_entry.getDestination()==null)
			return (p_timestamp!=null? p_timestamp:"")+
			","+PermissionsFormat.toCSV(p_entry.getPermissions())+
			","+PrincipalInfoFormat.toCSV(p_entry.getPrincipal(), null);
		
		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_entry.getDestination().getName())+
			","+PermissionsFormat.toCSV(p_entry.getPermissions())+
			","+PrincipalInfoFormat.toCSV(p_entry.getPrincipal(), null);
	}
	
	/**
	 * Prints out a list of ACLEntry objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_entries the list of ACLEntries to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 * @since 0.4
	 */
	public static void printCSV(ACLEntry p_entries[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(ACLEntryFormat.CSVHeader(p_timestamp));
		
		if (null!=p_entries) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_entries.length;i++) {
				System.out.println(ACLEntryFormat.toCSV(p_entries[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

