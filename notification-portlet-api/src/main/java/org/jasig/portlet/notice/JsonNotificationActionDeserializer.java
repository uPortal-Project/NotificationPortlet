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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonNotificationActionDeserializer extends JsonDeserializer<NotificationAction> {

    private static final String CLAZZ_FILDNAME = "clazz";
    private static final String LABEL_FILDNAME = "label";

    @Override
    public NotificationAction deserialize(final JsonParser parser, final DeserializationContext ctx) 
            throws IOException {

        // Things we need to capture
        String clazz = null;
        String label = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldname = parser.getCurrentName();
            if (CLAZZ_FILDNAME.equals(fieldname)) {
                parser.nextToken();  // Advance to the next token;  it will be the clazz
                clazz = parser.getText();
            } else if (LABEL_FILDNAME.equals(fieldname)) {
                parser.nextToken();  // Advance to the next token;  it will be the label
                label = parser.getText();
            }
        }

        if (clazz == null) {
            // We can't proceed
            final String msg = "Unable to deserialize NotificationAction due to missing clazz field";
            throw new IllegalArgumentException(msg);
        }

        NotificationAction rslt;
        try {
            rslt = (NotificationAction) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate a NotificationAction of concrete class:  " + clazz);
        }

        if (label != null) {
            rslt.setLabel(label);
        }

        return rslt;

    }

}
