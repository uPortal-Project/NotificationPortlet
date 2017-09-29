package org.jasig.portlet.notice.service.jdbc;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.portlet.PortletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.portlet.notice.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Concrete implementation of {@link AbstractJdbcNotificationService} that returns a 100% static
 * {@link NotificationResponse} from the classpath if the configured SQL returns at least one row.
 * Otherwise it returns <code>NotificationResponse.EMTY_RESPONSE</code>.  The default
 * {@link SqlParameterSource} wraps the <code>PortletRequest.USER_INFO</code> collection, but
 * subclasses may override this strategy.
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

    /**
     * Implements this part of the operation by wrapping the <code>PortletRequest.USER_INFO</code>
     * map in a {@link MapSqlParameterSource}.
     */
    @Override
    protected SqlParameterSource getSqlParameterSource(PortletRequest req) {
        final Map<String, String> userInfo = (Map<String, String>) req.getAttribute(PortletRequest.USER_INFO);
        logger.debug("Notification service '{}' prepared the following SQL parameters" +
                "for user '{}':  {}", getName(), usernameFinder.findUsername(req), userInfo);
        return new MapSqlParameterSource(userInfo);
    }

    @Override
    protected ResultSetExtractor<NotificationResponse> getResultSetExtractor(PortletRequest req) {
        return rs -> {
            if (rs.next()) {
                // The user should get a the Banner Holds notification
                logger.debug("ResultSet NOT empty for notification service '{}'", getName());
                return nonEmptyResponse;
            } else {
                // The user does not need the Banner Holds notification
                logger.debug("ResultSet empty for notification service '{}'", getName());
                return NotificationResponse.EMPTY_RESPONSE;
            }
        };
    }

}
