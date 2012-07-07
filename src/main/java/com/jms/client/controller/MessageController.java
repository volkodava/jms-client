package com.jms.client.controller;

import com.jms.client.entity.Message;
import java.util.List;

/**
 * Message controller interface
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public interface MessageController {

    /**
     * Create message
     *
     * @param entity the message
     * @return created message
     * @throws Exception
     */
    Message create(Message entity) throws Exception;

    /**
     * Edit message
     *
     * @param entity the message
     * @return edited message
     * @throws Exception
     */
    Message edit(Message entity) throws Exception;

    /**
     * Destroy message by id
     *
     * @param id the id
     * @throws Exception
     */
    void destroy(long id) throws Exception;

    /**
     * Destroy all messages that belong to the client with specific id
     *
     * @param clientId the client id
     * @throws Exception
     */
    void destroyAll(String clientId) throws Exception;

    /**
     * Find entities for specific client
     *
     * @param clientId the client id
     * @return list of client messages
     */
    List<Message> findEntities(String clientId);

    /**
     * Find message by message id
     *
     * @param id the id
     * @return message
     */
    Message findEntity(long id);

    /**
     * Count all clients messages
     *
     * @param clientId the client id
     * @return size of client messages
     */
    long getCount(String clientId);
}
