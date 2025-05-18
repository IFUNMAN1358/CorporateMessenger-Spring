<template>
  <div class="profile-container">
    <header class="profile-header">
      <button class="back-button" @click="goBack">←</button>
      <h1>{{ profile.username || 'Профиль' }}</h1>
    </header>

    <div v-if="photos.length" class="photo-slider">
      <div class="photo-wrapper">
        <img
          :src="photos[currentPhotoIndex] || UserDefaultAvatar"
          alt="User Photo"
          class="user-photo"
        />
        <button
          v-if="isOwnProfile"
          class="menu-button"
          @click="toggleMenu"
          ref="menuButton"
        >⋮</button>
        <div v-if="menuVisible && isOwnProfile" class="menu-dropdown" ref="menuDropdown">
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
        :src="UserDefaultAvatar"
        alt="Default Photo"
        class="user-photo"
      />
      <button
        v-if="isOwnProfile"
        class="menu-button"
        @click="toggleMenu"
        ref="menuButton"
      >⋮</button>
      <div v-if="menuVisible && isOwnProfile" class="menu-dropdown" ref="menuDropdown">
        <button @click="openUploadModal">Загрузить фото</button>
      </div>
      <p class="photo-counter">Фотографий нет</p>
    </div>

    <div class="profile-details">
      <h2>{{ profile.firstName || '' }} {{ profile.lastName || '' }}</h2>
      <div v-if="isOwnProfile" class="profile-actions">
        <button class="profile-button" @click="openPasswordModal">Сменить пароль</button>
        <button class="profile-button danger" @click="openDeleteModal">Удалить аккаунт</button>
      </div>
      <div v-else class="profile-actions">
        <button class="profile-button message-button" @click="startChat">Написать</button>
      </div>
    </div>

    <div v-if="isUploadModalOpen && isOwnProfile" class="modal-overlay" @click="closeUploadModal">
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

    <div v-if="isPasswordModalOpen && isOwnProfile" class="modal-overlay" @click="closePasswordModal">
      <div class="modal-content" @click.stop>
        <h3>Сменить пароль</h3>
        <input
          type="password"
          v-model="passwordData.currentPassword"
          placeholder="Текущий пароль"
        />
        <input
          type="password"
          v-model="passwordData.newPassword"
          placeholder="Новый пароль"
        />
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-actions">
          <button class="modal-button upload-button" @click="updatePassword">Изменить</button>
          <button class="modal-button cancel-button" @click="closePasswordModal">Отмена</button>
        </div>
      </div>
    </div>

    <div v-if="isDeleteModalOpen && isOwnProfile" class="modal-overlay" @click="closeDeleteModal">
      <div class="modal-content" @click.stop>
        <h3>Удалить аккаунт</h3>
        <p>Вы уверены, что хотите удалить аккаунт? Это действие необратимо.</p>
        <div class="modal-actions">
          <button class="modal-button danger" @click="deleteAccount">Удалить</button>
          <button class="modal-button cancel-button" @click="closeDeleteModal">Отмена</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserDefaultAvatar from '@/assets/images/UserDefaultAvatar.png';
import {jwtDecode} from "jwt-decode";
import authStore from "@/store/authStore";
import {
  fetchChangeUserPassword,
  fetchGetMyUserData,
  fetchGetUserByUserId,
  fetchSoftDeleteAccount
} from "@/api/resources/user";
import {
  fetchDeleteMyUserPhotoByPhotoId,
  fetchDownloadMyMainUserPhoto,
  fetchSetMyMainUserPhotoByPhotoId, fetchUploadMyUserPhoto
} from "@/api/resources/userPhoto";
import router from "@/router/router";
import {fetchGetOrCreatePrivateChatByUserId} from "@/api/resources/chat";

