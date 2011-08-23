package org.jasig.portlet.notice.service;

import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.exceptions.NotificationServiceException;

public abstract class AbstractNotificationService implements INotificationService {

    
    public final NotificationResponse getNotifications(String notificationsContextName, 
                String remoteUser, PortletRequest req) throws NotificationServiceException {
        // Subclasses need nothing special from this method.
        return this.fetchNotificationsFromSource(req);
    }

    public void refreshNotifications(String notificationsContextName, String remoteUser) {
        // This method is a no-op for basic service impls.
    }

}
