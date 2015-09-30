package org.jasig.portlet.notice.service.rest;

import org.jasig.portlet.notice.NotificationResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by rgood on 28/09/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/test-rest-json-context.xml"})
public class RestfulJsonNotificationServiceTest {

    private MockRestServiceServer mockServer;

    private MockRestServiceServer mockOAuthServer;

    private static final String notificationResponse="{\n" +
            "  \"errors\": [\n" +
            "    {\n" +
            "      \"error\": \"Error example - Service Not Available\",\n" +
            "      \"source\": \"Demo 2 Service\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"categories\": [\n" +
            "    {\n" +
            "      \"entries\": [\n" +
            "        {\n" +
            "          \"body\": \"The room you requested is now available.\",\n" +
            "          \"dueDate\": {\n" +
            "            \"date\": 9,\n" +
            "            \"day\": 2,\n" +
            "            \"hours\": 11,\n" +
            "            \"minutes\": 57,\n" +
            "            \"month\": 7,\n" +
            "            \"seconds\": 28,\n" +
            "            \"time\": 1312916248695,\n" +
            "            \"timezoneOffset\": 420,\n" +
            "            \"year\": 111\n" +
            "          },\n" +
            "          \"image\": \"http:\\/\\/library.university\\/studentlife.png\",\n" +
            "          \"url\": \"http:\\/\\/library.university\\/studentlife\",\n" +
            "          \"priority\": 3,\n" +
            "          \"id\": \"foobar\",\n" +
            "          \"title\": \"Room Available\",\n" +
            "          \"source\": \"Demo Service\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"title\": \"Student Life\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Autowired
    RestfulJsonNotificationService restfulJsonNotificationService;

    private ClientCredentialsResourceDetails emptyClientCredentialsResourceDetails;
    private OAuth2RestTemplate emptyOAuth2RestTemplate;

    @Mock
    PortletRequest portletRequest;

    @Mock
    PortletPreferences portletPreferences;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        mockServer = MockRestServiceServer.createServer(restfulJsonNotificationService.getRestTemplate());
        Map<String,String> userInfo = new HashMap<String,String>();
        String[] urlArray= {"http://www.notifications.com/demoNotificationResponse2.json"};
        userInfo.put("user.login.id","dummyuser");
        when(portletRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfo);
        when(portletRequest.getPreferences()).thenReturn(portletPreferences);
        when(portletPreferences.getValues(RestfulJsonNotificationService.SERVICE_URLS_PREFERENCE,new String[0])).thenReturn(urlArray);
        emptyClientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
        emptyOAuth2RestTemplate = new OAuth2RestTemplate(emptyClientCredentialsResourceDetails);


    }

    @Test
    public void testOAuth()
    {
        //mockServer = MockRestServiceServer.createServer(restfulJsonNotificationService.getoAuth2RestTemplate());
        mockServer.expect(requestTo("http://www.notifications.com/demoNotificationResponse2.json"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(notificationResponse, MediaType.APPLICATION_JSON));

        final NotificationResponse fetch = restfulJsonNotificationService.fetch(portletRequest);
        assertEquals("Service Unavailable", fetch.getErrors().get(0).getError());
    }

    @Test
    public void testBasicAuth()
    {
        restfulJsonNotificationService.setoAuth2RestTemplate(emptyOAuth2RestTemplate);
        mockServer.expect(requestTo("http://www.notifications.com/demoNotificationResponse2.json"))
                .andExpect(header("Authorization", "Basic dGVzdDp0ZXN0")).andRespond(withSuccess(notificationResponse, MediaType.APPLICATION_JSON));
        StringLiteralParameterEvaluator usernameParameterEvaluator = new StringLiteralParameterEvaluator();
        StringLiteralParameterEvaluator passwordParameterEvaluator = new StringLiteralParameterEvaluator();
        usernameParameterEvaluator.setValue("test");
        passwordParameterEvaluator.setValue("test");
        restfulJsonNotificationService.setUsernameEvaluator(usernameParameterEvaluator);
        restfulJsonNotificationService.setPasswordEvaluator(passwordParameterEvaluator);
        final NotificationResponse fetch = restfulJsonNotificationService.fetch(portletRequest);
        assertEquals(1, fetch.getCategories().size());
        assertEquals("Room Available",fetch.getCategories().get(0).getEntries().get(0).getTitle());

    }

    @Test
    public void testNoAuth()
    {
        restfulJsonNotificationService.setoAuth2RestTemplate(emptyOAuth2RestTemplate);
        mockServer.expect(requestTo("http://www.notifications.com/demoNotificationResponse2.json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(notificationResponse, MediaType.APPLICATION_JSON));
        final NotificationResponse fetch = restfulJsonNotificationService.fetch(portletRequest);
        assertEquals(1, fetch.getCategories().size());
        assertEquals("Room Available",fetch.getCategories().get(0).getEntries().get(0).getTitle());
    }


}
