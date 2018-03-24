// import 'bootstrap/dist/css/bootstrap.min.css';
import NotificationIcon from './NotificationIcon';
import 'reactive-elements';

// register icons
import fontawesome from '@fortawesome/fontawesome';
import faBell from '@fortawesome/fontawesome-free-solid/faBell';
fontawesome.library.add(faBell);

document.registerReact('notification-icon', NotificationIcon);
