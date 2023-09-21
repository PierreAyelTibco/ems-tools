
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class BridgeTargetFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"To,Type,Selector";
	}
	
	public static String toCSV(BridgeTarget p_target) {
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

