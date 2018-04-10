import React, {Component} from 'react';
import {
  Dropdown,
  DropdownMenu,
  DropdownToggle,
  DropdownItem,
} from 'reactstrap';
import FontAwesomeIcon from '@fortawesome/react-fontawesome';
import {translate} from 'react-i18next';
import reactTimeout from 'react-timeout';
import PropTypes from 'prop-types';

class NotificationIcon extends Component {
  static propTypes = {
    userInfoApiUrl: PropTypes.string,
    tokenTimeoutMs: PropTypes.number,
    notificationApiUrl: PropTypes.string,
    seeAllNotificationsUrl: PropTypes.string,
    debug: PropTypes.bool,
  };

  static defaultProps = {
    userInfoApiUrl: '/uPortal/api/v5-1/userinfo',
    tokenTimeoutMs: 180000, // 3 minutes
    notificationApiUrl: '/NotificationPortlet/api/v2/notifications',
    seeAllNotificationsUrl: '/uPortal/p/notification',
    debug: false,
  };

  state = {
    isDropdownOpen: false,
    bearerToken: null,
    notifications: [],
  };

  fetchBearerToken = async () => {
    const {setTimeout, userInfoApiUrl, tokenTimeoutMs} = this.props;

    try {
      const response = await fetch(userInfoApiUrl, {
        credentials: 'same-origin',
      });
      if (!response.ok) {
        throw new Error(response.statusText);
      }
      const bearerToken = await response.text();

      this.setState({bearerToken});

      // clear token after timeout has passed
      setTimeout(function() {
        this.setState({bearerToken: null});
      }, tokenTimeoutMs);

      return bearerToken;
    } catch (err) {
      // TODO: add an error view
      console.error(err);
    }
  };

  fetchNotifications = async () => {
    let {bearerToken} = this.state;
    const {notificationApiUrl, debug} = this.props;

    try {
      if (!bearerToken && !debug) {
        bearerToken = await this.fetchBearerToken();
      }
      const response = await fetch(notificationApiUrl, {
        credentials: 'same-origin',
        headers: {
          'Authorization': 'Bearer ' + bearerToken,
          'content-type': 'application/jwt',
        },
      });
      if (!response.ok) {
        throw new Error(response.statusText);
      }
      const notifications = await response.json();

      this.setState({notifications});
    } catch (err) {
      // TODO: add an error view
      console.error(err);
    }
  };

  toggle = () => {
    this.setState(({isDropdownOpen}) => {
      // TODO: determine why debounce is needed
      // debounce toggle
      if (isDropdownOpen === this.state.isDropdownOpen) {
        return {
          isDropdownOpen: !isDropdownOpen,
        };
      }
    });
  };

  renderNotificationCount = () => {
    const {t} = this.props;
    const {notifications} = this.state;

    const count = notifications.filter(({isRead}) => !isRead).length;

    return (
      <span className="up-notification--notification-count">
        {count}
        <span className="sr-only">{t('notification-count', {count})}</span>
      </span>
    );
  };

  renderNotifications = () => {
    const {t} = this.props;
    const {notifications} = this.state;

    // empty notifications
    if (notifications.length < 1) {
      return (
        <DropdownItem className="up-notification--menu-item" disabled>
          {t('notifications-all-read')}
        </DropdownItem>
      );
    }

    // one or more notifications
    return notifications.map(({url, body, isRead}) => (
      <DropdownItem
        key={body}
        tag="a"
        className={
          'up-notification--menu-item ' + (isRead ? 'up-read' : 'up-unread')
        }
        href={url}
      >
        {body}
      </DropdownItem>
    ));
  };

  componentDidMount = this.fetchNotifications;

  render = () => {
    const {t, seeAllNotificationsUrl} = this.props;
    const {notifications, isDropdownOpen} = this.state;

    const dropdownClasses = ['up-notification--toggle'];
    if (notifications.length !== 0) {
        dropdownClasses.push('up-active');
    }

    return (
      <Dropdown
        isOpen={isDropdownOpen}
        toggle={this.toggle}
        className="up-notification"
      >
        <DropdownToggle
          onClick={this.toggle}
          className={dropdownClasses}
        >
          <FontAwesomeIcon icon="bell" />
          {this.renderNotificationCount()}
        </DropdownToggle>

        <DropdownMenu className="up-notification--menu">
          <DropdownItem className="up-notification--menu-header" header>
            {t('notifications')}
          </DropdownItem>

          {this.renderNotifications()}

          <DropdownItem
            className="up-notification--menu-footer"
            tag="a"
            href={seeAllNotificationsUrl}
            header
          >
            {t('notifications-see-all')}
          </DropdownItem>
        </DropdownMenu>
      </Dropdown>
    );
  };
}

export default translate('translations')(reactTimeout(NotificationIcon));
