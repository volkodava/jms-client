package com.jms.client.controller;

import com.jms.client.entity.Message;
import java.util.*;
import org.apache.commons.lang.Validate;

/**
 * In memory message controller class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class InMemoryMessageController extends BaseController implements MessageController {

    private InMemoryMessageController() {
    }

    public static MessageController getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {

        private static final MessageController INSTANCE = new InMemoryMessageController();
    }

    /**
     * Create message
     *
     * @param entity the message
     * @return created message
     * @throws Exception
     */
    public Message create(Message entity) throws Exception {
        Validate.notNull(entity, "Entity must not be null");

        preAction(entity);

        entity.setId(getRandomLong());
        messages.add(entity);

        postAction(entity);

        return entity;
    }

    /**
     * Edit message
     *
     * @param entity the message
     * @return edited message
     * @throws Exception
     */
    public Message edit(Message entity) throws Exception {
        Validate.notNull(entity, "Entity must not be null");

        int foundIndex = messages.indexOf(entity);
        Validate.isTrue((foundIndex >= 0), "Entity not found");

        preAction(entity);

        messages.remove(foundIndex);
        messages.add(entity);

        postAction(entity);

        return entity;
    }

    /**
     * Destroy message by id
     *
     * @param id the id
     * @throws Exception
     */
    public void destroy(long id) throws Exception {
        Collections.sort(messages);
        int foundIndex = Collections.binarySearch(messages, new Message(id));
        Validate.isTrue((foundIndex >= 0), "Entity not found");

        messages.remove(foundIndex);
    }

    /**
     * Destroy all messages that belong to the client with specific id
     *
     * @param clientId the client id
     * @throws Exception
     */
    public void destroyAll(String clientId) throws Exception {
        for (Iterator<Message> it = messages.iterator(); it.hasNext();) {
            Message message = it.next();
            if (message.getClientId().equals(clientId)) {
                it.remove();
            }
        }
    }

    /**
     * Find entities for specific client
     *
     * @param clientId the client id
     * @return list of client messages
     */
    public List<Message> findEntities(String clientId) {
        List<Message> list = new ArrayList<Message>();

        for (Message message : messages) {
            if (message.getClientId().equals(clientId)) {
                list.add(message);
            }
        }

        return list;
    }

    /**
     * Find message by message id
     *
     * @param id the id
     * @return message
     */
    public Message findEntity(long id) {
        Collections.sort(messages);
        int foundIndex = Collections.binarySearch(messages, new Message(id));

        if (foundIndex < 0) {
            return null;
        }

        Message found = messages.get(foundIndex);

        return found;
    }

    /**
     * Count all clients messages
     *
     * @param clientId the client id
     * @return size of client messages
     */
    public long getCount(String clientId) {
        int size = 0;
        for (Message message : messages) {
            if (message.getClientId().equals(clientId)) {
                size++;
            }
        }

        return size;
    }
}
