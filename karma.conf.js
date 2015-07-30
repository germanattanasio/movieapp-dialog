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
  module.exports = function(config) {
    config.set({

      basePath: '',

      files: [
        '.tmp/bower_components/angular/angular.js',
        '.tmp/bower_components/angular-gettext/dist/angular-gettext.js',
        '.tmp/bower_components/angular-loader/angular-loader.js',
        '.tmp/bower_components/angular-mocks/angular-mocks.js',
        '.tmp/bower_components/angular-route/angular-route.js',
        '.tmp/bower_components/angular-sanitize/angular-sanitize.js',
        '.tmp/bower_components/lodash/dist/lodash.js',
        'app/**/*.js',
        'app/**/*.html'
      ],

      exclude: [],

      frameworks: ['mocha', 'chai', 'chai-as-promised', 'sinon-chai'],

      browsers: ['PhantomJS'],

      plugins: [
        'karma-junit-reporter',
        'karma-mocha',
        'karma-chai',
        'karma-chai-plugins',
        'karma-phantomjs-launcher',
        'karma-ng-html2js-preprocessor'
      ],

      preprocessors: {
        '**/*.html': ['ng-html2js']
      },

      ngHtml2JsPreprocessor: {
        stripPrefix: 'app/'
      },

      reporters: ['progress', 'junit'],

      junitReporter: {
        outputFile: 'test_out/unit.xml',
        suite: 'unit'
      }

    });
  };
}());