export default {
  name: "ProfileComponent",
  data() {
    return {
      profile: {},
      photos: [],
      currentPhotoIndex: 0,
      menuVisible: false,
      isUploadModalOpen: false,
      isPasswordModalOpen: false,
      isDeleteModalOpen: false,
      selectedFile: null,
      passwordData: {
        currentPassword: "",
        newPassword: "",
      },
      errorMessage: "",
      isOwnProfile: false,
      userId: null,
      UserDefaultAvatar,
    };
  },
  computed: {
    isCurrentPhotoMain() {
      const currentPhoto = this.profile.userProfilePhotos?.[this.currentPhotoIndex];
      return currentPhoto ? currentPhoto.isMain : false;
    },
  },
  async created() {
    this.userId = this.$route.params.id || jwtDecode(authStore.state.accessToken).sub;
    this.isOwnProfile = this.userId === jwtDecode(authStore.state.accessToken).sub;
    await this.fetchProfile();
    document.addEventListener("click", this.closeMenuOnClickOutside);
  },
  unmounted() {
    document.removeEventListener("click", this.closeMenuOnClickOutside);
  },
  methods: {
    async fetchProfile() {
      try {
        if (this.isOwnProfile) {
          this.profile = await fetchGetMyUserData();
        } else {
          this.profile = await fetchGetUserByUserId(this.userId);
        }

        if (this.profile.userProfilePhotos?.length) {
          this.photos = await Promise.all(
              this.profile.userProfilePhotos.map(async (photo) => {
                try {
                  return await fetchDownloadMyMainUserPhoto("big");
                } catch (error) {
                  console.error(`Ошибка загрузки фото ${photo.id}:`, error);
                  return this.UserDefaultAvatar;
                }
              })
          );
        } else {
          this.photos = [];
        }
      } catch (error) {
        console.error("Ошибка загрузки профиля:", error);
        this.errorMessage = "Не удалось загрузить профиль";
        router.push({name: "Dialogs"});
      }
    },
    toggleMenu() {
      this.menuVisible = !this.menuVisible;
    },
    openUploadModal() {
      if (!this.isOwnProfile) return;
      this.isUploadModalOpen = true;
      this.errorMessage = "";
      this.selectedFile = null;
      this.menuVisible = false;
    },
    closeUploadModal() {
      this.isUploadModalOpen = false;
      this.selectedFile = null;
      this.errorMessage = "";
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
        await fetchUploadMyUserPhoto(formData);
        this.closeUploadModal();
        await this.fetchProfile();
      } catch (error) {
        console.error("Ошибка загрузки фотографии:", error);
        this.errorMessage = "Не удалось загрузить фото";
      }
    },
    async deletePhoto() {
      try {
        const photoId = this.profile.userProfilePhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchDeleteMyUserPhotoByPhotoId(photoId);
          this.menuVisible = false;
          await this.fetchProfile();
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
        const photoId = this.profile.userProfilePhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchSetMyMainUserPhotoByPhotoId(photoId);
          this.menuVisible = false;
          await this.fetchProfile();
        }
      } catch (error) {
        console.error("Ошибка установки основной фотографии:", error);
        this.errorMessage = "Не удалось установить основное фото";
      }
    },
    openPasswordModal() {
      if (!this.isOwnProfile) return;
      this.isPasswordModalOpen = true;
      this.errorMessage = "";
      this.passwordData = {currentPassword: "", newPassword: ""};
    },
    closePasswordModal() {
      this.isPasswordModalOpen = false;
      this.errorMessage = "";
    },
    async updatePassword() {
      const {currentPassword, newPassword} = this.passwordData;
      if (!currentPassword || !newPassword) {
        this.errorMessage = "Заполните все поля";
        return;
      }
      if (newPassword.length < 6) {
        this.errorMessage = "Новый пароль должен быть не менее 6 символов";
        return;
      }
      try {
        await fetchChangeUserPassword({currentPassword, newPassword});
        this.closePasswordModal();
      } catch (error) {
        console.error("Ошибка смены пароля:", error);
        this.errorMessage = "Не удалось сменить пароль. Проверьте текущий пароль";
      }
    },
    openDeleteModal() {
      if (!this.isOwnProfile) return;
      this.isDeleteModalOpen = true;
    },
    closeDeleteModal() {
      this.isDeleteModalOpen = false;
    },
    async deleteAccount() {
      try {
        await fetchSoftDeleteAccount();
        this.closeDeleteModal();
        router.push({name: "Login"});
      } catch (error) {
        console.error("Ошибка удаления аккаунта:", error);
        this.errorMessage = "Не удалось удалить аккаунт";
      }
    },
    async startChat() {
      try {
        const response = await fetchGetOrCreatePrivateChatByUserId(this.userId);
        router.push({name: "Chat", params: {id: response.chatId}});
      } catch (error) {
        console.error("Ошибка создания чата:", error);
        this.errorMessage = "Не удалось начать чат";
      }
    },
    goBack() {
      router.go(-1);
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
      if (
          menu && !menu.contains(event.target) &&
          button && !button.contains(event.target)
      ) {
        this.menuVisible = false;
      }
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
  margin-bottom: 20px;
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
  gap: 15px;
  margin-top: 20px;
}

.profile-details h2 {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #f9f8f8;
}

.profile-actions {
  display: flex;
  flex-direction: column;
  width: 80%;
  gap: 10px;
}

.photo-slider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
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
  text-align: center;
  width: 300px;
}

.modal-content input[type="file"],
.modal-content input[type="password"] {
  width: 90%;
  padding: 8px 10px;
  margin: 10px 0;
  border: 1px solid #cccccc;
  border-radius: 4px;
  background-color: #f4f4f4;
  color: #343434;
  font-size: 14px;
}

.modal-content input[type="password"]::placeholder {
  color: #b3b3b3;
}

.modal-content input[type="password"]:focus {
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

.message-button {
  background-color: #1976d2;
  color: #fff;
}

.message-button:hover {
  background-color: #1565c0;
}

.error-message {
  color: #e53935;
  margin: 10px 0;
  font-size: 14px;
}

.profile-button {
  width: 100%;
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

.profile-button.danger {
  background-color: #d32f2f;
}

.profile-button.danger:hover {
  background-color: #b71c1c;
}
</style>