package org.jasig.portlet.notice.service.studentsuccessplan;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.service.studentsuccessplan.ISSPApi;
import org.jasig.portlet.notice.service.studentsuccessplan.SSPApi;
import org.jasig.portlet.notice.service.studentsuccessplan.SSPApiLocator;
import org.jasig.portlet.notice.service.studentsuccessplan.SSPApiRequest;
import org.jasig.portlet.notice.service.studentsuccessplan.SSPTaskNotificationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.portlet.ActionRequest;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * Mark an SSP task as completed.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
/* package private */ class MarkTaskCompletedAction extends NotificationAction {
    private static final long serialVersionUid = 1l;

    private static final String MARK_TASK_COMPLETE_FRAGMENT = "/api/1/mygps/task/mark?taskId={taskId}&complete={completed}";
    private String taskId;


    public MarkTaskCompletedAction(final String taskId) {
        this.taskId = taskId;
        setLabel("MARK AS COMPLETED");
    }


    @Override
    public void invoke(ActionRequest req) {
        ResponseEntity<String> updateResponse = null;
        SSPApiRequest updateReq = new SSPApiRequest(MARK_TASK_COMPLETE_FRAGMENT, String.class)
                .setMethod(HttpMethod.PUT)
                .addUriParameter("taskId", taskId)
                .addUriParameter("completed", true);

        try {
            // since these may be serialized and cached, need to actually look up the
            // service vs. injecting it or passing it in.
            ISSPApi sspApi = SSPApiLocator.getSSPApi();
            updateResponse = sspApi.doRequest(updateReq);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (updateResponse != null && updateResponse.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
            throw new RuntimeException("Error updating task: " + updateResponse.getBody());
        }
    }
}
