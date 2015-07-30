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

    angular.module('dialog.favorite', [])

    /**
     * @name favorite
     * @module dialog/favorite
     * @description
     *
     * Provides several directives relating to the favoriting of movies.
     * The directives are used within the preview panel, within the header
     * and also within the favorites sidebar/fullscreen UI.
     */
    .directive('favorite', function ($parse) {
        /*
         * @name favorite
         *
         * @description
         *
         * A 'favorite' element directive which provides a 'heart' button that the
         * end user can use to favorite (like) or unfavorite (unlike) a movie.
         * The directive simply calls the DialogCtrl module to toggle the 'favorite'
         * property on the selected movie.
         */
        return {
            'restrict': 'E',
            'template': '<span ng-click="toggle()" class="{{favoriteClass}}"></span>',
            'link': function (scope, element, attr) {
                scope.toggle = function () {
                    var movie = $parse(attr.content)(scope);
                    scope.dialogCtrl.toggleFavorite(movie);
                };

                scope.$watch(function () {
                    return attr.content;
                }, function () {
                    //movie changed
                    var content = $parse(attr.content)(scope);
                    if (!content || !content.favorite) {
                        scope.favoriteClass = 'dialog-no-favorite';
                    }
                    else {
                        scope.favoriteClass = 'dialog-favorite';
                    }
                });

                scope.$watch(function () {
                    return scope.dialogCtrl.getFavorites().length;
                }, function () {
                    //movie changed
                    var content = $parse(attr.content)(scope);
                    if (!content || !content.favorite) {
                        scope.favoriteClass = 'dialog-no-favorite';
                    }
                    else {
                        scope.favoriteClass = 'dialog-favorite';
                    }
                });
            }
        };
    })
    .directive('favoriteIndicator', function () {
        /*
         * @name favoriteIndicator
         *
         * @description
         *
         * A simple directive which is displayed in the application header.
         * The directive monitors the controller's array of favorites. If one or
         * more favorites exist in the array then the directive changes the class
         * of the DOM element to indicate one or more favorites are selected.
         */
        return {
            'restrict': 'E',
            'link': function (scope) {
                scope.favoriteIndicatorClass = 'dialog-no-favorite-left';
                scope.$watch(function () {
                    return scope.dialogCtrl.getFavorites().length;
                }, function () {
                    var favorites = scope.dialogCtrl.getFavorites();
                    if (!favorites || favorites.length === 0) {
                        scope.favoriteIndicatorClass = 'dialog-no-favorite-left';
                    }
                    else {
                        scope.favoriteIndicatorClass = 'dialog-favorite-left';
                    }
                });
            }
        };
    })
    .directive('favoriteOptions', function () {
        /*
         * @name favoriteOptions
         *
         * @description
         *
         * Used only at small resolutions. This allows users to unfavorite a movie.
         */
        return {
            'restrict': 'A',
            'link': function (scope) {
                scope.showPreview = function (movie) {
                    scope.dialogCtrl.toggleFavoritesPanel();
                    scope.dialogCtrl.selectMovie(movie);
                };
            }
        };
    });
}());
