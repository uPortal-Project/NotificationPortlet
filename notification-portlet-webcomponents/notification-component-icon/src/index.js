import NotificationIcon from './NotificationIcon';
import 'reactive-elements';

// register i18next
import './i18n';

// register icons
import fontawesome from '@fortawesome/fontawesome';
import faBell from '@fortawesome/fontawesome-free-solid/faBell';
fontawesome.library.add(faBell);

document.registerReact('notification-icon', NotificationIcon);
