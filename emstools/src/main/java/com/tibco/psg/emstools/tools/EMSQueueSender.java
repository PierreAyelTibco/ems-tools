
package com.tibco.psg.emstools.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * <p>
 * @author Richard Lawrence
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class EMSQueueSender extends EMSQueueClient {
    
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = -5190653890708009764L;

	/*************************************************************************/
	/***  SUB-CLASSES  *******************************************************/
	/*************************************************************************/
	
	/**
	 * A thread containing one <code>EMSQueueSender</code> object.
	 * When the thread runs, it invokes the {@link EMSQueueSender#start()} method.
	 * <p>
	 * @author Pierre Ayel
	 * @version 1.3.0
	 * @since 1.3.0
	 */
	public static class pThread extends Thread {
		
		private final EMSQueueSender m_sender;
		
		public pThread(final int n, final EMSQueueSender p_sender) {
			super();
			setName("QueueSender-Thread-"+((n<9)? "0":"")+n);
			setDaemon(false);
			m_sender = p_sender;
		}
		
		@Override
		public void run() {
			try {
				m_sender.start();
			} 
			catch (final Exception ex) {
				m_sender.logError("Failed to start msg publisher: ".concat(ex.getMessage()));
			}
		}
	}
	
	/**
	 * A class containing a connection, session and queue sender created by this object.
	 * <p>
	 * @author Pierre Ayel
	 * @since 1.3.3
	 */
	public class WorkSession extends Object {
		
		private QueueConnection m_connection;
        private QueueSession m_session;
        private Queue m_queue;
        
        /**
    	 * Creates a new <code>WorkSession</code> object.
    	 * <p>
    	 * Exits if the connection fails.
    	 */
    	public WorkSession() throws JMSException, NamingException, IOException {
            super();
            
            //*** CONNECT
            try {
                m_connection = createQueueConnection();
            }
            //1.2.0
            catch (final Exception ex) {
            	logError("failed to connect...");
            	logError(ex);
            	System.exit(EXIT_CODE_CONNECTION_ERROR);
            }
            
            m_session = m_connection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            
            //1.3.3
            m_queue = EMSQueueSender.this.getQueue(m_session);
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
    			} 
        		catch (final JMSException ex) {
    				logError(ex);
    			}
        	
        	//*** CLOSE JMS CONNECTION
        	if (null!=m_connection)
        		try {
    				logDebug("Closing connection...");
    				m_connection.close();
    			} 
        		catch (final JMSException ex) {
    				logError(ex);
    			}
    	}
   	}
 	
	/**
	 * A class containing a connection, session and queue sender created by this object.
	 * <p>
	 * @author Pierre Ayel
	 * @since 1.3.3
	 */
	public class WorkSession_Sender extends WorkSession {
		
	    private QueueSender m_sender;
        
        /** Total number of message received. */
        private long m_totalMsgs = 0;
         
        /**
    	 * Creates a new <code>WorkSession_Sender</code> object.
    	 * <p>
    	 * Exits if the connection fails.
    	 */
    	public WorkSession_Sender() throws JMSException, NamingException, IOException {
            super();
            
            m_sender = getQueueSession().createSender(this.getQueue());
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
    	 * @throws InterruptedException 
    	 * @since 1.2.0
    	 */
    	public void sendMessage(final TextMessage message, final int delMode) throws JMSException, InterruptedException {
    		
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

   			Thread.sleep(m_delay_ms);
    	}
 	}
 		
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSQueueSender</code> object.
	 * <p>
	 * @param p_args The command line arguments.
	 * @throws IOException If one command line parameter uses a file and the file cannot be read.
	 */
	public EMSQueueSender(final String[] p_args) throws IOException {
		super();
		
        parseArgs(p_args);

        //1.3.3
        checkArguments();

        /* print parameters */
        log(" ");
        log("------------------------------------------------------------------------");
        log(toString());
        log("------------------------------------------------------------------------");
        getConnectionConfiguration().logURL(this);
        logQueue();
        //1.4.0
        log("Delay between messages ...... " + m_delay_ms + " ms");
        log("------------------------------------------------------------------------\n");
	}
	
	/**
	 * Starts the queue sender, sends all messages speficied in the command line argument and then closes.
	 * <p>
	 * Exits if the connection fails.
	 * <p>
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
	 * @throws InterruptedException 
	 * @since 1.3.0
	 */
	public void start() throws JMSException, NamingException, IOException, InterruptedException {
        
        //*** CONNECT, CREATE SESSION AND QUEUE SENDER
		/* 1.3.2 */
		final WorkSession_Sender i_work_session = init();
        
        try{
            //1.3.0
			//*** WAIT FOR 1ST MSG ON TRIGGER TOPIC BEFORE STARTING PUBLISHING/SENDING LOOP
           	waitOnTriggerTopic();	

            //1.2.0
            logInfo("Sending on queue '"+i_work_session.getQueue().getQueueName()+"'");
            
            if (m_in_files.size()==1 && m_in_files.get(0).isDirectory())
            	while (i_work_session.getTotalMsgs()<m_count) { //1.3.0
            		
            		for(File i_file : m_in_files.get(0).listFiles()) {
            			
            			//1.3.0
            			if (i_work_session.getTotalMsgs()>=m_count) break;
            			
            			if (i_file.isDirectory()) continue;
            			if (i_file.isHidden()) continue;
            			if (!i_file.getName().toLowerCase().endsWith(".msg")) continue;
            		
    	            	//*** CREATE MESSAGE
    	                javax.jms.TextMessage message = i_work_session.getQueueSession().createTextMessage();

    	                //*** READ MESSAGE
   	               		loadMessage(message, i_file);
    	               	
    	               	//*** SEND MESSAGE
   	               	i_work_session.sendMessage(message, message.getJMSDeliveryMode());
            		}
            	}
            else
	            /* publish messages */
            	while (i_work_session.getTotalMsgs()<m_count) { //1.3.0

	            	/*
					 * Create request message
					 */
	                javax.jms.TextMessage message = i_work_session.getQueueSession().createTextMessage();
	                int delMode = DeliveryMode.NON_PERSISTENT;
	                //String CID = null;
	
	               	if (m_in_files.size()>0) {//1.1.0: infileName != null)	{
	               		loadMessage(message);
	               		delMode = message.getJMSDeliveryMode();
	               	}
	               	else {
					    // If no infile specified create default message
			
					    // Create Request message body
					    message.setText(m_data + i_work_session.getTotalMsgs());
					}
	
	               	//*** SEND MESSAGE
	               	i_work_session.sendMessage(message, delMode);
	            }
            
            //logInfo("Finished");
	    }
	    finally {
	    	//*** CLOSE JMS SESSION
        	//*** CLOSE JMS CONNECTION
        	i_work_session.close();
	    }
	}
	
	/**
	 * Starts the queue sender.
	 * <p>
	 * Exits if the connection fails.
	 * <p>
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
	 * @since 1.3.0
	 */
	public WorkSession_Sender init() throws JMSException, NamingException, IOException {
		return new WorkSession_Sender();
	}
	
    /**
     * Prints the command line usage on standard error.
     */
	@Override
    public void usage(final PrintStream p_out) {
        p_out.println("\nUsage: java "+getClass().getSimpleName()+" [options]");
        p_out.println("");
        p_out.println("   where options are:");
        p_out.println("");
        p_out.println("  -server     <serverURL> - EMS server URL, default is local server");
        p_out.println("  -jndi_url   <JNDI URL>  - JNDI server URL ");
        p_out.println("  -factory    <factory>   - JNDI factory name, default QueueConnectionFactory");
        p_out.println("  -user       <user name> - user name, default is null");
        p_out.println("  -password   <password>  - password, default is null");
        p_out.println("");
        p_out.println("  -queue      <name>      - The Queue full name");
        p_out.println("  -jndi_queue <name>      - The Queue JNDI name");
        p_out.println("  -infile     <file name> - The file/folder that contains the message(s) to send");
        //p_out.println("                             You can repeat this parameter multiple time to send multiple files/folders");
        p_out.println("  -log        <file name> - The output log/folder file name");
        p_out.println("  -count      <count>     - Number of messages to send, default 1");
        p_out.println("  -delay      <millisecs> - Delay between messages, default 1000");
    }

	/**************************************************************************/
	/***  MAIN METHOD  ********************************************************/
	/**************************************************************************/

    public static void main(final String[] args) {
    	try {
    		final EMSQueueSender i_tool = new EMSQueueSender(args);
        	
        	if (i_tool.getTestThreadCount()>1) {
        		final List<pThread> i_threads = new ArrayList<>(i_tool.getTestThreadCount());
        		for(int i=0;i<i_tool.getTestThreadCount();i++) {
        			i_tool.logInfo("Starting new thread ("+(1+i)+"/"+i_tool.getTestThreadCount()+")...");
        			final pThread i_thread = new pThread(i+1, new EMSQueueSender(args));
        			i_thread.start();
        			i_threads.add(i_thread);
        		}
        		for(pThread i_thread : i_threads)
        			i_thread.join();
        	}
        	else
        		i_tool.start();
        	
	        System.exit(EXIT_CODE_SUCCESS);
	    }
	    catch (final Throwable ex) {
	    	ex.printStackTrace();
	    	System.exit(EXIT_CODE_UNKNOWN_ERROR);
	    }
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/



