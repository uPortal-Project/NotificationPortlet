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

var upnotice = upnotice || {};

/*
 * This file offers a simple, flexible widget for displaying notifications.  It 
 * is possible to use this script with multiple, different markup approaches. 
 */

if (!upnotice.init) {

  upnotice.init = true;

  (function() {

    var defaults = {
      selectors: {
        template:  '.template',
        title:     '.title',
        body:      '.body',
        url:       '.url'
      },
      readyCallback: function() {}
    };

    upnotice.show = function ($, container, options) {

      var settings = $.extend({}, defaults, options);
      var template = container.find(settings.selectors.template);

      var drawNotices = function(feed) {

        // Do we have any notices to show?
        if (feed.length != 0) {

          // Iterate the notices
          for (var i=0; i < feed.length; i++) {
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
              element.find(settings.selectors.url).attr('href', alert.url).html(alert.url);
            }

            element.appendTo(container);
          }

          // Invoke the specified callback function, if any
          settings.readyCallback();

          container.slideDown('slow');

        }

      }

      // First 'prime-the-pump' with an ActionURL
      function initNotices() {
        $.ajax({
          type: 'POST',
          url: settings.invokeNotificationServiceUrl,
          async: false,
          success: fetchNotices
        });
      }

      // Then fetch the notifications with a ResourceURL
      function fetchNotices() {
        var feed = [];
        $.ajax({
          url      : settings.getNotificationsUrl,
          type     : 'POST',
          dataType : 'json',
          success: function (data) {
            drawNotices(data.feed);
          },
          error: function () {
            container.html(" ").text("AJAX failed. ~ THE END ~");
          }
        });

      }

      // Invoke notifications
      initNotices();
    }

  })();

}
