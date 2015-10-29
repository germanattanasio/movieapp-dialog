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
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.dialog.v1.model.NameValue;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifiedClass;
import com.ibm.watson.movieapp.dialog.exception.WatsonTheatersException;
import com.ibm.watson.movieapp.dialog.payload.MoviePayload;
import com.ibm.watson.movieapp.dialog.payload.ServerErrorPayload;
import com.ibm.watson.movieapp.dialog.payload.WDSConversationPayload;

/**
 * <p>
 * Proxy class to communicate with Watson Dialog Service
 * (WDS) to generate chat responses to the user input.
 * </p>
 * <p>
 * There are multiple JAX-RS entry points to this class, depending on the task to be performed. eg.: /postConversation to post the user input to the WDS
 * service and get a response.
 * </p>
 * <p>
 * In addition, there are various helper methods to parse response text, etc.
 * </p>
 */

@Path("/bluemix")
public class WDSBlueMixProxyResource {
    private static String wds_base_url;
    private static String nlc_base_url;
    private static DialogService dialogService = null;
    private static NaturalLanguageClassifier nlcService = null;
    private static String dialog_id;
    private static String classifier_id;
    private static String username_dialog = null;
    private static String password_dialog = null;
    private static String username_nlc = null;
    private static String password_nlc = null;
    private static String personalized_prompt_movie_selected = "USER CLICKS BOX"; //$NON-NLS-1$
    private static String personalized_prompt_movies_returned = "UPDATE NUM_MOVIES"; //$NON-NLS-1$
    private static String personalized_prompt_current_index = "UPDATE CURRENT_INDEX"; //$NON-NLS-1$

    static {
        loadStaticBluemixProperties();
        useDialogServiceInstance();
        useClassifierServiceInstance();
    }

    /**
     * Loads VCAP_SERVICES environment variables required to make calls to Dialog and Classifier services.
     */
    private static void loadStaticBluemixProperties() {
        String envServices = System.getenv("VCAP_SERVICES"); //$NON-NLS-1$
        if (envServices != null) {
            UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.VCAP_SERVICES_ENV_VAR_FOUND")); //$NON-NLS-1$
            JsonObject services = new JsonParser().parse(envServices).getAsJsonObject();
            UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.VCAP_SERVICES_JSONOBJECT_SUCCESS")); //$NON-NLS-1$

            // Get credentials for Dialog Service
            JsonArray arr = (JsonArray) services.get("dialog"); //$NON-NLS-1$
            if (arr != null && arr.size() > 0) {
                services = arr.get(0).getAsJsonObject();
                JsonObject credentials = services.get("credentials").getAsJsonObject(); //$NON-NLS-1$
                wds_base_url = credentials.get("url").getAsString(); //$NON-NLS-1$
                if (credentials.get("username") != null && !credentials.get("username").isJsonNull()) { //$NON-NLS-1$ //$NON-NLS-2$
                    username_dialog = credentials.get("username").getAsString(); //$NON-NLS-1$
                    UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.FOUND_WDS_USERNAME")); //$NON-NLS-1$
                }
                if (credentials.get("password") != null && !credentials.get("password").isJsonNull()) { //$NON-NLS-1$ //$NON-NLS-2$
                    password_dialog = credentials.get("password").getAsString(); //$NON-NLS-1$
                    UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.FOUND_WDS_PASSWORD")); //$NON-NLS-1$
                }
            }

            // Get credentials for Natural Language Classifier Service
            services = new JsonParser().parse(envServices).getAsJsonObject();
            arr = (JsonArray) services.get("natural_language_classifier"); //$NON-NLS-1$
            if (arr != null && arr.size() > 0) {
                services = arr.get(0).getAsJsonObject();
                JsonObject credentials = services.get("credentials").getAsJsonObject(); //$NON-NLS-1$
                nlc_base_url = credentials.get("url").getAsString(); //$NON-NLS-1$
                if (credentials.get("username") != null && !credentials.get("username").isJsonNull()) { //$NON-NLS-1$ //$NON-NLS-2$
                    username_nlc = credentials.get("username").getAsString(); //$NON-NLS-1$
                    UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.FOUND_NLC_USERNAME")); //$NON-NLS-1$
                }
                if (credentials.get("password") != null && !credentials.get("password").isJsonNull()) { //$NON-NLS-1$ //$NON-NLS-2$
                    password_nlc = credentials.get("password").getAsString(); //$NON-NLS-1$
                    UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.FOUND_NLC_PASSWORD")); //$NON-NLS-1$
                }
            }
        } else {
            UtilityFunctions.logger.error(Messages.getString("WDSBlueMixProxyResource.VCAP_SERVICES_CANNOT_LOAD")); //$NON-NLS-1$
        }

        // Get the dialog_id
        envServices = System.getenv("DIALOG_ID"); //$NON-NLS-1$
        if (envServices != null) {
            dialog_id = envServices;
            UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.DIALOG_ACCOUNT_ID_SUCCESS")); //$NON-NLS-1$
        } else {
            UtilityFunctions.logger.error(Messages.getString("WDSBlueMixProxyResource.DIALOG_ACCOUNT_ID_FAIL")); //$NON-NLS-1$
        }

        // Get the classifier_id
        envServices = System.getenv("CLASSIFIER_ID"); //$NON-NLS-1$
        if (envServices != null) {
            classifier_id = envServices;
            UtilityFunctions.logger.info(Messages.getString("WDSBlueMixProxyResource.CLASSIFIER_ID_SUCCESS")); //$NON-NLS-1$
        } else {
            UtilityFunctions.logger.error(Messages.getString("WDSBlueMixProxyResource.CLASSIFIER_ID_FAIL")); //$NON-NLS-1$
        }
    }

