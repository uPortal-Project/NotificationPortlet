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
package org.jasig.portlet.notice.util;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class attempts to read the portlet.xml file to determine what groups should be available to
 * evaluate against. Each group is a <security-role-ref> element in the portlet.xml
 *
 * @since 3.1
 */
@Service
public class PortletXmlRoleService implements ServletContextAware {

    private static final String PORTLET_XML_PATH = "/WEB-INF/portlet.xml";

    private Set<String> roles = Collections.emptySet();
    private ServletContext context;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void setServletContext(ServletContext context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        final Document doc = parseXml();

        if (doc != null) {
            String roleNameCandidate;
            Set<String> set = new HashSet<>();

            // Find all the <security-role-ref> elements in the file
            final NodeList roleSections = doc.getElementsByTagName("security-role-ref");
            for (int i = 0; i < roleSections.getLength(); i++) {
                // for each <security-role-ref>, get the child nodes
                if (roleSections.item(i).hasChildNodes()) {
                    final NodeList roleNames = roleSections.item(i).getChildNodes();
                    for (int j = 0; j < roleNames.getLength(); j++) {
                        // go through the child nodes of each <security-role-ref> to find the <role-name> node
                        if (roleNames.item(j).getNodeName().equalsIgnoreCase("role-name")) {
                            // copy the <role-name> to the roles list if it's not there already
                            roleNameCandidate = roleNames.item(j).getTextContent();
                            set.add(roleNameCandidate);
                        }
                    }
                }
            }

            roles = Collections.unmodifiableSet(set);
            logger.info("Successfully instantiated and found roles: {}", roles);
        } else {
            logger.error("Error parsing the file: {}. See other messages for trace.", PORTLET_XML_PATH);
        }
    }

    public Set<String> getAllRoles() {
        return roles;
    }

    private Document parseXml() {

        Document rslt = null;

        try {
            final URL portletXmlUrl = context.getResource(PORTLET_XML_PATH);
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final InputSource xmlInp = new InputSource(portletXmlUrl.openStream());
            final DocumentBuilder dbl = dbf.newDocumentBuilder();
            rslt = dbl.parse(xmlInp);
            logger.debug("Finished parsing ", PORTLET_XML_PATH);
        } catch (Exception e) {
            logger.error("Failed to parse the specified document: {}", PORTLET_XML_PATH, e);
        }

        return rslt;

    }

}
