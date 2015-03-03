package org.jasig.portlet.notice.service.studentsuccessplan;

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
