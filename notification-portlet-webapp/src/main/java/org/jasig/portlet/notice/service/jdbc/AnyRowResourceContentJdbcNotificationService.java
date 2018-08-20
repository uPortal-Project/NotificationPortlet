/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.notice.service.jdbc;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.portlet.notice.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Concrete implementation of {@link AbstractJdbcNotificationService} that returns a 100% static
 * {@link NotificationResponse} from the classpath if the configured SQL returns at least one row.
 * Otherwise it returns <code>NotificationResponse.EMTY_RESPONSE</code>.  The default
 * {@link SqlParameterSource} wraps the <code>PortletRequest.USER_INFO</code> collection (Portlet
 * API) or the OIDC Id Token (Servlet API), but subclasses may override this strategy.
 *
 * @since 3.2
 */
public class AnyRowResourceContentJdbcNotificationService extends AbstractJdbcNotificationService {

    // Managed by Spring
    private Resource jsonResource;

    // Managed internally
    private ObjectMapper objectMapper = new ObjectMapper();
    private NotificationResponse nonEmptyResponse;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Specify the complete content of the {@link NotificationResponse}.
     */
    public void setJsonResource(Resource jsonResource) {
        this.jsonResource = jsonResource;
    }

    /**
     * Subclasses <em>must</em> call <code>super.init()</code>.
     */
    @PostConstruct
    public void init() {
        super.init(); // Very important!
        try {
            nonEmptyResponse = objectMapper.readValue(jsonResource.getURL(), NotificationResponse.class);
        } catch (IOException ioe) {
            final String msg = "Failed to load JSON from resource:  " + jsonResource;
            throw new RuntimeException(msg, ioe);
        }
    }

    @Override
    protected ResultSetExtractor<NotificationResponse> getResultSetExtractor(PortletRequest req) {
        return getResultSetExtractor();
    }

    @Override
    protected ResultSetExtractor<NotificationResponse> getResultSetExtractor(HttpServletRequest request) {
        return getResultSetExtractor();
    }

    private ResultSetExtractor<NotificationResponse> getResultSetExtractor() {
        return rs -> {
            if (rs.next()) {
                // The user should get the configured notification
                logger.debug("ResultSet NOT empty for notification service '{}'", getName());
                return nonEmptyResponse;
            } else {
                // The user should NOT get the configured notification
                logger.debug("ResultSet empty for notification service '{}'", getName());
                return NotificationResponse.EMPTY_RESPONSE;
            }
        };
    }

}
