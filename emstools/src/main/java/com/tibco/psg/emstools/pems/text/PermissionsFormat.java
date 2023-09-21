
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.0.0
 */
public class PermissionsFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader() {
		return "Permissions";
	}
	
	public static String toCSV(Permissions p_perm) {
		if (null==p_perm)
			return "";
		
		StringBuffer i_buffer = new StringBuffer();
		
		 if (p_perm.hasPermission(Permissions.BROWSE))
			i_buffer.append(",BROWSE");
         if (p_perm.hasPermission(Permissions.BROWSE_PERMISSION))
        	 i_buffer.append(",BROWSE_PERMISSION");
         if (p_perm.hasPermission(Permissions.CREATE))
        	 i_buffer.append(",CREATE");
         if (p_perm.hasPermission(Permissions.DELETE))
        	i_buffer.append(",DELETE");
         if (p_perm.hasPermission(Permissions.DURABLE))
        	 i_buffer.append(",DURABLE");
         if (p_perm.hasPermission(Permissions.DURABLE_PERMISSION))
        	 i_buffer.append(",DURABLE_PERMISSION");
         if (p_perm.hasPermission(Permissions.MODIFY))
        	 i_buffer.append(",MODIFY");
         if (p_perm.hasPermission(Permissions.PUBLISH))
        	 i_buffer.append(",PUBLISH");
         if (p_perm.hasPermission(Permissions.PUBLISH_PERMISSION))
        	 i_buffer.append(",PUBLISH_PERMISSION");
         if (p_perm.hasPermission(Permissions.PURGE))
        	 i_buffer.append(",PURGE");
         if (p_perm.hasPermission(Permissions.RECEIVE))
        	 i_buffer.append(",RECEIVE");
         if (p_perm.hasPermission(Permissions.RECEIVE_PERMISSION))
        	i_buffer.append(",RECEIVE_PERMISSION");
         if (p_perm.hasPermission(Permissions.SEND))
        	 i_buffer.append(",SEND");
         if (p_perm.hasPermission(Permissions.SEND_PERMISSION))
        	 i_buffer.append(",SEND_PERMISSION");
         if (p_perm.hasPermission(Permissions.SUBSCRIBE))
        	 i_buffer.append(",SUBSCRIBE");
         if (p_perm.hasPermission(Permissions.SUBSCRIBE_PERMISSION))
        	 i_buffer.append(",SUBSCRIBE_PERMISSION");
         if (p_perm.hasPermission(Permissions.USE_DURABLE))
        	 i_buffer.append(",USE_DURABLE");
         if (p_perm.hasPermission(Permissions.VIEW))
        	 i_buffer.append(",VIEW");
         
         String i_perms = i_buffer.toString();
         if (i_perms.startsWith(",")) i_perms = i_perms.substring(1);

		return "\""+i_perms+"\"";
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

