/**
 *
 * IBM Confidential
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2001, 2015
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 *
 */
(function () {
  'use strict';

  angular.module('dialog.parser', ['lodash'])

    /**
     * @name DialogParser
     * @module dialog/parser
     * @description
     */
    .factory('dialogParser', function () {
        var mctPattern = new RegExp(/(<mct:[^]*?>)/);

        var isMctInPayload = function (response) {
            return mctPattern.test(response);
        };

        var parseMctLinkTag = function (text) {
            var watsonResponse = null;
            var mctLinks = [];
            var joinWithBrs = false;
            if (mctPattern.test(text)) {
                watsonResponse = text.match(/^(.*?)(<.*?>)*?<mct:link>/)[1]; //set Watson response to be everything up to first mct tag
                text = text.replace(/^(.*?)(<.*?>)/g, '$2').trim();
                mctLinks = text.match(/<mct\:link>([^]*?)<\/mct\:link>/g).map(function (linkContent) {
                    var html = ['<a class="dialog-mct-option" ng-click="dialogCtrl.submitLink(\''];
                    var input = linkContent.match(/<mct\:input>([^]*?)<\/mct\:input>/)[1]; //get the contents of mct:input
                    html.push(input.replace(/<[^>]*>/g, '').replace('"', '\\"').replace('\'', '\\\''));//Set the contents of mct:input as the value we want to submit (escape ' and ")
                    html.push('\')">');
                    linkContent = linkContent.replace(/<mct\:input>([^]*?)<\/mct\:input>/, input); //get rid of mct:input
                    input = linkContent.replace(/<mct\:link>(.*)<\/mct\:link>/, '$1');//append the contents of mct:link to the html link body
                    if (!joinWithBrs) {
                        //should look for a better solution here.. Here it is for now..
                        //If the input contains a <BR> we take it to mean that the items should be separated by BRs
                        //So when we join the items, we add <br> tags
                        joinWithBrs = new RegExp(/<br>/i).test(linkContent);
                    }
                    input = input.replace(/<br>/, '');
                    html.push(input);
                    html.push('</a>');
                    return html.join('');
                });
            }
            return { 'htmlOptions': mctLinks.join(joinWithBrs ? '<br>' : ''), 'watsonResponse': watsonResponse, 'question': null };
      };

      var replaceLinebreaks = function (text) {
          return text.replace(/<br\/>/g, '');
      };

      /**
       * Parses a response received from {@link DialogService#query}.
       *
       * @param {object} response - A response object received from {@link DialogService#query}.
       * @return {string} Markup ready to be rendered in the view.
       */
      var parse = function (response) {
        var text = response;
        var parsed = [text];
        if (!isMctInPayload(text)) {
            parsed = parsed.map(replaceLinebreaks);
        }
        else {
            //If we have mct tags then we want to keep formatting
            parsed = parsed.map(parseMctLinkTag);
        }
        return parsed[0];
      };

      return {
        'parse': parse,
        'isMctInPayload': isMctInPayload
      };
    });
}());
