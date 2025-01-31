<template>
  <div class="profile-container">
    <header class="profile-header">
      <button class="back-button" @click="goBack">←</button>
      <h1>{{ profile.username }}</h1>
    </header>

    <div v-if="photos.length" class="photo-slider">
      <div class="photo-wrapper">
        <img
          :src="photos[currentPhotoIndex]"
          alt="User Photo"
          class="user-photo"
        />
        <button
          class="menu-button"
          @click="toggleMenu"
          ref="menuButton">⋮</button>
        <div v-if="menuVisible" class="menu-dropdown" ref="menuDropdown">
          <button @click="openUploadModal">Загрузить фото</button>
          <button
            v-if="!isCurrentPhotoMain"
            @click="setMainPhoto">Сделать основным
          </button>
          <button v-if="photos.length" @click="deletePhoto">Удалить фото</button>
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
        src="@/assets/user/DefaultAvatar.png"
        alt="Default Photo"
        class="user-photo"
      />
      <button class="menu-button" @click="toggleMenu" ref="menuButton">⋮</button>
      <div v-if="menuVisible" class="menu-dropdown" ref="menuDropdown">
        <button @click="openUploadModal">Загрузить фото</button>
      </div>
      <p class="photo-counter">Фотографий нет</p>
    </div>

    <div class="profile-details">
      <h2>{{ profile.firstName }} {{ profile.lastName }}</h2>
      <button class="profile-button" @click="openPasswordModal">Пароль</button>
      <button class="profile-button" @click="openDeleteModal">Удалить</button>
    </div>

    <div v-if="isUploadModalOpen" class="upload-modal">
      <div class="modal-content">
        <h3>Загрузить фото</h3>
        <input type="file" @change="handleFileChange" accept="image/jpeg" />
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-actions">
          <button class="modal-button upload-button" @click="uploadPhoto">Загрузить</button>
          <button class="modal-button cancel-button" @click="closeUploadModal">Отмена</button>
        </div>
      </div>
    </div>

     <div v-if="isPasswordModalOpen" class="upload-modal">
      <div class="modal-content">
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

    <div v-if="isDeleteModalOpen" class="upload-modal">
      <div class="modal-content">
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
import {
  getUserPhoto,
  uploadPhoto as uploadPhotoApi,
  deletePhotoById,
  setMainPhotoById,
} from "@/js/service/userPhotoController";
import {deleteAccount, getProfile, updatePassword} from "@/js/service/userDataController";
import router from "@/js/config/router";

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
      errorMessage: ""
    };
  },
  computed: {
    isCurrentPhotoMain() {
      const currentPhoto = this.profile.userProfilePhotos?.[this.currentPhotoIndex];
      return currentPhoto ? currentPhoto.isMain : false;
    },
  },
  methods: {
    async fetchProfile() {
      try {
        const profileData = await getProfile();
        this.profile = profileData;

        if (profileData.userProfilePhotos?.length) {
          this.photos = await Promise.all(
            profileData.userProfilePhotos.map((photo) =>
              getUserPhoto(photo.id)
            )
          );
        }
      } catch (error) {
        console.error("Ошибка загрузки профиля:", error);
      }
    },
    toggleMenu() {
      this.menuVisible = !this.menuVisible;
    },
    openPasswordModal() {
      this.isPasswordModalOpen = true;
      this.errorMessage = "";
      this.passwordData = {
        currentPassword: "",
        newPassword: "",
      };
    },
    closePasswordModal() {
      this.isPasswordModalOpen = false;
    },
    openUploadModal() {
      this.isUploadModalOpen = true;
    },
    closeUploadModal() {
      this.isUploadModalOpen = false;
      this.selectedFile = null;
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file && file.type === "image/jpeg") {
        this.selectedFile = file;
        this.errorMessage = "";
      } else {
        this.errorMessage = "Пожалуйста, выберите файл в формате JPEG.";
        this.selectedFile = null;
      }
    },
    async uploadPhoto() {
      if (!this.selectedFile) {
        this.errorMessage = "Файл не выбран или имеет неверный формат.";
        return;
      }

      const formData = new FormData();
      formData.append("file", this.selectedFile);

      try {
        await uploadPhotoApi(formData);
        router.go(0);
      } catch (error) {
        console.error("Ошибка загрузки фотографии:", error);
      }
    },
    async deletePhoto() {
      try {
        const photoId = this.profile.userProfilePhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await deletePhotoById(photoId);
          router.go(0);
        }
      } catch (error) {
        console.error("Ошибка удаления фотографии:", error);
      }
    },
    async updatePassword() {
      const { currentPassword, newPassword } = this.passwordData;

      if (!currentPassword || !newPassword || newPassword.length < 6) {
        this.errorMessage = "Пароль должен быть не менее 6 символов.";
        return;
      }

      try {
        await updatePassword({
          currentPassword,
          newPassword,
        });
        this.closePasswordModal();
      } catch (error) {
        this.errorMessage = "Не удалось сменить пароль. Проверьте данные.";
        console.error("Ошибка смены пароля:", error);
      }
    },
    openDeleteModal() {
      this.isDeleteModalOpen = true;
    },
    closeDeleteModal() {
      this.isDeleteModalOpen = false;
    },
    async deleteAccount() {
      try {
        await deleteAccount();
      } catch (error) {
        console.error("Ошибка удаления аккаунта:", error);
      }
    },
    async setMainPhoto() {
      try {
        const photoId = this.profile.userProfilePhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await setMainPhotoById(photoId);
          router.go(0);
        }
      } catch (error) {
        console.error("Ошибка установки основной фотографии:", error);
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
  created() {
    this.fetchProfile();
    document.addEventListener("click", this.closeMenuOnClickOutside);
  },
  unmounted() {
    document.removeEventListener("click", this.closeMenuOnClickOutside);
  },
};
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background-color: #2C2C2C;
  color: #F9F8F8;
  border-radius: 10px;
  max-width: 600px;
  margin: auto;
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
  color: #F9F8F8;
  cursor: pointer;
}

.profile-details {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.profile-details h2 {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #F9F8F8;
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
  background: none;
  border: none;
  color: black;
  cursor: pointer;
  z-index: 10;
}

.menu-dropdown {
  position: absolute;
  top: 30px;
  right: 0;
  background-color: #3C3C3C;
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
  color: #F9F8F8;
  cursor: pointer;
  padding: 10px;
  text-align: left;
}

.menu-dropdown button:hover {
  background-color: #444;
}

.photo-counter {
  font-size: 14px;
  color: #F9F8F8;
  margin: 10px 10px;
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
  color: #F9F8F8;
  cursor: pointer;
  padding: 10px;
}

.upload-modal {
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

.error-message {
  color: #e53935;
  margin-top: 10px;
  font-size: 14px;
}

.profile-button {
  width: 80%;
  padding: 10px;
  font-size: 16px;
  background-color: #444;
  color: #F9F8F8;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.profile-button:hover {
  background-color: #555;
}

input[type="password"] {
  width: 90%;
  padding: 8px 10px;
  margin: 10px 0;
  border: 1px solid #CCCCCC;
  border-radius: 4px;
  background-color: #F4F4F4;
  color: #343434;
  font-size: 14px;
  transition: border-color 0.2s ease-in-out;
}

input[type="password"]::placeholder {
  color: #B3B3B3;
  font-style: normal;
}

input[type="password"]:focus {
  outline: none;
  border-color: #888888;
}
</style>