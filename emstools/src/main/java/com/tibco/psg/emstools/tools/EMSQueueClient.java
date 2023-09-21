
package com.tibco.psg.emstools.tools;

import java.io.IOException;
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
	 * A class containing a connection, session and queue sender created by this object.
	 * <p>
	 * @author Pierre Ayel
	 * @since 1.3.3
	 */
	public class pWorkSession extends Object {
		
		private QueueConnection m_connection;
        private QueueSession m_session;
        private Queue m_queue;
        
        /**
    	 * Creates a new <code>pWorkSession</code> object.
    	 * <p>
    	 * Exits if the connection fails.
    	 * <p>
    	 * @throws JMSException In case of JMSException.
    	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
    	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
    	 */
    	public pWorkSession(EMSQueueClient p_client) throws JMSException, NamingException, IOException {
            super();
            
            //*** CONNECT
            try {
                m_connection = createQueueConnection();
            }
            //1.2.0
            catch (JMSException ex) {
            	logError("failed to connect...");
            	logError(ex);
            	System.exit(EXIT_CODE_CONNECTION_ERROR);
            }
            
            try {
                m_session = m_connection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
                
                //1.3.3
                m_queue = p_client.getQueue(m_session);
    	    }
    	    finally {
    	    }
    	}
    	
		public QueueConnection getQueueConnection() {
			return m_connection;
		}
		
        public QueueSession getQueueSession() {
        	return m_session;
        }
        
        public Queue getQueue() {
        	return m_queue;
        }
        
    	/**
    	 * Closes the queue sender.
    	 * <p>
    	 * @throws JMSException In case of JMSException.
    	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
    	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
    	 */
    	public void close() throws JMSException, NamingException, IOException {
            
        	//*** CLOSE JMS SESSION
        	if (null!=m_session)
    			try {
    				logDebug("Closing session...");
    				m_session.close();
    			} catch (JMSException e) {
    				logError(e);
    			}
        	
        	//*** CLOSE JMS CONNECTION
        	if (null!=m_connection)
    			try {
    				logDebug("Closing connection...");
    				m_connection.close();
    			} catch (JMSException e) {
    				logError(e);
    			}
    	}
   	};
 	
	/**
	 * A class containing a connection, session and queue sender created by this object.
	 * <p>
	 * @author Pierre Ayel
	 * @since 1.3.3
	 */
	public class pWorkSession_Sender extends pWorkSession {
		
	    private QueueSender m_sender;
        
        /** Total number of message received. */
        private long m_totalMsgs = 0;
         
        /**
    	 * Creates a new <code>pWorkSession</code> object.
    	 * <p>
    	 * Exits if the connection fails.
    	 * <p>
    	 * @throws JMSException In case of JMSException.
    	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
    	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
    	 */
    	public pWorkSession_Sender(EMSQueueClient p_client) throws JMSException, NamingException, IOException {
            super(p_client);
            
            try {
                m_sender = getQueueSession().createSender(getQueue());
                
                //1.2.0
                //1.3.3: logInfo("Sending on queue '"+m_queue.getQueueName()+"'");
    	    }
    	    finally {
    	    }
    	}
    	
        public QueueSender getQueueSender() {
        	return m_sender;
        }
        
        /**
         * Gets the number of message sent so far.
         */
        public long getTotalMsgs() {
        	return m_totalMsgs;
        }
        
    	/**
    	 * @since 1.2.0
    	 */
    	public void sendMessage(TextMessage message, int delMode) throws JMSException {
    		
    	    // Set default Properties
    	    message.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED", true);
    	    
    	    if (null==message.getJMSCorrelationID())
    	    	message.setJMSCorrelationID(UUID.randomUUID().toString());
    		
    		/*
    		 * Publish Request
    		 * Set appropriate deliveryMode, Priority, TTL
    		 */
            m_sender.send(message, delMode, message.getJMSPriority(), message.getJMSExpiration());

            //1.3.0
            m_totalMsgs++;
            
           	logInfo(getTotalMsgs()+": Sent message: "+message.getJMSMessageID());

            if (m_out_file!=null)
    		    saveMessage(message, "$MsgNotify$");

    		try { Thread.sleep(m_delay_ms); }
    		catch (InterruptedException e) { }		
    	}
 	}; 	
 	
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
            //1.2.0 System.err.println("Error: must specify Queue name");
            usage();
        }
        
        //1.3.3
        if (null!=getDestinationConfiguration().getQueueJNDIName() && null==getConnectionConfiguration().getJNDIURL()) {
        	logError("you cannot find a queue by its JNDI name if you do not connect with the JNDI interface");
        	usage();
        }
	}
	
	public Queue getQueue(QueueSession p_session) throws JMSException, NamingException {
		if (null!=getDestinationConfiguration().getQueueJNDIName())
        	return (Queue) getJNDIContext().lookup(getDestinationConfiguration().getQueueJNDIName());
        else
        	return p_session.createQueue(getDestinationConfiguration().getQueueName());
	}

	@Override
	public void usage() {
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

