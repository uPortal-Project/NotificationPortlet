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
 * This file leverages Modal Bootstrap to display a single notice, like an Term of Service
 */

var upmodal_notice = upmodal_notice || {};

if (!upmodal_notice.init) {

  upmodal_notice.init = true;

  (function() {

    var defaults = {
      selectors: {
        content:         '.np-content',
        title:           '.np-title',
        body:            '.np-body',
        link:            '.np-link',
        actions:         '.np-actions',
        actionTemplate:  '.np-action-template',
        closeButton:     '.np-close'
      },
      readyCallback: function() {}
    };

    // First 'prime-the-pump' with an ActionURL
    function initNotices($, settings, callback) {
      $.ajax({
        type:    'POST',
        url:     settings.invokeNotificationServiceUrl,
        async:   false,
        success: function() {
          fetchNotices($, settings, callback);
        }
      });
    }

    // Then fetch the notifications with a ResourceURL
    function fetchNotices($, settings, callback) {
      $.ajax({
        url:      settings.getNotificationsUrl,
        type:     'POST',
        dataType: 'json',
        success:  function (data) {
          feed = data.feed;
          callback(feed);
        },
        error:    function () {
          container.html(" ").text("AJAX failed. ~ THE END ~");
        }
      });

    }

    upmodal_notice.show = function ($, container, options) {

      var settings = $.extend({}, defaults, options);

      var resetDialog = function() {
        container.find(settings.selectors.closeButton).hide();
        container.find(settings.selectors.actions).find('.np-action').remove();
      }

      var drawActions = function(actionsContainer, alert) {
        var availableActions = alert.availableActions;
        var actionTemplate = actionsContainer.find(settings.selectors.actionTemplate);
        if (availableActions && availableActions.length > 0) {
          // Draw actions if we have them...
          for (var i=0; i < availableActions.length; i++) {
            var action = availableActions[i];
            var actionUrl = settings.invokeActionUrlTemplate
                            .replace('NOTIFICATIONID', alert.id)
                            .replace('ACTIONID', action.id);
            var actionElement = actionTemplate.clone();
            actionElement.removeClass('np-action-template');
            actionElement.toggleClass('hidden');
            actionElement.addClass('np-action');
            actionElement.find('a').attr('href', actionUrl).html(action.label);
            actionElement.appendTo(actionsContainer);
          }
        } else {
          // Or offer a close (x) button if we don't...
          actionsContainer.find(settings.selectors.closeButton).show();
        }
      }

      var drawAlert = function(container, alert) {
        var element = container.find(settings.selectors.content);
        // Insert context
        element.find(settings.selectors.title).html(alert.title);
        if (alert.body) {
          element.find(settings.selectors.body).html(alert.body);
        }
        if (alert.url) {
          var linkText = alert.linkText || alert.url;
          element.find(settings.selectors.link).attr('href', alert.url).html(linkText);
        }
        // Add the actions
        var actionsContainer = element.find(settings.selectors.actions);
        drawActions(actionsContainer, alert);
      }

      var showEachNoticeInTurn = function(feed) {
        // Do we have any notices to show?
        if (feed && feed.length != 0) {
          // Iterate the notices
          for (var i=0; i < feed.length; i++) {
            resetDialog();
            var alert = feed[i];
            drawAlert(container, alert);
          }
          // Invoke the specified callback function, if any
          settings.readyCallback();
        }
      }
      // Invoke notifications
      initNotices($, settings, showEachNoticeInTurn);
    }

    upmodal_notice.pullFeed = function ($, options, callback) {
      var settings = $.extend({}, defaults, options);
      initNotices($, settings, callback);
    }
  })();
}
