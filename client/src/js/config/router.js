import { createRouter, createWebHistory } from 'vue-router';
import RegisterComponent from "@/components/auth/RegisterComponent.vue";
import LoginComponent from "@/components/auth/LoginComponent.vue";
import AuthComponent from "@/components/auth/AuthComponent.vue";
import authStore from "@/js/store/stores/authStore";
import DialogsComponent from "@/components/dialog/DialogsComponent.vue";
import ProfileComponent from "@/components/user/ProfileComponent.vue";
import UserComponent from "@/components/user/UserComponent.vue";
import CreateGroupChatComponent from "@/components/groupChat/CreateGroupChatComponent.vue";
import PrivateChatComponent from "@/components/dialog/PrivateChatComponent.vue";
import GroupChatComponent from "@/components/dialog/GroupChatComponent.vue";
import MainGroupChatComponent from "@/components/groupChat/MainGroupChatComponent.vue";

const routes = [

    {path: '/', name: "Auth", component: AuthComponent},
    {path: '/register', name: "Register", component: RegisterComponent},
    {path: '/login', name: "Login", component: LoginComponent},

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
  const isAuthenticated = authStore.state.accessToken && !authStore.actions.isTokenExpired(authStore.state.accessToken);

  if (isAuthenticated) {
    const requiresRole = to.meta.requiresRole;

    if (requiresRole && !authStore.actions.hasRole(requiresRole)) {
      return next({ name: 'Auth' });
    }

    if (to.name === 'Auth' || to.name === 'Login' || to.name === 'Register') {
      return next({ name: 'Dialogs' });
    }

    return next();
  }

  if (!isAuthenticated && to.meta.requiresRole) {
    return next({ name: 'Auth' });
  }

  next();
});


export default router;