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
