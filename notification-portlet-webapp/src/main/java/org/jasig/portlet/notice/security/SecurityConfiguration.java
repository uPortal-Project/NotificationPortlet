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
package org.jasig.portlet.notice.security;

import org.apereo.portal.soffit.security.SoffitApiAuthenticationManager;
import org.apereo.portal.soffit.security.SoffitApiPreAuthenticatedProcessingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.apereo.portal.soffit.service.AbstractJwtService.DEFAULT_SIGNATURE_KEY;
import static org.apereo.portal.soffit.service.AbstractJwtService.SIGNATURE_KEY_PROPERTY;

/**
 * Configuration of Spring Security for REST APIs.  (Portlet requests are not covered by these
 * settings.)
 *
 * @since 4.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * String representation of the 'Portal Administrators' group in uPortal.
     */
    private static final String PORTAL_ADMINISTRATORS_AUTHORITY = "Portal Administrators";

    @Value("${" + SIGNATURE_KEY_PROPERTY + ":" + DEFAULT_SIGNATURE_KEY + "}")
    private String signatureKey;

    @Value("${security.restV1ReadAuthority:" + PORTAL_ADMINISTRATORS_AUTHORITY + "}")
    private String restV1ReadAuthority;

    @Value("${security.restV1WriteAuthority:" + PORTAL_ADMINISTRATORS_AUTHORITY + "}")
    private String restV1WriteAuthority;

    @Override
    public void configure(WebSecurity web) throws Exception {
        /*
         * Since this module includes portlets, we only want to apply Spring Security to requests
         * targeting out REST APIs.
         */
        final RequestMatcher pathMatcher = new AntPathRequestMatcher("/api/**");
        final RequestMatcher inverseMatcher = new NegatedRequestMatcher(pathMatcher);
        web.ignoring().requestMatchers(inverseMatcher);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /*
         * Provide a SoffitApiPreAuthenticatedProcessingFilter (from uPortal) that is NOT a
         * top-level bean in the Spring Application Context.
         */
        final AbstractPreAuthenticatedProcessingFilter filter =
                new SoffitApiPreAuthenticatedProcessingFilter(signatureKey);
        filter.setAuthenticationManager(authenticationManager());

        http
            .addFilter(filter)
            .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/v1/**").access("hasAuthority('" + restV1ReadAuthority + "') or hasAuthority('" + restV1WriteAuthority + "')")
                .antMatchers(HttpMethod.POST,"/api/v1/**").hasAuthority(restV1WriteAuthority)
                .antMatchers(HttpMethod.DELETE,"/api/v1/**").denyAll()
                .antMatchers(HttpMethod.PUT,"/api/v1/**").denyAll()
                .antMatchers(HttpMethod.GET,"/api/v2/**").authenticated()
                .antMatchers(HttpMethod.POST,"/api/v2/**").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/v2/**").denyAll()
                .antMatchers(HttpMethod.PUT,"/api/v2/**").denyAll()
                .anyRequest().permitAll()
            .and()
            /*
             * Session fixation protection is provided by uPortal.  Since portlet tech requires
             * sessionCookiePath=/, we will make the portal unusable if other modules are changing
             * the sessionId as well.
             */
            .sessionManagement()
                .sessionFixation().none()
            .and()
            /*
             * Portlet POST requests include (Spring-based) CSRF protection managed by uPortal.
             * REST APIs are secured by OIDC Id tokens.
             */
            .csrf()
                .ignoringAntMatchers("/api/v1/**")
                .ignoringAntMatchers("/api/v2/**");

    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new SoffitApiAuthenticationManager();
    }

    @Bean("soffitSignatureKey")
    public String signatureKey() {
        return signatureKey;
    }
}
