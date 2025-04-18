import axios from "@/api/axios";
import authStore from "@/store/auth/authStore";

export async function fetchGetGroupChatPhoto(chatId) {
  try {
    const response = await axios.get(
        `/api/chat/group/${chatId}/photo`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            },
            responseType: 'blob'
        }
    );
    return URL.createObjectURL(response.data);
  } catch (error) {
    console.error('Get group chat photo failed:', error);
  }
}

export async function fetchUploadOrChangePhoto(chatId, formData) {
  try {
    const response = await axios.post(
        `/api/chat/group/${chatId}/photo`,
        formData,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'Content-Type': 'multipart/form-data'
            },
            responseType: 'blob'
        }
    );
    return URL.createObjectURL(response.data);
  } catch (error) {
    console.error('Upload or change group chat photo failed:', error);
  }
}

export async function fetchDeletePhoto(chatId) {
  try {
    const response = await axios.delete(
        `/api/chat/group/${chatId}/photo`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            }
        }
    );
    console.info(response.data.message);
  } catch (error) {
    console.error('Delete group chat photo failed:', error);
  }
}