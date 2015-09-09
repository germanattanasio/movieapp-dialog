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

    angular.module('dialog.preview', [])

    /**
     * @name preview
     * @module module/preview
     * @description
     *
     * Renders the preview panel within the UI. When a movie is clicked within the list
     * of movie results the controller's "selectedMovie" property is updated. Once selectedMovie
     * contains a movie this directive is invoked. This directive is responsible for rendering
     * the entire preview pane (movie, name, description etc.).
     *
     * @param {object}
     *            content - a reference to movie object
     */
    .directive('preview', function ($parse, $sce) {
        return {
            'template': '<div><span class="dialog-drawer-toggle"></span>' +
                        '<favorite class="dialog-favorite-sm" content="{{movie}}"></favorite>' +
                        '<div class="dialog-preview-scroll">' +
                        '<iframe id="trailerIFrame" class="dialog-trailer" src="{{trustedUrl}}" allowfullscreen frameborder="0"></iframe>' +
                        '<h3 id="noTrailerText" class="dialog-trailer-missing dialog-trailer-hidden">No Preview Available</h3>' +
                        '<div class="dialog-movie-info-spacing"><div class="dialog-movie-name-rating-spacing"></div><span class="dialog-movie-name-rating"><h3 class="dialog-movie-name">{{movie.movieName}}</h3>' +
                        '<span class="dialog-rating-label"><img src="{{certification}}"></span></span>' +
                        '<favorite class="dialog-favorite-lg" content="{{movie}}"></favorite>' +
                        '<h5 class="dialog-release-label" ng-hide="hideReleaseDate">Release date:<span class="dialog-release-date"> {{movie.localizedDate}}</span></h5>' +
                        '<showtoggle></showtoggle><div class="dialog-rating-spacing"></div><rating></rating></div></div>',
            'restrict': 'E',
            'link': function (scope, element, attr) {
                var closeButton = null;
                var date = null;
                var monthNames = [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ];
                var resizeContents = function () {
                    var docHeight = $(window).height();
                    var headerHeight = $('#dialog-header').outerHeight(true);
                    var previewParentHeight = $('#preview-parent')[0].scrollHeight;
                    var innerHeaderHeight = $('.dialog-drawer-toggle').outerHeight(true);
                    var previewAvailHeight = 0;
                    if (previewParentHeight === docHeight) {
                        //mobile
                        previewAvailHeight = docHeight - (innerHeaderHeight + 5);
                    }
                    else {
                        //desktop
                        previewAvailHeight = docHeight - (headerHeight + innerHeaderHeight);
                    }
                    if (docHeight < (headerHeight + previewParentHeight)) {
                        //we need to scroll the preview panel
                        $('.dialog-preview-scroll').height(previewAvailHeight);
                    }
                };
                scope.hideReleaseDate = true;
                scope.hideCertification = true;
                scope.playerClass = '';
                scope.isFavorite = false;

                closeButton = $('.dialog-drawer-toggle');
                closeButton.bind('touchstart click', function (e) {
                    scope.$apply(scope.dialogCtrl.clearMovieSelection());
                    $(window).off('resize', resizeContents);
                    e.preventDefault();
                    e.stopPropagation();
                });
                $(window).resize(resizeContents);
                //<iframe width="560" height="315" src="https://www.youtube.com/embed/vCqiNF94yDw?controls=0&amp;showinfo=0" frameborder="0" allowfullscreen></iframe>
                scope.$watch(function () {
                    return scope.dialogCtrl.getCurrentMovie();
                }, function () {
                    var url = null;
                    var movie = $parse(attr.content)(scope);
                    var iframe = $('#trailerIFrame');
                    var div = $('#noTrailerText');
                    //_.assign(scope.movie, movie);
                    scope.movie = movie;
                    if (!movie.trailerUrl && !movie.movieName && !movie.overview) {
                        return;
                    }
                    if (movie.trailerUrl) {
                        url = $sce.trustAsResourceUrl(movie.trailerUrl);
                        scope.trustedUrl = url;
                        iframe.removeClass('dialog-trailer-hidden');
                        div.addClass('dialog-trailer-hidden');
                    }
                    else {
                        scope.trustedUrl = null;
                        iframe.addClass('dialog-trailer-hidden');
                        div.removeClass('dialog-trailer-hidden');
                    }
                    if (movie.releaseDate) {
                        date = new Date(movie.releaseDate);
                        movie.localizedDate = monthNames[date.getMonth()] + ' ' + date.getDate() + ', ' + date.getFullYear();
                        scope.hideReleaseDate = false;
                    }
                    else {
                        scope.hideReleaseDate = true;
                    }
                    if (!movie.certification || movie.certification.length === 0) {
                        movie.certification = 'NR';
                        scope.certification = 'images/NR.svg';
                    }
                    else {
                        if (movie.certification === 'R') {
                            scope.certification = 'images/R.svg';
                        }
                        else if (movie.certification === 'G') {
                            scope.certification = 'images/G.svg';
                        }
                        else if (movie.certification === 'PG') {
                            scope.certification = 'images/PG.svg';
                        }
                        else if (movie.certification === 'PG-13') {
                            scope.certification = 'images/PG-13.svg';
                        }
                    }
                    resizeContents();
                }, true);
            }
        };
    });
}());
