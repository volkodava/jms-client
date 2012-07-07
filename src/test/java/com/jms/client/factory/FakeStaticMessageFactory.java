package com.jms.client.factory;

import com.jms.client.entity.*;
import com.jms.client.entity.Property.Type;
import java.util.UUID;

public class FakeStaticMessageFactory {

    public static final String CLIENT_ID = "CLIENT#123";

    public static Message createMessage() {
        String body = UUID.randomUUID().toString();
        Message entity = new Message();
        entity.setBody(body);
        entity.setSimpleBody(body);
        entity.setDeliveryMode(DeliveryMode.PERSISTENT);
        entity.setDestinationJmsType(UUID.randomUUID().toString());
        entity.setDestinationName(UUID.randomUUID().toString());
        entity.setDestinationType(DestinationType.QUEUE);
        entity.setJmsId("1000");
        entity.setTransactionType(TransactionType.TRANSACTED);
        entity.setType(MessageType.TEXT);
        entity.getHeaderList().add(new HeaderProperty(HeaderProperty.Type.JMS_REPLY_TO, "value1"));
        entity.getPropertyList().add(new Property(UUID.randomUUID().toString(), "value1", Type.STRING));
        entity.setClientId(CLIENT_ID);

        return entity;
    }

    public static Message createMapMessage() {
        Message entity = new Message();
        entity.getMapBodyItems().add(new Property(UUID.randomUUID().toString(), "Test message", Type.STRING));
        entity.setDeliveryMode(DeliveryMode.PERSISTENT);
        entity.setDestinationJmsType("1001");
        entity.setDestinationName("dest-1001");
        entity.setDestinationType(DestinationType.TOPIC);
        entity.setJmsId("1001");
        entity.setTransactionType(TransactionType.TRANSACTED);
        entity.setType(MessageType.MAP);
        entity.getHeaderList().add(new HeaderProperty(HeaderProperty.Type.JMS_REPLY_TO, "value1"));
        entity.getPropertyList().add(new Property(UUID.randomUUID().toString(), "value1", Type.STRING));
        entity.setClientId(CLIENT_ID);

        return entity;
    }
}
