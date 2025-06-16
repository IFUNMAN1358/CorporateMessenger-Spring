import axios from "@/api/axios";
import authStore from "@/store/authStore";

export async function fetchGetMyEmployee() {
    try {
        const response = await axios.get(
            `/api/user/employee`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`,
                    'X-Session-Id': authStore.state.sessionId
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get your employee data failed:', error);
        throw error;
    }
}

export async function fetchGetEmployeeByUserId(userId) {
    try {
        const response = await axios.get(
            `/api/user/${userId}/employee`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`,
                    'X-Session-Id': authStore.state.sessionId
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Get employee data by userId failed:', error);
        throw error;
    }
}

export async function fetchUpdateMyEmployee(data) {
    try {
        const response = await axios.patch(
            `/api/user/employee`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`,
                    'X-Session-Id': authStore.state.sessionId
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Update my employee data failed:', error);
        throw error;
    }
}

export async function fetchClearMyEmployee() {
    try {
        const response = await axios.delete(
            `/api/user/employee`,
            {
                headers: {
                    'Authorization': `Bearer ${authStore.state.accessToken}`,
                    'X-Session-Id': authStore.state.sessionId
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error('Clear my employee data failed:', error);
        throw error;
    }
}