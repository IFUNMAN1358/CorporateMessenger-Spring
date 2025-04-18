<template>
  <div class="dialogs-container" @click="handleOutsideClick">
    <header class="dialogs-header">
      <button
        class="menu-button"
        @click.stop="toggleMenu"
        ref="menuButton"
      >☰</button>
      <div class="search-container">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Поиск..."
          class="search-input"
          @input="handleSearch"
        />
        <transition name="fade">
          <ul
            v-if="searchResults.length"
            class="search-dropdown"
            ref="searchDropdown"
          >
            <li
              v-for="user in searchResults"
              :key="user.id"
              class="search-item"
              @click.stop="goToUser(user.id)"
            >
              <img
                :src="user.avatar || defaultAvatar"
                alt="avatar"
                class="user-avatar"
              />
              <div class="user-info">
                <p class="user-name">{{ user.username }}</p>
              </div>
            </li>
          </ul>
        </transition>
      </div>
    </header>

    <transition name="fade">
      <div
        v-if="isMenuOpen"
        class="menu-dropdown active"
        ref="menuDropdown"
        :style="{ top: `${menuTop}px`, left: `${menuLeft}px` }"
      >
        <ul class="menu-list">
          <li @click="goToProfile">Профиль</li>
          <li @click="goToCreateGroupChat">Создать групповой чат</li>
          <li @click="logout">Выход</li>
        </ul>
      </div>
    </transition>

    <div class="dialogs-list">
      <!-- PRIVATE CHATS -->
      <div
        v-for="chatInterface in privateChats"
        :key="chatInterface.privateChat.id"
        class="dialog-item"
        @click="goToPrivateChat(chatInterface.privateChat.id)"
      >
        <img
          :src="chatInterface.secondUser.userProfilePhoto || defaultAvatar"
          alt="avatar"
          class="chatInterface-avatar"
        />
        <div class="chatInterface-info">
          <p class="chatInterface-user-name">{{ chatInterface.secondUser.username }}</p>
          <p class="chatInterface-last-message">
            {{ chatInterface.lastMessage?.content || "Нет сообщений" }}
          </p>
        </div>
        <div class="chatInterface-meta">
          <p class="chatInterface-time">{{ formatTime(chatInterface.lastMessage?.createdAt) }}</p>
          <p v-if="chatInterface.unreadMessage.unreadCount > 0" class="unread-count">
            {{ chatInterface.unreadMessage.unreadCount }}
          </p>
        </div>
      </div>

      <!-- GROUP CHATS -->
      <div
        v-for="chatInterface in groupChats"
        :key="chatInterface.groupChat.id"
        class="dialog-item"
        @click="goToGroupChat(chatInterface.groupChat.id)"
      >
        <img
          :src="chatInterface.groupChat.filePath || groupChatDefaultAvatar"
          alt="avatar"
          class="chatInterface-avatar"
        />
        <div class="chatInterface-info">
          <p class="chatInterface-user-name">{{ chatInterface.groupChat.name }}</p>
          <p class="chatInterface-last-message">
            <template v-if="chatInterface.lastMessage">
              <strong>{{ chatInterface.lastMessageSender.firstName }}: </strong>
              {{ chatInterface.lastMessage.content }}
            </template>
            <span v-else>Нет сообщений</span>
          </p>
        </div>
        <div class="chatInterface-meta">
          <p class="chatInterface-time">{{ formatTime(chatInterface.lastMessage?.createdAt) }}</p>
          <p v-if="chatInterface.unreadMessage.unreadCount > 0" class="unread-count">
            {{ chatInterface.unreadMessage.unreadCount }}
          </p>
        </div>
      </div>

      <p v-if="!privateChats.length && !groupChats.length" class="no-chats-message">
        Нет доступных чатов.
      </p>
    </div>

  </div>
</template>

<script>
import { getUsers } from "@/js/service/userDataController";
import { getUserPhoto } from "@/js/service/userPhotoController";
import defaultAvatar from "@/assets/user/UserDefaultAvatar.png";
import groupChatDefaultAvatar from "@/assets/chatInterface/GroupChatDefaultAvatar.png";
import router from "@/js/config/router";
import { handleLogout } from "@/js/service/authService";
import {getAllPrivateChats} from "@/js/service/privateChatService";
import {getAllGroupChats} from "@/js/service/groupChatController";
import {getGroupChatPhoto} from "@/js/service/groupChatPhotoController";

