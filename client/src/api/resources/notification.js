import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchGetNotifications(category, page, size) {
  try {
    const response = await axios.get("/api/user/notifications", {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
      params: {
        category,
        page,
        size,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Fetch get notifications failed:", error);
    throw error;
  }
}

export async function fetchProcessNotificationByNotId(notificationId) {
  try {
    const response = await axios.patch(
      `/api/user/notification/${notificationId}/process`,
      null,
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Process notification by ID failed:", error);
    throw error;
  }
}

export async function fetchReadNotificationByNotId(notificationId) {
  try {
    const response = await axios.patch(
      `/api/user/notification/${notificationId}/read`,
      null,
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Read notification by ID failed:", error);
    throw error;
  }
}

export async function fetchReadAllNotifications() {
  try {
    const response = await axios.patch(
      "/api/user/notifications/read-all",
      null,
      {
        headers: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Read all notifications failed:", error);
    throw error;
  }
}

export async function fetchDeleteNotificationByNotId(notificationId) {
  try {
    const response = await axios.delete(`/api/user/notification/${notificationId}`, {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete notification by ID failed:", error);
    throw error;
  }
}

export async function fetchDeleteAllNotifications() {
  try {
    const response = await axios.delete("/api/user/notifications", {
      headers: {
        Authorization: `Bearer ${authStore.state.accessToken}`,
      },
    });
    console.info(response.data);
    return response.data;
  } catch (error) {
    console.error("Delete all notifications failed:", error);
    throw error;
  }
}