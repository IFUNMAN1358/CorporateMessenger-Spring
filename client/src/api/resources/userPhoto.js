import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchUploadMyUserPhoto(formData) {
  try {
    const response = await axios.post(
        `/api/user/photo`,
        formData,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'X-Session-Id': authStore.state.sessionId,
                'Content-Type': 'multipart/form-data'
            }
        }
    );
    return response.data;
  } catch (error) {
    console.error('Upload my user photo failed:', error);
    throw error;
  }
}

export async function fetchDownloadMyMainUserPhoto(size) {
  try {
    const response = await axios.get(
        `/api/user/photo/main`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'X-Session-Id': authStore.state.sessionId
            },
            params: {
                size
            },
            responseType: 'blob'
        }
    );
    if (response.data && response.data.size > 0) {
      return URL.createObjectURL(response.data);
    }
    return null;
  } catch (error) {
    console.error('Download my main user photo failed:', error);
    throw error;
  }
}

export async function fetchDownloadMyUserPhotoByPhotoId(photoId, size) {
  try {
    const response = await axios.get(
        `/api/user/photo/${photoId}`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'X-Session-Id': authStore.state.sessionId
            },
            params: {
                size
            },
            responseType: 'blob'
        }
    );
    if (response.data && response.data.size > 0) {
      return URL.createObjectURL(response.data);
    }
    return null;
  } catch (error) {
    console.error('Download my user photo by photoId failed:', error);
    throw error;
  }
}

export async function fetchDownloadMainUserPhotoByUserId(userId, size) {
  try {
    const response = await axios.get(
      `/api/user/${userId}/photo/main`,
      {
        headers: {
            'Authorization': `Bearer ${authStore.state.accessToken}`,
            'X-Session-Id': authStore.state.sessionId
        },
        params: {
          size
        },
        responseType: 'blob'
      }
    );
    if (response.data && response.data.size > 0) {
      return URL.createObjectURL(response.data);
    }
    return null;
  } catch (error) {
    console.error('Download main user photo by userId failed:', error);
    return null;
  }
}

export async function fetchDownloadUserPhotoByUserIdAndPhotoId(userId, photoId, size) {
  try {
    const response = await axios.get(
        `/api/user/${userId}/photo/${photoId}`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'X-Session-Id': authStore.state.sessionId
            },
            params: {
                size
            },
            responseType: 'blob'
        }
    );
    if (response.data && response.data.size > 0) {
      return URL.createObjectURL(response.data);
    }
    return null;
  } catch (error) {
    console.error('Download user photo by userId and photoId failed:', error);
    throw error;
  }
}

export async function fetchSetMyMainUserPhotoByPhotoId(photoId) {
  try {
    const response = await axios.patch(
        `/api/user/photo/${photoId}`,
        null,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'X-Session-Id': authStore.state.sessionId
            }
        }
    );
    return response.data;
  } catch (error) {
    console.error('Set my main user photo by photoId failed:', error);
    throw error;
  }
}

export async function fetchDeleteMyUserPhotoByPhotoId(photoId) {
  try {
    const response = await axios.delete(
        `/api/user/photo/${photoId}`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`,
                'X-Session-Id': authStore.state.sessionId
            }
        }
    );
    console.info(response.data.message);
  } catch (error) {
    console.error('Delete my user photo by photoId failed:', error);
    throw error;
  }
}