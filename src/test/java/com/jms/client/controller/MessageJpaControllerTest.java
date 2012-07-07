package com.jms.client.controller;

import com.jms.client.entity.Message;
import com.jms.client.entity.Property;
import com.jms.client.entity.Property.Type;
import com.jms.client.factory.FakeStaticMessageFactory;
import com.jms.client.core.ServiceLocator;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;

public class MessageJpaControllerTest {

    protected static final Logger LOGGER = Logger.getLogger(MessageJpaControllerTest.class);
    private MessageController jpaController = ServiceLocator.getObject(MessageController.class);

    @Before
    public void setUp() throws Exception {
        jpaController.destroyAll(FakeStaticMessageFactory.CLIENT_ID);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        LOGGER.info("create");
        Message entity = FakeStaticMessageFactory.createMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));
    }

    @Test
    public void testEditSimpleMessageBody() throws Exception {
        LOGGER.info("edit simple body");
        Message entity = FakeStaticMessageFactory.createMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));

        final String ASSERT_BODY = "FIXED BODY VALUE";

        entity.setSimpleBody(ASSERT_BODY);
        entity = jpaController.edit(entity);
        assertEquals("Entity not edited properly", ASSERT_BODY, entity.getSimpleBody());
    }

    @Test
    public void testEditMapMessageBody() throws Exception {
        LOGGER.info("edit map body");
        Message entity = FakeStaticMessageFactory.createMapMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));

        final String ASSERT_KEY = "BODY";
        final String ASSERT_BODY = "FIXED BODY VALUE";
        final Property ASSERT_MAP_ITEM = new Property(ASSERT_KEY, ASSERT_BODY, Type.STRING);

        entity.getMapBodyItems().add(ASSERT_MAP_ITEM);
        entity = jpaController.edit(entity);

        int indexOfMapItem = entity.getMapBodyItems().indexOf(ASSERT_MAP_ITEM);
        assertTrue("Entity not found in the collection", (indexOfMapItem > -1));
        Property foundMapItem = entity.getMapBodyItems().get(indexOfMapItem);
        assertEquals("Entity not edited properly", ASSERT_MAP_ITEM, foundMapItem);
    }

    @Test
    public void testDestroy() throws Exception {
        LOGGER.info("destroy");
        Message entity = FakeStaticMessageFactory.createMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));

        final long ASSERT_REMOVED_ENTITY_ID = entity.getId();

        jpaController.destroy(ASSERT_REMOVED_ENTITY_ID);
        Message foundEntity = jpaController.findEntity(ASSERT_REMOVED_ENTITY_ID);
        assertNull("Entity not removed properly", foundEntity);
    }

    @Test
    public void testDestroyAll() throws Exception {
        LOGGER.info("destroy all");
        Message entity = FakeStaticMessageFactory.createMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));

        final long ASSERT_ENTITIES_SIZE = 0L;

        jpaController.destroyAll(entity.getClientId());
        long size = jpaController.getCount(entity.getClientId());
        assertEquals("Entity not removed properly", ASSERT_ENTITIES_SIZE, size);
    }

    @Test
    public void testFindEntities() throws Exception {
        LOGGER.info("findEntities");
        Message entity = FakeStaticMessageFactory.createMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));

        final long ASSERT_SIZE = 1L;
        List<Message> foundEntities = jpaController.findEntities(entity.getClientId());
        assertEquals("No entities has been found", ASSERT_SIZE, foundEntities.size());
    }

    @Test
    public void testFindEntity() throws Exception {
        LOGGER.info("findEntity");
        Message entity = FakeStaticMessageFactory.createMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));

        final long ASSERT_CREATED_ENTITY_ID = entity.getId();

        Message foundEntity = jpaController.findEntity(ASSERT_CREATED_ENTITY_ID);
        assertNotNull("Entity not found", foundEntity);
    }

    @Test
    public void testGetCount() throws Exception {
        LOGGER.info("getCount");
        Message entity = FakeStaticMessageFactory.createMessage();
        entity = jpaController.create(entity);
        assertTrue("Entity not created properly", (entity.getId() > 0L));

        final long ASSERT_SIZE = 1L;
        long size = jpaController.getCount(entity.getClientId());
        assertEquals("No entities has been found", ASSERT_SIZE, size);
    }
}
