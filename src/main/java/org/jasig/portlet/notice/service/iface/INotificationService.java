package org.jasig.portlet.notice.service.iface;

import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.exceptions.NotificationServiceException;

/**
 * This is the interface used to retrieve notifications from a notifications 
 * data source.
 */
public interface INotificationService {

	/**
	 * Returns the name of the service.
	 * @return String.
	 */
	public String getName();

    /**
     * Gathers the collection of notifications for the user associated with the 
     * srequest.
     * 
     * @param req The PortletRequest
     * @return NotificationResponse
     * @throws NotificationServiceException
     */
    public NotificationResponse getNotifications(PortletRequest req) throws NotificationServiceException;    
        
}