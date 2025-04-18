import { createRouter, createWebHistory } from 'vue-router';
import ProfileComponent from "@/pages/ProfileComponent.vue";
import UserComponent from "@/pages/UserComponent.vue";
import PrivateChatComponent from "@/pages/PrivateChat.vue";
import GroupChatComponent from "@/pages/GroupChat.vue";
import CreateGroupChatComponent from "@/pages/CreateGroupChatComponent.vue";
import MainGroupChatComponent from "@/pages/GroupChat.vue";
import LoginComponent from "@/pages/auth/Login.vue";
import RegisterComponent from "@/pages/auth/Register.vue";
import DashboardComponent from "@/pages/Dashboard.vue";
import authStore from "@/store/authStore";

const routes = [

    {path: '/', name: "Login", component: LoginComponent},
    {path: '/register', name: "Register", component: RegisterComponent},

    {path: '/profile', name: "Profile", component: ProfileComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/user/:id', name: "User", component: UserComponent, meta: { requiresRole: 'ROLE_USER' }},

    {path: '/dashboard', name: "Dashboard", component: DashboardComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/chat/private/:id', name: "PrivateChat", component: PrivateChatComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/chat/group/:id', name: "GroupChat", component: GroupChatComponent, meta: { requiresRole: 'ROLE_USER' }},

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