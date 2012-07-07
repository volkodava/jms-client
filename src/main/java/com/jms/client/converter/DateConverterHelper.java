package com.jms.client.converter;

import com.jms.client.util.ResourceUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Date converter helper class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class DateConverterHelper {

    protected static final Logger LOGGER = Logger.getLogger(DateConverterHelper.class);

    private DateConverterHelper() {
    }

    /**
     * Covert long representation of the date to the Date
     *
     * @param value the long value of the Date
     * @return Date
     */
    public static Date convertToDate(long value) {
        return new Date(value);
    }

    /**
     * Covert string representation of the date to the Date by default
     * application formatter
     *
     * @param value the string value of the Date
     * @return Date
     */
    public static Date convertToDate(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        Date result = null;
        String dateFormat = ResourceUtil.getMessageResourceString("application.pattern.timestamp");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            result = simpleDateFormat.parse(value);
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return result;
    }

    /**
     * Convert Date to string by default application formatter
     *
     * @param value the date
     * @return string representation of the date
     */
    public static String convertToString(Date value) {
        if (value == null) {
            return null;
        }

        String dateFormat = ResourceUtil.getMessageResourceString("application.pattern.timestamp");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String result = simpleDateFormat.format(value);

        return result;
    }

    /**
     * Convert long Date representation to string by default application
     * formatter
     *
     * @param value the date in the long format
     * @return string representation of the date
     */
    public static String convertToString(long value) {
        Date date = convertToDate(value);

        String dateFormat = ResourceUtil.getMessageResourceString("application.pattern.timestamp");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String result = simpleDateFormat.format(date);

        return result;
    }

    /**
     * Convert String Date representation to long by default application
     * formatter
     *
     * @param value the String Date representation
     * @return long representation of the date
     */
    public static long convertToLong(String value) {
        Validate.notEmpty(value, "String date representation must not be null or empty");

        Date result = null;
        String dateFormat = ResourceUtil.getMessageResourceString("application.pattern.timestamp");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            result = simpleDateFormat.parse(value);
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new IllegalArgumentException(ex);
        }

        return result.getTime();
    }
}
