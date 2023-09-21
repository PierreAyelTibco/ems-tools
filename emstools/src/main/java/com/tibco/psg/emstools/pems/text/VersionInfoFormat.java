
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.0.0
 */
public class VersionInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String toCSV(VersionInfo p_version) {
		if (null==p_version)
			return "";
		
		return p_version.toString();
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

