package com.jms.client.entity;

import java.util.*;
import org.apache.commons.lang.Validate;

public class Message extends BaseEntity implements Comparable<Message> {

    private String jmsId;
    private String clientId;
    private String destinationName;
    private String destinationJmsType;
    private DestinationType destinationType;
    private DeliveryMode deliveryMode;
    private TransactionType transactionType;
    private MessageType type;
    private Calendar timestamp = Calendar.getInstance();
    private List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
    private List<Property> propertyList = new ArrayList<Property>();
    private List<Property> mapBodyItems = new ArrayList<Property>();
    private String simpleBody;
    /**
     * Readonly body value. Used only in the received page data.
     */
    private String body;
    private String headers;
    private String properties;
    private Status status;

    public static enum Status {

        SEND, RECEIVED;
    }

    public Message() {
    }

    public Message(long id) {
        super(id);
    }

    public Message(Status status) {
        this.status = status;
    }

    public static void cloneInstance(Message src, Message dest) {
        Validate.notNull(src, "Source message must not be null");
        Validate.notNull(dest, "Destination message must not be null");

        dest.setDeliveryMode(src.getDeliveryMode());
        dest.setDestinationJmsType(src.getDestinationJmsType());
        dest.setDestinationName(src.getDestinationName());
        dest.setDestinationType(src.getDestinationType());
        dest.setHeaders(src.getHeaders());
        dest.setId(src.getId());
        dest.setJmsId(src.getJmsId());
        dest.setProperties(src.getProperties());
        dest.setTimestamp(src.getTimestamp());
        dest.setTransactionType(src.getTransactionType());
        dest.setType(src.getType());
        dest.setBody(src.getBody());
        dest.setSimpleBody(src.getSimpleBody());
        dest.setMapBodyItems(src.getMapBodyItems());
        dest.setHeaderList(src.getHeaderList());
        dest.setHeaders(src.getHeaders());
        dest.setProperties(src.getProperties());
        dest.setPropertyList(src.getPropertyList());
    }

    protected void postAction() {
    }

    protected void preAction() {
        if (type == MessageType.MAP) {
            simpleBody = null;
        } else {
            mapBodyItems = null;
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public DeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(DeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDestinationJmsType() {
        return destinationJmsType;
    }

    public void setDestinationJmsType(String destinationJmsType) {
        this.destinationJmsType = destinationJmsType;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public DestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public List<HeaderProperty> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<HeaderProperty> headerList) {
        this.headerList = headerList;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getJmsId() {
        return jmsId;
    }

    public void setJmsId(String jmsId) {
        this.jmsId = jmsId;
    }

    public List<Property> getMapBodyItems() {
        return mapBodyItems;
    }

    public void setMapBodyItems(List<Property> mapBodyItems) {
        this.mapBodyItems = mapBodyItems;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public String getSimpleBody() {
        return simpleBody;
    }

    public void setSimpleBody(String simpleBody) {
        this.simpleBody = simpleBody;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseEntity other = (BaseEntity) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public int compareTo(Message o) {
        if (id < o.id) {
            return -1;
        } else if (id > o.id) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Message{" + "jmsId=" + jmsId + ", clientId=" + clientId + ", destinationName=" + destinationName + ", destinationJmsType=" + destinationJmsType + ", destinationType=" + destinationType + ", deliveryMode=" + deliveryMode + ", transactionType=" + transactionType + ", type=" + type + ", timestamp=" + timestamp + ", headerList=" + headerList + ", propertyList=" + propertyList + ", mapBodyItems=" + mapBodyItems + ", simpleBody=" + simpleBody + ", body=" + body + ", headers=" + headers + ", properties=" + properties + ", status=" + status + '}';
    }
}
