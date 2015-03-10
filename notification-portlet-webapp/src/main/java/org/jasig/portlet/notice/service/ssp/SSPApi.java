package org.jasig.portlet.notice.service.ssp;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Helper service to simplify interacting with the SSP REST Api.
 * Class is will take care of authentication and holds information
 * about the SSP host and context.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class SSPApi implements ISSPApi {
    private static final String AUTHORIZATION = "authorization";
    private static final String BASIC = "Basic";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private RestTemplate restTemplate;
    private String clientId;
    private String clientSecret;
    private String authenticationUrl = "/api/1/oauth2/token";
    private String sspProtocol = "https";
    private String sspHost = "locahost";
    private int sspPort = 443;
    private String sspContext = "/ssp";

    /**
     * Track the portals authentication token.  Note that this should NOT be
     * accessed directly outside of getAuthenticationToken
     */
    private SSPToken authenticationToken;


    @Autowired
    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Value("${studentSuccessPlanService.clientId:}")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    @Value("${studentSuccessPlanService.clientSecret:}")
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }


    @Value("${studentSuccessPlanService.authenticationUrl:/api/1/oauth2/token}")
    public void setAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }


    @Value("${studentSuccessPlanService.sspProtocol:https}")
    public void setSspProtocol(String sspProtocol) {
        Validate.isTrue(sspProtocol.equalsIgnoreCase("http") || sspProtocol.equalsIgnoreCase("https"),
                "sspProtocol must be set to either 'http' or 'https'"
        );

        this.sspProtocol = sspProtocol;
    }


    @Value("${studentSuccessPlanService.sspHost:}")
    public void setSspHost(String sspHost) {
        this.sspHost = sspHost;
    }


    @Value("${studentSuccessPlanService.sspPort:443}")
    public void setSspPort(int sspPort) {
        this.sspPort = sspPort;
    }


    /**
     * Set the SSP API Context.   Note:  this method will ensure that that the API context
     * starts with '/' and ensures that it does not end with a '/'.
     *
     * @param sspContext the API context
     */
    @Value("${studentSuccessPlanService.sspContext:/ssp}")
    public void setSspContext(String sspContext) {
        // ensure leading '/'
        if (!sspContext.startsWith("/")) {
            sspContext = "/" + sspContext;
        }

        // remove any trailing '/'
        if (sspContext.endsWith("/")) {
            sspContext = sspContext.replaceAll("/*$", "");
        }

        this.sspContext = sspContext;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> ResponseEntity<T> doRequest(SSPApiRequest<T> request) throws MalformedURLException, RestClientException {
        SSPToken token = getAuthenticationToken(false);

        request.setHeader(AUTHORIZATION, token.getTokenType() + " " + token.getAccessToken());

        URL url = getSSPUrl(request.getUrlFragment(), true);
        ResponseEntity<T> response = restTemplate.exchange(url.toExternalForm(),
                request.getMethod(),
                request.getRequestEntity(),
                request.getResponseClass(),
                request.getUriParameters());
        // if we get a 401, the token may have unexpectedly expired (eg. ssp server restart).
        // Clear it, get a new token and replay the request one time.
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            token = getAuthenticationToken(true);
            request.setHeader(AUTHORIZATION, token.getTokenType() + " " + token.getAccessToken());
            return restTemplate.exchange(url.toExternalForm(),
                    request.getMethod(),
                    request.getRequestEntity(),
                    request.getResponseClass(),
                    request.getUriParameters());
        }

        return response;
    }


    /**
     * {@inheritDoc}
     */
    public URL getSSPUrl(String urlFragment, boolean useContext) throws MalformedURLException {
        String path = (useContext) ? sspContext + urlFragment : urlFragment;
        return new URL(sspProtocol, sspHost, sspPort, path);
    }


    /**
     * Get the authentication token to use.
     *
     * @param forceUpdate if true, get a new auth token even if a cached instance exists.
     * @return The authentication token
     * @throws MalformedURLException if the authentication URL is invalid
     * @throws RestClientException if an error occurs when talking to SSP
     */
    private synchronized SSPToken getAuthenticationToken(boolean forceUpdate) throws MalformedURLException, RestClientException {
        if (authenticationToken != null && !authenticationToken.hasExpired() && !forceUpdate) {
            return authenticationToken;
        }

        String authString = getClientId() + ":" + getClientSecret();
        String authentication = new Base64().encodeToString(authString.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BASIC + " " + authentication);

        // form encode the grant_type...
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(GRANT_TYPE, CLIENT_CREDENTIALS);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        URL authURL = getAuthenticationURL();
        authenticationToken = restTemplate.postForObject(authURL.toExternalForm(), request, SSPToken.class);
        return authenticationToken;
    }


    private String getClientId() {
        return clientId;
    }


    private String getClientSecret() {
        return clientSecret;
    }


    private URL getAuthenticationURL() throws MalformedURLException {
        return getSSPUrl(authenticationUrl, true);
    }
}
