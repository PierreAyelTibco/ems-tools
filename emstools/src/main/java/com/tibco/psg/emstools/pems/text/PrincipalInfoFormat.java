
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class PrincipalInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	protected PrincipalInfoFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static final String CSV_HEADER = "Name,Description,isExternal";

	public static String CSVHeader(final String p_header) {
		return p_header+" Name,"+p_header+" Description,"+p_header+" is External";
	}
	
	public static String toCSV(final PrincipalInfo p_principal, final String p_timestamp) {
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

