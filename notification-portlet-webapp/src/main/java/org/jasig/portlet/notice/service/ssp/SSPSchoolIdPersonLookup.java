package org.jasig.portlet.notice.service.ssp;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import java.util.Map;

/**
 * This interface looks up the SSP user id by school id.  It assumes
 * that studentId is a user attribute that is available to the portlet.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class SSPSchoolIdPersonLookup implements ISSPPersonLookup {
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String USERNAME_ATTRIBUTE = "user.login.id";
    private static final String SUCCESS_QUERY = "$.success";
    private static final String RESULTS_QUERY = "$.results";
    private static final String STUDENT_ID_QUERY = "$.rows[0].id";

    private String personSearchURL = "/api/1/person/directoryperson/search?limit=2&schoolId={schoolId}&start=0";
    private ISSPApi sspApi;
    private Cache cache;


    @Resource(name="StudentSuccessPlanService.schoolIdToPersonIdCache")
    public void setCache(final Cache cache) {
        this.cache = cache;
    }


    @Autowired
    public void setSspApi(ISSPApi sspApi) {
        this.sspApi = sspApi;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupPersonId(PortletRequest request) {
        String studentId = getSchoolId(request);
        if (StringUtils.isBlank(studentId)) {
            return null;
        }

        Element element = cache.get(studentId);
        if (element != null) {
            return (String)element.getObjectValue();
        }

        String url = getPersonSearchURL();
        SSPApiRequest sspReq = new SSPApiRequest(url, String.class)
                .addUriParameter("schoolId", studentId);

        try {
            ResponseEntity<String> response = sspApi.doRequest(sspReq);
            String userId = extractUserId(studentId, response);

            Element cacheElement = new Element(studentId, userId);
            cache.put(cacheElement);

            return userId;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * Get the schoolId value from the request.
     *
     * @param request the portlet request
     * @return the school id.   May return null if the school id is not available
     */
    private String getSchoolId(PortletRequest request) {
        PortletPreferences prefs = request.getPreferences();
        String schoolIdAttributeName = prefs.getValue("SSPTaskNotificationService.schoolIdAttribute", "schoolId");

        Map<String, String> userInfo = (Map<String, String>)request.getAttribute(PortletRequest.USER_INFO);
        String studentId = userInfo.get(schoolIdAttributeName);
        if (!StringUtils.isEmpty(studentId)) {
            return studentId;
        }

        // if not found, fall back to username.
        studentId = userInfo.get(USERNAME_ATTRIBUTE);

        return studentId;
    }


    /**
     * Parse the person lookup response from SSP.
     *
     * @param studentId the uPortal studentid
     * @param response the SSP response
     * @return the SSP id if available, else null
     */
    private String extractUserId(String studentId, ResponseEntity<String> response) {
        Configuration config = Configuration.builder().options(
                Option.DEFAULT_PATH_LEAF_TO_NULL
        ).build();
        ReadContext readContext = JsonPath
                .using(config)
                .parse(response.getBody());

        String success = readContext.read(SUCCESS_QUERY);
        // SSP passes this as a string...
        if (!"true".equalsIgnoreCase(success)) {
            return null;
        }

        int count = readContext.read(RESULTS_QUERY, Integer.class);
        if (count != 1) {
            // couldn't find a single unique result.  Bail now...
            log.warn("Expected a single unique result for " + studentId + ".  Found " + count);
            return null;
        }

        String id = readContext.read(STUDENT_ID_QUERY);
        return id;
    }


    private String getPersonSearchURL() {
        return personSearchURL;
    }
}
