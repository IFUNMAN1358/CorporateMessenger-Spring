<template>
  <div class="profile-settings-container">

    <div v-cloak class="settings-content" :class="{ 'hidden': isLoading }">
      <header class="settings-header">
        <button class="back-button" @click="goBack">←</button>
        <h1>Конфиденциальность</h1>
      </header>

      <hr class="divider"/>
      <div class="settings-section">
        <div class="setting-item">
          <div class="setting-row">
            <label class="setting-label">Подтверждать заявки в контакты</label>
            <div class="control-container">
              <input type="checkbox" v-model="settings.isConfirmContactRequests" @change="markChanged"/>
            </div>
          </div>
          <span class="setting-hint">Включите, чтобы вручную одобрять запросы на добавление в контакты.</span>
        </div>
        <hr class="divider"/>
        <div class="setting-item">
          <div class="setting-row">
            <label class="setting-label">Видимость контактов</label>
            <div class="control-container">
              <select v-model="settings.contactsVisibility" @change="markChanged">
                <option v-for="option in translatedVisibilityOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </div>
          </div>
          <span class="setting-hint">Кто может видеть ваш список контактов: все, только контакты или только вы.</span>
        </div>
        <hr class="divider"/>
        <div class="setting-item">
          <div class="setting-row">
            <label class="setting-label">Видимость данных профиля</label>
            <div class="control-container">
              <select v-model="settings.profileVisibility" @change="markChanged">
                <option v-for="option in translatedVisibilityOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </div>
          </div>
          <span class="setting-hint">Кто может видеть ваши данные профиля: все, только контакты или только вы.</span>
        </div>
        <hr class="divider"/>
        <div class="setting-item">
          <div class="setting-row">
            <label class="setting-label">Видимость данных сотрудника</label>
            <div class="control-container">
              <select v-model="settings.employeeVisibility" @change="markChanged">
                <option v-for="option in translatedVisibilityOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </div>
          </div>
          <span class="setting-hint">Кто может видеть ваши данные сотрудника: все, только контакты или только вы.</span>
        </div>
        <hr class="divider"/>
        <div class="setting-item">
          <div class="setting-row">
            <label class="setting-label">Отображение аккаунта в поиске</label>
            <div class="control-container">
              <input type="checkbox" v-model="settings.isSearchable" @change="markChanged"/>
            </div>
          </div>
          <span class="setting-hint">Включите, чтобы ваш аккаунт отображался в результатах поиска.</span>
        </div>
        <hr class="divider"/>
      </div>
      <button class="save-button" :class="{ active: hasChanges }" @click="saveChanges" :disabled="!hasChanges">Изменить</button>
    </div>
  </div>
</template>

<script>
import {fetchGetMyUserSettings, fetchChangeMyUserSettings} from '@/api/resources/userSettings';
import router from '@/router/router';

export default {
  name: 'ProfileSettings',
  data() {
    return {
      isLoading: true,
      settings: {
        id: null,
        userId: null,
        isConfirmContactRequests: false,
        contactsVisibility: 'EVERYONE',
        profileVisibility: 'EVERYONE',
        employeeVisibility: 'EVERYONE',
        isSearchable: true,
        createdAt: null,
        updatedAt: null,
      },
      originalSettings: {},
      hasChanges: false,
      visibilityOptions: ['EVERYONE', 'CONTACTS', 'ONLY_ME'],
      translatedVisibilityOptions: [
        {value: 'EVERYONE', label: 'Все'},
        {value: 'CONTACTS', label: 'Только контакты'},
        {value: 'ONLY_ME', label: 'Только я'},
      ],
    };
  },
  computed: {
    hasValidSettings() {
      return Object.values(this.settings).every(
          (value) => value !== null && value !== undefined
      );
    },
  },
  async created() {
    await this.fetchSettings();
  },
  methods: {
    async fetchSettings() {
      try {
        this.isLoading = true;
        const response = await fetchGetMyUserSettings();
        this.settings = {...response};
        this.originalSettings = {...response};
      } catch (error) {
        console.error('Ошибка загрузки настроек:', error);
      } finally {
        this.isLoading = false;
      }
    },
    markChanged() {
      this.hasChanges = JSON.stringify(this.settings) !== JSON.stringify(this.originalSettings);
    },
    async saveChanges() {
      if (!this.hasChanges || !this.hasValidSettings) return;

      try {
        this.isLoading = true;
        await fetchChangeMyUserSettings(
            this.settings.isConfirmContactRequests,
            this.settings.contactsVisibility,
            this.settings.profileVisibility,
            this.settings.employeeVisibility,
            this.settings.isSearchable
        );
        this.originalSettings = {...this.settings};
        this.hasChanges = false;
      } catch (error) {
        console.error('Ошибка сохранения настроек:', error);
      } finally {
        this.isLoading = false;
      }
    },
    goBack() {
      router.go(-1);
    },
  },
};
</script>

