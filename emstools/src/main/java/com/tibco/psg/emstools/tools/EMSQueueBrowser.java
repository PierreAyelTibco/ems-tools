
package com.tibco.psg.emstools.tools;

import javax.jms.*;
import javax.naming.NamingException;

import com.tibco.tibjms.Tibjms;

import java.io.IOException;
import java.util.Enumeration;

/**
 * <p>
 * @author Pierre Ayel
 * @version 1.3.3
 */
public class EMSQueueBrowser extends EMSQueueClient {
    
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = 7767914128510952951L;

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSQueueBrowser</code> object.
	 * <p>
	 * @param p_args The command line arguments.
	 * @throws Throwable
	 */
	public EMSQueueBrowser(String[] p_args) throws IOException {
		super();
		
		// force default count to 0
		m_count = 0;
        parseArgs(p_args);

        /* print parameters */
        log("\n------------------------------------------------------------------------");
        //1.3.0
        log(toString());
        //System.out.println(""+getClass().getSimpleName());
        log("------------------------------------------------------------------------");
        getConnectionConfiguration().logURL(this);
        //1.3.3
        logQueue();
        log("Max Messages................. "+((m_count>0)? ""+m_count:"no limit"));
        log("Selector..................... " + getDestinationConfiguration().getSelector());
        log("------------------------------------------------------------------------\n");

        //1.3.3
        checkArguments();
        
        //1.3.3
        //start(); //user must call start()...
	}
	
	/**
	 * Starts the queue browser.
	 * <p>
	 * @param p_mode Session acknowledgement mode, 
	 * possible values are {@link javax.jms.Session#AUTO_ACKNOWLEDGE}, 
	 * {@link javax.jms.Session#CLIENT_ACKNOWLEDGE}, {@link javax.jms.Session#DUPS_OK_ACKNOWLEDGE} and
	 * {@link javax.jms.Session#SESSION_TRANSACTED}. This method does not check if the value is correct, but only passes
	 * it to the JMS API.
	 * @throws JMSException In case of JMSException.
	 * @throws NamingException In case of JNDI exception when connecting to the server JNDI interface.
	 * @see javax.jms.Session
	 * @since 1.3.3
	 */
	public void start() throws JMSException, NamingException {

        //*** CONNECT
        QueueConnection i_connection = null;
        QueueSession i_session = null;
        try {
            i_connection = createQueueConnection();
        }
        //1.2.0
        catch (JMSException ex) {
        	logError("failed to connect...");
        	logError(ex);
        	System.exit(EXIT_CODE_CONNECTION_ERROR);
        }
        
        try {
        	long i_totalMsgs = 0;
        	 
            i_session = i_connection.createQueueSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
            
            //1.3.3
            Queue i_queue = getQueue(i_session);
            
            //Queue i_queue = i_session.createQueue(m_queue_name);
            
            String i_selector = getDestinationConfiguration().getSelector();
            QueueBrowser browser = (i_selector!=null)? i_session.createBrowser(i_queue, i_selector) : i_session.createBrowser(i_queue);

            //1.2.0
            logInfo("Browsing Queue '"+i_queue.getQueueName()+"'");
            
            for(Enumeration<?> e = browser.getEnumeration() ; e.hasMoreElements() ; ) {
            	
            	if (m_count>0 && i_totalMsgs>=m_count)
            		break;
            	
            	Message i_msg = (Message)(e.nextElement());
            	logInfo("Message ("+Tibjms.calculateMessageSize(i_msg)+" bytes): " + i_msg);
            	
            	if (m_out_file!=null)
    		    	saveMessage(i_msg, "$Msg$");  
            	
            	i_totalMsgs++;
            }
            
            //1.1.0
            logInfo("Browsed " + i_totalMsgs + " message(s)");

            browser.close();
        }
        finally {
        	//*** CLOSE JMS SESSION
        	if (null!=i_session)
				try {
					logDebug("closing session...");
					i_session.close();
				} catch (JMSException e) {
					logError(e);
				}
        	
        	//*** CLOSE JMS CONNECTION
        	if (null!=i_connection)
				try {
					logDebug("closing connection...");
					i_connection.close();
				} catch (JMSException e) {
					logError(e);
				}
        }
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
        System.err.println("  -selector   <selector>  - The selector expression");
        System.err.println("  -log        <file name> - The output log file/folder name");
        
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
        	//1.3.3
        	//new EMSQueueBrowser(args);
        	
        	EMSQueueBrowser i_tool = new EMSQueueBrowser(args);
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



