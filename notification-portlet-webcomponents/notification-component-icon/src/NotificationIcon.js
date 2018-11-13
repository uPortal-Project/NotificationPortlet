import React, {Component} from 'react';
import oidc from '@uportal/open-id-connect';
import Dropdown from 'reactstrap/lib/Dropdown';
import DropdownMenu from 'reactstrap/lib/DropdownMenu';
import DropdownToggle from 'reactstrap/lib/DropdownToggle';
import DropdownItem from 'reactstrap/lib/DropdownItem';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import translate from 'react-i18next/dist/es/translate';
import reactTimeout from 'react-timeout';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import get from 'lodash/get';
import find from 'lodash/find';

const StyledDropdown = styled(Dropdown)``;
const StyledDropdownMenu = styled(DropdownMenu)`
  & {
    // NOTE: overload portal styles
    transform: translate3d(0, 32px, 0) !important;
    z-index: 1002 !important;
  }
`;
const StyledButtonStyle = {
  paddingRight: '1rem',
};
const StyledSVG = {
  maxWidth: '10px',
  marginRight: '5px',
};
const StyledDropdownToggle = styled(DropdownToggle)`
  &:hover,
  &:focus {
    color: white;
  }

  &.up-notification--toggle {
    background-color: inherit;
  }

  &.up-notification--toggle.up-active {
    background-color: #d50000;
  }
`;
const StyledDropdownItem = styled(DropdownItem)`
  & {
    // NOTE: overload portal styles
    width: 100%;
    clear: both;
    padding: 0.25rem 1.5rem;
    display: block;
    color: black !important;
  }

  &.up-notification--menu-item {
    border-bottom: 0.25px solid lightsteelblue;
  }

  &.up-notification--menu-header {
    font-weight: bold;
    border-bottom: 2px solid lightsteelblue;
    padding-bottom: 8px;
  }

  &.up-notification--menu-footer {
    margin-top: 8px;
    font-style: italic;
  }

  &.up-unread {
    background-color: aliceblue;
  }
`;

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
    notifications: [],
  };

  fetchNotifications = async () => {
    const {notificationApiUrl, tokenTimeoutMs, debug} = this.props;

    try {
      const response = await fetch(notificationApiUrl, {
        credentials: 'same-origin',
        headers: {
          'Authorization': debug
            ? null // don't worry about token in debug mode
            : 'Bearer ' + (await oidc({timeout: tokenTimeoutMs})).encoded,
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

  doPost = (url) => () => {
    let form = document.createElement('form');
    form.action = url;
    form.method = 'POST';
    form.style.display = 'none';
    document.body.appendChild(form);
    form.submit();
  };

  renderNotificationCount = (unreadCount) => {
    const {t} = this.props;

    return (
      <span className="up-notification--notification-count">
        {unreadCount || '0'}
        <span className="sr-only">
          {t('notification-count', {unreadCount})}
        </span>
      </span>
    );
  };

  renderNotifications = () => {
    const {t} = this.props;
    const {notifications} = this.state;

    // empty notifications
    if (notifications.length < 1) {
      return (
        <StyledDropdownItem className="up-notification--menu-item" disabled>
          {t('notifications-all-read')}
        </StyledDropdownItem>
      );
    }

    // one or more notifications
    return notifications.map(
        ({url, id, body, title, attributes, availableActions}) => {
          let onClick = undefined;
          let href = url;

          // Is this link based on MarkAsReadAndRedirectAction?
          const action = find(availableActions, {
            id: 'MarkAsReadAndRedirectAction',
          });
          if (action) {
            onClick = this.doPost(action.apiUrl);
            // TODO: Find another way to avoid a page refresh
            // eslint-disable-next-line no-script-url
            href = 'javascript:void(0)';
          }

          return (
            <StyledDropdownItem
              key={id || body}
              tag="a"
              className={
                'up-notification--menu-item ' +
              (JSON.parse(get(attributes, 'READ.0', 'true'))
                ? 'up-read'
                : 'up-unread')
              }
              onClick={onClick}
              href={href}
            >
              {title}
            </StyledDropdownItem>
          );
        }
    );
  };

  componentDidMount = this.fetchNotifications;

  render = () => {
    const {t, seeAllNotificationsUrl} = this.props;
    const {notifications, isDropdownOpen} = this.state;

    const unreadCount = notifications.filter(
        ({attributes}) => !JSON.parse(get(attributes, 'READ.0', 'true'))
    ).length;

    let dropdownClasses = 'up-notification--toggle';
    if (unreadCount !== 0) {
      dropdownClasses += ' up-active';
    }

    return (
      <React.Fragment>
        <link
          rel="stylesheet"
          href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
        />
        <StyledDropdown
          isOpen={isDropdownOpen}
          toggle={this.toggle}
          className="up-notification"
        >
          <StyledDropdownToggle
            onClick={this.toggle}
            className={dropdownClasses}
            style={StyledButtonStyle}
          >
            <FontAwesomeIcon icon="bell" style={StyledSVG} />
            &nbsp;
            {this.renderNotificationCount(unreadCount)}
          </StyledDropdownToggle>

          <StyledDropdownMenu className="up-notification--menu">
            <StyledDropdownItem className="up-notification--menu-header" header>
              {t('notifications')}
            </StyledDropdownItem>

            {this.renderNotifications()}

            <StyledDropdownItem
              className="up-notification--menu-footer"
              tag="a"
              href={seeAllNotificationsUrl}
            >
              {t('notifications-see-all')}
            </StyledDropdownItem>
          </StyledDropdownMenu>
        </StyledDropdown>
      </React.Fragment>
    );
  };
}

export default translate('notification-icon')(reactTimeout(NotificationIcon));
