
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * The <code>BridgeInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.BridgeInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class BridgeInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"From,Type";
	}
	
	public static String toCSV(BridgeInfo p_bridge, String p_timestamp) {
		if (null==p_bridge)
			return (p_timestamp!=null? p_timestamp:"")+",";
		
		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_bridge.getName())+","+QueueTopicTypeFormat.toCSV(p_bridge.getType());
	}
	
	/**
	 * Prints out a list of bridge info objects into a CSV document. 
	 * The first line contains the column header names if the input parameter 
	 * <code>p_header</code> equals <code>true</code>.
	 * <p> 
	 * @param p_bridges the list of bridges to print out. 
	 * If the list is <code>null</code> the document will display only the header line.
	 * @param p_header <code>true<code> if the header must be printed out, <code>false</code> otherwise.
	 * @since 0.4
	 */
	public static void printCSV(BridgeInfo p_bridges[], boolean p_header, boolean p_timestamp) {
		if (true==p_header)
			System.out.println(CSVHeader(p_timestamp));
		
		if (null!=p_bridges) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_bridges.length;i++) {
				BridgeTarget i_targets[] = p_bridges[i].getTargets();
				for(int j=0;j<i_targets.length;j++) {
					System.out.print(BridgeInfoFormat.toCSV(p_bridges[i], i_timestamp));
					System.out.print(",");
					System.out.println(BridgeTargetFormat.toCSV(i_targets[j]));
				}
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

