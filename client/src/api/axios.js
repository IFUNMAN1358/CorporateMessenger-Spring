import axios from 'axios';
import {fetchCsrfToken} from "@/api/auth";

axios.defaults.baseURL = process.env.VUE_APP_BACK_BASE_URL;
axios.defaults.withCredentials = true;
axios.defaults.withXSRFToken = true;

axios.interceptors.response.use(
  (response) => response,
  async (error) => {

    const originalRequest = error.config;

    if (error.response?.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        await fetchCsrfToken();
        return axios(originalRequest);
      } catch (retryError) {
        console.error('Failed to retry request with new CSRF token:', retryError);
        return Promise.reject(retryError);
      }
    }

    return Promise.reject(error);
  }
);

export default axios;