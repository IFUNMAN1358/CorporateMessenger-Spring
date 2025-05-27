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

import {fetchLogin} from "@/api/resources/auth";

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
        console.log(error.response);
      }
    }
  }
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
  color: #F9F8F8;
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
  color: #AAAAAA;
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
  background-color: #2D2D2D;
  color: #F9F8F8;
  box-sizing: border-box;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.error {
  color: #ff4d4d;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  margin-top: 0.25rem;
  display: block;
}

.button {
  width: 100%;
  padding: 0.75rem;
  background-color: #2D2D2D;
  color: #F9F8F8;
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

.auth-link {
  text-align: center;
  margin-top: 1rem;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  color: #AAAAAA;
}

.auth-link span {
  margin-right: 0.3rem;
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