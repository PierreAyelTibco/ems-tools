
package com.tibco.psg.emstools.pems.text;

import java.io.PrintStream;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class ConnectionFactoryInfoFormat {

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	/**
	 * Private constructor.
	 * <p>
	 * @since 1.4.0
	 */
	private ConnectionFactoryInfoFormat() {
	}
		
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(final boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Client ID,Connect Attempt Count,Connect Attempt Delay,Connect Attempt Timeout,"+
		"Type,JNDI Names,Multicast Daemon,Multicast,Load Balancing Metric,Params,"+
		"Reconnect Attempt Count,Reconnect Attempt Delay,Reconnect Attempt Timeout,URL,XAType,isLoadBalanced";
	}
	
	public static String toCSV(final ConnectionFactoryInfo p_factory, final String p_timestamp) {
		if (null==p_factory)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,,,,,,";

		return (p_timestamp!=null? p_timestamp:"")+StringFormat.toCSV(p_factory.getClientID())+ 
        	","+p_factory.getConnectAttemptCount()+ 
	        ","+p_factory.getConnectAttemptDelay() +
	        ","+p_factory.getConnectAttemptTimeout() + //NEW
	        ","+QueueTopicTypeFormat.toCSV(p_factory.getDestinationType()) +
	        ","+StringFormat.toCSV(p_factory.getJNDINames())+ 
	        ","+p_factory.getMetric() +
	        ","+p_factory.getMulticastDaemon() + //NEW
	        ","+p_factory.getMulticastEnabled() + //NEW
	        ","+MapFormat.toCSV(p_factory.getParams())+ 
	        ","+p_factory.getReconnectAttemptCount()+ 
	        ","+p_factory.getReconnectAttemptDelay() +
	        ","+p_factory.getReconnectAttemptTimeout() + //NEW
	        ","+StringFormat.toCSV(p_factory.getURL()) +
	        ","+p_factory.getXAType()+ 
	        ","+p_factory.isLoadBalanced(); 
	}
	
	/** @since 1.3.7 */
	public static void printCSV(final PrintStream p_out, final ConnectionFactoryInfo[] p_factories, final boolean p_header, final boolean p_timestamp) {
		if (p_header)
			p_out.println(ConnectionFactoryInfoFormat.CSVHeader(p_timestamp));
		
		if (null!=p_factories) {
			final String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_factories.length;i++) {
				p_out.println(ConnectionFactoryInfoFormat.toCSV(p_factories[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

