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
        form:            '.np-action-form',
        body:            '.np-body',
        link:            '.np-link',
        actions:         '.np-actions',
        actionTemplate:  '.np-action-template',
        closeButton:     '.np-close'
      }
    };

    // First 'prime-the-pump' with an ActionURL
    var initNotices = function($, settings, callback) {
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
    var fetchNotices = function($, settings, callback) {
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

    upmodal_notice.launch = function ($, container, options) {

      var settings = $.extend({}, defaults, options);

      var handleAction = function() {
        var actionButton = $(this);
        var formElement = container.find(settings.selectors.form);
        $.post(actionButton.attr('data-url'), formElement.serialize(), function(data, textStatus, jqXHR) {
          if (console && console.log) {
            console.log('handleAction received status: ' + textStatus);
          }
        });
        // Any action closes the modal window
        container.modal('hide');
      }

      var resetDialog = function() {
        container.off('hidden.bs.modal'); // Clear previous event handler(s)
        container.find(settings.selectors.closeButton).hide();
        container.find(settings.selectors.actions).find('.np-action').remove();
      }

      var drawActions = function(alert) {
        var actionsContainer = container.find(settings.selectors.actions);
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
            if (i == 0) {
              // Only the first button is btn-primary
              actionElement.find('a').addClass('btn-primary');
            }
            actionElement.find('a').attr('data-url', actionUrl).html(action.label).click(handleAction);
            actionElement.appendTo(actionsContainer);
          }
        } else {
          // Or offer a close (x) button if we don't...
          container.find(settings.selectors.closeButton).show();
        }
      }

      var drawAlert = function(alert) {
        var content = container.find(settings.selectors.content);
        // Insert context
        content.find(settings.selectors.title).html(alert.title);
        if (alert.body) {
          content.find(settings.selectors.body).html(alert.body);
        }
        if (alert.url) {
          var linkText = alert.linkText || alert.url;
          content.find(settings.selectors.link).attr('href', alert.url).html(linkText);
        }
        // Add the actions
        drawActions(alert);
      }

      var showNextNotice = function(feed, index) {
        var alert = feed[index];
        resetDialog();
        drawAlert(alert);
        if (feed.length > index + 1) {
          // There are more notices after this one...
          container.on('hidden.bs.modal', function() {
            showNextNotice(feed, index + 1);
          });
        }
        container.modal("show");
      }

      var showEachNoticeInTurn = function(feed) {
        // Do we have any notices to show?
        if (feed && feed.length != 0) {
          showNextNotice(feed, 0);
        }
      }

      // Invoke notifications
      initNotices($, settings, showEachNoticeInTurn);
    }

  })();
}
