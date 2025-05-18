<template>
  <div class="container">
    <h2 class="title">Регистрация</h2>
    <form @submit.prevent="handleSubmit">

      <div v-if="!showSecondStep">
        <div class="form-group">
          <label for="username">Имя пользователя</label>
          <input
            type="text"
            id="username"
            v-model="formData.username"
            @input="validateField('username')"
            :class="{ 'input-error': errors.username }"
          />
          <span v-if="errors.username" class="error">{{ errors.username }}</span>
        </div>

        <div class="form-group">
          <label for="password">Пароль</label>
          <input
            type="password"
            id="password"
            v-model="formData.password"
            @input="validateField('password')"
            :class="{ 'input-error': errors.password }"
          />
          <span v-if="errors.password" class="error">{{ errors.password }}</span>
        </div>

        <div class="form-group">
          <label for="confirmPassword">Повторите пароль</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="formData.confirmPassword"
            @input="validateField('confirmPassword')"
            :class="{ 'input-error': errors.confirmPassword }"
          />
          <span v-if="errors.confirmPassword" class="error">{{ errors.confirmPassword }}</span>
        </div>

        <div class="form-group">
          <label for="registrationKey">Ключ регистрации</label>
          <input
            type="text"
            id="registrationKey"
            v-model="formData.registrationKey"
            @input="validateField('registrationKey')"
            :class="{ 'input-error': errors.registrationKey }"
          />
          <span v-if="errors.registrationKey" class="error">{{ errors.registrationKey }}</span>
        </div>

        <button
          type="button"
          class="button"
          @click="proceedToSecondStep"
          :disabled="hasFirstStepErrors"
        >
          Далее
        </button>
      </div>

      <div v-else>
        <div class="form-group">
          <label for="firstName">Имя</label>
          <input
            type="text"
            id="firstName"
            v-model="formData.firstName"
            @input="validateField('firstName')"
            :class="{ 'input-error': errors.firstName }"
          />
          <span v-if="errors.firstName" class="error">{{ errors.firstName }}</span>
        </div>

        <div class="form-group">
          <label for="lastName">Фамилия (опционально)</label>
          <input
            type="text"
            id="lastName"
            v-model="formData.lastName"
            @input="validateField('lastName')"
            :class="{ 'input-error': errors.lastName }"
          />
          <span v-if="errors.lastName" class="error">{{ errors.lastName }}</span>
        </div>

        <div class="button-group">
          <button type="button" class="button button-back" @click="showSecondStep = false">
            Назад
          </button>
          <button
            type="submit"
            class="button"
            :disabled="hasSecondStepErrors"
          >
            Зарегистрироваться
          </button>
        </div>
      </div>
    </form>
    <div class="auth-link">
      <span>Есть аккаунт?</span>
      <a @click="$router.push({ name: 'Login' })" class="auth-link-text">Войти</a>
    </div>
  </div>
</template>

<script>

import {fetchRegister} from "@/api/resources/auth";

