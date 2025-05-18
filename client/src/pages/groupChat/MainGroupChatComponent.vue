<template>
  <div v-if="groupChat" class="profile-container">
    <header class="profile-header">
      <button class="back-button" @click="goBack">←</button>
      <h1>{{ groupChat.name || 'Групповой чат' }}</h1>
    </header>

    <div v-if="photos.length" class="photo-slider">
      <div class="photo-wrapper">
        <img
          :src="photos[currentPhotoIndex] || GroupChatDefaultAvatar"
          alt="Group Chat Photo"
          class="user-photo"
        />
        <button
          v-if="groupChat.ownerId === userId"
          class="menu-button"
          @click="toggleGroupChatMenu"
          ref="groupChatMenuButton"
        >⋮</button>
        <div v-if="groupChatMenuVisible && groupChat.ownerId === userId" class="menu-dropdown" ref="groupChatMenuDropdown">
          <button @click="openUploadModal">Загрузить фото</button>
          <button v-if="!isCurrentPhotoMain" @click="setMainPhoto">Сделать основным</button>
          <button @click="deletePhoto">Удалить фото</button>
        </div>
        <div class="photo-navigation">
          <button v-if="photos.length > 1" @click="prevPhoto" class="slider-button">‹</button>
          <p class="photo-counter">
            {{ currentPhotoIndex + 1 }}/{{ photos.length }}
          </p>
          <button v-if="photos.length > 1" @click="nextPhoto" class="slider-button">›</button>
        </div>
      </div>
    </div>

    <div v-else class="photo-wrapper">
      <img
        :src="GroupChatDefaultAvatar"
        alt="Default Photo"
        class="user-photo"
      />
      <button
        v-if="groupChat.ownerId === userId"
        class="menu-button"
        @click="toggleGroupChatMenu"
        ref="groupChatMenuButton"
      >⋮</button>
      <div v-if="groupChatMenuVisible && groupChat.ownerId === userId" class="menu-dropdown" ref="groupChatMenuDropdown">
        <button @click="openUploadModal">Загрузить фото</button>
      </div>
      <p class="photo-counter">Фотографий нет</p>
    </div>

    <div class="profile-details">
      <h2>{{ groupChat.description || 'Нет описания' }}</h2>
      <h2>Публичный: {{ groupChat.isPublic ? 'да' : 'нет' }}</h2>
      <button class="profile-button" @click="openListMembersModal">Список участников</button>
      <button
        v-if="groupChat.ownerId === userId"
        class="profile-button"
        @click="openAddMembersModal"
      >Добавить участников</button>
      <button
        v-if="groupChat.ownerId === userId"
        class="profile-button"
        @click="openMetadataModal"
      >Сменить название и описание</button>
      <button
        v-if="groupChat.ownerId === userId"
        class="profile-button"
        @click="changeChatStatus"
      >Сделать чат {{ groupChat.isPublic ? 'закрытым' : 'открытым' }}</button>
      <button
        v-if="groupChat.ownerId !== userId"
        class="profile-button"
        @click="leaveFromChat"
      >Покинуть чат</button>
      <button
        v-if="groupChat.ownerId === userId"
        class="profile-button danger"
        @click="openDeleteModal"
      >Удалить чат</button>
    </div>

    <div v-if="isListMembersModalOpen" class="modal-overlay" @click.self="closeListMembersModal">
      <div class="modal-content" @click.stop>
        <header class="profile-header">
          <button class="back-button" @click="closeListMembersModal">←</button>
          <h2>Участники</h2>
        </header>
        <div v-for="(member, index) in groupChatMembers" :key="member.userId" class="member-item">
          <img
            :src="member.userPhoto || UserDefaultAvatar"
            alt="Member Avatar"
            class="member-avatar"
            @click="handleMemberProfileClick(member)"
          />
          <div class="member-info">
            <p class="member-name" @click="handleMemberProfileClick(member)">
              {{ member.userFirstName || 'Участник' }}
            </p>
            <button
              v-if="userId === groupChat.ownerId && member.userId !== groupChat.ownerId"
              :ref="'groupChatMemberMenuButton-' + index"
              @click="toggleGroupChatMemberMenu(member.userId, index)"
              class="dropdown-toggle-button"
            >⋮</button>
            <div
              v-if="openMemberMenuId === member.userId"
              class="member-menu-dropdown"
              :ref="'groupChatMemberMenuDropdown-' + index"
            >
              <button class="dropdown-item" @click="makeOwner(member)">Сделать владельцем</button>
              <button class="dropdown-item" @click="removeMember(member)">Удалить участника</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isAddMembersModalOpen" class="modal-overlay" @click.self="closeAddMembersModal">
      <div class="modal-content" @click.stop>
        <header class="profile-header">
          <button class="back-button" @click="closeAddMembersModal">←</button>
          <h2>Добавить пользователей</h2>
        </header>
        <div v-for="user in availableUsers" :key="user.id" class="member-item">
          <img
            :src="user.userPhoto || UserDefaultAvatar"
            alt="User Avatar"
            class="member-avatar"
            @click="handleUserProfileClick(user)"
          />
          <div class="member-info">
            <p class="member-name" @click="handleUserProfileClick(user)">
              {{ user.username || 'Пользователь' }}
            </p>
            <button
              class="profile-button small"
              :class="{ 'cancel-button': selectedUsers.includes(user.id), 'upload-button': !selectedUsers.includes(user.id) }"
              @click="toggleUserSelection(user.id)"
            >
              {{ selectedUsers.includes(user.id) ? 'Отменить' : 'Добавить' }}
            </button>
          </div>
        </div>
        <div class="modal-actions">
          <button class="modal-button upload-button" @click="submitSelectedUsers">Добавить выбранных</button>
          <button class="modal-button cancel-button" @click="closeAddMembersModal">Закрыть</button>
        </div>
      </div>
    </div>

    <div v-if="isMetadataModalOpen" class="modal-overlay" @click.self="closeMetadataModal">
      <div class="modal-content" @click.stop>
        <h3>Сменить название и описание</h3>
        <input
          type="text"
          v-model="metadataData.newName"
          placeholder="Новое название"
        />
        <textarea
          v-model="metadataData.newDescription"
          placeholder="Новое описание"
          rows="4"
        ></textarea>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-actions">
          <button class="modal-button upload-button" @click="changeMetadata">Изменить</button>
          <button class="modal-button cancel-button" @click="closeMetadataModal">Отмена</button>
        </div>
      </div>
    </div>

    <div v-if="isUploadModalOpen" class="modal-overlay" @click.self="closeUploadModal">
      <div class="modal-content" @click.stop>
        <h3>Загрузить фото</h3>
        <input type="file" @change="handleFileChange" accept="image/jpeg" />
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-actions">
          <button class="modal-button upload-button" @click="uploadPhoto">Загрузить</button>
          <button class="modal-button cancel-button" @click="closeUploadModal">Отмена</button>
        </div>
      </div>
    </div>

    <div v-if="isDeleteModalOpen" class="modal-overlay" @click.self="closeDeleteModal">
      <div class="modal-content" @click.stop>
        <h3>Удалить чат</h3>
        <p>Вы уверены, что хотите удалить чат? Это действие необратимо. Все участники чата, включая вас, потеряют доступ к нему.</p>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-actions">
          <button class="modal-button danger" @click="deleteGroupChat">Удалить</button>
          <button class="modal-button cancel-button" @click="closeDeleteModal">Отмена</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { jwtDecode } from "jwt-decode";
