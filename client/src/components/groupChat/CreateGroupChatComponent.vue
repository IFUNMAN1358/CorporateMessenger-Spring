<template>
  <div class="container">
    <header class="profile-header">
      <button class="back-button" @click="goBack">←</button>
      <h2 class="title">Создать групповой чат</h2>
    </header>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="chatName">Название чата</label>
        <input
          type="text"
          id="chatName"
          v-model="formData.name"
          required
        />
      </div>

      <div class="form-group">
        <label for="chatDescription">Описание (опционально)</label>
        <input
          type="text"
          id="chatDescription"
          v-model="formData.description"
        />
      </div>

      <div class="form-group">
        <label for="chatPhoto">Фото чата (опционально)</label>
        <input
          type="file"
          id="chatPhoto"
          accept="image/jpeg, image/png"
          @change="handleFileChange"
        />
        <div v-if="preview" class="image-preview">
          <img :src="preview" alt="Предпросмотр фото" />
        </div>
      </div>

      <div class="form-group">
        <label class="inline-label">
          Сделать чат публичным
          <input
            type="checkbox"
            v-model="formData.isPublic"
          />
        </label>
      </div>

      <button type="submit" class="button">Создать</button>
    </form>
  </div>
</template>

<script>
import {createGroupChat} from "@/js/service/groupChatController";
import router from "@/js/config/router";

export default {
  name: "CreateGroupChatComponent",
  data() {
    return {
      formData: {
        file: null,
        name: "",
        description: "",
        isPublic: false
      },
      preview: null
    };
  },
  methods: {
    goBack() {
      router.go(-1);
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file && (file.type === "image/jpeg" || file.type === "image/png")) {
        this.formData.file = file;
        const reader = new FileReader();
        reader.onload = (e) => {
          this.preview = e.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        console.error("Пожалуйста, выберите файл в формате JPEG или PNG.");
      }
    },
    async handleSubmit() {
      if (!this.formData.name) {
        return;
      }

      const formData = new FormData();
      formData.append("name", this.formData.name);
      formData.append("description", this.formData.description || "");
      formData.append("isPublic", this.formData.isPublic);
      if (this.formData.file) {
        formData.append("file", this.formData.file);
      }

      try {
        await createGroupChat(formData);
        router.push({ name: "Dialogs" }).catch(() => {});
      } catch (error) {
        console.error("Ошибка создания группового чата:", error);
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

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.back-button {
  font-size: 24px;
  background: none;
  border: none;
  color: #F9F8F8;
  cursor: pointer;
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

.inline-label {
  display: flex;
  align-items: center;
  gap: 10px;
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

.image-preview {
  margin-top: 10px;
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 150px;
  border-radius: 10px;
  object-fit: cover;
}
</style>
