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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * Configuration of Spring Security for REST APIs.
 *
 * @since 4.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilter(preAuthenticatedProcessingFilter())
            .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/v1/**").access("hasRole('REST_READ') or hasRole('REST_WRITE')")
                .antMatchers(HttpMethod.POST,"/api/v1/**").hasRole("REST_WRITE")
                .antMatchers(HttpMethod.DELETE,"/api/v1/**").denyAll()
                .antMatchers(HttpMethod.PUT,"/api/v1/**").denyAll()
                .antMatchers(HttpMethod.GET,"/api/v2/**").authenticated()
                .antMatchers(HttpMethod.POST,"/api/v2/**").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/v2/**").denyAll()
                .antMatchers(HttpMethod.PUT,"/api/v2/**").denyAll()
                .anyRequest().permitAll();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new SoffitApiAuthenticationManager();
    }

    @Bean
    public AbstractPreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() {
        final AbstractPreAuthenticatedProcessingFilter rslt = new SoffitApiPreAuthenticatedProcessingFilter();
        rslt.setAuthenticationManager(authenticationManager());
        return rslt;
    }

}