import authStore from "@/store/authStore";
import router from "@/router/router";
import UserDefaultAvatar from "@/assets/images/UserDefaultAvatar.png";
import GroupChatDefaultAvatar from "@/assets/images/GroupChatDefaultAvatar.png";
import {
  fetchAddMembersToGroupChat,
  fetchDeleteMembersFromGroupChat, fetchGetAvailableUsersToAdding, fetchGetGroupChatMembers,
  fetchLeaveFromGroupChat
} from "@/api/resources/chatMember";
import {fetchDeleteChatByChatId, fetchGetChat} from "@/api/resources/chat";
import {
  fetchDeleteChatPhotoByChatIdAndPhotoId,
  fetchDownloadGroupChatPhotoByChatIdAndPhotoId,
  fetchSetMainGroupChatPhotoByChatIdAndPhotoId,
  fetchUploadGroupChatPhoto
} from "@/api/resources/chatPhoto";
import {fetchDownloadMainUserPhotoByUserId} from "@/api/resources/userPhoto";

export default {
  name: "MainGroupChatComponent",
  data() {
    return {
      UserDefaultAvatar,
      GroupChatDefaultAvatar,
      chatId: this.$route.params.id,
      userId: null,
      groupChat: null,
      photos: [],
      currentPhotoIndex: 0,
      groupChatMembers: [],
      availableUsers: [],
      metadataData: {
        newName: "",
        newDescription: "",
      },
      selectedUsers: [],
      groupChatMenuVisible: false,
      openMemberMenuId: null,
      isUploadModalOpen: false,
      isMetadataModalOpen: false,
      isDeleteModalOpen: false,
      isListMembersModalOpen: false,
      isAddMembersModalOpen: false,
      selectedFile: null,
      errorMessage: "",
    };
  },
  computed: {
    isCurrentPhotoMain() {
      const currentPhoto = this.groupChat?.groupChatPhotos?.[this.currentPhotoIndex];
      return currentPhoto ? currentPhoto.isMain : false;
    },
  },
  created() {
    this.userId = jwtDecode(authStore.state.accessToken).sub;
    this.fetchChatData();
    document.addEventListener("click", this.closeGroupChatMenuOnClickOutside);
    document.addEventListener("click", this.closeMemberMenuOnClickOutside);
  },
  unmounted() {
    document.removeEventListener("click", this.closeGroupChatMenuOnClickOutside);
    document.removeEventListener("click", this.closeMemberMenuOnClickOutside);
  },
  methods: {
    async fetchChatData() {
      try {
        this.groupChat = await fetchGetChat(this.chatId);
        if (this.groupChat.groupChatPhotos?.length) {
          this.photos = await Promise.all(
            this.groupChat.groupChatPhotos.map(async (photo) => {
              try {
                return await fetchDownloadGroupChatPhotoByChatIdAndPhotoId(this.chatId, photo.id, "big");
              } catch (error) {
                console.error(`Ошибка загрузки фото ${photo.id}:`, error);
                return this.GroupChatDefaultAvatar;
              }
            })
          );
        } else {
          this.photos = [];
        }
      } catch (error) {
        console.error("Ошибка при загрузке данных чата:", error);
        this.errorMessage = "Не удалось загрузить данные чата";
        router.push({name: "Dialogs"});
      }
    },
    async loadListMembers() {
      try {
        this.groupChatMembers = await fetchGetGroupChatMembers(this.chatId);
        this.groupChatMembers = await Promise.all(
            this.groupChatMembers.map(async (member) => {
              return {
                ...member,
                userPhoto: await fetchDownloadMainUserPhotoByUserId(member.userId, "small"),
              };
            })
        );
      } catch (error) {
        console.error("Ошибка при загрузке участников:", error);
        this.errorMessage = "Не удалось загрузить список участников";
      }
    },
    async loadListAvailableUsers() {
      try {
        this.availableUsers = await fetchGetAvailableUsersToAdding(this.chatId, 1, 10);
        this.availableUsers = await Promise.all(
            this.availableUsers.map(async (user) => {
              return {
                ...user,
                userPhoto: await fetchDownloadMainUserPhotoByUserId(user.id, "small"),
              };
            })
        );
      } catch (error) {
        console.error("Ошибка при загрузке доступных пользователей:", error);
        this.errorMessage = "Не удалось загрузить список пользователей";
      }
    },
    async removeMember(member) {
      try {
        await fetchDeleteMembersFromGroupChat(this.chatId, {userIds: [member.userId]});
        this.openMemberMenuId = null;
        await this.loadListMembers();
      } catch (error) {
        console.error("Ошибка при удалении участника:", error);
        this.errorMessage = "Не удалось удалить участника";
      }
    },
    async makeOwner(member) {
      try {
        console.info(member)
        // await bchangeGroupChatOwner(this.chatId, {userId: member.userId});
        this.openMemberMenuId = null;
        await this.fetchChatData();
        await this.loadListMembers();
      } catch (error) {
        console.error("Ошибка при смене владельца:", error);
        this.errorMessage = "Не удалось сменить владельца";
      }
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (!file) {
        this.errorMessage = "Файл не выбран";
        this.selectedFile = null;
        return;
      }
      if (file.type !== "image/jpeg") {
        this.errorMessage = "Пожалуйста, выберите файл в формате JPEG";
        this.selectedFile = null;
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage = "Файл слишком большой (максимум 5 МБ)";
        this.selectedFile = null;
        return;
      }
      this.selectedFile = file;
      this.errorMessage = "";
    },
    async uploadPhoto() {
      if (!this.selectedFile) {
        this.errorMessage = "Файл не выбран или имеет неверный формат";
        return;
      }
      try {
        const formData = new FormData();
        formData.append("file", this.selectedFile);
        await fetchUploadGroupChatPhoto(this.chatId, formData);
        this.closeUploadModal();
        await this.fetchChatData();
      } catch (error) {
        console.error("Ошибка загрузки фотографии:", error);
        this.errorMessage = "Не удалось загрузить фото";
      }
    },
    async deletePhoto() {
      try {
        const photoId = this.groupChat.groupChatPhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchDeleteChatPhotoByChatIdAndPhotoId(this.chatId, photoId);
          this.groupChatMenuVisible = false;
          await this.fetchChatData();
          if (this.currentPhotoIndex >= this.photos.length) {
            this.currentPhotoIndex = Math.max(0, this.photos.length - 1);
          }
        }
      } catch (error) {
        console.error("Ошибка удаления фотографии:", error);
        this.errorMessage = "Не удалось удалить фото";
      }
    },
    async setMainPhoto() {
      try {
        const photoId = this.groupChat.groupChatPhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchSetMainGroupChatPhotoByChatIdAndPhotoId(this.chatId, photoId);
          this.groupChatMenuVisible = false;
          await this.fetchChatData();
        }
      } catch (error) {
        console.error("Ошибка установки основной фотографии:", error);
        this.errorMessage = "Не удалось установить основное фото";
      }
    },
    async changeChatStatus() {
      try {
        // await fetchchangeGroupChatPublicStatus(this.chatId);
        await this.fetchChatData();
      } catch (error) {
        console.error("Ошибка смены статуса чата:", error);
        this.errorMessage = "Не удалось изменить статус чата";
      }
    },
    async leaveFromChat() {
      try {
        await fetchLeaveFromGroupChat(this.chatId);
        router.push({name: "Dialogs"});
      } catch (error) {
        console.error("Ошибка при попытке покинуть чат:", error);
        this.errorMessage = "Не удалось покинуть чат";
      }
    },
    async changeMetadata() {
      const {newName, newDescription} = this.metadataData;
      console.info(newDescription)
      if (!newName) {
        this.errorMessage = "Название не может быть пустым";
        return;
      }
      try {
        // await fetchChanchangeGroupChatMetadata(this.chatId, {newName, newDescription});
        this.closeMetadataModal();
        await this.fetchChatData();
      } catch (error) {
        console.error("Ошибка смены метаданных:", error);
        this.errorMessage = "Не удалось сменить название и описание";
      }
    },
    async deleteGroupChat() {
      try {
        await fetchDeleteChatByChatId(this.chatId);
        this.closeDeleteModal();
        router.push({name: "Dialogs"});
      } catch (error) {
        console.error("Ошибка удаления чата:", error);
        this.errorMessage = "Не удалось удалить чат";
      }
    },
    async submitSelectedUsers() {
      if (!this.selectedUsers.length) {
        this.errorMessage = "Выберите хотя бы одного пользователя";
        return;
      }
      try {
        await fetchAddMembersToGroupChat(this.chatId, {userIds: this.selectedUsers});
        this.closeAddMembersModal();
        await this.loadListMembers();
      } catch (error) {
        console.error("Ошибка добавления пользователей:", error);
        this.errorMessage = "Не удалось добавить пользователей";
      }
    },
    toggleGroupChatMenu() {
      this.groupChatMenuVisible = !this.groupChatMenuVisible;
    },
    closeGroupChatMenuOnClickOutside(event) {
      const menu = this.$refs.groupChatMenuDropdown;
      const button = this.$refs.groupChatMenuButton;
      if (
          menu && !menu.contains(event.target) &&
          button && !button.contains(event.target)
      ) {
        this.groupChatMenuVisible = false;
      }
    },
    toggleGroupChatMemberMenu(memberId, index) {
      this.openMemberMenuId = this.openMemberMenuId === memberId ? null : memberId;
      this.$nextTick(() => {
        const button = this.$refs[`groupChatMemberMenuButton-${index}`];
        const menu = this.$refs[`groupChatMemberMenuDropdown-${index}`];
        if (button && menu) {
          const buttonRect = button.getBoundingClientRect();
          menu.style.top = `${buttonRect.bottom + window.scrollY}px`;
          menu.style.left = `${buttonRect.left + window.scrollX}px`;
        }
      });
    },
    closeMemberMenuOnClickOutside(event) {
      const menus = this.$refs.groupChatMemberMenuDropdown;
      if (!menus || !Array.isArray(menus)) return;
      const buttons = this.$refs.groupChatMemberMenuButton;
      menus.forEach((menu, index) => {
        const button = buttons[index];
        if (
            menu &&
            button &&
            !menu.contains(event.target) &&
            !button.contains(event.target)
        ) {
          this.openMemberMenuId = null;
        }
      });
    },
    toggleUserSelection(userId) {
      if (this.selectedUsers.includes(userId)) {
        this.selectedUsers = this.selectedUsers.filter((id) => id !== userId);
      } else {
        this.selectedUsers.push(userId);
      }
    },
    goBack() {
      router.go(-1);
    },
    handleUserProfileClick(user) {
      router.push({name: "User", params: {id: user.id}});
    },
    handleMemberProfileClick(member) {
      router.push({name: "User", params: {id: member.userId}});
    },
    openUploadModal() {
      this.isUploadModalOpen = true;
      this.errorMessage = "";
      this.selectedFile = null;
      this.groupChatMenuVisible = false;
    },
    closeUploadModal() {
      this.isUploadModalOpen = false;
      this.selectedFile = null;
      this.errorMessage = "";
    },
    openMetadataModal() {
      this.isMetadataModalOpen = true;
      this.errorMessage = "";
      this.metadataData = {
        newName: this.groupChat.name,
        newDescription: this.groupChat.description,
      };
    },
    closeMetadataModal() {
      this.isMetadataModalOpen = false;
      this.errorMessage = "";
    },
    openDeleteModal() {
      this.isDeleteModalOpen = true;
      this.errorMessage = "";
    },
    closeDeleteModal() {
      this.isDeleteModalOpen = false;
      this.errorMessage = "";
    },
    async openListMembersModal() {
      this.isListMembersModalOpen = true;
      await this.loadListMembers();
    },
    closeListMembersModal() {
      this.isListMembersModalOpen = false;
      this.openMemberMenuId = null;
    },
    async openAddMembersModal() {
      this.isAddMembersModalOpen = true;
      this.selectedUsers = [];
      await this.loadListAvailableUsers();
    },
    closeAddMembersModal() {
      this.isAddMembersModalOpen = false;
      this.selectedUsers = [];
      this.errorMessage = "";
    },
    nextPhoto() {
      this.currentPhotoIndex = (this.currentPhotoIndex + 1) % this.photos.length;
    },
    prevPhoto() {
      this.currentPhotoIndex = (this.currentPhotoIndex - 1 + this.photos.length) % this.photos.length;
    },
  },
};
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background-color: #2c2c2c;
  color: #f9f8f8;
  border-radius: 10px;
  max-width: 600px;
  margin: 20px auto;
  position: relative;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.back-button {
  font-size: 24px;
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
}

