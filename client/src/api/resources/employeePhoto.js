import axios from "@/api/axios";
import authStore from "@/store/authStore";

// @NotNull
// private MultipartFile file;
//
export async function fetchUploadOrUpdateMyEmployeePhoto(formData) {
  try {
    const response = await axios.post(
        `/api/user/employee/photo`,
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
    console.error('Upload or update my employee photo failed:', error);
    throw error;
  }
}

export async function fetchDownloadMyEmployeePhoto(size) {
  try {
    const response = await axios.get(
        `/api/user/employee/photo`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            },
            params: {
                size
            },
            responseType: "blob"
        }
    );
    if (response.data && response.data.size > 0) {
      return URL.createObjectURL(response.data);
    }
    return null;
  } catch (error) {
    console.error('Download my employee photo failed:', error);
    throw error;
  }
}

export async function fetchDownloadEmployeePhotoByUserId(userId, size) {
  try {
    const response = await axios.get(
        `/api/user/${userId}/employee/photo`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            },
            params: {
                size
            },
            responseType: "blob"
        }
    );
    if (response.data && response.data.size > 0) {
      return URL.createObjectURL(response.data);
    }
    return null;
  } catch (error) {
    console.error('Download employee photo by userId failed:', error);
    throw error;
  }
}

export async function fetchDeleteMyEmployeePhoto() {
  try {
    const response = await axios.delete(
        `/api/user/employee/photo`,
        {
            headers: {
                'Authorization': `Bearer ${authStore.state.accessToken}`
            }
        }
    );
    console.info(response.data.message);
  } catch (error) {
    console.error('Delete my employee photo failed:', error);
    throw error;
  }
}