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

import java.util.HashSet;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationEntry;

public final class FavoriteAction extends NotificationAction {

    /**
     * This INSTANCE is only for convenience -- FAVORITE and UNFAVORITE not singletons.
     * There may be situations where de-serialization will create additional
     * instances, and that's okay.
     */
    public static final FavoriteAction FAVORITE = new FavoriteAction();

    /**
     * Stores the Ids of notices marked as favorite.
     */
    private static final String FAVORITE_NOTIFICATION_IDS_PREFERENCE =
            FavoriteAction.class.getName() + ".FAVORITE_NOTIFICATION_IDS_PREFERENCE";

    private static final long serialVersionUID = 1L;

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Must remain public, no-arg for de-serialization.
     */
    public FavoriteAction() {
        // Set a default label;  most use cases will use the setter and override
        setLabel("FAVORITE");
    }

    public FavoriteAction(String label) {
        setLabel(label);
    }

    public static final FavoriteAction createFavoriteInstance() {
        return new FavoriteAction();
    }

    public static final FavoriteAction createUnfavoriteInstance() {
        return new FavoriteAction("UNFAVORITE");
    }

    /**
     * Invoking a FavoriteAction toggles it.
     */
    @Override
    public void invoke(final ActionRequest req) {
        final NotificationEntry entry = getTarget();
        final String notificationId = entry.getId();
        final Set<String> favoriteNotices = this.getFavoriteNotices(req);
        if (favoriteNotices.contains(notificationId)) {
            favoriteNotices.remove(notificationId);
        } else {
            favoriteNotices.add(notificationId);
        }
        setFavoriteNotices(req, favoriteNotices);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final String id = getId();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        // At present, any instance FavoriteAction is equal to another
        return true;
    }

    public void removeFavoriteNotices (final PortletRequest req, Set<String> idsToRemove) {
        Set<String> currentIds = getFavoriteNotices(req);
        currentIds.removeAll(idsToRemove);
        setFavoriteNotices(req, currentIds);
    }

    /*
     * Non-public API
     */

    /* package-private */ Set<String> getFavoriteNotices(final PortletRequest req) {

        final HashSet<String> rslt = new HashSet<String>();

        final PortletPreferences prefs = req.getPreferences();
        final String[] ids = prefs.getValues(FAVORITE_NOTIFICATION_IDS_PREFERENCE, new String[0]);

        for (int i=0; i < ids.length; i++) {
            rslt.add(ids[i]);
        }
        return rslt;

    }

    /* package-private */ void setFavoriteNotices(final PortletRequest req, final Set<String> favoriteNotices) {
        final String[] ids = favoriteNotices.toArray(new String[favoriteNotices.size()]);
        final PortletPreferences prefs = req.getPreferences();
        try {
            prefs.setValues(FAVORITE_NOTIFICATION_IDS_PREFERENCE, ids);
            prefs.store();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
