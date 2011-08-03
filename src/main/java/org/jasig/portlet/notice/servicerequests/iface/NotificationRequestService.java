package org.jasig.portlet.notice.servicerequests.iface;

import java.util.List;

import org.jasig.portlet.notice.source.NotificationIdentifier;
import org.jasig.portlet.notice.servicerequests.NotificationRequest;
import org.jasig.portlet.notice.servicerequests.iface.exceptions.NotificationRequestServiceException;


public interface NotificationRequestService {

    /**
     * Retrieves a single ServiceRequest by uid for the requester from the configured DAO
     * 
     * @param partyNumber
     * @param username
     * @param sourceIdentifier
     * @return
     * @throws ServiceRequestServiceException
     */
    public NotificationRequest getRequest(String partyNumber, String username, NotificationIdentifier sourceIdentifier) throws NotificationRequestServiceException;
    
    /**
     * Retrieves current service requests for requester
     * 
     * @param partyNumber
     * @param username
     * @return List of service requests or an empty list
     */
    public List<NotificationRequest> getCurrentRequests(String partyNumber, String username);
    
    /**
     * Retrieves all available service requests for requester
     * 
     * @param partyNumber
     * @param username
     * @return List of service requests or an empty list
     */
    public List<NotificationRequest> getAllRequests(String partyNumber, String username);
    
        
}