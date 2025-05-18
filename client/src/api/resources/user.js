import axios from "@/api/axios";
import authStore from "@/store/authStore";


// @NotNull(message = "Новое имя пользователя не может быть null")
// @NotBlank(message = "Новое имя пользователя не может быть пустым")
// @Size(message = "Новое имя пользователя должно содержать от 5 до 32 символов", min = 5, max = 32)
// private String newUsername;
//
export async function fetchChangeUserUsername(data) {
    try {
        const response = await axios.patch(
            `/api/user/username`,
            data,
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

// @NotNull(message = "Текущий пароль не может быть null")
// @NotBlank(message = "Текущий пароль не может быть пустым")
// private String currentPassword;
//
// @NotNull(message = "Новый пароль не может быть null")
// @NotBlank(message = "Новый пароль не может быть пустым")
// @Size(message = "Новый пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
// private String newPassword;
//
// @NotNull(message = "Повторный пароль не может быть null")
// @NotBlank(message = "Повторный пароль не может быть пустым")
// @Size(message = "Повторный пароль должен содержать от 10 до 30 символов", min = 10, max = 30)
// private String confirmNewPassword;
//
export async function fetchChangeUserPassword(data) {
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
        return response.data;
    } catch (error) {
        console.error('Change user password failed:', error);
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
        console.error('Get your user data:', error);
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

// @NotNull(message = "Идентификатор пользователя не может быть null")
// private UUID userId;
//
export async function fetchBlockUserByUserId(data) {
    try {
        const response = await axios.patch(
            `/api/user/block`,
            data,
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

// @NotNull(message = "Идентификатор пользователя не может быть null")
// private UUID userId;
//
export async function fetchUnblockUserByUserId(data) {
    try {
        const response = await axios.patch(
            `/api/user/unblock`,
            data,
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