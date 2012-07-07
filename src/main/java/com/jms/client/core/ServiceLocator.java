package com.jms.client.core;

import com.jms.client.controller.*;
import org.apache.commons.lang.Validate;

/**
 * Service locator class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class ServiceLocator {

    /**
     * Get object by the class
     *
     * @param <T> the generic type of the object
     * @param clazz the class of the object
     * @return instance by the object class
     */
    public static <T> T getObject(Class<T> clazz) {
        Validate.notNull(clazz, "Class type must not be null");

        if (MessageController.class.isAssignableFrom(clazz)) {
            return (T) InMemoryMessageController.getInstance();
        }

        throw new IllegalArgumentException("Class type not supported: " + clazz);
    }
}
