package org.jasig.portlet.notice.filter;

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.INotificationServiceFilterChain;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.util.sort.Sorting;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Sorts the output of {@link INotificationService} beans based on priority.
 */
@Component
public class SortNotificationServiceFilter extends AbstractNotificationServiceFilter {

    protected SortNotificationServiceFilter() {
        super(AbstractNotificationServiceFilter.ORDER_VERY_EARLY);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        final NotificationResponse unfiltered = chain.doFilter();

        return Sorting.sort(request.getParameter(Sorting.REQUEST_PARAM_SORT), request.getParameter(Sorting.REQUEST_PARAM_ORDER), unfiltered);
    }
}
