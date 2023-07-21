/*
 * LifecycleLogger.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.logger;

import com.github.toolarium.network.proxy.Version;
import com.github.toolarium.network.proxy.config.INetworkProxyConfiguration;
import com.github.toolarium.network.proxy.config.INetworkProxyNode;
import com.github.toolarium.network.proxy.logger.ansi.ColoredStackTraceWriter;
import io.undertow.Undertow.ListenerInfo;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.ColorScheme;


/**
 * The startup logger
 *  
 * @author patrick
 */
public class LifecycleLogger {
    private static final Logger LOG = LoggerFactory.getLogger(LifecycleLogger.class);
    private static final String SLASH = "/";
    private static final String NL = "\n";
    private static final String LINE = "----------------------------------------------------------------------------------------";
    private static final String APP = "network-proxy v" + Version.VERSION;
    private ColorScheme colorSchema = Help.defaultColorScheme(Help.Ansi.AUTO);

    
    /**
     * Constructor
     */
    public  LifecycleLogger() {
    }

    
    /**
     * Constructor
     * 
     * @param colorSchema the color schema
     */
    public  LifecycleLogger(ColorScheme colorSchema) {
        this.colorSchema = colorSchema;
    }

    
    /**
     * Get the color schema
     *
     * @return Get the color schema
     */
    public ColorScheme getColorScheme() {
        return colorSchema;
    }
    
    
    /**
     * Print server startup
     *
     * @param configuration the configuration
     * @param listenerInfoList the listener information
     */
    public void printServerStartup(INetworkProxyConfiguration configuration, List<ListenerInfo> listenerInfoList) {
        LOG.info(prepareServerStartup(Help.defaultColorScheme(Help.Ansi.OFF), configuration, listenerInfoList));
        System.out.println(prepareServerStartup(getColorScheme(), configuration, listenerInfoList)); // CHECKSTYLE IGNORE THIS LINE
    }
    

    /**
     * Convert a {@code Throwable} to a {@code String} , with message and stack traces extracted and colored according
     * to {@code ColorScheme}.
     * 
     * @param t the {@code Throwable} to be converted
     * @return converted and colored {@code String}
     */
    public String preapreThrowable(Throwable t) {
        Help.ColorScheme colorScheme = new Help.ColorScheme.Builder(getColorScheme()).applySystemProperties().build();
        StringWriter stringWriter = new ColoredStackTraceWriter(colorScheme);
        t.printStackTrace(new PrintWriter(stringWriter)); // CHECKSTYLE IGNORE THIS LINE
        return stringWriter.toString();
    }

    
    /**
     * Get server message
     *
     * @param colorScheme the color schema
     * @param configuration the configuration
     * @param listenerInfoList the listener information
     * @return the server message
     */
    protected String prepareServerStartup(ColorScheme colorScheme, INetworkProxyConfiguration configuration, List<ListenerInfo> listenerInfoList) {
        StringBuilder message = new StringBuilder();
        message.append(NL).append(LINE).append(NL);
        
        preapreTitle(colorScheme, message, configuration);
        prepareListener(colorScheme, message, configuration, listenerInfoList);

        if (listenerInfoList != null) { 
            if (configuration.hasHealthCheck()) {
                prepareHeader(message, "Health").append(commandText(colorScheme, configuration.getHealthPath())).append(NL);
            }
            
            /* TODO
            if (configuration.hasBasicAuthentication()) {
                prepareHeader(message, "Basic Auth").append("enabled").append(NL);
            }
    
            if (configuration.isDirectoryListingEnabled()) {
                prepareHeader(message, "Listing").append("enabled").append(NL);
            }
            */
        }

        prepareRouting(colorScheme, message, configuration);
                        
        message.append(LINE).append(NL);
        return message.toString();
    }

    
    /**
     * Prepare the title
     * 
     * @param colorScheme the color schema
     * @param configuration the configuration
     * @param message the message
     * @return the message
     */
    private StringBuilder preapreTitle(ColorScheme colorScheme, StringBuilder message, INetworkProxyConfiguration configuration) {
        message.append("  ");
        if (configuration.getNetworkProxyName() != null && !configuration.getNetworkProxyName().isBlank()) {
            //title = "" + parameterText(configuration.getWebserverName()) + " (powered by " + APP + ")";
            message.append(parameterText(colorScheme, configuration.getNetworkProxyName())).append(" (powered by ").append(APP).append(")");

        } else {
            message.append(parameterText(colorScheme, APP)); /*Ansi.AUTO.string("@|bold,blue " + APP + "!|@")*/
        }
        message.append(NL);
        return message;
    }


