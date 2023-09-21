# EMS Tools

A set of Java-based command line tools for TIBCO EMS:

- **EMSAdmin**:
  Exports data from one TIBCO EMS Server.
  
- **EMSAdmin-all**:
  Exports data from one TIBCO EMS Server.
  
- **EMSQueueBrowser**:
  Reads messages from one JMS Queue (messages stay in the JMS Queue). Messages can be writen to standard output or into text files.
  
- **EMSQueueReceiver**:
  Receives messages from one JMS Queue. Messages can be writen to standard output or into text files.
  
- **EMSQueueSender**:
  Publishes one or more messages into one JMS Queue. Messages can be generated randomly or read from input text files.
  
- **EMSTestQueueConnection**:
  Tests the connection to one TIBCO EMS Server.
  
- **EMSTestTopicConnection**:
  Tests the connection to one TIBCO EMS Server.
  
- **EMSTopicListener**:
  Receives messages from one JMS Topic. Messages can be writen to standard output or into text files.
  
- **EMSTopicPublisher**:
  Publishes one or more messages into one JMS Topic. Messages can be generated randomly or read from input text files.
              
All tools have extensive configuration options and can be used for performance 
testing of one TIBCO ActiveSpaces datagrid.

# How to Build

1) Clone this repository: `git clone https://gitlab.tibcopsg.net/Ayel/ems-tools`.

2) Go to the folder `emstools`.

3) Build with Maven: `mvn clean package`

The `target/emstools-<version>.zip` is the installation to use at customers, 
which contains shell scripts for both Windows and Linux as well as 
TIBCO EMS client libraries.

# How to Use

Please read the MSWorld document inside the doc subfolder.