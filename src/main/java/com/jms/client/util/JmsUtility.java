package com.jms.client.util;

import com.jms.client.converter.DateConverterHelper;
import com.jms.client.entity.DestinationType;
import com.jms.client.entity.MessageType;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.commons.lang.Validate;

/**
 * JMS utility class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class JmsUtility {

    /**
     * Get Jms Destination name
     *
     * @param destination the destination
     * @return jms destination name
     * @throws JMSException
     */
    public static String getDestinationName(Destination destination) throws JMSException {
        if (destination == null) {
            return String.valueOf(destination);
        }

        if (destination instanceof Queue) {
            return ((Queue) destination).getQueueName();
        } else if (destination instanceof Topic) {
            return ((Topic) destination).getTopicName();
        }

        throw new UnsupportedOperationException("Destination of message could not be detected: " + destination);
    }

    /**
     * Get Jms Destination Type
     *
     * @param destination the destination
     * @return jms destination type
     * @throws JMSException
     */
    public static DestinationType getDestinationType(Destination destination) throws JMSException {
        Validate.notNull(destination, "Destination must not be null");

        if (destination instanceof Queue) {
            return DestinationType.QUEUE;
        } else if (destination instanceof Topic) {
            return DestinationType.TOPIC;
        }

        throw new UnsupportedOperationException("Destination of message could not be detected: " + destination);
    }

    /**
     * Get Jms Message Type
     *
     * @param message the jms message
     * @return jms message type
     */
    public static MessageType getMessageType(Message message) {
        Validate.notNull(message, "Message must not be null");

        if (message instanceof TextMessage) {
            return MessageType.TEXT;
        } else if (message instanceof BytesMessage) {
            return MessageType.BYTES;
        } else if (message instanceof MapMessage) {
            return MessageType.MAP;
        } else if (message instanceof ObjectMessage) {
            return MessageType.OBJECT;
        } else if (message instanceof StreamMessage) {
            return MessageType.STREAM;
        }

        throw new UnsupportedOperationException("Message Type of message could not be detected: " + message.getClass());
    }

    /**
     * Convert byte array to hex string
     *
     * @param byteArray the array of bytes
     * @return hex representation of array of bytes
     */
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++) {
            int v = byteArray[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }

        return sb.toString().toUpperCase();
    }

    /**
     * Get Jms Bytes message body as a string
     *
     * @param message the jms bytes message
     * @return jms bytes message body as a string
     */
    public static String jmsBytesBodyAsString(Message message) {
        Validate.notNull(message, "Message must not be null");

        byte[] body = new byte[1024];
        int n = 0;

        if (message instanceof BytesMessage) {
            try {
                BytesMessage bs = (BytesMessage) message;
                bs.reset();
                n = bs.readBytes(body);
            } catch (JMSException ex) {
                return ex.toString();
            }
        } else if (message instanceof StreamMessage) {
            try {
                StreamMessage sm = (StreamMessage) message;
                sm.reset();
                n = sm.readBytes(body);
            } catch (JMSException ex) {
                return ex.toString();
            }
        }

        if (n <= 0) {
            return "";
        }

        StringBuilder hexBody = new StringBuilder();
        hexBody.append(byteArrayToHexString(body));
        if (n >= body.length) {
            hexBody.append("\n. . .");
        }

        return hexBody.toString();
    }

    /**
     * Get Jms message body as a string
     *
     * @param message the message
     * @return jms message body as a string
     * @throws JMSException
     */
    public static String jmsMessageBodyAsString(Message message) throws JMSException {
        Validate.notNull(message, "Message must not be null");

        if (message instanceof TextMessage) {
            try {
                return ((TextMessage) message).getText();
            } catch (JMSException ex) {
                return ex.toString();
            }
        } else if (message instanceof BytesMessage) {
            return jmsBytesBodyAsString(message);
        } else if (message instanceof MapMessage) {
            MapMessage msg = (MapMessage) message;
            Map<String, String> mapProperties = new HashMap<String, String>();

            try {
                for (Enumeration<?> enumeration = msg.getMapNames(); enumeration.hasMoreElements();) {
                    String name = (enumeration.nextElement()).toString();
                    mapProperties.put(name, (msg.getObject(name)).toString());
                }

                return mapProperties.toString();
            } catch (JMSException ex) {
                return (ex.toString());
            }
        } else if (message instanceof ObjectMessage) {
            ObjectMessage msg = (ObjectMessage) message;
            try {
                Object object = msg.getObject();

                return String.valueOf(object);
            } catch (Exception ex) {
                return ex.toString();
            }
        } else if (message instanceof StreamMessage) {
            return jmsBytesBodyAsString(message);
        }

        throw new UnsupportedOperationException("Unknown message type: " + message.getClass());
    }

    /**
     * Get Jms message properties as map
     *
     * @param message the message
     * @return map of jms message properties
     * @throws JMSException
     */
    public static Map<String, String> jmsPropertiesAsMap(Message message)
        throws JMSException {
        Validate.notNull(message, "Message must not be null");

        Map<String, String> properties = new LinkedHashMap<String, String>();

        Enumeration<String> propertyNames = message.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = propertyNames.nextElement();
            String propertyValue = message.getStringProperty(propertyName);

            properties.put(propertyName, propertyValue);
        }

        return properties;
    }

    /**
     * Get Jms message headers as a map
     *
     * @param message the message
     * @return map of jms message headers
     * @throws JMSException
     */
    public static Map<String, String> jmsHeadersAsMap(Message message)
        throws JMSException {
        Validate.notNull(message, "Message must not be null");

        Map<String, String> headers = new LinkedHashMap<String, String>();

        String stringJmsProperty = message.getJMSMessageID();
        headers.put("JMSMessageID", stringJmsProperty);

        stringJmsProperty = message.getJMSCorrelationID();
        headers.put("JMSCorrelationID", stringJmsProperty);

        Destination jmsDestination = message.getJMSDestination();
        String destinationType = null;
        if (jmsDestination != null) {
            destinationType = ResourceUtil.getMessageResourceString(getDestinationType(jmsDestination).name());
            headers.put(String.format("JMSDestination (%s)", destinationType), getDestinationName(jmsDestination));
        } else {
            headers.put("JMSDestination", String.valueOf(destinationType));
        }

        jmsDestination = message.getJMSReplyTo();
        if (jmsDestination != null) {
            destinationType = ResourceUtil.getMessageResourceString(getDestinationType(jmsDestination).name());
            headers.put(String.format("JMSReplyTo (%s)", destinationType), getDestinationName(jmsDestination));
        } else {
            headers.put("JMSReplyTo", String.valueOf(destinationType));
        }

        stringJmsProperty = DateConverterHelper.convertToString(message.getJMSTimestamp());
        headers.put("JMSTimestamp", stringJmsProperty);

        stringJmsProperty = message.getJMSType();
        headers.put("JMSType", stringJmsProperty);

        stringJmsProperty = String.valueOf(message.getJMSDeliveryMode());
        headers.put("JMSDeliveryMode", stringJmsProperty);

        stringJmsProperty = DateConverterHelper.convertToString(message.getJMSExpiration());
        headers.put("JMSExpiration", stringJmsProperty);

        stringJmsProperty = String.valueOf(message.getJMSPriority());
        headers.put("JMSPriority", stringJmsProperty);

        stringJmsProperty = String.valueOf(message.getJMSRedelivered());
        headers.put("JMSRedelivered", stringJmsProperty);

        return headers;
    }
}
