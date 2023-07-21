/*
 * RouteHandler.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.handler.route;

import com.github.toolarium.network.proxy.config.INetworkProxyConfiguration;
import com.github.toolarium.network.proxy.config.INetworkProxyNode;
import com.github.toolarium.network.proxy.handler.auth.BasicAuthenticationHttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.util.Methods;
import java.net.URI;


/**
 * Defines the route handler
 * 
 * @author patrick
 */
public final class RouteHandler {
    private static final String STAR = "*";


    /**
     * Constructor for RouteHandler
     */
    private RouteHandler() {
    }
    

    /**
     * Add basic authentication
     *
     * @param configuration the configuration
     * @param routingHandler the routing handler
     * @return the handler
     */
    public static RoutingHandler addHandler(INetworkProxyConfiguration configuration, RoutingHandler routingHandler) {
        
        for (INetworkProxyNode node : configuration.getNetworkProxyNodeList()) {
            String resourcePath = node.getResource();
            String resourcePathFilter = resourcePath;
            if (resourcePath == null || resourcePath.isBlank()) {
                resourcePath = "/";
                resourcePathFilter = resourcePath;
            } else {
                resourcePathFilter += STAR;
            }

            LoadBalancingProxyClient loadBalancer = new LoadBalancingProxyClient()
                    .setConnectionsPerThread(configuration.getConnectionsByThread());
            for (URI uri : node.getInstances()) {
                loadBalancer.addHost(uri, resourcePath);
            }

            ProxyHandler proxyHandler = ProxyHandler.builder().setProxyClient(loadBalancer).setMaxRequestTime(configuration.getMaxRequestTime()).build();
            for (String method : node.getMethods()) {
                routingHandler.add(Methods.fromString(method.trim()), resourcePath, BasicAuthenticationHttpHandler.addHandler(configuration, proxyHandler));
                
                if (!"/".equals(resourcePath)) {
                    routingHandler.add(Methods.fromString(method.trim()), resourcePathFilter, BasicAuthenticationHttpHandler.addHandler(configuration, proxyHandler));
                }
            }
        }
        
        return routingHandler;
    }
}
