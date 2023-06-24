/*
 * NetworkProxyConfiguration.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.config;

import com.github.toolarium.network.proxy.logger.VerboseLevel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Define the process runner configuration
 * 
 * @author patrick
 */
public class NetworkProxyConfiguration implements INetworkProxyConfiguration {
    private static final String SLASH = "/";
    private static final String END_VALUE = "].";
    private static final String PROCESSRUNNER_PROPERTIES = "processrunner.properties";
    private static final Logger LOG = LoggerFactory.getLogger(NetworkProxyConfiguration.class);
    private static final String USER_HOME = System.getProperty("user.home");
    private String serverName;
    private String hostname;
    private int port;
    private String directory;
    private VerboseLevel verboseLevel;
    private String accessLogFormatString;
    private String accessLogFilePattern;
    private String basicAuthentication;
    private String healthPath;
    private String resourcePath;
    private int ioThreads;
    private int workerThreads;
    
    
    /**
     * Constructor for NetworkProxyConfiguration
     */
    public NetworkProxyConfiguration() {
        this.serverName = "";
        this.hostname = "0.0.0.0";
        this.port = 8080;
        this.directory = ".";
        this.verboseLevel = VerboseLevel.INFO;
        this.accessLogFormatString = "combined";
        this.accessLogFilePattern = "logs/access-%d{yyyy-MM-dd}.log.gz"; // "logs/access-%d{yyyy-MM-dd}.%i.log.gz"
        this.basicAuthentication = null;
        this.healthPath = "/q/health";
        this.resourcePath = SLASH;
        this.ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
        this.workerThreads = ioThreads * 8;
    }


    /**
     * Constructor for NetworkProxyConfiguration
     * 
     * @param configuration the configuration
     */
    public NetworkProxyConfiguration(INetworkProxyConfiguration configuration) {
        this.serverName = configuration.getRemoteServerName();
        this.hostname = configuration.getHostname();
        this.port = configuration.getPort();
        this.directory = configuration.getDirectory();
        this.verboseLevel = configuration.getVerboseLevel();
        this.accessLogFormatString = configuration.getAccessLogFormatString();
        this.accessLogFilePattern = configuration.getAccessLogFilePattern();
        this.basicAuthentication = configuration.getBasicAuthentication();
        this.healthPath = configuration.getHealthPath();
        this.resourcePath = configuration.getResourcePath();
        this.ioThreads = configuration.getIoThreads();
        this.workerThreads = configuration.getWorkerThreads();
    }


