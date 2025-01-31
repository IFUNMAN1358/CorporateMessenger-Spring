import axios from 'axios';

axios.defaults.baseURL = process.env.VUE_APP_BACK_BASE_URL;
axios.defaults.withCredentials = true;
axios.defaults.withXSRFToken = true;

export default axios;