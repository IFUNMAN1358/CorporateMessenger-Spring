import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchGetGroupChatMembers(chatId, page, size) {
  try {
    const response = await axios.get(`/api/chat/group/${chatId}/members`, {
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
    console.error("Get group chat members failed:", error);
    throw error;
  }
}

export async function fetchGetAvailableUsersToAdding(chatId, page, size) {
  try {
    const response = await axios.get(`/api/chat/group/${chatId}/members/available`, {
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
    console.error("Get available users to add failed:", error);
    throw error;
  }
}

export async function fetchAddMembersToGroupChat(chatId, userIds) {
  try {
    const response = await axios.post(
      `/api/chat/group/${chatId}/members`,
      { userIds },
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
    console.error("Add members to group chat failed:", error);
    throw error;
  }
}

export async function fetchDeleteMembersFromGroupChat(chatId, userIds) {
  try {
    const response = await axios.delete(`/api/chat/group/${chatId}/members`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId,
        "Content-Type": "application/json",
      },
      data: { userIds },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete members from group chat failed:", error);
    throw error;
  }
}

export async function fetchLeaveFromGroupChat(chatId) {
  try {
    const response = await axios.delete(`/api/chat/group/${chatId}/leave`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Leave from group chat failed:", error);
    throw error;
  }
}