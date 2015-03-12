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
