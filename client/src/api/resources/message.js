import axios from "@/api/axios";
import authStore from "@/store/authStore";
import PhotoDefaultPlaceholder from "@/assets/images/PhotoDefaultPlaceholder.png";

export async function fetchCreateMessage(chatId, content, files = []) {
  const formData = new FormData();
  if (content) {
    formData.append('content', content);
  }

  if (files && files.length > 0) {
    files.forEach((file, index) => {
      if (file instanceof File) {
        formData.append('files', file);
      } else {
        console.warn(`Файл ${index + 1} не является объектом File:`, file);
      }
    });
  }

  const response = await axios.post(`/api/chat/${chatId}/message`, formData, {
    headers: {
      'Authorization': `Bearer ${authStore.state.accessToken}`,
      'X-Session-Id': authStore.state.sessionId,
      'Content-Type': 'multipart/form-data'
    },
  });
  return response.data;
}

export async function fetchGetAllMessages(chatId, page, size) {
  try {
    const response = await axios.get(`/api/chat/${chatId}/messages`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
      params: {
        page,
        size,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Get all messages failed:", error);
    throw error;
  }
}

export async function fetchDownloadMessageFile(chatId, messageId, fileId, size) {
  try {
    const response = await axios.get(
      `/api/chat/${chatId}/message/${messageId}/file/${fileId}`,
      {
        headers: {
          'Authorization': `Bearer ${authStore.state.accessToken}`,
          'X-Session-Id': authStore.state.sessionId
        },
        params: {
          size: size
        },
        responseType: 'blob',
      }
    );

    const contentType = response.headers['content-type'] || 'application/octet-stream';
    const blob = new Blob([response.data], { type: contentType });
    return URL.createObjectURL(blob);
  } catch (error) {
    console.error('Download message file failed:', error);
    return PhotoDefaultPlaceholder;
  }
}

export async function fetchReadMessage(chatId, messageId) {
  try {
    const response = await axios.patch(
      `/api/chat/${chatId}/message/${messageId}/read`,
      null,
      {
        headers: {
          'Authorization': `Bearer ${authStore.state.accessToken}`,
          'X-Session-Id': authStore.state.sessionId
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Read message failed:", error);
    throw error;
  }
}

export async function fetchUpdateMessageContent(chatId, messageId, newContent) {
  try {
    const response = await axios.patch(
      `/api/chat/${chatId}/message/${messageId}/content`,
      {
        newContent: newContent
      },
      {
        headers: {
          'Authorization': `Bearer ${authStore.state.accessToken}`,
          'X-Session-Id': authStore.state.sessionId,
          "Content-Type": "application/json",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Update message content failed:", error);
    throw error;
  }
}

export async function fetchDeleteMessage(chatId, messageId) {
  try {
    const response = await axios.delete(`/api/chat/${chatId}/message/${messageId}`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete message failed:", error);
    throw error;
  }
}