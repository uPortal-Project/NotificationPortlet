/**
 * Written by Anthony Taylor (Anthony.Taylor@manchester.ac.uk), Web Services Manager, EPS
 * 23 Jan 2010
 */

package org.jasig.portlet.notice.constants.core.ajax;

public class AjaxResponseConstants {

		
	public static final String MODEL_OBJECT_MODEL   = "objectModel";
	public static final String MODEL_STATUS_CODE    = "statusCode";
	public static final String MODEL_STATUS_MSG     = "statusMessage";
	public static final String MODEL_STATUS_MSG_TXT = "statusMessageText";
	public static final String MODEL_ACTION         = "action";
	
	// Ajax Response Codes
	
	// 0: General Error
	public static final Integer STATUS_CODE_GENERAL_ERROR = 0;
	public static final String  STATUS_MSG_GENERAL_ERROR  = "ERROR";
	public static final String  STATUS_MSGTXT_GENERAL_ERROR  = "A general error has occurred.";
	
	// 1: Success
	public static final Integer STATUS_CODE_SUCCESS = 1;
	public static final String  STATUS_MSG_SUCCESS  = "SUCCESS";	
	
	
	// 2: Authorisation Error
	public static final Integer STATUS_CODE_AUTHORISATION_ERROR = 2;
	public static final String  STATUS_MSG_AUTHORISATION_ERROR  = "AUTHORISATION ERROR";	
	
	
	// 3: Duplication Error
	public static final Integer STATUS_CODE_DUPLICATE_ERROR = 3;
	public static final String  STATUS_MSG_DUPLICATE_ERROR  = "DUPLICATE";	
	
	
	// 4: Not Logged in Error
	public static final Integer STATUS_CODE_SESSION_ERROR = 4;
	public static final String  STATUS_MSG_SESSION_ERROR  = "SESSION ERROR";	
	public static final String  STATUS_MSGTXT_SESSION_ERROR  = "The portal login session appears to have timed out. Please close this window and log back into portal.";

	public static final String REQUESTER_PARTY_NUMBER = "rpn";

	public static final String OPERATION = "op";

	
}
