package org.jasig.portlet.notice.service.jpa;

/**
 * Handle custom dozer post processing.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
*/
public interface IMappedClassPostProcessor<DEST, SRC> {
    void process(DEST mappedObject, SRC srcObject);
}
