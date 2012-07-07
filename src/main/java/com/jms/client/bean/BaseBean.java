package com.jms.client.bean;

import com.jms.client.core.UiLogLevel;
import com.jms.client.util.FacesUtil;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Base managed bean class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public abstract class BaseBean {

    protected static final Logger LOGGER = Logger.getLogger(BaseBean.class);
    /**
     * UI status area message
     */
    private StringBuffer statusAreaMessages = new StringBuffer();

    /**
     * Application index page address
     *
     * @return index page of the application
     */
    public abstract String index();

    /**
     * Get UI status area messages
     *
     * @return UI status area messages
     */
    public String getStatusAreaMessages() {
        return statusAreaMessages.toString();
    }

    /**
     * Add message to the UI status area message
     *
     * @param message the message
     * @param level the level of the message
     */
    public void addStatusAreaMessage(String message, UiLogLevel level) {
        Validate.notEmpty(message, "Message must not be null or empty");
        Validate.notNull(level, "Message level must not be null");

        // add status message
        String statusMessage = getStatusMessage(message, level);
        LOGGER.info(statusMessage);
        // if debug, not show in the GUI
        if (level == UiLogLevel.DEBUG) {
            return;
        }

        statusAreaMessages.insert(0, statusMessage);
    }

    /**
     * Add the message to the UI
     *
     * @param message the message
     * @param level the level of the message
     */
    public void addMessage(String message, UiLogLevel level) {
        Validate.notEmpty(message, "Message must not be null or empty");
        Validate.notNull(level, "Message level must not be null");

        // add status message
        addStatusAreaMessage(message, level);

        // add message
        switch (level) {
            case INFO:
                addInfo(message);
                break;
            case WARN:
                addWarn(message);
                break;
            case ERROR:
                addError(message);
                break;
            case FATAL:
                addFatal(message);
                break;
        }
    }

    /**
     * Add info message to the UI
     *
     * @param message the message
     */
    private void addInfo(String message) {
        FacesContext context = FacesUtil.getFacesContext();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
            message, null));
    }

    /**
     * Add warning message to the message
     *
     * @param message the message
     */
    private void addWarn(String message) {
        FacesContext context = FacesUtil.getFacesContext();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
            message, null));
    }

    /**
     * Add error message to the UI
     *
     * @param message the message
     */
    private void addError(String message) {
        FacesContext context = FacesUtil.getFacesContext();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
            message, null));
    }

    /**
     * Add fatal message to the UI
     *
     * @param message the message
     */
    private void addFatal(String message) {
        FacesContext context = FacesUtil.getFacesContext();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
            message, null));
    }

    /**
     * Create status message by message string and level
     *
     * @param message the message
     * @param level the level
     * @return created status message by message string and level
     */
    private String getStatusMessage(String message, UiLogLevel level) {
        return String.format("%1$s --- [%2$s]   %3$s\n", new Date(), level.name(), message);
    }
}
