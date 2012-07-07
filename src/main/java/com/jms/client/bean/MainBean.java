package com.jms.client.bean;

import com.jms.client.controller.MessageController;
import com.jms.client.converter.MessageConverterHelper;
import com.jms.client.converter.DateConverterHelper;
import com.jms.client.core.UiLogLevel;
import com.jms.client.entity.*;
import com.jms.client.entity.Connection;
import com.jms.client.entity.DeliveryMode;
import com.jms.client.entity.Message;
import com.jms.client.entity.Property.Type;
import com.jms.client.util.JmsUtility;
import com.jms.client.impl.JmsConnectionFactoryRegistry;
import com.jms.client.util.ApplicationConfigurationHelper;
import com.jms.client.util.FacesUtil;
import com.jms.client.util.ResourceUtil;
import com.jms.client.core.ServiceLocator;
import java.io.Serializable;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.jms.*;
import org.apache.log4j.Logger;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

/**
 * Main managed bean class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
@ManagedBean(name = "mainBean")
@SessionScoped
public class MainBean extends BaseBean implements Serializable {

    protected static final Logger LOGGER = Logger.getLogger(MainBean.class);
    private static final long serialVersionUID = 1L;
    public static final String BEAN_NAME = "mainBean";
    private transient MessageController messageController = ServiceLocator.getObject(MessageController.class);
    private static List<String> recentInitialContextFactories;
    private Connection connection = new Connection();
    private List<Message> messages;
    private Message selectedMessage = new Message(Message.Status.RECEIVED);
    private Message messageToSend = new Message(Message.Status.SEND);
    private Receiver receiver = new Receiver(Status.NOT_CONNECTED);
    private Property selectedMapMessage = new Property();
    private HeaderProperty selectedHeader = new HeaderProperty();
    private Property selectedProperty = new Property();
    private Property selectedConnectionAdvancedProperty = new Property(Property.Type.STRING);
    private boolean autoUpdateMessages = false;
    private GuiState guiState = GuiState.DISCONNECTED;
    // XXX: Jms settings
    private javax.jms.ConnectionFactory jmsConnectionFactory;
    private javax.jms.Connection jmsConnection;
    private javax.jms.Session jmsProducerSession;
    private javax.jms.Session jmsConsumerSession;
    private javax.jms.MessageProducer jmsMessageProducer;
    private javax.jms.MessageConsumer jmsMessageConsumer;
    private javax.jms.Destination jmsDestination;
    // XXX: caches for jms fields
    private Map<String, javax.jms.Session> cachedSessionMap = new HashMap<String, javax.jms.Session>();
    private Map<String, javax.jms.MessageProducer> cachedProducerMap = new HashMap<String, javax.jms.MessageProducer>();

    static {
        // get recent initial context factory classes from the application config
        recentInitialContextFactories = ApplicationConfigurationHelper.getRecentInitialContextFactories();
    }

    /**
     * UI state
     */
    public static enum GuiState {

        CONNECTED_RECEIVER_STOPPED, CONNECTED_RECEIVER_STARTED, DISCONNECTED, ERROR;
    }

    @PostConstruct
    protected void postConstruct() {
        reinitConnection();

        messages = messageController.findEntities(connection.getClient().getClientId());
    }

    @PreDestroy
    protected void preDestroy() {
        if (messages != null) {
            messages.clear();
        }
        messages = null;
        selectedMessage = null;
    }

    public Status[] getStatuses() {
        return Status.values();
    }

    public AcknowledgeMode[] getAcknowledgeModes() {
        return AcknowledgeMode.values();
    }

    public DeliveryMode[] getDeliveryModes() {
        return DeliveryMode.values();
    }

    public DestinationType[] getDestinationTypes() {
        return DestinationType.values();
    }

    public Property.Type[] getPropertyTypes() {
        return Property.Type.values();
    }

    public HeaderProperty.Type[] getHeaderPropertyTypes() {
        return HeaderProperty.Type.values();
    }

    public MessageType[] getMessageTypes() {
        return MessageType.values();
    }

    public TransactionType[] getTransactionTypes() {
        return TransactionType.values();
    }

    /**
     * Get Message Type filter options
     *
     * @return message type filter options
     */
    public SelectItem[] getMessageTypeFilterOptions() {
        MessageType[] data = MessageType.values();
        SelectItem[] options = new SelectItem[data.length + 1];

        String selectKey = ResourceUtil.getI18nMessage("button.select");
        options[0] = new SelectItem("", selectKey);
        for (int i = 0; i < data.length; i++) {
            MessageType key = data[i];
            String value = ResourceUtil.getI18nMessage(key.name());
            options[i + 1] = new SelectItem(key, value);
        }

        return options;
    }

    /**
     * Get Destination Type filter options
     *
     * @return destination type filter options
     */
    public SelectItem[] getDestinationTypeFilterOptions() {
        DestinationType[] data = DestinationType.values();
        SelectItem[] options = new SelectItem[data.length + 1];

        String selectKey = ResourceUtil.getI18nMessage("button.select");
        options[0] = new SelectItem("", selectKey);
        for (int i = 0; i < data.length; i++) {
            DestinationType key = data[i];
            String value = ResourceUtil.getI18nMessage(key.name());
            options[i + 1] = new SelectItem(key, value);
        }

        return options;
    }

    /**
     * UI input autocomplete for initial context factory
     *
     * @param query query from the input field
     * @return matched initial context factories
     */
    public List<String> completeInitialContextFactory(String query) {
        List<String> results = new ArrayList<String>();

        for (String initialContextFactory : recentInitialContextFactories) {
            if (initialContextFactory.startsWith(query)) {
                results.add(initialContextFactory);
            }
        }

        return results;
    }

    public boolean isShowConnect() {
        return (guiState == GuiState.DISCONNECTED || guiState == GuiState.ERROR);
    }

    public boolean isShowDisconnect() {
        return (guiState == GuiState.CONNECTED_RECEIVER_STARTED || guiState == GuiState.CONNECTED_RECEIVER_STOPPED);
    }

    public boolean isShowReceive() {
        return (guiState == GuiState.CONNECTED_RECEIVER_STOPPED);
    }

    public boolean isShowReceiveStop() {
        return (guiState == GuiState.CONNECTED_RECEIVER_STARTED);
    }

    public boolean isShowSend() {
        return (guiState == GuiState.CONNECTED_RECEIVER_STARTED || guiState == GuiState.CONNECTED_RECEIVER_STOPPED);
    }

    /**
     * Update UI message table
     */
    public void updateMessagesTable() {
        messages = messageController.findEntities(connection.getClient().getClientId());
    }

    /**
     * Update UI message table by actionListener
     *
     * @param event action event
     */
    public void updateMessagesTable(ActionEvent event) {
        updateMessagesTable();
    }

    public void handleChange() {
        addMessage("handleChange called", UiLogLevel.DEBUG);
    }

    public void handleChange(AjaxBehaviorEvent event) {
        addMessage("handleChange called", UiLogLevel.DEBUG);
    }

    public void onRowSelect(SelectEvent event) {
        Message selected = ((Message) event.getObject());

        addMessage("onRowSelect called. Selected: " + selected, UiLogLevel.DEBUG);
    }

    /**
     * Reinit Map Message with empty one
     *
     * @return page to redirect/forward
     */
    public String reinitMapMessage() {
        selectedMapMessage = new Property();

        return null;
    }

    /**
     * Reinit Message headers with empty one
     *
     * @return page to redirect/forward
     */
    public String reinitMessageHeaders() {
        selectedHeader = new HeaderProperty();

        return null;
    }

    /**
     * Reinit Message properties with empty one
     *
     * @return page to redirect/forward
     */
    public String reinitMessageProperties() {
        selectedProperty = new Property();

        return null;
    }

    /**
     * Reinit Connection properties with empty one
     *
     * @return page to redirect/forward
     */
    public String reinitConnectionProperties() {
        selectedConnectionAdvancedProperty = new Property(Property.Type.STRING);

        return null;
    }

    /**
     * Reinit Send Message dialog with empty one
     *
     * @return page to redirect/forward
     */
    public String reinitSendMessage() {
        messageToSend = new Message(Message.Status.SEND);
        messageToSend.setClientId(connection.getClient().getClientId());
        reinitMapMessage();
        reinitMessageHeaders();
        reinitMessageProperties();

        return null;
    }

    /**
     * Reinit Receiver dialog with empty one
     *
     * @return page to redirect/forward
     */
    public String reinitReceiver() {
        receiver = new Receiver(Status.NOT_CONNECTED);

        return null;
    }

    /**
     * Reinit Connection properties with empty one
     *
     * @return page to redirect/forward
     */
    public String reinitConnection() {
        connection = new Connection();
        String clientId = FacesUtil.getHttpSession().getId();
        Client client = new Client(clientId);
        connection.setClient(client);

        reinitConnectionProperties();

        return null;
    }

    public void handleCloseCreateConnectionAdvancedProperty(CloseEvent event) {
        addMessage("handleCloseCreateConnectionAdvancedProperty called", UiLogLevel.DEBUG);
    }

    public void handleCloseCreateMapMessage(CloseEvent event) {
        addMessage("handleCloseCreateMapMessage called", UiLogLevel.DEBUG);
    }

    public void handleCloseCreateMessageHeaders(CloseEvent event) {
        addMessage("handleCloseCreateMessageHeaders called", UiLogLevel.DEBUG);
    }

    public void handleCloseCreateMessageProperties(CloseEvent event) {
        addMessage("handleCloseCreateMessageProperties called", UiLogLevel.DEBUG);
    }

    public void handleCloseConnectionDialog(CloseEvent event) {
        addMessage("handleCloseConnectionDialog called", UiLogLevel.DEBUG);
    }

    public void handleCloseDisconnectionDialog(CloseEvent event) {
        addMessage("handleCloseDisconnectionDialog called", UiLogLevel.DEBUG);
    }

    public void handleCloseSendMessageDialog(CloseEvent event) {
        addMessage("handleCloseSendMessageDialog called", UiLogLevel.DEBUG);
    }

    public void handleCloseReceiveMessageDialog(CloseEvent event) {
        addMessage("handleCloseReceiveMessageDialog called", UiLogLevel.DEBUG);
    }

    public void showConnectionDialog(ActionEvent event) {
        addMessage("showConnectionDialog called", UiLogLevel.DEBUG);
    }

    public void showDisconnectionDialog(ActionEvent event) {
        addMessage("showDisconnectionDialog called", UiLogLevel.DEBUG);
    }

    public void showSendMessageDialog(ActionEvent event) {
        addMessage("showSendMessageDialog called", UiLogLevel.DEBUG);
    }

    public void showReceiveMessageDialog(ActionEvent event) {
        addMessage("showReceiveMessageDialog called", UiLogLevel.DEBUG);
    }

    /**
     * Fire connect from the UI
     *
     * @param event action event
     */
    public void connect(ActionEvent event) {
        addMessage("connect called", UiLogLevel.DEBUG);

        try {
            doConnect();

            String host = FacesUtil.getClientIpAddress();
            connection.getClient().setHost(host);
            connection.setStatus(Status.CONNECTED);

            String message = ResourceUtil.getI18nMessage("label.connection.connected");
            message = String.format(message, connection.getUrl());
            addMessage(message, UiLogLevel.INFO);
        } catch (Exception ex) {
            connection.setStatus(Status.ERROR);
            guiState = GuiState.ERROR;
            String message = ResourceUtil.getI18nMessage("label.caughtException");
            message = String.format(message, ex.toString());
            addMessage(message, UiLogLevel.ERROR);
            message = ResourceUtil.getI18nMessage("label.connection.broker.error");
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Fire Cancel of connect
     *
     * @param event action event
     */
    public void cancelConnect(ActionEvent event) {
        addMessage("cancelConnect called", UiLogLevel.DEBUG);
    }

    /**
     * Fire disconnect action
     *
     * @param event action event
     */
    public void disconnect(ActionEvent event) {
        addMessage("disconnect called", UiLogLevel.DEBUG);

        try {
            doDisconnect();

            String message = ResourceUtil.getI18nMessage("label.disconnection.close");
            message = String.format(message, connection.getUrl());
            addMessage(message, UiLogLevel.INFO);
            message = ResourceUtil.getI18nMessage("label.message.clearReceived.success");
            addMessage(message, UiLogLevel.INFO);
        } catch (Exception ex) {
            connection.setStatus(Status.ERROR);
            String message = ResourceUtil.getI18nMessage("label.caughtException");
            message = String.format(message, ex.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(ex.getMessage(), ex);
        }

        reinitConnection();
        reinitReceiver();
    }

    /**
     * Fire cancel disconnect
     *
     * @param event action event
     */
    public void cancelDisconnect(ActionEvent event) {
        addMessage("cancelDisconnect called", UiLogLevel.DEBUG);
    }

    /**
     * Fire send message
     *
     * @param event action event
     */
    public void sendMessage(ActionEvent event) {
        addMessage("sendMessage called", UiLogLevel.DEBUG);

        try {
            doSendMessage();
        } catch (Exception ex) {
            String message = ResourceUtil.getI18nMessage("label.caughtException");
            message = String.format(message, ex.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Fire cancel send message
     *
     * @param event action event
     */
    public void cancelSendMessage(ActionEvent event) {
        addMessage("cancelSendMessage called", UiLogLevel.DEBUG);
    }

    /**
     * Fire receive message
     *
     * @param event action event
     */
    public void receiveMessage(ActionEvent event) {
        addMessage("receiveMessage called", UiLogLevel.DEBUG);

        try {
            doReceiveMessage();

            String message = ResourceUtil.getI18nMessage("label.message.receiving.started");
            message = String.format(message, receiver.getDestinationName());
            addMessage(message, UiLogLevel.INFO);
            message = ResourceUtil.getI18nMessage("label.receiver.waitingOnMessage");
            addMessage(message, UiLogLevel.INFO);
        } catch (Exception ex) {
            String message = ResourceUtil.getI18nMessage("label.caughtException");
            message = String.format(message, ex.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Fire cancel receive message
     *
     * @param event action event
     */
    public void cancelReceiveMessage(ActionEvent event) {
        addMessage("cancelReceiveMessage called", UiLogLevel.DEBUG);
    }

    /**
     * Fire stop message receiver
     *
     * @param event action event
     */
    public void stopMessageReceiver(ActionEvent event) {
        addMessage("stopMessageReceiver called", UiLogLevel.DEBUG);

        try {
            doStopMessageReceiver();

            String message = ResourceUtil.getI18nMessage("label.message.receiving.stopped");
            message = String.format(message, receiver.getDestinationName());
            addMessage(message, UiLogLevel.INFO);
        } catch (Exception ex) {
            String message = ResourceUtil.getI18nMessage("label.caughtException");
            message = String.format(message, ex.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(ex.getMessage(), ex);
        }

        reinitReceiver();
    }

    /**
     * Fire clear message
     *
     * @param event action event
     */
    public void clearMessages(ActionEvent event) {
        addMessage("clearMessages called", UiLogLevel.DEBUG);

        try {
            doClearMessages();

            String message = ResourceUtil.getI18nMessage("label.message.clearReceived.success");
            addMessage(message, UiLogLevel.INFO);
        } catch (Exception ex) {
            String message = ResourceUtil.getI18nMessage("label.caughtException");
            message = String.format(message, ex.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Start autoupdater for message tables
     *
     * @param event action event
     */
    public void startPoll(ActionEvent event) {
        autoUpdateMessages = true;
        String message = ResourceUtil.getI18nMessage("label.poller.start.success.message");
        addMessage(message, UiLogLevel.INFO);
        message = ResourceUtil.getI18nMessage("label.poller.start.success.info.message");
        addMessage(message, UiLogLevel.INFO);
    }

    /**
     * Stop autoupdater for message tables
     *
     * @param event action event
     */
    public void stopPoll(ActionEvent event) {
        autoUpdateMessages = false;
        String message = ResourceUtil.getI18nMessage("label.poller.stop.success.message");
        addMessage(message, UiLogLevel.INFO);
    }

    /**
     * Get index page
     *
     * @return index page
     */
    @Override
    public String index() {
        return "START_PAGE";
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public MessageController getMessageController() {
        return messageController;
    }

    public void setMessageController(MessageController messageController) {
        this.messageController = messageController;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Message getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(Message selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public Message getMessageToSend() {
        return messageToSend;
    }

    public void setMessageToSend(Message messageToSend) {
        this.messageToSend = messageToSend;
    }

    public Property getSelectedMapMessage() {
        return selectedMapMessage;
    }

    public void setSelectedMapMessage(Property selectedMapMessage) {
        this.selectedMapMessage = selectedMapMessage;
    }

    public HeaderProperty getSelectedHeader() {
        return selectedHeader;
    }

    public void setSelectedHeader(HeaderProperty selectedHeader) {
        this.selectedHeader = selectedHeader;
    }

    public Property getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public Property getSelectedConnectionAdvancedProperty() {
        return selectedConnectionAdvancedProperty;
    }

    public void setSelectedConnectionAdvancedProperty(Property selectedConnectionAdvancedProperty) {
        this.selectedConnectionAdvancedProperty = selectedConnectionAdvancedProperty;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public boolean isAutoUpdateMessages() {
        return autoUpdateMessages;
    }

    public void setAutoUpdateMessages(boolean autoUpdateMessages) {
        this.autoUpdateMessages = autoUpdateMessages;
    }

    public GuiState getGuiState() {
        return guiState;
    }

    // XXX: Jms settings
    /**
     * Do connection
     *
     * @return true - if connection success, false - if failed
     * @throws Exception
     */
    public boolean doConnect() throws Exception {
        if (connection.getStatus() == Status.CONNECTED && (guiState == GuiState.CONNECTED_RECEIVER_STARTED || guiState == GuiState.CONNECTED_RECEIVER_STOPPED)) {
            return true;
        }

        // to do auto reconnect
        if (jmsConnection != null) {
            jmsConnection.close();
        }

        jmsConnectionFactory = JmsConnectionFactoryRegistry.getConnectionFactory(connection);

        jmsConnection = jmsConnectionFactory.createConnection(connection.getClient().getName(),
            connection.getClient().getPassword());

        // set exception listener to the Jms connection.
        jmsConnection.setExceptionListener(new ExceptionListener() {

            @Override
            public void onException(JMSException jmse) {
                String message = ResourceUtil.getMessageResourceString("label.message.connection.exception");
                message = String.format(message, jmse.toString());
                addMessage(message, UiLogLevel.ERROR);

                try {
                    guiState = GuiState.ERROR;
                    connection.setStatus(Status.ERROR);
                    doDisconnect();
                } catch (Exception ex) {
                    String exceptionMessage = ResourceUtil.getMessageResourceString("label.caughtException");
                    exceptionMessage = String.format(exceptionMessage, ex.toString());
                    addMessage(exceptionMessage, UiLogLevel.ERROR);
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        });
        jmsConnection.start();

        // update connection status
        connection.setStatus(Status.CONNECTED);
        guiState = GuiState.CONNECTED_RECEIVER_STOPPED;

        return true;
    }

    /**
     * Do disconnect
     *
     * @throws Exception
     */
    private void doDisconnect() throws Exception {
        if (connection.getStatus() == Status.NOT_CONNECTED) {
            return;
        }

        destroyCurrentSession();
        doClearMessages();

        connection.setStatus(Status.NOT_CONNECTED);
        guiState = GuiState.DISCONNECTED;
        receiver.setStatus(Status.NOT_CONNECTED);
    }

    /**
     * Do send message
     *
     * @throws Exception
     */
    private void doSendMessage() throws Exception {
        if (connection.getStatus() != Status.CONNECTED) {
            String message = ResourceUtil.getI18nMessage("label.message.send.broker.error");
            addMessage(message, UiLogLevel.ERROR);
            return;
        }

        try {
            boolean isTransacted = messageToSend.getTransactionType().isTransacted();
            String destinationName = messageToSend.getDestinationName();

            jmsProducerSession = createProducerSession(isTransacted);
            jmsDestination = createDestination(messageToSend.getDestinationType(), destinationName,
                jmsProducerSession);
            jmsMessageProducer = createProducer(jmsDestination, isTransacted);

            if (messageToSend.getDeliveryMode() == DeliveryMode.NON_PERSISTENT) {
                jmsMessageProducer.setDeliveryMode(messageToSend.getDeliveryMode().index());
            }

            // start message sender
            Thread msgSender = new SenderThread();
            msgSender.start();
        } catch (Exception e) {
            String message = ResourceUtil.getI18nMessage("label.message.send.error");
            message = String.format(message, e.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);
            guiState = GuiState.ERROR;
            connection.setStatus(Status.ERROR);
            doDisconnect();
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Send message
     *
     * @throws Exception
     */
    private void sendMessage() throws Exception {
        boolean isTransacted = messageToSend.getTransactionType().isTransacted();

        String messageId = UUID.randomUUID().toString();

        javax.jms.Message msg = null;

        try {
            msg = createMessage(messageId);

            jmsMessageProducer.send(msg);

            if (isTransacted) {
                jmsProducerSession.commit();
            }
        } catch (TransactionRolledBackException e) {
            String message = ResourceUtil.getMessageResourceString("label.message.send.error");
            message = String.format(message, e.toString());
            addStatusAreaMessage(message, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);
        } catch (JMSException e) {
            String message = ResourceUtil.getMessageResourceString("label.message.send.error");
            message = String.format(message, e.toString());
            addStatusAreaMessage(message, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);
            if (isTransacted) {
                jmsProducerSession.rollback();
            }
        }

        try {
            String message = ResourceUtil.getMessageResourceString("label.message.sending.info");
            message = String.format(message, messageId, JmsUtility.getDestinationName(msg.getJMSDestination()));
            addStatusAreaMessage(message, UiLogLevel.INFO);
        } catch (JMSException e) {
            String message = ResourceUtil.getMessageResourceString("label.message.send.error");
            message = String.format(message, e.toString());
            addStatusAreaMessage(message, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);
        }

        reinitSendMessage();
    }

    /**
     * Do receive message
     *
     * @throws Exception
     */
    private void doReceiveMessage() throws Exception {
        if (connection.getStatus() != Status.CONNECTED) {
            String message = ResourceUtil.getI18nMessage("label.message.receive.broker.error");
            addMessage(message, UiLogLevel.ERROR);
            return;
        }

        receiver.setStatus(Status.CONNECTED);
        guiState = GuiState.CONNECTED_RECEIVER_STARTED;

        try {
            boolean isTransacted = receiver.getAcknowledgeMode().isTransacted();
            String destinationName = receiver.getDestinationName();
            DestinationType destinationType = receiver.getDestinationType();
            String selector = receiver.getSelector();
            int acknowledgeMode = receiver.getAcknowledgeMode().index();

            jmsConsumerSession = createConsumerSession(isTransacted, acknowledgeMode);

            jmsDestination = createDestination(destinationType, destinationName,
                jmsConsumerSession);
            jmsMessageConsumer = createConsumer(jmsDestination, destinationType.index(), isTransacted,
                jmsConsumerSession, selector);

            // start message receiver
            Thread msgReceiver = new ReceiverThread();
            msgReceiver.start();
        } catch (Exception e) {
            String message = ResourceUtil.getI18nMessage("label.message.receive.error");
            message = String.format(message, e.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);
            connection.setStatus(Status.ERROR);
            receiver.setStatus(Status.ERROR);
            guiState = GuiState.ERROR;
            doDisconnect();
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Start message receiver in separate thread
     *
     * @throws Exception
     */
    private void receiveMessage() throws Exception {
        AcknowledgeMode acknowledgeMode = receiver.getAcknowledgeMode();
        boolean isTransacted = acknowledgeMode.isTransacted();

        javax.jms.Message msg = null;
        while ((msg = jmsMessageConsumer.receive()) != null) {

            try {
                receiver.setStatus(Status.CONNECTED);
                guiState = GuiState.CONNECTED_RECEIVER_STARTED;

                if (isTransacted) {
                    jmsConsumerSession.commit();
                } else if (acknowledgeMode == AcknowledgeMode.CLIENT_ACKNOWLEDGE) {
                    msg.acknowledge();
                }
            } catch (TransactionRolledBackException ex) {
                String messageStr = ResourceUtil.getMessageResourceString("label.message.receive.error");
                messageStr = String.format(messageStr, ex.toString());
                addStatusAreaMessage(messageStr, UiLogLevel.ERROR);
                LOGGER.error(ex.getMessage(), ex);
                if (receiver.getStatus() != Status.CONNECTED) {
                    receiver.setStatus(Status.CONNECTED);
                    guiState = GuiState.CONNECTED_RECEIVER_STARTED;

                    break;
                }

                if (isConnectionUp()) {
                    continue;
                } else {
                    break;
                }
            } catch (JMSException ex) {
                String messageStr = ResourceUtil.getMessageResourceString("label.message.receive.error");
                messageStr = String.format(messageStr, ex.toString());
                addStatusAreaMessage(messageStr, UiLogLevel.ERROR);
                LOGGER.error(ex.getMessage(), ex);
                if (isTransacted) {
                    jmsConsumerSession.rollback();
                }

                if (receiver.getStatus() != Status.CONNECTED) {
                    receiver.setStatus(Status.CONNECTED);
                    guiState = GuiState.CONNECTED_RECEIVER_STARTED;

                    break;
                }

                if (isConnectionUp()) {
                    continue;
                } else {
                    break;
                }
            }

            // add message to UI table
            addMessageToTable(msg);

            try {
                String messageStr = ResourceUtil.getMessageResourceString("label.message.receiving.info");
                messageStr = String.format(messageStr, msg.getJMSMessageID(), JmsUtility.getDestinationName(msg.getJMSDestination()));
                addStatusAreaMessage(messageStr, UiLogLevel.INFO);
            } catch (JMSException e) {
                String messageStr = ResourceUtil.getMessageResourceString("label.caughtException");
                messageStr = String.format(messageStr, e.toString());
                addStatusAreaMessage(messageStr, UiLogLevel.ERROR);
                LOGGER.error(e.getMessage(), e);
            }
        }

        jmsMessageConsumer.close();

        if (receiver.getStatus() == Status.CONNECTED) {
            receiver.setStatus(Status.NOT_CONNECTED);
            guiState = GuiState.DISCONNECTED;
        }
    }

    /**
     * Do stop message receiver
     *
     * @throws Exception
     */
    private void doStopMessageReceiver() throws Exception {
        if (jmsMessageConsumer == null) {
            return;
        }

        try {
            jmsMessageConsumer.close();
        } catch (Exception e) {
            String messageStr = ResourceUtil.getMessageResourceString("label.caughtException");
            messageStr = String.format(messageStr, e.toString());
            addMessage(messageStr, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);
        }

        guiState = GuiState.CONNECTED_RECEIVER_STOPPED;
        receiver.setStatus(Status.NOT_CONNECTED);
    }

    /**
     * Do clear messages from the messages table
     *
     * @throws Exception
     */
    private void doClearMessages() throws Exception {
        messageController.destroyAll(connection.getClient().getClientId());
        updateMessagesTable();
    }

    // XXX: Helper methods
    /**
     * Create message by message id
     *
     * @param messageId the message id
     * @return jms message
     * @throws Exception
     */
    public javax.jms.Message createMessage(String messageId) throws Exception {
        MessageType type = messageToSend.getType();
        String simpleBody = messageToSend.getSimpleBody();
        List<Property> mapBodyItems = messageToSend.getMapBodyItems();
        byte[] byteBody = ((simpleBody == null) ? new byte[0] : simpleBody.getBytes());

        javax.jms.Message jmsMessage = null;

        switch (type) {
            case TEXT:
                jmsMessage = jmsProducerSession.createTextMessage();
                ((TextMessage) jmsMessage).setText(simpleBody);

                break;

            case OBJECT:
                jmsMessage = jmsProducerSession.createObjectMessage();
                ((ObjectMessage) jmsMessage).setObject(simpleBody);

                break;

            case MAP:
                jmsMessage = jmsProducerSession.createMapMessage();

                for (Property mapItem : mapBodyItems) {
                    Type propertyType = mapItem.getType();

                    switch (propertyType) {
                        case BOOLEAN:
                            ((MapMessage) jmsMessage).setBoolean(mapItem.getName(), Boolean.valueOf(mapItem.getValue()));
                            break;
                        case BYTE:
                            ((MapMessage) jmsMessage).setByte(mapItem.getName(), Byte.valueOf(mapItem.getValue()));
                            break;
                        case CHAR:
                            ((MapMessage) jmsMessage).setChar(mapItem.getName(), mapItem.getValue().charAt(0));
                            break;
                        case DATE_TIME:
                            ((MapMessage) jmsMessage).setString(mapItem.getName(), mapItem.getValue());
                            break;
                        case DOUBLE:
                            ((MapMessage) jmsMessage).setDouble(mapItem.getName(), Double.valueOf(mapItem.getValue()));
                            break;
                        case FLOAT:
                            ((MapMessage) jmsMessage).setFloat(mapItem.getName(), Float.valueOf(mapItem.getValue()));
                            break;
                        case INT:
                            ((MapMessage) jmsMessage).setInt(mapItem.getName(), Integer.valueOf(mapItem.getValue()));
                            break;
                        case LONG:
                            ((MapMessage) jmsMessage).setLong(mapItem.getName(), Long.valueOf(mapItem.getValue()));
                            break;
                        case OBJECT:
                            ((MapMessage) jmsMessage).setString(mapItem.getName(), mapItem.getValue());
                            break;
                        case SHORT:
                            ((MapMessage) jmsMessage).setShort(mapItem.getName(), Short.valueOf(mapItem.getValue()));
                            break;
                        case STRING:
                            ((MapMessage) jmsMessage).setString(mapItem.getName(), mapItem.getValue());
                            break;
                        default:
                            throw new UnsupportedOperationException("Map message body item type not supported: " + propertyType);
                    }
                }

                break;

            case BYTES:
                jmsMessage = jmsProducerSession.createBytesMessage();
                ((BytesMessage) jmsMessage).writeBytes(byteBody);

                break;

            case STREAM:
                jmsMessage = jmsProducerSession.createStreamMessage();
                ((StreamMessage) jmsMessage).writeBytes(byteBody);

                break;

            default:
                throw new UnsupportedOperationException("Message Type not supported: " + type);
        }

        jmsMessage.setJMSType(messageToSend.getDestinationJmsType());
        jmsMessage.setJMSMessageID(messageId);

        // fill jms properties
        for (Property property : messageToSend.getPropertyList()) {
            Type propertyType = property.getType();

            switch (propertyType) {
                case BOOLEAN:
                    jmsMessage.setBooleanProperty(property.getName(), Boolean.valueOf(property.getValue()));
                    break;
                case BYTE:
                    jmsMessage.setByteProperty(property.getName(), Byte.valueOf(property.getValue()));
                    break;
                case CHAR:
                    jmsMessage.setStringProperty(property.getName(), property.getValue());
                    break;
                case DATE_TIME:
                    jmsMessage.setStringProperty(property.getName(), property.getValue());
                    break;
                case DOUBLE:
                    jmsMessage.setDoubleProperty(property.getName(), Double.valueOf(property.getValue()));
                    break;
                case FLOAT:
                    jmsMessage.setFloatProperty(property.getName(), Float.valueOf(property.getValue()));
                    break;
                case INT:
                    jmsMessage.setIntProperty(property.getName(), Integer.valueOf(property.getValue()));
                    break;
                case LONG:
                    jmsMessage.setLongProperty(property.getName(), Long.valueOf(property.getValue()));
                    break;
                case OBJECT:
                    jmsMessage.setStringProperty(property.getName(), property.getValue());
                    break;
                case SHORT:
                    jmsMessage.setShortProperty(property.getName(), Short.valueOf(property.getValue()));
                    break;
                case STRING:
                    jmsMessage.setStringProperty(property.getName(), property.getValue());
                    break;
                default:
                    throw new UnsupportedOperationException("Map message body item type not supported: " + propertyType);
            }
        }

        // fill jms additional headers
        for (HeaderProperty header : messageToSend.getHeaderList()) {
            HeaderProperty.Type headerType = header.getType();

            switch (headerType) {
                case JMS_CORRELATION_ID:
                    jmsMessage.setJMSCorrelationID(header.getValue());
                    break;
                case JMS_EXPIRATION:
                    jmsMessage.setJMSExpiration(DateConverterHelper.convertToLong(header.getValue()));
                    break;
                case JMS_PRIORITY:
                    jmsMessage.setJMSPriority(Integer.valueOf(header.getValue()));
                    break;
                case JMS_REDELIVERED:
                    jmsMessage.setJMSRedelivered(Boolean.valueOf(header.getValue()));
                    break;
                case JMS_REPLY_TO:
                    Destination destinationReplyTo = createDestination(messageToSend.getDestinationType(), header.getValue(),
                        jmsProducerSession);
                    jmsMessage.setJMSReplyTo(destinationReplyTo);
                    break;
                default:
                    throw new UnsupportedOperationException("Map message body item type not supported: " + headerType);
            }
        }

        return jmsMessage;
    }

    /**
     * Create message consumer
     *
     * @param destination the destination
     * @param destinationType the destination type
     * @param isTransacted is consumer should be transacted
     * @param session the session
     * @param selector the message selector
     * @return message consumer
     * @throws JMSException
     */
    private MessageConsumer createConsumer(Destination destination, int destinationType,
        boolean isTransacted, Session session, String selector)
        throws JMSException {

        return session.createConsumer(destination, selector, isTransacted);
    }

    /**
     * Create message producer
     *
     * @param destination the destination
     * @param isTransacted is destination should be transacted
     * @return message producer
     * @throws JMSException
     */
    private MessageProducer createProducer(Destination destination, boolean isTransacted)
        throws JMSException {
        String key = String.format("%s%s", destination, isTransacted);
        MessageProducer messageProducer = (MessageProducer) cachedProducerMap.get(key);

        if (messageProducer == null) {
            messageProducer = jmsProducerSession.createProducer(destination);
            cachedProducerMap.put(key, messageProducer);
        }

        return messageProducer;
    }

    /**
     * Create producer session
     *
     * @param isTransacted is producer should be transacted
     * @return message producer session
     * @throws JMSException
     */
    private Session createProducerSession(boolean isTransacted)
        throws JMSException {
        String key = String.format("%sProducer", isTransacted);
        Session session = (Session) cachedSessionMap.get(key);

        if (session == null) {
            session = jmsConnection.createSession(isTransacted, Session.AUTO_ACKNOWLEDGE);
            cachedSessionMap.put(key, session);
        }

        return session;
    }

    /**
     * Create consumer session
     *
     * @param isTransacted is consumer session should be transacted
     * @param acknowledgeMode acknowledge mode
     * @return message consumer session
     * @throws JMSException
     */
    private Session createConsumerSession(boolean isTransacted, int acknowledgeMode)
        throws JMSException {
        String key = String.format("%s%sConsumer", isTransacted, acknowledgeMode);
        Session session = (Session) cachedSessionMap.get(key);

        if (session == null) {
            session = jmsConnection.createSession(isTransacted, acknowledgeMode);
            cachedSessionMap.put(key, session);
        }

        return session;
    }

    /**
     * Create message destination
     *
     * @param destinationType the destination type
     * @param destinationName the destination name
     * @param session the message session
     * @return message destination
     * @throws JMSException
     */
    private Destination createDestination(DestinationType destinationType, String destinationName, Session session)
        throws JMSException {
        if (destinationType == DestinationType.QUEUE) {
            return session.createQueue(destinationName);
        } else if (destinationType == DestinationType.TOPIC) {
            return session.createTopic(destinationName);
        }

        throw new UnsupportedOperationException("Destination Type not supported: " + destinationType);
    }

    /**
     * Check is connection up
     *
     * @return result of test connection
     */
    private boolean isConnectionUp() {
        boolean result = false;

        if (jmsConnection == null) {
            return false;
        }

        try {
            jmsConnection.start();
            result = true;
        } catch (JMSException e) {
            String message = ResourceUtil.getMessageResourceString("label.caughtException");
            message = String.format(message, e.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Add message to the messages table
     *
     * @param message the message
     * @throws Exception
     */
    private void addMessageToTable(javax.jms.Message message) throws Exception {
        Message targetMessage = MessageConverterHelper.convert(message);
        targetMessage.setClientId(connection.getClient().getClientId());
        messageController.create(targetMessage);
    }

    /**
     * Destroy current session
     */
    private void destroyCurrentSession() {
        try {
            if (jmsMessageConsumer != null) {
                jmsMessageConsumer.close();
            }

            // close all producers
            Collection<MessageProducer> producers = cachedProducerMap.values();

            for (Iterator<MessageProducer> it = producers.iterator(); it.hasNext();) {
                MessageProducer producer = it.next();
                producer.close();
            }

            cachedProducerMap.clear();

            // close all producer & consumer session
            Collection<javax.jms.Session> sessions = cachedSessionMap.values();

            for (Iterator<javax.jms.Session> it = sessions.iterator(); it.hasNext();) {
                Session session = it.next();
                session.close();
            }

            cachedSessionMap.clear();

            // close connection
            if (jmsConnection != null) {
                jmsConnection.close();
            }
            jmsMessageConsumer = null;
            jmsMessageProducer = null;
            jmsProducerSession = null;
            jmsConsumerSession = null;
            jmsConnection = null;
        } catch (Exception e) {
            String message = ResourceUtil.getMessageResourceString("label.caughtException");
            message = String.format(message, e.toString());
            addMessage(message, UiLogLevel.ERROR);
            LOGGER.error(e.getMessage(), e);

            // clear cached values in any case
            cachedProducerMap.clear();
            cachedSessionMap.clear();
        }
    }

    /**
     * Message sender class
     */
    class SenderThread extends Thread {

        @Override
        public void run() {
            try {
                sendMessage();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Message receiver thread
     */
    class ReceiverThread extends Thread {

        @Override
        public void run() {
            try {
                receiveMessage();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
