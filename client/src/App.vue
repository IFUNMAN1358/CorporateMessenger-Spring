<template>
  <div class="main-wrapper">
    <div class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style>
body, html {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  font-family: 'Roboto', sans-serif;
  background-color: #2D2D2D;
}

.main-wrapper {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.content {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 0;
}
</style>