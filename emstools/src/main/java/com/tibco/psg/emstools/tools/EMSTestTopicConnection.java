
package com.tibco.psg.emstools.tools;

import javax.jms.*;

/**
 * <p>
 * @author Pierre Ayel
 * @version 1.2.0
 * @since 1.3.3
 */
public class EMSTestTopicConnection extends EMSClient {
    
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = -8079949593760910495L;

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSTestTopicConnection</code> object.
	 * <p>
	 * @param args The command line arguments.
	 * @throws Throwable
	 */
	public EMSTestTopicConnection(String[] args) throws Throwable {
		super();
		
        parseArgs(args);

       /* print parameters */
        log("\n------------------------------------------------------------------------");
        //1.3.0
        log(toString());
        //System.out.println("" + getClass().getSimpleName());
        log("------------------------------------------------------------------------");
        getConnectionConfiguration().logURL(this);
        log("------------------------------------------------------------------------\n");
       
        //*** CONNECT
        TopicConnection i_connection = null;
        TopicSession i_session = null;
        try {
            i_connection = createTopicConnection();
            
            logInfo("connected successfully");
        }
        //1.2.0
        catch (JMSException ex) {
        	logError("failed to connect...");
        	logError(ex);
        	System.exit(EXIT_CODE_CONNECTION_ERROR);
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

	/**************************************************************************/
	/***  MAIN METHOD  ********************************************************/
	/**************************************************************************/
    
    public static void main(String args[]) {
        try {
        	new EMSTestTopicConnection(args);
        	System.exit(EXIT_CODE_SUCCESS);
        }
        catch (Throwable ex) {
        	ex.printStackTrace();
        	System.exit(EXIT_CODE_UNKNOWN_ERROR);
        }
    }

    public void usage() {
        System.err.println("\nUsage: java "+getClass().getSimpleName()+" [options]");
        System.err.println("");
        System.err.println("   where options are:");
        System.err.println("");
        System.err.println("  -server    <server URL> - EMS server URL ");
        System.err.println("  -provider  <JNDI URL>   - JNDI server URL ");
        System.err.println("  -factory   <factory>    - JNDI factory name, default Topic/QueueConnectionFactory");
        System.err.println("  -user      <user name>  - user name, default is null");
        System.err.println("  -password  <password>   - password, default is null");
        
        System.exit(EXIT_CODE_INVALID_USAGE);
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/



