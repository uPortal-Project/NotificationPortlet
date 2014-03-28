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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

/**
 * Parse the attributes list.  The attributes list may be in one of two forms:
 *
 * <blockquote><pre>
 * v1.0:
 * "attributes": [
 *     {
 *         "name": "category",
 *         "values": ["Academic"]
 *     },
 *     {
 *         "name": "department",
 *         "values": ["Physics and Astronomy", "Earth Sciences"]
 *     }
 *  ],
 *
 * v2:0
 * "attributes": {
 *     "category": ["Academic"],
 *     "department": ["Physics and Astronomy", "Earth Sciences"]
 * },
 * </pre></blockquote>
 *
 * This class dynamically determines which scenario applies and handles the parsing accordingly.
 *
 * @since 2.1
 * @author James Wennmacher jwennmacher@unicon.net
 */
public class JsonAttributesDeserializer extends JsonDeserializer<List<NotificationAttribute>> {
    
    @Override
    public List<NotificationAttribute> deserialize(JsonParser parser, DeserializationContext ctx)
            throws JsonParseException, IOException {

        List<NotificationAttribute> result = new ArrayList<NotificationAttribute>();
        JsonToken token = parser.getCurrentToken();

        // v1.0: array of objects
        if (token == JsonToken.START_ARRAY) {
            NotificationAttribute[] attributes = parser.readValueAs(NotificationAttribute[].class);
            result.addAll(Arrays.asList(attributes));
        } else if (token == JsonToken.START_OBJECT) {
            // v2.0: series of string arrays
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                NotificationAttribute attr = new NotificationAttribute();
                attr.setName(parser.getCurrentName());
                parser.nextToken();  // Should be the array of values
                String[] values = parser.readValueAs(String[].class);
                attr.setValues(Arrays.asList(values));
                result.add(attr);
            }
        } else throw new IllegalArgumentException("Invalid attributes value. Expected array of NotificationAttribute"
                + " objects or object with a series of string arrays. See https://issues.jasig.org/browse/NOTIFPLT-32"
                + " for format." + parser.getCurrentLocation().toString());

        return result;
        
    }

}
