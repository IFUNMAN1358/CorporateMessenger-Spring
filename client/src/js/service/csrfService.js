import axios from "@/js/config/axios";

async function fetchCsrfToken() {
  try {
    await axios.get('/api/csrf-token');
    console.log('CSRF token fetched');
  } catch (error) {
    console.error('Failed to fetch CSRF token:', error);
  }
};

await fetchCsrfToken();