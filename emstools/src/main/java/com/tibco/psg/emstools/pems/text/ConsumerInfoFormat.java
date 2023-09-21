
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class ConsumerInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Connection ID,CreateTime,Destination,Type,Pending Msg Count,Pending Msg Size,Selector,Durable,ID,Session ID,"+StatDataFormat.CSVHeader()+",Username";
	}
	
	public static String toCSV(ConsumerInfo p_consumer, String p_timestamp) {
		if (null==p_consumer)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,,,,"+StatDataFormat.toCSV(null)+",";
		
		return (p_timestamp!=null? p_timestamp:"")+p_consumer.getConnectionID()+ 
			","+TimeFormat.toCSV(p_consumer.getCreateTime())+ 
			","+StringFormat.toCSV(p_consumer.getDestinationName())+ 
			","+QueueTopicTypeFormat.toCSV(p_consumer.getDestinationType())+ 
//DetailedDestStat[] getDetailedStatistics()

			","+p_consumer.getPendingMessageCount()+ //NEW
			","+p_consumer.getPendingMessageSize()+ //NEW
			","+StringFormat.toCSV(p_consumer.getSelector())+ //NEW
			
			","+StringFormat.toCSV(p_consumer.getDurableName())+ 
			","+p_consumer.getID()+ 
			","+p_consumer.getSessionID()+ 
			","+StatDataFormat.toCSV(p_consumer.getStatistics())+
			","+StringFormat.toCSV(p_consumer.getUsername()); 
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

