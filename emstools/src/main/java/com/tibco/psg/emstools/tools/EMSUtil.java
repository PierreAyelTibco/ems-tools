
package com.tibco.psg.emstools.tools;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

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
	
    public static void setClientID(final BaseObject p_trace, final Connection p_connection, final String p_client_id) throws JMSException {
		if (StringUtil.isValid(p_client_id))
			try {
				p_connection.setClientID(p_client_id);
			}
			catch (final JMSException ex) {
				p_trace.logWarning(ex, "Failed to set connection client ID");
			}
    }
    
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
				p_trace.logDebug(StringUtil.concat("  JMS Version: ", i_meta.getJMSVersion()));
				p_trace.logDebug("  JMS Major Version: " + i_meta.getJMSMajorVersion());
				p_trace.logDebug("  JMS Minor Version: " + i_meta.getJMSMinorVersion());
				p_trace.logDebug(StringUtil.concat("  Provider: ", i_meta.getJMSProviderName()));
				p_trace.logDebug(StringUtil.concat("  Provider Version: ", i_meta.getProviderVersion()));
				p_trace.logDebug("  Provider Major Version: " + i_meta.getProviderMajorVersion());
				p_trace.logDebug("  Provider Minor Version: " + i_meta.getProviderMinorVersion());
				p_trace.logDebug("  Provider JMSX Property Names: ");
				final Enumeration<?> e = i_meta.getJMSXPropertyNames();
				if (null!=e)
					while (e.hasMoreElements())
						p_trace.logDebug("    " + e.nextElement());
			}
			
			p_trace.logDebug(StringUtil.concat("connection client ID: ", p_connection.getClientID()));
		}
    }
    
    public static void close(final BaseObject p_trace, final QueueConnection p_connection) {
    	if (null!=p_connection)
			try {
				p_trace.logInfo("Closing queue connection...");
				p_connection.close();
			} 
    		catch (final JMSException ex) {
    			p_trace.logError("Failed to close queue connection...");
				p_trace.logError(ex);
			}
    }

    public static void close(final BaseObject p_trace, final TopicConnection p_connection) {
    	if (null!=p_connection)
			try {
				p_trace.logInfo("Closing topic connection...");
				p_connection.close();
			} 
    		catch (final JMSException ex) {
    			p_trace.logError("Failed to close topic connection...");
				p_trace.logError(ex);
			}
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

