import axios from "@/js/config/axios";
import authStore from "@/js/store/stores/authStore";


export async function createGroupChat(formData) {
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

export async function getAllGroupChats() {
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

export async function getGroupChat(chatId) {
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

export async function changeGroupChatMetadata(chatId, data) {
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

export async function changeGroupChatPublicStatus(chatId) {
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

export async function changeGroupChatOwner(chatId, data) {
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

export async function deleteGroupChat(chatId) {
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