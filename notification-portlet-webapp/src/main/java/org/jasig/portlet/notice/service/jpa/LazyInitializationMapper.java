/**
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

import org.dozer.CustomFieldMapper;
import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;
import org.hibernate.Hibernate;

/**
 * Dozer mapping interceptor that skips detects if the field being
 * mapped is a Hibernate proxy and, if so, will skip mapping it.
 * This is intended to avoid creating huge unintended object graphs,
 * and to avoid doing a ton of unnecessary queries.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
public class LazyInitializationMapper implements CustomFieldMapper {
    @Override
    public boolean mapField(Object source, Object destination, Object sourceFieldValue, ClassMap classMap, FieldMap fieldMapping) {
        // don't map uninitialized hibernate proxies -- avoid
        // lazy instantiation exceptions and huge and unnecessary
        // object graphs being serialized and returned.
        return !Hibernate.isInitialized(sourceFieldValue);
    }
}
