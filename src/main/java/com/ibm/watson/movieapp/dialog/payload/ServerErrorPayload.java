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

/**
 * <P>
 * Information associated with an exception in the server code. This class is instantiated when an exception is caught in {@code SearchTheMovieDbProxyResource}
 * and {@code WDSBlueMixProxyResource}. This object is sent as payload to the client-side for displaying error messages to the user.</p>
 * 
 * @author Ashima Arora
 *
 */
public class ServerErrorPayload {
    private String message;
    private String userErrorMessage;

    /**
     * Constructor
     * 
     * @param userErrorMessage the user-understandable message describing the exception thrown
     * @param message the system-understandable message describing the exception thrown
     */
    public ServerErrorPayload(String userErrorMessage, String message) {
        this.userErrorMessage = userErrorMessage;
        this.message = message;
    }

    /**
     * @return  the system-understandable message describing the exception thrown
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param  message the system-understandable message describing the exception thrown
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return  the user-understandable message describing the exception thrown
     */
    public String getUserErrorMessage() {
        return userErrorMessage;
    }

    /**
     * @param  userErrorMessage the user-understandable message describing the exception thrown
     */
    public void setUserErrorMessage(String userErrorMessage) {
        this.userErrorMessage = userErrorMessage;
    }

}
