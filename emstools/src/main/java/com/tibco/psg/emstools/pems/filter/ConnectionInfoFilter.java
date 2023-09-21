
package com.tibco.psg.emstools.pems.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.tibco.tibjms.admin.ConnectionInfo;

/**
 * The <code>ConnectionInfoFilter</code> filters a list of {@link com.tibco.tibjms.admin.ConnectionInfo}
 * objects by different criterias such as connection id, client id, username, etc...
 * <p>
 * @author Pierre Ayel
 * @version 1.0.0
 */
public class ConnectionInfoFilter extends Object implements Serializable {
	
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = 2856193115029361555L;

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 */
	private ConnectionInfoFilter() {
		super();
	}
	
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/
	
	/**
	 * Returns an empty list if nothing was found...
	 */
	public static ConnectionInfo[] filterByConnectionID(final ConnectionInfo[] p_info, final long p_connectionID) {
		if (null!=p_info)
			for(int i=0 ; i<p_info.length ; i++) {
				if (null!=p_info[i] && p_info[i].getID()==p_connectionID)
					return new ConnectionInfo[]{ p_info[i] };
			}
		return new ConnectionInfo[]{};
	}

	/**
	 * Returns an empty list if nothing was found...
	 */
	public static ConnectionInfo[] filterByUsername(final ConnectionInfo[] p_info, final String p_username) {
		if (null!=p_info) {
			final List<ConnectionInfo> i_result = new ArrayList<>();
			for(int i=0 ; i<p_info.length ; i++) {
				if (null!=p_info[i] && p_info[i].getUserName().equals(p_username))
					i_result.add(p_info[i]);
			}
			
			final ConnectionInfo[] i_list = new ConnectionInfo[i_result.size()];
			for(int i=0 ; i<i_result.size() ; i++)
				i_list[i] = (ConnectionInfo)i_result.get(i);
			return i_list;
		}
		return new ConnectionInfo[]{};
	}

	/**
	 * Returns an empty list if nothing was found...
	 */
	public static ConnectionInfo[] filterByHostname(final ConnectionInfo[] p_info, final String p_hostname) {
		if (null!=p_info) {
			final List<ConnectionInfo> i_result = new ArrayList<>();
			for(int i=0 ; i<p_info.length ; i++) {
				if (null!=p_info[i] && p_info[i].getHost().equalsIgnoreCase(p_hostname))
					i_result.add(p_info[i]);
			}
			
			final ConnectionInfo[] i_list = new ConnectionInfo[i_result.size()];
			for(int i=0 ; i<i_result.size() ; i++)
				i_list[i] = (ConnectionInfo)i_result.get(i);
			return i_list;
		}
		return new ConnectionInfo[]{};
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

