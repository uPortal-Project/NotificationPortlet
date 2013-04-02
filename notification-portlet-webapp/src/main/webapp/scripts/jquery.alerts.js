/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var upalert = upalert || {};

if (!upalert.init) {

  upalert.init = true;

  (function() {

    var defaults = {
      selectors: {
        template:  '.template',
        pager:     '.alerts-pager',
        previous:  '.alerts-previous',
        next:      '.alerts-next',
        title:     '.title',
        body:      '.body',
        url:       '.url'
      }
    };

    upalert.emergencyAlerts = function ($, container, options) {

      var settings = $.extend({}, defaults, options);
      var template = container.find(settings.selectors.template);
      var intervalId = -1;

      var advance = function() {
        var outgoingAlert = container.find('.view-alert:visible');
        var incomingAlert = outgoingAlert.next();
        if (incomingAlert.size() == 0) {
          // Cycle to the beginning...
          incomingAlert = container.find('.view-alert:first');
        }
        outgoingAlert.toggle('slide', { direction: 'left' });
        incomingAlert.toggle('slide', { direction: 'right' });
      };

      var recede = function() {
        var outgoingAlert = container.find('.view-alert:visible');
        var incomingAlert = outgoingAlert.prev();
        outgoingAlert.toggle('slide', { direction: 'right' });
        incomingAlert.toggle('slide', { direction: 'left' });
      };

      var drawAlerts = function(feed) {

        // Do we have any alerts to show?
        if (feed.length != 0) {

          // Iterate the alerts
          for (var i=0; i < feed.length; i++) {
            var alert = feed[i];
            var element = template.clone();
            
            // Make the first one visible
            if (i == 0) {
              element.toggleClass('hidden');  
            }

            // Paging?
            if (feed.length > 1) {
              element.find(settings.selectors.pager).toggleClass('hidden');
              // All but the first should enable the previous link
              if (i != 0) {
                element.find(settings.selectors.previous).toggleClass('disabled');
              }
              // All but the last should enable the next link
              if (i != feed.length -1) {
               element.find(settings.selectors.next).toggleClass('disabled');
              }
            }

            // Content
            element.find(settings.selectors.title).html(alert.title);
            if (alert.body) {
              element.find(settings.selectors.body).html(alert.body);
            }
            if (alert.url) {
              element.find(settings.selectors.url).attr('href', alert.url).html(alert.url);
            }

            element.appendTo(container);
          }

          // Scrolling bahavior
          container.find('.alerts-next:not(.disabled)').click(function() {
            advance();
            window.clearInterval(intervalId);
          });
          container.find('.alerts-previous:not(.disabled)').click(function() {
            recede();
            window.clearInterval(intervalId);
          });
          if (settings.autoAdvance && feed.length > 1) {
            intervalId = window.setInterval(advance, 10000);
          }
        
          container.slideDown('slow');
        
        }

      }

      function fetchAlerts() {
        var feed = [];

        // Now fetch the notifications with a ResourceURL
        $.ajax({
          url      : settings.getNotificationsUrl,
          type     : 'POST',
          dataType : 'json',
          success: function (data) {
        	drawAlerts(data.feed);
          },
          error: function () {
            container.html(" ").text("AJAX failed. ~ THE END ~");
          }
        });

      }
    
      function initAlerts() {
        // First 'prime-the-pump' with an ActionURL
        $.ajax({
          type: 'POST',
          url: settings.invokeNotificationServiceUrl,
          async: false,
          success: fetchAlerts
        });
      }

      // Invoke notifications
      initAlerts();
    }

  })();
  
}
