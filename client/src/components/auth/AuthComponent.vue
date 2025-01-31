<template>
  <div class="auth-container">
    <button @click="$router.push({ name: 'Login' })" class="auth-button">ВХОД</button>
    <button @click="$router.push({ name: 'Register' })" class="auth-button">РЕГИСТРАЦИЯ</button>
  </div>
</template>

<script>
export default {
  name: "AuthComponent"
};
</script>

<style scoped>
.auth-container {
  background-color: #393939;
  border-radius: 20px;
  padding: 50px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  max-width: 350px;
  width: 100%;
}

.auth-button {
  color: #F9F8F8;
  font-weight: bold;
  font-size: 20px;
  font-family: 'Roboto', sans-serif;
  background-color: #444;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin: 15px 0;
  padding: 15px 20px;
  width: 100%;
  transition: background-color 0.3s ease;
}

.auth-button:hover {
  background-color: #5A5A5A;
}
</style>