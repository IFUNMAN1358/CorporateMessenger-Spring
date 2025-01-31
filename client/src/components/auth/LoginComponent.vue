<template>
  <div class="container">
    <h2 class="title">ВХОД</h2>
    <form @submit.prevent="loginSubmit">
      <div class="form-group">
        <label for="username">Имя пользователя</label>
        <input type="text" id="username" v-model="loginForm.username" required />
      </div>

      <div class="form-group">
        <label for="password">Пароль</label>
        <input type="password" id="password" v-model="loginForm.password" required />
      </div>

      <button type="submit" class="button">Войти</button>
    </form>
    <button @click="$router.push({ name: 'Auth' })" class="back-button">Назад</button>
  </div>
</template>

<script>
import { handleLogin } from "@/js/service/authService";

export default {
  name: "LoginComponent",
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      }
    };
  },
  methods: {
    async loginSubmit() {
      await handleLogin(this.loginForm);
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