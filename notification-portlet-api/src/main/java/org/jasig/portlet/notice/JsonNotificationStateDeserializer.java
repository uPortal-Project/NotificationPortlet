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

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class JsonNotificationStateDeserializer extends JsonDeserializer<NotificationState> {

    private static final String CLAZZ_FILDNAME = "clazz";
    private static final String LABEL_FILDNAME = "label";
    private static final String TIMESTAMP_FILDNAME = "timestamp";

    @Override
    public NotificationState deserialize(final JsonParser parser, final DeserializationContext ctx) 
            throws IOException {

        // Things we need to capture
        String clazz = null;
        String label = null;
        long timestamp = 0L;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldname = parser.getCurrentName();
            switch(fieldname) {
                case CLAZZ_FILDNAME:
                    parser.nextToken();  // Advance to the next token;  it will be the clazz
                    clazz = parser.getText();
                    break;
                case LABEL_FILDNAME:
                    parser.nextToken();  // Advance to the next token;  it will be the label
                    label = parser.getText();
                    break;
                case TIMESTAMP_FILDNAME:
                    parser.nextToken();  // Advance to the next token;  it will be the timestamp
                    timestamp = Long.parseLong(parser.getText());
                    break;
            }
        }

        if (clazz == null) {
            // We can't proceed
            final String msg = "Unable to deserialize NotificationState due to missing clazz field";
            throw new IllegalArgumentException(msg);
        }

        NotificationState rslt;
        try {
            rslt = (NotificationState) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate a NotificationState of concrete class:  " + clazz);
        }

        if (label != null) {
            rslt.setLabel(label);
        }
        rslt.setTimestamp(timestamp);

        return rslt;

    }

}
