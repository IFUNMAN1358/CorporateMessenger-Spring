<template>
  <div class="employee-container">
    <div v-if="isLoading" class="page-loading">
      <div class="spinner"></div>
      <p>Загрузка...</p>
    </div>

    <div v-else>
      <header class="employee-header">
        <button class="back-button" @click="goBack">←</button>
        <h1>Сотрудник</h1>
      </header>

      <div v-if="isEmployeeLoading" class="photo-loading">
        <div class="spinner"></div>
        <p>Загрузка...</p>
      </div>

      <div class="photo-slider">
        <div class="photo-wrapper">
          <img
            :src="employeePhoto || UserDefaultAvatar"
            alt="Employee Photo"
            class="user-photo"
          />
          <button
            v-if="isOwnProfile"
            class="menu-button"
            @click="toggleEmployeeMenu"
            ref="employeeMenuButton"
          >⋮</button>
          <div v-if="employeeMenuVisible && isOwnProfile" class="menu-dropdown" ref="employeeMenuDropdown">
            <button @click="openEmployeePhotoUploadModal">Загрузить фото</button>
            <button v-if="employeePhoto" @click="deleteEmployeePhoto">Удалить фото</button>
          </div>
        </div>
      </div>

      <hr class="divider" />

      <div class="employee-details">
        <h2 class="employee-details-header">Аккаунт</h2>
        <div class="employee-detail-item">
          <span class="employee-text-button">{{ employee?.department || 'Не указано' }}</span>
          <span class="employee-detail-label">Отдел</span>
        </div>
        <div class="employee-detail-item">
          <span class="employee-text-button">{{ employee?.position || 'Не указано' }}</span>
          <span class="employee-detail-label">Должность</span>
        </div>
        <div class="employee-detail-item">
          <span class="employee-text-button">{{ employee?.description || 'Не указано' }}</span>
          <span class="employee-detail-label">Описание</span>
        </div>
        <div class="employee-detail-item">
          <span class="employee-text-button">{{ employee?.workSchedule || 'Не указано' }}</span>
          <span class="employee-detail-label">График работы</span>
        </div>
      </div>

      <hr v-if="isOwnProfile" class="divider" />

      <div v-if="isOwnProfile" class="employee-actions">
        <button v-if="employee" class="employee-button" @click="openEmployeeEditModal">Редактировать</button>
        <button v-if="employee" class="employee-button" @click="clearEmployeeData">Очистить данные</button>
      </div>

      <div v-if="isEmployeePhotoUploadModalOpen && isOwnProfile" class="modal-overlay" @click="closeEmployeePhotoUploadModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="employeePhotoUpload">Выберите фото сотрудника</label>
            <input
              type="file"
              id="employeePhotoUpload"
              @change="handleEmployeePhotoChange"
              accept="image/jpeg"
              :class="{ 'input-error': errors.employeePhoto }"
            />
            <span v-if="errors.employeePhoto" class="error">{{ errors.employeePhoto }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="uploadEmployeePhoto"
              :disabled="isUpdating || !selectedEmployeePhoto"
            >
              Загрузить
            </button>
            <button class="modal-button cancel-button" @click="closeEmployeePhotoUploadModal">Отмена</button>
          </div>
        </div>
      </div>

      <div v-if="isEmployeeEditModalOpen && isOwnProfile" class="modal-overlay" @click="closeEmployeeEditModal">
        <div class="modal-content" @click.stop>
          <div class="form-group">
            <label for="department">Отдел</label>
            <input
              type="text"
              id="department"
              v-model="employeeData.department"
              @input="validateField('department')"
              :class="{ 'input-error': errors.department }"
            />
            <span v-if="errors.department" class="error">{{ errors.department }}</span>
          </div>
          <div class="form-group">
            <label for="position">Должность</label>
            <input
              type="text"
              id="position"
              v-model="employeeData.position"
              @input="validateField('position')"
              :class="{ 'input-error': errors.position }"
            />
            <span v-if="errors.position" class="error">{{ errors.position }}</span>
          </div>
          <div class="form-group">
            <label for="description">Описание</label>
            <textarea
              id="description"
              v-model="employeeData.description"
              rows="4"
              @input="validateField('description')"
              :class="{ 'input-error': errors.description }"
            ></textarea>
            <span v-if="errors.description" class="error">{{ errors.description }}</span>
          </div>
          <div class="form-group">
            <label for="workSchedule">График работы</label>
            <input
              type="text"
              id="workSchedule"
              v-model="employeeData.workSchedule"
              @input="validateField('workSchedule')"
              :class="{ 'input-error': errors.workSchedule }"
            />
            <span v-if="errors.workSchedule" class="error">{{ errors.workSchedule }}</span>
          </div>
          <div class="modal-actions">
            <button
              class="modal-button upload-button"
              @click="updateEmployeeData"
              :disabled="hasEmployeeErrors || isUpdating"
            >
              Сохранить
            </button>
            <button class="modal-button cancel-button" @click="closeEmployeeEditModal">Отмена</button>
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
  fetchGetMyEmployee,
  fetchGetEmployeeByUserId,
  fetchUpdateMyEmployee,
  fetchClearMyEmployee
} from '@/api/resources/employee';
import { debounce } from 'lodash';
import router from '@/router/router';
import {
  fetchDeleteMyEmployeePhoto,
  fetchDownloadEmployeePhotoByUserId,
  fetchDownloadMyEmployeePhoto,
  fetchUploadOrUpdateMyEmployeePhoto
} from "@/api/resources/employeePhoto";

