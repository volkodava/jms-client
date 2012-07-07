package com.jms.client.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.apache.log4j.Logger;

/**
 * JSF Date converter class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class DateConverter implements Converter {

    protected static final Logger LOGGER = Logger.getLogger(DateConverter.class);

    /**
     * Converts a field value into a String representation of the Date object
     *
     * @param fc the faces context
     * @param uic the UI component
     * @param value the value to convert
     * @return field value as the String representation of the Date object
     */
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        return value;
    }

    /**
     * Converts a model value into a String representation of the Date object
     *
     * @param fc the faces context
     * @param uic the UI component
     * @param object the object to convert
     * @return model value as the String representation of the Date object
     */
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object == null) {
            return null;
        }

        return object.toString();
    }
}
