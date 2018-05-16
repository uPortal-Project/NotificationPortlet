package org.jasig.portlet.notice.filter;

import org.apache.commons.lang3.StringUtils;
import org.jasig.portlet.notice.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This {@link INotificationServiceFilter} sets the <code>apiUrl</code> property on
 * {@link NotificationAction} objects within the {@link NotificationResponse}.
 *
 * @since 4.0
 */
@Component
public class ApiUrlSupportFilter extends AbstractNotificationServiceFilter {

    /**
     * The format strings are as follows:
     * <ul>
     *   <li>protocol, host, port (if applicable), and context</li>
     *   <li>action id</li>
     *   <li>notification id</li>
     *   <li>Spring CSRF token</li>
     * </ul>
     */
    private static final String REST_API_URL_FORMAT = "%s/api/v2/action/%s/%s?_csrf=%s";

    /**
     * This {@link INotificationServiceFilter} must do its work late in the chain because filters
     * commonly add actions.
     */
    public ApiUrlSupportFilter() {
        super(AbstractNotificationServiceFilter.ORDER_VERY_LATE);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        final NotificationResponse response = chain.doFilter();

        final NotificationResponse rslt = response.cloneIfNotCloned();

        // Add apiUrl values to actions with our copy
        for (NotificationCategory category : rslt.getCategories()) {

            for (NotificationEntry entry : category.getEntries()) {

                final List<NotificationAction> actions = entry.getAvailableActions().stream()
                        .map(action -> {
                            if (StringUtils.isNotBlank(action.getId())
                                    && action.getTarget() != null
                                    && StringUtils.isNotBlank(action.getTarget().getId())) {
                                // Pick up scheme, host[, port,] and context from the request
                                final String requestUrl = request.getRequestURL().toString();
                                final String contextPath = request.getContextPath();
                                final String urlBase = requestUrl.substring(0,
                                        requestUrl.indexOf(contextPath)) + contextPath;
                                final CsrfToken csrf =
                                        (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                                final String apiUrl = String.format(REST_API_URL_FORMAT,
                                        urlBase,
                                        action.getId(),
                                        action.getTarget().getId(),
                                        csrf != null ? csrf.getToken() : null);
                                action.setApiUrl(apiUrl);
                            }
                            return action;
                        })
                        .collect(Collectors.toList());
                entry.setAvailableActions(actions);

            }

        }

        return rslt;

    }

}
