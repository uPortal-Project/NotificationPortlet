package org.jasig.portlet.notice.servicerequests;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jasig.portlet.notice.source.NotificationIdentifier;

public class NotificationRequest {

    private NotificationIdentifier source;
    private String referenceIdentifier;
    private Date submissionDate;
    private Date lastUpdatedDate;
    private String status;
    private String type;
    private String details;
    private String link;
    private Map<String,String> additionalInformation;
    
    
    public Map<String,String> toMap() {
        Map map = new HashMap<String, String>();
        map.put("source", source);
        map.put("referenceIdentifier", referenceIdentifier);
        map.put("submissionDate", submissionDate);
        map.put("lastUpdatedDate", lastUpdatedDate);
        map.put("status", status);
        map.put("type", type);
        map.put("details", details);
        map.put("link", details);
        map.putAll(additionalInformation);
        return map;
    }
    
    /**
     * @return the source
     */
    public NotificationIdentifier getSource() {
        return source;
    }
    /**
     * @param source the source to set
     */
    public void setSource(NotificationIdentifier source) {
        this.source = source;
    }
    /**
     * @return the referenceIdentifier
     */
    public String getReferenceIdentifier() {
        return referenceIdentifier;
    }
    /**
     * @param referenceIdentifier the referenceIdentifier to set
     */
    public void setReferenceIdentifier(String referenceIdentifier) {
        this.referenceIdentifier = referenceIdentifier;
    }
    /**
     * @return the submissionDate
     */
    public Date getSubmissionDate() {
        return submissionDate;
    }
    /**
     * @param submissionDate the submissionDate to set
     */
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    /**
     * @return the lastUpdatedDate
     */
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }
    /**
     * @param lastUpdatedDate the lastUpdatedDate to set
     */
    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }
    /**
     * @param details the details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }
    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }
    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }
    /**
     * @return the additionalInformation
     */
    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }
    /**
     * @param additionalInformation the additionalInformation to set
     */
    public void setAdditionalInformation(Map<String, String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
    
    
    
}