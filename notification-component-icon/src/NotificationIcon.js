import React, {Component} from 'react';
import {Dropdown, DropdownMenu, DropdownToggle} from 'reactstrap';
import FontAwesomeIcon from '@fortawesome/react-fontawesome';

class NotificationIcon extends Component {
  constructor(props) {
    super(props);

    this.state = {
      dropdownOpen: false,
    };
  }

  toggle = () => {
    this.setState({
      dropdownOpen: !this.state.dropdownOpen,
    });
  };

  render() {
    return (
      <Dropdown isOpen={this.state.dropdownOpen} toggle={this.toggle}>
        <DropdownToggle onClick={this.toggle}>
          <span className="sr-only">notifications</span>
          <FontAwesomeIcon icon="bell" />
        </DropdownToggle>
        <DropdownMenu>
          <div>plain ol' content</div>
        </DropdownMenu>
      </Dropdown>
    );
  }
}

export default NotificationIcon;
