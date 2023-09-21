
package com.tibco.psg.emstools.pems.text;

import com.tibco.tibjms.admin.*;

/**
 * <p> 
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class ProducerInfoFormat {

	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/

	public static String CSVHeader(boolean p_timestamp) {
		return (p_timestamp? "Timestamp,":"")+"Connection ID,CreateTime,Destination,Type,ID,Session ID,"+StatDataFormat.CSVHeader()+",Username";
	}
	
	public static String toCSV(ProducerInfo p_producer, String p_timestamp) {
		if (null==p_producer)
			return (p_timestamp!=null? p_timestamp:"")+",,,,,,"+StatDataFormat.toCSV(null)+",";
		
		return (p_timestamp!=null? p_timestamp:"")+p_producer.getConnectionID()+ 
			","+TimeFormat.toCSV(p_producer.getCreateTime())+ 
			","+StringFormat.toCSV(p_producer.getDestinationName())+ 
			","+QueueTopicTypeFormat.toCSV(p_producer.getDestinationType())+ 
//DetailedDestStat[] getDetailedStatistics() 
			","+p_producer.getID()+ 
			","+p_producer.getSessionID()+ 
			","+StatDataFormat.toCSV(p_producer.getStatistics())+
			","+StringFormat.toCSV(p_producer.getUsername()); 
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

