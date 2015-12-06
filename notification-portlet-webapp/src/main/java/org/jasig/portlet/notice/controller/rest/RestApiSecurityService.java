package org.jasig.portlet.notice.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Helper class that provides an additional security check to ensure that the REST API has
 * been enabled.  If not enabled, all calls to the REST API should return a 403.  I feel
 * like it should be possible to do this with pure spring-el, but didn't seem to work right.
 * In addition, this provides a place to potentially add hooks for adding auth hooks that
 * can look for a uPortal principal.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@Service("restApiSecurityService")
public class RestApiSecurityService {
    @Value("${restApi.enabled:false}")
    private boolean restApiEnabled;


    public boolean isEnabled() {
        return restApiEnabled;
    }
}
