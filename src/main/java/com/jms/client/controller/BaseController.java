package com.jms.client.controller;

import com.jms.client.entity.*;
import com.jms.client.entity.Property.Type;
import java.io.Serializable;
import java.util.*;

/**
 * Base controller class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public abstract class BaseController implements Serializable {

    /**
     * Initial size of messages
     */
    public static final long MESSAGES_INITIAL_SIZE = 3;
    /**
     * Initial size of message properties
     */
    public static final long MESSAGE_PROPERTIES_INITIAL_SIZE = 5;
    /**
     * Fake message list
     */
    protected static List<Message> messages = new Vector<Message>();

    static {
        for (long i = 0; i < MESSAGES_INITIAL_SIZE; i++) {
            Message message = new Message();
            message.setDeliveryMode(getRandomEnum(DeliveryMode.class));
            message.setDestinationJmsType(getRandomString());
            message.setDestinationName(getRandomString());
            message.setDestinationType(getRandomEnum(DestinationType.class));
            message.setHeaderList(getRandomJmsHeaders());
            message.setId(getRandomLong());
            message.setJmsId(getRandomString());
            message.setPropertyList(getRandomJmsProperties());
            message.setTimestamp(getRandomCalendar());
            message.setTransactionType(getRandomEnum(TransactionType.class));
            message.setType(getRandomEnum(MessageType.class));
            message.setSimpleBody(getRandomBody());
            message.setClientId(getRandomString());

            postAction(message);
            messages.add(message);
        }
    }

    /**
     * Post processing of message
     *
     * @param message the message
     */
    protected static void postAction(Message message) {
    }

    /**
     * Pre processing of message
     *
     * @param message the message
     */
    protected static void preAction(Message message) {
        if (message.getType() == MessageType.MAP) {
            message.setSimpleBody(null);
        } else {
            message.setMapBodyItems(null);
        }
    }

    /**
     * Get random map message body
     *
     * @return list of message body items
     */
    private static List<Property> getRandomMapBody() {
        List<Property> map = new ArrayList<Property>();

        for (long index = 0; index < MESSAGE_PROPERTIES_INITIAL_SIZE; index++) {
            map.add(new Property(getRandomString(), getRandomString(), getRandomEnum(Type.class)));
        }

        return map;
    }

    /**
     * Get random message body
     *
     * @return message body
     */
    private static String getRandomBody() {
        return getRandomString();
    }

    /**
     * Generate random string
     *
     * @return random string
     */
    public static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get random Enum value
     *
     * @param <T> the generic type of the Enum
     * @param clazz the class of the Enum
     * @return random Enum value
     */
    public static <T extends Enum> T getRandomEnum(Class<T> clazz) {
        Object[] arr = clazz.getEnumConstants();
        int size = arr.length;
        int index = (int) (Math.random() * size);

        return (T) arr[index];
    }

    /**
     * Generate random number between numbers
     *
     * @param start the start number
     * @param end the end number
     * @return random number between numbers
     */
    public static int getRandomBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    /**
     * Generate random Calendar date
     *
     * @return random Calendar date
     */
    public static Calendar getRandomCalendar() {
        int year = getRandomBetween(1900, 2010);
        int month = getRandomBetween(0, 11);
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = getRandomBetween(1, gc.getActualMaximum(gc.DAY_OF_MONTH));
        gc.set(year, month, day);

        return gc;
    }

    /**
     * Get random of Jms message headers
     *
     * @return random jms message headers
     */
    public static List<HeaderProperty> getRandomJmsHeaders() {
        List<HeaderProperty> list = new ArrayList<HeaderProperty>();

        final String valuePrefix = "value#";
        for (long index = 0; index < MESSAGE_PROPERTIES_INITIAL_SIZE; index++) {
            HeaderProperty item = new HeaderProperty(getRandomEnum(HeaderProperty.Type.class), valuePrefix + index);
            list.add(item);
        }
        return list;
    }

    /**
     * Generate random jms message properties
     *
     * @return random jms message properties
     */
    public static List<Property> getRandomJmsProperties() {
        List<Property> list = new ArrayList<Property>();

        final String namePrefix = "name#";
        final String valuePrefix = "value#";
        for (long index = 0; index < MESSAGE_PROPERTIES_INITIAL_SIZE; index++) {
            Property item = new Property(namePrefix + index, valuePrefix + index, getRandomEnum(Property.Type.class));
            list.add(item);
        }
        return list;
    }

    /**
     * Generate random Long value
     *
     * @return random Long value
     */
    public static long getRandomLong() {
        return System.nanoTime();
    }

    /**
     * Generate random Int value
     *
     * @return random Int value
     */
    public static int getRandomInt() {
        return (int) System.nanoTime();
    }
}
