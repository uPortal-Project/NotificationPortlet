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

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toMap;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all the categories and errors retrieved by an INotificationService. It is
 * also used to aggregate all the NotificationResponses from various services into a single
 * NotificationResponse.  The data from the overall NotificationResponse instance is returned to the
 * portlet to be rendered.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationResponse implements Serializable, Cloneable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = 1L;

    /**
     * Useful for returning an empty set of notices.
     */
    public static final NotificationResponse EMPTY_RESPONSE = new NotificationResponse() {
        @Override
        public void setCategories(List<NotificationCategory> categories) {
            throw new UnsupportedOperationException("NotificationResponse.EMPTY_RESPONSE is and must be immutable");
        }
        @Override
        public void setErrors(List<NotificationError> errors) {
            throw new UnsupportedOperationException("NotificationResponse.EMPTY_RESPONSE is and must be immutable");
        }
    };

    @XmlTransient
    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<NotificationCategory> categories = new ArrayList<>();
    private List<NotificationError> errors = new ArrayList<>();

    /**
     * Field indicating the response is a cloned defensive copy and can be
     * modified (specifically,you can replace the collections with new
     * collections)
     */
    @JsonIgnore
    @XmlTransient
    private boolean cloned = false;

    public NotificationResponse() {}

    public NotificationResponse(NotificationResponse response) {
        this(response.getCategories(), response.getErrors());
    }

    public NotificationResponse(List<NotificationCategory> categories, List<NotificationError> errors) {
        this.setCategories(categories);
        this.setErrors(errors);
    }

    public List<NotificationCategory> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<NotificationCategory> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(
                    categories.stream()
                            .map(NotificationCategory::cloneNoExceptions)
                            .collect(Collectors.toList())
            );
        }
    }

    public List<NotificationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void setErrors(List<NotificationError> errors) {
        this.errors.clear();
        addErrors(errors);
    }

    /**
     * Returns the {@link NotificationEntry} with the specified id, or null if 
     * not present.
     */
    public NotificationEntry findNotificationEntryById(final String notificationId) {

        // Assertions
        if (notificationId == null) {
            String msg = "Argument 'notificationId' cannot be null";
            throw new IllegalArgumentException(msg);
        }

        // Providing a brute-force implementation for 
        // now;  we can improve it if it becomes important.
        NotificationEntry rslt = null;  // default -- means not present
        for (NotificationCategory category : categories) {
            for (NotificationEntry entry : category.getEntries()) {
                if (notificationId.equals(entry.getId())) {
                    rslt = entry;
                    break;
                }
            }
            if (rslt != null) {
                break;
            }
        }

        return rslt;

    }

    /**
     * Combine the contents of this response with the provided response and 
     * return a <b>new instance</b> of {@link NotificationResponse}.  The 
     * original instances are unchanged.
     * 
     * @param response A new {@link NotificationResponse} that contains data from both originals
     */
    public NotificationResponse combine(NotificationResponse response) {
        NotificationResponse rslt = new NotificationResponse(this);
        rslt.addCategories(response.getCategories());
        rslt.addErrors(response.getErrors());
        return rslt;
    }

    /**
     * Return a <b>new instance</b> of {@link NotificationResponse} from which 
     * the specified errors have been removed.  The original instances are 
     * unchanged.
     * 
     * @param hiddenErrorKeys set of error keys to exclude from result
     */
    public NotificationResponse filterErrors(Set<Integer> hiddenErrorKeys) {
        NotificationResponse rslt = new NotificationResponse(this);
        List<NotificationError> filteredErrors = new ArrayList<>();
        for (NotificationError r : errors) {
            if (!hiddenErrorKeys.contains(r.getKey())) {
                filteredErrors.add(r);
            }
        }
        rslt.setErrors(filteredErrors); // deep copy
        return rslt;
    }

    /**
     * Return a <b>new instance</b> of {@link NotificationResponse} containing only
     * {@link NotificationEntry} objects within this response that match the specified
     * <code>Predicate</code>.  The category structure is preserved.  Empty categories are removed.
     */
    public NotificationResponse filter(Predicate<NotificationEntry> predicate) {

        final List<NotificationCategory> filteredCategories = categories.stream()
                .map(category -> {
                    final List<NotificationEntry> filteredEntries = category.getEntries().stream()
                            .filter(predicate)
                            .collect(Collectors.toList());
                    return filteredEntries.size() > 0
                            ? new NotificationCategory(category.getTitle(), filteredEntries)
                            : null;
                })
                .filter(value -> value != null)
                .collect(Collectors.toList());
        return new NotificationResponse(filteredCategories, getErrors()); // deep copy

    }

    /**
     * Return a <b>new instance</b> of {@link NotificationResponse} with the
     * {@link NotificationEntry} objects within this response sorted against a specified
     * <code>Comparator</code>.  The category structure is preserved.
     */
    public NotificationResponse sort(Comparator<NotificationEntry> sorter) {

        final List<NotificationCategory> newCategories = categories.stream()
                .map(category -> {
                    final List<NotificationEntry> sortedEntries = category.getEntries().stream()
                            .sorted(sorter)
                            .collect(Collectors.toList());
                    for(NotificationEntry ne : sortedEntries) {
                        logger.info("Notification entry found after sorting!  " + category.getTitle() + " - " + ne.getLinkText() + ", due=" + ne.getDueDate());
                    }
                    return sortedEntries.size() > 0
                            ? new NotificationCategory(category.getTitle(), sortedEntries)
                            : null;
                })
                .collect(Collectors.toList());
        return new NotificationResponse(newCategories, getErrors()); // deep copy

    }

    /**
     * Provides the total number of notifications contained in the response.
     */
    @JsonIgnore
    @XmlTransient
    public int size() {
        return categories.stream()
                .map(NotificationCategory::getEntries)
                .mapToInt(List::size)
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationResponse [categories=");
        builder.append(categories);
        builder.append(", errors=");
        builder.append(errors);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Implements deep-copy clone.
     *
     * @throws CloneNotSupportedException Not really, but it's on the method 
     * signature we're overriding.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {

        // Start with superclass impl (handles immutables and primitives)
        final NotificationResponse rslt = (NotificationResponse) super.clone();

        // Adjust to satisfy deep-copy strategy
        List<NotificationCategory> cList = new ArrayList<>(categories.size());
        for (NotificationCategory category : categories) {
            cList.add((NotificationCategory) category.clone());
        }
        rslt.setCategories(cList);
        List<NotificationError> eList = new ArrayList<>(errors.size());
        for (NotificationError error : errors) {
            eList.add((NotificationError) error.clone());
        }
        rslt.setErrors(eList);
        rslt.setCloned(true);

        return rslt;

    }

    /*
     * Implementation
     */

    /** 
     * Insert the given categories and their entries into the any existing
     * categories of the same title. If a category doesn't match an existing
     * one, add it to the list.
     * @param newCategories collection of new categories and their entries.
     */
    private void addCategories(List<NotificationCategory> newCategories) {
        if (newCategories == null) {
            return;
        }

        // Start with a deep copy of the method parameter to simplify remaining logic
        newCategories = newCategories.parallelStream().map(NotificationCategory::cloneNoExceptions).collect(Collectors.toList());

        // Create a map of current categories by title for processing
        Map<String, NotificationCategory> catsByName =
                this.categories.parallelStream().collect(toMap(c -> c.getTitle().toLowerCase(), c -> c));

        // Split new categories between those that match an existing category and those that are completely new
        Map<Boolean, List<NotificationCategory>> matchingNewCats =
                newCategories.stream()
                        .collect(partitioningBy(c -> catsByName.containsKey(c.getTitle().toLowerCase())));

        // Add new entries to existing categories
        matchingNewCats.get(Boolean.TRUE).stream()
                .forEachOrdered(c -> catsByName.get(c.getTitle().toLowerCase()).addEntries(c.getEntries()));

        // Add new categories
        this.categories.addAll(matchingNewCats.get(Boolean.FALSE));
    }

    private void addErrors(List<NotificationError> newErrors) {
        if (newErrors != null) {
            this.errors.addAll(
                    newErrors.stream()
                            .map(NotificationError::cloneNoExceptions)
                            .collect(Collectors.toList())
            );
        }
    }

    /**
     * Best guess as to the reason this method exists: beans that produce fresh
     * {@link NotificationResponse} objects (without cloning them) may safely cache them.  They need
     * not fear that another bean will transform their copy.  Beans that do clone them, however, can
     * safely operate on a clone.
     */
    public NotificationResponse cloneIfNotCloned() {
        try {
            return isCloned() ? this : (NotificationResponse) this.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Failed to clone() the sourceResponse", e);
        }
        return this;
    }

    private boolean isCloned() {
        return cloned;
    }

    private void setCloned(boolean cloned) {
        this.cloned = cloned;
    }

}
