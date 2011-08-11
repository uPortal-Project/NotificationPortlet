package org.jasig.portlet.notice.serviceresponse.iface;

import java.util.List;

import org.jasig.portlet.notice.source.NotificationIdentifier;
import org.jasig.portlet.notice.serviceresponse.NotificationResponse;
import org.jasig.portlet.notice.serviceresponse.iface.exceptions.NotificationResponseServiceException;


public interface NotificationResponseService {

	/**
	 * Returns the name of the service.
	 * @return String.
	 */
	public String getName();

    /**
     * Retrieves a single ServiceRequest by uid for the requester from the configured DAO
     * 
     * @param partyNumber
     * @param username
     * @param sourceIdentifier
     * @return
     * @throws ServiceRequestServiceException
     */
    public NotificationResponse getResponse(String partyNumber, String username, NotificationIdentifier sourceIdentifier) throws NotificationResponseServiceException;
    
    /**
     * Retrieves current service requests for requester
     * 
     * @param partyNumber
     * @param username
     * @return List of service requests or an empty list
     */
    public List<NotificationResponse> getCurrentResponses(String partyNumber, String username);
    
    /**
     * Retrieves all available service requests for requester
     * 
     * @param partyNumber
     * @param username
     * @return List of service requests or an empty list
     */
    public List<NotificationResponse> getAllResponses(String partyNumber, String username);
    
        
}