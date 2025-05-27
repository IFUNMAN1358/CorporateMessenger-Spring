import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchGetOrCreatePrivateChatByUserId(userId) {
  try {
    const response = await axios.post(
      `/api/user/${userId}/chat/private`,
      null,
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Get or create private chat by user ID failed:", error);
    throw error;
  }
}

export async function fetchCreateGroupChat(title, username) {
  try {
    const response = await axios.post(
      "/api/chat/group",
        {
          title: title,
          username: username
        },
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
          "Content-Type": "application/json",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Create group chat failed:", error);
    throw error;
  }
}

export async function fetchExistsGroupChatByUsername(username) {
  try {
    const response = await axios.get(
      "/api/chat/group/exists-username",
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
          "Content-Type": "application/json",
        },
        params: {
          username: username
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error("Exists group chat by username failed:", error);
    throw error;
  }
}

export async function fetchGetChat(chatId) {
  try {
    const response = await axios.get(`/api/chat/${chatId}`, {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Get chat by ID failed:", error);
    throw error;
  }
}

export async function fetchGetAllChats() {
  try {
    const response = await axios.get("/api/chats", {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Get all chats failed:", error);
    throw error;
  }
}

export async function fetchChangeGroupChatTitle(chatId, title) {
  try {
    await axios.patch(
        `/api/chat/${chatId}/group/title`,
        {
          title: title
        },
        {
          headers: {
            Authorization: `Bearer ${authStore.state.accessToken}`,
          },
        }
      );
  } catch (error) {
    console.error("Change group chat title failed:", error);
    throw error;
  }
}

export async function fetchChangeGroupChatUsername(chatId, username) {
  try {
    await axios.patch(
        `/api/chat/${chatId}/group/username`,
        {
          username: username
        },
        {
          headers: {
            Authorization: `Bearer ${authStore.state.accessToken}`,
          },
        }
      );
  } catch (error) {
    console.error("Change group chat username failed:", error);
    throw error;
  }
}

export async function fetchChangeGroupChatDescription(chatId, description) {
  try {
    await axios.patch(
        `/api/chat/${chatId}/group/description`,
        {
          description: description
        },
        {
          headers: {
            Authorization: `Bearer ${authStore.state.accessToken}`,
          },
        }
      );
  } catch (error) {
    console.error("Change group chat description failed:", error);
    throw error;
  }
}

export async function fetchChangeGroupChatSettings(chatId, joinByRequest, hasHiddenMembers) {
  try {
    await axios.patch(
        `/api/chat/${chatId}/group/settings`,
        {
          joinByRequest: joinByRequest,
          hasHiddenMembers: hasHiddenMembers
        },
        {
          headers: {
            Authorization: `Bearer ${authStore.state.accessToken}`,
          },
        }
      );
  } catch (error) {
    console.error("Change group chat settings failed:", error);
    throw error;
  }
}

export async function fetchDeleteChatByChatId(chatId) {
  try {
    const response = await axios.delete(`/api/chat/${chatId}`, {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete chat by ID failed:", error);
    throw error;
  }
}