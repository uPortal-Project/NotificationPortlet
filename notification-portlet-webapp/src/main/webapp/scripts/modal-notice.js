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

(function() {

    if (!upmodal_notice.init) {
        upmodal_notice.init = true;

        var defaults = {
            selectors: {
                content: '.np-content',
                title: '.np-title',
                form: '.np-action-form',
                body: '.np-body',
                link: '.np-link',
                actions: '.np-actions',
                actionTemplate: '.np-action-template',
                closeButton: '.np-close'
            }
        };

        upmodal_notice.launch = function($, container, options) {
            var settings = $.extend({}, defaults, options);

            // First 'prime-the-pump' with an ActionURL
            var initNotices = function(settings, callback) {
                $.ajax({
                    type: 'POST',
                    url: settings.invokeNotificationServiceUrl,
                    async: false,
                    success: function() {
                        fetchNotices(settings, callback);
                    },
                    error: console.error
                });
            }

            // Then fetch the notifications with a ResourceURL
            var fetchNotices = function(settings, callback) {
                $.ajax({
                    url: settings.getNotificationsUrl,
                    dataType: 'json',
                    success: function(data) {
                        var feed = data.feed;
                        callback(feed);
                    },
                    error: console.error
                });
            }

            var handleAction = function() {
                var actionButton = $(this);
                var formElement = container.find(settings.selectors.form);
                if (actionButton.attr('data-ajax') === 'false') {
                    // This alert is an exception;  submit the form directly.
                    formElement.attr('action', actionButton.attr('data-url')).submit();
                } else {
                    // Submit via AJAX (default)
                    $.post(actionButton.attr('data-url'), formElement.serialize(), function(data, textStatus, jqXHR) {
                        console.log(data, textStatus, jqXHR);
                    });
                    // Any action closes the modal window
                    container.modal('hide');
                }
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
                    availableActions.forEach(function(action, index) {
                        var actionUrl = settings
                            .invokeActionUrlTemplate
                            .replace('NOTIFICATIONID', alert.id)
                            .replace('ACTIONID', action.id);

                        var actionElement = actionTemplate.clone();
                        actionElement.removeClass('np-action-template');
                        actionElement.toggleClass('hidden');
                        actionElement.addClass('np-action');

                        if (index === 0) {
                            // Only the first button is btn-primary
                            actionElement.find('a').addClass('btn-primary');
                        }

                        actionElement.find('a').attr('data-url', actionUrl).html(action.label)
                                .click(handleAction);

                        // By default, actions on this dialog submit to the server via
                        // AJAX;  but you can turn that behavior off for a single notice.
                        if (alert.attributes['org.jasig.portlet.notice.action.AJAX']
                                && alert.attributes['org.jasig.portlet.notice.action.AJAX'][0] === 'false') {
                            actionElement.find('a').attr('data-ajax', 'false');
                        }

                        actionElement.appendTo(actionsContainer);
                    })
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
                else {
                    content.find(settings.selectors.link).remove();
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
                if (feed && feed.length !== 0) {
                    showNextNotice(feed, 0);
                }
            }

            // Invoke notifications
            initNotices(settings, showEachNoticeInTurn);
        }

    }

})();
