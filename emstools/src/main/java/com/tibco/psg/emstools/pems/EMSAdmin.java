
package com.tibco.psg.emstools.pems;

import java.io.Serializable;

import com.tibco.psg.emstools.pems.filter.ConnectionInfoFilter;
import com.tibco.psg.emstools.pems.text.*;
import com.tibco.tibjms.admin.*;

/**
 * The <code>EMSAdmin</code> is the main class of the EMSAdmin command line tool.
 * <p>
 * Version 1.3.5:
 * <ul>
 * <li>Added queues and topics print commands.
 * </ul>
 * Version 1.3.7:
 * <ul>
 * <li>Added -header, -noheader, -timestamp and -notimestamp command line options.
 * </ul>
 * @author Pierre Ayel
 * @version 1.3.7
 */
public class EMSAdmin extends Object implements Serializable {

	/*************************************************************************/
	/***  DEFINITIONS  *******************************************************/
	/*************************************************************************/
	
    /** Unique ID for serialisation. */
	private static final long serialVersionUID = -2203186215269305543L;
	
	/*************************************************************************/
	/***  RUNTIME DATA  ******************************************************/
	/*************************************************************************/

	/**
	 * The TIBCO EMS Admin API main object to administer the EMS server where EMSAdmin tool is connected to.
	 */
	protected TibjmsAdmin m_admin;
	
	/**
	 * @since 1.3.7
	 */
	private boolean m_flag_header = true;
	
	/**
	 * @since 1.3.7
	 */
	private boolean m_flag_timestamp = true;
	
	/*************************************************************************/
	/***  CONSTRUCTORS  ******************************************************/
	/*************************************************************************/

	public EMSAdmin(String p_server, String p_userName, String p_password) throws TibjmsAdminException {
		super();
		m_admin = new TibjmsAdmin(p_server, p_userName, p_password);
	}

	/*************************************************************************/
	/***  ACCESSOR METHODS  **************************************************/
	/*************************************************************************/
	
	/**
	 * @return the m_flag_header
	 * @since 1.3.7
	 */
	public boolean printHeader() {
		return m_flag_header;
	}

	/**
	 * @param m_flag_header the m_flag_header to set
	 * @since 1.3.7
	 */
	public void setPrintHeader(boolean m_flag_header) {
		this.m_flag_header = m_flag_header;
	}

	/**
	 * @return the m_flag_timestamp
	 * @since 1.3.7
	 */
	public boolean printTimestamp() {
		return m_flag_timestamp;
	}

	/**
	 * @param m_flag_timestamp the m_flag_timestamp to set
	 * @since 1.3.7
	 */
	public void setPrintTimestamp(boolean m_flag_timestamp) {
		this.m_flag_timestamp = m_flag_timestamp;
	}

	/*************************************************************************/
	/***  METHODS  ***********************************************************/
	/*************************************************************************/

	public void getACLEntries() throws TibjmsAdminException {
		ACLEntryFormat.printCSV(m_admin.getACLEntries(), printHeader(), printTimestamp());
	}

	public void getGroupACLEntries(String p_groupName) throws TibjmsAdminException {
		ACLEntryFormat.printCSV(m_admin.getGroupACLEntries(p_groupName), printHeader(), printTimestamp());
	}

	public void getUserACLEntries(String p_userName) throws TibjmsAdminException {
		ACLEntryFormat.printCSV(m_admin.getUserACLEntries(p_userName), printHeader(), printTimestamp());
	}

	public void getQueueACLEntries(String p_queueName) throws TibjmsAdminException {
		ACLEntryFormat.printCSV(m_admin.getQueueACLEntries(p_queueName), printHeader(), printTimestamp());
	}

	public void getTopicACLEntries(String p_topicName) throws TibjmsAdminException {
		ACLEntryFormat.printCSV(m_admin.getTopicACLEntries(p_topicName), printHeader(), printTimestamp());
	}
	
	public void getConnectionFactories() throws TibjmsAdminException {
		ConnectionFactoryInfoFormat.printCSV(m_admin.getConnectionFactories(), printHeader(), printTimestamp());
	}
	
