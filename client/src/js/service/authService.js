import axios from "@/js/config/axios";
import authStore from "@/js/store/stores/authStore";

export async function handleRegister(registerForm) {
    try {
        const jwtResponse = await axios.post('/api/auth/registration', registerForm);
        await authStore.actions.login(
            jwtResponse.data.accessToken,
            jwtResponse.data.refreshToken
        );
    } catch (error) {
        console.error('Registration failed:', error);
    }
}

export async function handleLogin(loginForm) {
    try {
        const jwtResponse = await axios.post('/api/auth/login', loginForm);
        await authStore.actions.login(
            jwtResponse.data.accessToken,
            jwtResponse.data.refreshToken
        );
    } catch (error) {
        console.error('Login failed:', error);
    }
}

export async function handleLogout() {
    try {
        await axios.post('/api/auth/logout', null, {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            }
        });
        await authStore.actions.logout();
    } catch (error) {
        console.error('Logout failed:', error);
    }
}