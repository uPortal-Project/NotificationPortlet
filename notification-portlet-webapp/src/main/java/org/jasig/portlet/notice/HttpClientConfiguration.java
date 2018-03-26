/*
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
package org.jasig.portlet.notice;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {

    private static final int MAX_TOTAL_CONNECTIONS = 1000;

    @Bean(name = "httpConnectionManager", destroyMethod = "shutdown")
    public HttpClientConnectionManager httpConnectionManager() {
        PoolingHttpClientConnectionManager rslt = new PoolingHttpClientConnectionManager();
        rslt.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        return rslt;
    }

    @Bean("httpClient")
    public HttpClient httpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(httpConnectionManager());
        return builder.build();
    }

}
