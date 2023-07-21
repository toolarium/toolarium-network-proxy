/*
 * NetworkProxyBaseTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.network.proxy.config.NetworkProxyConfiguration;
import io.restassured.RestAssured;
import io.undertow.Undertow;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Test;


/**
 * Network proxy general tests.
 * 
 * @author patrick
 */
public class NetworkProxyBaseTest extends AbstractNetworkProxyTest {

    /**
     * Test default behaviour.
     * 
     * @throws InterruptedException In case of an interrupt
     * @throws IOException In case of an I/O error
     */
    @Test void testDefaultBehaviour() throws IOException, InterruptedException {

        // create configuration
        NetworkProxyConfiguration configuration = newConfiguration();
        
        // create content servers
        final String serverName1 = "server-a";
        final String propagatedResource1 = "/abc";
        final List<Undertow> servers1 = createServers("localhost", 9000, "/", propagatedResource1, serverName1, 4, configuration);
        
        final String serverName2 = "server-b";
        final String propagatedResource2 = "/";
        final List<Undertow> servers2 = createServers("localhost", 9100, "/h", propagatedResource2, serverName2, 2, configuration);
        
        try {
            assertServers(servers1, propagatedResource1, serverName1, configuration);
            assertServers(servers2, propagatedResource2, serverName2, configuration);
            
            // run network proxy
            run(configuration);

            RestAssured.port = configuration.getPort();
            given().when().get().then().statusCode(200);
            assertTrue(given().when().get().getBody().asPrettyString().startsWith(serverName2));
            given().when().get(propagatedResource2).then().statusCode(200);
            assertTrue(given().when().get(propagatedResource2).getBody().asPrettyString().startsWith(serverName2));
            assertTrue(given().when().get(propagatedResource1 + "/").getBody().asPrettyString().startsWith(serverName1));
            assertTrue(given().when().get(propagatedResource1 + "/cc/ggg").getBody().asPrettyString().startsWith(serverName1));
            //TODO:  assertTrue(given().when().get(propagatedResource1).getBody().asPrettyString().startsWith(serverName1));

            // verify servers
            for (int i = 0; i < 100; i++) {
                String proxy = "http://localhost:" + configuration.getPort() + propagatedResource1 + "/";
                final HttpResponse<String> response = request(URI.create(proxy));
                assertEquals(response.statusCode(), 200);
                assertTrue(response.body().startsWith(serverName1));
                HttpHeaders headers = response.headers();
                assertEquals(headers.map().get("content-type").toString(), "[text/plain]");

                String proxy2 = "http://localhost:" + configuration.getPort() + propagatedResource2;
                final HttpResponse<String> response2 = request(URI.create(proxy2));
                assertEquals(response2.statusCode(), 200);
                assertTrue(response2.body().startsWith(serverName2));
                HttpHeaders headers2 = response.headers();
                assertEquals(headers2.map().get("content-type").toString(), "[text/plain]");
            }

        } finally {    
            stopServers(servers1);
            stopServers(servers2);
        }
    }


    /**
     * Test network proxy name.
     */
    @Test void testNetworkProxyName() {
        NetworkProxyConfiguration configuration = new NetworkProxyConfiguration();
        configuration.setPort(getNewPort());
        configuration.setIoThreads(1);
        configuration.setNetworkProxyName("My Network Proxy");
        
        NetworkProxy networkProxy = new NetworkProxy();
        networkProxy.setConfiguration(configuration);
        networkProxy.run();
        
        assertTrue(configuration.hasHealthCheck());
        RestAssured.port = configuration.getPort();
        given().when().get(configuration.getHealthPath()).then().statusCode(200).body(is("{ \"status\": \"UP\" }"));
    }
}
