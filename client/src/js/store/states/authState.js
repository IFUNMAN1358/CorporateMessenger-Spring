import {reactive} from "vue";

const authState = reactive({
    accessToken: null
});

export default authState;