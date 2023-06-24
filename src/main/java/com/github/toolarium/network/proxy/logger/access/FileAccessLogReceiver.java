/*
 * FileAccessLogReceiver.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.network.proxy.logger.access;

import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jptools.logger.Level;
import jptools.logger.LogConfig;
import jptools.logger.LogMessage;
import jptools.logger.appender.DailyFileAppender;
import jptools.logger.appender.FileAppender;
import jptools.util.EnvironmentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a file access log receiver which writes the access log day by day.
 * 
 * @author patrick
 */
public class FileAccessLogReceiver implements AccessLogReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(FileAccessLogReceiver.class);
    private static final String CLASS_NAME = FileAccessLogReceiver.class.getName();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");    
    private String filename;
    private String baseName;
    private String trailerName;
    private LogConfig config;
    private FileAppender fileAppender;

    
    /**
     * Constructor
     *
     * @param filename the filename
     */
    public FileAccessLogReceiver(String filename) {
        this.config = new LogConfig();
        this.filename = filename;
        this.baseName = "";
        this.trailerName = "";

        init();
    }

    
    /**
     * @see io.undertow.server.handlers.accesslog.AccessLogReceiver#logMessage(java.lang.String)
     */
    @Override
    public void logMessage(String message) {
        if (fileAppender == null) {
            synchronized (this) {
                init();
            }
        }

        try {
            fileAppender.writeMessage(new LogMessage(CLASS_NAME, message, null, Level.INFO), config);
        } catch (IOException e) {
            LOG.warn("Error occured: " + e.getMessage(), e);
        }
    }

    
    /**
     * Close the Method description
     */
    public void close() {
        fileAppender.close(config);
        fileAppender = null;
    }

    
    
    /**
     * Gets the used filename back
     *
     * @return the filename
     */
    public String getCurrentFileName() {
        return baseName + trailerName;
    }


    /**
     * Gets the used filename back
     *
     * @return the filename
     */
    public String getFileName() {
        return baseName + "-" + dateFormatter.format(new Date()) + trailerName;
    }


    /**
     * Initialize
     */
    protected void init() {
        baseName = "access";
        trailerName = "";

        // resolve environment config like ${...}
        filename = EnvironmentHelper.getInstance().replaceEnvironmentNames(filename);

        int idx = filename.lastIndexOf('.');
        if (idx > 0) {
            baseName = this.filename.substring(0, idx);
            trailerName = this.filename.substring(idx);
        } else {
            baseName = filename;
        }

        if (trailerName == null || trailerName.trim().length() == 0) {
            trailerName = ".log";
        }

        config.setProperty(LogConfig.ENABLE_TIMESTAMP, "false");
        config.setProperty(LogConfig.ENABLE_PACKAGENAME, "false");
        config.setProperty(LogConfig.ENABLE_CLASSNAME, "false");
        config.setProperty(LogConfig.ENABLE_LEVEL, "false");
        config.setProperty(LogConfig.DESTINATION, baseName + "-'yyyy-MM-dd'" + trailerName);
        config.setProperty(LogConfig.CURRENT_DESTINATION, baseName + trailerName);
        config.setProperty(LogConfig.APPEND_FILE, "true");
        fileAppender = new DailyFileAppender();
        fileAppender.configurationChange(config);
    }
}

