package com.jms.client.converter;

import java.util.Date;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;

public class DateConverterHelperTest {

    protected static final Logger LOGGER = Logger.getLogger(DateConverterHelperTest.class);
    private static final String ASSERT_DATE_STRING = "30.06.2012 12:44:07 EEST";
    private static final long ASSERT_DATE_LONG = 1341049447000L;
    private static final Date ASSERT_DATE = new Date(ASSERT_DATE_LONG);

    @Test
    public void testConvertDateToLong() throws Exception {
        LOGGER.info("convert date string to long value");

        long value = DateConverterHelper.convertToLong(ASSERT_DATE_STRING);
        Assert.assertEquals("Result is not as expected.", ASSERT_DATE_LONG, value);
    }

    @Test
    public void testConvertToString() throws Exception {
        LOGGER.info("convert long value to date string");

        String value = DateConverterHelper.convertToString(ASSERT_DATE_LONG);
        Assert.assertEquals("Result is not as expected.", ASSERT_DATE_STRING, value);
    }

    @Test
    public void testConvertDateToStringToLongAndBackToDate() throws Exception {
        LOGGER.info("convert date to string to long and back to date");

        String stringValue = DateConverterHelper.convertToString(ASSERT_DATE);
        long longValue = DateConverterHelper.convertToLong(stringValue);
        Date resultDate = DateConverterHelper.convertToDate(longValue);

        Assert.assertEquals("Result is not as expected.", ASSERT_DATE, resultDate);
    }
}
