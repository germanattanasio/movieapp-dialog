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
(function () {
    'use strict';

    angular.module('dialog.service', [])

    /**
     * @name dialogService
     * @module dialog/service
     * @description
     *
     * Implements the dialogService interface using the Watson Theaters App API to interface with the
     * Watson Dialog Service (WDS) and themoviedb.org's movie API.
     */
    .service('dialogService', function (_, $http, $q, dialogParser) {
        var clientId;
        var conversationId;
        var welcomeMessage;
        var index = 0;
        var conversation = [];

        /**
         * Gets all entries (responses) in the conversation so far.
         *
         * @public
         * @return {object[]} All entries in the conversation.
         */
        var getConversation = function () {
            return conversation;
        };
        /**
         * A shorthand for retrieving the latest entry in the conversation.
         *
         * @return {object} The latest entry in the conversation.
         */
        var getLatestResponse = function () {
            return conversation.length > 0 ? conversation[conversation.length - 1] : undefined;
        };

        /**
         * Retrieves a clientId for the API connection.
         *
         * @public
         * @return {Object} The current client id and welcome message if it exists,
         * otherwise a new one retrieved from the API.
         */
        var initChat = function () {
           var firstTimeUser = true;
            if (clientId) {
                // Reuse existing clientId
                return $q.when({
                    'clientId': clientId,
                    'welcomeMessage': welcomeMessage,
                    'conversationId': conversationId
                });
            }
            else {
                 if (typeof (Storage) !== 'undefined') {
                     // Store session
                     if (localStorage.getItem('firstTimeUser')) {
                         firstTimeUser = false;
                     }
                 }
                 return $http.get('../api/bluemix/initChat', {
                     'params': {
                         'firstTimeUser': firstTimeUser
                     }
                 }).then(function (response) {
                     if (typeof (Storage) !== 'undefined') {
                         //User session has been initialized, nest time true we want to
                         //notify the system that this is not the user's first session.
                         localStorage.setItem('firstTimeUser', 'false');
                     }
                     clientId = response.data.clientId;
                     welcomeMessage = response.data.wdsResponse;
                     conversationId = response.data.conversationId;
                     return {
                         'clientId': clientId,
                         'welcomeMessage': welcomeMessage,
                         'conversationId': conversationId
                     };
                 }, function (errorResponse) {
                     var data = errorResponse;
                     if (errorResponse) {
                         data = data.data;
                         return {
                             'clientId': null,
                             'welcomeMessage': data.userErrorMessage,
                             'conversationId': null
                         };
                    }
                });
            }
        };

        /**
         * Calls the WDS getResponse API. Given a question/input from the user a call is made to the API. The response
         * is parsed appropriately. e.g. movies parsed from payload etc
         *
         * @private
         * @return {object} A JSON object representing a segment in the conversation.
         */
        var getResponse = function (question) {
            return $http.get('../api/bluemix/postConversation', {
                'params': {
                    'clientId': clientId,
                    'conversationId': conversationId,
                    'input': question
                }
            }).then(function (response) {
                var watsonResponse = response.data.wdsResponse;
                var movies = null, htmlLinks = null, transformedPayload = null;
                var segment = null;
                if (watsonResponse) {
                    if (!dialogParser.isMctInPayload(watsonResponse)) {
                        //For 'mct' tags we have to maintain the formatting.
                        watsonResponse = watsonResponse.replace(/<br>/g, '');
                    }
                    //yes, seems odd, but we are compensating for some
                    //inconsistencies in the API and how it handles new lines
                    watsonResponse = watsonResponse.replace(/\n+/g, '<br/>');
                }
                if ($.isArray(response.data.movies)) {
                    movies = response.data.movies;
                }
                if (!watsonResponse) {
                    //Unlikely, but hardcoding these values in case the dialog service/account does
                    //not provide a response with the list of movies.
                    if (movies) {
                        watsonResponse = 'Here is what I found';
                    }
                    else {
                        watsonResponse = 'Oops, this is embarrassing but my system seems to be having trouble at the moment, please try a bit later.';
                    }
                }
                if (dialogParser.isMctInPayload(watsonResponse)) {
                    transformedPayload = dialogParser.parse(watsonResponse);
                    htmlLinks = transformedPayload.htmlOptions;
                    question = transformedPayload.question;
                    watsonResponse = transformedPayload.watsonResponse;
                }
                segment = {
                        'message': question,
                        'responses': watsonResponse,
                        'movies': movies,
                        'options': htmlLinks
                    };
                return segment;
            }, function (error) {
                //Error case!
                var response = error.data.userErrorMessage;
                if (!response) {
                    response = 'Failed to get valid response from the Dialog service. Please refresh your browser';
                }
                return {
                    'message': question,
                    'responses': response
                };
            });
        };

        /**
         * A (public) utility method that ensures initChat is called and returns before calling the getResponse API.
         *
         * @public
         * @return {object[]} An array of chat segments.
         */
        var query = function (input) {
            conversation.push({
                'message': input,
                'index': index++
            });

            return initChat().then(function () {
                var response = $q.when();
                response = response.then(function (res) {
                    if (res) {
                        conversation.push(res);
                    }
                    return getResponse(input);
                });
                return response;
            }, function (error) {
                var segment = {};
                segment.responses = 'Error received from backend system. Please refresh the browser to start again.';
                conversation.push(segment);
            }).then(function (lastRes) {
                if (lastRes) {
                    conversation.forEach(function (segment) {
                        if (segment.index === index - 1) {
                            segment.responses = lastRes.responses;
                            segment.movies = lastRes.movies;
                            segment.options = lastRes.options;
                        }
                    });
                }
                return conversation;
            });
        };
        /**
         * Called when the end user clicks on a movie. A REST call is initiated to the app server code which
         * acts as a proxy to WDS and themoviedb.
         *
         * @private
         */
        var getMovieInfo = function (movieName, id, popularity) {
            return initChat().then(function (res) {
                return $http.get('../api/bluemix/getSelectedMovieDetails', {
                'params': {
                    'clientId': res.clientId,
                    'conversationId': res.conversationId,
                    'movieName': movieName,
                    'movieId': id
                }
            }, function (errorResponse) {
                var data = errorResponse;
                if (errorResponse) {
                    data = data.data;
                }
            }).then(function (response) {
                var segment = response.data;
                if (segment) {
                    if (segment.movies && segment.movies.length > 0) {
                        segment = segment.movies[0];
                    }
                    segment.commentary = response.data.wdsResponse;
                }
                return segment;
                },
                function (error) {
                    var segment = error.data;
                    if (segment) {
                        if (error.data.userErrorMessage) {
                            segment.commentary = error.data.userErrorMessage;
                        }
                        else {
                            segment.commentary = 'Failed to retrieve movie details. Please retry later.';
                        }
                    }
                    segment.error = true;
                    return segment;
                });
            });
        };

        return {
            'getConversation': getConversation,
            'getLatestResponse': getLatestResponse,
            'initChat': initChat,
            'query': query,
            'getMovieInfo': getMovieInfo
        };
    });
}());
