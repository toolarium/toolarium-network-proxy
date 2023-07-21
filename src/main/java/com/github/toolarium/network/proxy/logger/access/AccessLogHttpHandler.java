/*
 * AcceessLogHttpHandler.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.logger.access;

import com.github.toolarium.network.proxy.config.INetworkProxyConfiguration;
import com.github.toolarium.network.proxy.logger.VerboseLevel;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;

/**
 * The access log handler utility
 *  
 * @author patrick
 */
public final class AccessLogHttpHandler {
    
    /**
     * Constructor for AccessLogHttpHandler
     */
    private AccessLogHttpHandler() {
        // NOP
    }

    
    /**
     * Add basic authentication
     *
     * @param configuration the configuration
     * @param handlerToWrap the handler to wrap
     * @return the handler
     */
    public static HttpHandler addHandler(final INetworkProxyConfiguration configuration, final HttpHandler handlerToWrap) {
        
        if (VerboseLevel.VERBOSE.equals(configuration.getVerboseLevel()) || VerboseLevel.ACCESS.equals(configuration.getVerboseLevel())) {
            String name = "access"; // TODO: - configuration.getNetworkProxyName();
            final AccessLogReceiver accessLogReceiver = new FileAccessLogReceiver(name);
            return new AccessLogHandler(handlerToWrap, accessLogReceiver, configuration.getAccessLogFormatString(), AccessLogHttpHandler.class.getClassLoader());
        } else if (VerboseLevel.ACCESS_CONSOLE.equals(configuration.getVerboseLevel())) {
            //LogbackUtil.getInstance().detachAppender(ACCESSLOG_APPENDER_NAME);
            final AccessLogReceiver accessLogReceiver = new StdoutAccessLogReceiver();
            return new AccessLogHandler(handlerToWrap, accessLogReceiver, configuration.getAccessLogFormatString(), AccessLogHttpHandler.class.getClassLoader());
        } else {
            //LogbackUtil.getInstance().detachAppender(ACCESSLOG_APPENDER_NAME);
        }
        
        return handlerToWrap;
    }
}
