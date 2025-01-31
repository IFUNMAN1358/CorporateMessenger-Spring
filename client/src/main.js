import { createApp } from 'vue';
import App from './App.vue';
import router from '@/js/config/router';
import VueCookies from 'vue-cookies';
import authStore from "@/js/store/stores/authStore";

const app = createApp(App);

authStore.actions.initializeStore();

app.use(router)
app.use(VueCookies)
app.mount('#app')