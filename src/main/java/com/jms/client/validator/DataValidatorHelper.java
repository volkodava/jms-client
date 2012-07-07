package com.jms.client.validator;

import com.jms.client.util.ResourceUtil;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

public class DataValidatorHelper {

    protected static final Logger LOGGER = Logger.getLogger(DataValidatorHelper.class);

    private DataValidatorHelper() {
    }

    /**
     * Check if value is Date
     *
     * @param value the value
     * @param format the format
     * @return result of check the value to Date
     */
    public static boolean isDate(String value, String format) {
        Validate.notEmpty(format, "Date format must not be null or empty");
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        try {
            new SimpleDateFormat(format).parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if value is Date
     *
     * @param value the value
     * @return result of check the value to Date
     */
    public static boolean isDate(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        try {
            String dateFormat = ResourceUtil.getMessageResourceString("application.pattern.timestamp");
            new SimpleDateFormat(dateFormat).parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
