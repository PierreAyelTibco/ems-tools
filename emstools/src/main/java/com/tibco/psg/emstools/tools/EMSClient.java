
package com.tibco.psg.emstools.tools;

import javax.jms.*;
import javax.naming.*;

import com.tibco.tibjms.Tibjms;
import com.tibco.tibjms.TibjmsQueueConnectionFactory;
import com.tibco.tibems.ufo.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * <p>
 * Version 1.2.0:
 * <ul>
 * <li>The method {@link #saveMessage(Message, String)} can now write each message in its own file.</li>
 * </ul>
 * <p>
 * @author Richard Lawrence (TIL Test Harness superclass)
 * @author Pierre Ayel
 * @version 1.4.0
 */
public abstract class EMSClient extends BaseObject implements Serializable, ExceptionListener {
	
	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = 4123566618083592853L;

	/*************************************************************************/
	/***  SUB-CLASSES  *******************************************************/
	/*************************************************************************/

	/** 
	 * <p>
	 * @since 1.3.3
	 * @version 1.4.0 
	 */
	public static class ConnectionConfiguration extends Object implements Serializable {

		/*************************************************************************/
		/***  DEFINITIONS  *******************************************************/
		/*************************************************************************/
		
	    /** Unique ID for serialisation. */
		private static final long serialVersionUID = -9138881146466661105L;

		/*************************************************************************/
		/***  RUNTIME DATA  ******************************************************/
		/*************************************************************************/
		
		/**
		 * The JNDI provider factory class-name (default value is <code>com.tibco.tibjms.naming.TibjmsInitialContextFactory</code>).
		 */
		private String 			m_provider_factory = "com.tibco.tibjms.naming.TibjmsInitialContextFactory";
		
		/**
		 * @see #getJNDIURL()
		 */
	    private String 			m_provider_url;
	    
	    /**
	     * @see #getServerURL();
	     */
	    private String 			m_server_url = "tcp://localhost:7222"; //1.3.7: default value
	    
	    private String 			m_factory_name;
	    private String 			m_username = "admin"; //1.3.7: default value 
	    private String 			m_password = ""; //1.3.7: default value
	    
	    /** 
	     * The JNDI login (if <code>null</code>, we use {@link #m_username}).
	     * <p>
	     * @since 1.3.3
	     */
	    private String m_jndi_username;

	    /** 
	     * The JNDI password (if <code>null</code>, we use {@link #m_password}).
	     * <p>
	     * @since 1.3.3
	     */
	    private String m_jndi_password;
	    
	    /** @since 1.3.3 */
	    private ArrayList<String> m_jndi_properties;
	    
	    private boolean m_flag_reconnect = false;
	    private boolean m_flag_shared_connection = false;
	    
	    /** @since 1.2.0 */
	    private String m_data_factory_clientid;
	    private String m_data_clientid;
	    
		/*************************************************************************/
		/***  ACCESSOR METHODS  **************************************************/
		/*************************************************************************/
	   
	    /** @since 1.3.3 */
	    public String getJNDIURL() {
	    	return m_provider_url;
	    }
	    
	    /** @since 1.3.3 */
	    public String getServerURL() {
	    	return m_server_url;
	    }
	    
	    public boolean mustReconnect() {
	    	return m_flag_reconnect;
	    }
	    
		/*************************************************************************/
		/***  METHODS  ***********************************************************/
		/*************************************************************************/
	    
	    public void logURL(EMSClient p_client) {
	    	if (null!=getJNDIURL())
	        	p_client.log("JNDI URL..................... " + getJNDIURL());
	        else
	        	p_client.log("Server....................... " + getServerURL());
	    	if (null!=m_jndi_username)
	    		p_client.log("JNDI User......................... " + m_jndi_username);
	        p_client.log("User......................... "+(m_username!=null?m_username:"(null)"));
	    }
	    
	    public InitialContext createJNDIContext() throws NamingException {
	    	
		    final Hashtable<Object,Object> i_env = new Hashtable<>();
		    i_env.put(Context.INITIAL_CONTEXT_FACTORY, m_provider_factory);
		    i_env.put(Context.PROVIDER_URL, m_provider_url);
	
		    if (null!=m_jndi_username || null!=m_username)  {
		    	i_env.put(Context.SECURITY_PRINCIPAL, (null!=m_jndi_username)? m_jndi_username : m_username);
		    	if (null!=m_jndi_password || null!=m_password)
		    		i_env.put(Context.SECURITY_CREDENTIALS, (null!=m_jndi_password)? m_jndi_password : m_password);
		    }
		    
		    //*** ADDITIONAL USER PROPERTIES
		    if (null!=m_jndi_properties) //1.3.4
			    for(String i_property : m_jndi_properties) {
			    	int i_pos = i_property.indexOf('=');
			    	if (i_pos > 0)
			    		i_env.put(i_property.substring(0, i_pos).trim(), i_property.substring(i_pos+1).trim());
			    }
		    
		    return new InitialContext(i_env);
	    }
	}
	
	/** 
	 * <p>
	 * @since 1.3.3
	 * @version 1.4.0 
	 */
	public static class SSLConfiguration extends Object implements Serializable {

		/*************************************************************************/
		/***  DEFINITIONS  *******************************************************/
		/*************************************************************************/
		
	    /** Unique ID for serialisation. */
		private static final long serialVersionUID = 193515875843759560L;

		/*************************************************************************/
		/***  RUNTIME DATA  ******************************************************/
		/*************************************************************************/
		
	    private boolean 			m_ssl_enabled = false;
	    private String 				m_ssl_identity;
	    private String 				m_ssl_key;
	    private String 				m_ssl_password;
	    private List<String> 		m_ssl_trustedcerts;
	    private String 				m_ssl_vendor;
	    private String 				m_ssl_ciphers;
	    private String 				m_ssl_hostname;
	    private boolean 			m_ssl_verify_host = false;
	    private boolean 			m_ssl_verify_hostname = false;
	    private boolean 			m_ssl_trace = false;
	    private boolean 			m_ssl_debug_trace = false;
		
	    /**
	     * When using a TibjmsQueueConnectionFactory (connecting with tcp), this method creates appropriate
	     * properties to associate with the factory.
	     * <p>
	     * @since 1.1.0
	     */
	    @SuppressWarnings({ "unchecked", "rawtypes" })
		private Map<String,Object> createFactoryProperties() {
	    	
	    	final HashMap<String,Object> i_env = new HashMap<>();
	    	
	    	if (m_ssl_enabled) {
	    		if (m_ssl_identity!=null && !m_ssl_identity.equals("")) {
	    			i_env.put(com.tibco.tibjms.TibjmsSSL.IDENTITY, m_ssl_identity);
	    			i_env.put(com.tibco.tibjms.TibjmsSSL.PASSWORD, m_ssl_password);
	    			
	    			if (m_ssl_key!=null && !m_ssl_key.equals(""))
	    				i_env.put(com.tibco.tibjms.TibjmsSSL.PRIVATE_KEY, m_ssl_key);
	    		}

	    		//fixed in 1.2.1
				if (m_ssl_trustedcerts!=null) {
					i_env.put(com.tibco.tibjms.TibjmsSSL.TRUSTED_CERTIFICATES, m_ssl_trustedcerts);
				}

				if (m_ssl_vendor!=null && !m_ssl_vendor.equals(""))
					i_env.put(com.tibco.tibjms.TibjmsSSL.VENDOR, m_ssl_vendor);
				
				if (m_ssl_ciphers!=null && !m_ssl_ciphers.equals(""))
					i_env.put(com.tibco.tibjms.TibjmsSSL.CIPHER_SUITES, m_ssl_ciphers);
				
				if (m_ssl_hostname!=null && !m_ssl_hostname.equals(""))
					i_env.put(com.tibco.tibjms.TibjmsSSL.EXPECTED_HOST_NAME, m_ssl_hostname);
				
				i_env.put(com.tibco.tibjms.TibjmsSSL.ENABLE_VERIFY_HOST, m_ssl_verify_host);
				i_env.put(com.tibco.tibjms.TibjmsSSL.ENABLE_VERIFY_HOST_NAME, m_ssl_verify_hostname);
				i_env.put(com.tibco.tibjms.TibjmsSSL.TRACE, m_ssl_trace);
				i_env.put(com.tibco.tibjms.TibjmsSSL.DEBUG_TRACE, m_ssl_debug_trace);
	    	}
	    	
	    	return i_env;
	    }
	}
	
	/** 
	 * <p>
	 * @since 1.3.3
	 * @version 1.4.0 
	 */
	public static class DestinationConfiguration extends Object implements Serializable {
		
		/*************************************************************************/
		/***  DEFINITIONS  *******************************************************/
		/*************************************************************************/
		
	    /** Unique ID for serialisation. */
		private static final long serialVersionUID = -4597661906160479212L;

		/*************************************************************************/
		/***  RUNTIME DATA  ******************************************************/
		/*************************************************************************/
		
	    /** 
	     * Name of the topic to publish into or receive from (made private in 1.3.3). 
	     * <p>
	     * @see #getTopicName()
	     */
	    private String	 			m_topic_name;

	    /**
	     * @see #getTopicJNDIName() 
	     * @since 1.3.3 
	     */
	    private String				m_topic_jndi_name;
	    
	    /** 
	     * Name of the queue to publish into or receive from (made private in 1.3.3). 
	     * <p>
	     * @see #getQueueName()
	     */
	    private String 				m_queue_name;

	    /**
	     * @see #getQueueJNDIName() 
	     * @since 1.3.3 
	     */
	    private String				m_queue_jndi_name;
	    
	    /**
	     * Destination selector (used by EMSQueueBrowser).
	     */
	    private String 			m_selector;
		
		/*************************************************************************/
		/***  ACCESSOR METHODS  **************************************************/
		/*************************************************************************/
	    
	    /** @since 1.3.3 */
	    public String getTopicName() {
	    	return m_topic_name;
	    }

	    /** @since 1.3.3 */
	    public String getTopicJNDIName() {
	    	return m_topic_jndi_name;
	    }

	    /** @since 1.3.3 */
	    public String getQueueName() {
	    	return m_queue_name;
	    }

	    /** @since 1.3.3 */
	    public String getQueueJNDIName() {
	    	return m_queue_jndi_name;
	    }

	    /** @since 1.3.3 */
	    public String getSelector() {
	    	return m_selector;
	    }
	    
	}
	
	/*************************************************************************/
	/***  RUNTIME DATA  ******************************************************/
	/*************************************************************************/
	
	/** @since 1.3.3 */
	private ConnectionConfiguration m_config_connection = new ConnectionConfiguration();
	
    /**
     * List of input files.
     * <p>
     * @since 1.1.0
     */
    protected List<File> 		m_in_files = new ArrayList<>(1);
    
    /**
     * If multiple file names have been provided in the command line, this member is
     * the index of the file currently being read by the method {@link #loadMessage(TextMessage)} 
     * (the index moves in a round robin fashion; when a file needs to be read next time, the index increases).
     * <p>
     * @since 1.1.0
     */
    private int 				m_in_files_index = 0;
    
    /** Output file data. */
    protected File				m_out_file;
    private transient FileOutputStream 	m_out_fstream;
    private transient PrintStream 		m_out_pstream;

    /** @since 1.3.3 */
    private final DestinationConfiguration m_config_destination = new DestinationConfiguration();    
    
    /** Default message text data to send. */ 
    protected String      m_data = "message\r\nline2";
    
    /** Maximum number of message to send or receive (if 0, there is no limit; default value is <code>1</code>). */
    protected int				m_count = 1;
    
    /** Delay (in milliseconds) between message publications (default value is <code>1000</code>). */
    protected int				m_delay_ms = 1000;
    
    /** Time (in seconds) to wait for the next message reception (default value is <code>30</code>). */
    protected int				m_timeout_s = 30;
    
    protected String 			m_replyTo;
    
     /**
     * Indicates if MapMessage should be written as unmapped (default is <code>true</code>).
     */
    protected boolean 			m_unmapMapMsgs = true;
    
    /**
     * SSL Configuration
     * <p>
     * @since 1.3.3
     */
    private final SSLConfiguration m_config_ssl = new SSLConfiguration();
    
    /** @since 1.2.0 */
    public static final int EXIT_CODE_INVALID_USAGE = -2;
    
    /** @since 1.2.0 */
    public static final int EXIT_CODE_SUCCESS = 0;

    /** @since 1.2.0 */
    public static final int EXIT_CODE_INVALID_FILE = -1;

    /** @since 1.2.0 */
    public static final int EXIT_CODE_CONNECTION_ERROR = -10;

    /** @since 1.2.0 */
    public static final int EXIT_CODE_UNKNOWN_ERROR = -3;
    
    /**
     * Lists of system properties to set before connecting (if the property has currently no value).
     * <p> 
     * @since 1.2.0 
     * @see #setSystemProperties()
     */
    private final HashMap<String,String> m_data_system_properties = new HashMap<>();

    /**
     * Lists of command line option and the corresponding system property name to store 
     * in {@link #m_data_system_properties} when parsing the command line.
     * <p> 
     * @since 1.2.0 
     */
    private static final HashMap<String,String> m_data_cmdline_system_properties = new HashMap<>();

    protected boolean m_flag_reply = true; 
    
    /** @since 1.3.0 */
    private TopicConnection m_topic_connection;
    private QueueConnection m_queue_connection;
    /** @since 1.3.3 */
    private transient InitialContext m_jndi_context;
    
    /** 
     * <p>
     * UFO connection is re-used in the onException method.
     * <p>
     * @since 1.3.8 
     */
    private TibjmsUFOQueueConnectionFactory m_ufo_queue_connection_factory;

    /** 
     * <p>
     * UFO connection is re-used in the onException method.
     * <p>
     * @since 1.3.8 
     */
    private TibjmsUFOTopicConnectionFactory m_ufo_topic_connection_factory;

    /**
     * When running multiple thread, each thread can have its own client or they can share one client defined here.
     */
    private static final EMSClient SHARED_EMS_CLIENT = new EMSClient() {

    	/** Unique ID for serialisation. */
		private static final long serialVersionUID = 5669789213010252943L;

		@Override
	    public boolean sharesConnection() {
	    	return false;
	    }
		
		@Override
		public void usage(final PrintStream p_out) {
		}
    };
    
    /**
     * Integer value of the command line parameter "-test-threads" (default value is <code>1</code>).
     * <p>
     * @since 1.3.0
     * @see #getTestThreadCount()
     */
    private int m_data_param_test_threads = 1;

    /**
     * String value of the command line parameter "-test-trigger-topic" (default value is <code>null</code>).
     * This is the name of a topic where the tool listens before starting publishing or sending messages (even in 
     * multi-threaded mode).
     * <p>
     * @since 1.3.0
     * @see #getTestTriggerTopic()
     */
    private String m_data_param_test_trigger_topic;
    
    /**
     * Integer value of the command line parameter "-ackmode", e.g. the message acknowledgement mode
     * (default value is {@link javax.jms.Session#AUTO_ACKNOWLEDGE}).
     * <p>
     * @since 1.3.0
     * @see #getAckMode()
     */
    private int m_data_param_ack_mode = javax.jms.Session.AUTO_ACKNOWLEDGE;
    
    /**
     * Boolean value of the command line parameter "-quiet" (default value is <code>false</code>).
     * <p>
     * @since 1.3.0
     * @see #isQuiet()
     */
    private boolean m_flag_param_quiet = false;
    
    /**
     * Copy of the command line arguments.
     * <p>
     * @since 1.3.0
     * @see #getArgs();
     */
    private String[] m_data_params;
    
    /** @since 1.3.0 */
    private Properties m_data_properties;
    
	/*************************************************************************/
	/***  STATIC INITIALISATION  *********************************************/
	/*************************************************************************/
    
    static {
    	m_data_cmdline_system_properties.put("-close[-_]in[-_]callback", Tibjms.PROP_CLOSE_IN_CALLBACK);

    	m_data_cmdline_system_properties.put("-connect[-_]attempts", Tibjms.PROP_CONNECT_ATTEMPTS);
    	m_data_cmdline_system_properties.put("-connect[-_]attempt[-_]timeout", Tibjms.PROP_CONNECT_ATTEMPT_TIMEOUT);

    	m_data_cmdline_system_properties.put("-daemon[-_]dispatcher", Tibjms.PROP_DAEMON_DISPATCHER);

    	m_data_cmdline_system_properties.put("-trace[-_]ft[-_]events", Tibjms.PROP_FT_EVENTS_EXCEPTION);
    	m_data_cmdline_system_properties.put("-trace[-_]ft[-_]switch", Tibjms.PROP_FT_SWITCH_EXCEPTION);
    	
    	m_data_cmdline_system_properties.put("-message[-_]encoding", Tibjms.PROP_MESSAGE_ENCODING);
    	
    	m_data_cmdline_system_properties.put("-multicast[-_]daemon", Tibjms.PROP_MULTICAST_DAEMON);
    	m_data_cmdline_system_properties.put("-multicast[-_]enabled", Tibjms.PROP_MULTICAST_ENABLED);
    	
    	m_data_cmdline_system_properties.put("-ping[-_]interval", Tibjms.PROP_PING_INTERVAL);
    	
    	m_data_cmdline_system_properties.put("-reconnect[-_]attempts", Tibjms.PROP_RECONNECT_ATTEMPTS);
    	m_data_cmdline_system_properties.put("-reconnect[-_]attempt[-_]timeout", Tibjms.PROP_RECONNECT_ATTEMPT_TIMEOUT);

    	m_data_cmdline_system_properties.put("-socket[-_]connect[-_]timeout", Tibjms.PROP_SOCKET_CONNECT_TIMEOUT);
    	m_data_cmdline_system_properties.put("-socket[-_]receive", Tibjms.PROP_SOCKET_RECEIVE);
    	m_data_cmdline_system_properties.put("-socket[-_]send", Tibjms.PROP_SOCKET_SEND);
    	
    	m_data_cmdline_system_properties.put("-trace[-_]file", Tibjms.PROP_TRACE_FILE);
    	
    	//1.3.8
    	Tibjms.setExceptionOnFTSwitch(false);
    	Tibjms.setExceptionOnFTEvents(true);
    }
    
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/
	
	/**
	 * Creates a new <code>EMSClient</code> object.
	 * <p>
	 * @since 1.1.0
	 */
    public EMSClient() {
    	super();

    	m_data_system_properties.put(Tibjms.PROP_FT_EVENTS_EXCEPTION, "true");
    	m_data_system_properties.put(Tibjms.PROP_FT_SWITCH_EXCEPTION, "true");
    	m_data_system_properties.put(Tibjms.PROP_CONNECT_ATTEMPTS, "10,500");
    	m_data_system_properties.put(Tibjms.PROP_RECONNECT_ATTEMPTS, "10,500");
    	
    	//1.4.0
    	setSharedTrace(SHARED_EMS_CLIENT);
    }

	/*************************************************************************/
	/***  ACCESSOR METHODS  **************************************************/
	/*************************************************************************/
   
    /** @since 1.3.3 */
    public DestinationConfiguration getDestinationConfiguration() {
    	return m_config_destination;
    }

    /** @since 1.3.3 */
    public ConnectionConfiguration getConnectionConfiguration() {
    	return m_config_connection;
    }
    
    /**
     * Indicates if this client uses a shared connection or not.
     * <p>
     * @since 1.3.0
     */
    public boolean sharesConnection() {
    	return getConnectionConfiguration().m_flag_shared_connection;
    }
    
    /**
     * Gets the integer value of the command line parameter "-test-threads" (default value is <code>1</code>).
     * <p>
     * @since 1.3.0.
     */
    public int getTestThreadCount() {
    	return m_data_param_test_threads;
    }

    /**
     * Gets the string value of the command line parameter "-test-trigger-topic" (default value is <code>null</code>).
     * This is the name of a topic where the tool listens before starting publishing or sending messages (even in 
     * multi-threaded mode).
     * <p>
     * @since 1.3.0
     */
    public String getTestTriggerTopic() {
    	return m_data_param_test_trigger_topic;
    }
    
    /**
     * Gets the copy of the command line arguments.
     * <p>
     * @since 1.3.0
     */
    public String[] getArgs() {
    	return m_data_params;
    }
    
    /**
     * Gets the integer value of the command line parameter "-ackmode", e.g. the message acknowledgement mode
     * (default value is {@link javax.jms.Session#AUTO_ACKNOWLEDGE}).
     * <p>
     * @since 1.3.0
     */
    public int getAckMode() {
    	return m_data_param_ack_mode;
    }
    
    /**
     * Gets the Boolean value of the command line parameter "-quiet" (default value is <code>false</code>).
     * <p>
     * @since 1.3.0
     */
    public boolean isQuiet() {
    	return m_flag_param_quiet;
    }
    
	/*************************************************************************/
	/***  METHODS  ***********************************************************/
	/*************************************************************************/

    /**
     * <p>
     * Version 1.3.0:
     * <p>
     * The method returns the same connection if called multiple times.
     * If the connection is set to be shared...
     * <p>
     * @return
     * @throws NamingException 
     * @throws JMSException 
     * @throws Exception 
     */
    public synchronized TopicConnection createTopicConnection() throws NamingException, JMSException {
    	
    	//1.3.0
    	if (sharesConnection())
   			return SHARED_EMS_CLIENT.createTopicConnection();
    	if (null!=m_topic_connection)
    		return m_topic_connection;
    	
		TopicConnectionFactory i_factory = null;
	
		//1.2.0
		setSystemProperties();
		
		if (m_config_connection.m_provider_url != null) {
			//1.3.0
			logInfo("Connecting with JNDI interface: " + m_config_connection.m_provider_url);

		    /*
		     * Lookup topic connection factory which must exist in the
		     * factories config file.
		     */
			String i_fname = (m_config_connection.m_factory_name!=null)? m_config_connection.m_factory_name : "TopicConnectionFactory";
	    	i_factory = (TopicConnectionFactory) getJNDIContext().lookup(i_fname);
		}
		else if (m_config_connection.m_server_url != null) {
			//1.3.0
			logInfo("Connecting with TCP interface: " + m_config_connection.m_server_url);

			//1.3.8
			if (m_config_connection.m_server_url.indexOf('+')>0)
				m_ufo_topic_connection_factory = new TibjmsUFOTopicConnectionFactory(m_config_connection.m_server_url);
			else
				i_factory = new com.tibco.tibjms.TibjmsTopicConnectionFactory(m_config_connection.m_server_url, null, createFactoryProperties());
		}
		else {
			logError("You must specify provider or server URL");
			exitOnInvalidUsage();
		}
		
		//1.4.0
		if (null!=m_ufo_topic_connection_factory) {
			if (isDebugEnabled())
				logDebug("Found connection factory: ".concat(m_ufo_topic_connection_factory.toString()));
		}
		else if (null!=i_factory) {
			if (isDebugEnabled())
				logDebug("Found connection factory: ".concat(i_factory.toString()));
		}
		else
			throw new java.lang.IllegalStateException("Connection factory not found");
		
		//*** SETUP SECURITY
		setGlobalSSLSettings();
		
		//1.2.0
		if (StringUtil.isValid(m_config_connection.m_data_factory_clientid))
			if (i_factory instanceof TibjmsQueueConnectionFactory)
				((TibjmsQueueConnectionFactory)i_factory).setClientID(m_config_connection.m_data_factory_clientid);
		
		//*** CREATE CONNECTION
		m_topic_connection = 
			(null!=m_ufo_topic_connection_factory)? 
				m_ufo_topic_connection_factory.createTopicConnection(m_config_connection.m_username,  m_config_connection.m_password) : 
				i_factory.createTopicConnection(m_config_connection.m_username, m_config_connection.m_password);
		
		//1.4.0
		if (isDebugEnabled())
			logDebug("Created connection: ".concat(m_topic_connection.toString()));
		
		//1.3.8
		m_topic_connection.setExceptionListener(this);
				
		//1.3.0
		logInfo("Connected (topic connection)...");
		
		//*** SET CONNECTION CLIENT ID
		EMSUtil.setClientID(this, m_topic_connection, m_config_connection.m_data_clientid);
		
		//*** TRACE CONNECTION PROPERTIES
		EMSUtil.traceConnectionMetadata(this, m_topic_connection);
		
		return m_topic_connection;
    }

    /**
     * @since 1.3.0
     */
    public synchronized void closeTopicConnection() throws JMSException {
    	
    	if (sharesConnection()) {
   			SHARED_EMS_CLIENT.closeTopicConnection();
   			return;
    	}
    	
    	EMSUtil.close(this, m_topic_connection);
    	
    	m_topic_connection = null;
    }
    
    /**
     * @throws NamingException 
     * @since 1.3.3
     */
    public InitialContext getJNDIContext() throws NamingException {
    	
    	if (sharesConnection())
   			return SHARED_EMS_CLIENT.getJNDIContext();
    	
    	if (null==m_jndi_context)
    		m_jndi_context = m_config_connection.createJNDIContext();
		
    	return m_jndi_context;
    }
    
    /**
     * <p>
     * Version 1.3.0:
     * <p>
     * The method returns the same connection if called multiple times.
     * If the connection is set to be shared...
     * <p>
     * @return
     * @throws NamingException 
     * @throws JMSException 
     * @throws Exception 
     */
    public synchronized QueueConnection createQueueConnection() throws NamingException, JMSException {
       	
    	//1.3.0
    	if (sharesConnection())
   			return SHARED_EMS_CLIENT.createQueueConnection();
    	if (null!=m_queue_connection)
    		return m_queue_connection;

    	QueueConnectionFactory i_factory = null;
	
		//1.2.0
		setSystemProperties();
		
		if (m_config_connection.m_provider_url != null) {
			//1.3.0
			logInfo("Connecting with JNDI interface: " + m_config_connection.m_provider_url);

			/*
		     * Lookup topic connection factory which must exist in the
		     * factories config file.
		     */
			String i_fname = (m_config_connection.m_factory_name!=null)? m_config_connection.m_factory_name : "QueueConnectionFactory";
	    	i_factory = (QueueConnectionFactory) getJNDIContext().lookup(i_fname);
		}
		else if (m_config_connection.m_server_url != null) {
			//1.3.0
			logInfo("Connecting with TCP interface: " + m_config_connection.m_server_url);
			
			//1.3.8
			if (m_config_connection.m_server_url.indexOf('+')>0)
				m_ufo_queue_connection_factory = new TibjmsUFOQueueConnectionFactory(m_config_connection.m_server_url);
			else
				i_factory = new TibjmsQueueConnectionFactory(m_config_connection.m_server_url, null, createFactoryProperties());
		}
		else {
			logError("You must specify provider or server URL");
			exitOnInvalidUsage();
		}
		
		//1.4.0
		if (null!=m_ufo_queue_connection_factory) {
			if (isDebugEnabled())
				logDebug("Found connection factory: ".concat(m_ufo_queue_connection_factory.toString()));
		}
		else if (null!=i_factory) {
			if (isDebugEnabled())
				logDebug("Found connection factory: ".concat(i_factory.toString()));
		}
		else
			throw new java.lang.IllegalStateException("Connection factory not found");
		
		//*** SETUP SECURITY
		setGlobalSSLSettings();
		
		//1.2.0
		if (StringUtil.isValid(m_config_connection.m_data_factory_clientid))
			if (i_factory instanceof TibjmsQueueConnectionFactory) {
				if (null!=m_ufo_queue_connection_factory)
					m_ufo_queue_connection_factory.setClientID(m_config_connection.m_data_clientid);
				else
					((TibjmsQueueConnectionFactory)i_factory).setClientID(m_config_connection.m_data_factory_clientid);
			}
	
		//*** CREATE CONNECTION
		m_queue_connection = 
			(null!=m_ufo_queue_connection_factory)? 
				m_ufo_queue_connection_factory.createQueueConnection(m_config_connection.m_username,  m_config_connection.m_password) : 
				i_factory.createQueueConnection(m_config_connection.m_username, m_config_connection.m_password);
		
		//1.4.0
		if (isDebugEnabled())
			logDebug("Created connection: ".concat(m_queue_connection.toString()));
		
		//1.3.8
		m_queue_connection.setExceptionListener(this);
				
		//1.3.0
		logInfo("Connected (queue connection)...");
		
		//*** SET CONNECTION CLIENT ID
		EMSUtil.setClientID(this, m_queue_connection, m_config_connection.m_data_clientid);
		
		//*** TRACE CONNECTION PROPERTIES
		EMSUtil.traceConnectionMetadata(this, m_queue_connection);
		
		return m_queue_connection;
    }
    
    /**
     * @since 1.3.0
     */
    public synchronized void closeQueueConnection() throws JMSException {
    	
    	if (sharesConnection()) {
   			SHARED_EMS_CLIENT.closeQueueConnection();
   			return;
    	}
    	
    	EMSUtil.close(this, m_queue_connection);
    	
    	m_queue_connection = null;
    }
    
    /**
     * With JNDI SSL, the client side certificate files have to be setup in the connection factory.
     * If the connection factory is on the EMS server, this means client files must be present on the
     * EMS server machine, which seems silly in term of security.
     * <p>
     * This methods updates the TIBCO EMS security settings so client side settings are not required in
     * the connection factory on the EMS server.
     * <p>
     * @since 1.1.0
     */
    private void setGlobalSSLSettings() throws JMSSecurityException {
    	/*if (m_ssl_enabled) {
    		com.tibco.tibjms.TibjmsSSL.setVerifyHost(m_ssl_verify_host);
    		com.tibco.tibjms.TibjmsSSL.setVerifyHostName(m_ssl_verify_hostname);
    		if (m_ssl_identity!=null && !m_ssl_identity.equals(""))
    			com.tibco.tibjms.TibjmsSSL.setIdentity(m_ssl_identity, m_ssl_key, m_ssl_password.toCharArray());
    		
    		if (m_ssl_trustedcerts!=null && m_ssl_trustedcerts.equals("")) {
				//Vector i_trusted = new Vector(1);
				//i_trusted.add(m_ssl_trustedcerts);
				com.tibco.tibjms.TibjmsSSL.addTrustedCerts(m_ssl_trustedcerts);
			}    		
    	}*/
    }
    
    /**
     * @since 1.2.0
     */
    private void setSystemProperties() {
    	for(String i_name : m_data_system_properties.keySet())
    		if (null==System.getProperty(i_name))
    			System.setProperty(i_name, m_data_system_properties.get(i_name));
    }
    
    /**
     * When using a TibjmsQueueConnectionFactory (connecting with tcp), this method creates appropriate
     * properties to associate with the factory.
     * <p>
     * @since 1.1.0
     */
	private Map<String,Object> createFactoryProperties() {
    	return m_config_ssl.createFactoryProperties();
    }
    
    /**
     * Writes a message into the output file.
     * <p>
     * Since version 1.2.0, if the output file is a folder, each message is written in its own file.
     * <p>
     * @param p_msg The message (cannot be <code>null</code>).
     * @param p_header A string line to write before the message details (cannot be <code>null</code>).
     */
    public void saveMessage(final Message p_msg, final String p_header) {
    	
    	//1.2.0
    	File i_out_file = m_out_file; // File where message must be written
    	
    	try {
    		if (p_msg instanceof MapMessage && m_unmapMapMsgs) {
    			final byte[] i_bytes = ((MapMessage)p_msg).getBytes("message_bytes");

    			if (null!=i_bytes) {
    				saveMessage(Tibjms.createFromBytes(i_bytes), p_header);
    				return;
    			}    			
    		}
    		
    		//*** IF NO FILE IS OPENED YET (first msg to save)
    		if (null==m_out_pstream) { //otherwise we are writing all msgs into a single file and it is already opened
    			
    			//1.2.0
    			//*** if output file is a folder, we write each message in <folder>/<queuename>/<msg id>.msg file
    			if (m_out_file.isDirectory() && m_out_file.exists()) {
    				
    				//*** CREATE NEW FOLDER FOR QUEUE NAME
    				final File i_folder = new File(m_out_file, toFilename(p_msg.getJMSDestination().toString()));
    				i_folder.mkdir();
    				
    				//*** CREATE FILE FOR MESSAGE
    				i_out_file = new File(i_folder, toFilename(p_msg.getJMSMessageID()).concat(".msg"));
    			}
    			
    			//*** OPEN FILE FOR WRITTING
    			m_out_fstream = new FileOutputStream(i_out_file, true);
				m_out_pstream = new PrintStream(m_out_fstream);
		    }
		    m_out_pstream.println(p_header);
		    
		    //*** WRITE MESSAGE METADATA
		    m_out_pstream.println("$MsgHeader$");
		    
		    //*** MESSAGE ID
		    m_out_pstream.print("JMSMessageID=");
		    m_out_pstream.println(p_msg.getJMSMessageID());
		    
		    //*** TIMESTAMP
		    final Date d = new Date();
		    d.setTime(p_msg.getJMSTimestamp());
		    m_out_pstream.print("JMSTimestamp=");
		    m_out_pstream.println(d.toString());
		    
		    m_out_pstream.print("JMSDestination=");
		    m_out_pstream.println(p_msg.getJMSDestination());
		    
		    //*** DELIVERY MODE
		    switch (p_msg.getJMSDeliveryMode()) {
		    	case javax.jms.DeliveryMode.PERSISTENT:
		    		m_out_pstream.println("JMSDeliveryMode=PERSISTENT");
		    		break;
		    		
		    	case javax.jms.DeliveryMode.NON_PERSISTENT:
		    		m_out_pstream.println("JMSDeliveryMode=NON_PERSISTENT");
		    		break;
		    		
		    	default:
		    		m_out_pstream.println("JMSDeliveryMode=RELIABLE");
		    		break;
		    }
		    
		    if (p_msg.getJMSCorrelationID() != null) {
		    	m_out_pstream.print("JMSCorrelationID=");
		    	m_out_pstream.println(p_msg.getJMSCorrelationID());
		    }
		    if (p_msg.getJMSType() != null) {
		    	m_out_pstream.print("JMSType=");
		    	m_out_pstream.println(p_msg.getJMSType());
		    }
		    if (p_msg.getJMSReplyTo() != null) {
		    	m_out_pstream.println("JMSReplyTo=");
		    	m_out_pstream.println(p_msg.getJMSReplyTo());
		    }
		    if (p_msg.getJMSExpiration() > 0) {
		    	d.setTime(p_msg.getJMSExpiration());
		    	m_out_pstream.print("JMSExpiration=");
		    	m_out_pstream.println(d.toString());
		    }
		    
		    //1.2.0: write JMS Priority as well
		    m_out_pstream.print("JMSPriority=");
		    m_out_pstream.println(p_msg.getJMSPriority());
		    
		    //*** WRITE MESSAGE PROPERTIES
		    m_out_pstream.println("$MsgProperties$");
		    for(final Enumeration<?> props = p_msg.getPropertyNames() ; props.hasMoreElements() ; ) {
		    	final String ps = (String) props.nextElement();
		    	switch (ps) {
		    		case "JMS_TIBCO_PRESERVE_UNDELIVERED":
		    			m_out_pstream.print(ps);
		    			m_out_pstream.print("=");
		    			m_out_pstream.println(p_msg.getBooleanProperty(ps));
		    			break;
		    			
		    		default:
		    			m_out_pstream.print(ps);
		    			m_out_pstream.print("=");
		    			m_out_pstream.println(p_msg.getStringProperty(ps));
		    			break;
		    	}
		    }
		    
		    //*** WRITE MESSAGE BODY
		    m_out_pstream.println("$MsgBody$");
	
		    if (p_msg instanceof TextMessage) {
		    	m_out_pstream.println(((TextMessage) p_msg).getText());
		    }
		    else if (p_msg instanceof MapMessage) {
		    	m_out_pstream.println(p_msg.toString());
		    } 
		    else if (p_msg instanceof BytesMessage) {
		    	final BytesMessage  bytesMsg = (BytesMessage) p_msg;
		    	bytesMsg.reset();
		    	long s = bytesMsg.getBodyLength();
		    	if (s > 1000) {
				    System.err.println("Warning: Bytes message limited to 1000 bytes");
				    s = 1000;
				}
				final byte[] b = new byte[(int)s];
				bytesMsg.readBytes(b,(int)s);
				m_out_pstream.println(dumpBytes(b));
		    }
		    else if (p_msg instanceof StreamMessage) {
				m_out_pstream.println(p_msg.toString());
		    }
		    else if (p_msg instanceof ObjectMessage) {
				m_out_pstream.println(p_msg.toString());
		    }
	
		    //*** WRITE END
		    m_out_pstream.println("$MsgEnd$");
		    m_out_pstream.println("");
		    
		    //1.2.0
		    m_out_pstream.flush();
		}
		catch (final Exception ex) {
			//1.2.0
			System.err.println("Error: failed to write message to file: " + ((null!=i_out_file)? i_out_file.getAbsolutePath() : "null"));
			
		    System.err.println(ex.getClass().getSimpleName()+": "+ex.getMessage());
		    ex.printStackTrace();
		    return ;
		}
    	//1.2.0
    	finally {
			if (m_out_file.isDirectory() && m_out_file.exists()) {
				m_out_pstream.close();
				try {
					m_out_fstream.close();
				} 
				catch (final IOException ex) {
					System.err.println("Error: failed to close message file: " + ((null!=i_out_file)? i_out_file.getAbsolutePath() : "null"));
				    System.err.println(ex.getClass().getSimpleName()+": "+ex.getMessage());
				}
				
				m_out_pstream = null;
				m_out_fstream = null;
			}
    	}
    } 

    public void loadMessage(final TextMessage msg) {
		final File f = m_in_files.get(m_in_files_index);
		m_in_files_index++;
		if (m_in_files_index>=m_in_files.size())
			m_in_files_index = 0;

		loadMessage(msg, f);
    } 
    
    /**
     * @since 1.2.0
     */
    public void loadMessage(final TextMessage msg, final File p_file) {
    	
    	BufferedReader d = null;
    	try {
    		d = new BufferedReader(new FileReader(p_file));
    		String l;
    		StringBuilder t = new StringBuilder();
    		boolean first = true;
    		
    		//1.3.7
    		boolean i_header = false, i_props = false, i_body = true;
    		while ((l = d.readLine()) != null) {
    			if (l.startsWith("$Msg$")) {
					i_header = false; i_props = false; i_body = false;
				}
    			else if (l.startsWith("$MsgHeader$")) {
					i_header = true; i_props = false; i_body = false;
				}
				else if (l.startsWith("$MsgProperties$")) {
					i_header = false; i_props = true; i_body = false;
				}
				else if (l.startsWith("$MsgBody$")) {
					i_header = false; i_props = false; i_body = true;
				}
				else if (l.startsWith("$MsgEnd$")) {
		    		break;
				}
				else if (i_header)
					setJMSMsgProp(l,msg);
				else if (i_props)
					setMsgProp(l,msg);
				else if (i_body) {
					if (!first)
						t.append('\n');
					t.append(l);
					first = false;
				}
    		}
    		
			msg.setText(t.toString());
    	}
    	catch (final Exception ex) {
			System.err.println("Error: failed to read message from file: " + p_file.getAbsolutePath());
		    System.err.println(ex.getClass().getSimpleName()+": "+ex.getMessage());
		    ex.printStackTrace();
    		return ;
    	}
    	finally {
    		if (null!=d)
				try {
					d.close();
				} 
    			catch (final IOException ex) {
					System.err.println("Error: failed to close file: " + p_file.getAbsolutePath());
				    System.err.println(ex.getClass().getSimpleName()+": "+ex.getMessage());
				}
    	}
    } 
    
    public void setJMSMsgProp(final String ps, final TextMessage m) {
    	try {
		    int i = ps.indexOf('=');
		    if (i > 0) {
				String p = ps.substring(0,i);
				String v = ps.substring(i+1);
		
				if (p.equals("JMSDeliveryMode")) {
				    if (v.startsWith("NON"))
				    	m.setJMSDeliveryMode(javax.jms.DeliveryMode.NON_PERSISTENT);
				    else
				    	m.setJMSDeliveryMode(javax.jms.DeliveryMode.PERSISTENT);
				}
				else if (p.equals("JMSCorrelationID"))
				    m.setJMSCorrelationID(v);
		
				else if (m_replyTo == null && p.equals("JMSReplyTo"))
				    m_replyTo = v;
				else if (p.equals("JMSType"))
				    m.setJMSType(v);
		
				else if (p.equals("JMSExpiration"))
				    m.setJMSExpiration(Integer.parseInt(v));
		
				else if (p.equals("JMSPriority"))
				    m.setJMSPriority(Integer.parseInt(v));
		
				else 
				    System.err.println("Warning: Unsupported JMS property: "+p);
		    }
		}
		catch (final JMSException ex) {
			//1.2.0
			System.err.println("Error: failed to set message data...");
		    System.err.println(ex.getClass().getSimpleName()+": "+ex.getMessage());
			
		    //System.err.println("JMSException: "+ex.getMessage());
		    return ;
		}
    }
    
    public void setMsgProp(final String ps, final TextMessage m) {
    	try {
		    int i = ps.indexOf('=');
		    if (i > 0) {
				String p = ps.substring(0,i);
				String v = ps.substring(i+1);
		
				if (p != null && p.length()>0) {
				    if (v.equals("true") || v.equals("false"))
				    	m.setBooleanProperty(p,Boolean.valueOf(v).booleanValue()); 
				    else
				    	m.setStringProperty(p,v); 
				}
		    }
    	}
    	catch(final JMSException ex) {
    		System.err.println("JMSException: ".concat(ex.getMessage()));
    		return ;
    	}
    }

    public abstract void usage(PrintStream p_out);
    
    /** 
     * <p>
     * @throws IOException If one command line parameter uses a file and the file cannot be read.
     * @since 1.3.0 
     */
    public void parseArgs(final Properties p_props) throws IOException {
    	
    	final ArrayList<String> i_args = new ArrayList<>();
    	
    	for(Enumeration<Object> e = p_props.keys() ; e.hasMoreElements() ; ) {
    		final String i_name = e.nextElement().toString();
    		
    		if (i_name.equals("emsclient.propfile")) continue;
    		
    		if (i_name.startsWith("emsclient.")) {
    			
    			final String i_value = p_props.getProperty(i_name);

    			if (i_value.equals("true"))
        			i_args.add("-"+i_name.substring("emsclient.".length()));
    			else if (!i_value.equals("false")) {
    				i_args.add("-"+i_name.substring("emsclient.".length()));
    				i_args.add(i_value);
    			}
    		}
    	}
    	
    	parseArgs(i_args.toArray(new String[]{}));
    }
    
    /** @since 1.3.0 */
    public String getProperty(final String p_name, final String p_default) {
    	return (null!=m_data_properties)? m_data_properties.getProperty(p_name, p_default) : p_default;
    }

    /**
     * <p>
	 * @throws IOException If one command line parameter uses a file and the file cannot be read.     
	 */
    public void parseArgs(final String[] args) throws IOException {
    	
    	/*logInfo("Parsing arguments...");
    	for(String i_arg : args)
    		logInfo("Argument: ".concat(i_arg));
    	try {
    		throw new Exception();
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();
    	}*/
    	
    	//1.3.0: parse from a property file...
    	if (this!=SHARED_EMS_CLIENT) {    	
    		if (args.length>0 && args[0].equals("-propfile")) {
        		if (args.length<2) 
        			exitOnInvalidUsage();
        	
        		File i_propfile = new File(args[1]);
        		if (!i_propfile.exists()) {
        			logError("Cannot find property file: " + i_propfile.getAbsolutePath());
        			exitOnInvalidUsage();
        		}
        		
        		m_data_properties = new Properties();
        		FileInputStream i_sfile = null;
        		try {
        			i_sfile = new FileInputStream(i_propfile);
        			m_data_properties.load(i_sfile);
        		}
        		catch (IOException ex) {
        			logError("Failed to load property file: " + i_propfile.getAbsolutePath());
        			logError(ex);
        			exitOnInvalidUsage();
        		}
        		finally {
        			if (null!=i_sfile)
        				try {
        					i_sfile.close();
        				}
        				catch (IOException ex) {
        					logWarning("Failed to close property file: " + i_propfile.getAbsolutePath());
        					logWarning(ex.getMessage());
        				}
        		}
        		
        		parseArgs(m_data_properties);
        		
        		//1.3.2
        		// parse other arguments, so they can overwrite properties...
        		parseArgsImpl(args);
        		return;
    		}
    	}
    	
    	// 1.3.2
    	parseArgsImpl(args);
    }
    
    /** @since 1.4.0 */
    private String parseStringOrEmptyArg(final String[] args, final int i) {
    	if ((i+1) >= args.length) 
    		exitOnInvalidUsage();
   		return args[i+1];
    }
    
    /** @since 1.4.0 */
    private String parseStringArg(final String[] args, final int i) {
    	if ((i+1) >= args.length) 
    		exitOnInvalidUsage();
    	if (StringUtil.isValid(args[i+1]))
    		return args[i+1];
    	
		System.err.println("Error: invalid value of "+args[i]+" parameter: ".concat(args[i+1]));
		exitOnInvalidUsage();
		return null;
    }

    /** @since 1.4.0 */
    private int parseIntegerArg(final String[] args, final int i) {
    	if ((i+1) >= args.length) exitOnInvalidUsage();
    	try {
    		final int i_value = Integer.parseInt(args[i+1]);
    		
    		if (i_value<0) {
        		System.err.println("Error: invalid value of "+args[i]+" parameter: ".concat(args[i+1]));
        		exitOnInvalidUsage();
    		}
    		
    		return i_value;
    	}
    	catch (final NumberFormatException ex) {
    		System.err.println("Error: invalid value of "+args[i]+" parameter: ".concat(args[i+1]));
    		exitOnInvalidUsage();
    	}
    	return -1;
    }
    
    /**
     * Parses the command line arguments.
     * <p>
     * @since 1.3.2 
     */
    private void parseArgsImpl(final String[] args) throws IOException { 
        int i=0;

        //1.3.0
      	m_data_params = args;
          
        while(i < args.length) {
            if (args[i].compareTo("-server")==0) {
                m_config_connection.m_server_url = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-factory")==0) {
                m_config_connection.m_factory_name = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-provider")==0 || args[i].compareTo("-jndi_url")==0) {
                m_config_connection.m_provider_url = parseStringArg(args, i);
                i += 2;
            }
            //1.3.3
            else if (args[i].compareTo("-jndi_provider_factory")==0) {
                m_config_connection.m_provider_factory = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-topic")==0) {
                m_config_destination.m_topic_name = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-jndi_topic")==0) { //1.3.3
                m_config_destination.m_topic_jndi_name = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-queue")==0) {
                m_config_destination.m_queue_name = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-jndi_queue")==0) { //1.3.3
                m_config_destination.m_queue_jndi_name = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-reply")==0) {
                m_replyTo = parseStringArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-infile")==0) {
                final File i_file = new File(parseStringArg(args, i));
                m_in_files.add(i_file);
                
                //1.2.0
                if (!i_file.exists()) {
                	System.err.println("Error: cannot find file or folder: " + i_file.getAbsolutePath());
                	System.exit(EXIT_CODE_INVALID_FILE);
                }
                if (!i_file.canRead()) {
                	System.err.println("Error: cannot read file or folder: " + i_file.getAbsolutePath());
                	System.exit(EXIT_CODE_INVALID_FILE);
                }
                
                i += 2;
            }
            else if (args[i].equalsIgnoreCase("-log") || args[i].equalsIgnoreCase("-outfile")) {
                m_out_file = new File(parseStringArg(args, i));
                
                //1.2.0: check the parent folder...
                final File i_parent = m_out_file.getParentFile();
                if (null!=i_parent && !m_out_file.exists()) {
	                if (!i_parent.exists()) {
	                	System.err.println("Error: cannot find file or folder: " + i_parent.getAbsolutePath());
	                	System.exit(EXIT_CODE_INVALID_FILE);
	                }
	                if (!i_parent.canWrite()) {
	                	System.err.println("Error: cannot write into file or folder: " + i_parent.getAbsolutePath());
	                	System.exit(EXIT_CODE_INVALID_FILE);
	                }
                }
                                
                i += 2;
            }
            else if (args[i].compareTo("-count")==0) {
            	m_count = parseIntegerArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-delay")==0) {
            	m_delay_ms = parseIntegerArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-timeout")==0) {
            	m_timeout_s = parseIntegerArg(args, i);
                i += 2;
            }
            else if (args[i].compareTo("-user")==0) {
                m_config_connection.m_username = parseStringArg(args, i);
                i += 2;
            }
            //1.3.3
            else if (args[i].compareTo("-jndi_user")==0) {
                m_config_connection.m_jndi_username = parseStringArg(args, i);;
	            i += 2;
	        }
            else if (args[i].compareTo("-password")==0) {
                m_config_connection.m_password = parseStringOrEmptyArg(args, i);
                i += 2;
            }
            //1.3.3
            else if (args[i].compareTo("-jndi_password")==0) {
                m_config_connection.m_jndi_password = parseStringOrEmptyArg(args, i);
                i += 2;
            }
            //1.2.0
            else if (args[i].matches("-password[-_]file")) {
                final File i_file = new File(parseStringArg(args, i));
                if (!i_file.exists()) {
                	System.err.println("Error: cannot find password file: " + i_file.getAbsolutePath());
                	System.exit(EXIT_CODE_INVALID_FILE);
                }
                if (!i_file.canRead()) {
                	System.err.println("Error: cannot read password file: " + i_file.getAbsolutePath());
                	System.exit(EXIT_CODE_INVALID_FILE);
                }
                
                m_config_connection.m_password = readTextFile(i_file).trim();
                i += 2;
            }
            else if (args[i].compareTo("-help")==0) {
            	exitOnInvalidUsage();
            }
            //1.1
            else if (args[i].compareTo("-selector")==0) {
                m_config_destination.m_selector = parseStringArg(args, i);;
                i += 2;
            }
            //1.3.2
            else if (args[i].compareTo("-noUnmap")==0) {
            	m_unmapMapMsgs = false;
            	i++;
            }
            //1.1.0
            else if (args[i].compareTo("-ssl")==0) {
            	m_config_ssl.m_ssl_enabled = true;
            	i++;
            }
            else if (args[i].matches("-ssl[-_]identity")) {
            	m_config_ssl.m_ssl_identity = parseStringArg(args, i);;
            	i+=2;
            }
            else if (args[i].matches("-ssl[-_]key")) {
            	m_config_ssl.m_ssl_key = parseStringArg(args, i);;
            	i+=2;
        	}
            else if (args[i].matches("-ssl[-_]password")) {
            	m_config_ssl.m_ssl_password = parseStringOrEmptyArg(args, i);;
            	i+=2;
    		}
    		else if (args[i].matches("-ssl[-_]trusted([-_]certs){0,1}")) {
    			if (null==m_config_ssl.m_ssl_trustedcerts)
    				m_config_ssl.m_ssl_trustedcerts = new Vector<String>();
    			m_config_ssl.m_ssl_trustedcerts.add(parseStringArg(args, i));
    			i+=2;
    		}
            else if (args[i].matches("-ssl[-_]vendor")) {
            	m_config_ssl.m_ssl_vendor = parseStringArg(args, i);;
            	i+=2;
            }
            else if (args[i].matches("-ssl[-_]ciphers")) {
            	m_config_ssl.m_ssl_ciphers = parseStringArg(args, i);;
            	i+=2;
            }
            else if (args[i].matches("-ssl[-_]hostname")) {
            	m_config_ssl.m_ssl_hostname = parseStringArg(args, i);;
            	i+=2;
            }
            else if (args[i].matches("-ssl[-_]verify[-_]host")) {
            	m_config_ssl.m_ssl_verify_host = true;
            	i++;
            }
            else if (args[i].matches("-ssl[-_]verify[-_]hostname")) {
            	m_config_ssl.m_ssl_verify_hostname = true;
            	i++;
            }
            else if (args[i].matches("-ssl[-_]trace")) {
            	m_config_ssl.m_ssl_trace = true;
            	i++;
            }
            else if (args[i].matches("-ssl[-_]debug[-_]trace")) {
            	m_config_ssl.m_ssl_debug_trace = true;
            	i++;
            }
            //1.2.0
            else if (args[i].matches("-debug")) {
            	setDebug(true);
            	i++;
            }
            else if (args[i].matches("-noreply")) {
            	m_flag_reply = false;
            	i++;
            }
            else if (args[i].matches("-factory[-_]clientid")) {
            	m_config_connection.m_data_factory_clientid = parseStringArg(args, i);
            	i+=2;
            }
            else if (args[i].matches("-clientid")) {
            	m_config_connection.m_data_clientid = parseStringArg(args, i);
            	i+=2;
            }
            //1.3.0
            else if (args[i].matches("-shared")) {
            	m_config_connection.m_flag_shared_connection = true;
            	i++;
            }
            //1.3.0
            else if (args[i].matches("-reconnect")) {
            	m_config_connection.m_flag_reconnect = true;
            	i++;
            }
            //1.3.0
            else if (args[i].matches("-test-threads")) {
                m_data_param_test_threads = parseIntegerArg(args, i);
                i += 2;
            }   
            else if (args[i].matches("-test-trigger-topic")) {
            	m_data_param_test_trigger_topic = parseStringArg(args, i);
            	i+=2;
            }
            //1.3.0
            else if (args[i].matches("-ackmode")) {
            	parseAckModeArg(i, args);
            	i+=2;
            }
            //1.3.0
            else if (args[i].matches("-quiet")) {
            	m_flag_param_quiet = true;
            	i++;
            }
            //1.3.0
            else if (args[i].matches("-log4j")) {
            	setLog4j(true);
            	i++;
            }
            //1.3.3
            else if (args[i].matches("-jndi_property")) {
            	if (null==m_config_connection.m_jndi_properties) //1.3.4
            		m_config_connection.m_jndi_properties = new ArrayList<String>();
            	m_config_connection.m_jndi_properties.add(parseStringOrEmptyArg(args, i));
            	i+=2;
            }
            else {
            	boolean i_found = false;
            	for(String i_pattern : m_data_cmdline_system_properties.keySet())
            		if (args[i].matches(i_pattern)) {
            			m_data_system_properties.put(m_data_cmdline_system_properties.get(i_pattern), parseStringOrEmptyArg(args, i));
            			i+=2;
            			
            			i_found = true;
            			break;
            		}
            	
            	if (!i_found) {
                    m_data = args[i];
                    i++;
            	}
            }
        }
  
      	//1.3.0
    	if (this!=SHARED_EMS_CLIENT)
    		SHARED_EMS_CLIENT.parseArgs(args);
    }
    
    /** @since 1.4.0 */
    protected void exitOnInvalidUsage() {
		usage(System.err);
		System.err.println("");
		System.exit(EXIT_CODE_INVALID_USAGE);
    }
    
    //1.3.0
    private void parseAckModeArg(final int i, final String[] args) {
    	if ((i+1) >= args.length)
    		exitOnInvalidUsage();
    	
        try {
        	if (args[i+1].equalsIgnoreCase("auto_acknowledge"))
        		m_data_param_ack_mode = javax.jms.Session.AUTO_ACKNOWLEDGE;
        	else if (args[i+1].equalsIgnoreCase("auto"))
        		m_data_param_ack_mode = javax.jms.Session.AUTO_ACKNOWLEDGE;
        	
        	else if (args[i+1].equalsIgnoreCase("client_acknowledge"))
        		m_data_param_ack_mode = javax.jms.Session.CLIENT_ACKNOWLEDGE;
        	else if (args[i+1].equalsIgnoreCase("client"))
        		m_data_param_ack_mode = javax.jms.Session.CLIENT_ACKNOWLEDGE;

        	else if (args[i+1].equalsIgnoreCase("dups_ok_acknowledge"))
        		m_data_param_ack_mode = javax.jms.Session.DUPS_OK_ACKNOWLEDGE;
        	else if (args[i+1].equalsIgnoreCase("dups_ok"))
        		m_data_param_ack_mode = javax.jms.Session.DUPS_OK_ACKNOWLEDGE;

        	else if (args[i+1].equalsIgnoreCase("session_transacted"))
        		m_data_param_ack_mode = javax.jms.Session.SESSION_TRANSACTED;
        	else if (args[i+1].equalsIgnoreCase("transacted"))
        		m_data_param_ack_mode = javax.jms.Session.SESSION_TRANSACTED;
        	
        	else
        		m_data_param_ack_mode = Integer.parseInt(args[i+1]);
        }
        catch (final NumberFormatException ex) {
            System.err.println("Error: invalid value of -ackmode parameter");
    		exitOnInvalidUsage();
        }
    }

    public static String dumpBytes(final byte[] bs) {
        final StringBuilder ret = new StringBuilder(bs.length);
        for (int i = 0; i < bs.length; i++) {
            final String hex = Integer.toHexString(0x0100 + (bs[i] & 0x00FF)).substring(1);
            ret.append((hex.length() < 2 ? "0" : "") + hex);
        }
        return ret.toString();
    }
    
    /**
     * Transforms a string into a valid filename by replacing all filename invalid characters by underscore characters.
     * <p>
     * @since 1.2.0
     */
    public String toFilename(final String p_str) {
    	return p_str.replaceAll("[^A-Za-z0-9-_\\.\\(\\)\\[\\]]", "_");
    }

    /**
     * Blocks until one message is received on the topic name from the command line argument "test-trigger-topic".
     * This allows synchronised start of multiple message publishers/senders.
     * <p>
     * @throws InterruptedException In case the thread running this was interrupted.
     * @since 1.3.0
     */
    public void waitOnTriggerTopic() throws JMSException, NamingException, IOException, InterruptedException {
    	
    	if (null==getTestTriggerTopic())
    		return;
    	
		//*** WAIT FOR 1ST MSG ON TRIGGER TOPIC BEFORE STARTING PUBLISHING/SENDING LOOP
    	
    	final String[] i_args = new String[getArgs().length+6];
    	for(int i=0;i<getArgs().length ; i++)
    		i_args[i] = getArgs()[i];
    	
    	i_args[getArgs().length] = "-count";
    	i_args[getArgs().length+1] = "1";
    	i_args[getArgs().length+2] = "-test-threads";
    	i_args[getArgs().length+3] = "1";
    	i_args[getArgs().length+4] = "-topic";
    	i_args[getArgs().length+5] = getTestTriggerTopic();
    	
    	try {
    		EMSTopicListener i_trigger = new EMSTopicListener(i_args);

    		//*** BLOCKS UNTIL ONE MSG IS RECEIVED
        	i_trigger.start();

    	} 
    	catch (final JMSException | NamingException | IOException ex) {
    		logError("Failed to listen on trigger topic: " + getTestTriggerTopic());
    		logError(ex);
			throw ex;
    	}
    }
    
    /** @since 1.3.8 */
    public void onException(final JMSException exception) {
    	logError(exception);
    	
    	if (null!=m_ufo_queue_connection_factory)
    		try {
    			logWarning("Recovering queue connection...");
    			m_ufo_queue_connection_factory.recoverConnection(m_queue_connection);
    			logWarning("Recovering queue connection... Done.");
    		}
    		catch (final JMSException ex) {
    			logError("Failed to recover queue connection: " + ex.getMessage());
    		}

    	if (null!=m_ufo_topic_connection_factory)
    		try {
    			logWarning("Recovering topic connection...");
    			m_ufo_topic_connection_factory.recoverConnection(m_topic_connection);
    			logWarning("Recovering topic connection... Done.");
    		}
    		catch (final JMSException ex) {
    			logError("Failed to recover topic connection: " + ex.getMessage());
    		}
    }
    
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/
    
    /**
     * Returns the entire content of a text file into a string.
     * Newline characters at the end of each line are converted to &quot;\n&quot;.
     * <p>
     * @param p_file The file to read.
     * @return a string containing all lines of the file. If exceptions occur, the returned string is empty.
     * @since 1.2.0
     */
     public static String readTextFile(final File p_file) throws IOException {
    	 
    	 final StringBuilder i_text = new StringBuilder();

         BufferedReader i_reader = null;
         try {
        	 i_reader = new BufferedReader(new FileReader(p_file));
             String i_line ;
             while((i_line = i_reader.readLine())!=null) {
            	 i_text.append(i_line);
                 i_text.append('\n');
             }
             return i_text.toString();
         }
         finally {
        	 if (null!=i_reader)
        		try {
        			i_reader.close();
        		}
        	 	catch (final Exception ex) {
        	 		
        	 	}
            }
     }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/


