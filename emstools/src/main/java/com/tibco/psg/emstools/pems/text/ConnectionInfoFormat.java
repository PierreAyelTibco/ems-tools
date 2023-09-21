
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * The <code>ConnectionInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.ConnectionInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class ConnectionInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Connection ID,Username,Client Host,Client IP Address,Client URL"+
		",Client ID,Client Type,Connection Type,EMS Client Version,Consumer Count,Producer Count,Session Count" + 
		",SSLType,Start Time,Connection UpTime (ms),Uncommitted Count,Uncommitted Size";
	}
	
	public static String toCSV(ConnectionInfo p_connection, String p_timestamp) {
		if (null==p_connection)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,,,,,,,,,,,";

		String i_clientType = p_connection.getClientType();
		if (null!=i_clientType) {
			if (i_clientType.equals(ConnectionInfo.CLIENT_TYPE_C))
				i_clientType = "C";
			else if (i_clientType.equals(ConnectionInfo.CLIENT_TYPE_CSHARP))
				i_clientType = "C#";
			else if (i_clientType.equals(ConnectionInfo.CLIENT_TYPE_JAVA))
				i_clientType = "JAVA";
			else
				i_clientType = "UNKNOWN";
		}
		
		return (p_timestamp!=null? p_timestamp:"")+p_connection.getID()+
			","+StringFormat.toCSV(p_connection.getUserName())+
			","+p_connection.getHost()+ 
			","+StringFormat.toCSV(p_connection.getAddress())+ 
			","+StringFormat.toCSV(p_connection.getURL())+
			","+StringFormat.toCSV(p_connection.getClientID())+ 
			","+StringFormat.toCSV(i_clientType)+ 
			","+p_connection.getType()+
			","+VersionInfoFormat.toCSV(p_connection.getVersionInfo())+
			","+p_connection.getConsumerCount()+ 
		    ","+p_connection.getProducerCount()+ 
    		","+p_connection.getSessionCount() +
    		","+p_connection.getSSLType()+ 
    		","+TimeFormat.toCSV(p_connection.getStartTime())+ 
    		","+p_connection.getUpTime()+
			","+p_connection.getUncommittedCount() + //NEW
			","+p_connection.getUncommittedSize(); //NEW
	}
	
	/**
	 * Prints out a list of connection info objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_connections the list of connections to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 */
	public static void printCSV(ConnectionInfo p_connections[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(ConnectionInfoFormat.CSVHeader(p_timestamp));
		
		if (null!=p_connections) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_connections.length;i++) {
				System.out.println(ConnectionInfoFormat.toCSV(p_connections[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