	/**
	 * @since 0.5
	 */
	public void getConnection(String p_connectionID) throws TibjmsAdminException {
		long i_connectionID = new Long(p_connectionID).longValue();
		
		ConnectionInfoFormat.printCSV(
			ConnectionInfoFilter.filterByConnectionID(m_admin.getConnections(), i_connectionID), printHeader(), printTimestamp());
		ConnectionInfoFormat.printCSV(
			ConnectionInfoFilter.filterByConnectionID(m_admin.getSystemConnections(), i_connectionID), false, false);
	}

	/**
	 * @since 0.5
	 */
	public void getConnectionByUsername(String p_username) throws TibjmsAdminException {
		ConnectionInfoFormat.printCSV(
			ConnectionInfoFilter.filterByUsername(m_admin.getConnections(), p_username), printHeader(), printTimestamp());
		ConnectionInfoFormat.printCSV(
			ConnectionInfoFilter.filterByUsername(m_admin.getSystemConnections(), p_username), false, false);
	}

	/**
	 * @since 0.5
	 */
	public void getConnectionByHostname(String p_hostname) throws TibjmsAdminException {
		ConnectionInfoFormat.printCSV(
			ConnectionInfoFilter.filterByHostname(m_admin.getConnections(), p_hostname), printHeader(), printTimestamp());
		ConnectionInfoFormat.printCSV(
			ConnectionInfoFilter.filterByHostname(m_admin.getSystemConnections(), p_hostname), false, false);
	}
	
	public void getConnections() throws TibjmsAdminException {
		ConnectionInfoFormat.printCSV(m_admin.getConnections(), printHeader(), printTimestamp());
		ConnectionInfoFormat.printCSV(m_admin.getSystemConnections(), false, false);
	}
	
	public void getConsumers() throws TibjmsAdminException {
		if (printHeader())
			System.out.println(ConnectionInfoFormat.CSVHeader(printTimestamp())+","+ConsumerInfoFormat.CSVHeader(false));
		
		String i_timestamp = printTimestamp()? StringFormat.timestamp() : null;
		ConnectionInfo i_connections[] = m_admin.getConnections();
		
		for(int i=0;i<i_connections.length;i++) {
			ConsumerInfo i_consumers[] = m_admin.getConsumersStatistics(new Long(i_connections[i].getID()), null, null);
			for(int j=0;j<i_consumers.length;j++) {
				System.out.print(ConnectionInfoFormat.toCSV(i_connections[i], i_timestamp));
				System.out.print(",");
				System.out.println(ConsumerInfoFormat.toCSV(i_consumers[j], null));
			}
		}
	}

	public void getDurables() throws TibjmsAdminException {
		DurableInfoFormat.printCSV(m_admin.getDurables(), printHeader(), printTimestamp());
	}

	@SuppressWarnings("deprecation")
	public void getDurables(String p_topicName) throws TibjmsAdminException {
		DurableInfoFormat.printCSV(m_admin.getDurables(p_topicName), printHeader(), printTimestamp());
	}
	
	public void getDurables(String p_name, String p_cid) throws TibjmsAdminException {
		DurableInfoFormat.printCSV(new DurableInfo[]{ m_admin.getDurable(p_name, p_cid) }, printHeader(), printTimestamp());
	}
	
	public void getProducers() throws TibjmsAdminException {
		if (printHeader())
			System.out.println(ConnectionInfoFormat.CSVHeader(printTimestamp())+","+ProducerInfoFormat.CSVHeader(false));
		
		String i_timestamp = printTimestamp()? StringFormat.timestamp() : null;
		ConnectionInfo i_connections[] = m_admin.getConnections();
		
		for(int i=0;i<i_connections.length;i++) {
			ProducerInfo i_producers[] = m_admin.getProducersStatistics(new Long(i_connections[i].getID()), null, null);
			for(int j=0;j<i_producers.length;j++) {
				System.out.print(ConnectionInfoFormat.toCSV(i_connections[i], i_timestamp));
				System.out.print(",");
				System.out.println(ProducerInfoFormat.toCSV(i_producers[j], null));
			}
		}
	}
	
