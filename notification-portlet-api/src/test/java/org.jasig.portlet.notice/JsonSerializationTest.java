/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests JSON Serialization.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class JsonSerializationTest {

    ObjectMapper mapper;
    NotificationResponse resp;
    NotificationEntry entry;
    List<NotificationAttribute> attributes;

    @Before
    public void setup() {
        resp = new NotificationResponse();
        List<NotificationError> errors = new ArrayList<NotificationError>();
        errors.add(new NotificationError("error message", "source"));
        resp.setErrors(errors);

        List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
        NotificationCategory category = new NotificationCategory();
        category.setTitle("title");

        List<NotificationEntry> entries = new ArrayList<NotificationEntry>();
        entry = new NotificationEntry();
        entry.setFavorite(true);
        entry.setBody("body");

        attributes = new ArrayList<NotificationAttribute>();
        attributes.add(new NotificationAttribute("name1", "value"));
        attributes.add(new NotificationAttribute("name2", Arrays.asList(new String[] {"val1", "val2"})));
        entry.setAttributes(attributes);

        entries.add(entry);
        category.setEntries(entries);

        categories.add(category);
        resp.setCategories(categories);

        mapper = new ObjectMapper();
    }

    /**
     * Verify that List<NotificationAttributes> deserializes into the structure:
     * "attributes": {
     *     "category": ["Academic"],
     *     "department": ["Physics and Astronomy", "Earth Sciences"]
     * },
     *
     * @throws Exception
     */
    @Test
    public void testAttributeSerialization() throws Exception {
        String result = mapper.writeValueAsString(resp);
        assertTrue(result.contains("\"attributes\":{\"name1\":[\"value\"],\"name2\":[\"val1\",\"val2\"]}"));
    }

}
