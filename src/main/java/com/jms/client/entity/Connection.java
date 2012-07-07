package com.jms.client.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Connection implements Serializable {

    private String initialContextFactory;
    private String connectionFactoryName;
    private String url;
    private Client client;
    private Status status;
    private List<Property> propertyList = new ArrayList<Property>();

    public Connection() {
    }

    public Connection(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public String getInitialContextFactory() {
        return initialContextFactory;
    }

    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    @Override
    public String toString() {
        return "Connection{" + "initialContextFactory=" + initialContextFactory + ", connectionFactoryName=" + connectionFactoryName + ", url=" + url + ", client=" + client + ", status=" + status + ", propertyList=" + propertyList + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Connection other = (Connection) obj;
        if ((this.initialContextFactory == null) ? (other.initialContextFactory != null) : !this.initialContextFactory.equals(other.initialContextFactory)) {
            return false;
        }
        if ((this.connectionFactoryName == null) ? (other.connectionFactoryName != null) : !this.connectionFactoryName.equals(other.connectionFactoryName)) {
            return false;
        }
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.initialContextFactory != null ? this.initialContextFactory.hashCode() : 0);
        hash = 73 * hash + (this.connectionFactoryName != null ? this.connectionFactoryName.hashCode() : 0);
        hash = 73 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }
}
