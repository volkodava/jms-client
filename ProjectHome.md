<h4>Description</h4>
<p><b>JMS client</b> application is designed to work with a <b>JMS providers</b> that support of <i>JMS 1.1 API</i>.<br>
The application uses <i>JMS Message Producer</i> and <i>Consumer</i> to send and receive message.</p>

<p>The client is able to create a <i>Message</i> object with different <i>message headers</i>, <i>message properties</i>,<br>
<i>message data</i> (called the <i>payload</i> or <i>message body</i>) and in various types<br>
that are defined by the <i>payload</i> they carry. The payload<br>
itself might be very structured, as with <i>StreamMessage</i> and <i>BytesMessage</i> objects, or fairly<br>
unstructured, as with <i>TextMessage</i>, <i>ObjectMessage</i>, and <i>MapMessage</i> types.</p>

<p>To obtain access to the <b>JMS provider</b> a client should use <i>JNDI</i>. The JMS client looking up in the <i>JNDI</i> a<br>
<i>ConnectionFactory</i>. The <i>ConnectionFactory</i> is used to create <i>JMS connections</i>, which<br>
can then be used for sending and receiving messages. For example, once you have a <i>Connection</i>,<br>
you can create a <i>Session</i>. Once you have a <i>Session</i>, you can create a <i>Message</i>, <i>Message<br>
Producer</i>, and <i>Message Receiver</i>.</p>

<p><i>Destination</i> objects, which represent virtual channels (<i>topics</i> and <i>queues</i>) in JMS,<br>
are created via <i>Session</i> and are used by the JMS client.</p>

<p>The directory service (<i>JNDI</i>) can be configured by the system administrator<br>
to provide JMS-administered objects so that the JMS clients don’t need to use<br>
proprietary code to access a <b>JMS provider</b>.</p>

<p>The <i>JNDI</i> properties contains the <i>JNDI</i> connection information for the <b>JMS<br>
provider</b>. A client should set the <i>initial context factory class</i>, <i>provider URL</i>, <i>username</i>,<br>
and <i>password</i> needed to connect to the <b>JMS server</b>. Each vendor will have a different<br>
<i>context factory class</i> and <i>URL name</i> for connecting to the server. Client should<br>
consult the documentation of a specific <b>JMS provider</b> to obtain these values.</p>

<h4>Requirements</h4>
<p>In order to be able to start the <b>JMS client</b> application, we should have:</p>
<ol>
<li><a href='http://www.oracle.com/technetwork/java/javase/downloads/index.html'>Java SDK 6+</a>.</li>
<li><a href='http://maven.apache.org/download.html'>Maven 3+</a>.</li>
</ol>

<h4>How to use</h4>
<p>To start the <b>JMS client</b> application you should execute <i>start.sh</i> or <i>start.bat</i> script from the application directory.</p>
<p>When the application has started, go to the <a href='http://localhost:8080/jmsClient/'><a href='http://localhost:8080/jmsClient/'>http://localhost:8080/jmsClient/</a></a>. Use the toolbar menu to connect to<br>
a <b>JMS provider</b> (<b>JMS broker</b>), that support of <i>JMS 1.1 API</i>. The <b>JMS provider</b> <i>initial context factory class</i>, <i>provider URL</i>, <i>username</i>,<br>
and <i>password</i> can be provided using the GUI. Once the client is connected, he can send jms messages to any <i>Topic</i> or <i>Queue</i> destination. Once the <i>Producer</i> send messages to a destination of the given <b>JMS provider</b>, the client can create a consumer for the same destination which receives the messages and display them in a <b>Messages Table</b> after refresh button is clicked. For the <i>Queue</i> destination client can simply receive message either by using <i>transacted session</i> or by using <i>non-transacted session</i>. For <i>Topic</i> type destination the client can create a <i>durable Topic subscriber</i>. For a given destination (<i>Topic</i> or <i>Queue</i>) client can set a <i>Message filtering</i> - <i>selector</i>. Client can receive message using a destination subscriber created during the first receive message button click, but if later client will change the destination selector or the transaction type, then a new destination selector will be created. Client can see the details of the message received, by using the <b>Message Details</b> button.</p>

<p>The application is starting not connected to any JMS provider.<br>
To connect to a JMS provider, select the <i>Connect ...</i> menu item.<br>
A dialog will appear to provide JMS provider <i>Initial Context Factory</i>, <i>Connection Factory Name</i>, <i>Connection URL</i>, <i>username</i>, <i>password</i>, etc.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/connectionDialog.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/connectionDialog.png)

<p>To add a new <i>Initial Context Factory</i> class to the autocomplete text field at the <i>Connect ...</i> dialog form, you should add it to the xml configuration file: <i>${project.basedir}/src/main/resources/applicationConfig.xml</i></p>

<p>To disconnect from a connected JMS provider select the <i>Disconnect</i> menu item.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/disconnectDialog.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/disconnectDialog.png)

<p>Once the application is connected, client can create and send message using<br>
a message producer. To create a producer select the <i>Send Message ...</i> menu item. The Send Message dialog box will come up, where <i>destination name</i>, <i>type</i> etc. can be specified while sending message.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/sendMessageDialog.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/sendMessageDialog.png)

<p>To receive message select the <i>Receive Message ...</i> menu item. The receive message dialog box will come up where <i>destination name</i>, <i>message consumer type</i> and<br>
<i>message selector</i> can be specified.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/receiverDialog.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/receiverDialog.png)

<p>To stop message receiver select the <i>Stop Message Receiver</i> menu item.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/stopReceiver.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/stopReceiver.png)

<p>To refresh the <i>Received Message Table</i> select the <i>Refresh Messages Table</i> menu item.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/refreshMessagesTable.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/refreshMessagesTable.png)

<p>To autorefresh the <i>Received Message Table</i> select the <i>Start auto updater</i> menu item.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/startAutoupdater.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/startAutoupdater.png)

<p>To stop autorefresh the <i>Received Message Table</i> select the <i>Stop auto updater</i> menu item.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/stopAutoupdater.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/stopAutoupdater.png)

<p>The received message are displayed on the <i>Received Message Table</i>. To see the<br>
details of a message click on <i>Show Details</i> button.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/showMessageDetails.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/showMessageDetails.png)
<br />
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/messageDetailsDialog.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/messageDetailsDialog.png)

<p>To clear existing messages in the <i>Received Message Table</i> select the <i>Clear Messages</i> menu item.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/clearReceivedMessages.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/clearReceivedMessages.png)

<p>The status bar at the bottom shows <i>Notification And Connection Log</i>.</p>
![http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/applicationLog.png](http://i1234.photobucket.com/albums/ff414/volkodavav/JmsListener/applicationLog.png)