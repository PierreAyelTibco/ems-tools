
package com.tibco.psg.emstools.tools;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * <p>
 * @author Richard Lawrence
 * @author Pierre Ayel
 * @version 1.3.3
 */
public class EMSTopicPublisher extends EMSTopicClient {
    
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = 314185433581886248L;

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
		
		private EMSTopicPublisher m_sender;
		
		public pThread(int n, EMSTopicPublisher p_receiver) {
			super();
			setName("TopicPublisher-Thread-"+((n<9)? "0":"")+n);
			setDaemon(false);
			m_sender = p_receiver;
		}
		
		public void run() {
			try {
				m_sender.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * A class containing a connection, session and topic sender created by this object.
	 * <p>
	 * @author Pierre Ayel
	 * @version 1.3.2
	 * @since 1.3.2
	 */
	public class pWorkSession extends Object {
		
        private TopicConnection m_connection;
        private TopicSession m_session;
        private Topic m_topic;
        private TopicPublisher m_publisher;
        
        /** Total number of message sent. */
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
        public pWorkSession() throws JMSException, NamingException, IOException {
        	super();
        	
        	//*** CONNECT
	        try {
	            m_connection = createTopicConnection();
	        }
	        //1.2.0
	        catch (JMSException ex) {
	        	logError("failed to connect...");
	        	logError(ex);
	        	System.exit(EXIT_CODE_CONNECTION_ERROR);
	        }
	            
	        try {
	            m_session = m_connection.createTopicSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
	            
	            //1.3.3
	            m_topic = EMSTopicPublisher.this.getTopic(m_session);
	            
	            m_publisher = m_session.createPublisher(m_topic);
	
	            //1.2.0
	            //1.3.3: logInfo("Publishing on topic '"+m_topic.getTopicName()+"'");
	        }
	        finally {
	        }
        }
        
		public TopicConnection getTopicConnection() {
			return m_connection;
		}
		
        public TopicSession getTopicSession() {
        	return m_session;
        }
        
        public Topic getTopic() {
        	return m_topic;
        }
        
        public TopicPublisher getTopicPublisher() {
        	return m_publisher;
        }
        
        /**
         * Gets the number of message sent so far.
         */
        public long getTotalMsgs() {
        	return m_totalMsgs;
        }
        
    	/**
    	 * Closes the topic publisher.
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
            m_publisher.publish(message, delMode, message.getJMSPriority(), message.getJMSExpiration());

            //1.3.0
            m_totalMsgs++;
            
            logInfo(getTotalMsgs()+": Sent message: "+message.getJMSMessageID());

            if (m_out_file!=null)
    		    saveMessage(message, "$MsgNotify$");

    		try { Thread.sleep(m_delay_ms); }
    		catch (InterruptedException e) { }		
    	}
	};
	
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSTopicPublisher</code> object.
	 * <p>
	 * @param p_args The command line arguments.
	 * @throws IOException If one command line parameter uses a file and the file cannot be read.
	 */
	public EMSTopicPublisher(String[] p_args) throws IOException {
		super(p_args);
		
        parseArgs(p_args);

        /* print parameters */
        log(" ");
        log("------------------------------------------------------------------------");
        //1.3.0
        log(toString());
        getConnectionConfiguration().logURL(this);
        //1.3.3
        logTopic();
        log("------------------------------------------------------------------------\n");

        //1.3.3
        checkArguments();

        //1.2.0 System.out.println("Publishing on topic '"+m_topic_name+"'\n");
        //1.3.0
        //start(); //user must call start()...
	}
	
	/**
	 * Starts the topic publisher.
	 * <p>
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
	 * @since 1.3.0
	 */
	public void start() throws JMSException, NamingException, IOException {

        //*** CONNECT, CREATE SESSION AND TOPIC PUBLISHER
		/* 1.3.2 */
		pWorkSession i_work_session = init();
		/*
        //*** CONNECT
        TopicConnection i_connection = null;
        TopicSession i_session = null;
        try {
            i_connection = createTopicConnection();
        }
        //1.2.0
        catch (JMSException ex) {
        	logError("failed to connect...");
        	logError(ex);
        	System.exit(EXIT_CODE_CONNECTION_ERROR);
        }*/
		
        try {
            /*i_session = i_connection.createTopicSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
            Topic i_topic = i_session.createTopic(m_topic_name);
            TopicPublisher i_publisher = i_session.createPublisher(i_topic);*/

            //1.3.0
			//*** WAIT FOR 1ST MSG ON TRIGGER TOPIC BEFORE STARTING PUBLISHING/SENDING LOOP
           	waitOnTriggerTopic();	
            
            //1.2.0
            logInfo("Publishing on topic '"+i_work_session.getTopic().getTopicName()+"'");
            
            if (m_in_files.size()==1 && m_in_files.firstElement().isDirectory())
            	while (i_work_session.getTotalMsgs()<m_count) { //1.3.0
            		
            		for(File i_file : m_in_files.firstElement().listFiles()) {
            			
            			//1.3.0
            			if (i_work_session.getTotalMsgs()>=m_count) break;
            			
            			if (i_file.isDirectory()) continue;
            			if (i_file.isHidden()) continue;
            			if (!i_file.getName().toLowerCase().endsWith(".msg")) continue;
            		
    	            	//*** CREATE MESSAGE
    	                javax.jms.TextMessage message = i_work_session.getTopicSession().createTextMessage();

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
	                javax.jms.TextMessage message = i_work_session.getTopicSession().createTextMessage();
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
	    }
	    finally {
	    	//*** CLOSE JMS SESSION
        	//*** CLOSE JMS CONNECTION
        	i_work_session.close();
        }
	}
	
	/**
	 * Starts the topic publisher.
	 * <p>
	 * Exits if the connection fails.
	 * <p>
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
	 * @since 1.3.0
	 */
	public pWorkSession init() throws JMSException, NamingException, IOException {
		return new pWorkSession();
	}
	
    /**
     * Prints the command line usage on standard error.
     */
    public void usage() {
        System.err.println("\nUsage: java "+getClass().getSimpleName()+" [options]");
        System.err.println("");
        System.err.println("   where options are:");
        System.err.println("");
        System.err.println("  -server     <serverURL> - EMS server URL");
        System.err.println("  -jndi_url   <JNDI URL>  - JNDI server URL ");
        System.err.println("  -factory    <factory>   - JNDI factory name, default TopicConnectionFactory");
        System.err.println("  -user       <user name> - user name, default is null");
        System.err.println("  -password   <password>  - password, default is null");
        System.err.println("  -topic      <name>      - The Topic full name");
        System.err.println("  -jndi_topic <name>      - The Topic JNDI name");
        System.err.println("  -infile     <file name> - The file/folder that contains the message(s) to send");
        //System.err.println("                             You can repeat this parameter multiple time to send multiple files/folders");
        System.err.println("  -log        <file name> - The output log file/folder name");
        System.err.println("  -count      <count>     - Number of messages to send, default 1");
        System.err.println("  -delay      <millisecs> - Delay between messages, default 1000");
        
        System.exit(EXIT_CODE_INVALID_USAGE);
    }

	/**************************************************************************/
	/***  MAIN METHOD  ********************************************************/
	/**************************************************************************/

    public static void main(String args[]) {
    	try {
	        //1.3.0
    		//new EMSTopicPublisher(args);
    		
    		EMSTopicPublisher i_tool = new EMSTopicPublisher(args);
        	
        	if (i_tool.getTestThreadCount()>1) {
        		Vector<pThread> i_threads = new Vector<pThread>(i_tool.getTestThreadCount());
        		for(int i=0;i<i_tool.getTestThreadCount();i++) {
        			i_tool.logInfo("Starting new thread ("+(1+i)+"/"+i_tool.getTestThreadCount()+")...");
        			pThread i_thread = new pThread(i+1, new EMSTopicPublisher(args));
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
	    catch (Throwable ex) {
	    	ex.printStackTrace();
	    	System.exit(EXIT_CODE_UNKNOWN_ERROR);
	    }
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/



