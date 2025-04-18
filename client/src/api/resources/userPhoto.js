import axios from "@/api/axios";
import authStore from "@/store/auth/authStore";

export async function fetchGetUserPhoto(photoId) {
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

export async function fetchGetMainUserPhotoByUserId(userId) {
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

export async function fetchUploadPhoto(formData) {
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

export async function fetchDeletePhotoById(photoId) {
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

export async function fetchSetMainPhotoById(photoId) {
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