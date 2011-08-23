package org.jasig.portlet.notice.service;

import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.exceptions.NotificationServiceException;

/**
 * This is the interface used to retrieve notifications from a notifications 
 * data source.
 */
public interface INotificationService {

	/**
	 * Returns the name of the service, which should be unique in the portlet app.
	 * 
	 * @return String.
	 */
	public String getName();

    /**
     * Most implementations need do nothing special with this method.  They can 
     * extend AbstractNotificationService.  Special service impls (such as 
     * caching) may want to override it.
     * 
     * @param notificationsContextName Configured in the -portlet.xml Spring file 
     * @param remoteUser From req.getRemoteUser
     * @param req The PortletRequest
     * @return NotificationResponse
     * @throws NotificationServiceException
     */
    public NotificationResponse getNotifications(String notificationsContextName, String remoteUser, PortletRequest req) throws NotificationServiceException;    

    /**
     * After calling this method, the next call to getNotifications will cause 
     * the notifications to be fetched from their source(s), not from cache.
     * 
     * @param notificationsContextName
     * @param remoteUser
     */
    public void refreshNotifications(String notificationsContextName, String remoteUser);    

    /**
     * Gathers the collection of notifications for the user associated with the 
     * srequest.
     * 
     * @param req The PortletRequest
     * @return NotificationResponse
     * @throws NotificationServiceException
     */
    public NotificationResponse fetchNotificationsFromSource(PortletRequest req) throws NotificationServiceException;    
        
}