/* Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.movieapp.dialog.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.watson.movieapp.dialog.exception.WatsonTheatersException;
import com.ibm.watson.movieapp.dialog.payload.MoviePayload;
import com.ibm.watson.movieapp.dialog.payload.ServerErrorPayload;
import com.ibm.watson.movieapp.dialog.payload.WDSConversationPayload;

/**
 * <p>
 * Proxy class to communicate with themoviedb.org. This class acts as a proxy on the server-side to communicate with themoviedb.org via RESTful API calls and
 * relays back movie information to the client-side.
 * </p>
 * <p>
 * There are two entry points into this class via JAX-RS calls/internal calls made by {@code WDSBlueMixProxyResource} to gather movie data. eg.:
 * /watson-movieapp-dialog/api/movies/getMovieDetails is one such entry point.
 * </p>
 * <p>
 * This class also implements helper functions for discovering movies and gathering movie details.
 * </p>
 * 
 * @author Ashima Arora
 */

@Path("/movies")
public class SearchTheMovieDbProxyResource {
    private static final String TMDB_BASE_URL = "api.themoviedb.org/3"; //$NON-NLS-1$
    private static final String DISCOVER = "/discover/movie"; //$NON-NLS-1$
    private static final String MOVIE_DETAILS = "/movie/"; //$NON-NLS-1$
    private static final String CONFIGURATION = "/configuration"; //$NON-NLS-1$
    private static final String LIST_GENRES = "/genre/movie/list"; //$NON-NLS-1$
    private static final String IMAGE_CACHE_KEY = "imageUrl"; //$NON-NLS-1$
    private static final String GENRE_CACHE_PREFIX = "genre."; //$NON-NLS-1$
    private static String themoviedbapikey = null;
    public static SimpleDateFormat movieDateFormatter = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
    private static LoadingCache<String, String> theMovieDbCache = loadTheMovieDbCache();

    static {
        themoviedbapikey = System.getenv("TMDB_API_KEY"); //$NON-NLS-1$
        if (themoviedbapikey != null) {
            UtilityFunctions.logger.info(Messages.getString("SearchTheMovieDbProxyResource.TMDB_API_KEY_LOADED")); //$NON-NLS-1$
        } else {
            UtilityFunctions.logger.error(Messages.getString("SearchTheMovieDbProxyResource.TMDB_API_KEY_LOAD_FAIL")); //$NON-NLS-1$
        }
    }

    /**
     * Loads an internal cache which gets refreshed periodically. We are depending on a third party system (themoviedb.org) so it is possible that their genres
     * may change over time or the url used to retrieve posters will change. As a result we periodically (for now daily) check to make sure we have the correct
     * values for these.
     * 
     * @return a cache which looks up certain values in themoviedb.org
     */
    private static LoadingCache<String, String> loadTheMovieDbCache() {
        return CacheBuilder.newBuilder().initialCapacity(8).expireAfterWrite(1, TimeUnit.DAYS) // refresh once a day
                .maximumSize(20).concurrencyLevel(5).build(new CacheLoader<String, String>() {
                    private HashMap<String, String> genresAndIds = new HashMap<>();

                    @Override
                    public String load(String key) throws Exception {
                        if (IMAGE_CACHE_KEY.equals(key)) {
                            // Get the poster path.
                            String imageBaseURL = null;
                            URI uri = buildUriStringFromParamsHash(new Hashtable<String, String>(), CONFIGURATION);
                            JsonObject tmdbResponse = UtilityFunctions.httpGet(createTMDBHttpClient(), uri);
                            if (tmdbResponse.has("images")) { //$NON-NLS-1$
                                JsonObject images = tmdbResponse.get("images").getAsJsonObject(); //$NON-NLS-1$
                                imageBaseURL = UtilityFunctions.getPropValue(images, "base_url"); //$NON-NLS-1$
                                if (images.has("backdrop_sizes")) { //$NON-NLS-1$
                                    JsonArray sizes = images.get("backdrop_sizes").getAsJsonArray(); //$NON-NLS-1$
                                    String size = sizes.get(0).getAsString();
                                    if (size != null) {
                                        imageBaseURL += size;
                                    }
                                }
                                return imageBaseURL;
                            }
                        }
                        if (key != null && key.startsWith(GENRE_CACHE_PREFIX)) {
                            if (genresAndIds.isEmpty()) {
                                URI uri = buildUriStringFromParamsHash(null, LIST_GENRES);
                                JsonObject tmdbResponse = UtilityFunctions.httpGet(createTMDBHttpClient(), uri);
                                if (tmdbResponse.has("genres")) { //$NON-NLS-1$
                                    JsonArray genres = tmdbResponse.getAsJsonArray("genres"); //$NON-NLS-1$
                                    for (JsonElement element : genres) {
                                        JsonObject genre = element.getAsJsonObject();
                                        genresAndIds.put(genre.get("name").getAsString().toLowerCase(), genre.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
                                    }
                                }
                            }
                            String ret = genresAndIds.get(key.substring(GENRE_CACHE_PREFIX.length()).toLowerCase());
                            return ret != null ? ret : ""; //$NON-NLS-1$
                        }
                        return null;
                    }
                });
    }

    /**
     * Creates HTTP Client
     * 
     * @return a new HttpClient for each new HTTP call to TMDB
     */
    private static CloseableHttpClient createTMDBHttpClient() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return httpClient;
    }