    /**
     * Sets the values of the dialog-specific variables
     */
    private static void useDialogServiceInstance() {
        if (username_dialog != null && password_dialog != null) {
            dialogService = new DialogService();
            dialogService.setUsernameAndPassword(username_dialog, password_dialog);
            dialogService.setEndPoint(wds_base_url);
        }else{
            UtilityFunctions.logger.error(Messages.getString("WDSBlueMixProxyResource.DIALOG_CREDENTIALS_EMPTY"));
        }
    }

    /**
     * Sets the values of the classifier-specific variables if specified
     */
    private static void useClassifierServiceInstance(){
        if (username_nlc != null && password_nlc != null) {
            nlcService = new NaturalLanguageClassifier();
            nlcService.setUsernameAndPassword(username_nlc, password_nlc);
            nlcService.setEndPoint(nlc_base_url);
        }else{
            UtilityFunctions.logger.error(Messages.getString("WDSBlueMixProxyResource.NLC_CREDENTIALS_EMPTY"));
        }
    }

    /**
     * Checks and extracts movie parameters sent by WDS
     * <p>
     * This will extract movie parameters sent by WDS (in the response text) when they're sent.
     * </p>
     * 
     * @param wdsResponseText the textual part of the response sent by WDS
     * @return the JsonObject containing the response from WDS as well as the parameters and their values sent by WDS.
     */
    public JsonObject matchSearchNowPattern(String wdsResponseText) {
        JsonObject result = new JsonObject();
        // If WDS wants us to search themoviedb then it will return a JSON
        // payload within the response. Quickly check the response for a specific token
        int idx = wdsResponseText.toLowerCase().indexOf("{search_now:"); //$NON-NLS-1$
        if (idx != -1) {
            // token exists, parse out some extra chars from dialog.
            String json = wdsResponseText.substring(idx).trim();
            wdsResponseText = wdsResponseText.substring(0, idx - 1).trim();
            if (json.startsWith("\"")) { //$NON-NLS-1$
                json = json.substring(0);
            }
            if (json.endsWith("\"")) { //$NON-NLS-1$
                json = json.substring(0, json.length() - 1);
            }
            JsonElement element = new JsonParser().parse(json);
            result.add("Params", element.getAsJsonObject()); //$NON-NLS-1$
        }
        result.addProperty("WDSMessage", wdsResponseText); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return result;
    }

