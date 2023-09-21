
package com.tibco.psg.emstools.tools;

import javax.jms.*;

/**
 * <p>
 * @author Pierre Ayel
 * @version 1.3.0
 * @since 1.3.3
 */
public class EMSTestQueueConnection extends EMSClient {
    
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = -5102512236026333287L;

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSTestQueueConnection</code> object.
	 * <p>
	 * @param args The command line arguments.
	 * @throws Throwable
	 */
	public EMSTestQueueConnection(String[] args) throws Throwable {
		super();
		
        parseArgs(args);

       /* print parameters */
        log("\n------------------------------------------------------------------------");
        //1.3.0
        log(toString());
        //System.out.println(""+getClass().getSimpleName());
        log("------------------------------------------------------------------------");
        getConnectionConfiguration().logURL(this);
        log("------------------------------------------------------------------------\n");
       
        //*** CONNECT
        QueueConnection i_connection = null;
        try {
            i_connection = createQueueConnection();
            
            logInfo("connected successfully");
        }
        //1.2.0
        catch (JMSException ex) {
        	logError("failed to connect...");
        	logError(ex);
        	System.exit(EXIT_CODE_CONNECTION_ERROR);
        }
        
        try{
        }
        finally {
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
        	new EMSTestQueueConnection(args);
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



