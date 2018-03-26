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
package org.jasig.portlet.notice.service.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.DozerEventListener;
import org.dozer.Mapper;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class MappingConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean("addresseePostProcessor")
    public AddresseePostProcessor getAddresseePostProcessor() {
        return new AddresseePostProcessor();
    }

    @Bean
    public DozerBeanMapperFactoryBean getDozerBeanMapperFactory() {
        final Resource mappingFile = applicationContext.getResource("classpath:/mapping/jpa-mappings.xml");

        final JpaEntryPostProcessor jpaEntryPostProcessor = new JpaEntryPostProcessor();
        jpaEntryPostProcessor.setAddresseePostProcessor(getAddresseePostProcessor());

        final Map<Class<?>,IMappedClassPostProcessor<?,?>> postProcessorMap = new HashMap<>();
        postProcessorMap.put(JpaEntry.class, jpaEntryPostProcessor);
        postProcessorMap.put(JpaAddressee.class, getAddresseePostProcessor());

        final NotificationDTOMapperEventListener listener = new NotificationDTOMapperEventListener();
        listener.setPostProcessorMap(postProcessorMap);

        final List<DozerEventListener> eventListeners = new ArrayList<>();
        eventListeners.add(listener);

        final DozerBeanMapperFactoryBean rslt = new DozerBeanMapperFactoryBean();
        rslt.setMappingFiles(new Resource[] { mappingFile });
        rslt.setEventListeners(eventListeners);

        return rslt;
    }

    @Bean("dozerBeanMapper")
    public Mapper getDozerBeanMapper() {
        try {
            return getDozerBeanMapperFactory().getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
