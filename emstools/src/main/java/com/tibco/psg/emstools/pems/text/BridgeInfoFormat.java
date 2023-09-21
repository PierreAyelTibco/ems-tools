
package com.tibco.psg.emstools.pems.text;

import java.io.PrintStream;

import com.tibco.tibjms.admin.*;

/**
 * The <code>BridgeInfoFormat</code> class formats one or several {@link com.tibco.tibjms.admin.BridgeInfo}
 * objects into CSV (comma-separated-values) lines.
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class BridgeInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private BridgeInfoFormat() {
	}
	
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"From,Type";
	}
	
	public static String toCSV(final BridgeInfo p_bridge, final String p_timestamp) {
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
	public static void printCSV(final PrintStream p_out, final BridgeInfo[] p_bridges, final boolean p_header, final boolean p_timestamp) {
		if (p_header)
			p_out.println(CSVHeader(p_timestamp));
		
		if (null!=p_bridges) {
			final String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0 ; i<p_bridges.length ; i++) {
				final BridgeTarget[] i_targets = p_bridges[i].getTargets();
				for(int j=0 ; j<i_targets.length ; j++) {
					p_out.print(BridgeInfoFormat.toCSV(p_bridges[i], i_timestamp));
					p_out.print(",");
					p_out.println(BridgeTargetFormat.toCSV(i_targets[j]));
				}
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

