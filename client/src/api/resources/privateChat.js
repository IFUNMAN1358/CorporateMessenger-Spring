import axios from "@/api/axios";
import authStore from "@/store/auth/authStore";

export async function fetchGetOrCreatePrivateChat(data) {
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

export async function fetchGetPrivateChat(chatId) {
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

export async function fetchGetAllPrivateChats() {
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