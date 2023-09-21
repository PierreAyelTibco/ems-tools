
package com.tibco.psg.emstools.tools;

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
	public EMSTopicClient(String[] p_args) {
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
            //1.2.0 System.err.println("Error: must specify topic name");
            usage();
        }
        
        //1.3.3
        if (null!=getDestinationConfiguration().getTopicJNDIName() && null==getConnectionConfiguration().getJNDIURL()) {
        	logError("you cannot find a topic by its JNDI name if you do not connect with the JNDI interface");
        	usage();
        }
	}
	
	public Topic getTopic(TopicSession p_session) throws JMSException, NamingException {
        //1.3.3
        if (null!=getDestinationConfiguration().getTopicJNDIName())
        	return (Topic) getJNDIContext().lookup(getDestinationConfiguration().getTopicJNDIName());
        else
            return p_session.createTopic(getDestinationConfiguration().getTopicName());
	}

	@Override
	public void usage() {
		// TODO Auto-generated method stub
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

