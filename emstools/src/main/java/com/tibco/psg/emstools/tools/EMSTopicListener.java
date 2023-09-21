
package com.tibco.psg.emstools.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.jms.*;
import javax.naming.NamingException;

import com.tibco.tibjms.Tibjms;

/**
 * <p>
 * @author Pierre Ayel
 * @version 1.4.0
 */
public class EMSTopicListener extends EMSTopicClient {
    
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = 2866807805219572384L;

	/*************************************************************************/
	/***  SUB-CLASSES  *******************************************************/
	/*************************************************************************/
	
	/**
	 * A thread containing one <code>EMSTopicListener</code> object.
	 * When the thread runs, it invokes the {@link EMSTopicListener#start()} method.
	 * <p>
	 * @author Pierre Ayel
	 * @version 1.3.0
	 * @since 1.3.0
	 */
	public static class pThread extends Thread {
		
		private final EMSTopicListener m_receiver;
		
		public pThread(final int n, final EMSTopicListener p_receiver) {
			super();
			setName("TopicListener-Thread-"+((n<9)? "0":"")+n);
			setDaemon(false);
			m_receiver = p_receiver;
		}
		
		@Override
		public void run() {
			try {
				m_receiver.start();
			} 
			catch (final Exception ex) {
				m_receiver.logError("Failed to start msg receiver: ".concat(ex.getMessage()));
			}
		}
	}
	
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSTopicListener</code> object.
	 * <p>
	 * @param p_args The command line arguments.
	 * @throws IOException If one command line parameter uses a file and the file cannot be read.
	 */
	public EMSTopicListener(final String[] p_args) throws IOException {
		super(p_args);
		
    	m_count = 0;
    	m_timeout_s = 0;
    	m_delay_ms = 0; //1.3.0

    	parseArgs(p_args);

       /* print parameters */
    	log(" ");
        log("------------------------------------------------------------------------");
        //1.3.0
        log(toString());
        log("------------------------------------------------------------------------");
        getConnectionConfiguration().logURL(this);
        //1.3.3
        logTopic();
        log("Selector..................... " + getDestinationConfiguration().getSelector());
        log("Max Messages................. "+((m_count>0)? ""+m_count:"no limit"));
        log("Time to wait for next msg.... "+((m_timeout_s>0)? ""+m_timeout_s+"s":"infinite"));
        log("------------------------------------------------------------------------\n");
       
        //1.3.3
        checkArguments();
	}
	
	/**
	 * Starts the queue receiver.
	 * <p>
	 * This is equivalent to <code>start(getAckMode());</code>.
	 * <p>
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @see #start(int)
	 * @see javax.jms.Session
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
	public void start(final int p_mode) throws JMSException, NamingException {
        
		long i_totalMsgs = 0;
		
		//1.3.0
		while(true) {
		
	        //*** CONNECT
	        TopicConnection i_connection = null;
	        TopicSession i_session = null;
	        try {
	            i_connection = createTopicConnection();
	        }
	        //1.2.0
	        catch (final JMSException ex) {
	        	logError("failed to connect...");
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
	            
	        TopicSubscriber i_subscriber = null;
	        try {
	        	i_session = i_connection.createTopicSession(false, p_mode);
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
	        	Topic i_topic = null;
	            if (null!=getDestinationConfiguration().getTopicJNDIName())
	            	i_topic = (Topic) getJNDIContext().lookup(getDestinationConfiguration().getTopicJNDIName());
	            else
	                i_topic = i_session.createTopic(getDestinationConfiguration().getTopicName());
	        	
	            String i_selector = getDestinationConfiguration().getSelector();
	            i_subscriber = (null!=i_selector)? i_session.createSubscriber(i_topic, i_selector, true) : i_session.createSubscriber(i_topic);
	
	            //1.2.0
	            logInfo("Listening on topic: '"+i_topic.getTopicName()+"'");
	            
	            i_connection.start();
	            //1.1.0
	            long i_time_start = System.currentTimeMillis();
	            long i_time_recv_first = 0;
	
	            /* read topic messages */
	            while(true && (m_count==0 || (m_count>0 && i_totalMsgs<m_count))) {
	        	
		            javax.jms.Message i_msg = (m_timeout_s>0)? i_subscriber.receive(m_timeout_s*1000) : i_subscriber.receive();
		            //1.1.0
		            long i_time_recv = System.currentTimeMillis();
		            if (i_time_recv_first==0) i_time_recv_first = i_time_recv;
	
		            if (i_msg == null) {
		            	if (m_timeout_s>0)
		            		logError("No message after " + m_timeout_s + "s...");
		            	break;
		            }
		            
		            i_totalMsgs++;
		            
		            //1.2.0
		            if (!isQuiet()) // 1.3.0
		            	if (isDebugEnabled())
		            		logDebug(i_totalMsgs+": Received message ("+Tibjms.calculateMessageSize(i_msg)+" bytes): "+i_msg);
		            
		            i_totalMsgs++;
		            
		            //1.1.0
		            if (!isQuiet()) { // 1.3.0
		            	double i_rate = i_totalMsgs;
		            	if (i_time_recv-i_time_recv_first>0) i_rate = i_rate / (i_time_recv-i_time_recv_first);
		            	i_rate *= 1000f;
		            	logInfo(i_totalMsgs+": Time since first message: " + (i_time_recv-i_time_recv_first) + 
		            		"ms ("+i_rate+"msgs/s), time since connection start: " + (i_time_recv-i_time_start) + "ms");
		            }
		            
		            //1.3.0
		            onMessage(i_session, i_msg);
	            }
	         
