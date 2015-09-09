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

    /**
     * @name DialogController
     * @module dialog/controller
     * @description
     *
     * Controls the state of the Dialog view. At any given point of time, the Dialog is in one of the following states:
     *
     * - initial  The "home" view displayed to the user when launching dialog
     * - chatting  The view displayed when user is typing a new response/question
     * - preview  The view is showing a movie preview
     * - favorites  When in small resolutions the favorites panel is displayed
     *
     */
    var DialogController = function (_, $rootScope, $scope, $location, $anchorScroll, $timeout, gettextCatalog, dialogService) {
        var self = this;
        var placeholderText = null;
        var states = {
            'intro': {
                'key': 'intro',
                'class': 'intro',
                'placeholder': 'Loading. Please wait...',
                'introText': ''
            },
            'chatting': {
                'key': 'chatting',
                'class': 'chatting',
                'placeholder': 'Start typing...',
                'introText': ''
            },
            'preview': {
                'key': 'preview',
                'class': 'preview',
                'placeholder': 'Start typing...',
                'introText': ''
            },
            'favorites': {
                'key': 'favorites',
                'class': 'favorites',
                'placeholder': 'Start typing...',
                'introText': ''
            }
        };

        var setState = function (state) {
            self.state = _.cloneDeep(state);
        };

        self.favorites = [];
        self.selectedMovies = [];
        self.selectedMovie = {};
        self.showFavorites = false;

        /**
         * Called when a user clicks on a movie within the UI. This util method first checked if the selected
         * movie was previously clicked on. If it was the cached movie details are displayed and a REST call
         * is made to the WDS API to inform it of the current selection.
         * If the movie is not cached (not previously clicked on) a REST call is made to retrieve the movie
         * details and to inform WDS of the current selection. Once the movie details are retrieved
         * they are cached (for the duration of the session) so that further REST calls are not needed
         * in order to populate the preview UI.
         *
         * @public
         */
        self.selectMovie = function (movie) {
            var movieName = null;
            var movieId = null;
            var popularity = null;
            var result = null;
            var keys = null;
            var objKeys = null;
            var query = null;
            var scrollable = null;
            if (movie) {
                movieName = movie.movieName;
                movieId = movie.movieId;
                popularity = movie.popularity;
            }
            else {
                return null;
            }
            result = _.find(self.selectedMovies, { 'movieId': movieId, 'movieName': movieName });
            if (result) {
                keys = _.keys(self.selectedMovie);
                if (keys) {
                    keys.forEach(function (key) {
                        delete self.selectedMovie[key];
                    });
                }
                _.assign(self.selectedMovie, result);
                //We don't need to wait for a response here, we already have cached info.
                //We are just notifying WDS of the selection.
                dialogService.getMovieInfo(movieName, movieId, popularity);
                scrollable = $('#scrollable-div');
                if (scrollable[0]) {
                    scrollable.animate({ 'scrollTop': scrollable[0].scrollHeight }, 1000);
                }
                //Reduce space between chat box and chat messages
                if ( $('#scrollable-div').height() > $('#conversationParent').height() ) {
                    $('.dialog-center').css({ 'top': $('#scrollable-div').height() - $('#conversationParent').height() - 10 + 'px' });
                    setState(states.preview);
                    return result;
                }
                $('.dialog-center').css({ 'top': '0px' });
                setState(states.preview);
                return result;
            }
            else {
                query = dialogService.getMovieInfo(movieName, movieId, popularity);
                query.then(function (segment) {
                    if (segment.error === true) {
                        setState(states.chatting);
                    }
                    else {
                        setState(states.preview);
                    }
                    objKeys = _.keys(self.selectedMovie);
                    if (objKeys) {
                        objKeys.forEach(function (objKey) {
                            delete self.selectedMovie[objKey]; //reset selected movie
                        });
                    }
                    _.assign(self.selectedMovie, segment);
                    if (segment.error !== true) {
                        self.selectedMovies.push(segment);
                    }
                    $('#scrollable-div').animate({ 'scrollTop': $('#scrollable-div')[0].scrollHeight }, 1000);
                    //Reduce space between chat box and chat messages
                    if ( $('#scrollable-div').height() > $('#conversationParent').height() ) {
                    $('.dialog-center').css({ 'top': $('#scrollable-div').height() - $('#conversationParent').height() - 10 + 'px' });
                    return self.selectedMovie;
                    }
                    $('.dialog-center').css({ 'top': '0px' });
                    return self.selectedMovie;
                });
            }
        };
        /**
         * Sets the 'selectedMovie' object back to an empty object.
         *
         */
        self.clearMovieSelection = function () {
            var objKeys = _.keys(self.selectedMovie);
            if (objKeys) {
                objKeys.forEach(function (objKey) {
                    delete self.selectedMovie[objKey]; //reset selected movie
                });
            }
            setState(states.chatting);
        };

        /**
         * Returns the list of currently selected favorites.
         *
         * @public
         * @return an array of movies which have been 'favorited' by the end user in the current session.
         */
        self.getFavorites = function () {
            return self.favorites;
        };

        /**
         * Returns the movie currently selected by the user.
         * @public
         * @return {object} The movie selected by the user.
         */
        self.getCurrentMovie = function () {
            return self.selectedMovie;
        };

        /**
         * Adds or removes a movie from the list of favorites. The current array of favorite movies is
         * interrogated for the provided movie (the array is check for a movie with the same name and id).
         * If the movie is not found and the 'add' flag is true the movie is added to the array.
         * If the movie is found and the 'add' flag is false the movie is removed from the list.
         * If the movie is found and the 'add' flag is true or the movie is not found and the 'add' flag
         * is false then no action is taken (as the array is in the correct state).
         *
         * @public
         * @param movie - a object representing a movie which is to be added to the list of favorites.
         *                The object must have a movieId and movieName property.
         * @param add - a flag which determines whether the provided movie is to be added or removed to/from
         *              the list of favorites. A value of true signaling the movie is to be added, false to
         *              remove the movie from the array.
         */
        self.setAsFavorite = function (movie, add) {
            var result = _.find(self.selectedMovies, { 'movieId': movie.movieId, 'movieName': movie.movieName });
            if (result) {
                result.favorite = add;
            }
            if (movie && self.selectedMovie.movieName === movie.movieName && self.selectedMovie.movieId === movie.movieId) {
                self.selectedMovie.favorite = add;
            }
            if (movie && !add) {
                _.remove(self.favorites, { 'movieId': movie.movieId, 'movieName': movie.movieName });
                if (self.favorites.length === 0) {
                    self.showFavorites = false;
                }
                return;
            }
            result = _.find(self.favorites, { 'movieId': movie.movieId, 'movieName': movie.movieName });
            if (!result && add) {
                self.favorites.unshift(_.clone(movie));
            }
        };

        setState(states.intro);
        //gets the conversation array such that it can be tracked for additions
        self.conversation = dialogService.getConversation();
        self.question = null;

        if (!self.placeHolder) {
            //if we haven't received the placeholder, make a call to initChat API to get welcome message
            self.placeHolder = (function () {
                var init = dialogService.initChat();
                return init.then(function (response) {
                    placeholderText = response.welcomeMessage;
                    states.intro.introText = placeholderText.replace(/\n\n/g, ' '); //for placeholder attr use spaces
                    states.intro.placeholder = 'Start typing...';
                    $('#question').removeAttr('disabled');
                    setState(states.intro);
                    $('#question').focus();
                });
            }());
        }

        /**
         * Submits the current question using dialogService
         */
        self.submit = function () {
            var child = null;
            var timeout = null;
            var footer = null;
            if (!self.question || self.question.length === 0) {
                $('#question').focus();
                return;
            }
            if (self.conversation.length > 1 && self.conversation[self.conversation.length - 1].options) {
                self.conversation[self.conversation.length - 1].options = null;
            }
            if (self.selectedMovie) {
                self.selectedMovie.commentary = null;
            }
            $('#question').attr('disabled', '');
            timeout = $timeout(function () {
                    var scrollable = $('#scrollable-div');
                    if (scrollable[0]) {
                        scrollable[0].scrollTop = scrollable[0].scrollHeight;
                    }
                }, 500);

            dialogService.query(self.question, true).then(function (response) {
                $('#question').removeAttr('disabled');
                $('#question').val('');
                if ($.isArray(response)) {
                    response = response[response.length - 1];
                    //If we are displaying movies on a mobile device (less than 750 tall) we do
                    //not want to put focus into the field! (we don't want the keyboard popping up)
                    if (!response.movies || $(window).height() > 750) {
                        $('#question').focus();
                    }
                }
                //This is not a great hack, but the only fix I could find for compensating
                //for the width of the scrollbars. When the scrollbar appears it
                if ($('#scrollable-div').prop('clientHeight') < $('#scrollable-div').prop('scrollHeight')) {
                    child = document.getElementById('resize-footer-col');
                    child.style.display = 'table-cell';
                    footer = document.getElementById('dialog-footer');
                    footer.style.overflowY = 'scroll';
                    if (timeout) {
                        $timeout.cancel(timeout);
                    }
                    timeout = $timeout(function () {
                        var scrollableDiv = $('#scrollable-div');
                        child.style.display = 'none';
                        if (scrollableDiv[0]) {
                            scrollableDiv[0].scrollTop = scrollableDiv[0].scrollHeight;
                        }
                     }, 500);
                }
                else {
                    child = document.getElementById('resize-footer-col');
                    child.style.display = 'table-cell';
                    footer = document.getElementById('dialog-footer');
                    footer.style.overflowY = 'hidden';
                    if (timeout) {
                        $timeout.cancel(timeout);
                    }
                    timeout = $timeout(function () {
                        var scrollableDiv = $('#scrollable-div');
                        child.style.display = 'none';
                        if (scrollableDiv[0]) {
                            scrollableDiv[0].scrollTop = scrollableDiv[0].scrollHeight;
                        }
                    }, 500);
                }
            });
            delete self.question;
        };

        self.toggleFavorite = function (movie) {
            if (movie) {
                if (movie.favorite) {
                    movie.favorite = false;
                    self.setAsFavorite(movie, false);
                }
                else {
                    movie.favorite = true;
                    self.setAsFavorite(movie, true);
                }
            }
        };

        self.toggleFavoritesPanel = function () {
            self.showFavorites = !self.showFavorites;
        };

        self.submitLink = function (textToSubmit) {
            $('#question').val(textToSubmit);
            self.question = textToSubmit;
            self.submit();
        };

        self.switchToChatting = function () {
            $location.path('chatting');
        };

        $scope.$on('$viewContentLoaded', function (next, current) {
            if (placeholderText) {
                $('#question').removeAttr('disabled');
                $('#question').focus();
            }
        });

        //Watch the conversation array.. If a segment is added then update the state
        $scope.$watch(function () {
            return self.conversation;
        }, function () {
            // We have a new response, switch to 'answered' state
            if (!_.isEmpty(self.conversation)) {
                if (self.conversation.length === 1) {
                   states.intro.introText = self.conversation[0].responses;
                    $('body').addClass('dialog-body-running');
                    if (self.state.key !== states.preview.key) {
                        setState(states.chatting);
                    }
                }
            }
        }, true);
    };

    angular.module('dialog.controller', [ 'gettext', 'lodash', 'ngRoute', 'ngSanitize', 'ngAnimate', 'dialog.service' ]).config(
            function ($routeProvider) {
                $routeProvider.when('/', {
                    'templateUrl': 'modules/home.html',
                    'reloadOnSearch': false
                }).when('/chatting', {
                    'templateUrl': 'modules/dialog.html',
                    'reloadOnSearch': false
                });
            }).controller('DialogController', DialogController);
}());
