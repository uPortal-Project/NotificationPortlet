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

package org.jasig.portlet.notice.action.read;

import java.util.HashSet;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationEntry;

public final class ReadAction extends NotificationAction{

    private static final long serialVersionUID = 1L;
    
    protected static Logger logger = LoggerFactory.getLogger(ReadAction.class);

    /**
     * This INSTANCE is only for convenience -- READ and UNREAD not singletons.
     * There may be situations where de-serialization will create additional
     * instances, and that's okay.
     */
    public static final ReadAction READ = new ReadAction();
    
    /**
     * Stores the Ids of read notices.
     */
    private static final String READ_NOTIFICATION_IDS_PREFERENCE = 
            ReadAction.class.getName() + ".READ_NOTIFICATION_IDS_PREFERENCE";
    
    /**
     * Must remain public, no-arg for de-serialization.
     */
    public ReadAction() {
        // Set a default label;  most use cases will use the setter and override
        setLabel("MARK AS READ");
    }
    
    public ReadAction(String label){
        setLabel(label);
    }
    
    public static final ReadAction createReadInstance() {
        return new ReadAction();
    }

    public static final ReadAction createUnReadInstance() {
        return new ReadAction("MARK AS UNREAD");
    }
    
    /**
     * Invoking a ReadAction toggles it.
     */
    @Override
    public void invoke(final ActionRequest req) {
        final NotificationEntry entry = getTarget();
        final String notificationId = entry.getId();
        final Set<String> readNotices = this.getReadNotices(req);
        if (readNotices.contains(notificationId)) {
            readNotices.remove(notificationId);
        } else {
            readNotices.add(notificationId);
        }
        setReadNotices(req, readNotices);
    }
    
    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).
                append(super.getId()).
                toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        // At present, any instance ReadAction is equal to another
        return true;
    }
    
    public void removeReadNotices (final PortletRequest req, Set<String> idsToRemove) {
        Set<String> currentIds = getReadNotices(req);
        currentIds.removeAll(idsToRemove);
        setReadNotices(req, currentIds);
    }
    
    /*
     * Non-public API
     */

    /* package-private */ Set<String> getReadNotices(final PortletRequest req) {
        final HashSet<String> rslt = new HashSet<String>();
        final PortletPreferences prefs = req.getPreferences();
        final String[] ids = prefs.getValues(READ_NOTIFICATION_IDS_PREFERENCE, new String[0]);
        for (int i=0; i < ids.length; i++) {
            rslt.add(ids[i]);
        }
        return rslt;
    }

    /* package-private */ void setReadNotices(final PortletRequest req, final Set<String> favoriteNotices) {
        final String[] ids = favoriteNotices.toArray(new String[favoriteNotices.size()]);
        final PortletPreferences prefs = req.getPreferences();
        try {
            prefs.setValues(READ_NOTIFICATION_IDS_PREFERENCE, ids);
            prefs.store();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
