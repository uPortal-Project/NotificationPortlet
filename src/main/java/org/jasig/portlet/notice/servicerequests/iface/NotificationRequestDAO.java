package org.jasig.portlet.notice.servicerequests.iface;

import java.util.List;

import org.jasig.portlet.notice.source.NotificationIdentifier;
import org.jasig.portlet.notice.servicerequests.NotificationRequest;
import org.jasig.portlet.notice.servicerequests.iface.exceptions.NotificationRequestDAOException;


public interface NotificationRequestDAO {

    public NotificationRequest getRequest(String partyNumber, String useranme, NotificationIdentifier sourceIdentifier) throws NotificationRequestDAOException;
    
    public List<NotificationRequest> getCurrentRequests(String partyNumber, String username) throws NotificationRequestDAOException;
    public List<NotificationRequest> getAllRequests(String partyNumber, String username) throws NotificationRequestDAOException;

}