# Apereo Notification Portlet

[![Linux Build Status](https://travis-ci.org/Jasig/NotificationPortlet.svg?branch=master)](https://travis-ci.org/Jasig/NotificationPortlet)
[![Windows Build status](https://ci.appveyor.com/api/projects/status/we2hk9cv24f47oao/branch/master?svg=true)](https://ci.appveyor.com/project/ChristianMurphy/notificationportlet/branch/master)

This is a [Sponsored Portlet][] in the uPortal project.

## Configuration

See also [documentation in the external wiki][Notifications portlet in Confluence].

### Using Encrypted Property Values

You may optionally provide sensitive configuration items -- such as database passwords -- in encrypted format.  Use the [Jasypt CLI Tools](http://www.jasypt.org/cli.html) to encrypt the sensitive value, then include it in a `.properties` file like this:

```
hibernate.connection.password=ENC(9ffpQXJi/EPih9o+Xshm5g==)
```

Specify the encryption key using the `UP_JASYPT_KEY` environment variable.

### [Modal Notification](docs/modal.md)
Modal Notification is a single notice that will be presented until the user clicks the `Accept` button. This feature could be used for a Terms of Service pop-up that must be accepted before
accessing the portal.

[Sponsored Portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets

[Notifications portlet in Confluence]: https://wiki.jasig.org/pages/viewpage.action?pageId=47875986
