<template>
  <div class="container">
    <h2 class="title">Вход</h2>
    <form @submit.prevent="loginSubmit">
      <div class="form-group">
        <label for="username">Имя пользователя</label>
        <input type="text" id="username" v-model="loginForm.username" required />
        <span v-if="errors.username" class="error">{{ errors.username }}</span>
      </div>

      <div class="form-group">
        <label for="password">Пароль</label>
        <input type="password" id="password" v-model="loginForm.password" required />
        <span v-if="errors.password" class="error">{{ errors.password }}</span>
      </div>

      <button type="submit" class="button">Войти</button>
    </form>
    <div class="auth-link">
      <span>Нет аккаунта?</span>
      <a @click="$router.push({ name: 'Register' })" class="auth-link-text">Зарегистрироваться</a>
    </div>
  </div>
</template>

<script>
import {fetchLogin} from "@/api/auth";

export default {
  name: "LoginComponent",
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      errors: {
        username: '',
        password: ''
      }
    };
  },
  methods: {
    async loginSubmit() {
      this.clearErrors();
      try {
        await fetchLogin(this.loginForm);
        this.$router.push({ name: 'Dashboard' });
      } catch (error) {
        this.handleErrors(error);
      }
    },
    clearErrors() {
      this.errors.username = '';
      this.errors.password = '';
    },
    handleErrors(error) {
      if (error.response && error.response.data) {
        const apiError = error.response.data;
        if (apiError.validationErrors) {
          Object.keys(apiError.validationErrors).forEach(key => {
            if (key === 'username' || key === 'password') {
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