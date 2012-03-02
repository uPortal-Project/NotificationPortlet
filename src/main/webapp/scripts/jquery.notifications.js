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

//
//  Notifications portlet jQuery plugin
//  developed on behalf of the University 
//  of Manchester
//
//  Author: Jacob Lichner
//  Email: jlichner@unicon.net
//
(function ($) {

  // Set underscore's templating syntax
  _.templateSettings = {
    interpolate : /\{\{(.+?)\}\}/g, // {{ variable }}
    evaluate    : /\{%(.+?)%\}/g    // {% expression %}
  };
  
  $.fn.notifications = function (opts) {

    // Cache existing DOM elements  
    var portlet         = this.find(".notification-portlet-wrapper"),
        outerContainer  = this.selector,
        links           = this.find(".notification-portlet a"),
        errorContainer  = this.find(".notification-error-container"),
        loading         = this.find(".notification-loading"),
        notifications   = this.find(".notification-container"),
        detailView      = this.find(".notification-detail-wrapper"),
        detailContainer = this.find(".notification-detail-container"),
        backButton      = this.find(".notification-back-button"),
        refreshButton   = this.find(".notification-refresh a"),
        filterOptions   = this.find(".notification-options"),
        todayFilter     = filterOptions.find(".today"),
        allFilter       = filterOptions.find(".all");
        
    // Notification gets cached in the AJAX callback
    // but is created here for scope
    var notification;
    
    // Store the filter state (notifications that are 
    // currently being displayed), defaults to today
    var filterState = {"days": 1};
    
    function getNotifications(params, doRefresh) {
            
      // Looading div is displayed by default
      // and is then hidden after the AJAX call
      loading.ajaxStop(function () {
        $(this).hide();
        portlet.fadeIn("fast");
        filterOptions.fadeIn("fast");
      });
      
      var data = $.extend({}, params, {refresh: doRefresh || 'false' });
      
      $.ajax({
        url      : opts.url,
        type     : 'POST',
        dataType : 'json',
        data     : data,
        
        beforeSend: function () {
          
          // Hide detail view
          if ( detailView.is(":visible") ) {
            detailView.hide();
            notifications.show();
          }

          // Show loading
          portlet.hide();
          loading.show();

          // Unbind click events
          links.unbind("click");
          backButton.unbind("click");
          filterOptions.find("a").unbind("click");

          // Clear out notifications and errors
          notifications.html(" ");
          errorContainer.html(" ");
        },
        
        success: function (data) {
          var data = data.notificationResponse;
          
          // Build notifications
          buildNotifications(data);

          // Once notifications have been injected into the DOM
          // we cache the notication element...
          notification = $(outerContainer + " .notifications a");

          // ...and bind our events
          bindEvent.accordion(data);
          bindEvent.viewDetail();
          bindEvent.goBack();
          bindEvent.refresh();
          bindEvent.filterOptions(data);

          // Errors
          errorHandling(data);
        },
        
        error: function () {
          $(this).html(" ").text("AJAX failed. ~ THE END ~");
        }
      });
    }

    // Build notifications using underscore.js
    // template method
    function buildNotifications(data) {

      // HTML string compiled with underscore.js
      var html = '\
        {% if (categories.length < 1 ) { %} \
          <div class="no-notifications-container"> \
            <h3>You have 0 notifications.</h3> \
          </div> \
        {% } else { %} \
          {% var accordion = categories.length > 1; %} \
          {% _.each(categories, function(category) { %} \
            <div class="notification-trigger"> \
              <h3 class="portlet-section-header trigger-symbol" role="header"> \
                {{ category.title }} \
                {% if (accordion) { %} \
                  ({{ category.entries.length }}) \
                {% } %} \
              </h3> \
            </div> \
            {% if (category.entries.length < 1) { %} \
              <!-- no notifications --> \
            {% } else { %} \
              <div class="notification-content" style="display: none;"> \
                <ul class="notifications"> \
                  {% _.each(category.entries, function(entry) { %} \
                    <li> \
                      {% if (!accordion) { %} \
                        &raquo; \
                      {% } %} \
                      <a href="{{ entry.link }}" \
                         data-body="{{ entry.body }}" \
                         data-title="{{ entry.title }}" \
                         data-source="{{ entry.source }}"> {{ entry.title }}</a> \
                      {% if ( entry.dueDate ) { \
                           var date  = new Date(entry.dueDate.time), \
                               month = date.getMonth() + 1, \
                               day   = date.getDay(), \
                               year  = date.getFullYear(), \
                               overDue = (date < new Date() ? " overdue" : ""); %} \
                        <span class="notification-due-date{{ overDue }}"> \
                          Due {{ month }}/{{ day }}/{{ year }} \
                        </span> \
                      {% } %} \
                    </li> \
                  {% }); %} \
                </ul> \
              </div> \
            {% } %} \
          {% }); %} \
        {% } %} \
      ';
      var compiled = _.template(html, data);

      // Inject compiled markup into notifications container div
      notifications.html(" ").prepend(compiled);
    }

    // Bind events object helps keep events together 
    var bindEvent = {

      // Accordion via plugin
      accordion: function (data) {
        if ( data.categories.length === 1 ) {
          portlet.removeClass("accordion");
          notifications.children().show();
        } else {
          notifications.accordion();
          portlet.addClass("accordion");
        }
      },

      // View detail page
      viewDetail: function () {
        notification.click(function () {

          // Notification detail is retrieved from 'data-' 
          // attributes and stored in a notification object
          var notification = {
            body   : $(this).data("body"),
            title  : $(this).data("title"),
            source : $(this).data("source"),
            link   : $(this).attr("href")
          }

          var html = '\
            <h3><a href="{{ link }}">{{ title }}</a></h3> \
            <p>{{ body }}</p> \
            <p class="notification-source"> \
              Source: <a href="{{ link }}">{{ source }}</a> \
            </p> \
          ';
          var compiled = _.template(html, notification);
          
          $.each([notifications, errorContainer], function () {
            $(this).hide(
              "slide", 200, function () {
                detailContainer.html(" ").append(compiled);
                detailView.show();
              });
          });

          return false;
        });
      },

      // Go back to all notifications
      goBack: function () {
        backButton.click(function () {
          detailView.hide(
            "slide", {direction: "right"}, 200, function () {
              notifications.show();
              errorContainer.show();
            }
          )

          return false;
        })
        .hover(
          function () { $(this).addClass('hover'); },
          function () { $(this).removeClass('hover') }
        );
      },
      
      refresh: function () {
        refreshButton.click(function () {
          getNotifications(filterState, 'true');
          return false;
        });
      },
      
      filterOptions: function (data) {
        todayFilter.click(function () {
          filter($(this), {"days":1});
          return false;
        });
        
        allFilter.click(function () {
          filter($(this));
          return false;
        });
      }
    }
    
    // Filter notifications by passing params
    // via ajax ie {"days":1} is today, also
    // stores and returns filterState
    function filter(link, params) {
      filterState = params || {};
      if ( link.hasClass("active") ) {
        return false;
      } else {
        getNotifications(filterState);
        filterOptions.find("a").removeClass("active");
        link.addClass("active");
      }
      return filterState;
    }
    
    // Errors (broken feeds)
    function errorHandling(data) {      
      if ( data.errors ) {
        var html = '\
          {% _.each(errors, function(error) { %} \
            <div class="portlet-msg-error" errorkey="{{ error.key }}"> \
              {{ error.source }}: {{ error.error }} \
              <a href="#" class="remove" title="Hide"></a> \
            </div> \
          {% }); %} \
        ';  
        var compile = _.template(html, data);
        
        errorContainer.show().append(compile);
        errorContainer.find(".remove").click(function () {
         var thisErrorContainer = $(this).parent();
         thisErrorContainer.fadeOut("fast", function () {
            var settings = [];
            $.ajax({
              url: (opts.hideErrorUrl).replace("ERRORKEY", thisErrorContainer.attr("errorkey")),
              type: 'POST', 
              success: function() { return false; }
            });
          });
          return false;
        }); 
      }
    }
    
    // Load notifications
    getNotifications(filterState);
  }
  
})(jQuery);
