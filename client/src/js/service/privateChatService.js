import axios from "@/js/config/axios";
import authStore from "@/js/store/stores/authStore";

export async function getOrCreatePrivateChat(data) {
    try {
        const response = await axios.post(
            '/api/chat/private',
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get or create private chat failed:', error);
    }
}

export async function getPrivateChat(chatId) {
    try {
        const response = await axios.get(
            `/api/chat/private/${chatId}`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get private chat failed:', error);
    }
}

export async function getAllPrivateChats() {
    try {
        const response = await axios.get(
            '/api/chat/private/all',
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get all private chats failed:', error);
    }
}