/*
 * NetworkProxyBasicAuthTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.auth;

import static io.restassured.RestAssured.given;

import com.github.toolarium.network.proxy.AbstractNetworkProxyTest;
import com.github.toolarium.network.proxy.config.NetworkProxyConfiguration;
import io.restassured.RestAssured;
import io.undertow.Undertow;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test the basic authentication
 * 
 * @author patrick
 */
public class NetworkProxyBasicAuthTest extends AbstractNetworkProxyTest {

    /**
     * Test basic authentication.
     * 
     * @throws InterruptedException In case of interruption 
     * @throws IOException In case of an I/O error 
     */
    @Test void testBasicAuth() throws IOException, InterruptedException {
        NetworkProxyConfiguration configuration = newConfiguration();
        configuration.setHealthPath("/q/health");
        configuration.setBasicAuthentication("user:password");
        
        final String serverName = "server-basicauth";
        final String propagatedResource = "/";
        final List<Undertow> servers = createServers("localhost", 9000, "/", propagatedResource, serverName, 4, configuration);
        assertServers(servers, propagatedResource, serverName, configuration);

        run(configuration);

        RestAssured.port = configuration.getPort();
        given().when().get(propagatedResource).then().statusCode(401);
        given().auth().basic("user", "password").when().get(propagatedResource).then().statusCode(200);
    }
}
