package org.jasig.portlet.notice.filter;

import net.bytebuddy.TypeCache;
import org.apache.commons.lang3.StringUtils;
import org.jasig.portlet.notice.*;
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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected SortNotificationServiceFilter() {
        super(AbstractNotificationServiceFilter.ORDER_VERY_EARLY);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        final NotificationResponse unfiltered = chain.doFilter();

        for(NotificationCategory nc : unfiltered.getCategories()) {
            for(NotificationEntry ne : nc.getEntries()) {
                logger.info("Notification entry found before sorting!  " + nc.getTitle() + " - " + ne.getLinkText() + ", due=" + ne.getDueDate());
            }
        }

        return unfiltered.sort(new Comparator<NotificationEntry>() {
            @Override
            public int compare(NotificationEntry ne1, NotificationEntry ne2) {
                // Currently there's only a need to sort via a single strategy, thus no parameter in the request.  If enhanced to allow a choice for sort options, this would be a good choice for implementing various sort options.
                return ne1.getDueDate().compareTo(ne2.getDueDate());
            }
        });
    }

}
