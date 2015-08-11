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
import java.net.ConnectException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Ashima Arora
 *         <p>
 *         Utility class.
 *         </p>
 *         <p>
 *         This class contains helper methods used by proxy classes {@code SearchTheMovieDbProxyResource} and {@code WDSBlueMixProxyResource}.
 *         </p>
 */
public final class UtilityFunctions {
    static Logger logger = LogManager.getLogger("watson.theaters.logger"); //$NON-NLS-1$

    /**
     * Parses response to JSON object
     * <p>
     * This extracts a JSON object response from a CloseableHttpResponse.</p>
     * 
     * @param response the CloseableHttpResponse
     * @return the JSON object response
     * @throws IllegalStateException if the response stream cannot be parsed correctly
     * @throws IOException if it is unable to parse the response
     * @throws HttpException if the HTTP call responded with a status code other than 200 or 201
     */
    public static JsonObject parseHTTPResponse(CloseableHttpResponse response, String uri) throws IllegalStateException, IOException, HttpException {
        int statusCode = response.getStatusLine().getStatusCode();

        // Messages.setInfo(response.getLocale());
        if (statusCode != 200 && statusCode != 201) {
            logger.error(MessageFormat.format(Messages.getString("UtilityFunctions.HTTP_STATUS"), response.getStatusLine().getStatusCode(), uri)); //$NON-NLS-1$
            throw new HttpException(MessageFormat.format(Messages.getString("UtilityFunctions.HTTP_STATUS"), response.getStatusLine().getStatusCode(), uri));
        }
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity);
        JsonElement je = new JsonParser().parse(strResponse);
        JsonObject jo = je.getAsJsonObject();
        return jo;
    }

    /**
     * Makes HTTP PUT request
     * <p>
     * This makes HTTP PUT requests to the url provided.</p>
     * 
     * @param httpClient the http client used to make the request
     * @param url the url for the request
     * @param requestJson the JSON object containing the parameters for the PUT request
     * @return the JSON object response
     * @throws ClientProtocolException if it is unable to execute the call
     * @throws IOException if it is unable to execute the call
     * @throws IllegalStateException if the input stream could not be parsed correctly
     * @throws HttpException if the HTTP call responded with a status code other than 200 or 201
     */
    public static JsonObject httpPut(CloseableHttpClient httpClient, String url, JsonObject requestJson) throws ClientProtocolException, IOException,
            IllegalStateException, HttpException {
        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader("Content-Type", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
        httpPut.addHeader("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
        String inp = requestJson.toString();
        StringEntity input = new StringEntity(inp, ContentType.APPLICATION_JSON);
        httpPut.setEntity(input);
        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
            return UtilityFunctions.parseHTTPResponse(response, url);
        } catch (ClientProtocolException e) {
            throw e;
        }
    }

    /**
     * Makes HTTP POST request
     * <p>
     * This makes HTTP POST requests to the url provided.
     * 
     * @param httpClient the http client used to make the request
     * @param url the url for the request
     * @param nvps the {name, value} pair parameters to be embedded in the request
     * @return the JSON object response
     * @throws ClientProtocolException if it is unable to execute the call
     * @throws IOException if it is unable to execute the call
     * @throws IllegalStateException if the input stream could not be parsed correctly
     * @throws HttpException if the HTTP call responded with a status code other than 200 or 201
     */
    public static JsonObject httpPost(CloseableHttpClient httpClient, String url, List<NameValuePair> nvps) throws ConnectException, ClientProtocolException,
            IOException, IllegalStateException, HttpException {
        HttpPost httpPost = new HttpPost(url);
        if (nvps != null) {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        }
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return parseHTTPResponse(response, url);
        }
    }

    /**
     * Makes HTTP GET request
     * <p>
     * This makes HTTP GET requests to the url provided.
     * 
     * @param httpClient the http client used to make the request
     * @param uri the uri for the request
     * @return the JSON object response
     * @throws ClientProtocolException if it is unable to execute the call
     * @throws IOException if it is unable to execute the call
     * @throws IllegalStateException if the input stream could not be parsed correctly
     * @throws HttpException if the HTTP call responded with a status code other than 200 or 201
     */
    public static JsonObject httpGet(CloseableHttpClient httpClient, URI uri) throws ClientProtocolException, IOException, IllegalStateException, HttpException {
        HttpGet httpGet = new HttpGet(uri);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return UtilityFunctions.parseHTTPResponse(response, uri.toString());
        } catch (ClientProtocolException e) {
            throw e;
        }

    }

    /**
     * Returns a property's value from a JSON
     * <p>
     * This returns a property's value from a JSON object.</p>
     * 
     * @param json the JSON object to be searched
     * @param propName the name of the property key
     * @return the value of the property
     */
    public static String getPropValue(JsonObject json, String propName) {
        if (json != null && propName != null) {
            if (json.has(propName)) {
                if (json.get(propName) != null) {
                    JsonElement element = json.get(propName);
                    if (!element.isJsonNull()) {
                        return json.get(propName).getAsString();
                    }
                }
            }
        }
        return null;
    }
}
