package com.jms.client.entity;

public enum DeliveryMode {

    PERSISTENT,
    NON_PERSISTENT;

    public int index() {
        if (this == DeliveryMode.PERSISTENT) {
            return javax.jms.DeliveryMode.PERSISTENT;
        } else if (this == DeliveryMode.NON_PERSISTENT) {
            return javax.jms.DeliveryMode.NON_PERSISTENT;
        }

        throw new IllegalArgumentException("Delivery mode could not be mapped");
    }

    public static DeliveryMode fromIndex(int index) {
        if (index == javax.jms.DeliveryMode.PERSISTENT) {
            return DeliveryMode.PERSISTENT;
        }
        if (index == javax.jms.DeliveryMode.NON_PERSISTENT) {
            return DeliveryMode.NON_PERSISTENT;
        }

        throw new IllegalArgumentException("Delivery Mode index not supported: " + index);
    }
}
