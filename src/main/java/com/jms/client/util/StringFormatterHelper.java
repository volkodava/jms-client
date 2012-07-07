package com.jms.client.util;

import com.jms.client.entity.HeaderProperty;
import com.jms.client.entity.Property;
import com.jms.client.util.ResourceUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 * String formatter helper class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class StringFormatterHelper {

    private StringFormatterHelper() {
    }

    /**
     * Convert and format map with delimiter to string
     *
     * @param map the map
     * @param delimiter the delimiter
     * @return formatted map as a string
     */
    public static String formatMap(Map map, String delimiter) {
        Validate.notNull(map, "Map must not be null");
        Validate.notNull(delimiter, "Entry delimiter must not be null");

        Set<Entry> entrySet = map.entrySet();
        StringBuilder sb = new StringBuilder();
        for (Iterator<Entry> it = entrySet.iterator(); it.hasNext();) {
            Entry entry = it.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());

            sb.append(format(key, value));
            sb.append(delimiter);
        }

        return sb.toString();
    }

    /**
     * Format and convert properties as a string
     *
     * @param properties the properties
     * @param delimiter the delimiter
     * @return formatted properties as a string
     */
    public static String formatProperties(List<Property> properties, String delimiter) {
        Validate.notNull(properties, "List of properties must not be null");
        Validate.notNull(delimiter, "Entry delimiter must not be null");

        StringBuilder sb = new StringBuilder();
        for (Property property : properties) {
            sb.append(format(property.getName(), property.getValue()));
            sb.append(delimiter);
        }

        return sb.toString();
    }

    /**
     * Format and convert headers properties as a string
     *
     * @param headerProperties the header properties
     * @param delimiter the delimiter
     * @return formatted header properties as a string
     */
    public static String formatHeaderProperties(List<HeaderProperty> headerProperties, String delimiter) {
        Validate.notNull(headerProperties, "List of header properties must not be null");
        Validate.notNull(delimiter, "Entry delimiter must not be null");

        StringBuilder sb = new StringBuilder();
        for (HeaderProperty header : headerProperties) {
            String name = ResourceUtil.getMessageResourceString(header.getType().name());
            sb.append(format(name, header.getValue()));
            sb.append(delimiter);
        }

        return sb.toString();
    }

    /**
     * Format and convert key, value pairs to a string
     *
     * @param key the key
     * @param value the value
     * @return formatted key, value pairs as a string
     */
    public static String format(String key, String value) {
        return String.format("%-60s%s", key, value);
    }
}
