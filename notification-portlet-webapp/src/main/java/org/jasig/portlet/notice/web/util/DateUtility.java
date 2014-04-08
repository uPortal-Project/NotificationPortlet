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

package org.jasig.portlet.notice.web.util;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

/**
 * Utility class to provide date functions to JSP EL
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class DateUtility {
    public static String todayMinusDays(Integer days, String jodaFormatPattern) {
        LocalDate date = new LocalDate();
        return DateTimeFormat.forPattern(jodaFormatPattern).print(date.minusDays(days));
    }

    public static String todayMinusMonths(Integer months, String jodaFormatPattern) {
        LocalDate date = new LocalDate();
        return DateTimeFormat.forPattern(jodaFormatPattern).print(date.minusMonths(months));
    }
}
