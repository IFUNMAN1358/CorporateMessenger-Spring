<template>
  <div class="profile-container">
    <div v-if="isLoading" class="page-loading">
      <div class="spinner"></div>
      <p>Загрузка...</p>
    </div>

    <div v-else>
      <header class="profile-header">
        <button class="back-button" @click="goBack">←</button>
        <h1>Профиль</h1>
      </header>

      <div v-if="isPhotoLoading" class="photo-loading">
        <div class="spinner"></div>
        <p>Загрузка...</p>
      </div>

      <div class="photo-slider">
        <div class="photo-wrapper">
          <img
            :src="photos.length ? photos[currentPhotoIndex] : UserDefaultAvatar"
            alt="User Photo"
            class="user-photo"
          />
          <div v-if="photos.length" class="photo-gradient photo-gradient-bottom">
            <p class="photo-counter">
              {{ currentPhotoIndex + 1 }}/{{ photos.length }}
            </p>
          </div>
          <div class="photo-gradient photo-gradient-top"></div>
          <button
            v-if="isOwnProfile"
            class="menu-button"
            @click="toggleMenu"
            ref="menuButton"
          >⋮</button>
          <div v-if="menuVisible && isOwnProfile" class="menu-dropdown" ref="menuDropdown">
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

      <div v-if="profile.user" class="profile-details">
        <h2 class="profile-details-header">Аккаунт</h2>
        <div class="profile-detail-item">
          <button class="profile-text-button" @click="openUsernameModal">
            {{ profile.user.username }}
          </button>
          <span class="profile-detail-label">Имя пользователя</span>
        </div>
        <div class="profile-detail-item">
          <button class="profile-text-button" @click="openNameModal">
            {{ profile.user.firstName }} {{ profile.user.lastName || '' }}
          </button>
          <span class="profile-detail-label">Имя и фамилия</span>
        </div>
        <div class="profile-detail-item">
          <button class="profile-text-button" @click="openBioModal">
            {{ profile.user.bio || 'О себе' }}
          </button>
          <span class="profile-detail-label">О себе</span>
        </div>
      </div>

      <hr v-if="canShowEmployeeSection" class="divider" />

      <div v-if="canShowEmployeeSection" class="profile-actions">
        <h2 class="profile-details-header">Данные сотрудника</h2>
        <div>
          <button class="profile-button" @click="goToEmployeePage">Сотрудник</button>
        </div>
      </div>

      <hr class="divider" />

      <div class="profile-actions">
        <h2 class="profile-details-header">Люди</h2>
        <div>
          <button v-if="profile.user && isOwnProfile" class="profile-button" @click="openContactsModal">Контакты</button>
          <button v-if="profile.user && isOwnProfile" class="profile-button" @click="openBlockedUsersModal">Заблокированные пользователи</button>
          <button v-if="profile.user && !isOwnProfile" class="profile-button" @click="openForeignContactsModal">Контакты</button>
        </div>
      </div>

      <hr v-if="profile.user && isOwnProfile" class="divider" />

      <div v-if="profile.user && isOwnProfile" class="profile-actions">
        <h2 class="profile-details-header">Настройки</h2>
        <div>
          <button class="profile-button" @click="openSettings">Конфиденциальность</button>
          <button class="profile-button" @click="openPasswordModal">Сменить пароль</button>
          <button class="profile-button" @click="openDeleteModal">Удалить аккаунт</button>
        </div>
      </div>

      <hr v-if="profile.user && !isOwnProfile" class="divider" />

      <div v-if="profile.user && !isOwnProfile" class="profile-actions">
        <h2 class="profile-details-header">Действия</h2>
        <div>
          <button class="profile-button" @click="startChat">Написать</button>
          <button
            v-if="canAddContact"
            class="profile-button"
            @click="addContact"
            :disabled="isUpdating"
          >Добавить в контакты</button>
          <button
            v-if="canRemoveContact"
            class="profile-button"
            @click="removeContact"
            :disabled="isUpdating"
          >Удалить из контактов</button>
          <button
            v-if="canBlockUser"
            class="profile-button"
            @click="blockUser"
            :disabled="isUpdating"
          >Заблокировать</button>
          <button
            v-if="canUnblockUser"
            class="profile-button"
            @click="unblockUser"
            :disabled="isUpdating"
          >Разблокировать</button>
        </div>
      </div>

      <div v-if="isUploadModalOpen && isOwnProfile" class="modal-overlay" @click="closeUploadModal">
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

      <div v-if="isPasswordModalOpen && isOwnProfile" class="modal-overlay" @click="closePasswordModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="password">Текущий пароль</label>
            <input
              type="password"
              id="password"
              v-model="passwordData.password"
              @input="validateField('password')"
              :class="{ 'input-error': errors.password }"
            />
            <span v-if="errors.password" class="error">{{ errors.password }}</span>
          </div>
          <div class="form-group">
            <label for="newPassword">Новый пароль</label>
            <input
              type="password"
              id="newPassword"
              v-model="passwordData.newPassword"
              @input="validateField('newPassword')"
              :class="{ 'input-error': errors.newPassword }"
            />
            <span v-if="errors.newPassword" class="error">{{ errors.newPassword }}</span>
          </div>
          <div class="form-group">
            <label for="confirmPassword">Повторите новый пароль</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="passwordData.confirmPassword"
              @input="validateField('confirmPassword')"
              :class="{ 'input-error': errors.confirmPassword }"
            />
            <span v-if="errors.confirmPassword" class="error">{{ errors.confirmPassword }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="updatePassword"
              :disabled="hasPasswordErrors || isUpdating"
            >
              Изменить
            </button>
            <button class="modal-button cancel-button" @click="closePasswordModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isUsernameModalOpen && isOwnProfile" class="modal-overlay" @click="closeUsernameModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="newUsername">Новое имя пользователя</label>
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

      <div v-if="isNameModalOpen && isOwnProfile" class="modal-overlay" @click="closeNameModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="firstName">Имя</label>
            <input
              type="text"
              id="firstName"
              v-model="nameData.firstName"
              @input="validateField('firstName')"
              :class="{ 'input-error': errors.firstName }"
            />
            <span v-if="errors.firstName" class="error">{{ errors.firstName }}</span>
          </div>
          <div class="form-group">
            <label for="lastName">Фамилия (необязательно)</label>
            <input
              type="text"
              id="lastName"
              v-model="nameData.lastName"
              @input="validateField('lastName')"
              :class="{ 'input-error': errors.lastName }"
            />
            <span v-if="errors.lastName" class="error">{{ errors.lastName }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="updateName"
              :disabled="hasNameErrors || isUpdating"
            >
              Изменить
            </button>
            <button class="modal-button cancel-button" @click="closeNameModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isBioModalOpen && isOwnProfile" class="modal-overlay" @click="closeBioModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="newBio">О себе</label>
            <textarea
              id="newBio"
              v-model="bioData.newBio"
              rows="4"
              @input="validateField('newBio')"
              :class="{ 'input-error': errors.newBio }"
            ></textarea>
            <span v-if="errors.newBio" class="error">{{ errors.newBio }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="updateBio"
              :disabled="hasBioErrors || isUpdating"
            >
              Изменить
            </button>
            <button class="modal-button cancel-button" @click="closeBioModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isDeleteModalOpen && isOwnProfile" class="modal-overlay" @click="closeDeleteModal">
        <div class="modal-content" @click.stop>
          <p>Вы уверены, что хотите удалить аккаунт? Это действие необратимо.</p>
          <div class="modal-actions">
            <button class="modal-button danger" @click="deleteAccount" :disabled="isUpdating">Удалить</button>
            <button class="modal-button cancel-button" @click="closeDeleteModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isContactsModalOpen && isOwnProfile" class="modal-overlay" @click="closeContactsModal">
        <div class="modal-content user-list-modal" @click.stop>
          <h3>Контакты</h3>
          <div class="user-list" ref="contactsList" @scroll="handleContactsScroll">
            <div
              v-for="contact in contacts"
              :key="contact.user.id"
              class="user-item"
              @click="goToProfile(contact.user.id)"
            >
              <img
                :src="contact.photoUrl || UserDefaultAvatar"
                alt="User Photo"
                class="user-item-photo"
              />
              <span class="user-item-username">{{ contact.user.username }}</span>
            </div>
          </div>
          <p v-if="!contacts.length && !isContactsLoading" class="not-found">Контакты не найдены</p>
          <div v-if="isContactsLoading" class="modal-loading">
            <div class="spinner"></div>
            <p>Загрузка...</p>
          </div>
          <div class="modal-actions">
            <button class="modal-button cancel-button centered-button" @click="closeContactsModal">Закрыть</button>
          </div>
        </div>
      </div>

      <div v-if="isForeignContactsModalOpen && !isOwnProfile" class="modal-overlay" @click="closeForeignContactsModal">
        <div class="modal-content user-list-modal" @click.stop>
          <h3>Контакты {{ profile.user.username }}</h3>
          <div class="user-list" ref="foreignContactsList" @scroll="handleForeignContactsScroll">
            <div
              v-for="contact in foreignContacts"
              :key="contact.user.id"
              class="user-item"
              @click="goToProfile(contact.user.id)"
            >
              <img
                :src="contact.photoUrl || UserDefaultAvatar"
                alt="User Photo"
                class="user-item-photo"
              />
              <span class="user-item-username">{{ contact.user.username }}</span>
            </div>
          </div>
          <p v-if="!foreignContacts.length && !isForeignContactsLoading" class="not-found">Контакты не найдены</p>
          <div v-if="isForeignContactsLoading" class="modal-loading">
            <div class="spinner"></div>
            <p>Загрузка...</p>
          </div>
          <div class="modal-actions">
            <button class="modal-button cancel-button centered-button" @click="closeForeignContactsModal">Закрыть</button>
          </div>
        </div>
      </div>

      <div v-if="isBlockedUsersModalOpen && isOwnProfile" class="modal-overlay" @click="closeBlockedUsersModal">
        <div class="modal-content user-list-modal" @click.stop>
          <h3>Заблокированные пользователи</h3>
          <div class="user-list" ref="blockedUsersList" @scroll="handleBlockedUsersScroll">
            <div
              v-for="blockedUser in blockedUsers"
              :key="blockedUser.user.id"
              class="user-item"
              @click="goToProfile(blockedUser.user.id)"
            >
              <img
                :src="blockedUser.photoUrl || UserDefaultAvatar"
                alt="User Photo"
                class="user-item-photo"
              />
              <span class="user-item-username">{{ blockedUser.user.username }}</span>
            </div>
          </div>
          <p v-if="!blockedUsers.length && !isBlockedUsersLoading" class="not-found">Заблокированные пользователи не найдены</p>
          <div v-if="isBlockedUsersLoading" class="modal-loading">
            <div class="spinner"></div>
            <p>Загрузка...</p>
          </div>
          <div class="modal-actions">
            <button class="modal-button cancel-button centered-button" @click="closeBlockedUsersModal">Закрыть</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserDefaultAvatar from '@/assets/images/UserDefaultAvatar.png';
import { jwtDecode } from 'jwt-decode';
import authStore from '@/store/authStore';
import {
  fetchChangeUserUsername,
  fetchChangeUserFirstNameAndLastName,
  fetchChangeUserPassword,
  fetchChangeUserBio,
  fetchGetMyUserData,
  fetchGetUserByUserId,
  fetchSoftDeleteAccount,
  fetchExistsByUsername,
  fetchFindAllMyBlockedUsers,
  fetchBlockUserByUserId,
  fetchUnblockUserByUserId
} from '@/api/resources/user';
import {
  fetchDeleteMyUserPhotoByPhotoId,
  fetchDownloadMainUserPhotoByUserId,
  fetchDownloadMyUserPhotoByPhotoId,
  fetchDownloadUserPhotoByUserIdAndPhotoId,
  fetchSetMyMainUserPhotoByPhotoId,
  fetchUploadMyUserPhoto,
} from '@/api/resources/userPhoto';
import { fetchGetOrCreatePrivateChatByUserId } from '@/api/resources/chat';
import { debounce } from 'lodash';
import router from '@/router/router';
import {
  fetchAddContactByUserId,
  fetchDeleteContactByUserId,
  fetchFindAllContactUsersByUserId,
  fetchFindAllMyContactUsers
} from "@/api/resources/contact";

export default {
  name: 'ProfileComponent',
  data() {
    return {
      profile: {
        user: null,
        userSettings: null,
        userPhotos: [],
        isYouBlacklisted: false,
        isUserBlacklisted: false,
        isContact: false,
      },
      photos: [],
      currentPhotoIndex: 0,
      menuVisible: false,
      isUploadModalOpen: false,
      isPasswordModalOpen: false,
      isUsernameModalOpen: false,
      isNameModalOpen: false,
      isBioModalOpen: false,
      isDeleteModalOpen: false,
      isContactsModalOpen: false,
      isForeignContactsModalOpen: false,
      isBlockedUsersModalOpen: false,
      isUpdating: false,
      isUsernameChecking: false,
      isContactsLoading: false,
      isForeignContactsLoading: false,
      isBlockedUsersLoading: false,
      selectedFile: null,
      contacts: [],
      foreignContacts: [],
      blockedUsers: [],
      contactsPage: 0,
      foreignContactsPage: 0,
      blockedUsersPage: 0,
      contactsPageSize: 20,
      foreignContactsPageSize: 20,
      blockedUsersPageSize: 20,
      hasMoreContacts: true,
      hasMoreForeignContacts: true,
      hasMoreBlockedUsers: true,
      passwordData: {
        password: '',
        newPassword: '',
        confirmPassword: '',
      },
      usernameData: {
        newUsername: '',
      },
      nameData: {
        firstName: '',
        lastName: '',
      },
      bioData: {
        newBio: '',
      },
      errors: {
        upload: '',
        password: '',
        newPassword: '',
        confirmPassword: '',
        newUsername: '',
        firstName: '',
        lastName: '',
        newBio: '',
      },
      isOwnProfile: false,
      userId: null,
      UserDefaultAvatar,
      isLoading: true,
      isPhotoLoading: false,
    };
  },
  computed: {
    isCurrentPhotoMain() {
      const currentPhoto = this.profile.userPhotos?.[this.currentPhotoIndex];
      return currentPhoto ? currentPhoto.isMain : false;
    },
    hasPasswordErrors() {
      return (
        !!this.errors.password ||
        !!this.errors.newPassword ||
        !!this.errors.confirmPassword ||
        !this.passwordData.password.trim() ||
        !this.passwordData.newPassword.trim() ||
        !this.passwordData.confirmPassword.trim()
      );
    },
    hasUsernameErrors() {
      return (
        !!this.errors.newUsername ||
        !this.usernameData.newUsername.trim() ||
        this.isUsernameChecking ||
        this.usernameData.newUsername.trim() === this.profile.user?.username
      );
    },
    hasNameErrors() {
      return !!this.errors.firstName || !!this.errors.lastName || !this.nameData.firstName.trim();
    },
    hasBioErrors() {
      return !!this.errors.newBio || !this.bioData.newBio.trim();
    },
    canAddContact() {
      return (
        !this.profile.isYouBlacklisted &&
        !this.profile.isUserBlacklisted &&
        !this.profile.isContact
      );
    },
    canRemoveContact() {
      return (
        !this.profile.isYouBlacklisted &&
        !this.profile.isUserBlacklisted &&
        this.profile.isContact
      );
    },
    canBlockUser() {
      return !this.profile.isUserBlacklisted;
    },
    canUnblockUser() {
      return this.profile.isUserBlacklisted;
    },
    canShowEmployeeSection() {
      if (this.isOwnProfile) return true;
      if (!this.profile.userSettings) return false;
      return (
        this.profile.userSettings.employeeVisibility === 'EVERYONE' ||
        (this.profile.userSettings.employeeVisibility === 'CONTACTS' && this.profile.isContact)
      );
    },
  },
  async created() {
    this.userId = this.$route.params.id || jwtDecode(authStore.state.accessToken).sub;
    this.isOwnProfile = this.userId === jwtDecode(authStore.state.accessToken).sub;
    await this.fetchProfile();
    document.addEventListener('click', this.closeMenuOnClickOutside);
  },
  unmounted() {
    document.removeEventListener('click', this.closeMenuOnClickOutside);
  },
  beforeRouteUpdate(to, from, next) {
    if (to.params.id !== from.params.id) {
      this.userId = to.params.id || jwtDecode(authStore.state.accessToken).sub;
      this.isOwnProfile = this.userId === jwtDecode(authStore.state.accessToken).sub;
      this.photos = [];
      this.currentPhotoIndex = 0;
      this.profile = {
        user: null,
        userSettings: null,
        userPhotos: [],
        isYouBlacklisted: false,
        isUserBlacklisted: false,
        isContact: false,
      };
      this.fetchProfile();
    }
    next();
  },
  methods: {
    async fetchProfile() {
      try {
        this.isLoading = true;
        this.isPhotoLoading = true;
        let response;
        if (this.isOwnProfile) {
          response = await fetchGetMyUserData();
        } else {
          response = await fetchGetUserByUserId(this.userId);
        }

        this.profile = {
          user: response.user || {
            id: '',
            username: '',
            firstName: '',
            lastName: '',
            bio: '',
            createdAt: '',
            updatedAt: '',
          },
          userSettings: response.userSettings || null,
          userPhotos: response.userPhotos || [],
          isYouBlacklisted: response.isYouBlacklisted || false,
          isUserBlacklisted: response.isUserBlacklisted || false,
          isContact: response.isContact || false,
        };

        if (this.profile.userPhotos.length) {
          this.photos = await Promise.all(
            this.profile.userPhotos.map(async (photo) => {
              try {
                if (this.isOwnProfile) {
                  return await fetchDownloadMyUserPhotoByPhotoId(photo.id, 'big');
                } else {
                  return await fetchDownloadUserPhotoByUserIdAndPhotoId(photo.userId, photo.id, 'big');
                }
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
        console.error('Ошибка загрузки профиля:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isLoading = false;
        this.isPhotoLoading = false;
      }
    },
    goToEmployeePage() {
      router.push({ name: 'Employee', params: { id: this.userId } });
    },
    async openContactsModal() {
      if (!this.isOwnProfile) return;
      this.isContactsModalOpen = true;
      this.contacts = [];
      this.contactsPage = 0;
      this.hasMoreContacts = true;
      await this.fetchContacts();
    },
    async fetchContacts() {
      if (!this.hasMoreContacts || this.isContactsLoading) return;
      try {
        this.isContactsLoading = true;
        const response = await fetchFindAllMyContactUsers(this.contactsPage, this.contactsPageSize);
        const contactsWithPhotos = await Promise.all(
          response.map(async (contact) => {
            const photoUrl = await fetchDownloadMainUserPhotoByUserId(contact.user.id, 'small');
            return { ...contact, photoUrl };
          })
        );
        this.contacts = [...this.contacts, ...contactsWithPhotos];
        this.hasMoreContacts = response.length === this.contactsPageSize;
        this.contactsPage += 1;
      } catch (error) {
        console.error('Ошибка загрузки контактов:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isContactsLoading = false;
      }
    },
    handleContactsScroll() {
      const list = this.$refs.contactsList;
      if (!list || this.isContactsLoading || !this.hasMoreContacts) return;

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
            this.fetchContacts();
          }
        }
      } else if (scrollPosition >= totalHeight - 50) {
        this.fetchContacts();
      }
    },
    closeContactsModal() {
      this.isContactsModalOpen = false;
      this.contacts = [];
      this.contactsPage = 0;
      this.hasMoreContacts = true;
    },
    async openForeignContactsModal() {
      if (this.isOwnProfile) return;
      this.isForeignContactsModalOpen = true;
      this.foreignContacts = [];
      this.foreignContactsPage = 0;
      this.hasMoreForeignContacts = true;
      await this.fetchForeignContacts();
    },
    async fetchForeignContacts() {
      if (!this.hasMoreForeignContacts || this.isForeignContactsLoading) return;
      try {
        this.isForeignContactsLoading = true;
        const response = await fetchFindAllContactUsersByUserId(
          this.userId,
          this.foreignContactsPage,
          this.foreignContactsPageSize
        );
        const contactsWithPhotos = await Promise.all(
          response.map(async (contact) => {
            const photoUrl = await fetchDownloadMainUserPhotoByUserId(contact.user.id, 'small');
            return { ...contact, photoUrl };
          })
        );
        this.foreignContacts = [...this.foreignContacts, ...contactsWithPhotos];
        this.hasMoreForeignContacts = response.length === this.foreignContactsPageSize;
        this.foreignContactsPage += 1;
      } catch (error) {
        console.error('Ошибка загрузки контактов пользователя:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isForeignContactsLoading = false;
      }
    },
    handleForeignContactsScroll() {
      const list = this.$refs.foreignContactsList;
      if (!list || this.isForeignContactsLoading || !this.hasMoreForeignContacts) return;

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
            this.fetchForeignContacts();
          }
        }
      } else if (scrollPosition >= totalHeight - 50) {
        this.fetchForeignContacts();
      }
    },
    closeForeignContactsModal() {
      this.isForeignContactsModalOpen = false;
      this.foreignContacts = [];
      this.foreignContactsPage = 0;
      this.hasMoreForeignContacts = true;
    },
    async openBlockedUsersModal() {
      if (!this.isOwnProfile) return;
      this.isBlockedUsersModalOpen = true;
      this.blockedUsers = [];
      this.blockedUsersPage = 0;
      this.hasMoreBlockedUsers = true;
      await this.fetchBlockedUsers();
    },
    async fetchBlockedUsers() {
      if (!this.hasMoreBlockedUsers || this.isBlockedUsersLoading) return;
      try {
        this.isBlockedUsersLoading = true;
        const response = await fetchFindAllMyBlockedUsers(this.blockedUsersPage, this.blockedUsersPageSize);
        const blockedUsersWithPhotos = await Promise.all(
          response.map(async (blockedUser) => {
            const photoUrl = await fetchDownloadMainUserPhotoByUserId(blockedUser.user.id, 'small');
            return { ...blockedUser, photoUrl };
          })
        );
        this.blockedUsers = [...this.blockedUsers, ...blockedUsersWithPhotos];
        this.hasMoreBlockedUsers = response.length === this.blockedUsersPageSize;
        this.blockedUsersPage += 1;
      } catch (error) {
        console.error('Ошибка загрузки заблокированных пользователей:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isBlockedUsersLoading = false;
      }
    },
    handleBlockedUsersScroll() {
      const list = this.$refs.blockedUsersList;
      if (!list || this.isBlockedUsersLoading || !this.hasMoreBlockedUsers) return;

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
            this.fetchBlockedUsers();
          }
        }
      } else if (scrollPosition >= totalHeight - 50) {
        this.fetchBlockedUsers();
      }
    },
    closeBlockedUsersModal() {
      this.isBlockedUsersModalOpen = false;
      this.blockedUsers = [];
      this.blockedUsersPage = 0;
      this.hasMoreBlockedUsers = true;
    },
    goToProfile(userId) {
      this.closeContactsModal();
      this.closeForeignContactsModal();
      this.closeBlockedUsersModal();
      router.push({ name: 'Profile', params: { id: userId } });
    },
    async addContact() {
      try {
        this.isUpdating = true;
        await fetchAddContactByUserId(this.userId);
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка добавления в контакты:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async removeContact() {
      try {
        this.isUpdating = true;
        await fetchDeleteContactByUserId(this.userId);
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка удаления из контактов:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async blockUser() {
      try {
        this.isUpdating = true;
        await fetchBlockUserByUserId(this.userId);
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка блокировки пользователя:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async unblockUser() {
      try {
        this.isUpdating = true;
        await fetchUnblockUserByUserId(this.userId);
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка разблокировки пользователя:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    toggleMenu() {
      this.menuVisible = !this.menuVisible;
    },
    openUploadModal() {
      if (!this.isOwnProfile) return;
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
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        await fetchUploadMyUserPhoto(formData);
        this.closeUploadModal();
        await this.fetchProfile();
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
        const photoId = this.profile.userPhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchDeleteMyUserPhotoByPhotoId(photoId);
          this.menuVisible = false;
          await this.fetchProfile();
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
        const photoId = this.profile.userPhotos[this.currentPhotoIndex]?.id;
        if (photoId) {
          await fetchSetMyMainUserPhotoByPhotoId(photoId);
          this.menuVisible = false;
          await this.fetchProfile();
        }
      } catch (error) {
        console.error('Ошибка установки основной фотографии:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    openSettings() {
      if (!this.isOwnProfile) return;
      this.$router.push({ name: 'ProfileSettings' });
    },
    openPasswordModal() {
      if (!this.isOwnProfile) return;
      this.isPasswordModalOpen = true;
      this.clearErrors();
      this.passwordData = { password: '', newPassword: '', confirmPassword: '' };
    },
    closePasswordModal() {
      this.isPasswordModalOpen = false;
      this.clearErrors();
    },
    openUsernameModal() {
      if (!this.isOwnProfile) return;
      this.isUsernameModalOpen = true;
      this.clearErrors();
      this.usernameData = { newUsername: this.profile.user.username };
    },
    closeUsernameModal() {
      this.isUsernameModalOpen = false;
      this.clearErrors();
      this.isUsernameChecking = false;
    },
    openNameModal() {
      if (!this.isOwnProfile) return;
      this.isNameModalOpen = true;
      this.clearErrors();
      this.nameData = {
        firstName: this.profile.user.firstName,
        lastName: this.profile.user.lastName || '',
      };
    },
    closeNameModal() {
      this.isNameModalOpen = false;
      this.clearErrors();
    },
    openBioModal() {
      if (!this.isOwnProfile) return;
      this.isBioModalOpen = true;
      this.clearErrors();
      this.bioData = { newBio: this.profile.user.bio || '' };
    },
    closeBioModal() {
      this.isBioModalOpen = false;
      this.clearErrors();
    },
    openDeleteModal() {
      if (!this.isOwnProfile) return;
      this.isDeleteModalOpen = true;
      this.clearErrors();
    },
    closeDeleteModal() {
      this.isDeleteModalOpen = false;
      this.clearErrors();
    },
    validateField: debounce(function (field) {
      this.errors[field] = '';

      if (field === 'password') {
        const value = this.passwordData.password.trim();
        if (!value) {
          this.errors.password = 'Текущий пароль не может быть пустым';
        }
      }

      if (field === 'newPassword') {
        const value = this.passwordData.newPassword.trim();
        if (!value) {
          this.errors.newPassword = 'Новый пароль не может быть пустым';
        } else if (value.length < 10 || value.length > 30) {
          this.errors.newPassword = 'Новый пароль должен содержать от 10 до 30 символов';
        }
        if (this.passwordData.confirmPassword) {
          this.validateField('confirmPassword');
        }
      }

      if (field === 'confirmPassword') {
        const value = this.passwordData.confirmPassword.trim();
        if (!value) {
          this.errors.confirmPassword = 'Повторный пароль не может быть пустым';
        } else if (value.length < 10 || value.length > 30) {
          this.errors.confirmPassword = 'Повторный пароль должен содержать от 10 до 30 символов';
        } else if (value !== this.passwordData.newPassword.trim()) {
          this.errors.confirmPassword = 'Пароли не совпадают';
        }
      }

      if (field === 'newUsername') {
        const value = this.usernameData.newUsername.trim();

        if (!value) {
          this.errors.newUsername = 'Имя пользователя не может быть пустым';
          return;
        }

        if (value.length < 5 || value.length > 32) {
          this.errors.newUsername = 'Имя пользователя должно содержать от 5 до 32 символов';
          return;
        }

        if (value === this.profile.user.username) {
          this.errors.newUsername = 'Новое имя пользователя совпадает с текущим';
          return;
        }

        if (!/^[a-zA-Z]/.test(value)) {
          this.errors.newUsername = 'Имя пользователя должно начинаться с буквы';
          return;
        }

        if (value.startsWith('_')) {
          this.errors.newUsername = 'Имя пользователя не должно начинаться с подчеркивания';
          return;
        }

        if (!/^[a-zA-Z0-9_]*$/.test(value)) {
          this.errors.newUsername = 'Имя пользователя должно содержать только буквы, цифры и подчеркивания';
          return;
        }

        if (!/[a-zA-Z0-9]$/.test(value)) {
          this.errors.newUsername = 'Имя пользователя должно заканчиваться буквой или цифрой';
          return;
        }

        if (value.includes('__')) {
          this.errors.newUsername = 'Имя пользователя не должно содержать последовательные подчеркивания';
          return;
        }

        this.checkUsernameAvailability(value);
      }

      if (field === 'firstName') {
        const value = this.nameData.firstName.trim();
        if (!value) {
          this.errors.firstName = 'Имя не может быть пустым';
        } else if (value.length < 1 || value.length > 64) {
          this.errors.firstName = 'Имя должно содержать от 1 до 64 символов';
        }
      }

      if (field === 'lastName') {
        const value = this.nameData.lastName.trim();
        if (value.length > 64) {
          this.errors.lastName = 'Фамилия должна содержать до 64 символов';
        }
      }

      if (field === 'newBio') {
        const value = this.bioData.newBio.trim();
        if (!value) {
          this.errors.newBio = 'Биография не может быть пустой';
        } else if (value.length > 70) {
          this.errors.newBio = 'Максимальная длина биографии - 70 символов';
        }
      }
    }, 500),
    async checkUsernameAvailability(username) {
      this.isUsernameChecking = true;
      try {
        const exists = await fetchExistsByUsername(username);
        if (exists) {
          this.errors.newUsername = 'Имя пользователя уже занято';
        }
      } catch (error) {
        console.error('Ошибка проверки имени пользователя:', error);
        this.handleErrors(error, ['newUsername']);
      } finally {
        this.isUsernameChecking = false;
      }
    },
    async updatePassword() {
      this.validateField('password');
      this.validateField('newPassword');
      this.validateField('confirmPassword');

      if (this.hasPasswordErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { password, newPassword, confirmPassword } = this.passwordData;
        await fetchChangeUserPassword(
          password.trim(),
          newPassword.trim(),
          confirmPassword.trim()
        );
        this.closePasswordModal();
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка смены пароля:', error);
        this.handleErrors(error, ['password', 'newPassword', 'confirmPassword']);
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
        await fetchChangeUserUsername(newUsername.trim());
        this.closeUsernameModal();
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка смены имени пользователя:', error);
        this.handleErrors(error, ['newUsername']);
      } finally {
        this.isUpdating = false;
      }
    },
    async updateName() {
      this.validateField('firstName');
      this.validateField('lastName');

      if (this.hasNameErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { firstName, lastName } = this.nameData;
        await fetchChangeUserFirstNameAndLastName(firstName.trim(), lastName.trim() || '');
        this.closeNameModal();
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка смены имени и фамилии:', error);
        this.handleErrors(error, ['firstName', 'lastName']);
      } finally {
        this.isUpdating = false;
      }
    },
    async updateBio() {
      this.validateField('newBio');

      if (this.hasBioErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { newBio } = this.bioData;
        await fetchChangeUserBio(newBio.trim());
        this.closeBioModal();
        await this.fetchProfile();
      } catch (error) {
        console.error('Ошибка смены информации о себе:', error);
        this.handleErrors(error, ['newBio']);
      } finally {
        this.isUpdating = false;
      }
    },
    async deleteAccount() {
      try {
        this.isUpdating = true;
        await fetchSoftDeleteAccount();
        this.closeDeleteModal();
        router.push({ name: 'Login' });
      } catch (error) {
        console.error('Ошибка удаления аккаунта:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
      }
    },
    async startChat() {
      try {
        this.isUpdating = true;
        const response = await fetchGetOrCreatePrivateChatByUserId(this.userId);
        router.push({ name: 'Chat', params: { id: response.chat.id } });
      } catch (error) {
        console.error('Ошибка создания чата:', error);
        this.handleErrors(error, ['upload']);
      } finally {
        this.isUpdating = false;
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
.profile-container {
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

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.profile-header h1 {
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

.user-photo {
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

.profile-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1rem;
  margin-top: 1rem;
  padding: 0 1rem;
  width: 100%;
  box-sizing: border-box;
}

.profile-details-header {
  color: #aaaaaa;
  font-size: clamp(0.875rem, 3vw, 1rem);
  font-weight: 500;
  margin-bottom: 0.25rem;
  text-align: left;
  width: 100%;
}

.profile-detail-item {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 0.25rem;
}

.profile-text-button {
  background: none;
  border: none;
  color: #f9f8f8;
  font-size: clamp(1rem, 3.5vw, 1.125rem);
  font-weight: bold;
  text-align: left;
  cursor: pointer;
  padding: 0.25rem 0;
  transition: color 0.2s ease;
  width: 100%;
  overflow-wrap: break-word;
  word-break: break-word;
  white-space: normal;
}

.profile-text-button:hover {
  color: #cccccc;
}

.profile-detail-label {
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

.profile-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0 1rem;
}

.profile-button {
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

.profile-button:hover {
  background-color: #444444;
}

.profile-button.danger {
  background-color: #1f2937;
}

.profile-button.danger:hover {
  background-color: #374151;
}

.message-button {
  background-color: #374151;
  color: #f9f8f8;
}

.message-button:hover {
  background-color: #4b5563;
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
  justify-content: center;
  gap: 5px;
  margin-top: 1rem;
  flex-wrap: wrap;
}

.modal-button {
  padding: 0.75rem 1.5rem;
  font-size: clamp(0.875rem, 3vw, 1rem);
  font-weight: bold;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
  flex: 0 1 auto;
  min-width: 100px;
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
  flex: 0 1 auto;
  min-width: 100px;
}

.danger {
  background-color: #1f2937;
  color: #f9f8f8;
}

.danger:hover {
  background-color: #374151;
}

@media (max-width: 600px) {
  .profile-container,
  .modal-content {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  .profile-header h1 {
    font-size: clamp(1.25rem, 6vw, 1.5rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .profile-button {
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

  .profile-details-header {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .profile-text-button {
    font-size: clamp(0.875rem, 4vw, 1rem);
  }

  .profile-detail-label {
    font-size: clamp(0.625rem, 3vw, 0.75rem);
  }

  .user-item-photo {
    width: 32px;
    height: 32px;
  }

  .user-item-username {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .not-found {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .modal-button {
    padding: 0.6rem 1rem;
    min-width: 80px;
  }
}

@media (min-width: 601px) and (max-width: 1024px) {
  .profile-container,
  .modal-content {
    width: 85%;
    max-width: 35rem;
    padding: 1.75rem;
  }

  .profile-header h1 {
    font-size: clamp(1.5rem, 4vw, 1.75rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .profile-button {
    padding: 0.7rem;
  }

  .profile-details-header {
    font-size: clamp(0.875rem, 3vw, 1rem);
  }

  .profile-text-button {
    font-size: clamp(1rem, 3.5vw, 1.125rem);
  }

  .profile-detail-label {
    font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  }
}

@media (min-width: 1025px) {
  .profile-container,
  .modal-content {
    max-width: 30rem;
    padding: 2rem;
  }

  .profile-header h1 {
    font-size: clamp(1.75rem, 3vw, 2rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .profile-button {
    padding: 0.8rem;
  }

  .profile-details-header {
    font-size: clamp(1rem, 2.5vw, 1.125rem);
  }

  .profile-text-button {
    font-size: clamp(1.125rem, 3vw, 1.25rem);
  }

  .profile-detail-label {
    font-size: clamp(0.875rem, 2vw, 1rem);
  }
}
</style>