package com.jms.client.entity;

public class HeaderProperty extends BaseEntity {

    private Type type;
    private String value;

    public HeaderProperty() {
    }

    public HeaderProperty(Type type) {
        this.type = type;
    }

    public HeaderProperty(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public static enum Type {

        JMS_EXPIRATION(Property.Type.DATE_TIME),
        JMS_REDELIVERED(Property.Type.BOOLEAN),
        JMS_PRIORITY(Property.Type.INT),
        JMS_REPLY_TO(Property.Type.STRING),
        JMS_CORRELATION_ID(Property.Type.STRING);
        private Property.Type propertyType;

        private Type(Property.Type propertyType) {
            this.propertyType = propertyType;
        }

        public Property.Type getPropertyType() {
            return propertyType;
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "HeaderProperty{" + "type=" + type + ", value=" + value + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HeaderProperty other = (HeaderProperty) obj;
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