	public void getBridges() throws TibjmsAdminException {
		BridgeInfoFormat.printCSV(m_admin.getBridges(), printHeader(), printTimestamp());
	}

	public void getGroups() throws TibjmsAdminException {
		GroupInfoFormat.printCSV(m_admin.getGroups(), printHeader(), printTimestamp());
	}

	/** @since 1.3.5 */
	public void getQueues() throws TibjmsAdminException {
		QueueInfoFormat.printCSV(m_admin.getQueues(), printHeader(), printTimestamp());
	}

	/** @since 1.3.5 */
	public void getTopics() throws TibjmsAdminException {
		TopicInfoFormat.printCSV(m_admin.getTopics(), printHeader(), printTimestamp());
	}
	
	public void getRoutes() throws TibjmsAdminException {
		RouteInfoFormat.printCSV(m_admin.getRoutes(), printHeader(), printTimestamp());
	}
	
	public void getStorage() throws TibjmsAdminException {
		if (printHeader())
			System.out.println(StoreInfoFormat.CSVHeader(printTimestamp()));
		
		String i_timestamp = printTimestamp()? StringFormat.timestamp() : null;
		
		for(String i_name : m_admin.getStores()) {
			StoreInfo i_store = m_admin.getStoreInfo(i_name);
			System.out.println(StoreInfoFormat.toCSV(i_name, i_store, i_timestamp));
		}
	}

	public void getFileStores() throws TibjmsAdminException {
		if (printHeader())
			System.out.println(FileStoreInfoFormat.CSVHeader(printTimestamp()));
		
		String i_timestamp = printTimestamp()? StringFormat.timestamp() : null;
		
		for(String i_name : m_admin.getStores()) {
			StoreInfo i_store = m_admin.getStoreInfo(i_name);
			if (i_store instanceof FileStoreInfo)
				System.out.println(FileStoreInfoFormat.toCSV(i_name, (FileStoreInfo)i_store, i_timestamp));
		}
	}
	
	public void getUsers() throws TibjmsAdminException {
		UserInfoFormat.printCSV(m_admin.getUsers(), printHeader(), printTimestamp());
	}
	
	public void close() throws TibjmsAdminException {
		if (null!=m_admin)
			m_admin.close();
	}
	
	/**
	 * @since 0.5
	 */
	public void destroyConnection(String p_connectionID) throws TibjmsAdminException {
		m_admin.destroyConnection(new Long(p_connectionID).longValue());
		System.out.println("Destroyed connection "+p_connectionID);
	}
	
	/**
	 * @since 0.5
	 */
	public void destroyConnections(ConnectionInfo p_connections[]) throws TibjmsAdminException {
		if (null!=p_connections)
			for(int i=0;i<p_connections.length;i++)
				destroyConnection(""+p_connections[i].getID());
	}

	/**
	 * @since 0.5
	 */
	public void destroyConnectionByUsername(String p_username) throws TibjmsAdminException {
		destroyConnections(ConnectionInfoFilter.filterByUsername(m_admin.getConnections(), p_username));
		destroyConnections(ConnectionInfoFilter.filterByUsername(m_admin.getSystemConnections(), p_username));
	}

	/**
	 * @since 0.5
	 */
	public void destroyConnectionByHostname(String p_hostname) throws TibjmsAdminException {
		destroyConnections(ConnectionInfoFilter.filterByHostname(m_admin.getConnections(), p_hostname));
		destroyConnections(ConnectionInfoFilter.filterByHostname(m_admin.getSystemConnections(), p_hostname));
	}
	
	/*************************************************************************/
	/***  STATIC METHODS  ****************************************************/
	/*************************************************************************/
	
