/*
 * INetworkProxyNode.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.config;

import java.net.URI;
import java.util.List;


/**
 * Define the network proxy node
 * 
 * @author patrick
 */
public interface INetworkProxyNode {
    
    /**
     * Get the network proxy node name
     *
     * @return the name of the network proxy node
     */
    String getName();

    
    /**
     * Get the resource path
     *
     * @return the resource path
     */
    String getResource();

    
    /**
     * Get the supported methods
     *
     * @return the supported methods
     */
    List<String> getMethods();

    
    /**
     * Get the instances
     *
     * @return the instances
     */
    List<URI> getInstances();

}