.profile-details {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.profile-details h2 {
  font-size: 20px;
  margin: 0;
  color: #f9f8f8;
  text-align: center;
}

.photo-slider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px 0;
  position: relative;
}

.photo-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-photo {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.menu-button {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 25px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  z-index: 10;
  padding: 5px;
  border-radius: 50%;
}

.menu-dropdown {
  position: absolute;
  top: 40px;
  right: 10px;
  background-color: #3c3c3c;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  padding: 10px;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.menu-dropdown button {
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  padding: 10px;
  text-align: left;
}

.menu-dropdown button:hover {
  background-color: #444;
}

.photo-counter {
  font-size: 14px;
  color: #f9f8f8;
  margin: 10px;
}

.photo-navigation {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
}

.slider-button {
  font-size: 24px;
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  padding: 10px;
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
  padding: 20px;
  border-radius: 10px;
  color: #f9f8f8;
  width: 400px;
  max-width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-content input[type="file"],
.modal-content input[type="text"],
.modal-content textarea {
  width: 90%;
  padding: 8px 10px;
  margin: 10px 0;
  border: 1px solid #cccccc;
  border-radius: 4px;
  background-color: #f4f4f4;
  color: #343434;
  font-size: 14px;
}

.modal-content textarea {
  resize: vertical;
}

.modal-content input[type="text"]::placeholder,
.modal-content textarea::placeholder {
  color: #b3b3b3;
}

.modal-content input[type="text"]:focus,
.modal-content textarea:focus {
  outline: none;
  border-color: #888888;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.modal-button {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.upload-button {
  background-color: #2e7d32;
  color: #fff;
}

.upload-button:hover {
  background-color: #255d27;
}

.cancel-button {
  background-color: #8a1c1c;
  color: #fff;
}

.cancel-button:hover {
  background-color: #611313;
}

.danger {
  background-color: #d32f2f;
  color: #fff;
}

.danger:hover {
  background-color: #b71c1c;
}

.error-message {
  color: #e53935;
  margin: 10px 0;
  font-size: 14px;
}

.profile-button {
  width: 80%;
  padding: 10px;
  font-size: 16px;
  background-color: #444;
  color: #f9f8f8;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.profile-button:hover {
  background-color: #555;
}

.profile-button.small {
  width: 30%;
  font-size: 14px;
  padding: 8px;
}

.member-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 15px;
  cursor: pointer;
}

.member-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.member-name {
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.dropdown-toggle-button {
  background: none;
  color: #f9f8f8;
  cursor: pointer;
  padding: 10px;
  font-size: 20px;
  border: none;
  z-index: 10;
}

.member-menu-dropdown {
  position: absolute;
  background-color: #3c3c3c;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  padding: 10px;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.member-menu-dropdown button {
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  padding: 10px;
  text-align: left;
}

.member-menu-dropdown button:hover {
  background-color: #444;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 15px;
    margin: 10px;
  }

  .user-photo {
    width: 200px;
    height: 200px;
  }

  .modal-content {
    width: 90%;
    padding: 15px;
  }

  .profile-button {
    width: 100%;
  }

  .profile-button.small {
    width: 40%;
  }
}
</style>