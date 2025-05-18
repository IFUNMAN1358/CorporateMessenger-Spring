import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchCreateMessage(chatId, content, file = null) {
  try {
    const formData = new FormData();
    if (content) formData.append("content", content);
    if (file) formData.append("file", file);

    const response = await axios.post(
      `/api/chat/${chatId}/message`,
      formData,
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Create message failed:", error);
    throw error;
  }
}

export async function fetchGetAllMessages(chatId, page, size) {
  try {
    const response = await axios.get(`/api/chat/${chatId}/messages`, {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
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
    const response = await axios.get(`/api/chat/${chatId}/message/${messageId}/file/${fileId}`, {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
      params: {
        size,
      },
      responseType: "blob"
    });
    if (response.data && response.data.size > 0) {
      return URL.createObjectURL(response.data);
    }
    return null;
  } catch (error) {
    console.error("Download message file failed:", error);
    throw error;
  }
}

export async function fetchReadMessage(chatId, messageId) {
  try {
    const response = await axios.patch(
      `/api/chat/${chatId}/message/${messageId}/read`,
      null,
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Read message failed:", error);
    throw error;
  }
}

export async function fetchUpdateMessageContent(chatId, messageId, content) {
  try {
    const response = await axios.patch(
      `/api/chat/${chatId}/message/${messageId}/content`,
      { content },
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
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
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete message failed:", error);
    throw error;
  }
}