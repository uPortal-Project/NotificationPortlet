package org.jasig.portlet.notice.servicerequests.iface;

import java.util.List;

import org.jasig.portlet.notice.servicerequests.iface.exceptions.ServiceRequestDAOException;
import org.jasig.portlet.notice.source.SourceIdentifier;
import org.jasig.portlet.notice.servicerequests.ServiceRequest;


public interface ServiceRequestDAO {

	public ServiceRequest getRequest(String partyNumber, String useranme, SourceIdentifier sourceIdentifier) throws ServiceRequestDAOException;
	
	public List<ServiceRequest> getCurrentRequests(String partyNumber, String username) throws ServiceRequestDAOException;
	public List<ServiceRequest> getAllRequests(String partyNumber, String username) throws ServiceRequestDAOException;

}
