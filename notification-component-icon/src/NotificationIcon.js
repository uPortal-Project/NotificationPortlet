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
      notifications: [
        {
          url: 'https://example.com/register',
          message: 'time to register for finals',
          isRead: false,
        },
        {
          url: 'https://example.com/game',
          message: 'get tickets to the big game',
          isRead: true,
        },
      ],
    };
  }

  toggle = () => {
    this.setState({
      dropdownOpen: !this.state.dropdownOpen,
    });
  };

  renderEmpty = () => (
    <DropdownItem className="up-notification--menu-item" disabled>
      congrats no notifications
    </DropdownItem>
  );

  renderOneOrMany = () =>
    this.state.notifications.map(({url, message, isRead}) => (
      <DropdownItem
        tag="a"
        className={'up-notification--menu-item ' + (isRead ? 'read' : 'unread')}
        href={url}
      >
        {message}
      </DropdownItem>
    ));

  render() {
    return (
      <Dropdown
        isOpen={this.state.dropdownOpen}
        toggle={this.toggle}
        className="up-notification"
      >
        <DropdownToggle
          onClick={this.toggle}
          className="up-notification--toggle"
        >
          <span className="sr-only">notifications</span>
          <FontAwesomeIcon icon="bell" />
        </DropdownToggle>
        <DropdownMenu className="up-notification--menu">
          <DropdownItem className="up-notification--menu-header" header>
            Notifications
          </DropdownItem>
          {this.state.notifications.length < 1
            ? this.renderEmpty()
            : this.renderOneOrMany()}
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
}

export default NotificationIcon;
