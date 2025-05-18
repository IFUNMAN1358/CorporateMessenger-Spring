<template>
  <div class="dialogs-container" @click="handleOutsideClick">
    <header class="dialogs-header">
      <button class="menu-button" @click.stop="toggleMenu" ref="menuButton">
        ☰
      </button>
      <div class="search-container">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Поиск..."
          class="search-input"
          @input="debouncedSearch"
        />
        <transition name="fade">
          <div
            v-if="showSearchDropdown"
            class="search-dropdown"
            ref="searchDropdown"
            @scroll="handleScroll"
          >
            <ul v-if="searchResults.length" class="search-results">
              <li
                v-for="user in searchResults"
                :key="user.id"
                class="search-item"
                @click.stop="goToUser(user.id)"
              >
                <img
                  :src="user.avatar || UserDefaultAvatar"
                  alt="avatar"
                  class="user-avatar"
                />
                <div class="user-info">
                  <p class="user-name">{{ user.username }}</p>
                </div>
              </li>
            </ul>
            <div v-if="isLoading" class="loading">Загрузка...</div>
            <div v-else-if="errorMessage" class="error">{{ errorMessage }}</div>
            <div
              v-else-if="searchQuery && !searchResults.length && !isLoading"
              class="no-results"
            >
              Пользователи не найдены
            </div>
          </div>
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
        v-for="chat in privateChats"
        :key="chat.chat.id"
        class="dialog-item"
        @click="goToChat(chat.chat.id)"
      >
        <img
          :src="chat.photoUrl || UserDefaultAvatar"
          alt="avatar"
          class="chatInterface-avatar"
        />
        <div class="chatInterface-info">
          <p class="chatInterface-user-name">
            {{ chat.partner?.username || "Unknown" }}
          </p>
          <p class="chatInterface-last-message">
            {{ chat.lastMessage?.content || "Нет сообщений" }}
          </p>
        </div>
        <div class="chatInterface-meta">
          <p class="chatInterface-time">
            {{ formatTime(chat.lastMessage?.createdAt) }}
          </p>
          <p
            v-if="chat.unreadMessage?.unreadCount > 0"
            class="unread-count"
          >
            {{ chat.unreadMessage.unreadCount }}
          </p>
        </div>
      </div>

      <!-- GROUP CHATS -->
      <div
        v-for="chat in groupChats"
        :key="chat.chat.id"
        class="dialog-item"
        @click="goToChat(chat.chat.id)"
      >
        <img
          :src="chat.photoUrl || GroupChatDefaultAvatar"
          alt="avatar"
          class="chatInterface-avatar"
        />
        <div class="chatInterface-info">
          <p class="chatInterface-user-name">
            {{ chat.chat.title || "Group Chat" }}
          </p>
          <p class="chatInterface-last-message">
            <template v-if="chat.lastMessage">
              <strong>{{ chat.lastMessage.senderUsername }}: </strong>
              {{ chat.lastMessage.content }}
            </template>
            <span v-else>Нет сообщений</span>
          </p>
        </div>
        <div class="chatInterface-meta">
          <p class="chatInterface-time">
            {{ formatTime(chat.lastMessage?.createdAt) }}
          </p>
          <p
            v-if="chat.unreadMessage?.unreadCount > 0"
            class="unread-count"
          >
            {{ chat.unreadMessage.unreadCount }}
          </p>
        </div>
      </div>

      <p
        v-if="!privateChats.length && !groupChats.length"
        class="no-chats-message"
      >
        Нет доступных чатов.
      </p>
    </div>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import router from '@/router/router';
import { fetchLogout } from '@/api/resources/auth';
import { fetchGetAllChats } from '@/api/resources/chat';
import {
  fetchDownloadMainUserPhotoByUserId,
  fetchDownloadMyMainUserPhoto,
} from '@/api/resources/userPhoto';
import { fetchDownloadMainGroupChatPhotoByChatId } from '@/api/resources/chatPhoto';
import { fetchSearchUsersByUsername } from '@/api/resources/user';
import GroupChatDefaultAvatar from '@/assets/images/GroupChatDefaultAvatar.png';
import UserDefaultAvatar from '@/assets/images/UserDefaultAvatar.png';

