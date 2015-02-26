package org.jasig.portlet.notice.service.studentsuccessplan;

import javax.portlet.PortletRequest;


/**
 * @author Josh Helmer, jhelmer.unicon.net
 */
public interface ISSPPersonLookup {
    /**
     * Lookup the SSP studentId associated with a request.   This implementation
     * depends on the user having the schoolId available as an attribute on the
     * portal user record.
     *
     * @param request the portlet request
     * @return the studentId if available.  null if studentId can not
     * be found.
     */
    String lookupPersonId(PortletRequest request);
}
