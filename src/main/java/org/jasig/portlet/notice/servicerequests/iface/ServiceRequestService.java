package org.jasig.portlet.notice.servicerequests.iface;

import java.util.List;

import org.jasig.portlet.notice.source.SourceIdentifier;
import org.jasig.portlet.notice.servicerequests.ServiceRequest;
import org.jasig.portlet.notice.servicerequests.iface.exceptions.ServiceRequestServiceException;


public interface ServiceRequestService {

	/**
	 * Retrieves a single ServiceRequest by uid for the requester from the configured DAO
	 * 
	 * @param partyNumber
	 * @param username
	 * @param sourceIdentifier
	 * @return
	 * @throws ServiceRequestServiceException
	 */
	public ServiceRequest getRequest(String partyNumber, String username, SourceIdentifier sourceIdentifier) throws ServiceRequestServiceException;
	
	/**
	 * Retrieves current service requests for requester
	 * 
	 * @param partyNumber
	 * @param username
	 * @return List of service requests or an empty list
	 */
	public List<ServiceRequest> getCurrentRequests(String partyNumber, String username);
	
	/**
	 * Retrieves all available service requests for requester
	 * 
	 * @param partyNumber
	 * @param username
	 * @return List of service requests or an empty list
	 */
	public List<ServiceRequest> getAllRequests(String partyNumber, String username);
	
		
}
