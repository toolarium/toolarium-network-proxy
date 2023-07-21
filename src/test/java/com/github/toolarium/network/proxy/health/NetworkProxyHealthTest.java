/*
 * NetworkProxyHealthTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.health;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.proxy.AbstractNetworkProxyTest;
import com.github.toolarium.network.proxy.config.NetworkProxyConfiguration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;


/**
 * Network proxy health tests
 * 
 * @author patrick
 */
public class NetworkProxyHealthTest extends AbstractNetworkProxyTest {
    
    /**
     * Test enabled health check.
     */
    @Test void testValidHealthCheck() {
        NetworkProxyConfiguration configuration = newConfiguration();
        configuration.setHealthPath("/q/health");
        run(configuration);
        
        assertTrue(configuration.hasHealthCheck());
        RestAssured.port = configuration.getPort();
        given().when().get(configuration.getHealthPath()).then().statusCode(200).body(is("{ \"status\": \"UP\" }"));
    }


    /**
     * Test disable health check.
     */
    @Test void testDisabledHealthCheck() {
        NetworkProxyConfiguration configuration = newConfiguration();
        configuration.setHealthPath("");
        run(configuration);
        
        assertFalse(configuration.hasHealthCheck());
        RestAssured.port = configuration.getPort();
        given().when().get(configuration.getHealthPath()).then().statusCode(404);

        NetworkProxyConfiguration newConfiguration = newConfiguration();
        newConfiguration.setHealthPath(null);
        
        run(newConfiguration);
        assertFalse(newConfiguration.hasHealthCheck());
    }
}
