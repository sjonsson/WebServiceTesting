package com.sjonsson.demo.restassured;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author sjonsson
 */
public class TestProperties {

    private static final Properties properties = new Properties();
    
    static {
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String get(String key) {
        return properties.getProperty(key);
    }
}