export default {
  name: 'EmployeeComponent',
  data() {
    return {
      employee: null,
      employeePhoto: null,
      isLoading: true,
      isEmployeeLoading: false,
      isUpdating: false,
      isOwnProfile: false,
      userId: null,
      employeeMenuVisible: false,
      isEmployeePhotoUploadModalOpen: false,
      isEmployeeEditModalOpen: false,
      selectedEmployeePhoto: null,
      employeeData: {
        department: '',
        position: '',
        description: '',
        workSchedule: '',
      },
      errors: {
        employeePhoto: '',
        department: '',
        position: '',
        description: '',
        workSchedule: '',
      },
      UserDefaultAvatar,
    };
  },
  computed: {
    hasEmployeeErrors() {
      return (
        !!this.errors.department ||
        !!this.errors.position ||
        !!this.errors.description ||
        !!this.errors.workSchedule
      );
    },
  },
  async created() {
    this.userId = this.$route.params.id || jwtDecode(authStore.state.accessToken).sub;
    this.isOwnProfile = this.userId === jwtDecode(authStore.state.accessToken).sub;
    await this.fetchEmployeeData();
    document.addEventListener('click', this.closeEmployeeMenuOnClickOutside);
  },
  unmounted() {
    document.removeEventListener('click', this.closeEmployeeMenuOnClickOutside);
  },
  methods: {
    async fetchEmployeeData() {
      try {
        this.isLoading = true;
        this.isEmployeeLoading = true;
        const employeeResponse = this.isOwnProfile
          ? await fetchGetMyEmployee()
          : await fetchGetEmployeeByUserId(this.userId);
        this.employee = employeeResponse?.employee || null;
        if (employeeResponse?.employeePhoto) {
          this.employeePhoto = this.isOwnProfile
            ? await fetchDownloadMyEmployeePhoto('big')
            : await fetchDownloadEmployeePhotoByUserId(this.userId, 'big');
        } else {
          this.employeePhoto = null;
        }
      } catch (error) {
        console.error('Ошибка загрузки данных сотрудника:', error);
        this.employee = null;
        this.employeePhoto = null;
        this.handleErrors(error, ['employeePhoto']);
      } finally {
        this.isLoading = false;
        this.isEmployeeLoading = false;
      }
    },
    toggleEmployeeMenu() {
      this.employeeMenuVisible = !this.employeeMenuVisible;
    },
    closeEmployeeMenuOnClickOutside(event) {
      const menu = this.$refs.employeeMenuDropdown;
      const button = this.$refs.employeeMenuButton;
      if (menu && !menu.contains(event.target) && button && !button.contains(event.target)) {
        this.employeeMenuVisible = false;
      }
    },
    openEmployeePhotoUploadModal() {
      this.isEmployeePhotoUploadModalOpen = true;
      this.clearErrors();
      this.selectedEmployeePhoto = null;
    },
    closeEmployeePhotoUploadModal() {
      this.isEmployeePhotoUploadModalOpen = false;
      this.selectedEmployeePhoto = null;
      this.clearErrors();
    },
    handleEmployeePhotoChange(event) {
      const file = event.target.files[0];
      this.errors.employeePhoto = '';
      this.selectedEmployeePhoto = null;

      if (!file) {
        this.errors.employeePhoto = 'Файл не выбран';
        return;
      }
      if (file.type !== 'image/jpeg') {
        this.errors.employeePhoto = 'Пожалуйста, выберите файл в формате JPEG';
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        this.errors.employeePhoto = 'Файл слишком большой (максимум 5 МБ)';
        return;
      }

      this.selectedEmployeePhoto = file;
    },
    async uploadEmployeePhoto() {
      if (!this.selectedEmployeePhoto) return;

      try {
        this.isUpdating = true;
        const formData = new FormData();
        formData.append('file', this.selectedEmployeePhoto);
        await fetchUploadOrUpdateMyEmployeePhoto(formData);
        this.closeEmployeePhotoUploadModal();
        await this.fetchEmployeeData();
      } catch (error) {
        console.error('Ошибка загрузки фотографии сотрудника:', error);
        this.handleErrors(error, ['employeePhoto']);
      } finally {
        this.isUpdating = false;
      }
    },
    async deleteEmployeePhoto() {
      try {
        this.isUpdating = true;
        await fetchDeleteMyEmployeePhoto();
        this.employeeMenuVisible = false;
        await this.fetchEmployeeData();
      } catch (error) {
        console.error('Ошибка удаления фотографии сотрудника:', error);
        this.handleErrors(error, ['employeePhoto']);
      } finally {
        this.isUpdating = false;
      }
    },
    openEmployeeEditModal() {
      this.isEmployeeEditModalOpen = true;
      this.clearErrors();
      this.employeeData = {
        department: this.employee?.department || '',
        position: this.employee?.position || '',
        description: this.employee?.description || '',
        workSchedule: this.employee?.workSchedule || '',
      };
    },
    closeEmployeeEditModal() {
      this.isEmployeeEditModalOpen = false;
      this.clearErrors();
    },
    async updateEmployeeData() {
      this.validateField('department');
      this.validateField('position');
      this.validateField('description');
      this.validateField('workSchedule');

      if (this.hasEmployeeErrors) {
        return;
      }

      try {
        this.isUpdating = true;
        const { department, position, description, workSchedule } = this.employeeData;
        await fetchUpdateMyEmployee({
          newDepartment: department.trim() || null,
          newPosition: position.trim() || null,
          newDescription: description.trim() || null,
          newWorkSchedule: workSchedule.trim() || null,
          newLeaderId: null,
        });
        this.closeEmployeeEditModal();
        await this.fetchEmployeeData();
      } catch (error) {
        console.error('Ошибка обновления данных сотрудника:', error);
        this.handleErrors(error, ['department', 'position', 'description', 'workSchedule']);
      } finally {
        this.isUpdating = false;
      }
    },
    async clearEmployeeData() {
      try {
        this.isUpdating = true;
        await fetchClearMyEmployee();
        await this.fetchEmployeeData();
      } catch (error) {
        console.error('Ошибка очистки данных сотрудника:', error);
        this.handleErrors(error, ['employeePhoto']);
      } finally {
        this.isUpdating = false;
      }
    },
    validateField: debounce(function (field) {
      this.errors[field] = '';

      if (field === 'department') {
        const value = this.employeeData.department.trim();
        if (value.length > 255) {
          this.errors.department = 'Отдел должен содержать до 255 символов';
        }
      }

      if (field === 'position') {
        const value = this.employeeData.position.trim();
        if (value.length > 255) {
          this.errors.position = 'Должность должна содержать до 255 символов';
        }
      }

      if (field === 'description') {
        const value = this.employeeData.description.trim();
        if (value.length > 255) {
          this.errors.description = 'Описание должно содержать до 255 символов';
        }
      }

      if (field === 'workSchedule') {
        const value = this.employeeData.workSchedule.trim();
        if (value.length > 255) {
          this.errors.workSchedule = 'График работы должен содержать до 255 символов';
        }
      }
    }, 500),
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
    goBack() {
      router.go(-1);
    },
  },
};
</script>

