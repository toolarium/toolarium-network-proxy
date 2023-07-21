/*
 * NetworkProxyNode.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.config;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Implements the {@link INetworkProxyNode}.
 * 
 * @author patrick
 */
public class NetworkProxyNode implements INetworkProxyNode, Serializable {
    private static final long serialVersionUID = -4294486282636369123L;
    private static final List<String> DEFAULT_METHODS = Arrays.asList("GET", "PATCH", "PUT", "POST", "DELETE");
    private String name;
    private String resource;
    private List<String> methods;
    private List<URI> instances;

    
    /**
     * Constructor for NetworkProxyNode
     */
    public NetworkProxyNode() {
        this(null, null, null, null);
    }

    
    /**
     * Constructor for NetworkProxyNode
     * 
     * @param resource the resource
     * @param instance the instance
     */
    public NetworkProxyNode(String resource, URI instance) {
        this(null, resource, null, null);
        
        if (instance != null) {
            addInstance(instance);
        }
    }
    
    
    /**
     * Constructor for NetworkProxyNode
     * 
     * @param name the name
     * @param resource the resource
     * @param methods the methods
     * @param instances the instances
     */
    public NetworkProxyNode(String name, String resource, List<String> methods, List<URI> instances) {
        this.resource = resource;
        if (this.resource == null || this.resource.isBlank()) {
            this.resource = "/";
        }

        this.name = name;
        if (this.name == null || this.name.isBlank()) {
            this.name = resource;
        }
        
        this.methods = methods;
        if (this.methods == null || this.methods.isEmpty()) {
            this.methods = DEFAULT_METHODS;
        }
        
        this.instances = instances;
        if (this.instances == null) {
            this.instances = new ArrayList<>();
        }
    }

    
    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyNode#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    
    
    /**
     * Set the name
     *
     * @param name the name
     * @return the network proxy node
     */
    public NetworkProxyNode setName(String name) {
        this.name = name;
        return this;
    }
    

    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyNode#getResource()
     */
    @Override
    public String getResource() {
        return resource;
    }
    
    
    /**
     * Set the resource
     *
     * @param resource the resource
     * @return the network proxy node
     */
    public NetworkProxyNode setResource(String resource) {
        this.resource = resource;
        
        if (resource == null || resource.isBlank()) {
            this.resource = "/";
        }
        
        this.resource = this.resource.trim();
        if (!this.resource.endsWith("/")) {
            this.resource += "/";
        }
        
        
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyNode#getMethods()
     */
    @Override
    public List<String> getMethods() {
        return methods;
    }
    
    
    /**
     * Set the methods
     *
     * @param methods the methods
     * @return the network proxy node
     */
    public NetworkProxyNode setMethods(List<String> methods) {
        this.methods = methods;
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyNode#getInstances()
     */
    @Override
    public List<URI> getInstances() {
        return instances;
    }
    
    
    /**
     * Set the instances
     *
     * @param instances the instances
     * @return the network proxy node
     */
    public NetworkProxyNode setInstances(List<URI> instances) {
        this.instances = instances;
        return this;
    }

    
    /**
     * Add a network proxy node instance
     *
     * @param instances the instance
     * @return the configuration
     * @throws IllegalArgumentException In case of an invalid uri
     */
    public NetworkProxyNode addInstance(String instances) throws IllegalArgumentException {
        String[] instancesSplit = instances.split(",");
        if (instancesSplit != null && instancesSplit.length > 0) {
            if (this.instances == null) {
                this.instances = new ArrayList<>(); 
            }
                    
            for (String instance : instancesSplit) {
                try {
                    this.instances.add(new URI(instance.trim()));
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid instance uri [" + instance + "]: " + e.getMessage());
                }
            }
        }
        
        return this;
    }

    
    /**
     * Add a network proxy node instance
     *
     * @param instance the instance
     * @return the configuration
     */
    public NetworkProxyNode addInstance(URI instance) {
        instances.add(instance);
        return this;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(instances, methods, name, resource);
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        NetworkProxyNode other = (NetworkProxyNode) obj;
        return Objects.equals(instances, other.instances) && Objects.equals(methods, other.methods)
                && Objects.equals(name, other.name) && Objects.equals(resource, other.resource);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NetworkProxyNode [name=" + name + ", resource=" + resource + ", methods=" + methods + ", instances=" + instances + "]";
    }
}
