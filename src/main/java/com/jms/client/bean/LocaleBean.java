package com.jms.client.bean;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import com.jms.client.util.FacesUtil;
import java.io.IOException;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Locale managed bean class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
@ManagedBean(name = "localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

    protected static final Logger LOGGER = Logger.getLogger(LocaleBean.class);
    private static final long serialVersionUID = 1L;
    public static final String BEAN_NAME = "localeBean";
    /**
     * Available application locales
     */
    private static final Map<String, Locale> AVAILABLE_LOCALES = new LinkedHashMap<String, Locale>();
    // application locale
    private Locale locale;
    // user preferred language [from combobox]
    private String selectedLocaleKey;

    static {
        AVAILABLE_LOCALES.put("en", new Locale("en", "US"));
    }

    @PostConstruct
    public void postConstruct() {
        init();
    }

    /**
     * Initialize UI managed bean
     */
    private void init() {
        locale = FacesUtil.getLocale();

        String language = locale.getLanguage().toLowerCase();
        selectedLocaleKey = language;
    }

    @PreDestroy
    public void preDestroy() {
        destroy();
    }

    private void destroy() {
        locale = null;
        selectedLocaleKey = null;
    }

    /**
     * Change application locale
     *
     * @param event the event
     * @throws IOException
     */
    public void changeLocale(AjaxBehaviorEvent event) throws IOException {
        // update application locale by user selected [from combobox]
        locale = AVAILABLE_LOCALES.get(selectedLocaleKey);

        FacesUtil.setLocale(locale);

        // redirected to the same URL (update the localization of the page)
        HttpServletRequest httpServletRequest = FacesUtil.getHttpServletRequest();
        String uri = httpServletRequest.getRequestURI();
        FacesUtil.getExternalContext().redirect(uri);
        FacesUtil.getFacesContext().responseComplete();
    }

    /**
     * Get available locale keys
     *
     * @return available locale keys
     */
    public Object[] getAvailableLocaleKeys() {
        return AVAILABLE_LOCALES.keySet().toArray();
    }

    /**
     * Get current application locale
     *
     * @return current application locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get application time zone
     *
     * @return application time zone
     */
    public TimeZone getTimeZone() {
        return FacesUtil.getTimeZone();
    }

    /**
     * Get selected locale key from the UI
     *
     * @return selected locale key
     */
    public String getSelectedLocaleKey() {
        return selectedLocaleKey;
    }

    /**
     * Set selected locale key (action from UI)
     *
     * @param selectedLocaleKey the selected locale key
     */
    public void setSelectedLocaleKey(String selectedLocaleKey) {
        this.selectedLocaleKey = selectedLocaleKey;
    }
}