package com.ibm.watson.app.dialog.rest.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ibm.watson.movieapp.dialog.exception.WatsonTheatersException;
import com.ibm.watson.movieapp.dialog.payload.MoviePayload;
import com.ibm.watson.movieapp.dialog.payload.WDSConversationPayload;
import com.ibm.watson.movieapp.dialog.rest.Messages;
import com.ibm.watson.movieapp.dialog.rest.SearchTheMovieDbProxyResource;
import com.ibm.watson.movieapp.dialog.rest.WDSBlueMixProxyResource;

@RunWith(MockitoJUnitRunner.class)
public class SearchTheMovieDbProxyResourceTest {

    @Before
    public void setUp() {
        RuntimeDelegate.setInstance(runtimeDelegate);
    }

    /**
     * Tests the getMovieDetail method with a mock MoviePayload with invalid movie id.
     */
    @Test
    public void testinvalidgetMovieDetails() throws Exception {

        MoviePayload moviePayload1 = null;
        mock_search_movie_rest_client_is_created(500, moviePayload1);
        when(moviedbresource.getMovieDetails(null, "The Hunger Games:Mockingjay Part 1")).thenReturn(response);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        moviedbresource.getMovieDetails(null, "The Hunger Games:Mockingjay Part 1");
        verify(moviedbresource, times(1)).getMovieDetails(argumentCaptor.capture(), Mockito.eq("The Hunger Games:Mockingjay Part 1"));
        assertEquals(null, argumentCaptor.getAllValues().get(0));
        assertEquals(response.getStatus(), 500);
        assertEquals(response.getEntity(), null);

    }

    /**
     * Tests the getMovieDetail method with a mock MoviePayload.
     */
    @Test
    public void testvalidgetMovieDetails() throws Exception {
        MoviePayload moviePayload = mock(MoviePayload.class);
        mock_search_movie_rest_client_is_created(200, moviePayload);
        when(moviedbresource.getMovieDetails("131631", "The Hunger Games:Mockingjay Part 1")).thenReturn(response);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        moviedbresource.getMovieDetails("131631", "The Hunger Games:Mockingjay Part 1");
        verify(moviedbresource, times(1)).getMovieDetails(Mockito.eq("131631"), argumentCaptor.capture());
        assertEquals("The Hunger Games:Mockingjay Part 1", argumentCaptor.getValue());
        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
    }

    /**
     * Tests the discoverMovies method with a mock WDSConversationPayload. Return WatsonTheatersException if recency is invalid
     */
    @Test(expected = WatsonTheatersException.class)
    public void testdiscoverMovies() throws Exception {
        MoviePayload moviePayload = mock(MoviePayload.class);
        mock_search_movie_rest_client_is_created(200, moviePayload);
        WDSConversationPayload conversationPayload = mock(WDSConversationPayload.class);
        when(moviedbresource.discoverMovies("action", "R", "Upcoming", 1, 1, false)).thenReturn(conversationPayload);
        String errorMessage = Messages.getString("SearchTheMovieDbProxyResource.RECENCY_INFO_NEEDED"); //$NON-NLS-1$
        String issue = Messages.getString("SearchTheMovieDbProxyResource.RECENCY_UNSPECIFIED"); //$NON-NLS-1$
        when(moviedbresource.discoverMovies("action", "R", null, 1, 1, false)).thenThrow(new WatsonTheatersException(errorMessage, issue));
        WDSConversationPayload response_payload = moviedbresource.discoverMovies("action", "R", "Upcoming", 1, 1, false);
        assertEquals(response.getStatus(), 200);
        assertNotNull(response_payload);
        moviedbresource.discoverMovies("action", "R", null, 1, 1, false);
    }

