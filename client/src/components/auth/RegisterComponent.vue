<template>
  <div class="container">
    <h2 class="title">РЕГИСТРАЦИЯ</h2>
    <form @submit.prevent="registerSubmit">
      <div class="form-group" v-for="(field, index) in formFields" :key="index">
        <label :for="field.id">{{ field.label }}</label>
        <input
          :type="field.type"
          :id="field.id"
          v-model="formData[field.id]"
          :required="field.required"
          @blur="field.validation"
        />
        <span v-if="errors[field.id]" class="error-message">{{ errors[field.id] }}</span>
      </div>

      <button type="submit" class="button" :disabled="hasErrors">Зарегистрироваться</button>
    </form>
    <button @click="$router.push({ name: 'Auth' })" class="back-button">Назад</button>
  </div>
</template>

<script>
import { handleRegister } from "@/js/service/authService";

export default {
  name: "RegisterComponent",
  data() {
    return {
      formData: {
        username: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: '',
        registrationKey: ''
      },
      errors: {
        username: null,
        password: null,
        confirmPassword: null,
        firstName: null,
        lastName: null,
        registrationKey: null
      },
      formFields: [
        { id: 'username', label: 'Имя пользователя', type: 'text', required: true, validation: this.validateUsername },
        { id: 'password', label: 'Пароль', type: 'password', required: true, validation: this.validatePassword },
        { id: 'confirmPassword', label: 'Повторите пароль', type: 'password', required: true, validation: this.validateConfirmPassword },
        { id: 'firstName', label: 'Имя', type: 'text', required: true, validation: this.validateFirstName },
        { id: 'lastName', label: 'Фамилия', type: 'text', required: true, validation: this.validateLastName },
        { id: 'registrationKey', label: 'Ключ регистрации', type: 'text', required: true, validation: this.validateRegistrationKey }
      ]
    };
  },
  computed: {
    hasErrors() {
      return Object.values(this.errors).some(error => error !== null);
    }
  },
  methods: {
    validateUsername() {
      if (!this.formData.username) {
        this.errors.username = 'Введите имя пользователя';
      } else if (this.formData.username.length < 6) {
        this.errors.username = 'Имя пользователя должно быть не менее 6 символов';
      } else {
        this.errors.username = null;
      }
    },
    validatePassword() {
      if (!this.formData.password) {
        this.errors.password = 'Введите пароль';
      } else if (this.formData.password.length < 6) {
        this.errors.password = 'Пароль должен быть не менее 6 символов';
      } else {
        this.errors.password = null;
      }
    },
    validateConfirmPassword() {
      if (!this.formData.confirmPassword) {
        this.errors.confirmPassword = 'Введите пароль повторно';
      } else if (this.formData.confirmPassword !== this.formData.password) {
        this.errors.confirmPassword = 'Пароли не совпадают';
      } else {
        this.errors.confirmPassword = null;
      }
    },
    validateFirstName() {
      if (!this.formData.firstName) {
        this.errors.firstName = 'Введите имя';
      } else {
        this.errors.firstName = null;
      }
    },
    validateLastName() {
      if (!this.formData.lastName) {
        this.errors.lastName = 'Введите фамилию';
      } else {
        this.errors.lastName = null;
      }
    },
    validateRegistrationKey() {
      if (!this.formData.registrationKey) {
        this.errors.registrationKey = 'Введите ключ регистрации';
      } else {
        this.errors.registrationKey = null;
      }
    },
    async registerSubmit() {
      this.validateUsername();
      this.validatePassword();
      this.validateConfirmPassword();
      this.validateFirstName();
      this.validateLastName();
      this.validateRegistrationKey()

      if (!this.hasErrors) {
        const registerForm = {
          username: this.formData.username,
          password: this.formData.password,
          firstName: this.formData.firstName,
          lastName: this.formData.lastName,
          registrationKey: this.formData.registrationKey
        };
        await handleRegister(registerForm);
      }
    }
  }
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
  color: #F9F8F8;
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
  color: #AAAAAA;
  display: block;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #555;
  border-radius: 8px;
  background-color: #2D2D2D;
  color: #F9F8F8;
  box-sizing: border-box;
}

.error-message {
  color: #ff4d4d;
  font-size: 12px;
  margin-top: 5px;
}

.button, .back-button {
  width: 100%;
  padding: 12px;
  background-color: #2D2D2D;
  color: #F9F8F8;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 15px;
  transition: background-color 0.3s ease;
}

.back-button {
  background-color: #555555;
}

.button:hover, .back-button:hover {
  background-color: #444444;
}

@media (max-width: 600px) {
  .container {
    max-width: 90%;
    padding: 15px;
  }

  .button, .back-button {
    padding: 10px;
    font-size: 14px;
  }

  .form-group input {
    padding: 10px;
  }
}
</style>