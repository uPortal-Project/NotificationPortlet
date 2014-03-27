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

package org.jasig.portlet.notice.action.hide;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationEntry;

public final class HideAction extends NotificationAction {

    /**
     * This INSTANCE is only for convenience -- HideAction is not a singleton.  
     * There may be situations where de-serialization will create additional 
     * instances, and that's okay.
     */
    public static final HideAction INSTANCE = new HideAction();

    /**
     * Stores the Ids of hidden notices.
     */
    private static final String HIDDEN_NOTIFICATION_IDS_PREFERENCE = 
            HideAction.class.getName() + ".HIDDEN_NOTIFICATION_IDS_PREFERENCE";

    /**
     * Tracks when each notice was hidden.
     */
    private static final String HIDDEN_NOTIFICATION_TIMESTAMPS_PREFERENCE = 
            HideAction.class.getName() + ".HIDDEN_NOTIFICATION_TIMESTAMPS_PREFERENCE";

    private static final long MILLIS_IN_ONE_HOUR = 60L * 60L * 1000L;

    private static final long serialVersionUID = 1L;

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Must remain public, no-arg for de-serialization.
     */
    public HideAction() {
        // Set a default label;  most use cases will use the setter and override
        setLabel("HIDE");
    }

    /**
     * Invoking a HideAction toggles it.
     */
    @Override
    public void invoke(final ActionRequest req) {
        final NotificationEntry entry = getTarget();
        final String notificationId = entry.getId();
        final Map<String,Long> hiddenNoticesMap = this.getHiddenNoticesMap(req);
        if (hiddenNoticesMap.containsKey(notificationId)) {
            // Un-hide
            hiddenNoticesMap.remove(notificationId);
        } else {
            // Hide
            hiddenNoticesMap.put(notificationId, System.currentTimeMillis());
        }
        setHiddenNoticesMap(req, hiddenNoticesMap);
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
        // At present, any instance HideAction is equal to another
        return true;
    }

    /*
     * Non-public API
     */

    /**
     * Returns a mutable, empty map if the current data is in an unworkable state.
     */
    /* package-private */ Map<String,Long> getHiddenNoticesMap(final PortletRequest req) {

        final Map<String,Long> rslt = new HashMap<String,Long>();

        final PortletPreferences prefs = req.getPreferences();

        // Each hide action has a notification and a timestamp
        final String[] ids = prefs.getValues(HIDDEN_NOTIFICATION_IDS_PREFERENCE, new String[0]);
        final String[] timestamps = prefs.getValues(HIDDEN_NOTIFICATION_TIMESTAMPS_PREFERENCE, new String[0]);
        if (ids.length != timestamps.length) {
            log.warn("Collections for ids and timestamps were not the same length for user:  " + req.getRemoteUser());
        } else {
            // Build the Map
            try {
                final long hideDurationMillis = calculateHideDurationMillis(req);
                final boolean hideForever = hideDurationMillis == 0L;
                for (int i=0; i < ids.length; i++) {
                    final String id = ids[i];
                    final long timestamp = Long.parseLong(timestamps[i]);
                    if (hideForever || (timestamp + hideDurationMillis > System.currentTimeMillis())) {
                        rslt.put(id, timestamp);
                    }
                }
            } catch (NumberFormatException nfe) {
                log.warn("Failed to build the HideTuple collection for user:  " + req.getRemoteUser());
                throw new RuntimeException(nfe);
            }
        }

        /*
         * Update the data if (1) what we built is different from 
         * what we expected to build for any reason, and (2) we can
         */
        final boolean isActionPhase = req.getAttribute(PortletRequest.LIFECYCLE_PHASE)
                                            .equals(PortletRequest.ACTION_PHASE);
        if (rslt.size() != ids.length && isActionPhase) {
            setHiddenNoticesMap((ActionRequest) req, rslt);
        }

        return rslt;

    }

    /* package-private */ void setHiddenNoticesMap(final PortletRequest req, final Map<String,Long> hiddenNoticesMap) {
        final String[] ids = new String[hiddenNoticesMap.size()];
        final String[] timestamps = new String[hiddenNoticesMap.size()];
        int index = 0;
        for (Map.Entry<String,Long> y : hiddenNoticesMap.entrySet()) {
            ids[index] = y.getKey();
            timestamps[index++] = y.getValue().toString();
        }
        final PortletPreferences prefs = req.getPreferences();
        try {
            prefs.setValues(HIDDEN_NOTIFICATION_IDS_PREFERENCE, ids);
            prefs.setValues(HIDDEN_NOTIFICATION_TIMESTAMPS_PREFERENCE, timestamps);
            prefs.store();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* package-private */ long calculateHideDurationMillis(final PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        final String hideDurationHours = prefs.getValue(
                HideNotificationServiceDecorator.HIDE_DURATION_HOURS_PREFERENCE, 
                HideNotificationServiceDecorator.DEFAULT_HIDE_DURATION.toString());
        final long rslt = Long.parseLong(hideDurationHours) * MILLIS_IN_ONE_HOUR;
        return rslt;
    }

}
