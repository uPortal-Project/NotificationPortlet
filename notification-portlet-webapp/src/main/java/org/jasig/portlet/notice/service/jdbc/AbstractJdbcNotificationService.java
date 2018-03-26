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
package org.jasig.portlet.notice.service.jdbc;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.sql.DataSource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Base class for {@link INotificationService} implementations that pull notifications from JDBC
 * data sources using common patterns.
 *
 * @since 3.2
 */
public abstract class AbstractJdbcNotificationService extends AbstractNotificationService {

    // These items are provided by Spring and/or the subclass
    private DataSource dataSource;
    private String sql;
    private Cache cache;

    // These items are managed internally
    private NamedParameterJdbcTemplate jdbcTemplate;
    private Set<String> requiredParameters = Collections.emptySet();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Required
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Resource(name="AbstractJdbcNotificationService.responseCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Implementors <em>may</em> may call this method to specify parameters that must be present in
     * the {@link SqlParameterSource}.
     */
    public void setRequiredParameters(Set<String> requiredParameters) {
        this.requiredParameters = Collections.unmodifiableSet(requiredParameters);
    }

    @PostConstruct
    public void init() {
        logger.debug("Initializing AbstractJdbcNotificationService where name={}", getName());
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void invoke(final ActionRequest req, final ActionResponse res, final boolean refresh) {

        final String username = usernameFinder.findUsername(req);

        logger.debug("Performing AbstractJdbcNotificationService.invoke() for user='{}' refresh={}",
                username, refresh);

        if (refresh) {
            final CacheKey cacheKey = new CacheKey(getName(), username, sql);
            cache.remove(cacheKey);
        }

    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {

        NotificationResponse rslt;

        final String username = usernameFinder.findUsername(req);
        final CacheKey cacheKey = new CacheKey(getName(), username, sql);

        final Element m = cache.get(cacheKey);
        if (m != null) {
            // Cache hit
            rslt = (NotificationResponse) m.getObjectValue();
            logger.debug("Found the following response for user='{}' from cache:  {}", username, rslt);
        } else {
            // Cache miss
            final SqlParameterSource sqlParameterSource = getSqlParameterSource(req);
            final ResultSetExtractor<NotificationResponse> resultSetExtractor = getResultSetExtractor(req);

            // Do we have what we need?
            boolean hasAllParameters = true;
            for (String parameter : requiredParameters) {
                if (!sqlParameterSource.hasValue(parameter)) {
                    logger.debug("Skipping notification service='{}' for user='{}' because " +
                            "required parameter '{}' was not present",
                            getName(), username, parameter);
                    hasAllParameters = false;
                    break;
                }
            }

            if (hasAllParameters) {
                rslt = jdbcTemplate.query(sql, sqlParameterSource, resultSetExtractor);
            } else {
                rslt = NotificationResponse.EMPTY_RESPONSE;
            }

            cache.put(new Element(cacheKey, rslt));
            logger.debug("Notification service '{}' generated the following response" +
                    "for user='{}':  {}", getName(), username, rslt);
        }

        return rslt;

    }

    /**
     * Subclasses <em>may</em> override this method to provide a custom {@link SqlParameterSource}.
     */
    protected SqlParameterSource getSqlParameterSource(PortletRequest req) {
        return EmptySqlParameterSource.INSTANCE;
    }

    /**
     * Subclasses <em>must</em> override this method to provide a custom {@link ResultSetExtractor}.
     */
    protected abstract ResultSetExtractor<NotificationResponse> getResultSetExtractor(PortletRequest req);

    /*
     * Nested Types
     */

    private static final class CacheKey implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String serviceName;
        private final String username;
        private final String sql;

        public CacheKey(String serviceName, String username, String sql) {
            this.serviceName = serviceName;
            this.username = username;
            this.sql = sql;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("serviceName", serviceName)
                    .append("username", username)
                    .append("sql", sql)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            return new EqualsBuilder()
                    .append(serviceName, cacheKey.serviceName)
                    .append(username, cacheKey.username)
                    .append(sql, cacheKey.sql)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(serviceName)
                    .append(username)
                    .append(sql)
                    .toHashCode();
        }
    }

}
