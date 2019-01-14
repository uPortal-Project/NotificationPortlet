package org.jasig.portlet.notice.service.rest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

import javax.annotation.PostConstruct;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Obtains notifications as JSON from a remote URL using a Spring <code>OAuth2RestTemplate</code>.
 * Most of the work is done by the superclass ({@link RestfulJsonNotificationService});  this class
 * merely leverages a different template and a different property/preference.
 *
 * @since 4.6
 */
public class OAuth2RestfulJsonNotificationService extends RestfulJsonNotificationService {

    /**
     * URLs for this service must be defined separately (from the superclass) because we don't have
     * a real way to infer which ones use which template.
     */
    private static final String SERVICE_URLS_PREFERENCE = "OAuth2RestfulJsonNotificationService.serviceUrls";

    /**
     * Service URLs defined in an external file, in the post-Portlet API style.  Comma-separated
     * list.  As above, URLs for this service must be defined separately from
     * {@link RestfulJsonNotificationService}.
     */
    @Value("${" + SERVICE_URLS_PREFERENCE + ":}")
    private String serviceUrlsProperty;

    private List<String> serviceUrlsList = Collections.emptyList();

    @Value("${OAuth2RestfulJsonNotificationService.clientId:}")
    private String clientId;

    @Value("${OAuth2RestfulJsonNotificationService.clientSecret:}")
    private String clientSecret;

    @Value("${OAuth2RestfulJsonNotificationService.accessTokenUri:}")
    private String accessTokenUri;

    private OAuth2RestTemplate oAuth2RestTemplate;

    @PostConstruct
    public void init() {
        if (!StringUtils.isEmpty(serviceUrlsProperty)) {
            serviceUrlsList =
                    Collections.unmodifiableList(Arrays.asList(serviceUrlsProperty.split(",")));
        }

        final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);
        resourceDetails.setAccessTokenUri(accessTokenUri);
        oAuth2RestTemplate = new OAuth2RestTemplate(resourceDetails);
    }

    @Override
    protected RestTemplate getRestTemplate() {
        return oAuth2RestTemplate;
    }

    @Override
    @Deprecated
    protected List<String> getServiceUrls(PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        final String[] urls = prefs.getValues(SERVICE_URLS_PREFERENCE, new String[0]);
        return new ArrayList<>(Arrays.asList(urls));
    }

    @Override
    protected List<String> getServiceUrls() {
        return serviceUrlsList;
    }

}
