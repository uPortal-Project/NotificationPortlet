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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests JSON Deserialization.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class JsonDeserializationTest {
    ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testAttributeDeserializationv1() throws Exception {
        InputStream jsonFile = getClass().getResourceAsStream("/objectWithAttributesv1.json");
        validateJsonAttributes(jsonFile);
    }

    @Test
    public void testAttributeDeserializationv2() throws Exception {
        InputStream jsonFile = getClass().getResourceAsStream("/objectWithAttributesv2.json");
        validateJsonAttributes(jsonFile);
    }

    private void validateJsonAttributes(InputStream jsonFile) throws IOException {
        NotificationResponse resp = mapper.readValue(jsonFile, NotificationResponse.class);
        assertTrue(resp.getCategories().size() == 1);
        assertTrue(resp.getCategories().get(0).getEntries().size() == 1);
        NotificationEntry entry = resp.getCategories().get(0).getEntries().get(0);
        assertTrue(entry.getAttributes().size() == 3);

        List<NotificationAttribute> attrs = entry.getAttributes();
        assertEquals("noValue", attrs.get(0).getName());
        assertTrue(attrs.get(0).getValues().size() == 0);

        assertEquals("oneValue", attrs.get(1).getName());
        assertTrue(attrs.get(1).getValues().size() == 1);

        assertEquals("multipleValues", attrs.get(2).getName());
        assertTrue(attrs.get(2).getValues().size() == 2);
    }

}
