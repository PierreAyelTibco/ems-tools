
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class ConnectionFactoryInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Client ID,Connect Attempt Count,Connect Attempt Delay,Connect Attempt Timeout,"+
		"Type,JNDI Names,Multicast Daemon,Multicast,Load Balancing Metric,Params,"+
		"Reconnect Attempt Count,Reconnect Attempt Delay,Reconnect Attempt Timeout,URL,XAType,isLoadBalanced";
	}
	
	public static String toCSV(ConnectionFactoryInfo p_factory, String p_timestamp) {
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
	public static void printCSV(ConnectionFactoryInfo p_factories[], boolean p_header, boolean p_timestamp) {
		if (p_header)
			System.out.println(ConnectionFactoryInfoFormat.CSVHeader(p_timestamp));
		
		if (null!=p_factories) {
			String i_timestamp = p_timestamp? StringFormat.timestamp() : null;
			
			for(int i=0;i<p_factories.length;i++) {
				System.out.println(ConnectionFactoryInfoFormat.toCSV(p_factories[i], i_timestamp));
			}
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

