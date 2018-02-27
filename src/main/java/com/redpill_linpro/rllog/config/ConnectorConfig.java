package com.redpill_linpro.rllog.config;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

@Configuration(friendlyName = "Configuration")
public class ConnectorConfig {

    /**
     * Control if we should add application name to Thread Context
     */
    @Configurable
    @Default("true")
    private boolean addApplicationName;
    
    public void setAddApplicationName(boolean addApplicationName) {
        this.addApplicationName = addApplicationName;
    }
    public boolean isAddApplicationName() {
        return addApplicationName;
    }
    
    
    /**
     * Control if we should add message id to Thread Context
     */
    @Configurable
    @Default("true")
    private boolean addMessageId;
    
    public void setAddMessageId(boolean addMessageId) {
        this.addMessageId = addMessageId;
    }
    public boolean isAddMessageId() {
        return addMessageId;
    }
    
    /**
     * Control if we should add correlation id to Thread Context
     */
    @Configurable
    @Default("true")
    private boolean addCorrelationId;
    
    public void setAddCorrelationId(boolean addCorrelationId) {
        this.addCorrelationId = addCorrelationId;
    }
    public boolean isAddCorrelationId() {
        return addCorrelationId;
    }
    
    /**
     * Alternative correlation id 
     */
    @Configurable
    @Optional
    private String alternativeCorrelationId;
    
    public void setAlternativeCorrelationId(String alternativeCorrelationId) {
        this.alternativeCorrelationId = alternativeCorrelationId;
    }
    public String getAlternativeCorrelationId() {
        return alternativeCorrelationId;
    }
}