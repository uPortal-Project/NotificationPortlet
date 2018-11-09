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

### Data Sources for the Notifications REST API (Web Components)

The following data sources are available for use with the REST APIs and the Web Components that work
with them.  According to convention, the configuration for these APIs and data sources goes in a
`notification.properties` file within `portal.home`.

#### ClassLoaderResourceNotificationService

This data source reads notifications from one or more files in the Java classpath in the standard
Notification JSON format.  Use the following property to specify the location(s) of JSON files in
the classpath.

Example:

```properties
ClassLoaderResourceNotificationService.locations=demo/demoNotificationResponse.json,demo/demoNotificationResponse2.json
```

#### DemoNotificationService

The `DemoNotificationService` is a specialization of the `ClassLoaderResourceNotificationService`
designed for demonstrations.  Use the following property to specify the location(s) of JSON files in
the classpath.

Example:

```properties
DemoNotificationService.locations=demo/demoNotificationResponse.json,demo/demoNotificationResponse2.json
```

#### RestfulJsonNotificationService

This data source reads notifications from one or more remote URLs in the standard Notification JSON
format.  Use the following property to specify the URLs to read.

```properties
RestfulJsonNotificationService.serviceUrls=https://my.university.edu/notifications
```

#### RomeNotificationService

This data source converts RSS into notifications.  It reads from one or more feeds.  Use the
following property to specify feed URLs.

```properties
RomeNotificationService.feedUrls=https://my.university.edu/announcements/rss
```

#### JDBC (RDBMS) Notification Services

This module includes data sources that allow you to pull notifications from relational databases
using custom SQL queries.

Using the `AnyRowResourceContentJdbcNotificationService` you can define a notification using JSON in
the classpath and assign it to users when a cutom SQL query returns at least one row.  You can
parameterize the query with user attributes and other inputs.

Additional JDBC data sources normally extend from `AbstractJdbcNotificationService`.

#### JpaNotificationService

The `JpaNotificationService` is different from most other notification data sources because it is
based on a _push_ model:  other components or systems push notifications to the Notification module
when they become available, instead of the Notification module pulling notifications when they are
needed (_viz_. when the user logs in).

Notifications may be pushed to the `JpaNotificationService` _via_ HTTP POST to the
`/api/v1/notifications` URI.  Only authorized users may use this API, and a valid, signed OIDC Id
token must be presented as a `Bearer` token in the `Authorization` header of the POST request.

##### Sample JSON for JpaNotificationService

The `/api/v1/notifications` URI accepts JSON like the following in the request body:

```
{
  "title": "Applications for Graduation Due",
  "source": "Demo Service",
  "priority": 1,
  "url": "http:\/\/www.jasig.org",
  "linkText": "Follow story",
  "body": "Please submit your application to graduate in a timely manner.",
  "actions": [
    {
      "clazz": "org.jasig.portlet.notice.action.read.MarkAsReadAndRedirectAction",
      "label": "MARK AS READ AND REDIRECT"
    }
  ],
  "addressees": [
    {
      "name": "admin only",
      "type":  "INDIVIDUAL",
      "recipients": [
        {
          "username": "admin"
        }
      ]
    }
  ]
}
```

You can use the embedded Swagger client at ``/NotificationPortlet/swagger-ui.html` to try out the
`JpaNotificationService` and your JSON.

### Filtering the Notifications REST API (Web Components)

You can filter the contents of the Notifications REST API using query string parameters.  Some
filtering options are available now, and others are being added periodically.

Web Components in the Notification project typically support a `filter` property (HTML attribute)
that allows you to specify filtering options without knowing or editing the `notificationApiUrl`
property.

Example:

```
<script src="https://unpkg.com/vue@2.5.16"></script>
<script type="text/javascript" src="/NotificationPortlet/scripts/notification-modal.js"></script>
<notification-modal filter="minPriority=1"></notification-modal>
```

#### `minPriority`

Use the `minPriority` query string parameter to filter out notifications that either (1) have no
priority value assigned or (2) are lower in priority than the value specified.  **Remember that
priority 1 is the highest priority**, so `minPriority=2` means notifications with priority value 1
or 2.

Example:

```
/NotificationPortlet/api/v2/notifications?minPriority=2
```

#### `maxPriority`

Use the `maxPriority` query string parameter to filter out notifications that are higher in priority
than the value specified.  **Remember that priority 1 is the highest priority**, so `maxPriority=3`
means notifications with priority value 3-5 (assuming 1-5 is the normal range) or no priority value
assigned.

Example:

```
/NotificationPortlet/api/v2/notifications?maxPriority=3
```

#### `read`

Use the `read` query parameter to filter out notifications that have been marked read. A value of `true` will return
notifications that have been read while a value of `false` will return unread notifications. The absense of this
parameter will return all notifications regardless of their `READ` attribute value.

Example:

```
/NotificationPortlet/api/v2/notifications?read=false
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

#### Filtering in Portlet-Based Display Strategies

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
  <tr>
    <td>FilteringNotificationServiceDecorator.titleRegex</td>
    <td>Regular expression that matches the entire title</td>
  </tr>
  <tr>
    <td>FilteringNotificationServiceDecorator.bodyRegex</td>
    <td>Regular expression that matches the entire body</td>
  </tr>
</table>

#### [Modal Notifications][]

The `modal` display strategy presents notices in a Bootstrap modal dialog.  If the notice
defines actions, they will be rendered as buttons at the bottom of the dialog;  any button
will dismiss the dialog, as well as invoke the spe,cified action server-side.  Notices that
do not define actions will offer a 'close' (x) option in the upper-right corner of the dialog.  
This feature could be used for a Terms of Service pop-up that must be accepted before
accessing the portal.

## Additional Documentation

### Swagger Client and API Documentation

The Notification portlet provides API documentation with Swagger.  You can access the Swagger client
in your deployment at the following URI:  `/NotificationPortlet/swagger-ui.html`.

[Sponsored Portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets
[legacy documentation in the external wiki]: https://wiki.jasig.org/pages/viewpage.action?pageId=47875986
[README file for uPortal-Start]: https://github.com/Jasig/uPortal-start/blob/master/README.md
[Jasypt CLI Tools]: http://www.jasypt.org/cli.html
[Apereo uPortal]: https://github.com/jasig/uPortal
[Web Components]: https://www.webcomponents.org/
[Modal Notifications]: notification-portlet-webapp/docs/modal.md
[Oracle Object Names]: https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Database-Object-Names-and-Qualifiers.html#GUID-75337742-67FD-4EC0-985F-741C93D918DA