    /**
     * Adds the date filter in the URI parameter hash
     * <p>
     * This will add a "date greater than" and a "date less than " filter for the movie release date by adding parameters to the URI parameter hashtable.
     * <ol>
     * <li>Current movies: Released in the past one week.
     * <li>Upcoming movies: Released in the next six months.
     * </ol>
     * 
     * @param recency the recency preference selected by the user (eg.: "current" or "upcoming"), not null
     * @param uriParamsHash the hashtable containing parameters to be added to the URI for making the call to TMDB Hashtable elements are of the form
     *            {uri_parameter_name, uri_parameter_value}
     * @return the updated hashtable after adding the date filter
     */
    public Hashtable<String, String> addDateFilters(String recency, Hashtable<String, String> uriParamsHash) {
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        if (recency.equalsIgnoreCase("current")) { //$NON-NLS-1$
            // Current movies
            cal.add(Calendar.DAY_OF_MONTH, -28);
            Date monthAgo = cal.getTime();
            uriParamsHash.put("primary_release_date.gte", movieDateFormatter.format(monthAgo)); //$NON-NLS-1$
            uriParamsHash.put("primary_release_date.lte", movieDateFormatter.format(today)); //$NON-NLS-1$
        } else if (recency.equalsIgnoreCase("upcoming")) { //$NON-NLS-1$
            // Upcoming movies
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, +6);
            Date sixMonthsLater = cal.getTime();
            uriParamsHash.put("primary_release_date.gte", movieDateFormatter.format(today)); //$NON-NLS-1$
            uriParamsHash.put("primary_release_date.lte", movieDateFormatter.format(sixMonthsLater)); //$NON-NLS-1$
        }
        return uriParamsHash;
    }

    /**
     * Builds the URI from the URI parameter hash
     * <p>
     * This will append each of the {key, value} pairs in uriParamsHash to the URI.
     * 
     * @param uriParamsHash the hashtable containing parameters to be added to the URI for making the call to TMDB
     * @return URI for making the HTTP call to TMDB API
     * @throws URISyntaxException if the uri being built is in the incorrect format
     */
    private static URI buildUriStringFromParamsHash(Hashtable<String, String> uriParamsHash, String path) throws URISyntaxException {
        URIBuilder urib = new URIBuilder();
        urib.setScheme("http"); //$NON-NLS-1$
        urib.setHost(TMDB_BASE_URL);
        urib.setPath(path);
        urib.addParameter("api_key", themoviedbapikey); //$NON-NLS-1$
        if (uriParamsHash != null) {
            Set<String> keys = uriParamsHash.keySet();
            for (String key : keys) {
                urib.addParameter(key, uriParamsHash.get(key));
            }
        }
        return urib.build();
    }

    /**
     * Discovers movies based on the preferences specified
     * <p>
     * This will make a HTTP GET request to TMDB server to find movies based on the parameters specified.
     * 
     * @param genre the genre specified by the user
     * @param rating the rating specified by the user
     * @param recency the recency preference specified by the user ("upcoming" or "current")
     * @param currentIndex the index representing the number of results already shown to the end user
     * @param pageNum the page number from the set of result pages returned by TMDB for the search query
     * 
     *            See <a href="http://docs.themoviedb.apiary.io/#reference/discover/discovermovie">here</a> for more details
     * @return the {@code WDSConversationPayload} object containing a list of {@code MoviePayload} objects and a response message from WDS
     * @throws ClientProtocolException if it is unable to execute the call to TMDB API
     * @throws IllegalStateException if the HTTP GET method cannot parse the response stream
     * @throws IOException if it is unable to execute the call to TMDB API
     * @throws HttpException if the HTTP call responded with a status code other than 200 or 201
     * @throws URISyntaxException if the uri being built is in the incorrect format
     * @throws WatsonTheatersException if the recency parameter is "null"
     * @throws ParseException if the movie release date cannot be parsed correctly
     */
    public WDSConversationPayload discoverMovies(String genre, String rating, String recency, int currentIndex, int pageNum, boolean searchForward)
            throws ClientProtocolException, IllegalStateException, IOException, HttpException, URISyntaxException, WatsonTheatersException, ParseException {
        // Initializes url params to be updated.
        String errorMessage = null, issue = null;
        Hashtable<String, String> uriParamsHash = new Hashtable<String, String>();

        // Check if recency is null.
        if (recency.equals("null")) { //$NON-NLS-1$
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.RECENCY_INFO_NEEDED"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.RECENCY_UNSPECIFIED"); //$NON-NLS-1$
            throw new WatsonTheatersException(errorMessage, issue);
        }

        if (genre != null && !genre.isEmpty()) {
            uriParamsHash.put("with_genres", getGenreId(genre).toString()); //$NON-NLS-1$
        }

        if (rating != null && !rating.isEmpty()) {
            uriParamsHash.put("certification_country", "US"); //$NON-NLS-1$ //$NON-NLS-2$
            uriParamsHash.put("certification", rating); //$NON-NLS-1$
        }

        // Add the pageNumber and sort in decreasing order of votes_average.
        uriParamsHash.put("page", String.valueOf(pageNum)); //$NON-NLS-1$
        uriParamsHash.put("sort_by", "popularity.desc"); //$NON-NLS-1$ //$NON-NLS-2$

        // Add date filters to uriHash depending on recency.
        uriParamsHash = addDateFilters(recency, uriParamsHash);

        // Build the URI.
        URI uri = buildUriStringFromParamsHash(uriParamsHash, DISCOVER);

        // Make the REST call.
        JsonObject responseObj = UtilityFunctions.httpGet(createTMDBHttpClient(), uri);
        JsonArray jArray = responseObj.getAsJsonArray("results"); //$NON-NLS-1$

        // If previous search, set the currentIndex in order to extract last set of movies.
        if (!searchForward) {
            if (currentIndex % 20 == 0 || currentIndex % 20 > 10) {
                currentIndex = 0;
            } else {
                currentIndex = 10;
            }
        }

        // Get the next 10 movies from the returned payload from themoviedb
        List<MoviePayload> movies = getResults(jArray, (searchForward ? (currentIndex - ((pageNum - 1) * 20)) : currentIndex));

        // Return payload.
        WDSConversationPayload moviesPayload = new WDSConversationPayload();
        moviesPayload.setMovies(movies);
        moviesPayload.setTotalPages(Integer.parseInt(UtilityFunctions.getPropValue(responseObj, "total_pages"))); //$NON-NLS-1$
        moviesPayload.setNumMovies(Integer.parseInt(UtilityFunctions.getPropValue(responseObj, "total_results"))); //$NON-NLS-1$
        return moviesPayload;
    }

    /**
     * Builds a list of movies from TMDB response
     * <p>
     * This will extract the movie info from TMDB response and generate a list of {@code MoviePayload} objects.
     * 
     * @param jArray the JSONArray of movie info returned by TMDB
     * @param currentIndex the index in the current page
     * @return the list of {@code MoviePayload} objects with movie info
     * @throws ParseException if the movie release date could not be parsed correctly
     */
    public List<MoviePayload> getResults(JsonArray jArray, int currentIndex) throws ParseException {
        List<MoviePayload> movies = new ArrayList<>();
        if (currentIndex >= jArray.size()) {
            return movies;
        }
        // The index will typically be 0 or 10, we add 10 to this as we want to see the next 10 movies
        // Sometimes there may not be 10 more movies available so we take the min on currentIndex + 10
        // or array size.
        for (int i = currentIndex; i < Math.min(jArray.size(), currentIndex + 10); i++) {
            JsonObject j = jArray.get(i).getAsJsonObject();
            MoviePayload m = new MoviePayload();
            m.setMovieId(Integer.parseInt(j.get("id").getAsString())); //$NON-NLS-1$
            m.setMovieName(j.get("title").getAsString()); //$NON-NLS-1$
            m.setPopularity(Double.parseDouble(j.get("vote_average").getAsString())); //$NON-NLS-1$
            m.setReleaseDateStr(j.get("release_date").getAsString()); //$NON-NLS-1$
            String prop = (String) m.getReleaseDateStr();
            m.setReleaseDate(movieDateFormatter.parse(prop));
            movies.add(m);
        }
        return movies;
    }

    /**
     * Gets the genre id from TMDB
     * <p>
     * This gets the id for the genre supplied. This id is used in {@link #discoverMovies(genre, rating, recency, pageNum)}
     * 
     * @param userGenre the genre preference supplied by the user
     * @return the genre id
     * @throws URISyntaxException if the uri being built is in the incorrect format
     * @throws ClientProtocolException if it is unable to execute the call to TMDB /genre API
     * @throws IllegalStateException if the HTTP GET method cannot parse the response stream
     * @throws IOException if it is unable to execute the call to TMDB /genre API
     * @throws HttpException if the HTTP call responded with a status code other than 200 or 201
     * @throws WatsonTheatersException if the genre does not exist in TMDB database
     */
    public Integer getGenreId(String userGenre) throws URISyntaxException, ClientProtocolException, IllegalStateException, IOException, HttpException,
            WatsonTheatersException {
        String errorMessage = Messages.getString("SearchTheMovieDbProxyResource.GENRE_NOT_EXISTING"); //$NON-NLS-1$
        String issue = Messages.getString("SearchTheMovieDbProxyResource.INVALID_GENRE"); //$NON-NLS-1$
        try {
            String genreId = theMovieDbCache.get(GENRE_CACHE_PREFIX + userGenre);
            if (genreId != null) {
                return Integer.parseInt(genreId);
            }
        } catch (ExecutionException e) {
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.ERROR_RETRIEVING_GENRE"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.CACHE_ERROR"); //$NON-NLS-1$
        }
        throw new WatsonTheatersException(errorMessage, issue);
    }

    /**
     * Gets the details for the movie selected
     * <p>
     * This will retrieve the following movie info using themoviedb.org's /moviedetails API:
     * <ul>
     * <li>releaseDate the movie's release date
     * <li>releaseDateStr the movie's release date in String format
     * <li>genre the movie genre
     * <li>genreId the movie genre id
     * <li>movieId the movie id
     * <li>certification the movie certification
     * <li>certificationCountry the country of certification
     * <li>popularity the movie popularity (out of 10)
     * <li>movieName the movie name
     * <li>overview the brief summary of the movie
     * <li>runtime the runtime (in minutes)
     * <li>homepageUrl the url to the movie's homepage
     * <li>posterPath the path to the movie poster
     * <li>trailerUrl the url to the movie's trailer
     * </ul>
     * 
     * @param movieId the movie id of the movie selected, not null
     * @param movieName the name of the movie selected
     * @return a response containing either of these two entities- {@code MoviePayload} or {@code ServerErrorPayload}
     */
    @GET
    @Path("/getMovieDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieDetails(@QueryParam("movieid") String movieId, @QueryParam("moviename") String movieName) {
        String errorMessage = null, issue = null;

        try {
            if (movieId == null) {
                errorMessage = Messages.getString("SearchTheMovieDbProxyResource.MOVIE_NOT_FOUND"); //$NON-NLS-1$
                issue = Messages.getString("SearchTheMovieDbProxyResource.MOVIE_ID_NOT_SPECIFIED"); //$NON-NLS-1$
                throw new WatsonTheatersException(errorMessage, issue);
            }
            // Get general movie info.
            Hashtable<String, String> params = new Hashtable<>();
            params.put("append_to_response", "releases,videos"); //Make a single API call to retrieve all the info we need. //$NON-NLS-1$ //$NON-NLS-2$

            URI uri = buildUriStringFromParamsHash(params, MOVIE_DETAILS + movieId);
            JsonObject tmdbResponse = UtilityFunctions.httpGet(createTMDBHttpClient(), uri);
            MoviePayload moviePayload = new MoviePayload();
            moviePayload.setMovieId(Integer.parseInt(tmdbResponse.get("id").toString())); //$NON-NLS-1$
            moviePayload.setMovieName(UtilityFunctions.getPropValue(tmdbResponse, "title")); //$NON-NLS-1$
            moviePayload.setHomepageUrl(UtilityFunctions.getPropValue(tmdbResponse, "homepage")); //$NON-NLS-1$
            moviePayload.setOverview(UtilityFunctions.getPropValue(tmdbResponse, "overview")); //$NON-NLS-1$
            moviePayload.setPosterPath(UtilityFunctions.getPropValue(tmdbResponse, "poster_path")); //$NON-NLS-1$
            if (Integer.parseInt(UtilityFunctions.getPropValue(tmdbResponse, "vote_count")) >= 10) {
                moviePayload.setPopularity(Double.parseDouble(UtilityFunctions.getPropValue(tmdbResponse, "vote_average"))); //$NON-NLS-1$
            } else {
                moviePayload.setPopularity(-1.0); //$NON-NLS-1$
            }
            moviePayload.setReleaseDateStr(UtilityFunctions.getPropValue(tmdbResponse, "release_date")); //$NON-NLS-1$
            Date rDate = movieDateFormatter.parse(moviePayload.getReleaseDateStr());
            moviePayload.setReleaseDate(rDate);
            String time = UtilityFunctions.getPropValue(tmdbResponse, "runtime"); //$NON-NLS-1$
            if (time != null && !time.isEmpty()) {
                moviePayload.setRuntime(Integer.parseInt(time));
            }
            String path = UtilityFunctions.getPropValue(tmdbResponse, "poster_path"); //$NON-NLS-1$
            if (path != null) {
                try {
                    String imageBaseURL = theMovieDbCache.get("imageUrl"); //$NON-NLS-1$
                    moviePayload.setPosterPath(imageBaseURL + path);
                } catch (ExecutionException e) {
                    UtilityFunctions.logger.error(Messages.getString("SearchTheMovieDbProxyResource.CACHE_FAIL_IMAGE_URL"), e); //$NON-NLS-1$
                }
            }

            // Get the link for the trailer here and add it to payload.
            JsonObject videos = tmdbResponse.getAsJsonObject("videos"); //$NON-NLS-1$
            if (videos != null) {
                JsonArray jArray = videos.getAsJsonArray("results"); //$NON-NLS-1$
                for (int i = 0; i < jArray.size(); i++) {
                    JsonObject obj = jArray.get(i).getAsJsonObject();
                    String site = UtilityFunctions.getPropValue(obj, "site"); //$NON-NLS-1$
                    String type = UtilityFunctions.getPropValue(obj, "type"); //$NON-NLS-1$
                    if ("Trailer".equalsIgnoreCase(type)) { //$NON-NLS-1$
                        String key = UtilityFunctions.getPropValue(obj, "key"); //$NON-NLS-1$
                        if (key != null && "youtube".equalsIgnoreCase(site)) { //$NON-NLS-1$
                            // create youtube url
                            String trailerUrl = "https://www.youtube.com/embed/" + key + "?controls=0&amp;showinfo=0"; //$NON-NLS-1$ //$NON-NLS-2$
                            moviePayload.setTrailerUrl(trailerUrl);
                        }
                    }
                }
            }

            // Get the certification and release date and add it to the payload.
            JsonObject releases = tmdbResponse.getAsJsonObject("releases"); //$NON-NLS-1$
            if (releases != null) {
                JsonArray jArray = releases.getAsJsonArray("countries"); //$NON-NLS-1$
                for (int i = 0; i < jArray.size(); i++) {
                    JsonObject obj = (JsonObject) jArray.get(i);
                    if (obj.get("iso_3166_1").getAsString().equals("US")) { //$NON-NLS-1$ //$NON-NLS-2$
                        moviePayload.setCertificationCountry("US"); //$NON-NLS-1$
                        moviePayload.setCertification(UtilityFunctions.getPropValue(obj, "certification")); //$NON-NLS-1$
                        break;
                    }
                }
            }
            return Response.ok(moviePayload, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (URISyntaxException e) {
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.TMDB_INVALID_URL"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.URI_EXCEPTION"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (ClientProtocolException e) {
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.TMDB_CALL_FAIL"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.TMDB_HTTP_GET_CLIENT_EXCEPTION"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (IllegalStateException e) {
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.TMDB_REQUEST_FAIL"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.TMDB_HTTP_GET_ILLEGAL_STATE_EXCEPTION"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (IOException e) {
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.TMDB_REQUEST_FAIL"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.IOEXCEPTION_TMDB_GET"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (HttpException e) {
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.TMDB_REQUEST_FAIL"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.TMDB_HTTP_GET_EXCEPTION"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (ParseException e) {
            errorMessage = Messages.getString("SearchTheMovieDbProxyResource.UNEXPECTED_RESPONSE"); //$NON-NLS-1$
            issue = Messages.getString("SearchTheMovieDbProxyResource.HTTPGET_PARSE_EXCEPTION"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (WatsonTheatersException e) {
            UtilityFunctions.logger.error(issue, e);
        }
        return Response.serverError().entity(new ServerErrorPayload(errorMessage, issue)).build();
    }
}