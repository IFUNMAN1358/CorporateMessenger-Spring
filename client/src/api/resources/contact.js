import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchAddContactByUserId(userId) {
  try {
    const response = await axios.post(
      `/api/user/${userId}/contact`,
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
    console.error("Add contact by user ID failed:", error);
    throw error;
  }
}

export async function fetchFindContactByUserId(userId) {
  try {
    const response = await axios.get(`/api/user/${userId}/contact`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    return response.data;
  } catch (error) {
    console.error("Find contact by user ID failed:", error);
    throw error;
  }
}

export async function fetchFindAllMyContactUsers(page, size) {
  try {
    const response = await axios.get("/api/user/contacts", {
      params: {
        page: page,
        size: size
      },
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    return response.data;
  } catch (error) {
    console.error("Find all my contact users failed:", error);
    throw error;
  }
}

export async function fetchFindAllContactUsersByUserId(userId, page, size) {
  try {
    const response = await axios.get(`/api/user/${userId}/contacts`, {
      params: {
        page: page,
        size: size
      },
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    return response.data;
  } catch (error) {
    console.error("Find all contact users by userId failed:", error);
    throw error;
  }
}

export async function fetchConfirmContactByUserId(userId) {
  try {
    const response = await axios.patch(
      `/api/user/${userId}/contact/confirm`,
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
    console.error("Confirm contact by user ID failed:", error);
    throw error;
  }
}

export async function fetchRejectContactByUserId(userId) {
  try {
    const response = await axios.patch(
      `/api/user/${userId}/contact/reject`,
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
    console.error("Reject contact by user ID failed:", error);
    throw error;
  }
}

export async function fetchDeleteContactByUserId(userId) {
  try {
    const response = await axios.delete(`/api/user/${userId}/contact`, {
      headers: {
        'Authorization': `Bearer ${authStore.state.accessToken}`,
        'X-Session-Id': authStore.state.sessionId
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete contact by user ID failed:", error);
    throw error;
  }
}