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

    angular.module('dialog.compile', [])

    /**
     * @name compile
     * @module modules/compile
     *
     * @description
     *
     * Compiles a string into a HTML DOM Element
     *
     * @param {object}
     *            content - html content in text format
     */
    .directive('compile', function ($parse, $compile) {
        return {
            'restrict': 'A',
            'link': function (scope, element, attr) {
                var html = $parse(attr.compile)(scope); //Get the html content
                var newElement = $compile(html)(scope); //create DOM Element
                element.append(newElement); //append to DOM
            }
        };
    });
}());
