import { createRouter, createWebHistory } from 'vue-router';
import LoginComponent from "@/pages/auth/LoginComponent.vue";
import RegisterComponent from "@/pages/auth/RegisterComponent.vue";
import authStore from "@/store/authStore";
import CreateGroupChatComponent from "@/pages/groupChat/CreateGroupChatComponent.vue";
import Dashboard from "@/pages/dashboard/DashboardComponent.vue";
import ChatComponent from "@/pages/dashboard/ChatComponent.vue";
import MainGroupChatComponent from "@/pages/groupChat/MainGroupChatComponent.vue";
import ProfileComponent from "@/pages/user/ProfileComponent.vue";

const routes = [

    {path: '/', name: "Login", component: LoginComponent},
    {path: '/register', name: "Register", component: RegisterComponent},

    {path: '/profile/:id', name: "Profile", component: ProfileComponent, meta: { requiresRole: 'ROLE_USER' }},

    {path: '/dashboard', name: "Dashboard", component: Dashboard, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/chat/:id', name: "Chat", component: ChatComponent, meta: { requiresRole: 'ROLE_USER' }},

    {path: '/chat/group/create', name: "CreateGroupChat", component: CreateGroupChatComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/chat/group/view/:id', name: "MainGroupChat", component: MainGroupChatComponent, meta: { requiresRole: 'ROLE_USER' }},

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