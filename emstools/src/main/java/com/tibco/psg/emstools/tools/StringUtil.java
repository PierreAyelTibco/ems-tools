
package com.tibco.psg.emstools.tools;

/**
 * <p>
 * @author Pierre Ayel
 * @since 1.4.0
 * @version 1.4.0
 */
public class StringUtil {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 */
	private StringUtil() {
		
	}
	
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/
    
    /**
     * Tests if a string is not <code>null</code> and not empty after being trimmed.
     * <p>
     * @since 1.2.0
     */
    public static boolean isValid(final String p_str) {
    	return null!=p_str && p_str.trim().length()>0;
    }
    
    /**
     * Concatenate too strings. If the second one is <code>null</code> the string "null" is used instead.
     * @param a
     * @param b
     * @return
     */
    public static String concat(final String a, final String b) {
    	return (null==b)? a.concat("null") : a.concat(b);
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