    /**
     * Makes chat conversation with WDS
     * <p>
     * This makes chat conversation with WDS for the provided client id and conversation id, against the user input provided.
     * </p>
     * <p>
     * When WDS has collected all the required movie preferences, it sends a bunch of movie parameters embedded in the text response and signals to discover
     * movies from themoviedb.org. There may be the following kinds of discover movie calls:
     * <ul>
     * <li>New search: First time searching for the given set of parameters
     * <li>Repeat search: Repeat the search with the same parameters (just re-display the results)
     * <li>Previous search: Display the results on the previous page
     * <li>Next search: Display the results on the next page
     * </ul>
     * Depending on the kind of call, profile variables are set in WDS and personalized prompts are retrieved to be sent back to the UI in the payload.
     * </p>
     * 
     * @param conversationId the conversation id for the client id specified
     * @param clientId the client id for the session
     * @param input the user's input
     * @return a response containing either of these two entities- {@code WDSConversationPayload} or {@code ServerErrorPayload}
     */
    @GET
    @Path("/postConversation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postConversation(@QueryParam("conversationId") String conversationId, @QueryParam("clientId") String clientId,
            @QueryParam("input") String input) {
        long lStartTime = System.nanoTime();
        long lEndTime, difference;
        String errorMessage = null, issue = null;
        String wdsMessage = null;
        JsonObject processedText = null;
        if (input == null || input.trim().isEmpty()) {
            errorMessage = Messages.getString("WDSBlueMixProxyResource.SPECIFY_INPUT"); //$NON-NLS-1$
            issue = Messages.getString("WDSBlueMixProxyResource.EMPTY_QUESTION"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue);
            return Response.serverError().entity(new ServerErrorPayload(errorMessage, issue)).build();
        }
        try {

            // 1.Get all the class info from NLC and set appropriate profile variables.
            List<ClassifiedClass> classInfo = null;
            if(nlcService != null){
                if (UtilityFunctions.logger.isTraceEnabled()) {
                    UtilityFunctions.logger.trace(Messages.getString("WDSBlueMixProxyResource.NLC_SERVICE")); //$NON-NLS-1$
                }
                // Send utterance to NLC to get user intent
                Classification classification = nlcService.classify(classifier_id, input);
                classInfo = classification.getClasses();
                // Set classification profile variables for WDS.
                List<NameValue> nameValues = new ArrayList<NameValue>();
                nameValues.add(new NameValue("Class1", classInfo.get(0).getName()));
                nameValues.add(new NameValue("Class1_Confidence", Double.toString(classInfo.get(0).getConfidence())));
                nameValues.add(new NameValue("Class2", classInfo.get(1).getName()));
                nameValues.add(new NameValue("Class2_Confidence", Double.toString(classInfo.get(1).getConfidence())));
                dialogService.updateProfile(dialog_id, Integer.parseInt(clientId), nameValues);
            }

            // 2. Send original utterance to WDS
            Map<String, Object> converseParams = new HashMap<String, Object>();
            converseParams.put("dialog_id", dialog_id);
            converseParams.put("client_id", Integer.parseInt(clientId));
            converseParams.put("conversation_id", Integer.parseInt(conversationId));
            converseParams.put("input", input);
            Conversation conversation = dialogService.converse(converseParams);
            wdsMessage = StringUtils.join(conversation.getResponse(), " ");
            processedText = matchSearchNowPattern(wdsMessage);
            WDSConversationPayload conversationPayload = new WDSConversationPayload();
            if (!processedText.has("Params")) { //$NON-NLS-1$
                // We do not have enough info to search the movie db, go back to the user for more info.
                conversationPayload.setClientId(clientId); //$NON-NLS-1$
                conversationPayload.setConversationId(clientId); //$NON-NLS-1$
                conversationPayload.setInput(input); //$NON-NLS-1$
                conversationPayload.setWdsResponse(processedText.get("WDSMessage").getAsString()); //$NON-NLS-1$
                if (UtilityFunctions.logger.isTraceEnabled()) {
                    // Log the execution time.
                    lEndTime = System.nanoTime();
                    difference = lEndTime - lStartTime;
                    UtilityFunctions.logger.trace("Throughput: " + difference/1000000 + "ms.");
                }
                return Response.ok(conversationPayload, MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                // Dialog says we have enough info to proceed with a search of themoviedb..
                // Find out search variables.
                JsonObject paramsObj = processedText.getAsJsonObject("Params"); //$NON-NLS-1$
                boolean newSearch = false, prevSearch = false, nextSearch = false, repeatSearch = false;
                String page = paramsObj.get("Page").getAsString(); //$NON-NLS-1$
                switch (page) {
                case "new":newSearch = true; //$NON-NLS-1$
                    break;
                case "next":nextSearch = true; //$NON-NLS-1$
                    break;
                case "previous":prevSearch = true; //$NON-NLS-1$
                    break;
                case "repeat":repeatSearch = true; //$NON-NLS-1$
                    break;
                default:
                    errorMessage = Messages.getString("WDSBlueMixProxyResource.DIALOG_UNDERSTAND_FAIL"); //$NON-NLS-1$
                    issue = Messages.getString("WDSBlueMixProxyResource.PAGE_TYPE_NOT_UNDERSTOOD"); //$NON-NLS-1$
                    UtilityFunctions.logger.error(issue);
                }

                if (UtilityFunctions.logger.isTraceEnabled()) {
                    UtilityFunctions.logger.trace(Messages.getString("WDSBlueMixProxyResource.WDS_RESPONSE") + paramsObj); //$NON-NLS-1$
                }
                String prompt;
                Integer currentIndex = Integer.parseInt(paramsObj.get("Index").getAsString()); //$NON-NLS-1$
                Integer numMovies = 0;
                Integer totalPages = 0;
                boolean tmdbCallNeeded = true;
                List<NameValue> nameValues;
                if(paramsObj.has("Total_Movies")){
                    numMovies = Integer.parseInt(paramsObj.get("Total_Movies").getAsString());
                    totalPages = Integer.parseInt(paramsObj.get("Total_Pages").getAsString());
                    // If the user wishes to "go back" when the first set of results is displayed or
                    // "show more" results when all results have been displayed already---> do not need to make a call to themoviedb.org
                    tmdbCallNeeded = !((currentIndex <= 10 && prevSearch) || (currentIndex == numMovies && nextSearch));
                }
                if(tmdbCallNeeded){
                    // Need to make a call to TMDB.
                    int pageNum = (int) Math.ceil((float) currentIndex / 20);// round up.. 10/20 = .5 == page# 1
                    if ((nextSearch || newSearch) && (currentIndex % 20) == 0) {
                        pageNum++;
                    }

                    // Decrement page num. eg.: currentIndex = 30, 23, etc. Do not decrement page num for currentIndex = 20, 36, etc.
                    if (prevSearch && (currentIndex % 20 <= 10 && (currentIndex % 20 != 0))) {
                        pageNum--;
                    }

                    int currentDisplayCount = (currentIndex % 10 == 0) ? 10 : currentIndex % 10;   
                    SearchTheMovieDbProxyResource tmdb = new SearchTheMovieDbProxyResource();
                    conversationPayload = tmdb.discoverMovies(UtilityFunctions.getPropValue(paramsObj, "Genre"),  //$NON-NLS-1$
                            UtilityFunctions.getPropValue(paramsObj, "Rating"),  //$NON-NLS-1$
                            UtilityFunctions.getPropValue(paramsObj, "Recency"),  //$NON-NLS-1$
                            currentIndex, pageNum, nextSearch || newSearch);
                    int size = conversationPayload.getMovies().size();
                    if (prevSearch) {
                        currentIndex -= currentDisplayCount;
                    } else if (nextSearch || newSearch) {
                        currentIndex += size;
                    }

                    nameValues = new ArrayList<NameValue>();
                    // Save the number of movies displayed till now.
                    nameValues.add(new NameValue("Current_Index", currentIndex.toString())); //$NON-NLS-1$
                    // Save the total number of pages in a profile variable.
                    nameValues.add(new NameValue("Total_Pages", conversationPayload.getTotalPages().toString())); //$NON-NLS-1$
                    // Save the total number of movies in Num_Movies.
                    nameValues.add(new NameValue("Num_Movies", conversationPayload.getNumMovies().toString())); //$NON-NLS-1$
                    // Set the profile variables.
                    dialogService.updateProfile(dialog_id, Integer.parseInt(clientId), nameValues);
                }
                if(!tmdbCallNeeded){
                    // Set the value of the Index_Updated profile variable to No so that WDS knows that no indices were updated.
                    nameValues = new ArrayList<NameValue>();
                    nameValues.add(new NameValue("Index_Updated", "No"));
                    dialogService.updateProfile(dialog_id, Integer.parseInt(clientId), nameValues);
                    // Set some values in the ConversationPayload which are needed by the UI.
                    List <MoviePayload> movies = new ArrayList<MoviePayload>();
                    conversationPayload.setMovies(movies);
                    conversationPayload.setNumMovies(numMovies);
                    conversationPayload.setTotalPages(totalPages);
                }
                // If first time, get personalized prompt based on Num_Movies
                prompt = personalized_prompt_current_index;
                if (newSearch || repeatSearch) {
                    prompt = personalized_prompt_movies_returned;
                }
                // Get the personalized prompt.
                converseParams = new HashMap<String, Object>();
                converseParams.put("dialog_id", dialog_id);
                converseParams.put("client_id", Integer.parseInt(clientId));
                converseParams.put("conversation_id", Integer.parseInt(conversationId));
                converseParams.put("input", prompt);                
                conversation = dialogService.converse(converseParams);
                wdsMessage = StringUtils.join(conversation.getResponse(), " ");

                // Build the moviePayload.
                conversationPayload.setWdsResponse(wdsMessage);
                conversationPayload.setClientId(clientId); //$NON-NLS-1$
                conversationPayload.setConversationId(clientId); //$NON-NLS-1$
                conversationPayload.setInput(input); //$NON-NLS-1$

                if (UtilityFunctions.logger.isTraceEnabled()) {
                    // Log the execution time.
                    lEndTime = System.nanoTime();
                    difference = lEndTime - lStartTime;
                    UtilityFunctions.logger.trace("Throughput: " + difference/1000000 + "ms.");
                }
                // Return to UI.
                return Response.ok(conversationPayload, MediaType.APPLICATION_JSON_TYPE).build();
            }
        } catch (ClientProtocolException e) {
            errorMessage = Messages.getString("WDSBlueMixProxyResource.API_CALL_NOT_EXECUTED"); //$NON-NLS-1$
            issue = Messages.getString("WDSBlueMixProxyResource.CLIENT_EXCEPTION_IN_GET_RESPONSE"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (IllegalStateException e) {
            errorMessage = Messages.getString("WDSBlueMixProxyResource.API_CALL_NOT_EXECUTED"); //$NON-NLS-1$
            issue = Messages.getString("WDSBlueMixProxyResource.ILLEGAL_STATE_GET_RESPONSE"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (IOException e) {
            errorMessage = Messages.getString("WDSBlueMixProxyResource.API_CALL_NOT_EXECUTED"); //$NON-NLS-1$
            issue = Messages.getString("WDSBlueMixProxyResource.IO_EXCEPTION_GET_RESPONSE"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (HttpException e) {
            errorMessage = Messages.getString("WDSBlueMixProxyResource.TMDB_API_CALL_NOT_EXECUTED"); //$NON-NLS-1$
            issue = Messages.getString("WDSBlueMixProxyResource.HTTP_EXCEPTION_GET_RESPONSE"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (WatsonTheatersException e) {
            errorMessage = e.getErrorMessage();
            issue = e.getIssue();
            UtilityFunctions.logger.error(issue, e);
        } catch (URISyntaxException e) {
            errorMessage = Messages.getString("WDSBlueMixProxyResource.TMDB_URL_INCORRECT"); //$NON-NLS-1$
            issue = Messages.getString("WDSBlueMixProxyResource.URI_EXCEPTION_IN_DISOVERMOVIE"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        } catch (ParseException e) {
            errorMessage = Messages.getString("WDSBlueMixProxyResource.TMDB_RESPONSE_PARSE_FAIL"); //$NON-NLS-1$
            issue = Messages.getString("WDSBlueMixProxyResource.PARSE_EXCEPTION_TMDB_GET"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        }
        return Response.serverError().entity(new ServerErrorPayload(errorMessage, issue)).build();
    }

    /**
     * Returns selected movie details
     * <p>
     * This extracts the details of the movie specified. It uses themoviedb.org API to populate movie details in {@link MoviePayload}.
     * </p>
     * 
     * @param clientId the client id for the session
     * @param conversationId the conversation id for the client id specified
     * @param movieName the movie name
     * @param movieId the movie id
     * @return a response containing either of these two entities- {@code WDSConversationPayload} or {@code ServerErrorPayload}
     */
    @GET
    @Path("/getSelectedMovieDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelectedMovieDetails(@QueryParam("clientId") String clientId, @QueryParam("conversationId") String conversationId,
            @QueryParam("movieName") String movieName, @QueryParam("movieId") String movieId) throws IOException, HttpException, WatsonTheatersException {

        String errorMessage = Messages.getString("WDSBlueMixProxyResource.WDS_API_CALL_NOT_EXECUTED"); //$NON-NLS-1$
        String issue = null;
        WDSConversationPayload conversationPayload = new WDSConversationPayload();
        try {
            // Get movie info from TMDB.
            SearchTheMovieDbProxyResource tmdb = new SearchTheMovieDbProxyResource();
            Response tmdbResponse = tmdb.getMovieDetails(movieId, movieName);
            MoviePayload movie = (MoviePayload) tmdbResponse.getEntity();

            // Set the profile variable for WDS.
            List<NameValue> nameValues = new ArrayList<NameValue>();
            nameValues.add(new NameValue("Selected_Movie", URLEncoder.encode(movieName, "UTF-8"))); //$NON-NLS-1$ //$NON-NLS-2$
            nameValues.add(new NameValue("Popularity_Score", movie.getPopularity().toString())); //$NON-NLS-1$
            dialogService.updateProfile(dialog_id, Integer.parseInt(clientId), nameValues);

            // Get the personalized prompt.
            Map<String, Object> converseParams = new HashMap<String, Object>();
            converseParams.put("dialog_id", dialog_id);
            converseParams.put("client_id", Integer.parseInt(clientId));
            converseParams.put("conversation_id", Integer.parseInt(conversationId));
            converseParams.put("input", personalized_prompt_movie_selected);
            Conversation conversation = dialogService.converse(converseParams);
            String wdsMessage = StringUtils.join(conversation.getResponse(), " ");

            // Add the wds personalized prompt to the MoviesPayload and return.
            List<MoviePayload> movieList = new ArrayList<MoviePayload>();
            movieList.add(movie);
            conversationPayload.setMovies(movieList);
            conversationPayload.setWdsResponse(wdsMessage);
            if (UtilityFunctions.logger.isTraceEnabled()) {
                UtilityFunctions.logger
                        .trace(Messages.getString("WDSBlueMixProxyResource.MOVIE_NAME") + movieName + Messages.getString("WDSBlueMixProxyResource.POPULARITY") + movie.getPopularity().toString()); //$NON-NLS-1$ //$NON-NLS-2$
                UtilityFunctions.logger.trace(Messages.getString("WDSBlueMixProxyResource.WDS_PROMPT_SELECTED_MOVIE") + wdsMessage); //$NON-NLS-1$
            }
            return Response.ok(conversationPayload, MediaType.APPLICATION_JSON_TYPE).build();

        } catch (IllegalStateException e) {
            issue = Messages.getString("WDSBlueMixProxyResource.ILLEGAL_STATE_EXCEPTION_GET_RESPONSE"); //$NON-NLS-1$
            UtilityFunctions.logger.error(issue, e);
        }
        return Response.serverError().entity(new ServerErrorPayload(errorMessage, issue)).build();
    }

    /**
     * Initializes chat with WDS This initiates the chat with WDS by requesting for a client id and conversation id(to be used in subsequent API calls) and a
     * response message to be displayed to the user. If it's a returning user, it sets the First_Time profile variable to "No" so that the user is not taken
     * through the hand-holding process.
     * 
     * @param firstTimeUser specifies if it's a new user or a returning user(true/false). If it is a returning user WDS is notified via profile var.
     * 
     * @return a response containing either of these two entities- {@code WDSConversationPayload} or {@code ServerErrorPayload}
     */
    @GET
    @Path("/initChat")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startConversation(@QueryParam("firstTimeUser") boolean firstTimeUser) {
        Conversation conversation = dialogService.createConversation(dialog_id);
        if (!firstTimeUser) {
            List<NameValue> nameValues = new ArrayList<NameValue>();
            nameValues.add(new NameValue("First_Time", "No"));
            dialogService.updateProfile(dialog_id, conversation.getClientId(), nameValues);
        }
        WDSConversationPayload conversationPayload = new WDSConversationPayload();
        conversationPayload.setClientId(Integer.toString(conversation.getClientId())); //$NON-NLS-1$
        conversationPayload.setConversationId(Integer.toString(conversation.getId())); //$NON-NLS-1$
        conversationPayload.setInput(conversation.getInput()); //$NON-NLS-1$
        conversationPayload.setWdsResponse(StringUtils.join(conversation.getResponse(), " "));
        return Response.ok(conversationPayload, MediaType.APPLICATION_JSON_TYPE).build();
    }
}