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
package org.jasig.portlet.notice.service.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(getClass());

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

    /**
     * Gathers beans that implement {@link IParameterEvaluator} and prepares to leverage them in
     * dynamic URLs.
     *
     * @since 3.1
     */
    @Autowired
    public void setUrlParameterEvaluators(Set<IParameterEvaluator> evaluators) {
        final Map<String,IParameterEvaluator> map = new HashMap<>();
        evaluators.stream().forEach( evaluator -> map.put(evaluator.getToken(), evaluator));
        urlParameters = Collections.unmodifiableMap(map);
        logger.info("Found the following IParameterEvaluator beans:  {}", urlParameters);
    }

    @Required
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {
        
        NotificationResponse rslt = EMPTY_RESPONSE;  // default is empty
        
        final PortletPreferences prefs = req.getPreferences();
        final Map<String,String> params = createParameters(req);
        final RequestCallback requestCallback = new RequestCallbackImpl(req);

        final String[] serviceUrls = prefs.getValues(SERVICE_URLS_PREFERENCE, new String[0]);
        for (final String url : serviceUrls) {
            logger.debug("Invoking uri '{}' with the following parameters:  {}", url, params);
            try {
                final NotificationResponse response = restTemplate.execute(
                        url, HttpMethod.GET, 
                        requestCallback, responseExtractor, params);
                rslt = rslt.combine(response);
            } catch (Exception e) {
                final String msg = "Failed to invoke the following service at '" 
                        + url + "' for user " + usernameFinder.findUsername(req);
                logger.error(msg, e);
                rslt = prepareErrorResponse(getName(), "Service Unavailable");
            }
        }
        
        return rslt;

    }
    
    /*
     * Implementation
     */
    
    private Map<String,String> createParameters(PortletRequest req) {
        final Map<String,String> rslt = new HashMap<>();
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

                logger.debug("Preparing ClientHttpRequest for user '{}' (password provided = true)", username);

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
            
            NotificationResponse rslt;
            
            try (InputStream inpt = res.getBody()) {
                rslt = mapper.readValue(inpt, NotificationResponse.class);
                logger.debug("Produced the following NotificationResponse based on the ClientHttpResponse:  {}", rslt);
            } catch (Throwable t) {
                logger.error("Failed to invoke the remote service at " + res.getHeaders().getLocation(), t);
                final NotificationError error = new NotificationError();
                try {
                    error.setError(res.getRawStatusCode() + ":  " + res.getStatusText());
                } catch (IOException e) {
                    logger.error("Failed to read the ClientHttpResponse", e);
                }
                error.setSource(getClass().getSimpleName());
                rslt = new NotificationResponse();
                rslt.setErrors(Collections.singletonList(error));
            }
            return rslt;
        }
        
    }

}
