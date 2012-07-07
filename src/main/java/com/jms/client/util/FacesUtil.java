package com.jms.client.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.Validate;

/**
 * JSF utility class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class FacesUtil {

    private FacesUtil() {
    }

    /**
     * Get Application Context.
     *
     * @return application context
     */
    public static Application getApplication() {
        FacesContext context = getFacesContext();

        Validate.notNull(context, "FacesContext must not be null");

        Application application = context.getApplication();

        Validate.notNull(application, "Application must not be null");

        return application;
    }

    /**
     * Get External Context
     *
     * @return external context
     */
    public static ExternalContext getExternalContext() {
        FacesContext facesContext = getFacesContext();

        Validate.notNull(facesContext, "FacesContext must not be null");

        ExternalContext externalContext = facesContext.getExternalContext();

        Validate.notNull(externalContext, "ExternalContext must not be null");

        return externalContext;
    }

    /**
     * Check, is web application has been started
     *
     * @return is web application has been started
     */
    public static boolean isWebApplicationStarted() {
        FacesContext facesContext = getFacesContext();

        return facesContext != null;
    }

    /**
     * Get Faces Context
     *
     * @return faces context
     */
    public static FacesContext getFacesContext() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        return facesContext;
    }

    /**
     * Get Application Web Http Session
     *
     * @return application web http session
     */
    public static HttpSession getHttpSession() {
        HttpServletRequest request = getHttpServletRequest();
        HttpSession session = request.getSession(false);

        Validate.notNull(session, "Http session must not be null");

        return session;
    }

    /**
     * Get Application Http Servlet Request
     *
     * @return application http servlet request
     */
    public static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = (HttpServletRequest) getRequest();

        return request;
    }

    /**
     * Get Servlet Request
     *
     * @return servlet request
     */
    public static Object getRequest() {
        ExternalContext externalContext = getExternalContext();
        Object request = externalContext.getRequest();

        Validate.notNull(request, "Request must not be null");

        return request;
    }

    /**
     * Get Faces View Root
     *
     * @return faces view root
     */
    public static UIViewRoot getViewRoot() {
        FacesContext facesContext = getFacesContext();

        Validate.notNull(facesContext, "FacesContext must not be null");

        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        Validate.notNull(uiViewRoot, "UIViewRoot must not be null");

        return uiViewRoot;
    }

    /**
     * Set Faces locale
     *
     * @param locale the locale
     */
    public static void setLocale(Locale locale) {
        Validate.notNull(locale, "Locale must not be null");

        UIViewRoot uiViewRoot = getViewRoot();
        uiViewRoot.setLocale(locale);
    }

    /**
     * Get Application locale
     *
     * @return application locale
     */
    public static Locale getLocale() {
        Locale locale = Locale.getDefault();;
        UIViewRoot uiViewRoot = getViewRoot();
        HttpServletRequest request = FacesUtil.getHttpServletRequest();

        if (uiViewRoot != null) {
            Locale newLocale = uiViewRoot.getLocale();

            locale = (newLocale != null) ? newLocale : locale;
        }
        if (request != null && locale == null) {
            Locale newLocale = request.getLocale();

            locale = (newLocale != null) ? newLocale : locale;
        }

        Validate.notNull(locale, "Locale must not be null");

        return locale;
    }

    /**
     * Get application time zone
     *
     * @return application time zone
     */
    public static TimeZone getTimeZone() {
        return Calendar.getInstance().getTimeZone();
    }

    /**
     * Set application time zone
     *
     * @param timeZone the time zone
     */
    public static void setTimeZone(TimeZone timeZone) {
        Validate.notNull(timeZone, "TimeZone must not be null");

        TimeZone.setDefault(timeZone);
    }

    /**
     * Get Client Ip Address
     *
     * @return client ip address
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getHttpServletRequest();

        Validate.notNull(request, "Request must not be null");

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-FORWARDED-FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}