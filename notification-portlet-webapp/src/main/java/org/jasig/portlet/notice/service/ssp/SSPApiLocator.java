/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.notice.service.ssp;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Helper method to keep a static reference to the SSPAPI object.   Since
 * Actions are attached to NotificationEntries which should be serializable,
 * this takes care of looking up the service so that it does not need to be
 * injected into the Action.
 *
 * This should be declared as a bean (unnamed is fine) in your spring
 * context.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class SSPApiLocator implements ApplicationContextAware {
    private static ISSPApi instance;


    /**
     * Get the SSPApi instance the action should use.
     * @return the SSPApi instance.
     */
    public synchronized static ISSPApi getSSPApi() {
        return instance;
    }


    /**
     * Set the SSPApi instance.
     *
     * @param sspApi
     */
    private synchronized static void setSSPApi(ISSPApi sspApi) {
        instance = sspApi;
    }


    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        ISSPApi sspApi = appContext.getBean(ISSPApi.class);

        setSSPApi(sspApi);
    }
}
