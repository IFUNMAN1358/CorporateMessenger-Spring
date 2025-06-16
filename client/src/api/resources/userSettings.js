import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchGetMyUserSettings() {
    try {
        const response = await axios.get(
            `/api/user/settings`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`,
                    'X-Session-Id': authStore.state.sessionId
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get my user settings failed:', error);
        throw error;
    }
}

export async function fetchChangeMyUserSettings(
    isConfirmContactRequests,
    contactsVisibility,
    profileVisibility,
    employeeVisibility,
    isSearchable
) {
    try {
        const response = await axios.patch(
            `/api/user/settings`,
            {
                "isConfirmContactRequests": isConfirmContactRequests,
                "contactsVisibility": contactsVisibility,
                "profileVisibility": profileVisibility,
                "employeeVisibility": employeeVisibility,
                "isSearchable": isSearchable
            },
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`,
                    'X-Session-Id': authStore.state.sessionId
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Change user settings failed:', error);
        throw error;
    }
}