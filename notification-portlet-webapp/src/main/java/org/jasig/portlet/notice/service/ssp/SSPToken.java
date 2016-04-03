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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity to hold SSP authentication information.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class SSPToken {
    private static final long TIMEOUT_BUFFER = 5000;
    private long created = System.currentTimeMillis();
    private long expiresIn = 0;
    private String tokenType;
    private String accessToken;


    /**
     * Helper method to determine if an SSPToken has expired.
     */
    public boolean hasExpired() {
        long now = System.currentTimeMillis();
        if (created + (expiresIn * 1000) + TIMEOUT_BUFFER > now) {
            return false;
        }

        return true;
    }

    @JsonProperty("expires_in")
    public long getExpiresIn() {
        return expiresIn;
    }


    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }


    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }


    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }


    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }


    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
