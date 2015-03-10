package org.jasig.portlet.notice.service.ssp;

import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.service.ssp.ISSPApi;
import org.jasig.portlet.notice.service.ssp.SSPApiLocator;
import org.jasig.portlet.notice.service.ssp.SSPApiRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import java.io.IOException;

/**
 * Mark an SSP task as completed.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
/* package private */ class MarkTaskCompletedAction extends NotificationAction {

    private static final long serialVersionUID = 1L;

    private static final String MARK_TASK_COMPLETE_FRAGMENT = "/api/1/mygps/task/mark?taskId={taskId}&complete={completed}";
    private String taskId;


    public MarkTaskCompletedAction(final String taskId) {
        this.taskId = taskId;
        setLabel("MARK AS COMPLETED");
    }


    @Override
    public void invoke(ActionRequest req, ActionResponse res) throws IOException {
        ResponseEntity<String> updateResponse = null;

        @SuppressWarnings("unchecked")
        SSPApiRequest<String> updateReq = new SSPApiRequest<String>(MARK_TASK_COMPLETE_FRAGMENT, String.class)
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
