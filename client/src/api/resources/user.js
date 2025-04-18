import axios from "@/api/axios";
import authStore from "@/store/auth/authStore";

export async function fetchGetUsers(username, page) {
    try {
        const response = await axios.get(
            `/api/user/search/${username}/${page}`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get users failed:', error);
    }
}

export async function fetchGetProfile() {
    try {
        const response = await axios.get(
            `/api/user`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get profile failed:', error);
    }
}

export async function fetchGetUser(userId) {
    try {
        const response = await axios.get(
            `/api/user/${userId}`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get user failed:', error);
    }
}

export async function fetchUpdatePassword(data) {
    try {
        const response = await axios.patch(
            `/api/user/password`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        console.info(response.data.message);
    } catch (error) {
        console.error('Update password failed:', error);
    }
}

export async function fetchDeleteAccount() {
    try {
        const response = await axios.delete(
            `/api/user`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        await authStore.actions.logout();
        console.info(response.data.message);
    } catch (error) {
        console.error('Update password failed:', error);
    }
}