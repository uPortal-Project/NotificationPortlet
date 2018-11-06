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
package org.jasig.portlet.notice.service.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.Validate;
import org.jasig.portlet.notice.NotificationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * This DAO class handles raw CRUD operations for the JPA-flavor notifications.
 *
 * @since 3.0
 * @author drewwills
 */
@Repository
/* package-private */ class JpaNotificationDao implements INotificationDao {

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Search for a JpaEntry with the specified Id. If the entry exists in the
     * persistence context, it is returned;  otherwise, null is returned.
     */
    @Override
    @Transactional(readOnly=true)
    public JpaEntry getEntry(long entryId) {
        Validate.isTrue(entryId > 0, "Invalid entryId:  " + entryId);

        JpaEntry rslt = entityManager.find(JpaEntry.class, entryId);
        return rslt;
    }


    @Transactional(readOnly = true)
    public JpaEntry getFullEntry(long entryId) {
        Validate.isTrue(entryId > 0, "Invalid entryId: " + entryId);

        TypedQuery<JpaEntry> query = entityManager.createNamedQuery("JpaEntry.getFullById", JpaEntry.class);
        query.setParameter("entryId", entryId);
        return query.getSingleResult();
    }


    @Override
    @Transactional(readOnly = true)
    public List<JpaEntry> list(Integer page, Integer pageSize) {
        TypedQuery<JpaEntry> query = entityManager.createNamedQuery("JpaEntry.getAll", JpaEntry.class);

        if (page != null && pageSize != null) {
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JpaEntry> getNotificationsBySourceAndCustomAttribute(String source, String attributeName, String attributeValue) {
        final String jpql = "SELECT e FROM JpaEntry e "
                + "WHERE e.source = :source "
                + "AND EXISTS ("
                    + "SELECT a FROM JpaAttribute a "
                    + "WHERE a.entry = e "
                    + "AND a.name = :name "
                    + "AND :value MEMBER OF a.values)";
        final TypedQuery<JpaEntry> query = entityManager.createQuery(jpql, JpaEntry.class);
        query.setParameter("source", source);
        query.setParameter("name", attributeName);
        query.setParameter("value", attributeValue);
        return query.getResultList();
    }

    @Override
    @Transactional
    public JpaEntry createOrUpdateEntry(JpaEntry entry) {
        Validate.notNull(entry, "Argument 'entry' cannot be null");

        if (entry.getId() == 0) {
            // need to save and then flush to ensure the auto-generated
            // key value is populated in the entity.
            entityManager.persist(entry);
            entityManager.flush();
        } else {
            // should always work with the object returned from merge
            // rather than the original.
            entry = entityManager.merge(entry);
        }

        return entry;
    }

    @Override
    @Transactional
    public void removeEntry(JpaEntry entry) {
        Validate.notNull(entry, "Argument 'entry' cannot be null");

        JpaEntry y = entityManager.merge(entry);  // Insures that cascades will be handled properly
        entityManager.remove(y);
    }

    @Override
    @Transactional(readOnly=true)
    public Set<JpaEntry> getEntriesByRecipient(String username) {
        Validate.notEmpty(username, "Argument 'username' cannot be empty");

        final String jpql = "SELECT DISTINCT v.entry FROM JpaEvent v "
                                    + "WHERE v.username = :username";
        TypedQuery<JpaEntry> query = entityManager.createQuery(jpql, JpaEntry.class);

        log.debug("Query getEntriesByRecipient={}", query.toString());

        query.setParameter("username", username);
        List<JpaEntry> rslt = query.getResultList();
        return new HashSet<>(rslt);
    }

    @Override
    @Transactional(readOnly=true)
    public Set<JpaEntry> getEntriesByRecipientByStatus(String username,
            Set<NotificationState> include, Set<NotificationState> exclude) {
        throw new UnsupportedOperationException();
    }


    @Override
    @Transactional(readOnly = true)
    public List<JpaEvent> getEvents(long entryId) {
        Validate.isTrue(entryId > 0, "Argument 'entryId' must be greater than zero (0)");

        TypedQuery<JpaEvent> query = entityManager.createNamedQuery("JpaEvent.getAllByEntryId", JpaEvent.class);
        query.setParameter("entryId", entryId);

        List<JpaEvent> events = query.getResultList();
        return events;
    }


    @Override
    @Transactional(readOnly=true)
    public List<JpaEvent> getEvents(long entryId, String username) {
        Validate.isTrue(entryId > 0, "Argument 'entryId' must be greater than zero (0)");
        Validate.notEmpty(username, "Argument 'username' cannot be empty");

        final String jpql = "SELECT v FROM JpaEvent v WHERE v.username = :username "
                + "AND v.entry = (SELECT e FROM JpaEntry e WHERE e.id = :entryId) "
                + "ORDER BY v.timestamp";
        TypedQuery<JpaEvent> query = entityManager.createQuery(jpql, JpaEvent.class);
        query.setParameter("username", username);
        query.setParameter("entryId", entryId);
        List<JpaEvent> rslt = query.getResultList();
        return rslt;
    }


    @Override
    @Transactional
    public JpaAddressee createOrUpdateAddressee(JpaAddressee addressee) {
        Validate.notNull(addressee, "Argument 'addressee' cannot be null");

        if (addressee.getId() == 0) {
            entityManager.persist(addressee);
            entityManager.flush();
        } else {
            addressee = entityManager.merge(addressee);
        }

        return addressee;
    }


    @Override
    @Transactional(readOnly = true)
    public JpaAddressee getAddressee(long addresseeId) {
        Validate.isTrue(addresseeId > 0, "'addresseeId' must be greater than 0");

        JpaAddressee addr = entityManager.find(JpaAddressee.class, addresseeId);
        return addr;
    }


    @Override
    @Transactional
    public JpaEvent createOrUpdateEvent(JpaEvent event) {
        Validate.notNull(event, "Argument 'event' cannot be null");

        if (event.getId() == 0) {
            entityManager.persist(event);
            entityManager.flush();
        } else {
            event = entityManager.merge(event);
        }
        return event;
    }


    @Override
    @Transactional(readOnly = true)
    public JpaEvent getEvent(long eventId) {
        JpaEvent event = entityManager.find(JpaEvent.class, eventId);
        return event;
    }
}
