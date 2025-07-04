
package com.tibco.psg.emstools.tools;

import java.io.IOException;
import java.io.PrintStream;

import javax.jms.*;
import javax.naming.NamingException;

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
	 * @throws IOException 
	 */
	public EMSTestQueueConnection(final String[] args) throws IOException {
		super();
		
        parseArgs(args);

       /* print parameters */
        log("\n------------------------------------------------------------------------");
        log(toString());
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
        catch (final JMSException | NamingException ex) {
        	logError("Failed to connect...");
        	logError(ex);
        	System.exit(EXIT_CODE_CONNECTION_ERROR);
        }
        finally {
        	//*** CLOSE JMS CONNECTION
        	EMSUtil.close(this, i_connection);
        }
    }

	/**************************************************************************/
	/***  MAIN METHOD  ********************************************************/
	/**************************************************************************/
    
    public static void main(final String[] args) {
        try {
        	new EMSTestQueueConnection(args);
        	System.exit(EXIT_CODE_SUCCESS);
        }
        catch (final Exception ex) {
        	ex.printStackTrace();
        	System.exit(EXIT_CODE_UNKNOWN_ERROR);
        }
    }

    @Override
    public void usage(final PrintStream p_out) {
        p_out.println("\nUsage: java "+getClass().getSimpleName()+" [options]");
        p_out.println("");
        p_out.println("   where options are:");
        p_out.println("");
        p_out.println("  -server    <server URL> - EMS server URL ");
        p_out.println("  -provider  <JNDI URL>   - JNDI server URL ");
        p_out.println("  -factory   <factory>    - JNDI factory name, default Topic/QueueConnectionFactory");
        p_out.println("  -user      <user name>  - user name, default is null");
        p_out.println("  -password  <password>   - password, default is null");
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/



