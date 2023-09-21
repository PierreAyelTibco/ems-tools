
package com.tibco.psg.emstools.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.NamingException;

/**
 * <p>
 * @author Pierre Ayel
 * @since 1.3.3
 * @since 1.3.9
 */
public class EMSQueueClient extends EMSClient {
	
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = 2892883399092551421L;

	/*************************************************************************/
	/***  SUB-CLASSES  *******************************************************/
	/*************************************************************************/
	
 	/**
 	 * A serializable class to contain a QueueConnection (used inside BW5).
 	 * <p>
 	 * @since 1.3.9
 	 * @version 1.3.9
 	 */
 	public static class TransientQueueConnection extends Object implements Serializable {
 		
 		/*************************************************************************/
 		/***  DEFINITIONS  *******************************************************/
 		/*************************************************************************/
 		
 	    /** Unique ID for serialisation. */
		private static final long serialVersionUID = -6742415748695170533L;
		
		private final transient QueueConnection m_connection;
 		
 		public TransientQueueConnection(final QueueConnection p_connection) {
 			super();
 			
 			m_connection = p_connection;
 		}
 		
 		public QueueConnection getConnection() {
 			return m_connection;
 		}
 	}
 	
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSQueueClient</code> object.
	 */
	public EMSQueueClient() {
		super();
	}
	
	/*************************************************************************/
	/***  METHODS  ***********************************************************/
	/*************************************************************************/
	
	public void logQueue() {
		 if (null!=getDestinationConfiguration().getQueueJNDIName())
	        	log("Queue (JNDI Name)............ " + getDestinationConfiguration().getQueueJNDIName());
	        else
	        	log("Queue........................ " + getDestinationConfiguration().getQueueName());
	}
	
	public void checkArguments() {
		
        if (null==getDestinationConfiguration().getQueueName() && null==getDestinationConfiguration().getQueueJNDIName()) { // 1.3.3
        	logError("you must specify the queue name or its JNDI name");
        	exitOnInvalidUsage();
        }
        
        //1.3.3
        if (null!=getDestinationConfiguration().getQueueJNDIName() && null==getConnectionConfiguration().getJNDIURL()) {
        	logError("you cannot find a queue by its JNDI name if you do not connect with the JNDI interface");
        	exitOnInvalidUsage();
        }
	}
	
	public Queue getQueue(final QueueSession p_session) throws JMSException, NamingException {
		if (null!=getDestinationConfiguration().getQueueJNDIName())
        	return (Queue) getJNDIContext().lookup(getDestinationConfiguration().getQueueJNDIName());
        else
        	return p_session.createQueue(getDestinationConfiguration().getQueueName());
	}

	@Override
	public void usage(final PrintStream p_out) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * @throws NamingException 
	 * @throws JMSException 
	 * @since 1.3.9
	 */
	public TransientQueueConnection createTransientQueueConnection() throws JMSException, NamingException {
		return new TransientQueueConnection(createQueueConnection());
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

