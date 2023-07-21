/*
 * NetworkProxyConfigurationParser.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.config;

import com.github.toolarium.network.proxy.util.JSONUtil;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Network proxy configuration parser
 * 
 * @author patrick
 */
public final class NetworkProxyConfigurationParser {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkProxyConfigurationParser.class);

    
    /**
     * Private class, the only instance of the singelton which will be created by accessing the holder class.
     *
     * @author patrick
     */
    private static class HOLDER {
        static final NetworkProxyConfigurationParser INSTANCE = new NetworkProxyConfigurationParser();
    }

    
    /**
     * Constructor
     */
    private NetworkProxyConfigurationParser() {
        // NOP
    }

    
    /**
     * Get the instance
     *
     * @return the instance
     */
    public static NetworkProxyConfigurationParser getInstance() {
        return HOLDER.INSTANCE;
    }

    
    /**
     * Set the network proxy node list
     *
     * @param inputNetworkProxyNodeList the network node list 
     * @return the configuration
     */
    public List<INetworkProxyNode> parse(String... inputNetworkProxyNodeList) {

        List<INetworkProxyNode> networkProxyNodeList = new ArrayList<>();
        if (inputNetworkProxyNodeList != null && inputNetworkProxyNodeList.length > 0) {
            for (int i = 0; i < inputNetworkProxyNodeList.length; i++) {
                if (inputNetworkProxyNodeList[i] != null && inputNetworkProxyNodeList[i].length() > 0) {                    
                    if (inputNetworkProxyNodeList[i].trim().startsWith("{") && inputNetworkProxyNodeList[i].trim().endsWith("}")) {
                        // json
                        try {
                            @SuppressWarnings("unchecked")
                            List<INetworkProxyNode> n = JSONUtil.getInstance().read(List.class, inputNetworkProxyNodeList[i]);
                            networkProxyNodeList.addAll(n);
                        } catch (IOException e) {
                            LOG.warn("Invalid value network proxy node list: " + e.getMessage() + "[" + inputNetworkProxyNodeList[i] + "]");
                        }
                    } else {
                        try {
                            String line = inputNetworkProxyNodeList[i].trim();
                            int idx = line.indexOf('=');
                            while (idx > 0) {
                                final String resourcePath = line.substring(0, idx).trim();
                                final List<URI> instances = new ArrayList<>();
                                
                                if (idx > 0 && idx < line.length()) {
                                    line = line.substring(idx + 1).trim();
                            
                                    if (line.startsWith("[")) {
                                        line = line.substring(1).trim();
                                    }
                                    idx = line.indexOf(']');
                                    
                                    final String serverList;
                                    if (idx > 0 && idx < line.length()) {
                                        serverList = line.substring(0, idx);
                                        line = line.substring(idx + 1).trim();
                                        
                                    } else {
                                        idx = line.indexOf('=');
                                        if (idx > 0) { // there is a next entry ... search last comma // CHECKSTYLE IGNORE THIS LINE
                                            int nextEntry = line.substring(0, idx).lastIndexOf(',');
                                            if (nextEntry < 0) { // CHECKSTYLE IGNORE THIS LINE
                                                nextEntry = line.substring(0, idx).lastIndexOf(';');
                                            }
                                            serverList = line.substring(0, nextEntry);
                                            line = line.substring(nextEntry + 1);
                                        } else {
                                            serverList = line;
                                            line = "";
                                        }
                                    }

                                    final String[] servers = serverList.split(",");
                                    for (String server : servers) {
                                        instances.add(URI.create(server.trim()));
                                    }
                                    networkProxyNodeList.add(new NetworkProxyNode(null, resourcePath, null, instances));

                                    if (line.startsWith(",")) {
                                        line = line.substring(1).trim();
                                    }
                                    idx = line.indexOf('=');
                                }
                            }
                        } catch (RuntimeException e) {
                            LOG.warn("Invalid node list: " + e.getMessage() + "[" + inputNetworkProxyNodeList[i] + "]");
                        }
                    }
                }
            }
        }
        
        return networkProxyNodeList;
    }
}
