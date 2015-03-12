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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 */
public interface ISSPApi {
    /**
     * Make a request to the SSP REST API.
     *
     * @param request the SSP Request
     * @param <T> The type of the response content
     * @return A response entity that holds the SSP response
     * @throws MalformedURLException if the URL is invalid
     * @throws RestClientException if an error occurs communicating with SSP
     */
    public <T> ResponseEntity<T> doRequest(SSPApiRequest<T> request) throws MalformedURLException, RestClientException;


    /**
     * Get an SSP URL.
     *
     * @param urlFragment The URL fragment.
     * @param useContext if True, prepend the SSP api context, otherwise just use urlFragment as the path
     * @return A URL object
     * @throws MalformedURLException if the URL can not be constructed
     */
    public URL getSSPUrl(String urlFragment, boolean useContext) throws MalformedURLException;
}
