package com.jms.client.util;

import java.util.List;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ApplicationConfigurationHelperTest {

    protected static final Logger LOGGER = Logger.getLogger(ApplicationConfigurationHelperTest.class);

    @Test
    public void testGetRecentInitialContextFactoriesFromTheApplicationConfig() throws Exception {
        LOGGER.info("get recent initial context factories from the application config");
        List<String> recentConnectionFactories = ApplicationConfigurationHelper.getRecentInitialContextFactories();

        LOGGER.info(recentConnectionFactories);
        Assert.assertNotNull("Recent connection factories must not be null", recentConnectionFactories);
    }
}
