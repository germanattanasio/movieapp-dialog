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

    angular.module('dialog.rating', [])

    /**
     * @name rating
     * @module dialog/rating
     * @description
     *
     * Provides rating directive relating to the review of movies.
     * The directives are used within the preview panel.
     */
    .directive('rating', function ($parse) {
        /*
         * @name favorite
         *
         * @description
         *
         * A 'rating' element directive which provides a 'star' icon that the
         * end user can see to know a rating of a movie.
         * The directive calls the DialogCtrl module to get the current movie
         * and uses popularity property to display right number of stars for the selected movie.
         */
        return {
            'restrict': 'E',
            'template': '<span></span><span class="movie-review">themoviedb.org</span><p class="star-rating"><span id="rating"></span></p><span class="review-value">{{Math.round((movie.popularity)*10)/10}}/10</span></span>',
            'link': function (scope, element, attr) {
                scope.$watch(function () {
                    return scope.dialogCtrl.selectedMovie;
                }, function () {
                    //var num = parseFloat(scope.$parent.dialogCtrl.selectedMovie.popularity) / 2;
                    var num = parseFloat(scope.dialogCtrl.selectedMovie.popularity);
                    var rval = Math.round(num * 2) / 2;
                    var fullstar = parseInt(rval.toString().split('.')[0]);
                    var halfstar = (rval.toString().split('.')[1] === undefined) ? 0 : 1;
                    var count = 10;
                    var emptystar = count - (fullstar + halfstar);
                    var i = 0, stars = '';
                    var fs = '<img class="star" src=images/Full_star.svg>';
                    var es = '<img class="star" src=images/Empty_star.svg>';
                    var hs = '<img class="star" src=images/Half_star.svg>';
                    scope.Math = Math;
                    for (i = 0;i < fullstar;i++) {
                        stars += fs + ' ';
                    }
                    for (i = 0;i < halfstar;i++) {
                        stars += hs + ' ';
                    }
                    for (i = 0;i < emptystar;i++) {
                        stars += es + ' ';
                    }
                    $('#rating').html(stars);
                }, true);
            }
        };
    });
}());
