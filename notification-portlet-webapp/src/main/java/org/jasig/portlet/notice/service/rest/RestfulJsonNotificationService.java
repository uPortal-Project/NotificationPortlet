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

package org.jasig.portlet.notice.service.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public final class RestfulJsonNotificationService extends AbstractNotificationService {
    
    public static final String SERVICE_URLS_PREFERENCE = "RestfulJsonNotificationService.serviceUrls";

    private final NotificationResponse EMPTY_RESPONSE = new NotificationResponse();
    private final ResponseExtractor<NotificationResponse> responseExtractor = new ResponseExtractorImpl();

    // For HTTP Basic AuthN
    private IParameterEvaluator usernameEvaluator = null;
    private IParameterEvaluator passwordEvaluator = null;

    private Map<String,IParameterEvaluator> urlParameters;
    private RestTemplate restTemplate;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private UsernameFinder usernameFinder;

    @Required
    public void setUsernameEvaluator(IParameterEvaluator usernameEvaluator) {
        this.usernameEvaluator = usernameEvaluator;
    }

    @Required
    public void setPasswordEvaluator(IParameterEvaluator passwordEvaluator) {
        this.passwordEvaluator = passwordEvaluator;
    }

    public void setUrlParameters(Map<String,IParameterEvaluator> urlParameters) {
        this.urlParameters = new HashMap<String,IParameterEvaluator>(urlParameters);
    }

    @Required
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public NotificationResponse fetch(ResourceRequest req) {
        
        NotificationResponse rslt = EMPTY_RESPONSE;  // default is empty
        
        final PortletPreferences prefs = req.getPreferences();
        final Map<String,String> params = createParameters(req);
        final RequestCallback requestCallback = new RequestCallbackImpl(req);

        final String[] serviceUrls = prefs.getValues(SERVICE_URLS_PREFERENCE, new String[0]);
        for (final String url : serviceUrls) {
            if (log.isDebugEnabled()) {
                log.debug("Invoking uri '" + url + "' with the following parameters:  " + params.toString());
            }
            try {
                final NotificationResponse response = restTemplate.execute(
                        url, HttpMethod.GET, 
                        requestCallback, responseExtractor, params);
                rslt = rslt.combine(response);
            } catch (Exception e) {
                final String msg = "Failed to invoke the following service at '" 
                        + url + "' for user " + usernameFinder.findUsername(req);
                log.error(msg, e);
                rslt = prepareErrorResponse(getName(), "Service Unavailable");
            }
        }
        
        return rslt;

    }
    
    /*
     * Implementation
     */
    
    private Map<String,String> createParameters(PortletRequest req) {
        final Map<String,String> rslt = new HashMap<String,String>();
        for(final Map.Entry<String,IParameterEvaluator> y : urlParameters.entrySet()) {
            final String key = y.getKey();
            final String value = urlParameters.get(key).evaluate(req);
            rslt.put(key, value);
        }
        return rslt;
    }
    
    /*
     * Nested Types
     */
    
    private /* non-static */ final class RequestCallbackImpl implements RequestCallback {
        
        private final PortletRequest portletReq;
        
        public RequestCallbackImpl(PortletRequest portletReq) {
            this.portletReq = portletReq;
        }

        @Override
        public void doWithRequest(ClientHttpRequest httpReq) {

            final String username = usernameEvaluator.evaluate(portletReq);
            final String password = passwordEvaluator.evaluate(portletReq);

            // Perform BASIC AuthN if credentials are provided
            if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {

                if (log.isDebugEnabled()) {
                    final boolean hasPassword = password != null;
                    log.debug("Preparing ClientHttpRequest for user '" + username + "' (password provided = " +  hasPassword + ")");
                }

                final String authString = username.concat(":").concat(password);
                final String encodedAuthString = new Base64().encodeToString(authString.getBytes());
                httpReq.getHeaders().add("Authorization", "Basic ".concat(encodedAuthString));
            }

        }
        
    }

    private /* non-static */ final class ResponseExtractorImpl implements ResponseExtractor<NotificationResponse> {
        
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public NotificationResponse extractData(ClientHttpResponse res) {
            
            NotificationResponse rslt = null;
            
            InputStream inpt = null;
            try {
                inpt = res.getBody();
                rslt = mapper.readValue(inpt, NotificationResponse.class);
            } catch (Throwable t) {
                log.error("Failed to invoke the remote service at " + res.getHeaders().getLocation(), t);
                final NotificationError error = new NotificationError();
                try {
                    error.setError(res.getRawStatusCode() + ":  " + res.getStatusText());
                } catch (IOException e) {
                    log.error("Failed to read the ClientHttpResponse", e);
                }
                error.setSource(getClass().getSimpleName());
                rslt = new NotificationResponse();
                rslt.setErrors(Arrays.asList(new NotificationError[] { error }));
            }
            return rslt;
        }
        
    }

}
