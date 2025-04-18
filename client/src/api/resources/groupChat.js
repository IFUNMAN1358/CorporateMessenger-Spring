import axios from "@/api/axios";
import authStore from "@/store/auth/authStore";

export async function fetchCreateGroupChat(formData) {
    try {
        const response = await axios.post(
            '/api/chat/group',
            formData,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'Content-Type': 'multipart/form-data'
            }
        }
        );
        return response.data.message;
    } catch (error) {
        console.error('Create group chat failed:', error);
    }
}

export async function fetchGetAllGroupChats() {
    try {
        const response = await axios.get(
            '/api/chat/group/all',
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get all group chats failed:', error);
    }
}

export async function fetchGetGroupChat(chatId) {
    try {
        const response = await axios.get(
            `/api/chat/group/${chatId}`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get group chat failed:', error);
    }
}

export async function fetchChangeGroupChatMetadata(chatId, data) {
    try {
        const response = await axios.patch(
            `/api/chat/group/${chatId}/metadata`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change group chat metadata failed:', error);
    }
}

export async function fetchChangeGroupChatPublicStatus(chatId) {
    try {
        const response = await axios.patch(
            `/api/chat/group/${chatId}/status`,
            null,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change group chat public status failed:', error);
    }
}

export async function fetchChangeGroupChatOwner(chatId, data) {
    try {
        const response = await axios.patch(
            `/api/chat/group/${chatId}/owner`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change owner of group chat failed:', error);
    }
}

export async function fetchDeleteGroupChat(chatId) {
    try {
        const response = await axios.delete(
            `/api/chat/group/${chatId}`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Delete group chat failed:', error);
    }
}