	            //1.3.0
		        break;
	        }
	        catch (final JMSException ex) {
	        	if (getConnectionConfiguration().mustReconnect())
	        		logError(ex);
	        	else
	        		throw ex;
	        }
	        finally {
	        	//*** CLOSE SUBSCRIBER
	        	if (null!=i_subscriber)
	        		try {
		        		//1.1.0
			            logInfo("Received " + i_totalMsgs + " message(s)");
		        	
						logInfo("Closing subscriber...");
						i_subscriber.close();
					} 
	        		catch (final JMSException ex) {
						logError(ex);
					}
	        	
	        	//*** CLOSE JMS SESSION
	        	if (null!=i_session)
					try {
						logInfo("Closing session...");
						i_session.close();
					} 
	        		catch (final JMSException e) {
						logError(e);
					}
	        	
	        	//*** CLOSE JMS CONNECTION
	        	closeTopicConnection();
	        }
		}
    }
	
	/**
	 * Processes incoming messages. Sub-classes can overwrite this method to process messages differently.
	 * <p>
	 * @param p_session The JMS Session that received the message.
	 * @param p_message The received JMS Message.
	 * @throws JMSException In case of failure while extracting data from the message.
	 * @since 1.3.0
	 */
	protected void onMessage(final Session p_session, final Message p_message) throws JMSException {
		if (m_out_file!=null)
	    	saveMessage(p_message, "$Msg$"); 
		
		//1.3.0
		try { Thread.sleep(m_delay_ms); }
		catch (InterruptedException e) { }	
		
		//*** ACKNOWLEDGE THE MESSAGE
		p_message.acknowledge();
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
        p_out.println("  -server     <serverURL> - EMS server URL");
        p_out.println("  -jndi_url   <JNDI URL>  - JNDI server URL ");
        p_out.println("  -factory    <factory>   - JNDI factory name, default TopicConnectionFactory");
        p_out.println("  -user       <user name> - user name, default is null");
        p_out.println("  -password   <password>  - password, default is null");
        p_out.println("  -topic      <name>      - The Topic full name");
        p_out.println("  -jndi_topic <name>      - The Topic JNDI name");
        p_out.println("  -selector   <selector>  - selector expression");
        p_out.println("  -infile     <file name> - The file name that contains the response message");
        p_out.println("  -log        <file name> - The output log file/folder name");
        p_out.println("  -count      <count>     - maximum number of messages to process (default is infinite)");
        p_out.println("  -timeout    <timeout>   - time to wait for next message in seconds, otherwise stops (default is infinite)");
        
        //1.3.2
        p_out.println("");
        p_out.println("  -noUnmap                - disables unmapping of received MapMessage(s) before tracing");
    }

	/**************************************************************************/
	/***  MAIN METHOD  ********************************************************/
	/**************************************************************************/
    
    public static void main(final String[] args) {
        try {
        	final EMSTopicListener i_tool = new EMSTopicListener(args);
        	
        	if (i_tool.getTestThreadCount()>1) {
        		final List<pThread> i_threads = new ArrayList<>(i_tool.getTestThreadCount());
        		for(int i=0;i<i_tool.getTestThreadCount();i++) {
        			i_tool.logInfo("Starting new thread ("+(1+i)+"/"+i_tool.getTestThreadCount()+")...");
        			final pThread i_thread = new pThread(i+1, new EMSTopicListener(args));
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



