package com.jms.client.entity;

public enum DestinationType {

    QUEUE,
    TOPIC;

    public int index() {
        if (this == DestinationType.QUEUE) {
            return 0;
        } else if (this == DestinationType.TOPIC) {
            return 1;
        }

        throw new IllegalArgumentException("Destination type could not be mapped");
    }

    public static DestinationType fromIndex(int index) {
        if (index == 0) {
            return DestinationType.QUEUE;
        }
        if (index == 1) {
            return DestinationType.TOPIC;
        }

        throw new IllegalArgumentException("Destination Type index not supported: " + index);
    }
}
