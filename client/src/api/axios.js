import axios from 'axios';

axios.defaults.baseURL = process.env.VUE_APP_BACK_BASE_URL;
axios.defaults.withCredentials = true;
axios.defaults.withXSRFToken = true;
axios.defaults.headers.common['X-Service-Name'] = process.env.VUE_APP_BACK_X_SERVICE_NAME;

export default axios;