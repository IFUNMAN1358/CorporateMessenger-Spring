import axios from "@/api/axios";
import authStore from "@/store/auth/authStore";

export async function fetchCreateMessage(formData) {
    try {
        const response = await axios.post(
            '/api/message',
            formData,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`,
                    'Content-Type': 'multipart/form-data'
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Create message failed:', error);
    }
}

export async function fetchGetAllMessages(chatId, page) {
    try {
        const response = await axios.get(
            `/api/message/${chatId}/${page}`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get messages failed:', error);
    }
}

export async function fetchUpdateMessageContent(data) {
    try {
        const response = await axios.patch(
            `/api/message/content`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Update message content failed:', error);
    }
}

export async function fetchDeleteMessage(data) {
    try {
        const response = await axios.delete(
            `/api/message`,
            {
                data,
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Delete message failed:', error);
    }
}

export async function fetchReadMessage(data) {
    try {
        const response = await axios.post(
            `/api/message/read`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Read message failed:', error);
    }
}

export async function fetchGetMessageFile(chatId, messageId, fileId) {
    try {
        const response = await axios.get(
            `/api/chat/${chatId}/message/${messageId}/file/${fileId}`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`
                },
                responseType: 'blob'
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get message file failed:', error);
        return null;
    }
}