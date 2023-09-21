
package com.tibco.psg.emstools.tools;

import java.io.PrintStream;

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
	public EMSTestTopicConnection(final String[] args) throws Throwable {
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
        catch (final JMSException ex) {
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
				} 
        		catch (final JMSException e) {
					logError(e);
				}
        	
        	//*** CLOSE JMS CONNECTION
        	if (null!=i_connection)
				try {
					logDebug("closing connection...");
					i_connection.close();
				} 
        		catch (final JMSException e) {
					logError(e);
				}
        }
    }

	/**************************************************************************/
	/***  MAIN METHOD  ********************************************************/
	/**************************************************************************/
    
    public static void main(final String[] args) {
        try {
        	new EMSTestTopicConnection(args);
        	System.exit(EXIT_CODE_SUCCESS);
        }
        catch (final Throwable ex) {
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



