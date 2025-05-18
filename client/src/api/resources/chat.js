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

// private MultipartFile file;
//
// @NotNull(message = "Название чата не может быть null")
// @NotBlank(message = "Название чата не может быть пустым")
// @Size(message = "Название чата должно содержать от 1 до 128 символов", min = 1, max = 128)
// private String title;
//
export async function fetchCreateGroupChat(data) {
  try {
    const response = await axios.post(
      "/api/chat/group",
      data,
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