
package com.tibco.psg.emstools.tools;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;

/**
 * <p>
 * @author Pierre Ayel
 * @since 1.4.0
 * @version 1.4.0
 */
public class EMSUtil {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 */
	private EMSUtil() {
		
	}

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/
	
    /**
     * @throws JMSException 
     * @since 1.2.0
     */
    public static void traceConnectionMetadata(final BaseObject p_trace, final Connection p_connection) throws JMSException {
    	if (p_trace.isDebugEnabled()) {
			final ConnectionMetaData i_meta = p_connection.getMetaData();
			if (null==i_meta)
				p_trace.logDebug("No connection metadata");
			else {
				p_trace.logDebug("Connection metadata:");
				p_trace.logDebug(concat("  JMS Version: ", i_meta.getJMSVersion()));
				p_trace.logDebug("  JMS Major Version: " + i_meta.getJMSMajorVersion());
				p_trace.logDebug("  JMS Minor Version: " + i_meta.getJMSMinorVersion());
				p_trace.logDebug(concat("  Provider: ", i_meta.getJMSProviderName()));
				p_trace.logDebug(concat("  Provider Version: ", i_meta.getProviderVersion()));
				p_trace.logDebug("  Provider Major Version: " + i_meta.getProviderMajorVersion());
				p_trace.logDebug("  Provider Minor Version: " + i_meta.getProviderMinorVersion());
				p_trace.logDebug("  Provider JMSX Property Names: ");
				final Enumeration<?> e = i_meta.getJMSXPropertyNames();
				if (null!=e)
					while (e.hasMoreElements())
						p_trace.logDebug("    " + e.nextElement());
			}
			
			p_trace.logDebug(concat("connection client ID: ", p_connection.getClientID()));
		}
    }
    
    private static String concat(final String a, final String b) {
    	return (null==b)? a.concat("null") : a.concat(b);
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

