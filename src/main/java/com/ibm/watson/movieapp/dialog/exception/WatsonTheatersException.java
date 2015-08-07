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

package com.ibm.watson.movieapp.dialog.exception;

/**
 * <P>
 * WatsonTheatersException Object
 * <P>
 * Attributes of an exception of type WatsonTheatersException. This class is instantiated in REST endpoints {@code SearchTheMovieDbProxyResource} and
 * {@code WDSBlueMixProxyResource} when an exception specific to this application is to be thrown.
 * 
 * @author Ashima Arora
 */
public class WatsonTheatersException extends Exception {

    private String errorMessage;
    private String issue;

    private static final long serialVersionUID = 1094531525866086793L;

    /**
     * Constructor
     * 
     * @param errorMessage
     *            the user-understandable error message describing the cause of exception, not null
     * @param issue
     *            the system-understandable issue describing the exception thrown, not null
     */
    public WatsonTheatersException(String errorMessage, String issue) {
        this.errorMessage = errorMessage;
        this.issue = issue;
    }

    /**
     * @return the error message, not null
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the issue, not null
     */
    public String getIssue() {
        return issue;
    }

}
