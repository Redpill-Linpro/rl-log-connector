package com.redpill_linpro.rllog;

import org.apache.logging.log4j.ThreadContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.api.transport.PropertyScope;
import org.mule.context.notification.MessageProcessorNotification;

import com.redpill_linpro.rllog.config.ConnectorConfig;

public class RLLogMessageProcessorNotificationListener implements MessageProcessorNotificationListener<MessageProcessorNotification> {

    private ConnectorConfig config;
    
    public RLLogMessageProcessorNotificationListener(ConnectorConfig config) {
        this.config = config;
    }
    
    @Override
    public void onNotification(MessageProcessorNotification notification) {
        if (notification.getAction() == MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE) {
            MuleMessage message = notification.getSource().getMessage();
            if ( config.isAddCorrelationId()) {
                String correlationId = message.getProperty(
                        this.config.getAlternativeCorrelationId(),
                        PropertyScope.INBOUND);
                if ( correlationId == null ) {
                    correlationId = message.getCorrelationId();
                }
                ThreadContext.put("correlation-id", correlationId);
            }
            if ( config.isAddMessageId()) {
                ThreadContext.put("message-id", message.getMessageRootId());
            }
            if ( config.isAddApplicationName()) {
                ThreadContext.put("application",
                    notification.getSource().getMuleContext().getConfiguration().getId()
                    );
            }
        }
    }
}
