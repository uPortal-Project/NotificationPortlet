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
package org.jasig.portlet.notice.service.rest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.apereo.portal.soffit.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import javax.annotation.Resource;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of {@link IParameterEvaluator} based on user attributes.  Works with
 * both <code>PortletRequest</code> (though deprecated) and <code>HttpServletRequest</code>.
 *
 * @since 3.1
 */
public class UserAttributeParameterEvaluator extends AbstractParameterEvaluator {

    @Resource(name="soffitSignatureKey")
    private String signatureKey;

    private String userAttributeKey;
    private String claimName;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Defines the name of the user attribute that will be evaluated using the Portlet API.
     *
     * @param userAttributeKey The name of a user attribute, e.g. "user.login.id"
     * @deprecated Prefer interactions that are not based on the Portlet API
     */
    @Deprecated
    public void setUserAttributeKey(String userAttributeKey) {
        this.userAttributeKey = userAttributeKey;
    }

    /**
     * Defines the name of the user attribute that will be evaluated using the Servlet API and Open
     * ID Connect (OIDC).
     *
     * @param claimName The name of a claim in the OIDC Id token
     */
    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public String evaluate(PortletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String,String> userInfo = (Map<String,String>) request.getAttribute(PortletRequest.USER_INFO);
        return userInfo.get(this.userAttributeKey);
    }

    @Override
    public String evaluate(HttpServletRequest req) {

        String rslt = null;  // default

        final Jws<Claims> oidcToken = parseOidcToken(req);
        if (oidcToken != null) {
            final Object claimValue = oidcToken.getBody().get(claimName);

            if (claimValue == null) {
                //
            }
            else if (claimValue instanceof List<?>) {
                List<String> claimValues = (List<String>) claimValue;

                if(claimValues.size() > 1) {
                    logger.warn("{} contains more than one value, returning first value", this.claimName);
                }

                rslt = claimValues.get(0);
            }
            else {
                rslt = claimValue.toString();
            }
        }

        return rslt;

    }

    /*
     * Implementation
     */

    /**
     * Obtains information about the user from the Authorization header in the form of an OIDC Id
     * token.  In the future, it would be better to provide a standard tool (bean) for this job in
     * the <code>uPortal-soffit-renderer</code> component.
     */
    private Jws<Claims> parseOidcToken(HttpServletRequest req) {

        final String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authHeader)
                || !authHeader.startsWith(Headers.BEARER_TOKEN_PREFIX)) {
            /*
             * No OIDC token available
             */
            return null;
        }

        final String bearerToken = authHeader.substring(Headers.BEARER_TOKEN_PREFIX.length());

        try {
            // Validate & parse the JWT
            final Jws<Claims> rslt =
                    Jwts.parser().setSigningKey(signatureKey).parseClaimsJws(bearerToken);

            logger.debug("Found the following OIDC Id token:  {}", rslt.toString());

            return rslt;
        } catch (Exception e) {
            logger.info("The following Bearer token is unusable:  '{}'", bearerToken);
            logger.debug("Failed to validate and/or parse the specified Bearer token", e);
        }

        return null;

    }

}
