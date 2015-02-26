package org.jasig.portlet.notice.service.studentsuccessplan;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import javax.portlet.PortletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface looks up the SSP user id by school id.  It assumes
 * that studentId is a user attribute that is available to the portlet.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class SSPSchoolIdPersonLookup implements ISSPPersonLookup {
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String SUCCESS_QUERY = "$.success";
    private static final String RESULTS_QUERY = "$.results";
    private static final String STUDENT_ID_QUERY = "$.rows[0].id";

    private String schoolIdAttributeName = "schoolId";
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


    @Override
    public String lookupPersonId(PortletRequest request) {
        Map<String, String> userInfo = (Map<String, String>)request.getAttribute(PortletRequest.USER_INFO);
        String studentId = userInfo.get(schoolIdAttributeName);

        // todo: remove this...
        studentId = "jjackson332";
        if (StringUtils.isEmpty(studentId)) {
            return null;
        }

        Element element = cache.get(studentId);
        if (element != null) {
            log.debug("Found in cache: " + element);
//            return (String)element.getObjectValue();
        }

        String url = getPersonSearchURL();
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> variables = new HashMap<>();
        variables.put("schoolId", studentId);

        try {
            ResponseEntity<String> response = sspApi.doRequest(url, HttpMethod.GET, headers, null, String.class, variables);
            String userId = extractUserId(studentId, response);

            Element cacheElement = new Element(studentId, userId);
            cache.put(cacheElement);

            return userId;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    private String extractUserId(String studentId, ResponseEntity<String> response) {
        Configuration config = Configuration.builder().options(
                Option.DEFAULT_PATH_LEAF_TO_NULL
        ).build();
        ReadContext readContext = JsonPath
                .using(config)
                .parse(response.getBody());

        String success = readContext.read(SUCCESS_QUERY);
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
