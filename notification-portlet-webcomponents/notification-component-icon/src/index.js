import NotificationIcon from './NotificationIcon';
import 'reactive-elements';

// register i18next
import './i18n';

// register icons
import {library} from '@fortawesome/fontawesome-svg-core';
import {faBell} from '@fortawesome/free-solid-svg-icons/faBell';
library.add(faBell);

document.registerReact('notification-icon', NotificationIcon);
