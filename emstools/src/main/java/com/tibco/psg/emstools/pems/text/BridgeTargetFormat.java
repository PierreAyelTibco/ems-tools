
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class BridgeTargetFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private BridgeTargetFormat() {
	}
	
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"To,Type,Selector";
	}
	
	public static String toCSV(final BridgeTarget p_target) {
		if (null==p_target)
			return ",,";
		
		return StringFormat.toCSV(p_target.getName())+ 
			","+QueueTopicTypeFormat.toCSV(p_target.getType())+
			","+StringFormat.toCSV(p_target.getSelector());
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

