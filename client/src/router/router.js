import { createRouter, createWebHistory } from 'vue-router';
import authStore from "@/store/authStore";
import LoginComponent from "@/pages/LoginComponent.vue";
import RegisterComponent from "@/pages/RegisterComponent.vue";
import ProfileComponent from "@/pages/ProfileComponent.vue";
import DashboardComponent from "@/pages/DashboardComponent.vue";
import ChatComponent from "@/pages/ChatComponent.vue";
import MainGroupChatComponent from "@/pages/GroupChatComponent.vue";
import ProfileSettingsComponent from "@/pages/ProfileSettingsComponent.vue";
import EmployeeComponent from "@/pages/EmployeeComponent.vue";

const routes = [

    {path: '/', name: "Login", component: LoginComponent},
    {path: '/register', name: "Register", component: RegisterComponent},

    {path: '/profile/:id', name: "Profile", component: ProfileComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/profile/settings', name: "ProfileSettings", component: ProfileSettingsComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/profile/:id/employee', name: "Employee", component: EmployeeComponent, meta: { requiresRole: 'ROLE_USER' }},

    {path: '/dashboard', name: "Dashboard", component: DashboardComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/chat/:id', name: "Chat", component: ChatComponent, meta: { requiresRole: 'ROLE_USER' }},

    {path: '/chat/group/view/:id', name: "GroupChat", component: MainGroupChatComponent, meta: { requiresRole: 'ROLE_USER' }},

];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {

    const isAuthenticated =
        authStore.state.accessToken && !authStore.actions.isTokenExpired(authStore.state.accessToken);

    if (isAuthenticated) {
        const requiresRole = to.meta.requiresRole;

        if (requiresRole && !authStore.actions.hasRole(requiresRole)) {
            return next({ name: 'Login' });
        }

        if (to.name === 'Login' || to.name === 'Register') {
            return next({ name: 'Dashboard' });
        }

        return next();
    }

    if (!isAuthenticated && to.meta.requiresRole) {
        return next({ name: 'Login' });
    }

    next();
});


export default router;