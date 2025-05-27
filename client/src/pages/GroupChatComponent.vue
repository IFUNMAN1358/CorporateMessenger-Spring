<template>
  <div class="group-chat-container">
    <div v-if="isLoading" class="page-loading">
      <div class="spinner"></div>
      <p>Загрузка...</p>
    </div>

    <div v-else>
      <header class="group-chat-header">
        <button class="back-button" @click="goBack">←</button>
        <h1>Групповой чат</h1>
      </header>

      <div v-if="isPhotoLoading" class="photo-loading">
        <div class="spinner"></div>
      </div>
      <div class="photo-slider">
        <div class="photo-wrapper">
          <img
            :src="photos.length ? photos[currentPhotoIndex] : GroupChatDefaultAvatar"
            alt="Chat Photo"
            class="chat-photo"
          />
          <div v-if="photos.length" class="photo-gradient photo-gradient-bottom">
            <p class="photo-counter">
              {{ currentPhotoIndex + 1 }}/{{ photos.length }}
            </p>
          </div>
          <div class="photo-gradient photo-gradient-top"></div>
          <button
            v-if="isCreator"
            class="menu-button"
            @click="toggleMenu"
            ref="menuButton"
          >⋮</button>
          <div v-if="menuVisible && isCreator" class="menu-dropdown" ref="menuDropdown">
            <button @click="openUploadModal">Загрузить фото</button>
            <button v-if="photos.length && !isCurrentPhotoMain" @click="setMainPhoto">Сделать основным</button>
            <button v-if="photos.length" @click="deletePhoto">Удалить фото</button>
          </div>
          <button
            v-if="photos.length > 1"
            @click="prevPhoto"
            class="slider-button slider-prev"
          >‹</button>
          <button
            v-if="photos.length > 1"
            @click="nextPhoto"
            class="slider-button slider-next"
          >›</button>
        </div>
      </div>

      <hr class="divider" />

      <div v-if="chatData.chat" class="group-chat-details">
        <h2 class="group-chat-details-header">Информация о чате</h2>
        <div class="group-chat-detail-item">
          <button v-if="isCreator" class="group-chat-text-button" @click="openTitleModal">
            {{ chatData.chat.title }}
          </button>
          <span v-else class="group-chat-text">{{ chatData.chat.title }}</span>
          <span class="group-chat-detail-label">Название чата</span>
        </div>
        <div class="group-chat-detail-item">
          <button v-if="isCreator" class="group-chat-text-button" @click="openUsernameModal">
            {{ chatData.chat.username }}
          </button>
          <span v-else class="group-chat-text">{{ chatData.chat.username }}</span>
          <span class="group-chat-detail-label">Уникальное имя</span>
        </div>
        <div class="group-chat-detail-item">
          <button v-if="isCreator" class="group-chat-text-button" @click="openDescriptionModal">
            {{ chatData.chat.description || 'Описание чата' }}
          </button>
          <span v-else class="group-chat-text">{{ chatData.chat.description || 'Описание отсутствует' }}</span>
          <span class="group-chat-detail-label">Описание</span>
        </div>
      </div>

      <hr class="divider" />

      <div v-if="chatData.chat" class="group-chat-settings">
        <h2 class="group-chat-details-header">Участники</h2>
        <div>
          <button v-if="chatData.chat && !chatData.chat.hasHiddenMembers" class="group-chat-button" @click="openMembersModal">Список участников</button>
          <button v-if="chatData.chat && isCreator" class="group-chat-button" @click="openAddMembersModal">Добавить участников</button>
        </div>
      </div>

      <hr v-if="isCreator" class="divider" />

      <div v-if="chatData.chat && isCreator" class="group-chat-settings">
        <h2 class="group-chat-details-header">Настройки</h2>
        <div class="settings-item">
          <label class="settings-label">Присоединение по запросу</label>
          <input
            type="checkbox"
            v-model="settingsData.joinByRequest"
            @change="updateSettings"
            :disabled="isUpdating"
          />
        </div>
        <div class="settings-item">
          <label class="settings-label">Скрытые участники</label>
          <input
            type="checkbox"
            v-model="settingsData.hasHiddenMembers"
            @change="updateSettings"
            :disabled="isUpdating"
          />
        </div>
      </div>

      <hr class="divider" />

      <div v-if="chatData.chat" class="group-chat-settings">
        <h2 class="group-chat-details-header">Действия</h2>
        <div>
          <button v-if="isCreator" class="group-chat-button danger" @click="openDeleteModal">Удалить чат</button>
          <button v-if="!isCreator" class="group-chat-button danger" @click="leaveChat">Покинуть чат</button>
        </div>
      </div>

      <div v-if="isUploadModalOpen && isCreator" class="modal-overlay" @click="closeUploadModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="photoUpload">Выберите фото</label>
            <input
              type="file"
              id="photoUpload"
              @change="handleFileChange"
              accept="image/jpeg"
              :class="{ 'input-error': errors.upload }"
            />
            <span v-if="errors.upload" class="error">{{ errors.upload }}</span>
          </div>
          <div class="modal-actions">
            <button class="modal-button upload-button" @click="uploadPhoto" :disabled="isUpdating || !selectedFile">
              Загрузить
            </button>
            <button class="modal-button cancel-button" @click="closeUploadModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isTitleModalOpen && isCreator" class="modal-overlay" @click="closeTitleModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="newTitle">Новое название</label>
            <input
              type="text"
              id="newTitle"
              v-model="titleData.newTitle"
              @input="validateField('newTitle')"
              :class="{ 'input-error': errors.newTitle }"
            />
            <span v-if="errors.newTitle" class="error">{{ errors.newTitle }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="updateTitle"
              :disabled="hasTitleErrors || isUpdating"
            >
              Изменить
            </button>
            <button class="modal-button cancel-button" @click="closeTitleModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isUsernameModalOpen && isCreator" class="modal-overlay" @click="closeUsernameModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="newUsername">Новое уникальное имя</label>
            <input
              type="text"
              id="newUsername"
              v-model="usernameData.newUsername"
              @input="validateField('newUsername')"
              :class="{ 'input-error': errors.newUsername }"
              :disabled="isUsernameChecking"
            />
            <span v-if="isUsernameChecking" class="checking">Проверка...</span>
            <span v-else-if="errors.newUsername" class="error">{{ errors.newUsername }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="updateUsername"
              :disabled="hasUsernameErrors || isUpdating || isUsernameChecking"
            >
              Изменить
            </button>
            <button class="modal-button cancel-button" @click="closeUsernameModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isDescriptionModalOpen && isCreator" class="modal-overlay" @click="closeDescriptionModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="newDescription">Новое описание</label>
            <textarea
              id="newDescription"
              v-model="descriptionData.newDescription"
              rows="4"
              @input="validateField('newDescription')"
              :class="{ 'input-error': errors.newDescription }"
            ></textarea>
            <span v-if="errors.newDescription" class="error">{{ errors.newDescription }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="updateDescription"
              :disabled="hasDescriptionErrors || isUpdating"
            >
              Изменить
            </button>
            <button class="modal-button cancel-button" @click="closeDescriptionModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isDeleteModalOpen && isCreator" class="modal-overlay" @click="closeDeleteModal">
        <div class="modal-content" @click.stop>
          <p>Вы уверены, что хотите удалить чат? Это действие необратимо.</p>
          <div class="modal-actions">
            <button class="modal-button danger" @click="deleteChat" :disabled="isUpdating">Удалить</button>
            <button class="modal-button cancel-button" @click="closeDeleteModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isMembersModalOpen && !chatData.chat.hasHiddenMembers" class="modal-overlay" @click="closeMembersModal">
        <div class="modal-content user-list-modal" @click.stop>
          <h3>Участники</h3>
          <div class="user-list" ref="membersList" @scroll="handleMembersScroll">
            <div
              v-for="member in members"
              :key="member.user.id"
              class="user-item"
              @click="goToProfile(member.user.id)"
            >
              <img
                :src="member.photoUrl || UserDefaultAvatar"
                alt="User Photo"
                class="user-item-photo"
              />
              <span class="user-item-username">{{ member.user.username }}</span>
              <button
                v-if="isCreator && member.user.id !== userId"
                class="user-item-button danger"
                @click.stop="openRemoveMemberModal(member.user.id)"
                :disabled="isUpdating"
              >
                Удалить
              </button>
            </div>
          </div>
          <p v-if="!members.length && !isMembersLoading" class="not-found">Участники не найдены</p>
          <div v-if="isMembersLoading" class="modal-loading">
            <div class="spinner"></div>
            <p>Загрузка...</p>
          </div>
          <div class="modal-actions">
            <button class="modal-button cancel-button centered-button" @click="closeMembersModal">Закрыть</button>
          </div>
        </div>
      </div>

      <div v-if="isAddMembersModalOpen && isCreator" class="modal-overlay" @click="closeAddMembersModal">
        <div class="modal-content user-list-modal" @click.stop>
          <h3>Добавить участников</h3>
          <div class="user-list" ref="addMembersList" @scroll="handleAddMembersScroll">
            <div
              v-for="user in availableUsers"
              :key="user.user.id"
              class="user-item"
              @click="goToProfile(user.user.id)"
            >
              <img
                :src="user.photoUrl || UserDefaultAvatar"
                alt="User Photo"
                class="user-item-photo"
              />
              <span class="user-item-username">{{ user.user.username }}</span>
              <button
                class="user-item-button"
                @click.stop="addMember(user.user.id)"
                :disabled="isUpdating"
              >
                Добавить
              </button>
            </div>
          </div>
          <p v-if="!availableUsers.length && !isAddMembersLoading" class="not-found">Пользователи не найдены</p>
          <div v-if="isAddMembersLoading" class="modal-loading">
            <div class="spinner"></div>
            <p>Загрузка...</p>
          </div>
          <div class="modal-actions">
            <button class="modal-button cancel-button centered-button" @click="closeAddMembersModal">Закрыть</button>
          </div>
        </div>
      </div>

      <div v-if="isRemoveMemberModalOpen && isCreator" class="modal-overlay" @click="closeRemoveMemberModal">
        <div class="modal-content" @click.stop>
          <p>Вы уверены, что хотите удалить участника из чата?</p>
          <div class="modal-actions">
            <button class="modal-button danger" @click="removeMember" :disabled="isUpdating">Удалить</button>
            <button class="modal-button cancel-button" @click="closeRemoveMemberModal">Отмена</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import GroupChatDefaultAvatar from '@/assets/images/GroupChatDefaultAvatar.png';
import UserDefaultAvatar from '@/assets/images/UserDefaultAvatar.png';
import { jwtDecode } from 'jwt-decode';
import authStore from '@/store/authStore';
import { debounce } from 'lodash';
import router from '@/router/router';
import {
  fetchGetChat,
  fetchChangeGroupChatTitle,
  fetchChangeGroupChatUsername,
  fetchChangeGroupChatDescription,
  fetchChangeGroupChatSettings,
  fetchDeleteChatByChatId,
  fetchExistsGroupChatByUsername,
} from '@/api/resources/chat';
import {
  fetchUploadGroupChatPhoto,
  fetchGetAllGroupChatPhotosByChatId,
  fetchDownloadGroupChatPhotoByChatIdAndPhotoId,
  fetchSetMainGroupChatPhotoByChatIdAndPhotoId,
  fetchDeleteChatPhotoByChatIdAndPhotoId,
} from '@/api/resources/chatPhoto';
import { fetchDownloadMainUserPhotoByUserId } from '@/api/resources/userPhoto';
import {
  fetchAddMembersToGroupChat, fetchDeleteMembersFromGroupChat,
  fetchGetAvailableUsersToAdding,
  fetchGetGroupChatMembers, fetchLeaveFromGroupChat
} from "@/api/resources/chatMember";

export default {
  name: 'GroupChat',
  data() {
    return {
      chatId: null,
      chatData: { chat: null, chatPhotos: [] },
      photos: [],
      thumbnailPhotos: [],
      currentPhotoIndex: 0,
      menuVisible: false,
      isUploadModalOpen: false,
      isTitleModalOpen: false,
      isUsernameModalOpen: false,
      isDescriptionModalOpen: false,
      isDeleteModalOpen: false,
      isMembersModalOpen: false,
      isAddMembersModalOpen: false,
      isRemoveMemberModalOpen: false,
      isUpdating: false,
      isUsernameChecking: false,
      isMembersLoading: false,
      isAddMembersLoading: false,
      selectedFile: null,
      titleData: { newTitle: '' },
      usernameData: { newUsername: '' },
      descriptionData: { newDescription: '' },
      settingsData: { joinByRequest: false, hasHiddenMembers: false },
      members: [],
      availableUsers: [],
      membersPage: 0,
      addMembersPage: 0,
      membersPageSize: 20,
      addMembersPageSize: 20,
      hasMoreMembers: true,
      hasMoreAvailableUsers: true,
      errors: {
        upload: '',
        newTitle: '',
        newUsername: '',
        newDescription: '',
      },
      isCreator: false,
      userId: null,
      selectedMemberId: null,
      GroupChatDefaultAvatar,
      UserDefaultAvatar,
      isLoading: true,
      isPhotoLoading: false,
    };
  },
  computed: {
    isCurrentPhotoMain() {
      const currentPhoto = this.chatData.chatPhotos?.[this.currentPhotoIndex];
      return currentPhoto ? currentPhoto.isMain : false;
    },
    hasTitleErrors() {
      return !!this.errors.newTitle || !this.titleData.newTitle.trim();
    },
    hasUsernameErrors() {
      return (
        !!this.errors.newUsername ||
        !this.usernameData.newUsername.trim() ||
        this.isUsernameChecking ||
        this.usernameData.newUsername.trim() === this.chatData.chat?.username
      );
    },
    hasDescriptionErrors() {
      return !!this.errors.newDescription;
    },
  },
  async created() {
    this.chatId = this.$route.params.id;
    this.userId = jwtDecode(authStore.state.accessToken).sub;
    await this.fetchChat();
    document.addEventListener('click', this.closeMenuOnClickOutside);
  },
  unmounted() {
    document.removeEventListener('click', this.closeMenuOnClickOutside);
    this.photos.forEach((photo) => {
      if (photo !== this.GroupChatDefaultAvatar) {
        URL.revokeObjectURL(photo);
      }
    });
    this.thumbnailPhotos.forEach((photo) => {
      if (photo !== this.GroupChatDefaultAvatar) {
        URL.revokeObjectURL(photo);
      }
    });
  },
  methods: {
    async fetchChat() {
      try {
        this.isLoading = true;
        this.isPhotoLoading = true;

        const chatResponse = await fetchGetChat(this.chatId);
        this.chatData = {
          chat: chatResponse.chat || {
            id: '',
            type: 'GROUP',
            username: '',
            title: '',
            description: '',
            joinByRequest: false,
            hasHiddenMembers: false,
            createdAt: '',
            updatedAt: '',
          },
          chatMember: chatResponse.chatMember || {
            userId: '',
            status: '',
          },
          chatPhotos: [],
        };

        this.isCreator =
          this.chatData.chatMember.userId === this.userId &&
          this.chatData.chatMember.status === 'CREATOR';

        this.settingsData = {
          joinByRequest: this.chatData.chat.joinByRequest,
          hasHiddenMembers: this.chatData.chat.hasHiddenMembers,
        };

        const photosResponse = await fetchGetAllGroupChatPhotosByChatId(this.chatId);
        this.chatData.chatPhotos = photosResponse || [];

        if (this.chatData.chatPhotos.length) {
          this.photos = await Promise.all(
            this.chatData.chatPhotos.map(async (photo) => {
              try {
                const url = await fetchDownloadGroupChatPhotoByChatIdAndPhotoId(
                  this.chatId,
                  photo.id,
                  'big'
                );
                return url || this.GroupChatDefaultAvatar;
              } catch (error) {
                console.error(`Ошибка загрузки фото ${photo.id} (big):`, error);
                return this.GroupChatDefaultAvatar;
              }
            })
          );

          this.thumbnailPhotos = await Promise.all(
            this.chatData.chatPhotos.map(async (photo) => {
              try {
                const url = await fetchDownloadGroupChatPhotoByChatIdAndPhotoId(
                  this.chatId,
                  photo.id,
                  'small'
                );
                return url || this.GroupChatDefaultAvatar;
              } catch (error) {
                console.error(`Ошибка загрузки фото ${photo.id} (small):`, error);
                return this.GroupChatDefaultAvatar;
              }
            })
          );
        } else {
          this.photos = [];
          this.thumbnailPhotos = [];
        }
      } catch (error) {
        console.error('Ошибка загрузки чата:', error);
        this.handleErrors(error, ['upload']);
        router.push({ name: 'Chat', params: { id: this.chatId } });
      } finally {
        this.isLoading = false;
        this.isPhotoLoading = false;
      }
    },
    async openMembersModal() {
      if (this.chatData.chat.hasHiddenMembers) return;
      this.isMembersModalOpen = true;
      this.members = [];
      this.membersPage = 0;
      this.hasMoreMembers = true;
      await this.fetchMembers();
    },
    async fetchMembers() {
      if (!this.hasMoreMembers || this.isMembersLoading) return;
      try {
        this.isMembersLoading = true;
        const response = await fetchGetGroupChatMembers(this.chatId, this.membersPage, this.membersPageSize);
        const membersWithPhotos = await Promise.all(
          response.map(async (member) => {
            const photoUrl = await fetchDownloadMainUserPhotoByUserId(member.user.id, 'small');
            return { ...member, photoUrl };
          })
        );
        this.members = [...this.members, ...membersWithPhotos];
        this.hasMoreMembers = response.length === this.membersPageSize;
        this.membersPage += 1;
      } catch (error) {
        console.error('Ошибка загрузки участников:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isMembersLoading = false;
      }
    },
    handleMembersScroll() {
      const list = this.$refs.membersList;
      if (!list || this.isMembersLoading || !this.hasMoreMembers) return;

      const scrollPosition = list.scrollTop + list.clientHeight;
      const totalHeight = list.scrollHeight;
      const items = list.querySelectorAll('.user-item');
      const itemCount = items.length;

      if (itemCount >= 15) {
        const fifteenthFromBottom = items[itemCount - 15];
        if (fifteenthFromBottom) {
          const rect = fifteenthFromBottom.getBoundingClientRect();
          const listRect = list.getBoundingClientRect();
          if (rect.top <= listRect.bottom) {
            this.fetchMembers();
          }
        }
      } else if (scrollPosition >= totalHeight - 50) {
        this.fetchMembers();
      }
    },
    closeMembersModal() {
      this.isMembersModalOpen = false;
      this.members = [];
      this.membersPage = 0;
      this.hasMoreMembers = true;
    },
    async openAddMembersModal() {
      if (!this.isCreator) return;
      this.isAddMembersModalOpen = true;
      this.availableUsers = [];
      this.addMembersPage = 0;
      this.hasMoreAvailableUsers = true;
      await this.fetchAvailableUsers();
    },
    async fetchAvailableUsers() {
      if (!this.hasMoreAvailableUsers || this.isAddMembersLoading) return;
      try {
        this.isAddMembersLoading = true;
        const response = await fetchGetAvailableUsersToAdding(this.chatId, this.addMembersPage, this.addMembersPageSize);
        const usersWithPhotos = await Promise.all(
          response.map(async (user) => {
            const photoUrl = await fetchDownloadMainUserPhotoByUserId(user.user.id, 'small');
            return { ...user, photoUrl };
          })
        );
        this.availableUsers = [...this.availableUsers, ...usersWithPhotos];
        this.hasMoreAvailableUsers = response.length === this.addMembersPageSize;
        this.addMembersPage += 1;
      } catch (error) {
        console.error('Ошибка загрузки доступных пользователей:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isAddMembersLoading = false;
      }
    },
    handleAddMembersScroll() {
      const list = this.$refs.addMembersList;
      if (!list || this.isAddMembersLoading || !this.hasMoreAvailableUsers) return;

      const scrollPosition = list.scrollTop + list.clientHeight;
      const totalHeight = list.scrollHeight;
      const items = list.querySelectorAll('.user-item');
      const itemCount = items.length;

      if (itemCount >= 15) {
        const fifteenthFromBottom = items[itemCount - 15];
        if (fifteenthFromBottom) {
          const rect = fifteenthFromBottom.getBoundingClientRect();
          const listRect = list.getBoundingClientRect();
          if (rect.top <= listRect.bottom) {
            this.fetchAvailableUsers();
          }
        }
      } else if (scrollPosition >= totalHeight - 50) {
        this.fetchAvailableUsers();
      }
    },
    closeAddMembersModal() {
      this.isAddMembersModalOpen = false;
      this.availableUsers = [];
      this.addMembersPage = 0;
      this.hasMoreAvailableUsers = true;
    },
    async addMember(userId) {
      try {
        this.isUpdating = true;
        await fetchAddMembersToGroupChat(this.chatId, [userId]);
        this.availableUsers = this.availableUsers.filter(user => user.user.id !== userId);
        await this.fetchMembers();
      } catch (error) {
        console.error('Ошибка добавления участника:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    openRemoveMemberModal(userId) {
      this.selectedMemberId = userId;
      this.isRemoveMemberModalOpen = true;
    },
    closeRemoveMemberModal() {
      this.isRemoveMemberModalOpen = false;
      this.selectedMemberId = null;
    },
    async removeMember() {
      if (!this.selectedMemberId) return;
      try {
        this.isUpdating = true;
        await fetchDeleteMembersFromGroupChat(this.chatId, [this.selectedMemberId]);
        this.members = this.members.filter(member => member.user.id !== this.selectedMemberId);
        this.closeRemoveMemberModal();
        await this.fetchAvailableUsers();
      } catch (error) {
        console.error('Ошибка удаления участника:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async leaveChat() {
      try {
        this.isUpdating = true;
        await fetchLeaveFromGroupChat(this.chatId);
        this.closeMembersModal();
        router.push({ name: 'Dashboard' });
      } catch (error) {
        console.error('Ошибка выхода из чата:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    toggleMenu() {
      this.menuVisible = !this.menuVisible;
    },
    openUploadModal() {
      if (!this.isCreator) return;
      this.isUploadModalOpen = true;
      this.clearErrors();
      this.selectedFile = null;
    },
    closeUploadModal() {
      this.isUploadModalOpen = false;
      this.selectedFile = null;
      this.clearErrors();
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      this.errors.upload = '';
      this.selectedFile = null;

      if (!file) {
        this.errors.upload = 'Файл не выбран';
        return;
      }
      if (file.type !== 'image/jpeg') {
        this.errors.upload = 'Пожалуйста, выберите файл в формате JPEG';
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        this.errors.upload = 'Файл слишком большой (максимум 5 МБ)';
        return;
      }

      this.selectedFile = file;
    },
    async uploadPhoto() {
      if (!this.selectedFile) return;

      try {
        this.isUpdating = true;
        await fetchUploadGroupChatPhoto(this.chatId, this.selectedFile);
        this.closeUploadModal();
        await this.fetchChat();
      } catch (error) {
        console.error('Ошибка загрузки фотографии:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async deletePhoto() {
      try {
        this.isUpdating = true;
        const photoId = this.chatData.chatPhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchDeleteChatPhotoByChatIdAndPhotoId(this.chatId, photoId);
          this.menuVisible = false;
          await this.fetchChat();
          if (this.currentPhotoIndex >= this.photos.length) {
            this.currentPhotoIndex = Math.max(0, this.photos.length - 1);
          }
        }
      } catch (error) {
        console.error('Ошибка удаления фотографии:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async setMainPhoto() {
      try {
        this.isUpdating = true;
        const photoId = this.chatData.chatPhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchSetMainGroupChatPhotoByChatIdAndPhotoId(this.chatId, photoId);
          this.menuVisible = false;
          await this.fetchChat();
        }
      } catch (error) {
        console.error('Ошибка установки основной фотографии:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    openTitleModal() {
      if (!this.isCreator) return;
      this.isTitleModalOpen = true;
      this.clearErrors();
      this.titleData = { newTitle: this.chatData.chat.title };
    },
    closeTitleModal() {
      this.isTitleModalOpen = false;
      this.clearErrors();
    },
    openUsernameModal() {
      if (!this.isCreator) return;
      this.isUsernameModalOpen = true;
      this.clearErrors();
      this.usernameData = { newUsername: this.chatData.chat.username };
    },
    closeUsernameModal() {
      this.isUsernameModalOpen = false;
      this.clearErrors();
      this.isUsernameChecking = false;
    },
    openDescriptionModal() {
      if (!this.isCreator) return;
      this.isDescriptionModalOpen = true;
      this.clearErrors();
      this.descriptionData = { newDescription: this.chatData.chat.description || '' };
    },
    closeDescriptionModal() {
      this.isDescriptionModalOpen = false;
      this.clearErrors();
    },
    openDeleteModal() {
      if (!this.isCreator) return;
      this.isDeleteModalOpen = true;
      this.clearErrors();
    },
    closeDeleteModal() {
      this.isDeleteModalOpen = false;
      this.clearErrors();
    },
    validateField: debounce(function (field) {
      this.errors[field] = '';

      if (field === 'newTitle') {
        const value = this.titleData.newTitle.trim();
        if (!value) {
          this.errors.newTitle = 'Название чата не может быть пустым';
        } else if (value.length < 1 || value.length > 128) {
          this.errors.newTitle = 'Название чата должно содержать от 1 до 128 символов';
        }
      }

      if (field === 'newUsername') {
        const value = this.usernameData.newUsername.trim();

        if (!value) {
          this.errors.newUsername = 'Уникальное имя не может быть пустым';
          return;
        }

        if (value.length < 5 || value.length > 32) {
          this.errors.newUsername = 'Уникальное имя должно содержать от 5 до 32 символов';
          return;
        }

        if (value === this.chatData.chat.username) {
          this.errors.newUsername = 'Новое имя совпадает с текущим';
          return;
        }

        if (!/^[a-zA-Z]/.test(value)) {
          this.errors.newUsername = 'Уникальное имя должно начинаться с буквы';
          return;
        }

        if (value.startsWith('_')) {
          this.errors.newUsername = 'Уникальное имя не должно начинаться с подчеркивания';
          return;
        }

        if (!/^[a-zA-Z0-9_]*$/.test(value)) {
          this.errors.newUsername = 'Уникальное имя должно содержать только буквы, цифры и подчеркивания';
          return;
        }

        if (!/[a-zA-Z0-9]$/.test(value)) {
          this.errors.newUsername = 'Уникальное имя должно заканчиваться буквой или цифрой';
          return;
        }

        if (value.includes('__')) {
          this.errors.newUsername = 'Уникальное имя не должно содержать последовательные подчеркивания';
          return;
        }

        this.checkUsernameAvailability(value);
      }

      if (field === 'newDescription') {
        const value = this.descriptionData.newDescription.trim();
        if (value.length > 255) {
          this.errors.newDescription = 'Описание не должно превышать 255 символов';
        }
      }
    }, 500),
    async checkUsernameAvailability(username) {
      this.isUsernameChecking = true;
      try {
        const exists = await fetchExistsGroupChatByUsername(username);
        if (exists) {
          this.errors.newUsername = 'Уникальное имя уже занято';
        } else {
          this.errors.newUsername = '';
        }
      } catch (error) {
        console.error('Ошибка проверки уникального имени:', error);
        this.errors.newUsername = 'Не удалось проверить уникальное имя';
      } finally {
        this.isUsernameChecking = false;
      }
    },
    async updateTitle() {
      this.validateField('newTitle');

      if (this.hasTitleErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { newTitle } = this.titleData;
        await fetchChangeGroupChatTitle(this.chatId, newTitle.trim());
        this.closeTitleModal();
        await this.fetchChat();
      } catch (error) {
        console.error('Ошибка смены названия:', error);
        this.handleErrors(error, ['newTitle']);
      } finally {
        this.isUpdating = false;
      }
    },
    async updateUsername() {
      this.validateField('newUsername');

      if (this.hasUsernameErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { newUsername } = this.usernameData;
        await fetchChangeGroupChatUsername(this.chatId, newUsername.trim());
        this.closeUsernameModal();
        await this.fetchChat();
      } catch (error) {
        console.error('Ошибка смены уникального имени:', error);
        this.handleErrors(error, ['newUsername']);
      } finally {
        this.isUpdating = false;
      }
    },
    async updateDescription() {
      this.validateField('newDescription');

      if (this.hasDescriptionErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { newDescription } = this.descriptionData;
        await fetchChangeGroupChatDescription(this.chatId, newDescription.trim() || null);
        this.closeDescriptionModal();
        await this.fetchChat();
      } catch (error) {
        console.error('Ошибка смены описания:', error);
        this.handleErrors(error, ['newDescription']);
      } finally {
        this.isUpdating = false;
      }
    },
    async updateSettings() {
      try {
        this.isUpdating = true;
        const { joinByRequest, hasHiddenMembers } = this.settingsData;
        await fetchChangeGroupChatSettings(this.chatId, joinByRequest, hasHiddenMembers);
        await this.fetchChat();
      } catch (error) {
        console.error('Ошибка обновления настроек:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async deleteChat() {
      try {
        this.isUpdating = true;
        await fetchDeleteChatByChatId(this.chatId);
        this.closeDeleteModal();
        router.push({ name: 'Dashboard' });
      } catch (error) {
        console.error('Ошибка удаления чата:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    goToProfile(userId) {
      this.closeMembersModal();
      this.closeAddMembersModal();
      router.push({ name: 'Profile', params: { id: userId } });
    },
    goBack() {
      router.push({ name: 'Chat', params: { id: this.chatId } });
    },
    nextPhoto() {
      this.currentPhotoIndex = (this.currentPhotoIndex + 1) % this.photos.length;
    },
    prevPhoto() {
      this.currentPhotoIndex = (this.currentPhotoIndex - 1 + this.photos.length) % this.photos.length;
    },
    closeMenuOnClickOutside(event) {
      const menu = this.$refs.menuDropdown;
      const button = this.$refs.menuButton;
      if (menu && !menu.contains(event.target) && button && !button.contains(event.target)) {
        this.menuVisible = false;
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
              console.warn(`Необработанная ошибка валидации для поля ${key}: ${apiError.validationErrors[key]}`);
            }
          });
        } else {
          this.errors[fields[0]] = apiError.message || 'Произошла ошибка';
        }
      } else {
        this.errors[fields[0]] = error.message || 'Произошла ошибка';
      }
    },
  },
};
</script>

<style scoped>
.group-chat-container {
  background-color: #393939;
  border-radius: 1.25rem;
  padding: 2rem;
  width: 90%;
  max-width: 28rem;
  min-width: 18rem;
  margin: 2rem auto;
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.2);
  color: #f9f8f8;
  box-sizing: border-box;
}

.page-loading,
.modal-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.page-loading p,
.modal-loading p {
  margin-top: 0.5rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f9f8f8;
  border-top: 5px solid transparent;
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

.group-chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.group-chat-header h1 {
  font-size: clamp(1.5rem, 5vw, 1.75rem);
  font-weight: bold;
}

.back-button {
  font-size: clamp(1.25rem, 4vw, 1.5rem);
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
}

.photo-slider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1rem;
  position: relative;
}

.photo-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  max-width: 300px;
}

.chat-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0.5rem;
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.2);
  transition: opacity 0.3s ease;
}

.photo-gradient {
  position: absolute;
  left: 0;
  right: 0;
  height: 65px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.4), transparent);
  z-index: 1;
}

.photo-gradient-bottom {
  bottom: 0;
  border-bottom-left-radius: 0.5rem;
  border-bottom-right-radius: 0.5rem;
}

.photo-gradient-top {
  top: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.4), transparent);
  border-top-left-radius: 0.5rem;
  border-top-right-radius: 0.5rem;
}

.photo-counter {
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  color: #ffffff;
  font-weight: 500;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  padding-top: 2rem;
  text-align: center;
}

.photo-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  aspect-ratio: 1;
  max-width: 300px;
}

.menu-button {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  font-size: clamp(1.25rem, 4vw, 1.5rem);
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  z-index: 10;
  padding: 0.25rem;
  border-radius: 50%;
}

.menu-dropdown {
  position: absolute;
  top: 2rem;
  right: 0.5rem;
  background-color: #3c3c3c;
  border-radius: 0.5rem;
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.3);
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.menu-dropdown button {
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  padding: 0.5rem;
  text-align: left;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
}

.menu-dropdown button:hover {
  background-color: #444;
}

.slider-button {
  position: absolute;
  bottom: 1rem;
  font-size: clamp(1rem, 3vw, 1.25rem);
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 50%;
  z-index: 10;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.slider-prev {
  left: 0.5rem;
}

.slider-next {
  right: 0.5rem;
}

.group-chat-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1rem;
  margin-top: 1rem;
  padding: 0 1rem;
  width: 100%;
  box-sizing: border-box;
}

.group-chat-details-header {
  color: #aaaaaa;
  font-size: clamp(0.875rem, 3vw, 1rem);
  font-weight: 500;
  margin-bottom: 0;
  text-align: left;
  width: 100%;
}

.group-chat-detail-item {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 0.25rem;
}

.group-chat-text-button,
.group-chat-text {
  background: none;
  border: none;
  color: #f9f8f8;
  font-size: clamp(1rem, 3.5vw, 1.125rem);
  font-weight: bold;
  text-align: left;
  padding: 0.25rem 0;
  width: 100%;
  overflow-wrap: break-word;
  word-break: break-word;
  white-space: normal;
}

.group-chat-text-button {
  cursor: pointer;
  transition: color 0.2s ease;
}

.group-chat-text-button:hover {
  color: #cccccc;
}

.group-chat-detail-label {
  color: #aaaaaa;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  font-weight: normal;
  text-align: left;
}

.divider {
  border: none;
  border-top: 1px solid #444;
  margin: 1rem 0;
}

.group-chat-settings {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0 1rem;
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.settings-label {
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.group-chat-settings p {
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
  margin: 0.25rem 0;
}

.group-chat-button {
  width: 100%;
  padding: 0.75rem;
  font-size: clamp(0.875rem, 3vw, 1rem);
  background-color: #2d2d2d;
  color: #f9f8f8;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 3px;
}

.group-chat-button:hover {
  background-color: #444444;
}

.group-chat-button.danger {
  background-color: #1f2937;
}

.group-chat-button.danger:hover {
  background-color: #374151;
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
  background-color: #393939;
  padding: 1.5rem;
  border-radius: 1.25rem;
  color: #f9f8f8;
  width: 90%;
  max-width: 28rem;
  min-width: 18rem;
  box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.2);
  box-sizing: border-box;
}

.user-list-modal {
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-content h3 {
  font-size: clamp(1.25rem, 4vw, 1.5rem);
  font-weight: bold;
  margin-bottom: 1rem;
  text-align: center;
}

.user-list {
  max-height: 50vh;
  overflow-y: auto;
  margin-bottom: 1rem;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.user-item:hover {
  background-color: #444444;
}

.user-item-photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-item-username {
  font-size: clamp(0.875rem, 3vw, 1rem);
  color: #f9f8f8;
  flex-grow: 1;
}

.user-item-button {
  padding: 0.5rem 1rem;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  font-weight: bold;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.user-item-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.user-item-button:not(.danger) {
  background-color: #6b7280;
  color: #f9f8f8;
}

.user-item-button:not(.danger):hover {
  background-color: #9ca3af;
}

.user-item-button.danger {
  background-color: #1f2937;
  color: #f9f8f8;
}

.user-item-button.danger:hover {
  background-color: #374151;
}

.not-found {
  text-align: center;
  color: #aaaaaa;
  font-size: clamp(0.875rem, 3vw, 1rem);
  margin-bottom: 1rem;
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

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #555;
  border-radius: 0.5rem;
  background-color: #2d2d2d;
  color: #f9f8f8;
  box-sizing: border-box;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.form-group textarea {
  resize: vertical;
}

.form-group input:disabled,
.form-group textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-group input.input-error,
.form-group textarea.input-error {
  border-color: #ff4d4d;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #888888;
}

.error {
  color: #ff4d4d;
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

.centered-button {
  width: auto;
  min-width: 100px;
  margin: 0 auto;
}

.danger {
  background-color: #1f2937;
  color: #f9f8f8;
}

.danger:hover {
  background-color: #374151;
}

@media (max-width: 600px) {
  .group-chat-container,
  .modal-content {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  .group-chat-header h1 {
    font-size: clamp(1.25rem, 6vw, 1.5rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .group-chat-button {
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

  .group-chat-details-header {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .group-chat-text-button,
  .group-chat-text {
    font-size: clamp(0.875rem, 4vw, 1rem);
  }

  .group-chat-detail-label {
    font-size: clamp(0.625rem, 3vw, 0.75rem);
  }

  .user-item-photo {
    width: 32px;
    height: 32px;
  }

  .user-item-username {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .user-item-button {
    padding: 0.4rem 0.8rem;
    font-size: clamp(0.625rem, 3vw, 0.75rem);
  }

  .not-found {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }
}

@media (min-width: 601px) and (max-width: 1024px) {
  .group-chat-container,
  .modal-content {
    width: 85%;
    max-width: 35rem;
    padding: 1.75rem;
  }

  .group-chat-header h1 {
    font-size: clamp(1.5rem, 4vw, 1.75rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .group-chat-button {
    padding: 0.7rem;
  }

  .group-chat-details-header {
    font-size: clamp(0.875rem, 3vw, 1rem);
  }

  .group-chat-text-button,
  .group-chat-text {
    font-size: clamp(1rem, 3.5vw, 1.125rem);
  }

  .group-chat-detail-label {
    font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  }
}

@media (min-width: 1025px) {
  .group-chat-container,
  .modal-content {
    max-width: 30rem;
    padding: 2rem;
  }

  .group-chat-header h1 {
    font-size: clamp(1.75rem, 3vw, 2rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .group-chat-button {
    padding: 0.8rem;
  }

  .group-chat-details-header {
    font-size: clamp(1rem, 2.5vw, 1.125rem);
  }

  .group-chat-text-button,
  .group-chat-text {
    font-size: clamp(1.125rem, 3vw, 1.25rem);
  }

  .group-chat-detail-label {
    font-size: clamp(0.875rem, 2vw, 1rem);
  }
}
</style>