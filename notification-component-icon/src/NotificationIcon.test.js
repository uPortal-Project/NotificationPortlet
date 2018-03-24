import React from 'react';
import ReactDOM from 'react-dom';
import NotificationIcon from './NotificationIcon';

it('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<NotificationIcon />, div);
  ReactDOM.unmountComponentAtNode(div);
});