export default {
  name: "DialogsComponent",
  data() {
    return {
      isMenuOpen: false,
      searchQuery: "",
      searchResults: [],
      privateChats: [],
      groupChats: [],
      page: 0,
      defaultAvatar: defaultAvatar,
      groupChatDefaultAvatar: groupChatDefaultAvatar,
      menuTop: 0,
      menuLeft: 0,
    };
  },
  methods: {
    async fetchPrivateChats() {
      try {
        const chats = await getAllPrivateChats();

        this.privateChats = await Promise.all(
          chats.map(async (chatInterface) => {
            if (chatInterface.secondUser.userProfilePhoto?.id) {
              chatInterface.secondUser.userProfilePhoto = await getUserPhoto(chatInterface.secondUser.userProfilePhoto.id);
            } else {
              chatInterface.secondUser.userProfilePhoto = null;
            }
            return chatInterface;
          })
        );
      } catch (error) {
        console.error("Ошибка при загрузке личных чатов:", error);
      }
    },
    async fetchGroupChats() {
      try {
        const chats = await getAllGroupChats();

        this.groupChats = await Promise.all(
          chats.map(async (chatInterface) => {
            chatInterface.groupChat.filePath = chatInterface.groupChat.filePath
              ? await getGroupChatPhoto(chatInterface.groupChat.id)
              : groupChatDefaultAvatar;

            if (chatInterface.lastMessage) {
              chatInterface.lastMessageSender = {
                ...chatInterface.lastMessageSender,
                userFirstName: chatInterface.lastMessageSender.firstName,
              };
            }
            return chatInterface;
          })
        );
      } catch (error) {
        console.error("Ошибка при загрузке групповых чатов:", error);
      }
    },
    formatTime(isoString) {
      if (!isoString) return "";
      const date = new Date(isoString);
      return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    },
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
      if (this.isMenuOpen) {
        this.$nextTick(() => {
          const button = this.$refs.menuButton.getBoundingClientRect();
          this.menuTop = button.bottom + window.scrollY;
          this.menuLeft = button.left + window.scrollX;
        });
      }
    },
    async handleSearch() {
      if (this.searchQuery.trim() === "") {
        this.searchResults = [];
        return;
      }
      try {
        const users = await getUsers(this.searchQuery, this.page);

        this.searchResults = await Promise.all(
          users.map(async (user) => {
            if (user.userProfilePhoto?.id) {
              user.avatar = await getUserPhoto(user.userProfilePhoto.id);
            } else {
              user.avatar = null;
            }
            return user;
          })
        );
      } catch (error) {
        console.error("Ошибка при поиске пользователей:", error);
      }
    },
    handleOutsideClick(event) {
      const menu = this.$refs.menuDropdown;
      const dropdown = this.$refs.searchDropdown;
      const header = this.$el.querySelector(".dialogs-header");

      if (
        !menu?.contains(event.target) &&
        !dropdown?.contains(event.target) &&
        !header.contains(event.target)
      ) {
        this.isMenuOpen = false;
        this.searchResults = [];
      }
    },
    goToPrivateChat(chatId) {
      router.push({ name: "PrivateChat", params: { id: chatId } }).catch(() => {});
    },
    goToGroupChat(chatId) {
      router.push({ name: "GroupChat", params: { id: chatId } }).catch(() => {});
    },
    goToProfile() {
      router.push({ name: "Profile" }).catch(() => {});
    },
    goToUser(userId) {
      router.push({ name: "User", params: { id: userId } }).catch(() => {});
    },
    goToCreateGroupChat() {
      router.push({ name: "CreateGroupChat" }).catch(() => {});
    },
    async logout() {
      try {
        await handleLogout();
      } catch (error) {
        console.error("Ошибка выхода:", error);
      }
    },
  },
  async mounted() {
    await this.fetchPrivateChats();
    await this.fetchGroupChats();
  },
};
</script>

<style scoped>
.dialogs-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 800px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: #252525;
  overflow: hidden;
}

.dialogs-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: #333;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.dialog-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dialog-item:hover {
  background-color: #333;
}

.chatInterface-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 15px;
}

.chatInterface-info {
  flex: 1;
  overflow: hidden;
}

.chatInterface-user-name {
  font-size: 16px;
  font-weight: bold;
  color: #F9F8F8;
  margin: 0;
}

.chatInterface-last-message {
  font-size: 14px;
  color: #888;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}

.chatInterface-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.chatInterface-time {
  font-size: 12px;
  color: #888;
}

.unread-count {
  background-color: #e74c3c;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
  padding: 3px 6px;
  border-radius: 12px;
  margin-top: 5px;
}

.no-chats-message {
  text-align: center;
  color: #888;
  margin-top: 20px;
  font-size: 16px;
}

.menu-button {
  font-size: 24px;
  color: #F9F8F8;
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

.menu-button:hover {
  color: #888;
}

.search-container {
  display: flex;
  position: relative;
  flex: 1;
  margin-left: 20px;
}

.search-input {
  width: 100%;
  padding: 10px 15px;
  font-size: 16px;
  color: #F9F8F8;
  background-color: #444;
  border: none;
  border-radius: 8px;
  outline: none;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s ease;
}

.search-input:focus {
  background-color: #555;
}

.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #2C2C2C;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
  z-index: 10;
  overflow: hidden;
  max-height: 300px;
  overflow-y: auto;
  padding-left: 0px;
}

.search-item {
  display: flex;
  align-items: center;
  padding: 8px 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.search-item:hover {
  background-color: #3A3A3A;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
  background-color: #444;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: bold;
  color: #F9F8F8;
}

.menu-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: #2C2C2C;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
  z-index: 10;
  transform: translate(0, 10px);
  opacity: 0;
  pointer-events: none;
}

.menu-dropdown.active {
  opacity: 1;
  pointer-events: auto;
  transform: translate(0, 0);
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-list li {
  padding: 12px 20px;
  color: #F9F8F8;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.menu-list li:hover {
  background-color: #3A3A3A;
}

.dialogs-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.dialogs-list p {
  color: #888;
  text-align: center;
  margin: 0;
  font-size: 16px;
}
</style>
