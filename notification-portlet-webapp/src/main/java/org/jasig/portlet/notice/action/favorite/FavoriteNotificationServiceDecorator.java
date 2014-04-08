/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice.action.favorite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;

/**
 * This class can be used to add a "favorite" or "snooze" feature to notifications 
 * that have an id attribute.  By default, notices will be hidden for 365 days 
 * (effectively permanent).
 * 
 * @author James Wennmacher jwennmacher@unicon.net
 */
public class FavoriteNotificationServiceDecorator implements INotificationService {

    public static final String FAVORITE_ENABLED_PREFERENCE = "FavoriteNotificationServiceDecorator.enabled";
    public static final String DEFAULT_FAVORITE_BEHAVIOR = "false";  // The feature is disabled by default

    // Instance members
    private INotificationService enclosedNotificationService;
    private final Log log = LogFactory.getLog(getClass());

    public void setEnclosedNotificationService(INotificationService enclosedNotificationService) {
        this.enclosedNotificationService = enclosedNotificationService;
    }

    @Override
    public String getName() {
        return enclosedNotificationService.getName();
    }

    @Override
    public void invoke(ActionRequest req, ActionResponse res, boolean refresh) {
        enclosedNotificationService.invoke(req, res, refresh);
    }

    @Override
    public void collect(EventRequest req, EventResponse res) {
        enclosedNotificationService.collect(req, res);
    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {

        // Just pass through the enclosed collection if this feature is disabled
        if (!favoritesEnabled(req)) {
            return enclosedNotificationService.fetch(req);
        }

        // Build a fresh NotificationResponse based on a deep-copy of the one we enclose
        final NotificationResponse sourceResponse = enclosedNotificationService.fetch(req);
        NotificationResponse rslt = sourceResponse.cloneIfNotCloned();

        final Set<String> favoriteNotificationIds = FavoriteAction.FAVORITE.getFavoriteNotices(req);
        Set<String> potentiallyMissingIds = new HashSet<String>(favoriteNotificationIds);

        // Add and implement the favorite behavior with our copy
        for (NotificationCategory category : rslt.getCategories()) {

            for (NotificationEntry entry : category.getEntries()) {

                final List<NotificationAction> currentList = entry.getAvailableActions();

                /*
                 * There are 2 requirements for an entry to be decorated with Favorite behavior:
                 * 
                 *   - (1) It must have an id set
                 *   - (2) It must not have a FavoriteAction already
                 */
                if (StringUtils.isNotBlank(entry.getId())) {
                    // If the id is in the favorites list, set favorite=true and remove the ID from the potentially
                    // missing set.
                    if (favoriteNotificationIds.contains(entry.getId())) {
                        entry.setFavorite(true);
                        potentiallyMissingIds.remove(entry.getId());
                    }
                    if (!currentList.contains(FavoriteAction.FAVORITE)) {
                        final List<NotificationAction> replacementList = new ArrayList<NotificationAction>(currentList);
                        replacementList.add(!entry.isFavorite() ?
                                FavoriteAction.createFavoriteInstance() : FavoriteAction.createUnfavoriteInstance());
                        entry.setAvailableActions(replacementList);
                    }
                }
            }
        }

        // If there were no errors from the sources and there were favorite IDs that were not in the results, remove
        // them so we don't have them build up over time if the user doesn't un-favorite an item that goes away.
        if (rslt.getErrors().isEmpty() && potentiallyMissingIds.size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Removing " + potentiallyMissingIds.size() + " unreferenced favorites for user "
                        + req.getRemoteUser());
            }
            FavoriteAction.FAVORITE.removeFavoriteNotices(req, potentiallyMissingIds);
        }
        return rslt;

    }

    private boolean favoritesEnabled(PortletRequest request) {
        PortletPreferences prefs = request.getPreferences();
        return Boolean.valueOf(prefs.getValue(FAVORITE_ENABLED_PREFERENCE, DEFAULT_FAVORITE_BEHAVIOR));
    }

    @Override
    public boolean isValid(PortletRequest req, NotificationResponse previousResponse) {
        return enclosedNotificationService.isValid(req, previousResponse);
    }

}