    /**
     * Tests the getResults method based on JSONArray of movie info returned by TMDB and the index in the current page.
     */
    @Test
    public void testgetResults() throws Exception {
        String jArrayString = "[{\"id\":321494,\"title\":\"The Man with the Iron Fists 2\",\"vote_average\":5.3,\"backdrop_path\":\"/nqL7K2iWoMGrDWRqq6Jgn1s56d6.jpg\",\"release_date\":\"2015-04-14\",\"original_title\":\"The Man with the Iron Fists 2\",\"vote_count\":3,\"action\":false,\"poster_path\":\"/njPa0RO0KKmwBCGHcEOTbxljftF.jpg\",\"video\":false,\"popularity\":5.04235888862094},{\"id\":331963,\"title\":\"Hooligans at War: North vs South\",\"vote_average\":10.0,\"backdrop_path\":\"/m8A1yhr9sqbItYhxLZk6lnUhoQF.jpg\",\"release_date\":\"2015-04-13\",\"original_title\":\"Hooligans at War: North vs South\",\"vote_count\":1,\"action\":false,\"poster_path\":\"/sOoTQhKty7SE3sF8yU0BfEnACVG.jpg\",\"video\":false,\"popularity\":2.2753683900707}]";
        JsonElement je = new JsonParser().parse(jArrayString);
        JsonArray jArray = je.getAsJsonArray();
        SearchTheMovieDbProxyResource movieDbProxy = new SearchTheMovieDbProxyResource();
        List<MoviePayload> movies = null;
        movies = movieDbProxy.getResults(jArray, 0);
        assertEquals(movies.get(0).getMovieName(), "The Man with the Iron Fists 2");
        assertNotNull(movies);
    }

    /**
     * Tests the addDateFilters method based on Recency parameter which can be either "Upcoming" or "Current".
     */
    @Test
    public void testaddDateFilters() throws Exception {
        SearchTheMovieDbProxyResource movieDbProxy = new SearchTheMovieDbProxyResource();
        Hashtable<String, String> uriParamsHash = new Hashtable<String, String>();
        uriParamsHash = movieDbProxy.addDateFilters("upcoming", uriParamsHash);
        assertNotNull(uriParamsHash);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        assertEquals(dateFormat.format(date), uriParamsHash.get("primary_release_date.gte"));
        uriParamsHash = movieDbProxy.addDateFilters("current", uriParamsHash);
        assertNotNull(uriParamsHash);
        assertEquals(dateFormat.format(date), uriParamsHash.get("primary_release_date.lte"));
    }

    /**
     * Tests the getGenreId method based on genre specified by the user.
     */
    @Test
    public void testgetGenreId() throws Exception {
        MoviePayload moviePayload = mock(MoviePayload.class);
        mock_search_movie_rest_client_is_created(200, moviePayload);
        when(moviedbresource.getGenreId("invalid genre")).thenReturn(new Integer(0));
        when(moviedbresource.getGenreId("action")).thenReturn(new Integer(28));
        int gen_id = 0;
        gen_id = moviedbresource.getGenreId("action");
        assertNotNull(gen_id);
        assertEquals(gen_id, 28);
        gen_id = moviedbresource.getGenreId("invalid genre");
        assertNotNull(gen_id);
        assertEquals(gen_id, 0);
    }

    /**
     * Creates a mock SearchTheMovieDbProxyResource object with mocked status code and entity wrapped in a mock response.
     */
    protected SearchTheMovieDbProxyResource mock_search_movie_rest_client_is_created(int status_code, Object entity) throws Exception {
        moviedbresource = mock(SearchTheMovieDbProxyResource.class);
        ResponseBuilder responseBuilder = mock(ResponseBuilder.class);
        response = mock(Response.class);
        when(response.getStatus()).thenReturn(status_code);
        when(response.getEntity()).thenReturn(entity);
        when((responseBuilder).build()).thenReturn(response);
        return moviedbresource;
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
    SearchTheMovieDbProxyResource moviedbresource;
    Response response;
    @Mock
    private RuntimeDelegate runtimeDelegate;

}