<style scoped>
.employee-container {
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

.employee-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.employee-header h1 {
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

.employee-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1rem;
  margin-top: 1rem;
  padding: 0 1rem;
  width: 100%;
  box-sizing: border-box;
}

.employee-details-header {
  color: #aaaaaa;
  font-size: clamp(0.875rem, 3vw, 1rem);
  font-weight: 500;
  margin-bottom: 0.25rem;
  text-align: left;
  width: 100%;
}

.employee-detail-item {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 0.25rem;
}

.employee-text-button {
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

.employee-text-button:hover {
  color: #cccccc;
}

.employee-detail-label {
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

.employee-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0 1rem;
}

.employee-button {
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

.employee-button:hover {
  background-color: #444444;
}

.employee-button.danger {
  background-color: #1f2937;
}

.employee-button.danger:hover {
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
  .employee-container,
  .modal-content {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  .employee-header h1 {
    font-size: clamp(1.25rem, 6vw, 1.5rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .employee-button {
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

  .employee-details-header {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .employee-text-button {
    font-size: clamp(0.875rem, 4vw, 1rem);
  }

  .employee-detail-label {
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
  .employee-container,
  .modal-content {
    width: 85%;
    max-width: 35rem;
    padding: 1.75rem;
  }

  .employee-header h1 {
    font-size: clamp(1.5rem, 4vw, 1.75rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .employee-button {
    padding: 0.7rem;
  }

  .employee-details-header {
    font-size: clamp(0.875rem, 3vw, 1rem);
  }

  .employee-text-button {
    font-size: clamp(1rem, 3.5vw, 1.125rem);
  }

  .employee-detail-label {
    font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  }
}

@media (min-width: 1025px) {
  .employee-container,
  .modal-content {
    max-width: 30rem;
    padding: 2rem;
  }

  .employee-header h1 {
    font-size: clamp(1.75rem, 3vw, 2rem);
  }

  .form-group input,
  .form-group textarea,
  .modal-button,
  .employee-button {
    padding: 0.8rem;
  }

  .employee-details-header {
    font-size: clamp(1rem, 2.5vw, 1.125rem);
  }

  .employee-text-button {
    font-size: clamp(1.125rem, 3vw, 1.25rem);
  }

  .employee-detail-label {
    font-size: clamp(0.875rem, 2vw, 1rem);
  }
}
</style>