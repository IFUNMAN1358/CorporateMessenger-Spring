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
                @click.stop="goToProfile(user.id)"
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
            <div v-if="isLoading" class="loading">
              <div class="spinner"></div>
              <p>Загрузка...</p>
            </div>
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
          <li @click="goToProfile()">Профиль</li>
          <li @click="openCreateGroupChatModal">Создать групповой чат</li>
          <li @click="logout">Выход</li>
        </ul>
      </div>
    </transition>

    <div class="dialogs-list">

      <div v-if="isChatsLoading" class="loading">
        <div class="spinner"></div>
        <p>Загрузка чатов...</p>
      </div>
      <template v-else>

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
      </template>
    </div>

    <div
      v-if="isCreateGroupChatModalOpen"
      class="modal-overlay"
      @click="closeCreateGroupChatModal"
    >
      <div class="modal-content" @click.stop>
        <div class="form-group">
          <label for="groupChatTitle">Название чата</label>
          <input
            type="text"
            id="groupChatTitle"
            v-model="groupChatData.title"
            @input="validateField('title')"
            :class="{ 'input-error': errors.title }"
            placeholder="Введите название чата"
          />
          <span v-if="errors.title" class="error">{{ errors.title }}</span>
        </div>
        <div class="form-group">
          <label for="groupChatUsername">Уникальное имя чата</label>
          <input
            type="text"
            id="groupChatUsername"
            v-model="groupChatData.username"
            @input="validateField('username')"
            :class="{ 'input-error': errors.username }"
            :disabled="isUsernameChecking"
            placeholder="Введите уникальное имя"
          />
          <span v-if="isUsernameChecking" class="checking">Проверка...</span>
          <span v-else-if="errors.username" class="error">{{
            errors.username
          }}</span>
        </div>
        <div class="modal-actions">
          <button
            class="modal-button upload-button"
            @click="createGroupChat"
            :disabled="hasGroupChatErrors || isUpdating || isUsernameChecking"
          >
            Создать
          </button>
          <button
            class="modal-button cancel-button"
            @click="closeCreateGroupChatModal"
          >
            Отмена
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import router from '@/router/router';
import { fetchLogout } from '@/api/resources/auth';
import { fetchCreateGroupChat, fetchExistsGroupChatByUsername, fetchGetAllChats } from '@/api/resources/chat';
import { fetchDownloadMainUserPhotoByUserId } from '@/api/resources/userPhoto';
import { fetchDownloadMainGroupChatPhotoByChatId } from '@/api/resources/chatPhoto';
import { fetchSearchUsersByUsername } from '@/api/resources/user';
import GroupChatDefaultAvatar from '@/assets/images/GroupChatDefaultAvatar.png';
import UserDefaultAvatar from '@/assets/images/UserDefaultAvatar.png';
import { jwtDecode } from "jwt-decode";
import authStore from "@/store/authStore";

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
      isChatsLoading: false,
      errorMessage: '',
      UserDefaultAvatar,
      GroupChatDefaultAvatar,
      menuTop: 0,
      menuLeft: 0,
      lastSearchQuery: '',
      isCreateGroupChatModalOpen: false,
      isUpdating: false,
      isUsernameChecking: false,
      groupChatData: {
        title: '',
        username: '',
      },
      errors: {
        title: '',
        username: '',
      },
    };
  },
  computed: {
    showSearchDropdown() {
      return (
        (this.searchQuery.trim().length >= 2) ||
        this.searchResults.length > 0 ||
        this.isLoading ||
        this.errorMessage
      );
    },
    hasGroupChatErrors() {
      return (
        !!this.errors.title ||
        !!this.errors.username ||
        !this.groupChatData.title.trim() ||
        !this.groupChatData.username.trim() ||
        this.isUsernameChecking
      );
    },
  },
  created() {
    this.debouncedSearch = debounce(this.handleSearch, 300);
    this.validateField = debounce(this.validateField, 500);
  },
  mounted() {
    this.fetchChats();
  },
  methods: {
    async fetchChats() {
      try {
        this.isChatsLoading = true;
        const chats = await fetchGetAllChats();

        const privateChats = [];
        const groupChats = [];

        await Promise.all(
          chats.map(async (chatData) => {
            let photoUrl = null;

            if (chatData.chat.type === 'PRIVATE') {
              if (chatData.photo?.id && chatData.partner?.id) {
                try {
                  photoUrl = await fetchDownloadMainUserPhotoByUserId(chatData.partner.id, 'small');
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
        this.errorMessage = '';
      } catch (error) {
        console.error('Ошибка при загрузке чатов:', error);
        this.errorMessage = 'Не удалось загрузить чаты';
      } finally {
        this.isChatsLoading = false;
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
      const currentQuery = this.searchQuery.trim();

      if (!currentQuery || currentQuery.length < 2) {
        this.searchResults = [];
        this.page = 1;
        this.totalPages = 1;
        this.errorMessage = '';
        this.lastSearchQuery = '';
        return;
      }

      if (!isLoadMore || currentQuery !== this.lastSearchQuery) {
        this.searchResults = [];
        this.page = 1;
        this.totalPages = 1;
        this.lastSearchQuery = currentQuery;
      }

      if (this.isLoading || this.page > this.totalPages) return;

      this.isLoading = true;
      this.errorMessage = '';

      try {
        const apiPage = this.page - 1;
        const response = await fetchSearchUsersByUsername(
          currentQuery,
          apiPage,
          this.limit
        );

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

        this.searchResults = isLoadMore
          ? [...this.searchResults, ...newResults.filter((user) => user !== null)]
          : newResults.filter((user) => user !== null);

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
        this.lastSearchQuery = '';
      }
    },
    openCreateGroupChatModal() {
      this.isCreateGroupChatModalOpen = true;
      this.isMenuOpen = false;
      this.clearErrors();
      this.groupChatData = { title: '', username: '' };
    },
    closeCreateGroupChatModal() {
      this.isCreateGroupChatModalOpen = false;
      this.clearErrors();
      this.isUsernameChecking = false;
    },
    validateField(field) {
      this.errors[field] = '';

      if (field === 'title') {
        const value = this.groupChatData.title.trim();
        if (!value) {
          this.errors.title = 'Название чата не может быть пустым';
          return;
        }
        if (value.length < 1 || value.length > 128) {
          this.errors.title = 'Название чата должно содержать от 1 до 128 символов';
          return;
        }
      }

      if (field === 'username') {
        const value = this.groupChatData.username.trim();

        if (!value) {
          this.errors.username = 'Уникальное имя чата не может быть пустым';
          return;
        }

        if (value.length < 5 || value.length > 32) {
          this.errors.username = 'Уникальное имя чата должно содержать от 5 до 32 символов';
          return;
        }

        if (!/^[a-zA-Z]/.test(value)) {
          this.errors.username = 'Уникальное имя чата должно начинаться с буквы';
          return;
        }

        if (value.startsWith('_')) {
          this.errors.username = 'Уникальное имя чата не должно начинаться с подчеркивания';
          return;
        }

        if (!/^[a-zA-Z0-9_]*$/.test(value)) {
          this.errors.username = 'Уникальное имя чата должно содержать только буквы, цифры и подчеркивания';
          return;
        }

        if (!/[a-zA-Z0-9]$/.test(value)) {
          this.errors.username = 'Уникальное имя чата должно заканчиваться буквой или цифрой';
          return;
        }

        if (value.includes('__')) {
          this.errors.username = 'Уникальное имя чата не должно содержать последовательные подчеркивания';
          return;
        }

        this.checkUsernameAvailability(value);
      }
    },
    async checkUsernameAvailability(username) {
      this.isUsernameChecking = true;
      try {
        const exists = await fetchExistsGroupChatByUsername(username);
        if (exists) {
          this.errors.username = 'Уникальное имя чата уже занято';
        }
      } catch (error) {
        console.error('Ошибка проверки имени чата:', error);
        this.errors.username = 'Не удалось проверить уникальное имя чата';
      } finally {
        this.isUsernameChecking = false;
      }
    },
    async createGroupChat() {
      this.validateField('title');
      this.validateField('username');

      if (this.hasGroupChatErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { title, username } = this.groupChatData;
        const response = await fetchCreateGroupChat(title.trim(), username.trim());

        // Добавляем новый чат в groupChats
        this.groupChats.push({
          chat: response.chat,
          photoUrl: this.GroupChatDefaultAvatar,
          lastMessage: null,
          unreadMessage: { unreadCount: 0 },
        });

        this.closeCreateGroupChatModal();
        router.push({ name: 'Chat', params: { id: response.chat.id } });
      } catch (error) {
        console.error('Ошибка создания группового чата:', error);
        this.handleErrors(error, ['title', 'username']);
      } finally {
        this.isUpdating = false;
      }
    },
    clearErrors() {
      Object.keys(this.errors).forEach((key) => {
        this.errors[key] = '';
      });
    },
    handleErrors(error, fields) {
      if (error.response && error.response.data) {
        const apiError = error.response.data;
        if (apiError.validationErrors) {
          Object.keys(apiError.validationErrors).forEach((key) => {
            if (key in this.errors) {
              this.errors[key] = apiError.validationErrors[key];
            } else {
              console.warn(
                `Необработанная ошибка валидации для поля ${key}: ${apiError.validationErrors[key]}`
              );
            }
          });
        } else {
          this.errors[fields[0]] = apiError.message || 'Произошла ошибка';
        }
      } else {
        this.errors[fields[0]] = error.message || 'Произошла ошибка';
      }
    },
    goToChat(chatId) {
      router.push({ name: 'Chat', params: { id: chatId } }).catch(() => {});
    },
    goToProfile(userId = null) {
      try {
        const id = userId || jwtDecode(authStore.state.accessToken).sub;
        router.push({ name: 'Profile', params: { id } }).catch(() => {});

        if (userId) {
          this.searchResults = [];
          this.searchQuery = '';
          this.page = 1;
          this.lastSearchQuery = '';
        }
      } catch (error) {
        console.error('Ошибка при переходе на профиль:', error);
        this.errorMessage = 'Не удалось открыть профиль';
      }
    },
    async logout() {
      try {
        await fetchLogout();
        router.push({ name: 'Login' }).catch(() => {});
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

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #f9f8f8;
  font-size: 14px;
  padding: 10px;
}

.loading p {
  margin-top: 0.5rem;
}

.spinner {
  width: 30px;
  height: 30px;
  border: 4px solid #f9f8f8;
  border-top: 4px solid transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

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

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #2c2c2c;
  padding: 1.5rem;
  border-radius: 1rem;
  color: #f9f8f8;
  width: 90%;
  max-width: 28rem;
  min-width: 18rem;
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.2);
  box-sizing: border-box;
}

.modal-content h3 {
  font-size: clamp(1.25rem, 4vw, 1.5rem);
  font-weight: bold;
  margin-bottom: 1rem;
  text-align: center;
}

.form-group {
  margin-bottom: 1rem;
  text-align: left;
}

.form-group label {
  color: #aaaaaa;
  display: block;
  font-weight: bold;
  font-size: clamp(0.875rem, 3vw, 1rem);
  margin-bottom: 0.5rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #555;
  border-radius: 0.5rem;
  background-color: #3a3a3a;
  color: #f9f8f8;
  box-sizing: border-box;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.form-group input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-group input.input-error {
  border-color: #e53935;
}

.form-group input:focus {
  outline: none;
  border-color: #888888;
}

.error {
  color: #e53935;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  margin-top: 0.25rem;
  display: block;
}

.checking {
  color: #aaaaaa;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  margin-top: 0.25rem;
  display: block;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
}

.modal-button {
  width: 48%;
  padding: 0.75rem;
  font-size: clamp(0.875rem, 3vw, 1rem);
  font-weight: bold;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.modal-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.upload-button {
  background-color: #6b7280;
  color: #f9f8f8;
}

.upload-button:hover {
  background-color: #9ca3af;
}

.cancel-button {
  background-color: #4b5563;
  color: #f9f8f8;
}

.cancel-button:hover {
  background-color: #6b7280;
}

@media (max-width: 600px) {
  .dialogs-container {
    width: 100%;
  }

  .modal-content {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  .modal-content h3 {
    font-size: clamp(1.25rem, 6vw, 1.5rem);
  }

  .form-group input,
  .modal-button {
    padding: 0.6rem;
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .form-group label {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .error,
  .checking {
    font-size: clamp(0.625rem, 3vw, 0.75rem);
  }
}

@media (min-width: 601px) and (max-width: 1024px) {
  .dialogs-container {
    width: 85%;
  }

  .modal-content {
    width: 85%;
    max-width: 35rem;
    padding: 1.75rem;
  }

  .modal-content h3 {
    font-size: clamp(1.5rem, 4vw, 1.75rem);
  }

  .form-group input,
  .modal-button {
    padding: 0.7rem;
  }
}

@media (min-width: 1025px) {
  .dialogs-container {
    max-width: 800px;
  }

  .modal-content {
    max-width: 30rem;
    padding: 2rem;
  }

  .modal-content h3 {
    font-size: clamp(1.75rem, 3vw, 2rem);
  }

  .form-group input,
  .modal-button {
    padding: 0.8rem;
  }
}
</style>