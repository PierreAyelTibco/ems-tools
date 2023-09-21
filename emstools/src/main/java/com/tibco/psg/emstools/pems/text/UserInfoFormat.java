
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.UserInfo;

/**
 * The <code>UserInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.UserInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class UserInfoFormat extends PrincipalInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+CSVHeader("User");
	}
	
	/**
	 * Prints out a list of user info objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_users the list of users to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 * @since 0.4
	 */
	public static void printCSV(UserInfo p_users[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(CSVHeader(p_timestamp));
		
		if (null!=p_users) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_users.length;i++) {
				System.out.println(toCSV(p_users[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

