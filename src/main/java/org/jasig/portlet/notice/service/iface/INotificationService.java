package org.jasig.portlet.notice.service.iface;

import java.util.Map;

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.exceptions.NotificationServiceException;

/**
 * This is the interface used to retrieve notifications
 * from a notifications data source.
 */
public interface INotificationService {

	/**
	 * Returns the name of the service.
	 * @return String.
	 */
	public String getName();

    /**
     * Retrieves a single ServiceRequest by uid for the requester from the configured DAO
     * 
     * @param userInfo client data.
     * @return NotificationResponse
     * @throws NotificationServiceException
     */
    public NotificationResponse getNotifications(Map<String, String> userInfo)
    	throws NotificationServiceException;    
        
}