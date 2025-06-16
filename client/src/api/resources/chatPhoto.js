import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchUploadGroupChatPhoto(chatId, file) {
  try {
    const formData = new FormData();
    formData.append("file", file);

    const response = await axios.post(
      `/api/chat/group/${chatId}/photo`,
      formData,
      {
        headers: {
          'Authorization': `Bearer ${authStore.state.accessToken}`,
          'X-Session-Id': authStore.state.sessionId,
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Upload group chat photo failed:", error);
    throw error;
  }
}

export async function fetchGetAllGroupChatPhotosByChatId(chatId) {
  try {
    const response = await axios.get(`/api/chat/group/${chatId}/photos`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    return response.data;
  } catch (error) {
    console.error("Get all group chat photos failed:", error);
    throw error;
  }
}

export async function fetchDownloadMainGroupChatPhotoByChatId(chatId, size) {
  try {
    const response = await axios.get(`/api/chat/group/${chatId}/photo/main`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
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
    console.error("Download main group chat photo failed:", error);
    throw error;
  }
}

export async function fetchDownloadGroupChatPhotoByChatIdAndPhotoId(chatId, photoId, size) {
  try {
    const response = await axios.get(`/api/chat/group/${chatId}/photo/${photoId}`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
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
    console.error("Download group chat photo by photo ID failed:", error);
    throw error;
  }
}

export async function fetchSetMainGroupChatPhotoByChatIdAndPhotoId(chatId, photoId) {
  try {
    const response = await axios.patch(
      `/api/chat/group/${chatId}/photo/${photoId}`,
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
    console.error("Set main group chat photo failed:", error);
    throw error;
  }
}

export async function fetchDeleteChatPhotoByChatIdAndPhotoId(chatId, photoId) {
  try {
    const response = await axios.delete(`/api/chat/group/${chatId}/photo/${photoId}`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete group chat photo failed:", error);
    throw error;
  }
}