package com.redpill_linpro.rllog;

import javax.inject.Inject;

import org.apache.logging.log4j.ThreadContext;
import org.mule.api.MuleContext;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Default;
import org.mule.context.notification.NotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redpill_linpro.rllog.config.ConnectorConfig;

@Connector(name="rl-log", friendlyName="RLLog")
public class RLLogConnector {

    @Config
    ConnectorConfig config;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Inject
    private MuleContext muleContext;
    
    public void setMuleContext(MuleContext muleContext) {
        this.muleContext = muleContext;
    }
    public MuleContext getMuleContext() {
        return muleContext;
    }
    
    /**
     * Registers the a server notification listeners in the registry.
     * This is needed since we want to listen for both MessageProcessorNotification and ConnectorMessageNotification
     */
    @Start
    public void customStart() {
        try {
            this.muleContext.registerListener(new RLLogConnectorMessageNotificationListener(config));
            this.muleContext.registerListener(new RLLogMessageProcessorNotificationListener(config));
        } catch (NotificationException e) {
            logger.error("Failed to register as server notification listener", e);
        }
    }
    

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }
    
    /**
     * Puts a context value (the value parameter) as identified with the key parameter into the current thread's context map.
     * 
     * If the current thread does not have a context map it is created as a side effect. 
     * 
     * @param key The key to name
     * @param value The key value
     */
    @Processor
    public void putIntoThreadContextMap(@Default("#[]")String key, @Default("#[]")String value) {
        ThreadContext.put(key, value);
    }
    
    /**
     * Removes the context value identified by the key parameter.
     * 
     * @param key The key to remove
     */
    @Processor
    public void removeFromThreadContextMap(@Default("#[]")String key) {
        ThreadContext.remove(key);
    }
    
    /**
     * Pushes new diagnostic context information for the current thread.
     * 
     * The contents of the message parameter is determined solely by the client.
     * 
     * @param message The new diagnostic context information.
     */
    @Processor
    public void pushToThreadContextStack(@Default("#[]")String message) {
        ThreadContext.push(message);
    }
    
    /**
     * Returns the value of the last item placed on the stack.
     *  
     * The returned value is the value that was pushed last. If no context is available, then the empty string "" is returned. 
     *  
     * @return The innermost diagnostic context.
     */
    @Processor
    public String popThreadConextStack() {
        return ThreadContext.pop();
    }
}
