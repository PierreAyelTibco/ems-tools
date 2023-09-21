
package com.tibco.psg.emstools.tools;

import java.io.IOException;
import java.util.Vector;

import javax.jms.*;
import javax.naming.NamingException;

import com.tibco.tibjms.Tibjms;

/**
 * <p>
 * @author Richard Lawrence
 * @author Pierre Ayel
 * @version 1.3.3
 */
public class EMSQueueReceiver extends EMSQueueClient {
    
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = -2207822164523036005L;

	/*************************************************************************/
	/***  SUB-CLASSES  *******************************************************/
	/*************************************************************************/
	
	/**
	 * A thread containing one <code>EMSQueueReceiver</code> object.
	 * When the thread runs, it invokes the {@link EMSQueueReceiver#start()} method.
	 * <p>
	 * @author Pierre Ayel
	 * @version 1.3.0
	 * @since 1.3.0
	 */
	public static class pThread extends Thread {
		
		private EMSQueueReceiver m_receiver;
		
		public pThread(int n, EMSQueueReceiver p_receiver) {
			super();
			setName("QueueReceiver-Thread-"+((n<9)? "0":"")+n);
			setDaemon(false);
			m_receiver = p_receiver;
		}
		
		public void run() {
			try {
				m_receiver.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	/*************************************************************************/
	/***  RUNTIME DATA  ******************************************************/
	/*************************************************************************/
	
	/** 
	 * Message producer for sending reply messages.
	 * <p>
	 * @since 1.3.0 
	 */
	private MessageProducer m_msgProducer;
	
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSQueueReceiver</code> object.
	 * <p>
	 * @param p_args The command line arguments.
	 * @throws IOException If one command line parameter uses a file and the file cannot be read.
	 */
	public EMSQueueReceiver(String[] p_args) throws IOException {
		super();
		
    	//1.1: put count with default value 0 and timeout to 0;
    	m_count = 0;
    	m_timeout_s = 0;
    	m_delay_ms = 0; //1.3.0
    	
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
        log("Selector..................... " + getDestinationConfiguration().getSelector());
        log("Max Messages................. "+((m_count>0)? ""+m_count:"no limit"));
        log("Time to wait for next msg.... "+((m_timeout_s>0)? ""+m_timeout_s+"s":"infinite"));
        log("------------------------------------------------------------------------\n");
       
        //1.3.3
        checkArguments();

        //1.2.0 System.out.println("Receiving from queue: "+m_queue_name+"\n");
        //1.3.0
        //start(); //user must call start()...
	}
	
	/**
	 * Starts the queue receiver.
	 * <p>
	 * This is equivalent to <code>start(getAckMode());</code>.
	 * <p>
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @see #start(int)
	 * @since 1.3.0
	 */
	public void start() throws JMSException, NamingException {
		start(getAckMode());
	}
	
	/**
	 * Starts the queue receiver.
	 * <p>
	 * @param p_mode Session acknowledgement mode, 
	 * possible values are {@link javax.jms.Session#AUTO_ACKNOWLEDGE}, 
	 * {@link javax.jms.Session#CLIENT_ACKNOWLEDGE}, {@link javax.jms.Session#DUPS_OK_ACKNOWLEDGE} and
	 * {@link javax.jms.Session#SESSION_TRANSACTED}. This method does not check if the value is correct, but only passes
	 * it to the JMS API.
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @see javax.jms.Session
	 * @since 1.3.0
	 */
	public void start(int p_mode) throws JMSException, NamingException {
		
		//1.3.0
		while(true) {
			long i_totalMsgs = 0;

			//*** CONNECT
	        QueueConnection i_connection = null;
	        QueueSession i_session = null;
	        try {
	            i_connection = createQueueConnection();
	        }
	        //1.2.0
	        catch (JMSException ex) {
	        	logError("Failed to connect...");
	        	logError(ex);
	        	
	        	if (getConnectionConfiguration().mustReconnect()) {
	        		try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		continue;
	        	}
	        	else
	        		System.exit(EXIT_CODE_CONNECTION_ERROR);
	        }
	        
	        MessageConsumer receiver = null;
	        try{
	        	i_session = i_connection.createQueueSession(false, p_mode);
	        	//1.3.0
	        	switch (p_mode) {
	        		case Session.AUTO_ACKNOWLEDGE:
	        			logInfo("Session acknowledgement mode: AUTO_ACKNOWLEDGE");
	        			break;
	        			
	        		case Session.CLIENT_ACKNOWLEDGE:
	        			logInfo("Session acknowledgement mode: CLIENT_ACKNOWLEDGE");
	        			break;

	        		case Session.DUPS_OK_ACKNOWLEDGE:
	        			logInfo("Session acknowledgement mode: DUPS_OK_ACKNOWLEDGE");
	        			break;

	        		case Session.SESSION_TRANSACTED:
	        			logInfo("Session acknowledgement mode: SESSION_TRANSACTED");
	        			break;

	        		default:
	        			logInfo("Session acknowledgement mode: "+p_mode);
	        			break;
	        	}
	        		
	            //1.3.3
	            Queue i_queue = getQueue(i_session);
	        	
	            //Queue queue = i_session.createQueue(m_queue_name);
	
	            String i_selector = getDestinationConfiguration().getSelector();
	            
	            receiver = (null!=i_selector)? i_session.createConsumer(i_queue, i_selector) : i_session.createConsumer(i_queue);
	            m_msgProducer = i_session.createProducer(null);
	
	            //1.2.0
	            logInfo("Receiving from queue: '"+i_queue.getQueueName()+"'");
	            
	            i_connection.start();
	            //1.1.0
	            long i_time_start = System.currentTimeMillis();
	            long i_time_recv_first = 0;
	
		        /* read queue messages */
	            //int i_infile_index = 0;
		        while(true && (m_count==0 || (m_count>0 && i_totalMsgs<m_count))) {
		        	
		            Message message = (m_timeout_s>0)? receiver.receive(m_timeout_s*1000) : receiver.receive();
		            //1.1.0
		            long i_time_recv = System.currentTimeMillis();
		            if (i_time_recv_first==0) i_time_recv_first = i_time_recv;
		            
		            if (message == null) {
		            	if (m_timeout_s>0)
		            		logInfo("No message after " + m_timeout_s + "s...");
		            	break;
		            }
		            
		            i_totalMsgs++;
		            
		            if (!isQuiet()) // 1.3.0
		            	if (isDebugEnabled())
		            		logDebug(i_totalMsgs+": Received message ("+Tibjms.calculateMessageSize(message)+" bytes): "+message);
		            //1.2.0 System.out.println("Received message: "+message);
		            
		            //1.1.0
		            if (!isQuiet()) { // 1.3.0
		            	double i_rate = i_totalMsgs;
		            	if (i_time_recv-i_time_recv_first>0) i_rate = i_rate / (i_time_recv-i_time_recv_first);
		            	i_rate *= 1000f;
		            	logInfo(i_totalMsgs+": Time since first message: " + (i_time_recv-i_time_recv_first) + 
		            		"ms ("+i_rate+"msgs/s), time since connection start: " + (i_time_recv-i_time_start) + "ms");
		            }
	
		            //1.3.0
					onMessage(i_session, message, i_totalMsgs);
	            }
		        
		        //1.3.0
		        break; //break from while(true);
	        }
	        catch (JMSException ex) {
	        	if (getConnectionConfiguration().mustReconnect())
	        		logError(ex);
	        	else
	        		throw ex;
	        }
	        finally {
	        	//*** CLOSE RECEIVER
	        	if (null!=receiver)
	        		try {
		        		//1.1.0
			            logInfo("Received " + i_totalMsgs + " message(s)");
		        	
						logInfo("Closing receiver...");
		        		receiver.close();
					} catch (JMSException ex) {
						logError(ex);
					}
	        	
        		//*** CLOSE MSG REPLY PRODUCER
	            if (null!=m_msgProducer)
	            	try {
						logInfo("Closing reply message producer...");
	            		m_msgProducer.close();
					} catch (JMSException ex) {
						logError(ex);
					}
	        	
	        	//*** CLOSE JMS SESSION
	        	if (null!=i_session)
					try {
						logInfo("Closing session...");
						i_session.close();
					} catch (JMSException ex) {
						logError(ex);
					}
	        	
	        	//*** CLOSE JMS CONNECTION
	        	closeQueueConnection();
	        }
		}// while(true)
    }
	
	/**
	 * Processes incoming messages. Sub-classes can overwrite this method to process messages differently.
	 * <p>
	 * @param p_session The JMS Session that received the message.
	 * @param p_message The received JMS Message.
	 * @throws JMSException In case of failure while extracting data from the message.
	 * @since 1.3.0
	 */
	protected void onMessage(Session p_session, Message p_message, long i_totalMsgs) throws JMSException {
		
		Destination replyDest = p_message.getJMSReplyTo();
		if (m_flag_reply && replyDest != null) {
			if (m_out_file!=null)
		    	saveMessage(p_message, "$MsgRequest$");

		    /* create response message */
		    TextMessage msg = p_session.createTextMessage();

		    if (m_in_files.size()>0) {//1.1.0: infileName != null) {
			    loadMessage(msg);
		    }
		    else {
		    	/* set message text */
		    	msg.setText(m_data);
		    }
		    // Set TIL Properties
		    String op = p_message.getStringProperty("Operation");
		    if (op != null)
		    	msg.setStringProperty("Operation", op);
		    String src = p_message.getStringProperty("Source");
		    if (src != null)
		    	msg.setStringProperty("Source",src);
		    String XID = p_message.getStringProperty("TransactionId");
		    if (XID != null)
		    	msg.setStringProperty("TransactionId", XID);
		    String cv = p_message.getStringProperty("ContractVersion");
		    if (cv != null)
		    	msg.setStringProperty("ContractVersion", cv);

		    msg.setBooleanProperty("JMS_TIBCO_PRESERVE_UNDELIVERED",true);

		    /* set correlation id */
		    String cid = p_message.getJMSCorrelationID();
		    if (cid != null)
		    	msg.setJMSCorrelationID(cid);

		    // Get reply dest
		    String replyName = null;
		    if (replyDest instanceof Topic)
		    	replyName = ((Topic)replyDest).getTopicName();
		    else
		    	replyName = ((Queue)replyDest).getQueueName();

		    /* set delivery mode */
		    int deliveryMode = DeliveryMode.PERSISTENT;

		    if (p_message.getJMSDeliveryMode() == DeliveryMode.NON_PERSISTENT || replyName.startsWith("$TMP"))
		    	deliveryMode = DeliveryMode.NON_PERSISTENT;

		    /* publish response message */
		    m_msgProducer.send(p_message.getJMSReplyTo(), msg, deliveryMode, msg.getJMSPriority(),
		    							msg.getJMSExpiration());

		    logInfo(i_totalMsgs+": Published response (msg id): "+msg.getJMSMessageID());

		    if (m_out_file!=null)
		    	saveMessage(msg, "$MsgResponse$");
		}
		else {
			if (m_out_file!=null)
		    	saveMessage(p_message, "$MsgNotify$");
		}
		
		//1.3.0
		try { Thread.sleep(m_delay_ms); }
		catch (InterruptedException e) { }	
		
		//*** ACKNOWLEDGE THE MESSAGE
		p_message.acknowledge();
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
        System.err.println("  -selector   <selector>  - selector expression");
        System.err.println("  -infile     <file name> - The file name that contains the response message");
        System.err.println("  -log        <file name> - The output log/folder file name");
        System.err.println("  -count      <count>     - maximum number of messages to process (default is infinite)");
        System.err.println("  -timeout    <timeout>   - time to wait for next message in seconds, otherwise stops (default is infinite)");

        //1.0.0
        System.err.println("");
        System.err.println("  -noUnmap                - disables unmapping of received MapMessage(s) before tracing");

        System.exit(EXIT_CODE_INVALID_USAGE);
    }

	/**************************************************************************/
	/***  MAIN METHOD  ********************************************************/
	/**************************************************************************/
    
    public static void main(String args[]) {
        try {
        	//1.3.0
        	//new EMSQueueReceiver(args);
        	
        	EMSQueueReceiver i_tool = new EMSQueueReceiver(args);
        	
        	if (i_tool.getTestThreadCount()>1) {
        		Vector<pThread> i_threads = new Vector<pThread>(i_tool.getTestThreadCount());
        		for(int i=0;i<i_tool.getTestThreadCount();i++) {
        			i_tool.logInfo("Starting new thread ("+(1+i)+"/"+i_tool.getTestThreadCount()+")...");
        			pThread i_thread = new pThread(i+1, new EMSQueueReceiver(args));
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



