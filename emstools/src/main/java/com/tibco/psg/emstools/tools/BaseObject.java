
package com.tibco.psg.emstools.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A <code>BaseObject</code> is a basic object providing common features such as tracing.
 * <p>
 * @author Pierre Ayel
 * @since 1.4.0
 * @version 1.4.0
 */
public class BaseObject extends Object {

	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
	/** @since 1.3.0 */
	private static final String TRACE_DEBUG = " [DEBUG] ";
	private static final String TRACE_WARNING = " [WARN] ";
	private static final String TRACE_INFO = " [INFO] ";
	private static final String TRACE_ERROR = " [ERROR] ";
	
	/*************************************************************************/
	/***  RUNTIME DATA  ******************************************************/
	/*************************************************************************/
	
    /**
     * <p> 
     * @since 1.2.0 
     * @see #setDebug(boolean)
     * @see #isDebugEnabled()
     */
    private boolean m_flag_debug = false;

	/** 
	 * Indicates if we use log4j or not (default is <code>false</code>).
	 * <p>
	 * @since 1.3.0 
	 */
	private boolean m_flag_log4j = false;
    
    /** 
     * Log4j Logger
     * <p>
     * @since 1.3.0 
     */
	private final org.apache.log4j.Logger m_logger;
	
    /** @since 1.2.0 */
    private final SimpleDateFormat m_dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss (SSS'ms')");
    
    /** @since 1.4.0 */
    private Object m_data_shared_trace;

	/*************************************************************************/
	/***  CONSTRUCTORS  *****************************************************/
	/*************************************************************************/
    
	public BaseObject() {
		super();
		
		m_logger = org.apache.log4j.Logger.getLogger(System.getProperty(getClass().getName().concat(".logger"), "emstools.logger"));
	}

	/*************************************************************************/
	/***  ACCESSOR METHODS  **************************************************/
	/*************************************************************************/
	
	/**
	 * <p>
	 * @since 1.4.0
	 */
	public void setSharedTrace(final Object p_shared_trace) {
		m_data_shared_trace = p_shared_trace;
	}
	

	/**
	 * <p>
	 * @since 1.4.0
	 */
	public void setLog4j(final boolean p_set) {
		m_flag_log4j = p_set;
	}

	/**
	 * <p>
	 * @since 1.4.0
     * @see #isDebugEnabled()
	 */
	public void setDebug(final boolean p_set) {
		m_flag_debug = p_set;
	}
	
    /**
     * <p>
     * @since 1.2.0
     * @see #setDebug(boolean)
     */
    public boolean isDebugEnabled() {
    	return m_flag_debug;
    }
        
	/*************************************************************************/
	/***  METHODS  ***********************************************************/
	/*************************************************************************/
    
	/**
     * @since 1.3.0
     */
    public void log(final String p_text) {
    	if (m_flag_log4j) {
    		if (m_logger.isInfoEnabled())
    			m_logger.info(p_text);
    	}
    	else
    		System.out.println(p_text);
    }
    
    /**
     * @since 1.2.0
     */
    public synchronized void logDebug(final String p_text) {
    	if (isDebugEnabled()) {
    		//1.3.0
    		if (m_flag_log4j) {
    			if (m_logger.isDebugEnabled())
    				m_logger.debug(p_text);
    		}
    		else
    			trace(TRACE_DEBUG, p_text);
    	}
    }

    /**
     * @since 1.2.0
     */
    public synchronized void logWarning(final String p_text) {
    	//1.3.0
		if (m_flag_log4j) {
			m_logger.warn(p_text);
		}
		else
			trace(TRACE_WARNING, p_text);
    }

    /**
     * @since 1.2.0
     */
    public void logWarning(final Throwable ex, final String p_text) {
    	
    	if (null==ex) {
    		logWarning(p_text);
    		return;
    	}
    	
    	//1.3.0
    	if (m_flag_log4j) {
    		m_logger.warn(p_text, ex);
    	}
    	else {
	    	StringWriter i_sw = new StringWriter();
	    	PrintWriter i_pw = new PrintWriter(i_sw);
	    	ex.printStackTrace(i_pw);
	    	
	   		logWarning(p_text.trim() + ": " + ex.getClass().getSimpleName()+ ": " + ex.getMessage().trim() + ": " + i_sw.toString());
    	}
    }
    
    /**
     * @since 1.2.0
     */
    public synchronized void logInfo(final String p_text) {
    	
   		//1.3.0
		if (m_flag_log4j) {
			if (m_logger.isInfoEnabled())
				m_logger.info(p_text);
		}
		else
			trace(TRACE_INFO, p_text);
    }
    
    /**
     * @since 1.2.0
     */
    public synchronized void logError(final String p_text) {
   		//1.3.0
		if (m_flag_log4j)
			m_logger.error(p_text);
		else
			trace(TRACE_ERROR, p_text);
    }

    /**
     * @since 1.2.0
     */
    public void logError(final Throwable ex) {
    	
   		//1.3.0
		if (m_flag_log4j)
			m_logger.error(ex);
		else {
			StringWriter i_sw = new StringWriter();
			PrintWriter i_pw = new PrintWriter(i_sw);
			ex.printStackTrace(i_pw);
    	    	
			logError(ex.getClass().getSimpleName()+ ": " + ex.getMessage().trim() + ": " + i_sw.toString());
		}
    }
    
    /**
     * @since 1.3.0
     */
    private void trace(final String p_level, final String p_text) {
    	if (null!=m_data_shared_trace)
	    	synchronized (m_data_shared_trace) {
	   			System.out.println(m_dateformat.format(new Date())+p_level + "(" + Thread.currentThread().getName() + ") " + p_text);
	    	}
    	else {
    		System.out.println(m_dateformat.format(new Date())+p_level + "(" + Thread.currentThread().getName() + ") " + p_text);
    	}
    }
        
    /**
     * @since 1.3.0
     */
    @Override
    public String toString() {
    	return getClass().getSimpleName();
    }
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/
