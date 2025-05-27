import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchChangeUserFirstNameAndLastName(firstName, lastName) {
    try {
        const response = await axios.patch(
            `/api/user/firstName-and-lastName`,
            {
                "firstName": firstName,
                "lastName": lastName
            },
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change user firstName and lastName failed:', error);
        throw error;
    }
}

export async function fetchChangeUserUsername(username) {
    try {
        const response = await axios.patch(
            `/api/user/username`,
            {
                "username": username
            },
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change user username failed:', error);
        throw error;
    }
}

export async function fetchChangeUserPassword(password, newPassword, confirmPassword) {
    try {
        const response = await axios.patch(
            `/api/user/password`,
            {
                "password": password,
                "newPassword": newPassword,
                "confirmPassword": confirmPassword
            },
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change user password failed:', error);
        throw error;
    }
}

export async function fetchChangeUserBio(bio) {
    try {
        const response = await axios.patch(
            `/api/user/bio`,
            {
                "bio": bio
            },
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change user bio failed:', error);
        throw error;
    }
}

export async function fetchSearchUsersByUsername(username, page, size) {
    try {
        const response = await axios.get(
            `/api/user/search`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                },
                params: {
                    username,
                    page,
                    size
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Search users by username failed:', error);
        throw error;
    }
}


export async function fetchExistsByUsername(username) {
    try {
        const response = await axios.get(
            `/api/user/exists-username`,
            {
                params: {
                    username
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Fetch exists user by username failed:', error);
        throw error;
    }
}


export async function fetchGetMyUserData() {
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
        console.error('Get my user data failed:', error);
        throw error;
    }
}

export async function fetchGetUserByUserId(userId) {
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
        console.error('Get user by userId failed:', error);
        throw error;
    }
}

export async function fetchFindAllMyBlockedUsers(page, size) {
    try {
        const response = await axios.get(
            `/api/user/blocked`,
            {
                params: {
                    page: page,
                    size: size
                },
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Find all my blocked users failed:', error);
        throw error;
    }
}

export async function fetchBlockUserByUserId(userId) {
    try {
        const response = await axios.patch(
            `/api/user/${userId}/block`,
            null,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Block user by userId failed:', error);
        throw error;
    }
}

export async function fetchUnblockUserByUserId(userId) {
    try {
        const response = await axios.patch(
            `/api/user/${userId}/unblock`,
            null,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Unblock user by userId failed:', error);
        throw error;
    }
}

export async function fetchSoftDeleteAccount() {
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
        console.error('Delete account failed:', error);
        throw error;
    }
}