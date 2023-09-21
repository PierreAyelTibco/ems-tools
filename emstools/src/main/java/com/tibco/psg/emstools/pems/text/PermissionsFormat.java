
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class PermissionsFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private PermissionsFormat() {
	}
			
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static final String CSV_HEADER = "Permissions";
	
	private static final Object[] PERMISSIONS = new Object[] {
		Permissions.BROWSE, "BROWSE",
		Permissions.BROWSE_PERMISSION, "BROWSE_PERMISSION",
		Permissions.CREATE, "CREATE",
		Permissions.DELETE, "DELETE",
		Permissions.DURABLE, "DURABLE",
		Permissions.DURABLE_PERMISSION, "DURABLE_PERMISSION",
		Permissions.MODIFY, "MODIFY",
		Permissions.PUBLISH, "PUBLISH",
		Permissions.PUBLISH_PERMISSION, "PUBLISH_PERMISSION",
		Permissions.PURGE, "PURGE",
		Permissions.RECEIVE, "RECEIVE",
		Permissions.RECEIVE_PERMISSION, "RECEIVE_PERMISSION",
		Permissions.SEND, "SEND",
		Permissions.SEND_PERMISSION, "SEND_PERMISSION",
		Permissions.SUBSCRIBE, "SUBSCRIBE",
		Permissions.SUBSCRIBE_PERMISSION, "SUBSCRIBE_PERMISSION",
		Permissions.USE_DURABLE, "USE_DURABLE",
		Permissions.VIEW, "VIEW"
	};
	
	public static String toCSV(final Permissions p_perm) {
		if (null==p_perm)
			return "";
		
		final StringBuilder i_builder = new StringBuilder();
		
		for(int i=0 ; i<PERMISSIONS.length ; i+=2)
			if (p_perm.hasPermission((long) PERMISSIONS[i])) {
				
				if (i_builder.length()>0)
					i_builder.append(',');
				
				i_builder.append((String) PERMISSIONS[i+1]);
			}
		
        i_builder.insert(0, '\"');
        i_builder.append('\"');
		return i_builder.toString();
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

