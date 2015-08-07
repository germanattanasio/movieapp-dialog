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

package com.ibm.watson.movieapp.dialog.payload;

import java.util.List;

/**
 * <p>
 * The payload object which is ultimately returned to the client. This contains the message from the WDS system, the client id, the conversation id
 * and also the input from the client (if any). If a list of movies have been retrieved then those are returned also (as a list of MoviePayload objects).</p>
 * @author aarora
 *
 */
public class WDSConversationPayload {
    private String conversationId;
    private String clientId;
    private String input;
    private String wdsResponse;
    private List<MoviePayload> movies;
    private Integer totalPages;
    private Integer numMovies;

    /**
     * @return  the conversation id which is unique to a conversation in WDS
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * @param conversationId  the conversation id which is unique to a conversation in WDS
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * @return  the client id which is unique to a session in WDS
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId  the client id which is unique to a session in WDS
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return  the user input for the conversation
     */
    public String getInput() {
        return input;
    }

    /**
     * @param input  the user input for the conversation
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * @return  the text response from WDS
     */
    public String getWdsResponse() {
        return wdsResponse;
    }

    /**
     * @param wdsResponse  the text response from WDS
     */
    public void setWdsResponse(String wdsResponse) {
        this.wdsResponse = wdsResponse;
    }

    /**
     * @return  a list of movie payload objects containing movie info from TMDB
     */
    public List<MoviePayload> getMovies() {
        return movies;
    }

    /**
     * @param movies  a list of movie payload objects containing movie info from TMDB
     */
    public void setMovies(List<MoviePayload> movies) {
        this.movies = movies;
    }

    /**
     * @return  the total number of pages in TMDB response for the movie type preferences(uses this class object simply as a container to allow
     *         {@code WDSBlueMixProxyResource} to set profile variables depending on the value of this variable.)
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages  the total number of pages in TMDB response for the movie type preferences(uses this class object simply as a container to allow
     *            {@code WDSBlueMixProxyResource} to set profile variables depending on the value of this variable.)
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return  the total number of movies obtained using TMDB API based on the movie type preferences(uses this class object simply as a container to allow
     *         {@code WDSBlueMixProxyResource} to set profile variables depending on the value of this variable.)
     */
    public Integer getNumMovies() {
        return numMovies;
    }

    /**
     * @param numMovies  the total number of movies obtained using TMDB API based on the movie type preferences(uses this class object simply as a container to
     *            allow {@code WDSBlueMixProxyResource} to set profile variables depending on the value of this variable.)
     */
    public void setNumMovies(Integer numMovies) {
        this.numMovies = numMovies;
    }
}
