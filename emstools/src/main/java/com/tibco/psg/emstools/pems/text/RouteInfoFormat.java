
package com.tibco.psg.emstools.pems.text;

import java.io.PrintStream;

import com.tibco.tibjms.admin.*;

/**
 * The <code>RouteInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.RouteInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class RouteInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private RouteInfoFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Connection ID,Name,Params,URL,Zone,Zone Type,isConfigured,isConnected,isStalled"; 
	}
	
	public static String toCSV(final RouteInfo p_route, final String p_timestamp) {
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
	public static void printCSV(final PrintStream p_out, final RouteInfo[] p_routes, final boolean p_header, final boolean p_timestamp) {
		if (p_header)
			p_out.println(CSVHeader(p_timestamp));
		
		if (null!=p_routes) {
			final String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_routes.length;i++) {
				p_out.println(toCSV(p_routes[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

