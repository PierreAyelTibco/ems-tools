
package com.tibco.psg.emstools.tools;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * <p>
 * @author Richard Lawrence
 * @author Pierre Ayel
 * @version 1.3.3
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
		
		private EMSQueueSender m_sender;
		
		public pThread(int n, EMSQueueSender p_receiver) {
			super();
			setName("QueueSender-Thread-"+((n<9)? "0":"")+n);
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
	
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSQueueSender</code> object.
	 * <p>
	 * @param p_args The command line arguments.
	 * @throws IOException If one command line parameter uses a file and the file cannot be read.
	 */
	public EMSQueueSender(String[] p_args) throws IOException {
		super();
		
        parseArgs(p_args);

        /* print parameters */
        log(" ");
        log("------------------------------------------------------------------------");
        //1.3.0
        log(toString());
        //System.out.println(""+getClass().getSimpleName());
        log("------------------------------------------------------------------------");
        getConnectionConfiguration().logURL(this);
        //1.3.3
        logQueue();
        log("------------------------------------------------------------------------\n");

        //1.3.3
        checkArguments();

        //1.2.0 System.out.println("Sending on queue '"+m_queue_name+"'\n");
        //1.3.0
        //start(); //user must call start()...
	}
	
	/**
	 * Starts the queue sender, sends all messages speficied in the command line argument and then closes.
	 * <p>
	 * Exits if the connection fails.
	 * <p>
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @throws IOException In case a trigger topic is used and a command line parameter uses a file and the file cannot be read.
	 * @since 1.3.0
	 */
	public void start() throws JMSException, NamingException, IOException {
        
        //*** CONNECT, CREATE SESSION AND QUEUE SENDER
		/* 1.3.2 */
		pWorkSession_Sender i_work_session = init();
        /*QueueConnection i_connection = null;
        QueueSession i_session = null;
        try {
            i_connection = createQueueConnection();
        }
        //1.2.0
        catch (JMSException ex) {
        	logError("failed to connect...");
        	logError(ex);
        	System.exit(EXIT_CODE_CONNECTION_ERROR);
        }*/
        
        try{
            /*i_session = i_connection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            Queue i_queue = i_session.createQueue(m_queue_name);
            QueueSender i_sender = i_session.createSender(i_queue);*/
            
            //1.3.0
			//*** WAIT FOR 1ST MSG ON TRIGGER TOPIC BEFORE STARTING PUBLISHING/SENDING LOOP
           	waitOnTriggerTopic();	

            //1.2.0
            logInfo("Sending on queue '"+i_work_session.getQueue().getQueueName()+"'");
            
            if (m_in_files.size()==1 && m_in_files.firstElement().isDirectory())
            	while (i_work_session.getTotalMsgs()<m_count) { //1.3.0
            		
            		for(File i_file : m_in_files.firstElement().listFiles()) {
            			
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
	public pWorkSession_Sender init() throws JMSException, NamingException, IOException {
		return new pWorkSession_Sender(this);
	}
	
    /**
     * Prints the command line usage on standard error.
     */
    public void usage() {
        System.err.println("\nUsage: java "+getClass().getSimpleName()+" [options]");
        System.err.println("");
        System.err.println("   where options are:");
        System.err.println("");
        System.err.println("  -server     <serverURL> - EMS server URL, default is local server");
        System.err.println("  -jndi_url   <JNDI URL>  - JNDI server URL ");
        System.err.println("  -factory    <factory>   - JNDI factory name, default QueueConnectionFactory");
        System.err.println("  -user       <user name> - user name, default is null");
        System.err.println("  -password   <password>  - password, default is null");
        System.err.println("  -queue      <name>      - The Queue full name");
        System.err.println("  -jndi_queue <name>      - The Queue JNDI name");
        System.err.println("  -infile     <file name> - The file/folder that contains the message(s) to send");
        //System.err.println("                             You can repeat this parameter multiple time to send multiple files/folders");
        System.err.println("  -log        <file name> - The output log/folder file name");
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
	        //new EMSQueueSender(args);
    		
    		EMSQueueSender i_tool = new EMSQueueSender(args);
        	
        	if (i_tool.getTestThreadCount()>1) {
        		Vector<pThread> i_threads = new Vector<pThread>(i_tool.getTestThreadCount());
        		for(int i=0;i<i_tool.getTestThreadCount();i++) {
        			i_tool.logInfo("Starting new thread ("+(1+i)+"/"+i_tool.getTestThreadCount()+")...");
        			pThread i_thread = new pThread(i+1, new EMSQueueSender(args));
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



