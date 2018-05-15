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

/**
 * <p>The Redpill Linpro Log Connector simplifies handling of <a href="https://logging.apache.org/log4j/2.x/manual/thread-context.html">Log4j2 Thread Context</a>.</p>
 * 
 * <p>It allows for automatically inject application name, message id and correlation id into the thread context. This is done by register a set of Server Notification 
 * Listeners that will ensure that the correct logging is performed.</p>
 * 
 * <p>Besides adding the connector configuration to your application you also need to make sure that your selected Log4j2 layout includes the ThreadContext when writing the logs. 
 * For more information see the <a href="https://logging.apache.org/log4j/2.x/manual/thread-context.html#Including_the_ThreadContext_when_writing_logs">Log4J documentation</a>.</p>
 */
@Connector(name="rl-log", friendlyName="RLLog", description="The Redpill Linpro Log Connector simplifies handling of Log4j2 Thread Context")
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
            if ( config.getAlternativeCorrelationId() != null ) {
                this.muleContext.registerListener(new RLLogConnectorMessageNotificationListener(config));
            }
            if ( config.isAddCorrelationId() 
                 || config.isAddMessageId()
                 || config.isAddApplicationName()) {
                this.muleContext.registerListener(new RLLogMessageProcessorNotificationListener(config));
            }
            
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
