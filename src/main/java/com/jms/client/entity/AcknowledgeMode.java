package com.jms.client.entity;

import javax.jms.Session;

public enum AcknowledgeMode {

    AUTO_ACKNOWLEDGE,
    CLIENT_ACKNOWLEDGE,
    DUPS_OK_ACKNOWLEDGE,
    SESSION_TRANSACTED_MODE;

    public int index() {
        if (this == AcknowledgeMode.AUTO_ACKNOWLEDGE) {
            return Session.AUTO_ACKNOWLEDGE;
        } else if (this == AcknowledgeMode.CLIENT_ACKNOWLEDGE) {
            return Session.CLIENT_ACKNOWLEDGE;
        } else if (this == AcknowledgeMode.DUPS_OK_ACKNOWLEDGE) {
            return Session.DUPS_OK_ACKNOWLEDGE;
        } else if (this == AcknowledgeMode.SESSION_TRANSACTED_MODE) {
            return Session.SESSION_TRANSACTED;
        }

        throw new IllegalArgumentException("Acknowledge mode could not be mapped");
    }

    public boolean isTransacted() {
        return this == AcknowledgeMode.SESSION_TRANSACTED_MODE;
    }
}
