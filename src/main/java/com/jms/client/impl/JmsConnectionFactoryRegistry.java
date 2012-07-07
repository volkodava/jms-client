package com.jms.client.impl;

import com.jms.client.entity.Connection;
import com.jms.client.entity.Property;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.commons.lang.Validate;

/**
 * JMS connection factory registry class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class JmsConnectionFactoryRegistry {

    protected static final ConcurrentMap<String, javax.jms.ConnectionFactory> CONNECTION_FACTORY_REPOSITORY = new ConcurrentHashMap<String, javax.jms.ConnectionFactory>();

    /**
     * Create or get jms connection factory from the registry
     *
     * @param connection the connection from the UI
     * @return jms connection factory from the registry
     * @throws Exception
     */
    public static javax.jms.ConnectionFactory getConnectionFactory(Connection connection) throws Exception {
        Validate.notNull(connection, "Connection must not be null");

        String key = connection.getClient().getClientId();
        ConnectionFactory result = CONNECTION_FACTORY_REPOSITORY.get(key);

        if (result == null) {
            ConnectionFactory newConnectionFactory = createConnectionFactory(connection);
            result = CONNECTION_FACTORY_REPOSITORY.putIfAbsent(key, newConnectionFactory);
            if (result == null) {
                result = newConnectionFactory;
            }
        }

        return result;
    }

    /**
     * Create jms connection factory
     *
     * @param connection the connection from the UI
     * @return jms connection factory
     * @throws Exception
     */
    private static ConnectionFactory createConnectionFactory(Connection connection) throws Exception {
        Properties jmsProperties = new Properties();
        jmsProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            connection.getInitialContextFactory());
        jmsProperties.setProperty(Context.PROVIDER_URL, connection.getUrl());
        jmsProperties.setProperty(Context.SECURITY_PRINCIPAL, connection.getClient().getName());
        jmsProperties.setProperty(Context.SECURITY_CREDENTIALS, connection.getClient().getPassword());

        // set advanced properties
        for (Property property : connection.getPropertyList()) {
            jmsProperties.setProperty(property.getName(), property.getValue());
        }
        Context ctx = new InitialContext(jmsProperties);

        ConnectionFactory cf = (ConnectionFactory) ctx.lookup(connection.getConnectionFactoryName());

        return cf;
    }
}
