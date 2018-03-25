import React, {Component} from 'react';
import {
  Dropdown,
  DropdownMenu,
  DropdownToggle,
  DropdownItem,
} from 'reactstrap';
import FontAwesomeIcon from '@fortawesome/react-fontawesome';

class NotificationIcon extends Component {
  constructor(props) {
    super(props);

    this.state = {
      dropdownOpen: false,
      notifications: [],
    };
  }

  fetchNotifications = async () => {
    try {
      const response = await fetch('sample-notifications.json');
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
    this.setState({
      dropdownOpen: !this.state.dropdownOpen,
    });
  };

  renderNotifications = () => {
    // empty notifications
    if (this.state.notifications.length < 1) {
      return (
        <DropdownItem className="up-notification--menu-item" disabled>
          congrats no notifications
        </DropdownItem>
      );
    }

    // one or more notifications
    return this.state.notifications.map(({url, message, isRead}) => (
      <DropdownItem
        key={message}
        tag="a"
        className={'up-notification--menu-item ' + (isRead ? 'read' : 'unread')}
        href={url}
      >
        {message}
      </DropdownItem>
    ));
  };

  componentWillMount = this.fetchNotifications;

  render = () => (
    <Dropdown
      isOpen={this.state.dropdownOpen}
      toggle={this.toggle}
      className="up-notification"
    >
      <DropdownToggle onClick={this.toggle} className="up-notification--toggle">
        <span className="sr-only">notifications</span>
        <FontAwesomeIcon icon="bell" />
      </DropdownToggle>

      <DropdownMenu className="up-notification--menu">
        <DropdownItem className="up-notification--menu-header" header>
          Notifications
        </DropdownItem>

        {this.renderNotifications()}

        <DropdownItem
          className="up-notification--menu-footer"
          tag="a"
          href="/p/notifications"
          header
        >
          see all notifications
        </DropdownItem>
      </DropdownMenu>
    </Dropdown>
  );
}

export default NotificationIcon;
