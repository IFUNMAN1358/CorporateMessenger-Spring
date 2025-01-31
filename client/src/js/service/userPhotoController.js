import axios from "@/js/config/axios";
import authStore from "@/js/store/stores/authStore";

export async function getUserPhoto(photoId) {
  try {
    const response = await axios.get(
        `/api/user/photo/${photoId}`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            },
            responseType: 'blob'
        }
    );
    return URL.createObjectURL(response.data);
  } catch (error) {
    console.error('Get user photo failed:', error);
  }
}

export async function getMainUserPhotoByUserId(userId) {
  try {
    const response = await axios.get(
        `/api/user/${userId}/photo/main`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            },
            responseType: 'blob'
        }
    );
    return URL.createObjectURL(response.data);
  } catch (error) {
    return null;
  }
}

export async function uploadPhoto(formData) {
  try {
    const response = await axios.post(
        `/api/user/photo`,
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
    console.error('Upload user photo failed:', error);
  }
}

export async function deletePhotoById(photoId) {
  try {
    const response = await axios.delete(
        `/api/user/photo/${photoId}`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            }
        }
    );
    console.info(response.data.message);
  } catch (error) {
    console.error('Delete user photo failed:', error);
  }
}

export async function setMainPhotoById(photoId) {
  try {
    const response = await axios.patch(
        `/api/user/photo/${photoId}`,
        null,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            }
        }
    );
    return response.data;
  } catch (error) {
    console.error('Update user photo failed:', error);
  }
}