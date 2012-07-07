package com.jms.client.entity;

public enum TransactionType {

    TRANSACTED,
    NON_TRANSACTED;

    public boolean isTransacted() {
        return this == TransactionType.TRANSACTED;
    }
}
