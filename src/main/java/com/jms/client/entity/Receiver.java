package com.jms.client.entity;

import java.io.Serializable;

public class Receiver implements Serializable {

    private String destinationName;
    private DestinationType destinationType;
    private AcknowledgeMode acknowledgeMode;
    private String selector;
    private Status status;

    public Receiver() {
    }

    public Receiver(Status status) {
        this.status = status;
    }

    public AcknowledgeMode getAcknowledgeMode() {
        return acknowledgeMode;
    }

    public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
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

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Receiver{" + "destinationName=" + destinationName + ", destinationType=" + destinationType + ", acknowledgeMode=" + acknowledgeMode + ", selector=" + selector + ", status=" + status + '}';
    }
}
