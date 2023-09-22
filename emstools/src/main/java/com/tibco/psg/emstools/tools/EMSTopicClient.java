
package com.tibco.psg.emstools.tools;

import java.io.PrintStream;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.naming.NamingException;

/**
 * <p>
 * @author Pierre Ayel
 * @since 1.3.3
 */
public class EMSTopicClient extends EMSClient {
	
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = -2163177133622223934L;

	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSTopicClient</code> object.
	 * <p>
	 * @param p_args The command line arguments.
	 */
	public EMSTopicClient() {
		super();
	}
	
	/*************************************************************************/
	/***  METHODS  ***********************************************************/
	/*************************************************************************/
	
	public void logTopic() {
		if (null!=getDestinationConfiguration().getTopicJNDIName())
        	log("Topic (JNDI Name)............ " + getDestinationConfiguration().getTopicJNDIName());
        else
        	log("Topic........................ " + getDestinationConfiguration().getTopicName());		
	}
	
	public void checkArguments() {
		
        if (null==getDestinationConfiguration().getTopicName() && null==getDestinationConfiguration().getTopicJNDIName()) { //1.3.3
        	logError("you must specify the topic name or its JNDI name");
        	exitOnInvalidUsage();
        }
        
        //1.3.3
        if (null!=getDestinationConfiguration().getTopicJNDIName() && null==getConnectionConfiguration().getJNDIURL()) {
        	logError("you cannot find a topic by its JNDI name if you do not connect with the JNDI interface");
        	exitOnInvalidUsage();
        }
	}
	
	public Topic getTopic(final TopicSession p_session) throws JMSException, NamingException {
        //1.3.3
        if (null!=getDestinationConfiguration().getTopicJNDIName())
        	return (Topic) getJNDIContext().lookup(getDestinationConfiguration().getTopicJNDIName());
        else
            return p_session.createTopic(getDestinationConfiguration().getTopicName());
	}

	@Override
	public void usage(final PrintStream p_out) {
		//COMPLETED BY CHILD CLASSES
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