    /**
     * @see com.github.toolarium.process.runner.config.NetworkProxyConfiguration#getRemoteServerName()
     */
    @Override
    public String getRemoteServerName() {
        return serverName;
    }

    
    /**
     * Set the server name
     *
     * @param serverName the server name
     * @return the configuration
     */
    public NetworkProxyConfiguration setRemoteServerName(String serverName) {
        if (serverName != null && !serverName.isBlank()) {
            LOG.debug("Set server name: [" + serverName + "].  ");
            this.serverName = serverName.trim();
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#getHostname()
     */
    @Override
    public String getHostname() {
        return hostname;
    }

    
    /**
     * Set the host name
     *
     * @param hostname the hostname
     * @return the configuration
     */
    public NetworkProxyConfiguration setHostname(String hostname) {
        if (hostname != null && !hostname.isBlank()) {
            LOG.debug("Set hostname: [" + hostname + "].  ");
            this.hostname = hostname.trim();
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#getPort()
     */
    @Override
    public int getPort() {
        return port;
    }

    
    /**
     * Set the port
     *
     * @param port the port
     * @return the configuration
     */
    public NetworkProxyConfiguration setPort(Integer port) {
        if (port != null) {
            LOG.debug("Set port: [" + port + "].  ");            
            this.port = port.intValue();
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.process.runner.config.IProcessRunnerConfiguration#getDirectory()
     */
    @Override
    public String getDirectory() {
        return directory;
    }

    
    /**
     * Set the directory
     *
     * @param directory the directory
     * @return the configuration
     */
    public NetworkProxyConfiguration setDirectory(String directory) {
        LOG.debug("Set directory: [" + directory + END_VALUE);
        
        if (directory != null) {
            this.directory = directory.trim();

            if ("%HOME%".equals(directory) || "$HOME".equals(directory)) {
                this.directory = USER_HOME;
            }
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#getVerboseLevel()
     */
    @Override
    public VerboseLevel getVerboseLevel() {
        return verboseLevel;
    }

    
    /**
     * Set the verbose level
     *
     * @param verboseLevel the verbose level
     * @return the configuration
     */
    public NetworkProxyConfiguration setVerboseLevel(VerboseLevel verboseLevel) {
        if (verboseLevel != null) {
            LOG.debug("Set verboseLevel: [" + verboseLevel + END_VALUE);
            this.verboseLevel = verboseLevel;
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#getAccessLogFormatString()
     */
    @Override
    public String getAccessLogFormatString() {
        return accessLogFormatString;
    }

    
    /**
     * Set the access log format string
     *
     * @param accessLogFormatString the access log format string
     * @return the configuration
     */
    public NetworkProxyConfiguration setAccessLogFormatString(String accessLogFormatString) {
        if (accessLogFormatString != null && !accessLogFormatString.isBlank()) {
            LOG.debug("Set accessLogFormatString: [" + accessLogFormatString + END_VALUE);
            this.accessLogFormatString = accessLogFormatString;
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#getAccessLogFilePattern()
     */
    @Override
    public String getAccessLogFilePattern() {
        return accessLogFilePattern;
    }

    
    /**
     * Set the access log file pattern
     *
     * @param accessLogFilePattern the access log file pattern
     * @return the configuration
     */
    public NetworkProxyConfiguration setAccessLogFilePattern(String accessLogFilePattern) {
        if (accessLogFilePattern != null && !accessLogFilePattern.isBlank()) {
            LOG.debug("Set accessLogFilePattern: [" + accessLogFilePattern + END_VALUE);
            this.accessLogFilePattern = accessLogFilePattern;
        }
        
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#hasBasicAuthentication()
     */
    @Override
    public boolean hasBasicAuthentication() {
        return basicAuthentication != null && !basicAuthentication.isBlank(); 
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#getBasicAuthentication()
     */
    @Override
    public String getBasicAuthentication() {
        return basicAuthentication;
    }

    
    /**
     * Define if basic authentication is enabled
     *
     * @param basicAuthentication true if basic authentication is enabled
     * @return the configuration
     */
    public NetworkProxyConfiguration setBasicAuthentication(String basicAuthentication) {
        if (basicAuthentication == null || basicAuthentication.isBlank()) {
            LOG.debug("Disable basicAuthentication.");
        } else { 
            LOG.debug("Enable basicAuthentication.");
        }
            
        this.basicAuthentication = basicAuthentication;
        return this;
    }


    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#hasHealthCheck()
     */
    @Override
    public boolean hasHealthCheck() {
        return healthPath != null && !healthPath.isBlank(); 
    }
    

    /**
     * @see com.github.toolarium.network.proxy.config.INetworkProxyConfiguration#getHealthPath()
     */
    @Override
    public String getHealthPath() {
        return healthPath;
    }

    
    /**
     * Set the health path 
     *
     * @param healthPath the resource path
     * @return the configuration
     */
    public NetworkProxyConfiguration setHealthPath(String healthPath) {
        if (healthPath == null || healthPath.isBlank()) {
            LOG.debug("Disable health check.");
        } else { 
            LOG.debug("Enable health check: [" + healthPath + END_VALUE);
        }
            
        this.healthPath = healthPath;
        return this;
    }


    /**
     * @see com.github.toolarium.process.runner.config.IProcessRunnerConfiguration#getResourcePath()
     */
    @Override
    public String getResourcePath() {
        return resourcePath;
    }

    
    /**
     * Set the resource path 
     *
     * @param resourcePath the resource path
     * @return the configuration
     */
    public NetworkProxyConfiguration setResourcePath(String resourcePath) {
        if (resourcePath != null && !resourcePath.isBlank()) {
            LOG.debug("Set resourcePath: [" + resourcePath + END_VALUE);            
            this.resourcePath = resourcePath;
        }
        return this;
    }


    /**
     * @see com.github.toolarium.process.runner.config.IProcessRunnerConfiguration#getIoThreads()
     */
    @Override
    public int getIoThreads() {
        return ioThreads;
        
    }

    
    /**
     * Set the I/O threads
     *
     * @param ioThreads the io threads
     * @return the configuration
     */
    public NetworkProxyConfiguration setIoThreads(Integer ioThreads) {
        if (ioThreads != null && ioThreads.intValue() > 0) {
            LOG.debug("Set ioThreads: [" + ioThreads + END_VALUE);            
            this.ioThreads = ioThreads;
            setWorkerThreads(ioThreads * 8);
        }
        return this;
    }



    /**
     * @see com.github.toolarium.process.runner.config.IProcessRunnerConfiguration#getWorkerThreads()
     */
    @Override
    public int getWorkerThreads() {
        return workerThreads;
    }

    
    /**
     * Set the worker threads
     *
     * @param workerThreads the worker threads
     * @return the configuration
     */
    public NetworkProxyConfiguration setWorkerThreads(Integer workerThreads) {
        if (workerThreads != null && workerThreads.intValue() > 0) {
            LOG.debug("Set workerThreads: [" + workerThreads + END_VALUE);            
            this.workerThreads = workerThreads;
        }
        return this;
    }


    /**
     * Read the configuration from the classpath
     * 
     * @return the configuration
     */
    public NetworkProxyConfiguration readProperties() {
        Properties properties = readPropertiesFromClasspath();
        if (properties == null) {
            return this;
        }
        
        setRemoteServerName(readProperty(properties, "serverName", serverName, false));
        setHostname(readProperty(properties, "hostname", hostname, false));
        setPort(readProperty(properties, "port", port, false));
        setDirectory(readProperty(properties, "directory", directory, false));

        setIoThreads(readProperty(properties, "ioThreads", ioThreads, false));
        setWorkerThreads(readProperty(properties, "workerThreads", workerThreads, false));

        setVerboseLevel(readProperty(properties, "verboseLevel", verboseLevel, false));
        setAccessLogFormatString(readProperty(properties, "accessLogFormatString", accessLogFormatString, false));
        setAccessLogFilePattern(readProperty(properties, "accessLogFilePattern", accessLogFilePattern, false));
        
        //setBasicAuthentication(readProperty(properties, "basicAuthentication", basicAuthentication, true));
        setHealthPath(readProperty(properties, "healthPath", healthPath, true));
        setResourcePath(readProperty(properties, "resourcePath", resourcePath, false));
        return this;
    }

    
    /**
     * Read properties from classpath
     *
     * @return the read properties
     */
    protected Properties readPropertiesFromClasspath() {
        Properties properties = null;
        
        try {
            try (InputStream stream = this.getClass().getResourceAsStream(SLASH + PROCESSRUNNER_PROPERTIES)) {
                int countEntries = 0;
                if (stream != null) {
                    LOG.debug("Found " + PROCESSRUNNER_PROPERTIES + "...");
                    properties = new Properties();
                    String line;
                    InputStreamReader inputStreamReader = new InputStreamReader(stream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    while ((line = bufferedReader.readLine()) != null) {
                        if (!line.isBlank() && !line.trim().startsWith("#")) {
                            String c = line.trim();
                            int idx = c.indexOf('=');
                            if (idx > 0) {
                                String key = c.substring(0, idx).trim();
                                String value = c.substring(idx + 1).trim();
                                properties.setProperty(key, value);
                                countEntries++;
                            }
                        }
                    }
                }
                
                if (countEntries == 0) {
                    properties = null;
                }
            }
        } catch (IOException ex) { 
            LOG.info("Could not read and parse confuguration " + PROCESSRUNNER_PROPERTIES + " from classpath.");
        }
        
        return properties;
    }


    /**
     * Read boolean property
     *
     * @param properties the properties
     * @param name the name
     * @param defaultValue the default value
     * @param allowEmptyValue true to allow empty values otherwise in case of an empty value the default value will be taken
     * @return the result
     */
    protected String readProperty(Properties properties, String name, String defaultValue, boolean allowEmptyValue) {
        String result = properties.getProperty(name, defaultValue);
        
        if (result == null || result.isBlank()) {
            if (allowEmptyValue) {
                LOG.debug("Assign property [" + name + "] = [" + result + "] from " + PROCESSRUNNER_PROPERTIES + ".");
                return result;
            } else {
                LOG.debug("Assign default property [" + name + "] = [" + defaultValue + "] from " + PROCESSRUNNER_PROPERTIES + ".");
                return defaultValue;
            }
        } else {
            if (!result.equals(defaultValue)) {
                LOG.debug("Assign property [" + name + "] = [" + result + "] from " + PROCESSRUNNER_PROPERTIES + ".");
            }
        }
        
        return result;
    }

    
    /**
     * Read boolean property
     *
     * @param properties the properties
     * @param name the name
     * @param defaultValue the default value
     * @param allowEmptyValue true to allow empty values otherwise in case of an empty value the default value will be taken
     * @return the result
     */
    protected Boolean readProperty(Properties properties, String name, Boolean defaultValue, boolean allowEmptyValue) {
        String result = readProperty(properties, name, "" + defaultValue, allowEmptyValue);
        if ((result == null || result.isBlank())) {
            if (allowEmptyValue) {
                return null;
            } else {
                return defaultValue;
            }
        }
        
        try {
            return Boolean.valueOf(result);
        } catch (Exception e) {
            LOG.warn("Invalid value [" + result + "] for attribute [" + name + "], keep default value [" + defaultValue + END_VALUE);
            return defaultValue;
        }
    }

    
    /**
     * Read boolean property
     *
     * @param properties the properties
     * @param name the name
     * @param defaultValue the default value
     * @param allowEmptyValue true to allow empty values otherwise in case of an empty value the default value will be taken
     * @return the result
     */
    protected Integer readProperty(Properties properties, String name, Integer defaultValue, boolean allowEmptyValue) {
        String result = readProperty(properties, name, "" + defaultValue, allowEmptyValue);
        if ((result == null || result.isBlank())) {
            if (allowEmptyValue) {
                return null;
            } else {
                return defaultValue;
            }
        }
        
        try {
            return Integer.valueOf(result);
        } catch (Exception e) {
            LOG.warn("Invalid value [" + result + "] for attribute [" + name + "], keep default value [" + defaultValue + END_VALUE);
            return defaultValue;
        }
    }


    /**
     * Read boolean property
     *
     * @param properties the properties
     * @param name the name
     * @param defaultValue the default value
     * @param allowEmptyValue true to allow empty values otherwise in case of an empty value the default value will be taken
     * @return the result
     */
    protected VerboseLevel readProperty(Properties properties, String name, VerboseLevel defaultValue, boolean allowEmptyValue) {
        String result = readProperty(properties, name, "" + defaultValue, allowEmptyValue);
        if ((result == null || result.isBlank())) {
            if (allowEmptyValue) {
                return null;
            } else {
                return defaultValue;
            }
        }
        
        try {
            return VerboseLevel.valueOf(result);
        } catch (Exception e) {
            LOG.warn("Invalid value [" + result + "] for attribute [" + name + "], keep default value [" + defaultValue + END_VALUE);
            return defaultValue;
        }
    }
}
