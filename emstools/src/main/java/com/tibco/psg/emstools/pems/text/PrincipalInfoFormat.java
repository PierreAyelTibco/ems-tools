
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class PrincipalInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader() {
		return "Name,Description,isExternal";
	}

	public static String CSVHeader(String p_header) {
		return p_header+" Name,"+p_header+" Description,"+p_header+" is External";
	}
	
	public static String toCSV(PrincipalInfo p_principal, String p_timestamp) {
		if (null==p_principal)
			return (p_timestamp!=null? p_timestamp:"")+",,";
		
		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_principal.getName())+ 
			","+StringFormat.toCSV(p_principal.getDescription())+
			","+p_principal.isExternal();
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

