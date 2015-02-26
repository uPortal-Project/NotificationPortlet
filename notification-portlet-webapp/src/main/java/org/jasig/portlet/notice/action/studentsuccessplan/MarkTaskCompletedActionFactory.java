package org.jasig.portlet.notice.action.studentsuccessplan;

import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.service.studentsuccessplan.ISSPApi;
import org.springframework.beans.factory.annotation.Autowired;

import javax.portlet.ActionRequest;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class MarkTaskCompletedActionFactory {
    private ISSPApi sspApi;


    @Autowired
    public void setSspApi(ISSPApi sspApi) {
        this.sspApi = sspApi;
    }


    private class MarkTaskCompletedAction extends NotificationAction {
        private String taskId;


        @Override
        public void invoke(ActionRequest req) {

        }
    }
}
