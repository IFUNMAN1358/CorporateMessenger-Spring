import {reactive} from "vue";
import {jwtDecode} from "jwt-decode";
import {getCookie, removeCookies, setCookies} from "@/utils/cookie";
import router from "@/router/router";

const authState = reactive({
    accessToken: null
});

const authActions = {

    initializeStore() {
        const accessToken = getCookie("accessToken");

        if (accessToken && !authActions.isTokenExpired(accessToken)) {
          authState.accessToken = accessToken;
        } else {
          authActions.logout();
        }
    },

    login(accessToken, refreshToken) {
        authState.accessToken = accessToken;
        setCookies({
          'accessToken': accessToken,
          'refreshToken': refreshToken
        });
    },

    logout() {
        authState.accessToken = null;
        removeCookies('accessToken', 'refreshToken');
        router.push({ name: 'Login' }).catch(() => {});
    },

    isTokenExpired(token) {
        try {
            const decoded = jwtDecode(token);
            const now = Math.floor(Date.now() / 1000);
            return decoded.exp < now;
        } catch (e) {
            return true;
        }
    },

     hasRole(roleName) {
        try {
            const decoded = jwtDecode(authState.accessToken);
            const roles = decoded.roles || [];
            return roles.includes(roleName);
        } catch (e) {
            return false;
        }
    }
};

export default {
  state: authState,
  actions: authActions
};