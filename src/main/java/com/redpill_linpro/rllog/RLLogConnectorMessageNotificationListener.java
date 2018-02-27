package com.redpill_linpro.rllog;

import org.apache.logging.log4j.ThreadContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.notification.ConnectorMessageNotificationListener;
import org.mule.api.transport.PropertyScope;
import org.mule.context.notification.ConnectorMessageNotification;

import com.redpill_linpro.rllog.config.ConnectorConfig;

public class RLLogConnectorMessageNotificationListener implements ConnectorMessageNotificationListener<ConnectorMessageNotification> {

    private ConnectorConfig config;
    
    public RLLogConnectorMessageNotificationListener(ConnectorConfig config) {
        this.config = config;
    }
    
    @Override
    public void onNotification(ConnectorMessageNotification notification) {
        if (notification.getAction() == ConnectorMessageNotification.MESSAGE_RECEIVED) {
            MuleMessage message = notification.getSource();
            
            String correlationId = message.getProperty(this.config.getAlternativeCorrelationId(),
                            PropertyScope.INBOUND);
            if (correlationId != null) {
                message.setProperty(this.config.getAlternativeCorrelationId(), correlationId,
                        PropertyScope.SESSION);
            }
        } else if (notification.getAction() == ConnectorMessageNotification.MESSAGE_RESPONSE
                        || notification.getAction() == ConnectorMessageNotification.MESSAGE_ERROR_RESPONSE) {
                ThreadContext.remove(this.config.getAlternativeCorrelationId());
        }
    }
    

}
