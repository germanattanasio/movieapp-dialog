package com.ibm.watson.app.dialog.rest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.ibm.watson.movieapp.dialog.payload.WDSConversationPayload;
import com.ibm.watson.movieapp.dialog.rest.WDSBlueMixProxyResource;

@RunWith(MockitoJUnitRunner.class)
public class WDSBlueMixProxyResourceTest {

    @Before
    public void setUp() {
        RuntimeDelegate.setInstance(runtimeDelegate);
    }

    /**
     * Tests the matchSearchNowPattern method by checking for the JsonObject containing the response from WDS as well as the parameters and their values sent by
     * WDS.
     */

    @Test
    public void testmatchSearchNowPattern() throws Exception {
        WDSBlueMixProxyResource bluemixproxy = new WDSBlueMixProxyResource();
        // {"input":"update 1st_time","wdsResponse":"Would you like to see a movie tonight or at another time?","conversationId":"14074","clientId":"14074"}
        String wdsResponse = "Would you like to see a movie tonight or at another time?";
        JsonObject processedText = bluemixproxy.matchSearchNowPattern(wdsResponse);
        assertNotNull(processedText);
        assertEquals(processedText.get("WDSMessage").getAsString(), "Would you like to see a movie tonight or at another time?");
    }

    /**
     * Tests the postConversation method with mock WDSConversationPayload and input parameters.
     */
    @Test
    public void testpostConversation() throws Exception {
        WDSConversationPayload conversationPayload = mock(WDSConversationPayload.class);
        mock_bluemix_rest_client_is_created(200, conversationPayload);
        when(wdsresource.postConversation("100", "17567", "Whats playing")).thenReturn(response);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        wdsresource.postConversation("100", "17567", "Whats playing");
        verify(wdsresource, times(1)).postConversation(Mockito.eq("100"), argumentCaptor.capture(), Mockito.eq("Whats playing"));
        assertEquals("17567", argumentCaptor.getValue());
        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
    }

    /**
     * Tests the getSelectedMovieDetails method with mock WDSConversationPayload and input parameters.
     */
    @Test
    public void testgetSelectedMovieDetails() throws Exception {
        WDSConversationPayload conversationPayload = mock(WDSConversationPayload.class);
        mock_bluemix_rest_client_is_created(200, conversationPayload);
        when(wdsresource.getSelectedMovieDetails("100", "17567", "The Hunger Games:Mockingjay Part 1", "131631")).thenReturn(response);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        wdsresource.getSelectedMovieDetails("100", "17567", "The Hunger Games:Mockingjay Part 1", "131631");
        verify(wdsresource, times(1)).getSelectedMovieDetails(Mockito.eq("100"), argumentCaptor.capture(), Mockito.eq("The Hunger Games:Mockingjay Part 1"),
                Mockito.eq("131631"));
        assertEquals("17567", argumentCaptor.getValue());
        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
    }

    /**
     * Test Initialization of chat with WDS with mock WDSConversationPayload
     */
    @Test
    public void teststartConversation() throws Exception {
        WDSConversationPayload conversationPayload = mock(WDSConversationPayload.class);
        mock_bluemix_rest_client_is_created(200, conversationPayload);
        when(wdsresource.startConversation(true)).thenReturn(response);
        wdsresource.startConversation(true);
        verify(wdsresource, times(1)).startConversation(true);
        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
    }

    /**
     * Creates a mock WDSBlueMixProxyResource object with mocked status code and entity wrapped in a mock response.
     */
    protected WDSBlueMixProxyResource mock_bluemix_rest_client_is_created(int status_code, Object entity) throws Exception {
        wdsresource = mock(WDSBlueMixProxyResource.class);
        ResponseBuilder responseBuilder = mock(ResponseBuilder.class);
        response = mock(Response.class);
        when(response.getStatus()).thenReturn(status_code);
        when(response.getEntity()).thenReturn(entity);
        when((responseBuilder).build()).thenReturn(response);
        return wdsresource;
    }

    @Mock
    private EntityManager em;

    @Mock
    private UriInfo uriInfo;
    WDSBlueMixProxyResource wdsresource;
    Response response;
    @Mock
    private RuntimeDelegate runtimeDelegate;

}
