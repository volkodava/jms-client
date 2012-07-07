package com.jms.client.util;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Application configuration helper class
 *
 * @author <a href="mailto:volkodavav@gmail.com">Anatolii Volkodav</a>
 */
public class ApplicationConfigurationHelper {

    /**
     * Application config name
     */
    public static final String APPLICATION_CONFIG = "applicationConfig.xml";
    /**
     * Recent connections factories xpath
     */
    private static final String RECENT_CONNECTIONS_FACTORIES_XPATH = "/application/connections/recents/initialContextFactories/initialContextFactory/@value";

    private ApplicationConfigurationHelper() {
    }

    /**
     * Get recent initial context factories list
     *
     * @return recent initial context factories list
     */
    public static List<String> getRecentInitialContextFactories() {
        List<String> resultList = null;

        try {
            InputStream applicationConfigStream = getApplicationConfig();

            Validate.notNull(applicationConfigStream, "Application config stream must not be null");

            resultList = new ArrayList<String>();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(applicationConfigStream);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(RECENT_CONNECTIONS_FACTORIES_XPATH);

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeValue = nodes.item(i).getNodeValue();
                resultList.add(nodeValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }

    /**
     * Get application config input stream
     *
     * @return application config input stream
     */
    private static InputStream getApplicationConfig() {
        try {
            URL resource = findApplicationConfig();

            if (resource == null) {
                throw new IllegalStateException("Application config could not be found in the classpath: " + APPLICATION_CONFIG);
            }

            InputStream resourceAsStream = resource.openStream();

            return resourceAsStream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find application config in the application classpath
     *
     * @return application config in the application classpath
     */
    private static URL findApplicationConfig() {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            URL resource = ClassLoader.class.getResource(APPLICATION_CONFIG);
            if (resource == null) {
                resource = ClassLoader.class.getResource("/" + APPLICATION_CONFIG);
            }
            if (resource == null) {
                resource = ClassLoader.class.getResource("/WEB-INF/classes/" + APPLICATION_CONFIG);
            }

            // try to find with thread context class loader
            if (resource == null) {
                resource = contextClassLoader.getResource(APPLICATION_CONFIG);
            }
            if (resource == null) {
                resource = contextClassLoader.getResource("/" + APPLICATION_CONFIG);
            }
            if (resource == null) {
                resource = contextClassLoader.getResource("/WEB-INF/classes/" + APPLICATION_CONFIG);
            }

            // if web application has been started
            if (FacesUtil.isWebApplicationStarted()) {
                ExternalContext externalContext = FacesUtil.getExternalContext();

                if (resource == null && externalContext != null) {
                    resource = externalContext.getResource(APPLICATION_CONFIG);
                }
                if (resource == null && externalContext != null) {
                    resource = externalContext.getResource("/" + APPLICATION_CONFIG);
                }
                if (resource == null && externalContext != null) {
                    resource = externalContext.getResource("/WEB-INF/classes/" + APPLICATION_CONFIG);
                }
            }

            return resource;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
