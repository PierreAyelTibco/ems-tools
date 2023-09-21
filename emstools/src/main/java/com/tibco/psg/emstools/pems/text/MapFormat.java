
package com.tibco.psg.emstools.pems.text;

import java.util.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.0.0
 */
public class MapFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	@SuppressWarnings("rawtypes")
	public static String toCSV(Map p_map) {
		if (null!=p_map) {
			StringBuffer i_buffer = new StringBuffer("\"");
			int i=0;
			
			for(Iterator i_iterator=p_map.keySet().iterator() ; i_iterator.hasNext() ; i++) {
				if (i>0)
					i_buffer.append(",");
				
				Object i_name = i_iterator.next();
				i_buffer.append(i_name.toString());
				i_buffer.append("=");
				i_buffer.append(p_map.get(i_name));
			}
			i_buffer.append("\"");
			return i_buffer.toString();
		}
		return "";
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

