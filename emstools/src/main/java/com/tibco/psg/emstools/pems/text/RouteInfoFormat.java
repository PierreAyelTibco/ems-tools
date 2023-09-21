
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * The <code>RouteInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.RouteInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class RouteInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Connection ID,Name,Params,URL,Zone,Zone Type,isConfigured,isConnected,isStalled"; 
	}
	
	public static String toCSV(RouteInfo p_route, String p_timestamp) {
		if (null==p_route)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,,,";
		
		String i_zone = "";
		
		if (p_route.getZoneType()==RouteInfo.ZONE_TYPE_MULTI_HOP)
			i_zone="MULTI_HOP";
		else if (p_route.getZoneType()==RouteInfo.ZONE_TYPE_ONE_HOP)
			i_zone="ONE_HOP";
		else 
			i_zone="UNKNOWN";

		return (p_timestamp!=null? p_timestamp:"")+p_route.getConnectionID()+ 
//DetailedDestStat[] getDetailedStatistics() 
//StatData getInboundStatistics() 
//RouteSelector[] getIncomingSelectors() 
			","+StringFormat.toCSV(p_route.getName())+ 
//StatData getOutboundStatistics() 
//RouteSelector[] getOutgoingSelectors()
			","+MapFormat.toCSV(p_route.getParams())+ 
			","+StringFormat.toCSV(p_route.getURL())+ 
        	","+StringFormat.toCSV(p_route.getZoneName())+ 
        	","+i_zone+ 
    		","+p_route.isConfigured()+ 
        	","+p_route.isConnected()+ 
        	","+p_route.isStalled(); 
	}
	
	/**
	 * Prints out a list of route info objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_routes the list of routes to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 * @since 0.4
	 */
	public static void printCSV(RouteInfo p_routes[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(CSVHeader(p_timestamp));
		
		if (null!=p_routes) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_routes.length;i++) {
				System.out.println(toCSV(p_routes[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

