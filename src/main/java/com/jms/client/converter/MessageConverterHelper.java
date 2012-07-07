package com.jms.client.converter;

import com.jms.client.util.StringFormatterHelper;
import com.jms.client.entity.DeliveryMode;
import com.jms.client.entity.DestinationType;
import com.jms.client.entity.Message;
import com.jms.client.entity.MessageType;
import com.jms.client.util.JmsUtility;
import java.util.*;
import org.apache.commons.lang.Validate;

/**
 * Message converter helper class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class MessageConverterHelper {

    private MessageConverterHelper() {
    }

    /**
     * Convert Jms message to UI message
     *
     * @param sourceMessage the jms message
     * @return UI message representation of jms message
     * @throws Exception
     */
    public static com.jms.client.entity.Message convert(javax.jms.Message sourceMessage) throws Exception {
        Validate.notNull(sourceMessage, "Message must not be null");

        com.jms.client.entity.Message message = new Message();

        Map<String, String> headers = JmsUtility.jmsHeadersAsMap(sourceMessage);
        Map<String, String> properties = JmsUtility.jmsPropertiesAsMap(sourceMessage);
        String body = JmsUtility.jmsMessageBodyAsString(sourceMessage);
        String jmsMessageID = sourceMessage.getJMSMessageID();
        String destinationName = JmsUtility.getDestinationName(sourceMessage.getJMSDestination());
        String jmsType = sourceMessage.getJMSType();
        long jmsTimestamp = sourceMessage.getJMSTimestamp();
        int jmsDeliveryMode = sourceMessage.getJMSDeliveryMode();
        MessageType messageType = JmsUtility.getMessageType(sourceMessage);
        DestinationType destinationType = JmsUtility.getDestinationType(sourceMessage.getJMSDestination());
        Calendar time = GregorianCalendar.getInstance();
        time.setTimeInMillis(jmsTimestamp);

        String headersString = StringFormatterHelper.formatMap(headers, "\n");
        String propertiesString = StringFormatterHelper.formatMap(properties, "\n");

        message.setHeaders(headersString);
        message.setProperties(propertiesString);
        message.setBody(body);
        message.setJmsId(jmsMessageID);
        message.setDestinationJmsType(jmsType);
        message.setDestinationName(destinationName);
        message.setDestinationType(destinationType);
        message.setTimestamp(time);
        message.setType(messageType);
        message.setDeliveryMode(DeliveryMode.fromIndex(jmsDeliveryMode));

        return message;
    }
}
