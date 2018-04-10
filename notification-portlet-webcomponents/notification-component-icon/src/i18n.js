import i18n from 'i18next';
// eslint-disable-next-line
import resources from '@alienfast/i18next-loader!./locales/index.js';
import LanguageDetector from 'i18next-browser-languagedetector';
import {reactI18nextModule} from 'react-i18next';

i18n
  .use(LanguageDetector)
  .use(reactI18nextModule)
  .init({
    fallbackLng: 'en',

    // have a common namespace used around the full app
    ns: ['notification-icon'],
    defaultNS: 'notification-icon',

    debug: true,

    react: {
      wait: true,
    },
    resources,
  });

export default i18n;
