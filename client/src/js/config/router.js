import { createRouter, createWebHistory } from 'vue-router';
import authStore from "@/js/store/stores/authStore";
import LoginComponent from "@/components/LoginComponent.vue";
import RegisterComponent from "@/components/RegisterComponent.vue";
import ProfileComponent from "@/components/ProfileComponent.vue";
import UserComponent from "@/components/UserComponent.vue";
import DialogsComponent from "@/components/DialogsComponent.vue";
import PrivateChatComponent from "@/components/PrivateChatComponent.vue";
import GroupChatComponent from "@/components/GroupChatComponent.vue";
import CreateGroupChatComponent from "@/components/CreateGroupChatComponent.vue";
import MainGroupChatComponent from "@/components/MainGroupChatComponent.vue";

const routes = [

    {path: '/', name: "Login", component: LoginComponent},
    {path: '/register', name: "Register", component: RegisterComponent},

    {path: '/profile', name: "Profile", component: ProfileComponent, meta: { requiresRole: 'ROLE_USER' }},
    {path: '/user/:id', name: "User", component: UserComponent, meta: { requiresRole: 'ROLE_USER' }},

    {path: '/dialogs', name: "Dialogs", component: DialogsComponent, meta: { requiresRole: 'ROLE_USER' }},
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
            return next({ name: 'Dialogs' });
        }

        return next();
    }

    if (!isAuthenticated && to.meta.requiresRole) {
        return next({ name: 'Login' });
    }

    next();
});


export default router;