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
            :disabled="isUsernameChecking"
          />
          <span v-if="isUsernameChecking" class="checking">Проверка...</span>
          <span v-else-if="errors.username" class="error">{{ errors.username }}</span>
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
          :disabled="hasFirstStepErrors || isUsernameChecking"
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
import { fetchRegister } from "@/api/resources/auth";
import { fetchExistsByUsername } from "@/api/resources/user";
import { debounce } from "lodash";

export default {
  name: "RegisterComponent",
  data() {
    return {
      showSecondStep: false,
      isUsernameChecking: false,
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
    validateField: debounce(function (field) {
      this.errors[field] = "";

      if (field === "username") {
        const value = this.formData.username;

        if (!value) {
          this.errors.username = "Имя пользователя не может быть пустым";
          return;
        }

        if (value.length < 5 || value.length > 32) {
          this.errors.username = "Имя пользователя должно содержать от 5 до 32 символов";
          return;
        }

        if (!/^[a-zA-Z]/.test(value)) {
          this.errors.username = "Имя пользователя должно начинаться с буквы";
          return;
        }

        if (value.startsWith("_")) {
          this.errors.username = "Имя пользователя не должно начинаться с подчеркивания";
          return;
        }

        if (!/^[a-zA-Z0-9_]*$/.test(value)) {
          this.errors.username = "Имя пользователя должно содержать только буквы, цифры и подчеркивания";
          return;
        }

        if (!/[a-zA-Z0-9]$/.test(value)) {
          this.errors.username = "Имя пользователя должно заканчиваться буквой или цифрой";
          return;
        }

        if (value.includes("__")) {
          this.errors.username = "Имя пользователя не должно содержать последовательные подчеркивания";
          return;
        }

        this.checkUsernameAvailability(value);
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
    }, 500),
    async checkUsernameAvailability(username) {
      this.isUsernameChecking = true;
      try {
        const exists = await fetchExistsByUsername(username);
        if (exists) {
          this.errors.username = "Имя пользователя уже занято";
        }
      } catch (error) {
        this.errors.username = "Ошибка проверки имени пользователя";
        console.error("Ошибка проверки имени пользователя:", error);
      } finally {
        this.isUsernameChecking = false;
      }
    },
    proceedToSecondStep() {
      this.validateField("username");
      this.validateField("password");
      this.validateField("confirmPassword");
      this.validateField("registrationKey");

      if (!this.hasFirstStepErrors && !this.isUsernameChecking) {
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
        this.$router.push({ name: "Dashboard" });
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

.title {
  text-align: center;
  font-size: clamp(1.5rem, 5vw, 1.75rem);
  font-weight: bold;
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

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #555;
  border-radius: 0.5rem;
  background-color: #2d2d2d;
  color: #f9f8f8;
  box-sizing: border-box;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.form-group input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

.button {
  width: 100%;
  padding: 0.75rem;
  background-color: #2d2d2d;
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
  font-weight: bold;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  margin-top: 1rem;
  transition: background-color 0.3s ease;
}

.button:hover {
  background-color: #444444;
}

.button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-link {
  text-align: center;
  margin-top: 1rem;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  color: #aaaaaa;
}

.auth-link span {
  margin-right: 0.3rem;
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

@media (max-width: 600px) {
  .container {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  .title {
    font-size: clamp(1.25rem, 6vw, 1.5rem);
  }

  .form-group input,
  .button {
    padding: 0.6rem;
  }

  .form-group label,
  .button,
  .form-group input {
    font-size: clamp(0.75rem, 4vw, 0.875rem);
  }

  .error,
  .checking,
  .auth-link {
    font-size: clamp(0.625rem, 3vw, 0.75rem);
  }
}

@media (min-width: 601px) and (max-width: 1024px) {
  .container {
    width: 85%;
    max-width: 35rem;
    padding: 1.75rem;
  }

  .title {
    font-size: clamp(1.5rem, 4vw, 1.75rem);
  }

  .form-group input,
  .button {
    padding: 0.7rem;
  }
}

@media (min-width: 1025px) {
  .container {
    max-width: 30rem;
    padding: 2rem;
  }

  .title {
    font-size: clamp(1.75rem, 3vw, 2rem);
  }

  .form-group input,
  .button {
    padding: 0.8rem;
  }
}
</style>