	/**
	 * Prints out the tool command line usage and exits the JVM with a -2 return code. 
	 */
	public static void usage() {
		System.err.println("\nUsage: EMSAdmin [-server <url>] [-user <login>] [-password <password>] [-header] [-noheader] [-timestamp] [-notimestamp]");
		
		System.err.println("-destroyConnection -byConnectionID <connection ID>\n");
		System.err.println("-destroyConnection -byUser <user>\n");
		System.err.println("-destroyConnection -byHostname <hostname>\n");
		System.err.println("-print acl\n");
		System.err.println("-print acl -byUser <user>\n");
		System.err.println("-print acl -byGroup <group>\n");
		System.err.println("-print acl -byQueue <queue>\n");
		System.err.println("-print acl -byTopic <topic>\n");
		System.err.println("-print bridges\n");
		System.err.println("-print connectionFactories\n");
		System.err.println("-print connections\n");
		System.err.println("-print connections -byConnectionID <connection ID>\n");
		System.err.println("-print connections -byUser <user>\n");
		System.err.println("-print connections -byhostname <hostname>\n");
		System.err.println("-print consumers\n");
		System.err.println("-print durables\n");
		System.err.println("-print durables -byTopic <topic>\n");
		System.err.println("-print durables -byName <durable name> [-byClientID <client ID>]\n");
		System.err.println("-print factories|fileStores|groups|producers|queues|routes|storage|topics|users\n");
		
		System.err.println("\n-noheader: do not print CSV headers\n");
		System.err.println("\n-timestamp: prints a timestamp column as first column\n");
		
		System.exit(-2);
	}
	
