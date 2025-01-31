import axios from "@/js/config/axios";
import authStore from "@/js/store/stores/authStore";

export async function getGroupChatMembers(chatId) {
    try {
        const response = await axios.get(
            `/api/chat/group/${chatId}/members`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get group chat members failed:', error);
    }
}

export async function getAvailableUsersToAdd(chatId) {
    try {
        const response = await axios.get(
            `/api/chat/group/${chatId}/members/available`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get available users to add failed:', error);
    }
}

export async function addUsersToGroupChat(chatId, data) {
    try {
        const response = await axios.post(
            `/api/chat/group/${chatId}/members`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Add users to group chat failed:', error);
    }
}

export async function deleteGroupChatMembers(chatId, data) {
    try {
        const response = await axios.delete(
            `/api/chat/group/${chatId}/members`,
            {
                data,
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Delete group chat member/s failed:', error);
    }
}

export async function leaveFromChat(chatId) {
    try {
        const response = await axios.delete(
            `/api/chat/group/${chatId}/leave`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('An error occurred while trying to exit from the group chat:', error);
    }
}