<style scoped>
[v-cloak] {
  display: none;
}

.hidden {
  visibility: hidden;
  opacity: 0;
  transition: opacity 0.3s ease, visibility 0.3s ease;
}

.profile-settings-container {
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
  position: relative;
}

.settings-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  width: 100%;
  box-sizing: border-box;
}

.settings-header h1 {
  font-size: clamp(1.25rem, 4vw, 1.5rem);
  font-weight: bold;
  margin: 0;
  flex-grow: 1;
  text-align: center;
  overflow-wrap: break-word;
}

.back-button {
  font-size: clamp(1.25rem, 4vw, 1.5rem);
  background: none;
  border: none;
  color: #f9f8f8;
  cursor: pointer;
  padding: 0.5rem;
  flex-shrink: 0;
}

.save-button {
  font-size: clamp(0.875rem, 3vw, 1rem);
  color: #888888;
  background-color: #4b4b4b;
  border: 1px solid #555;
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
  cursor: not-allowed;
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  transition: all 0.3s ease;
}

.save-button.active {
  color: #f9f8f8;
  background-color: #5a5a5a;
  cursor: pointer;
}

.save-button.active:hover {
  background-color: #666666;
  color: #cccccc;
}

.settings-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
  padding: 0 1rem;
}

.setting-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  width: 100%;
}

.setting-label {
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
  flex-grow: 1;
}

.control-container {
  width: 7.5rem;
  flex-shrink: 0;
  display: flex;
  justify-content: flex-end;
}

.setting-row input[type="checkbox"] {
  width: 1.2rem;
  height: 1.2rem;
  appearance: none;
  background-color: #4b4b4b;
  border: 1px solid #555;
  border-radius: 0.2rem;
  position: relative;
  cursor: pointer;
}

.setting-row input[type="checkbox"]:checked::after {
  content: '✔';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #f9f8f8;
  font-size: 0.8rem;
}

.setting-row select {
  width: 7.5rem;
  padding: 0.5rem;
  border: 1px solid #555;
  border-radius: 0.5rem;
  background-color: #4b4b4b;
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
  cursor: pointer;
}

.setting-hint {
  color: #aaaaaa;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  margin-top: 0.25rem;
}

.divider {
  border: none;
  border-top: 1px solid #444;
  margin: 0.5rem 0;
}

@media (max-width: 600px) {
  .profile-settings-container {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  .settings-header h1 {
    font-size: clamp(1rem, 5vw, 1.25rem);
  }

  .save-button {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
    padding: 0.4rem 0.8rem;
    bottom: 0.75rem;
    right: 0.75rem;
  }

  .setting-label,
  .setting-row select {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .control-container,
  .setting-row select {
    width: 6rem;
  }

  .setting-hint {
    font-size: clamp(0.625rem, 3vw, 0.75rem);
  }
}

@media (min-width: 601px) and (max-width: 1024px) {
  .profile-settings-container {
    width: 85%;
    max-width: 35rem;
    padding: 1.75rem;
  }

  .settings-header h1 {
    font-size: clamp(1.25rem, 4vw, 1.5rem);
  }

  .save-button,
  .setting-label,
  .setting-row select {
    font-size: clamp(0.875rem, 3vw, 1rem);
  }

  .setting-hint {
    font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  }
}

@media (min-width: 1025px) {
  .profile-settings-container {
    max-width: 30rem;
    padding: 2rem;
  }

  .settings-header h1 {
    font-size: clamp(1.5rem, 3vw, 1.75rem);
  }

  .save-button,
  .setting-label,
  .setting-row select {
    font-size: clamp(1rem, 2.5vw, 1.125rem);
  }

  .setting-hint {
    font-size: clamp(0.875rem, 2vw, 1rem);
  }
}
</style>