	public static void main(String args[]) {
		try {
			String i_server = null;
			String i_userName = null;
			String i_password = null;
			String i_print = null;
			//0.5
			String i_destroyConnection = null;
			
			String i_byClientID = null;
			String i_byUser = null;
			String i_byGroup = null;
			String i_byName = null;
			String i_byTopic = null;
			String i_byQueue = null;
			
			//0.5
			String i_byConnectionID = null;
			String i_byHostname = null;
			
			
			//if (args.length<8)
			//	usage();
			
			//1.3.7
			boolean i_header = true;
			boolean i_timestamp = false;
	
			for(int i=0;i<args.length;i++) {
				//1.3.7
				if (args[i].equalsIgnoreCase("-noheader")) {
					i_header = false;
					continue;
				}
				if (args[i].equalsIgnoreCase("-header")) {
					i_header = true;
					continue;
				}
				if (args[i].equalsIgnoreCase("-notimestamp")) {
					i_timestamp = false;
					continue;
				}
				if (args[i].equalsIgnoreCase("-timestamp")) {
					i_timestamp = true;
					continue;
				}
				
				if (args[i].equalsIgnoreCase("-server")) {
					if (i==args.length-1)
						usage();
					
					i_server = args[i+1];
					i++;
					continue;
				}
				
				if (args[i].equalsIgnoreCase("-user")) {
					if (i==args.length-1)
						usage();
					
					i_userName = args[i+1];
					i++;
					continue;
				}
	
				if (args[i].equalsIgnoreCase("-password")) {
					if (i==args.length-1)
						usage();
					
					i_password = args[i+1];
					i++;
					continue;
				}

				if (args[i].equalsIgnoreCase("-print")) {
					if (i==args.length-1)
						usage();
					
					i_print = args[i+1];
					i++;
					continue;
				}

				if (args[i].equalsIgnoreCase("-byUser")) {
					if (i==args.length-1)
						usage();
					
					i_byUser = args[i+1];
					i++;
					continue;
				}

				if (args[i].equalsIgnoreCase("-byGroup")) {
					if (i==args.length-1)
						usage();
					
					i_byGroup = args[i+1];
					i++;
					continue;
				}
				
				if (args[i].equalsIgnoreCase("-byTopic")) {
					if (i==args.length-1)
						usage();
					
					i_byTopic = args[i+1];
					i++;
					continue;
				}
				
				if (args[i].equalsIgnoreCase("-byQueue")) {
					if (i==args.length-1)
						usage();
					
					i_byQueue = args[i+1];
					i++;
					continue;
				}

				if (args[i].equalsIgnoreCase("-byName")) {
					if (i==args.length-1)
						usage();
					
					i_byName = args[i+1];
					i++;
					continue;
				}
				
				if (args[i].equalsIgnoreCase("-byClientID")) {
					if (i==args.length-1)
						usage();
					
					i_byClientID = args[i+1];
					i++;
					continue;
				}
				
				if (args[i].equalsIgnoreCase("-byConnectionID")) {
					if (i==args.length-1)
						usage();
					
					i_byConnectionID = args[i+1];
					i++;
					continue;
				}

				if (args[i].equalsIgnoreCase("-byHostname")) {
					if (i==args.length-1)
						usage();
					
					i_byHostname = args[i+1];
					i++;
					continue;
				}
				
				if (args[i].equalsIgnoreCase("-destroyConnection")) {
					i_destroyConnection = args[i];
					continue;
				}
			}
			
			if (null!=i_destroyConnection) {
				if (null!=i_print) usage();
				if (null==i_byConnectionID && null==i_byUser && null==i_byHostname) usage();
			}
			else if (null!=i_print) {
				//if (null!=i_destroyConnection) usage();
			}
			else 
				usage();
			
			EMSAdmin i_admin = new EMSAdmin(i_server, i_userName, i_password);
			
			//1.3.7
			i_admin.setPrintHeader(i_header);
			i_admin.setPrintTimestamp(i_timestamp);

			if (null!=i_destroyConnection) {
				if (null!=i_byConnectionID)
					i_admin.destroyConnection(i_byConnectionID);
				if (null!=i_byUser)
					i_admin.destroyConnectionByUsername(i_byUser);
				if (null!=i_byHostname)
					i_admin.destroyConnectionByHostname(i_byHostname);
			}
			else {	
				if (i_print.equalsIgnoreCase("acl")) {
					if (null!=i_byUser)
						i_admin.getUserACLEntries(i_byUser);
					else if (null!=i_byGroup)
						i_admin.getGroupACLEntries(i_byGroup);
					else if (null!=i_byQueue)
						i_admin.getQueueACLEntries(i_byQueue);
					else if (null!=i_byTopic)
						i_admin.getTopicACLEntries(i_byTopic);
					else
						i_admin.getACLEntries();
				}
				if (i_print.equalsIgnoreCase("bridges"))
					i_admin.getBridges();
				if (i_print.equalsIgnoreCase("connectionFactories"))
					i_admin.getConnectionFactories();
				if (i_print.equalsIgnoreCase("connections")) {
					if (null!=i_byConnectionID)
						i_admin.getConnection(i_byConnectionID);
					else if (null!=i_byUser)
						i_admin.getConnectionByUsername(i_byUser);
					else if (null!=i_byHostname)
						i_admin.getConnectionByHostname(i_byHostname);
					else
						i_admin.getConnections();
				}
				if (i_print.equalsIgnoreCase("consumers"))
					i_admin.getConsumers();
				if (i_print.equalsIgnoreCase("durables")) {
					if (null!=i_byTopic)
						i_admin.getDurables(i_byTopic);
					else if (null!=i_byName || null!=i_byClientID)
						i_admin.getDurables(i_byName, i_byClientID);
					else
						i_admin.getDurables();
				}
				if (i_print.equalsIgnoreCase("factories"))
					i_admin.getConnectionFactories();
				if (i_print.equalsIgnoreCase("groups"))
					i_admin.getGroups();
				if (i_print.equalsIgnoreCase("producers"))
					i_admin.getProducers();
				if (i_print.equalsIgnoreCase("queues"))
					i_admin.getQueues();
				if (i_print.equalsIgnoreCase("routes"))
					i_admin.getRoutes();
				if (i_print.equalsIgnoreCase("storage"))
					i_admin.getStorage();
				if (i_print.equalsIgnoreCase("topics"))
					i_admin.getTopics();
				if (i_print.equalsIgnoreCase("fileStores"))
					i_admin.getFileStores();
				if (i_print.equalsIgnoreCase("users"))
					i_admin.getUsers();
			}	
			i_admin.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

/*****************************************************************************/
/***  END OF FILE  ***********************************************************/
/*****************************************************************************/

