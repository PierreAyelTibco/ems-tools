
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * The <code>GroupInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.GroupInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class GroupInfoFormat extends PrincipalInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+CSVHeader("Group");
	}
	
	/**
	 * Prints out a list of group info objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_groups the list of connections to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 * @since 0.4
	 */
	public static void printCSV(GroupInfo p_groups[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(CSVHeader(p_timestamp));
		
		if (null!=p_groups) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_groups.length;i++) {
				UserInfo i_users[] = p_groups[i].getUsers();
				for(int j=0;j<i_users.length;j++) {
					System.out.print(GroupInfoFormat.toCSV(p_groups[i], i_timestamp));
					System.out.print(",");
					System.out.println(UserInfoFormat.toCSV(i_users[j], null));
				}
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

