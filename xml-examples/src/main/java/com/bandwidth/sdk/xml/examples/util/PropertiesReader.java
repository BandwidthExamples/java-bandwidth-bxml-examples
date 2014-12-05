package com.bandwidth.sdk.xml.examples.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class loads the xml-examples.properties files and exposes its values.
 */
public class PropertiesReader {

    private static final Logger logger = Logger.getLogger(ExamplesUtil.class.getName());
    private static final Pattern phoneNumberPattern = Pattern.compile("[+0-9]+");

    public enum CatapultProperties {
        CallOutgoingNumber("catapult.call.outgoing.number", phoneNumberPattern),
        CallIncomingNumber("catapult.call.incoming.number", phoneNumberPattern),
        MessageOutgoingNumber("catapult.message.outgoing.number", phoneNumberPattern),
        MessageIncomingNumber("catapult.message.incoming.number", phoneNumberPattern);

        private String name;
        private Pattern validMatch;

        private CatapultProperties(String name, Pattern validMatch) {
            this.name = name;
            this.validMatch = validMatch;
        }

        public String getName() {
            return this.name;
        }

        public boolean isValid(String value) {
            return validMatch.matcher(value).find();
        }
    }

    private final Properties properties = new Properties();

    public PropertiesReader() {
        try {
            InputStream input = getClass().getResourceAsStream("/xml-examples.properties");
            properties.load(input);
        } catch (IOException e) {
            logger.severe(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Extracts a property value from the environment.
     *
     * @param property The property key
     * @return The property value
     */
    public String getCatapultProperty(CatapultProperties property) {
        String value = properties.getProperty(property.getName());
        if (property.isValid(value)) {
            return value;
        }

        throw new IllegalArgumentException(String.format(
                "The value '%s' is invalid for the property '%s'", value, property.getName()));
    }
}
