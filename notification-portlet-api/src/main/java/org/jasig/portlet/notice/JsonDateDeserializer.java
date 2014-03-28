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
import java.util.Date;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

/**
 * Apparently there is no standard format for dates in JSON.  JsonLib (which 
 * this portlet previously used) essentially serialized an instance of 
 * <code>java.util.Date</code> like any other object.  Jackson, however, (the
 * current method) converts the date to "epoch time" (a long representing 
 * milliseconds since January 1, 1970, 00:00:00 GMT).  The Jackson approach 
 * isn't that useful, since it becomes hard to detect what is a date.  This 
 * class is responsible for telling Jackson to use the former method.
 * 
 * @author awills
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {
    
    private static final String TIME_FILDNAME = "time";

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext ctx) throws JsonParseException, IOException {

        String time = null;
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldname = parser.getCurrentName();
            if (TIME_FILDNAME.equals(fieldname)) {
                parser.nextToken();  // Advance to the next token;  it will be the time
                time = parser.getText();
            }
        }
        
        if (time == null) {
            // This is a problem;  invalid input
            String msg = "Invalid date input at location:  " + parser.getCurrentLocation();
            throw new IllegalArgumentException(msg);
        }
        
        final long rslt = Long.parseLong(time);
        return new Date(rslt);
        
    }

}
