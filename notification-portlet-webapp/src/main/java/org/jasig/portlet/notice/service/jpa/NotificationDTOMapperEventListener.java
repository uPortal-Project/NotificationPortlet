package org.jasig.portlet.notice.service.jpa;

import org.dozer.DozerEventListener;
import org.dozer.event.DozerEvent;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

/**
 * Provide hooks for additional post-processing that can be handled during dozer
 * mapping.  Currently only supports adding class specific processing that can
 * be applied after dozer is done.  NOTE:  Dozer events only fire on the root object, not
 * on child objects.  If you have a hierarchy of objects, the mapping class will
 * need to be wired with the logic to handle the child object post processing
 * (unfortunately).
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
public class NotificationDTOMapperEventListener implements DozerEventListener {
    private Map<Class<?>, IMappedClassPostProcessor<?, ?>> postProcessorMap;


    @Required
    public void setPostProcessorMap(final Map<Class<?>, IMappedClassPostProcessor<?, ?>> map) {
        this.postProcessorMap = map;
    }


    @Override
    public void mappingStarted(DozerEvent event) {
    }


    @Override
    public void preWritingDestinationValue(DozerEvent event) {
    }


    @Override
    public void postWritingDestinationValue(DozerEvent event) {
    }


    @Override
    public void mappingFinished(DozerEvent event) {
        Class cls = event.getDestinationObject().getClass();
        if (postProcessorMap.containsKey(cls)) {
            IMappedClassPostProcessor processor = postProcessorMap.get(cls);
            processor.process(event.getDestinationObject(), event.getSourceObject());
        }
    }
}