    /**
     * Prepare the listener
     * 
     * @param colorScheme the color schema
     * @param message the message
     * @param configuration the configuration
     * @param listenerInfoList the listener list
     * @return the message
     */
    private StringBuilder prepareListener(ColorScheme colorScheme, StringBuilder message, INetworkProxyConfiguration configuration, List<ListenerInfo> listenerInfoList) {
        prepareHeader(message, "Timestamp");
        String instantStr = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString().replaceAll("[TZ]", " ");
        message.append(commandText(colorScheme, instantStr));
        message.append(NL);

        if (listenerInfoList != null && !listenerInfoList.isEmpty()) {
            for (ListenerInfo listenerInfo : listenerInfoList) {
                StringBuilder listenerInfoMessage = new StringBuilder();
                if (listenerInfo.getSslContext() == null) {
                    listenerInfoMessage.append(listenerInfo.getProtcol());
                } else {
                    listenerInfoMessage.append(listenerInfo.getSslContext().getProtocol());
                }
                listenerInfoMessage.append(":/");
    
                String listenerAddress = "" + listenerInfo.getAddress();
                if (listenerInfo.getAddress() instanceof InetSocketAddress) {
                    InetAddress address = ((InetSocketAddress)listenerInfo.getAddress()).getAddress();
                    if (address instanceof Inet6Address) {
                        if (listenerInfoList.size() == 1) {
                            listenerAddress = SLASH + configuration.getHostname() + ":" + configuration.getPort();                      
                        }
                    }
                }
                listenerInfoMessage.append(listenerAddress);
                listenerInfoMessage.append(NL);
                
                prepareHeader(message, "Listener");
                message.append(commandText(colorScheme, listenerInfoMessage.toString()));
            }
        }
        
        return message;
    }

    
    /**
     * Prepare the routing
     * 
     * @param colorScheme the color schema
     * @param message the message
     * @param configuration the configuration
     * @return the message
     */
    private StringBuilder prepareRouting(ColorScheme colorScheme, StringBuilder message, INetworkProxyConfiguration configuration) {
        if (configuration != null && configuration.getNetworkProxyNodeList() != null && !configuration.getNetworkProxyNodeList().isEmpty()) {
            prepareHeader(message, "Proxy");
            message.append(NL);
            
            for (INetworkProxyNode node : configuration.getNetworkProxyNodeList()) {
                message.append("    - " + commandText(colorScheme, node.getResource()));
                
                if (node.getInstances() != null && !node.getInstances().isEmpty()) {
                    if (node.getName() != null && !node.getName().equals(node.getResource())) {
                        message.append(NL);
                        message.append("       - NAME  " + commandText(colorScheme, node.getName()));
                    }
                    
                    message.append(NL);
                    message.append("       - URL   ");
                    
                    int lineLength = 0;
                    int i = 0;
                    for (URI uri : node.getInstances()) {
                        if (i > 0) {
                            message.append(",");
                            lineLength++;
                        }
    
                        String uriStr = uri.toString();
                        if (lineLength + uriStr.length() > 74) {
                            message.append(NL);
                            message.append("               ");
                            lineLength = 0;
                        }
                        
                        lineLength += uriStr.length();
                        message.append(commandText(colorScheme, uriStr));
                        i++;
                    }
                } else {
                    message.append("n/a");
                }
                
                message.append(NL);
            }
        }

        return message;
    }

    
    /**
     * Command text
     *
     * @param colorScheme the color schema
     * @param message the message
     * @return the formated message
     */
    private String commandText(ColorScheme colorScheme, String message) {
        if (colorScheme != null) {
            return "" + colorScheme.commandText(message);
        }
        
        return message;
    }
    

    /**
     * The parameter text
     *
     * @param colorScheme the color schema
     * @param message the message
     * @return the formated message
     */
    private String parameterText(ColorScheme colorScheme, String message) {
        if (colorScheme != null) {
            return "" + colorScheme.parameterText(message);
        }
        
        return message;
    }

    
    /**
     * Prepare header
     *
     * @param message the message
     * @param tag the tag
     * @return the message
     */
    private StringBuilder prepareHeader(StringBuilder message, String tag) {
        message.append("  > ");
        message.append(tag);
        for (int i = tag.length(); i < 11; i++) {
            message.append(' ');
        }
        return message;
    }
}
