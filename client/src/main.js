import { createApp } from 'vue';
import App from './App.vue';
import authStore from "@/store/authStore";
import router from "@/router/router";
import VueCookies from "vue-cookies";

const app = createApp(App);

authStore.actions.initializeStore();

app.use(router)
app.use(VueCookies)
app.mount('#app')