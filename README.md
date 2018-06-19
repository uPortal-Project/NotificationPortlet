# Apereo Notification Portlet

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet.notification/notification-portlet-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet.notification/notification-portlet-parent)
[![Linux Build Status](https://travis-ci.org/Jasig/NotificationPortlet.svg?branch=master)](https://travis-ci.org/Jasig/NotificationPortlet)
[![Windows Build status](https://ci.appveyor.com/api/projects/status/we2hk9cv24f47oao/branch/master?svg=true)](https://ci.appveyor.com/project/ChristianMurphy/notificationportlet/branch/master)

This is a [Sponsored Portlet][] in the uPortal project.

## Configuration

See also the [legacy documentation in the external wiki][].

### Java Properties

Some configuration settings for the Notification portlet are managed in Java properties files that
are loaded by a Spring `PropertySourcesPlaceholderConfigurer`.  (Other settings are data, managed in
the "portlet publication record" a.k.a. `portlet-definition.xml` file;  these are covered elsewhere
in this doc.)

The properties files that are sourced by Spring are:

  - `classpath:datasource.properties`
  - `classpath:configuration.properties`
  - `file:${portal.home}/global.properties`
  - `file:${portal.home}/notification.properties`

For a definitive, comprehensive list of these settings you must look inside `datasource.properties`
and `configuration.properties`.  (This `README` may be incomplete and/or out of date.)

#### The `portal.home` Directory

uPortal version 5 uses a directory called `portal.home` for properties files that live outside of
-- and have the ability to _override_ properties files within-- the webapp in Tommcat.  Please
review the [README file for uPortal-Start][] for more information on this sytem.

The Notification portlet sources the shared `global.properties` file, as well as it's own (private)
file called `notification.properties` in the `portal.home` directory.

#### Using Encrypted Property Values

Within the properties files that are sourced by Spring, you may optionally provide sensitive
configuration items -- such as database passwords -- in encrypted format.  Use the
[Jasypt CLI Tools][] to encrypt the sensitive value, then include it in a `.properties` file
like this:

```
hibernate.connection.password=ENC(9ffpQXJi/EPih9o+Xshm5g==)
```

Specify the encryption key using the `UP_JASYPT_KEY` environment variable.

### Notification Web Components

This project provides UI components for [Apereo uPortal][] that are modern [Web Components][].
These components are intended to replace the previous generation of components for uPortal based on
Java Portlets (see below).

The Web Components work differently from the earlier components.  They do not interact with data
sources through the Portlet API, but rather through a collection of REST APIs.  Unlike portlets,
configuration of data sources for the REST API is global:  it applies equally to all components that
work with them.

#### Data Sources for the Notifications REST API (Web Components)

The following data sources are available for use with the REST APIs and the Web Components that work
with them.  According to convention, the configuration for these APIs and data sources goes in a
`notification.properties` file within `portal.home`.

##### ClassLoaderResourceNotificationService

This data source reads notifications from one or more files in the Java classpath in the standard
Notification JSON format.  Use the following property to specify the location(s) of JSON files in
the classpath.

Example:

```properties
ClassLoaderResourceNotificationService.locations=demo/demoNotificationResponse.json,demo/demoNotificationResponse2.json
```

##### DemoNotificationService

The `DemoNotificationService` is a specialization of the `ClassLoaderResourceNotificationService`
designed for demonstrations.  Use the following property to specify the location(s) of JSON files in
the classpath.

Example:

```properties
DemoNotificationService.locations=demo/demoNotificationResponse.json,demo/demoNotificationResponse2.json
```

##### RestfulJsonNotificationService

This data source reads notifications from one or more remote URLs in the standard Notification JSON
format.  Use the following property to specify the URLs to read.

```properties
RestfulJsonNotificationService.serviceUrls=https://my.university.edu/notifications
```

##### RomeNotificationService

This data source converts RSS into notifications.  It reads from one or more feeds.  Use the
following property to specify feed URLs.

```properties
RomeNotificationService.feedUrls=https://my.university.edu/announcements/rss
```

### Java Portlet-Based UI Components

As it's name implies, this project was originally developed as a collection of Java Portlet
(JSR-286) technology user interface components.  This project is evolving away from portlets -- and
toward UI widgets based on [Web Components][] -- but the portlet-based components are still
available.

The following UI components in this project are portlet-based:

  - `notification`
  - `notification-icon` (though there is a replacement based on Web Components)
  - `emergency-alert`
  - `emergency-alert-admin`

The following subsections cover configuration of portlet-based UI components.

#### Publication Data

Besides Java properties, some configuration settings are managed as data in the _Portlet Publication
Record_ (the `portlet-definition.xml` file in uPortal).  These settings are defined on a per-
publication basis, so you can have several publications of the same portlet with each of them
configured differently.

##### Filtering

You can filter the notices that come from data sources in the publication record.  Use the following
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

#### [Modal Notifications][]

The `modal` display strategy presents notices in a modal dialog.  If the notice 
defines actions, they will be rendered as buttons at the bottom of the dialog;  any button
will dismiss the dialog, as well as invoke the specified action server-side.  Notices that
do not define actions will offer a 'close' (x) option in the upper-right corner of the dialog.  
This option will not invoke any specified server-side action. This feature could be used for a
Terms of Service pop-up that must be accepted before accessing the portal.

If you want to control the color of the modal you can override the CSS properties by adding
a file named `modal-override.css` in `{uportal-portlets-overlay|overlays}/NotificationPortlet/src/main/webapp/css`
inside your instance of uPortal.

[Sponsored Portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets
[legacy documentation in the external wiki]: https://wiki.jasig.org/pages/viewpage.action?pageId=47875986
[README file for uPortal-Start]: https://github.com/Jasig/uPortal-start/blob/master/README.md
[Jasypt CLI Tools]: http://www.jasypt.org/cli.html
[Apereo uPortal]: https://github.com/jasig/uPortal
[Web Components]: https://www.webcomponents.org/
[Modal Notifications]: notification-portlet-webapp/docs/modal.md
