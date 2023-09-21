
package com.tibco.psg.emstools.pems.text;

import java.io.PrintStream;

import com.tibco.tibjms.admin.*;

/**
 * The <code>GroupInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.GroupInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class GroupInfoFormat extends PrincipalInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private GroupInfoFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
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
	public static void printCSV(final PrintStream p_out, final GroupInfo[] p_groups, final boolean p_header, final boolean p_timestamp) {
		if (p_header)
			p_out.println(CSVHeader(p_timestamp));
		
		if (null!=p_groups) {
			final String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0 ; i<p_groups.length ; i++) {
				final UserInfo[] i_users = p_groups[i].getUsers();
				for(int j=0 ; j<i_users.length ; j++) {
					p_out.print(GroupInfoFormat.toCSV(p_groups[i], i_timestamp));
					p_out.print(",");
					p_out.println(UserInfoFormat.toCSV(i_users[j], null));
				}
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

