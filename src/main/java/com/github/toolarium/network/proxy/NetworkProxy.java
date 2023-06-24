/*
 * NetworkProxy.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy;

import com.github.toolarium.network.proxy.config.INetworkProxyConfiguration;
import com.github.toolarium.network.proxy.config.NetworkProxyConfiguration;
import com.github.toolarium.network.proxy.handler.health.HealthHttpHandler;
import com.github.toolarium.network.proxy.logger.LifecycleLogger;
import com.github.toolarium.network.proxy.logger.VerboseLevel;
import com.github.toolarium.network.proxy.logger.access.AccessLogHttpHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import jptools.runtime.ReflectionUtil;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.Option;


/**
 * The network proxy.
 *
 * <p>! This is just a sample please remove it. !</p>
 */
@Command(name = "networkproxy", mixinStandardHelpOptions = true, version = "networkproxy v" + Version.VERSION, description = "Network proxy.")
public class NetworkProxy implements Runnable {
    
    // disable warnings
    static {
        System.setProperty("disabledAccessWarnings", "true");
        ReflectionUtil.getInstance().disableAccessWarnings();
    }

    private static final Logger LOG = LoggerFactory.getLogger(NetworkProxy.class);
    
    @Option(names = { "-b", "--bind" }, paramLabel = "address", description = "The bind address, by default 0.0.0.0.")
    private String hostname;
    @Option(names = { "-p", "--port" }, paramLabel = "port", description = "The port, by default 8080.")
    private Integer port;
    @Option(names = { "-d", "--directory" }, paramLabel = "directory", description = "The directory, by default working path.")
    private String directory;
    @Option(names = { "--resourcePath" }, paramLabel = "resourcePath", description = "The resource path, by default /.")
    private String resourcePath;
    @Option(names = { "--healthPath" }, paramLabel = "healthPath", defaultValue = "/q/health", description = "The health path, by default /q/health.")
    private String healthPath;    
    @Option(names = { "--basicauth" }, paramLabel = "authentication", description = "The basic authentication: user:password, by default disabled.")
    private String basicAuth;
    @Option(names = { "--name" }, paramLabel = "webserverName", defaultValue = "", description = "The webserver name.")
    private String webserverName;    
    @Option(names = { "--verbose" }, paramLabel = "verboseLevel", defaultValue = "INFO", description = "Specify the verbose level: (${COMPLETION-CANDIDATES}), by default INFO.")
    private VerboseLevel verboseLevel;
    @Option(names = { "-v", "--version" }, versionHelp = true, description = "Display version info")
    private boolean versionInfoRequested;
    @Option(names = { "--accessLogFormat" }, paramLabel = "accessLogFormat", description = "Defines the access log format, default: combined.")
    private String accessLogFormatString;
    @Option(names = { "--accessLogFilePattern" }, paramLabel = "accessLogFilePattern", description = "Defines the access log file pattern, default: logs/access-%%d{yyyy-MM-dd}.log.gz.")
    private String accessLogFilePattern;
    @Option(names = {"-h", "--help" }, usageHelp = true, description = "Display this help message")
    private boolean usageHelpRequested;

    private NetworkProxyConfiguration configuration;
    private LifecycleLogger lifecycleLogger;
    private transient Undertow server;
    private boolean hasError;

    

    /**
     * Constructor for NetworkProxy
     */
    public NetworkProxy() {
        configuration = null;
        lifecycleLogger = new LifecycleLogger();
        server = null;
        hasError = false;
    }


    /**
     * Get the configuration
     *
     * @return the configuration
     */
    public INetworkProxyConfiguration getConfiguration() {
        if (configuration == null) {
            setConfiguration(new NetworkProxyConfiguration()
                    .readProperties()
                    .setRemoteServerName(webserverName)
                    .setHostname(hostname).setPort(port)
                    .setDirectory(directory).setResourcePath(resourcePath)
                    .setBasicAuthentication(basicAuth)
                    .setHealthPath(healthPath)
                    .setVerboseLevel(verboseLevel).setAccessLogFilePattern(accessLogFilePattern).setAccessLogFormatString(accessLogFormatString));
        }
        
        return configuration;
    }

    
    /**
     * Get the configuration
     *
     * @param configuration the configuration
     */
    public void setConfiguration(INetworkProxyConfiguration configuration) {
        this.configuration = new NetworkProxyConfiguration(configuration);
    }

    
    /**
     * Get the color schema
     * 
     * @return the color schema
     */
    private ColorScheme getColorSchmea() {
        return lifecycleLogger.getColorScheme();
    }


    /**
     * The main class
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // try to install jansi
        AnsiConsole.systemInstall();

        //ReflectionUtil.getInstance().disableAccessWarnings();
        
        // new webserver
        NetworkProxy networkProxy = new NetworkProxy();
        
        // parse command line and run
        CommandLine commandLine = new CommandLine(networkProxy).setColorScheme(networkProxy.getColorSchmea());        
        int exitCode = commandLine.execute(args);
        if (networkProxy.hasError()) {
            LOG.debug("Executed Ended with code:" + exitCode);
        } else {
            LOG.debug("Successful started.");
        }
        
        // try to uninstall jansi
        AnsiConsole.systemUninstall();
    }

    
    /**
     * Stop the server
     */
    public synchronized void start() {
        if (!isRunning()) {
            run();
        } else {
            LOG.warn("Network proxy is already running!");
        }
    }

    
    /**
     * Stop the server
     */
    public synchronized void stop() {
        if (isRunning()) {
            server.stop();
            server = null;
        } else {
            LOG.warn("Network proxy is already stopped.");
        }
    }


    /**
     * Check if the server is running
     *
     * @return true if it is running
     */
    public boolean isRunning() {
        return (server != null);
    }

    
    /**
     * Check if there are any errors
     *
     * @return true if there are any errors
     */
    public boolean hasError() {
        return hasError;
    }

    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public synchronized void run() {
        
        if (verboseLevel != null && VerboseLevel.VERBOSE.equals(verboseLevel)) {
            // TODO
        }
        
        INetworkProxyConfiguration configuration = getConfiguration();
        
        try {
            LOG.info("Start network procy [" + configuration.getHostname() + "] on port [" + configuration.getPort() + "]...");

            // create routing
            RoutingHandler routingHandler = Handlers.routing();
            // TODO: routingHandler.setFallbackHandler(RoutingHandlers::notFoundHandler);
            
            // add routes
            HealthHttpHandler.addHandler(configuration, routingHandler);
            // TODO: ResourceHandler.addHandler(configuration, routingHandler);
            
            // create simple server
            server = Undertow.builder()
                    .setIoThreads(configuration.getIoThreads()).setWorkerThreads(configuration.getWorkerThreads())
                    .addHttpListener(configuration.getPort(), configuration.getHostname(), AccessLogHttpHandler.addHandler(configuration, routingHandler))
                   .build();
            server.start();
            
            if (!VerboseLevel.NONE.equals(verboseLevel)) {
                lifecycleLogger.printServerStartup(configuration, server.getListenerInfo());
            }
        } catch (RuntimeException ex) {
            hasError = true;
            if (!VerboseLevel.NONE.equals(verboseLevel)) {
                lifecycleLogger.printServerStartup(configuration, null);
            }
            LOG.warn("Could not start network proxy [" + configuration.getHostname() + "] on port [" + configuration.getPort() + "]\n" + lifecycleLogger.preapreThrowable(ex));
        }
    }
}
