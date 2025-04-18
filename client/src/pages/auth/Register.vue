<template>
  <div class="container">
    <h2 class="title">Регистрация</h2>
    <form @submit.prevent="registerSubmit">
      <div class="form-group">
        <label for="username">Имя пользователя</label>
        <input type="text" id="username" v-model="formData.username" required />
        <span v-if="errors.username" class="error">{{ errors.username }}</span>
      </div>

      <div class="form-group">
        <label for="password">Пароль</label>
        <input type="password" id="password" v-model="formData.password" required />
        <span v-if="errors.password" class="error">{{ errors.password }}</span>
      </div>

      <div class="form-group">
        <label for="confirmPassword">Повторите пароль</label>
        <input type="password" id="confirmPassword" v-model="formData.confirmPassword" required />
        <span v-if="errors.confirmPassword" class="error">{{ errors.confirmPassword }}</span>
      </div>

      <div class="form-group">
        <label for="registrationKey">Ключ регистрации</label>
        <input type="text" id="registrationKey" v-model="formData.registrationKey" required />
        <span v-if="errors.registrationKey" class="error">{{ errors.registrationKey }}</span>
      </div>

      <button type="submit" class="button">Зарегистрироваться</button>
    </form>
    <div class="auth-link">
      <span>Есть аккаунт?</span>
      <a @click="$router.push({ name: 'Login' })" class="auth-link-text">Войти</a>
    </div>
  </div>
</template>

<script>
import {fetchRegister} from "@/api/auth";

export default {
  name: "RegisterComponent",
  data() {
    return {
      formData: {
        username: '',
        password: '',
        confirmPassword: '',
        registrationKey: ''
      },
      errors: {
        username: '',
        password: '',
        confirmPassword: '',
        registrationKey: ''
      }
    };
  },
  methods: {
    async registerSubmit() {
      this.clearErrors();
      try {
        await fetchRegister(this.formData);
        this.$router.push({ name: 'Dashboard' });
      } catch (error) {
        this.handleErrors(error);
      }
    },
    clearErrors() {
      this.errors.username = '';
      this.errors.password = '';
      this.errors.confirmPassword = '';
      this.errors.registrationKey = '';
    },
    handleErrors(error) {
      if (error.response && error.response.data) {
        const apiError = error.response.data;
        if (apiError.validationErrors) {
          Object.keys(apiError.validationErrors).forEach(key => {
            if (key === 'username' || key === 'password' || key === 'confirmPassword' || key === 'registrationKey') {
              this.errors[key] = apiError.validationErrors[key];
            } else {
              console.log(apiError.validationErrors[key] || apiError.message);
            }
          });
        } else {
          console.log(apiError.message);
        }
      } else {
        console.log(error.response || error.response.data);
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

.error {
  color: #ff4d4d;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.button {
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

.button:hover {
  background-color: #444444;
}

.auth-link {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #AAAAAA;
}

.auth-link span {
  margin-right: 5px;
}

.auth-link-text {
  color: #F9F8F8;
  background: none;
  text-decoration: none;
  cursor: pointer;
  font-weight: normal;
  transition: color 0.3s ease;
}
.auth-link-text:hover {
  color: #CCCCCC;
}
</style>