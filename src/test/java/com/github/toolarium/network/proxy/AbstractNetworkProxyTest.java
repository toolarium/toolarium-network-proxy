/*
 * AbstractNetworkProxyTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.toolarium.network.proxy.config.NetworkProxyConfiguration;
import com.github.toolarium.network.proxy.config.NetworkProxyNode;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base test class
 * 
 * @author patrick
 */
public class AbstractNetworkProxyTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkProxyTest.class);
    private static final int PORT_BASE = 10000;
    private static int port = PORT_BASE;
    
    
    /**
     * Create a new configuration
     * 
     * @return the new configuration
     */
    protected NetworkProxyConfiguration newConfiguration() {
        NetworkProxyConfiguration configuration = new NetworkProxyConfiguration();
        configuration.setIoThreads(1);
        configuration.setPort(getNewPort());
        configuration.setBasicAuthentication(null);
        configuration.setHealthPath(null);
        return configuration;
    }

    
    /**
     * Run a network procy configuration
     * 
     * @param configuration the configuration
     * @return the network proxy
     */
    protected NetworkProxy run(NetworkProxyConfiguration configuration) {
        NetworkProxy networkProxy = new NetworkProxy();
        networkProxy.setConfiguration(configuration);
        networkProxy.run();
        return networkProxy;
    }
   

    /**
     * Get new free port
     *
     * @return the port
     */
    protected int getNewPort() {
        return ++port;
    }
    

    /**
     * Create servers
     *
     * @param hostname the hostname
     * @param basePort the base port
     * @param inputResource the resource
     * @param inputPropagatedResource the propageted resource
     * @param staticContent the static content
     * @param amountOfServers the amount of servers
     * @param configuration the configuration
     * @return the undertow servers
     */
    protected List<Undertow> createServers(String hostname, 
                                           int basePort, 
                                           String inputResource,
                                           String inputPropagatedResource,
                                           String staticContent,
                                           int amountOfServers,
                                           NetworkProxyConfiguration configuration) {
        String resource = inputResource;
        if (resource == null || resource.isBlank()) {
            resource = "/";
        }

        String propagatedResource = inputPropagatedResource;
        if (propagatedResource == null || propagatedResource.isBlank()) {
            propagatedResource = resource;
        }
        
        NetworkProxyNode node = configuration.addNetworkProxyNode(null, propagatedResource, null, null);
        List<Undertow> servers = new ArrayList<>();
        for (int i = 0; i < amountOfServers; i++) {
            int port = basePort + i;
            String serverCall = "http://" + hostname + ":" + port + resource;
            final Undertow server = createServer(hostname, port, staticContent + i);
            servers.add(server);
            server.start();
            
            LOG.debug("Add server for resource [" + propagatedResource + "] -> " + serverCall);
            node.addInstance(serverCall);
        }
        
        return servers;
    }

    
    /**
     * Stop servers
     * 
     * @param servers the servers
     */
    protected void stopServers(final List<Undertow> servers) {
        for (Undertow undertow : servers) {
            undertow.stop();
        }
    }

    
    /**
     * Assert servers
     * 
     * @param servers the servers
     * @param intputPropagatedResource the propagated resource
     * @param staticContent the static content
     * @param configuration the configuration
     * @throws IOException In case of an I/O error
     * @throws InterruptedException In case of interruption
     */
    protected void assertServers(final List<Undertow> servers, final String intputPropagatedResource, String staticContent, NetworkProxyConfiguration configuration) throws IOException, InterruptedException {
        String propagatedResource = intputPropagatedResource;
        if (!propagatedResource.endsWith("/")) {
            propagatedResource += "/";
        }

        // verify servers
        for (int i = 0; i < servers.size(); i++) {
            final HttpResponse<String> response = request(configuration.getNetworkProxyNode(propagatedResource).getInstances().get(i));
            assertEquals(response.statusCode(), 200);
            assertEquals(response.body(), staticContent + i);
            HttpHeaders headers = response.headers();
            assertEquals(headers.map().get("content-type").toString(), "[text/plain]");
        }
    }


    /**
     * Create a server
     * 
     * @param hostname the hostname
     * @param port the port
     * @param staticContent the static content of the server
     * @return the server
     */
    protected Undertow createServer(String hostname, int port, String staticContent) {
        return Undertow.builder()
                .addHttpListener(port, hostname)
                .setIoThreads(1)
                .setHandler(new HttpHandler() {
                    /**
                     * @see io.undertow.server.HttpHandler#handleRequest(io.undertow.server.HttpServerExchange)
                     */
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send(staticContent);
                    }
                })
                .build();
    }
    

    /**
     * Request
     *
     * @param uri the uri
     * @return the content
     * @throws InterruptedException In case of interrupt
     * @throws IOException In case of an I/O exception
     */
    protected HttpResponse<String> request(URI uri) throws IOException, InterruptedException {
        LOG.debug("Request " + uri);
        HttpRequest request = HttpRequest.newBuilder(uri)/*.header("accept", "application/json")*/.build();
        HttpClient httpClient = HttpClient.newHttpClient();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
