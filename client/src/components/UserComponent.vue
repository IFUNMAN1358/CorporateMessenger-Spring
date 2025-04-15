<template>
  <div class="profile-container">
    <header class="profile-header">
      <button class="back-button" @click="goBack">←</button>
      <h1>{{ profile.username }}</h1>
    </header>

    <div v-if="photos.length" class="photo-slider">
      <div class="photo-wrapper">
        <img
          :src="photos[currentPhotoIndex]"
          alt="User Photo"
          class="user-photo"
        />
        <div class="photo-navigation">
          <button v-if="photos.length > 1" @click="prevPhoto" class="slider-button">‹</button>
          <p class="photo-counter">
            {{ currentPhotoIndex + 1 }}/{{ photos.length }}
          </p>
          <button v-if="photos.length > 1" @click="nextPhoto" class="slider-button">›</button>
        </div>
      </div>
    </div>

    <div v-else class="photo-wrapper">
      <img
        src="@/assets/user/DefaultAvatar.png"
        alt="Default Photo"
        class="user-photo"
      />
      <p class="photo-counter">Фотографий нет</p>
    </div>

    <div class="profile-details">
      <h2>{{ profile.firstName }} {{ profile.lastName }}</h2>
      <button class="profile-button" @click="createPrivateChat">Написать</button>
    </div>
  </div>
</template>

<script>
import { getUserPhoto } from "@/js/service/userPhotoController";
import { getUser } from "@/js/service/userDataController";
import router from "@/js/config/router";
import {getOrCreatePrivateChat} from "@/js/service/privateChatService";

export default {
  name: "UserComponent",
  data() {
    return {
      profile: {},
      photos: [],
      currentPhotoIndex: 0,
    };
  },
  methods: {
    async fetchUser() {
      try {
        const userId = this.$route.params.id;
        const profileData = await getUser(userId);
        this.profile = profileData;

        if (profileData.userProfilePhotos?.length) {
          this.photos = await Promise.all(
            profileData.userProfilePhotos.map((photo) =>
              getUserPhoto(photo.id)
            )
          );
        }
      } catch (error) {
        console.error("Ошибка загрузки данных пользователя:", error);
      }
    },
    async createPrivateChat() {
      try {
        const data = await getOrCreatePrivateChat({ secondUserId: this.$route.params.id });
        router.push({ name: "PrivateChat", params: { id: data.privateChat.id } }).catch(() => {});
      } catch (error) {
        console.error('Get or create private chat failed:', error);
      }
    },
    goBack() {
      router.go(-1);
    },
    nextPhoto() {
      this.currentPhotoIndex = (this.currentPhotoIndex + 1) % this.photos.length;
    },
    prevPhoto() {
      this.currentPhotoIndex = (this.currentPhotoIndex - 1 + this.photos.length) % this.photos.length;
    },
  },
  created() {
    this.fetchUser();
  },
};
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background-color: #2C2C2C;
  color: #F9F8F8;
  border-radius: 10px;
  max-width: 600px;
  margin: auto;
  position: relative;
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

.profile-details {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.profile-details h2 {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #F9F8F8;
}

.photo-slider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  position: relative;
}

.photo-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-photo {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.menu-dropdown button {
  background: none;
  border: none;
  color: #F9F8F8;
  cursor: pointer;
  padding: 10px;
  text-align: left;
}

.menu-dropdown button:hover {
  background-color: #444;
}

.photo-counter {
  font-size: 14px;
  color: #F9F8F8;
  margin: 10px 10px;
}

.photo-navigation {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
}

.slider-button {
  font-size: 24px;
  background: none;
  border: none;
  color: #F9F8F8;
  cursor: pointer;
  padding: 10px;
}

.profile-button {
  width: 80%;
  padding: 10px;
  font-size: 16px;
  background-color: #444;
  color: #F9F8F8;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.profile-button:hover {
  background-color: #555;
}

input[type="password"] {
  width: 90%;
  padding: 8px 10px;
  margin: 10px 0;
  border: 1px solid #CCCCCC;
  border-radius: 4px;
  background-color: #F4F4F4;
  color: #343434;
  font-size: 14px;
  transition: border-color 0.2s ease-in-out;
}

input[type="password"]::placeholder {
  color: #B3B3B3;
  font-style: normal;
}

input[type="password"]:focus {
  outline: none;
  border-color: #888888;
}
</style>