export default {
  name: "RegisterComponent",
  data() {
    return {
      showSecondStep: false,
      formData: {
        username: "",
        password: "",
        confirmPassword: "",
        registrationKey: "",
        firstName: "",
        lastName: "",
      },
      errors: {
        username: "",
        password: "",
        confirmPassword: "",
        registrationKey: "",
        firstName: "",
        lastName: "",
      },
    };
  },
  computed: {
    hasFirstStepErrors() {
      return (
          !!this.errors.username ||
          !!this.errors.password ||
          !!this.errors.confirmPassword ||
          !!this.errors.registrationKey ||
          !this.formData.username ||
          !this.formData.password ||
          !this.formData.confirmPassword ||
          !this.formData.registrationKey
      );
    },
    hasSecondStepErrors() {
      return !!this.errors.firstName || !!this.errors.lastName || !this.formData.firstName;
    },
  },
  methods: {
    validateField(field) {
      this.errors[field] = "";

      if (field === "username") {
        const value = this.formData.username;
        if (!value) {
          this.errors.username = "Имя пользователя не может быть пустым";
        } else if (value.length < 5 || value.length > 32) {
          this.errors.username = "Имя пользователя должно содержать от 5 до 32 символов";
        }
      }

      if (field === "password") {
        const value = this.formData.password;
        if (!value) {
          this.errors.password = "Пароль не может быть пустым";
        } else if (value.length < 10 || value.length > 30) {
          this.errors.password = "Пароль должен содержать от 10 до 30 символов";
        }
        if (this.formData.confirmPassword) {
          this.validateField("confirmPassword");
        }
      }

      if (field === "confirmPassword") {
        const value = this.formData.confirmPassword;
        if (!value) {
          this.errors.confirmPassword = "Повторный пароль не может быть пустым";
        } else if (value.length < 10 || value.length > 30) {
          this.errors.confirmPassword = "Повторный пароль должен содержать от 10 до 30 символов";
        } else if (value !== this.formData.password) {
          this.errors.confirmPassword = "Пароли не совпадают";
        }
      }

      if (field === "registrationKey") {
        const value = this.formData.registrationKey;
        if (!value) {
          this.errors.registrationKey = "Ключ регистрации не может быть пустым";
        } else if (value.length !== 12) {
          this.errors.registrationKey = "Ключ регистрации должен содержать 12 символов";
        }
      }

      if (field === "firstName") {
        const value = this.formData.firstName;
        if (!value) {
          this.errors.firstName = "Имя не может быть пустым";
        } else if (value.length < 1 || value.length > 64) {
          this.errors.firstName = "Имя должно содержать от 1 до 64 символов";
        }
      }

      if (field === "lastName") {
        const value = this.formData.lastName;
        if (value && value.length > 64) {
          this.errors.lastName = "Фамилия должна содержать до 64 символов";
        }
      }
    },
    proceedToSecondStep() {
      this.validateField("username");
      this.validateField("password");
      this.validateField("confirmPassword");
      this.validateField("registrationKey");

      if (!this.hasFirstStepErrors) {
        this.showSecondStep = true;
      }
    },
    async handleSubmit() {
      this.validateField("firstName");
      this.validateField("lastName");

      if (this.hasSecondStepErrors) {
        return;
      }

      this.clearErrors();
      try {
        await fetchRegister(this.formData);
        this.$router.push({name: "Dashboard"});
      } catch (error) {
        this.handleErrors(error);
      }
    },
    clearErrors() {
      Object.keys(this.errors).forEach((key) => {
        this.errors[key] = "";
      });
    },
    handleErrors(error) {
      if (error.response && error.response.data) {
        const apiError = error.response.data;
        if (apiError.validationErrors) {
          Object.keys(apiError.validationErrors).forEach((key) => {
            if (key in this.errors) {
              this.errors[key] = apiError.validationErrors[key];
            } else {
              console.log(apiError.validationErrors[key] || apiError.message);
            }
          });
        } else {
          console.log(apiError.message);
        }
      } else {
        console.log(error.response || error.message);
      }
    },
  },
};
</script>

<style scoped>
.container {
  background-color: #393939;
  border-radius: 20px;
  padding: 20px;
  max-width: 450px;
  width: 100%;
  margin: 0 auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  color: #f9f8f8;
}

.title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.form-group {
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  color: #aaaaaa;
  display: block;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #555;
  border-radius: 8px;
  background-color: #2d2d2d;
  color: #f9f8f8;
  box-sizing: border-box;
}

.input-error {
  border-color: #ff4d4d;
}

.error {
  color: #ff4d4d;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.button {
  width: 100%;
  padding: 12px;
  background-color: #2d2d2d;
  color: #f9f8f8;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 15px;
  transition: background-color 0.3s ease;
}

.button:hover {
  background-color: #444444;
}

.button:disabled {
  background-color: #555;
  cursor: not-allowed;
}

.button-group {
  display: flex;
  gap: 10px;
}

.button-back {
  background-color: #555;
}

.button-back:hover {
  background-color: #666;
}

.auth-link {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #aaaaaa;
}

.auth-link span {
  margin-right: 5px;
}

.auth-link-text {
  color: #f9f8f8;
  background: none;
  text-decoration: none;
  cursor: pointer;
  font-weight: normal;
  transition: color 0.3s ease;
}

.auth-link-text:hover {
  color: #cccccc;
}
</style>