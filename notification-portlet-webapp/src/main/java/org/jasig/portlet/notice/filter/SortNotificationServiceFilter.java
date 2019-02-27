package org.jasig.portlet.notice.filter;

import net.bytebuddy.TypeCache;
import org.apache.commons.lang3.StringUtils;
import org.jasig.portlet.notice.*;
import org.jasig.portlet.notice.util.sort.SortStrategy;
import org.jasig.portlet.notice.util.sort.Sorting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;

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
