package com.jms.client.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.Application;

/**
 * Data localization utility class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class ResourceUtil {

    private ResourceUtil() {
    }

    /**
     * Get Current class loader
     *
     * @param defaultObject the object to get class loader
     * @return class loader for the object
     */
    protected static ClassLoader getCurrentClassLoader(Object defaultObject) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = defaultObject.getClass().getClassLoader();
        }
        return loader;
    }

    /**
     * Get I18n Message String
     *
     * @param key the key
     * @return i18n message string
     */
    public static String getI18nMessage(String key) {
        String text = null;

        Application application = FacesUtil.getApplication();
        String messageBundle = application.getMessageBundle();
        Locale locale = FacesUtil.getLocale();

        System.err.println(messageBundle + " ; key=" + key + " ;locale=" + locale);
        text = getMessageResourceString(messageBundle, key, null, locale);

        return text;
    }

    /**
     * Get Default Message Resource String
     *
     * @param key the key
     * @return default message resource string
     */
    public static String getMessageResourceString(String key) {
        return getMessageResourceString("i18n", key, null, Locale.getDefault());
    }

    /**
     * Get Default Message Resource String
     *
     * @param bundleName the bundle name (without .properties)
     * @param key the key
     * @param params the params
     * @param locale the locale
     * @return default message resource string
     */
    public static String getMessageResourceString(String bundleName,
        String key, Object params[], Locale locale) {
        String text = null;
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale,
            getCurrentClassLoader(params));
        if (bundle == null) {
            return text;
        }

        try {
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = String.format("*** key %s not found ***", key);
        }

        if (params != null) {
            MessageFormat mf = new MessageFormat(text, locale);
            text = mf.format(params, new StringBuffer(), null).toString();
        }

        return text;
    }
}
