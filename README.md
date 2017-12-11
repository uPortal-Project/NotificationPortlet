# Apereo Notification Portlet

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet.notification/notification-portlet-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet.notification/notification-portlet-parent)
[![Linux Build Status](https://travis-ci.org/Jasig/NotificationPortlet.svg?branch=master)](https://travis-ci.org/Jasig/NotificationPortlet)
[![Windows Build status](https://ci.appveyor.com/api/projects/status/we2hk9cv24f47oao/branch/master?svg=true)](https://ci.appveyor.com/project/ChristianMurphy/notificationportlet/branch/master)

This is a [Sponsored Portlet][] in the uPortal project.

## Configuration

See also the [legacy documentation in the external wiki][].

### Java Properties

Some configuration settings for the Notification portlet are managed in Java properties files that
are loaded by a Spring `PropertySourcesPlaceholderConfigurer`. (Other settings are data, managed in
the "portlet publication record" a.k.a. `portlet-definition.xml` file; these are covered elsewhere
in this doc.)

The properties files that are sourced by Spring are:

* `classpath:datasource.properties`
* `classpath:configuration.properties`
* `file:${portal.home}/global.properties`
* `file:${portal.home}/notification.properties`

For a definitive, comprehensive list of these settings you must look inside `datasource.properties`
and `configuration.properties`. (This `README` may be incomplete and/or out of date.)

#### The `portal.home` Directory

uPortal version 5 uses a directory called `portal.home` for properties files that live outside of
-- and have the ability to _override_ properties files within-- the webapp in Tommcat. Please
review the [README file for uPortal-Start][] for more information on this sytem.

The Notification portlet sources the shared `global.properties` file, as well as it's own (private)
file called `notification.properties` in the `portal.home` directory.

#### Using Encrypted Property Values

Within the properties files that are sourced by Spring, you may optionally provide sensitive
configuration items -- such as database passwords -- in encrypted format. Use the
[Jasypt CLI Tools][] to encrypt the sensitive value, then include it in a `.properties` file
like this:

```
hibernate.connection.password=ENC(9ffpQXJi/EPih9o+Xshm5g==)
```

Specify the encryption key using the `UP_JASYPT_KEY` environment variable.

### Publication Data

Besides Java properties, some configuration settings are managed as data in the _Portlet Publication
Record_ (the `portlet-definition.xml` file in uPortal). These settings are defined on a per-
publication basis, so you can have several publications of the same portlet with each of them
configured differently.

#### Filtering

You can filter the notices that come from data sources in the publication record. Use the following
portlet preferences to _exclude_ some notices from appearing in the display:

<table>
  <tr>
    <th>Portlet Preference</th>
    <th>Possible Value(s)</th>
  </tr>
  <tr>
    <td>FilteringNotificationServiceDecorator.minPriority</td>
    <td>Number between 1 and 5</td>
  </tr>
  <tr>
    <td>FilteringNotificationServiceDecorator.maxPriority</td>
    <td>Number between 1 and 5</td>
  </tr>
  <tr>
    <td>FilteringNotificationServiceDecorator.requiredRole</td>
    <td>
      Either 'true' or 'false'; a value of 'true' enables role checking, but a notice must define an
      attribute named 'org.jasig.portlet.notice.service.filter.RequiredRoleNotificationFilter.requiredRole'
      to be subject to filtering.
    </td>
  </tr>
</table>

### [Modal Notifications][]

The `modal` display strategy presents notices in a modal dialog. If the notice
defines actions, they will be rendered as buttons at the bottom of the dialog; any button
will dismiss the dialog, as well as invoke the specified action server-side. Notices that
do not define actions will offer a 'close' (x) option in the upper-right corner of the dialog.  
This option will not invoke any specified server-side action. This feature could be used for a
Terms of Service pop-up that must be accepted before accessing the portal.

If you want to control the color of the modal you can override the CSS properties by adding
a file named `modal-override.css` in `{uportal-portlets-overlay|overlays}/NotificationPortlet/src/main/webapp/css`
inside your instance of uPortal.

[sponsored portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets
[legacy documentation in the external wiki]: https://wiki.jasig.org/pages/viewpage.action?pageId=47875986
[readme file for uportal-start]: https://github.com/Jasig/uPortal-start/blob/master/README.md
[jasypt cli tools]: http://www.jasypt.org/cli.html
[modal notifications]: notification-portlet-webapp/docs/modal.md
