package org.jasig.portlet.notice.filter;

import org.apache.commons.lang3.StringUtils;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.INotificationServiceFilterChain;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Filters the output of {@link INotificationService} beans based on priority.
 */
@Component
public class PriorityNotificationServiceFilter extends AbstractNotificationServiceFilter {

    /**
     * Priority 1 is the highest, so minPriority=2 means priority 1 or 2 (assuming the range is 1-5).
     */
    private static final String MIN_PRIORITY_PARAMETER_NAME = "minPriority";

    /**
     * Priority 1 is the highest, so maxPriority=4 means priority 4 or 5 (assuming the range is 1-5).
     */
    private static final String MAX_PRIORITY_PARAMETER_NAME = "maxPriority";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected PriorityNotificationServiceFilter() {
        super(AbstractNotificationServiceFilter.ORDER_LATE);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        final NotificationResponse unfiltered = chain.doFilter();

        final String minPriorityParameter = request.getParameter(MIN_PRIORITY_PARAMETER_NAME);
        final String maxPriorityParameter = request.getParameter(MAX_PRIORITY_PARAMETER_NAME);

        if (StringUtils.isNotBlank(minPriorityParameter) || StringUtils.isNotBlank(maxPriorityParameter)) {

            final Integer minPriority = StringUtils.isNotBlank(minPriorityParameter)
                    ? Integer.valueOf(minPriorityParameter)
                    : null;
            final Integer maxPriority = StringUtils.isNotBlank(maxPriorityParameter)
                    ? Integer.valueOf(maxPriorityParameter)
                    : null;

            // Sanity check...
            if (minPriority != null && maxPriority != null && maxPriority > minPriority) {
                logger.warn("Invalid parameters;  the value of '{}' ({}) was higher than the value of '{}' ({})",
                        MAX_PRIORITY_PARAMETER_NAME, maxPriority, MIN_PRIORITY_PARAMETER_NAME, minPriority);
                return NotificationResponse.EMPTY_RESPONSE;
            }

            return unfiltered.filter(entry -> {

                final int priority = entry.getPriority();

                if (minPriority != null && priority > minPriority) {
                    return false;
                }

                if (priority != NotificationEntry.PRIORITY_UNSPECIFIED
                        && maxPriority != null && priority < maxPriority) {
                    // Notifications with PRIORITY_UNSPECIFIED are not filtered-out by maxPriority.
                    return false;
                }

                return true;

            });

        } else {
            // We're not filtering, so pass on the unfiltered results...
            return unfiltered;
        }

    }

}
