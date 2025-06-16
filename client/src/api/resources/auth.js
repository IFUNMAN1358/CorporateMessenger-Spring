import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchRegister(registerForm) {
    try {
        const sessionResponse = await axios.post('/api/auth/registration', registerForm);
        await authStore.actions.login(
            sessionResponse.data.sessionId,
            sessionResponse.data.accessToken,
            sessionResponse.data.refreshToken
        );
        return { success: true };
    } catch (error) {
        console.error('Registration failed:', error);
        throw error;
    }
}

export async function fetchLogin(loginForm) {
    try {
        const sessionResponse = await axios.post('/api/auth/login', loginForm);
        await authStore.actions.login(
            sessionResponse.data.sessionId,
            sessionResponse.data.accessToken,
            sessionResponse.data.refreshToken
        );
        return { success: true };
    } catch (error) {
        console.error('Login failed:', error);
        throw error;
    }
}

export async function fetchLogout() {
    try {
        await axios.post('/api/auth/logout', null, {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'X-Session-Id': authStore.state.sessionId
            }
        });
        await authStore.actions.logout();
        return { success: true };
    } catch (error) {
        console.error('Logout failed:', error);
        throw error;
    }
}