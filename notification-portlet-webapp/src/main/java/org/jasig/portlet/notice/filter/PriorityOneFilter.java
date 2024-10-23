package org.jasig.portlet.notice.filter;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.jasig.portlet.notice.INotificationServiceFilterChain;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationAttribute;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.filter.AbstractNotificationServiceFilter;
import org.jasig.portlet.notice.filter.ReadStateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filter filters out ReadStateAction action(s) when the priority is 1. If the entry has an
 * attribute named canDismissPriorityOne with a value of true, this filter will not execute.
 */
public class PriorityOneFilter extends AbstractNotificationServiceFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected PriorityOneFilter() {
        super(AbstractNotificationServiceFilter.ORDER_LAST);
        logger.debug("Initializing PriorityOneFilter");
    }

    @Override
    public NotificationResponse doFilter(
            HttpServletRequest request, INotificationServiceFilterChain chain) {
        final NotificationResponse response = chain.doFilter();

        final NotificationResponse rslt = response.cloneIfNotCloned();

        for (NotificationCategory category : rslt.getCategories()) {

            for (NotificationEntry entry : category.getEntries()) {

                if (entry.getPriority() != 1) {
                    continue;
                }

                List<NotificationAttribute> attributes = entry.getAttributes();

                boolean canDismissPriorityOne =
                        attributes.stream()
                                .anyMatch(
                                        attribute ->
                                                attribute.getName().equals("canDismissPriorityOne")
                                                        && attribute.getValues().contains("true"));

                if (canDismissPriorityOne) {
                    continue;
                }

                List<NotificationAction> actions =
                        entry.getAvailableActions().stream()
                                .filter(action -> !ReadStateAction.class.isInstance(action))
                                .collect(Collectors.toList());

                entry.setAvailableActions(actions);
            }
        }

        return rslt;
    }
}
