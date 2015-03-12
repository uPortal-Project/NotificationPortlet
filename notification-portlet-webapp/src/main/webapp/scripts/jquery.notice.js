/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * This file offers a simple, flexible widget for displaying notifications.  It
 * is possible to use this script with multiple, different markup approaches.
 */

var upnotice = upnotice || {};

if (!upnotice.init) {

  upnotice.init = true;

  (function() {

    var defaults = {
      selectors: {
        template:        '.template',
        title:           '.title',
        body:            '.body',
        link:            '.link',
        actions:         '.notification-actions',
        actionTemplate:  '.action-template',
        summaryTemplate: '.summary-template'
      },
      readyCallback: function() {}
    };

    // First 'prime-the-pump' with an ActionURL
    function initNotices($, settings, callback) {
      $.ajax({
        type: 'POST',
        url: settings.invokeNotificationServiceUrl,
        async: false,
        success: function() {
          fetchNotices($, settings, callback);
        }
      });
    }

    // Then fetch the notifications with a ResourceURL
    function fetchNotices($, settings, callback) {
      $.ajax({
        url      : settings.getNotificationsUrl,
        type     : 'POST',
        dataType : 'json',
        success: function (data) {
          feed = data.feed;
          callback(feed);
        },
        error: function () {
          container.html(" ").text("AJAX failed. ~ THE END ~");
        }
      });

    }

    upnotice.show = function ($, container, options) {

      var settings = $.extend({}, defaults, options);
      var template = container.find(settings.selectors.template);
      var summaryTemplate = container.find(settings.selectors.summaryTemplate);

      var drawActions = function(actionsContainer, alert) {

          var availableActions = alert.availableActions;
          var actionTemplate = actionsContainer.find(settings.selectors.actionTemplate);

          for (var i=0; i < availableActions.length; i++) {
              var action = availableActions[i];

              var actionUrl = settings.invokeActionUrlTemplate
                      .replace('NOTIFICATIONID', alert.id)
                      .replace('ACTIONID', action.id);

              var actionElement = actionTemplate.clone();
              actionElement.removeClass('action-template');
              actionElement.toggleClass('hidden');
              actionElement.find('a').attr('href', actionUrl).html(action.label + " ");
              actionElement.appendTo(actionsContainer);
          }

          actionsContainer.toggleClass('hidden');

      }

      var drawNotices = function(feed) {

        // Do we have any notices to show?
        if (feed && feed.length != 0) {

          // Iterate the notices
          var len = feed.length;
          if (settings.numberToDisplay != undefined && settings.numberToDisplay < feed.length) {
              len = settings.numberToDisplay;
          }
          for (var i=0; i < len; i++) {
            var alert = feed[i];

            // Prepare an element
            var element = template.clone();
            element.removeClass('template');
            element.toggleClass('hidden');

            // Insert context
            element.find(settings.selectors.title).html(alert.title);
            if (alert.body) {
              element.find(settings.selectors.body).html(alert.body);
            }
            if (alert.url) {
              var linkText = alert.linkText || alert.url;
              element.find(settings.selectors.link).attr('href', alert.url).html(linkText);
            }

            // Are actions available?
            if (alert.availableActions && alert.availableActions.length != 0) {
                var actionsContainer = element.find(settings.selectors.actions);
                if (actionsContainer) {
                    drawActions(actionsContainer, alert);
                }
            }

            element.appendTo(template.parent());
          }
          
          // is there a summary section?
          if(summaryTemplate) {
              var showing = summaryTemplate.find('.showing');
              showing.text(len);
              
              var total = summaryTemplate.find('.total');
              total.text(feed.length);
          }

          // Invoke the specified callback function, if any
          settings.readyCallback();

          container.slideDown('slow');

        }

      }

      // Invoke notifications
      initNotices($, settings, drawNotices);

    }

    upnotice.pullFeed = function ($, options, callback) {
        var settings = $.extend({}, defaults, options);
        initNotices($, settings, callback);
    }

  })();

}
