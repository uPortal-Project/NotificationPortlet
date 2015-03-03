package org.jasig.portlet.notice.service.studentsuccessplan;

import org.codehaus.jackson.annotate.JsonProperty;

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
