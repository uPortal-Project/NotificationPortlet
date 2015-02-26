package org.jasig.portlet.notice.service.studentsuccessplan;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 */
public interface ISSPApi {
    public <T> ResponseEntity<T> doRequest(SSPApiRequest<T> request) throws MalformedURLException, RestClientException;
}