export default {
  name: 'DashboardComponent',
  data() {
    return {
      isMenuOpen: false,
      searchQuery: '',
      searchResults: [],
      privateChats: [],
      groupChats: [],
      page: 1,
      limit: 10,
      totalPages: 1,
      isLoading: false,
      errorMessage: '',
      UserDefaultAvatar,
      GroupChatDefaultAvatar,
      menuTop: 0,
      menuLeft: 0,
    };
  },
  computed: {
    showSearchDropdown() {
      return this.searchQuery.trim() || this.searchResults.length || this.isLoading || this.errorMessage;
    },
  },
  created() {
    this.debouncedSearch = debounce(this.handleSearch, 300);
  },
  mounted() {
    this.fetchChats();
  },
  methods: {
    async fetchChats() {
      try {
        const chats = await fetchGetAllChats();

        const privateChats = [];
        const groupChats = [];

        await Promise.all(
            chats.map(async (chatData) => {
              let photoUrl = null;

              if (chatData.chat.type === 'PRIVATE') {
                if (chatData.photo?.id && chatData.partner?.id) {
                  try {
                    photoUrl = await fetchDownloadMyMainUserPhoto('small');
                  } catch (error) {
                    console.error(`Failed to load photo for user ${chatData.partner.id}:`, error);
                  }
                }
                privateChats.push({
                  chat: chatData.chat,
                  partner: chatData.partner,
                  photoUrl,
                  lastMessage: chatData.lastMessage,
                  unreadMessage: chatData.unreadMessage,
                });
              } else if (chatData.chat.type === 'GROUP') {
                if (chatData.photo?.id) {
                  try {
                    photoUrl = await fetchDownloadMainGroupChatPhotoByChatId(
                        chatData.chat.id,
                        'small'
                    );
                  } catch (error) {
                    console.error(`Failed to load photo for chat ${chatData.chat.id}:`, error);
                  }
                }
                groupChats.push({
                  chat: chatData.chat,
                  photoUrl,
                  lastMessage: chatData.lastMessage,
                  unreadMessage: chatData.unreadMessage,
                });
              }
            })
        );

        this.privateChats = privateChats;
        this.groupChats = groupChats;
      } catch (error) {
        console.error('Ошибка при загрузке чатов:', error);
        this.errorMessage = 'Не удалось загрузить чаты';
      }
    },
    formatTime(isoString) {
      if (!isoString) return '';
      const date = new Date(isoString);
      return date.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
      });
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
    async handleSearch(isLoadMore = false) {
      if (!this.searchQuery.trim() || this.searchQuery.trim().length < 2) {
        this.searchResults = [];
        this.page = 1;
        this.totalPages = 1;
        this.errorMessage = '';
        return;
      }

      if (!isLoadMore) {
        this.searchResults = [];
        this.page = 1;
        this.totalPages = 1;
      }

      if (this.isLoading || this.page > this.totalPages) return;

      this.isLoading = true;
      this.errorMessage = '';

      try {
        const apiPage = this.page - 1;
        const response = await fetchSearchUsersByUsername(
          this.searchQuery.trim(),
          apiPage,
          this.limit
        );

        console.log('Ответ API:', response);

        let users = [];
        let total = 0;
        if (Array.isArray(response)) {
          users = response;
          total = users.length;
        } else if (response && Array.isArray(response.content)) {
          users = response.content;
          total = response.totalElements || response.totalCount || users.length;
        } else {
          console.error('Неверный формат ответа API:', response);
          this.errorMessage = 'Ошибка сервера';
          this.searchResults = [];
          return;
        }

        this.totalPages = Math.ceil(total / this.limit) || 1;

        const newResults = await Promise.all(
          users.map(async (userResponse) => {
            const user = userResponse.user;
            if (!user || !user.id) {
              console.warn('Неверный объект пользователя:', userResponse);
              return null;
            }
            let avatar = this.UserDefaultAvatar;
            try {
              const avatarUrl = await fetchDownloadMainUserPhotoByUserId(user.id, 'small');
              if (avatarUrl) avatar = avatarUrl;
            } catch (error) {
              console.error(`Не удалось загрузить аватар для пользователя ${user.id}:`, error);
            }
            return {
              id: user.id,
              username: user.username,
              avatar,
            };
          })
        );

        this.searchResults = [
          ...this.searchResults,
          ...newResults.filter((user) => user !== null),
        ];

        this.errorMessage = '';
      } catch (error) {
        console.error('Ошибка при поиске пользователей:', error);
        this.errorMessage = 'Не удалось выполнить поиск';
        if (!isLoadMore) {
          this.searchResults = [];
        }
      } finally {
        this.isLoading = false;
      }
    },
    handleScroll(event) {
      const dropdown = event.target;
      const isAtBottom =
          dropdown.scrollTop + dropdown.clientHeight >= dropdown.scrollHeight - 10;

      if (isAtBottom && !this.isLoading && this.page < this.totalPages) {
        this.page += 1;
        this.handleSearch(true);
      }
    },
    handleOutsideClick(event) {
      const menu = this.$refs.menuDropdown;
      const dropdown = this.$refs.searchDropdown;
      const header = this.$el.querySelector('.dialogs-header');

      if (
          !menu?.contains(event.target) &&
          !dropdown?.contains(event.target) &&
          !header.contains(event.target)
      ) {
        this.isMenuOpen = false;
        this.searchResults = [];
        this.searchQuery = '';
        this.page = 1;
        this.totalPages = 1;
      }
    },
    goToChat(chatId) {
      router.push({name: 'Chat', params: {id: chatId}}).catch(() => {
      });
    },
    goToProfile() {
      router.push({name: 'Profile'}).catch(() => {
      });
    },
    goToUser(userId) {
      router.push({name: 'User', params: {id: userId}}).catch(() => {
      });
      this.searchResults = [];
      this.searchQuery = '';
      this.page = 1;
    },
    goToCreateGroupChat() {
      router.push({name: 'CreateGroupChat'}).catch(() => {
      });
    },
    async logout() {
      try {
        await fetchLogout();
        router.push({name: 'Login'}).catch(() => {
        });
      } catch (error) {
        console.error('Ошибка выхода:', error);
      }
    },
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
  color: #f9f8f8;
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
  color: #f9f8f8;
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
  color: #f9f8f8;
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
  background-color: #2c2c2c;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
  z-index: 10;
  max-height: 300px;
  overflow-y: auto;
  padding: 10px;
}

.search-results {
  list-style: none;
  padding: 0;
  margin: 0;
}

.search-item {
  display: flex;
  align-items: center;
  padding: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.search-item:hover {
  background-color: #3a3a3a;
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
  color: #f9f8f8;
}

.loading,
.error,
.no-results {
  text-align: center;
  color: #f9f8f8;
  font-size: 14px;
  padding: 10px;
}

.error {
  color: #e53935;
}

.menu-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: #2c2c2c;
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
  color: #f9f8f8;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.menu-list li:hover {
  background-color: #3a3a3a;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>