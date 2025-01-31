<template>
  <div v-if="groupChat" class="profile-container">
    <header class="profile-header">
      <button class="back-button" @click="goBack">←</button>
      <h1>{{ groupChat.name }}</h1>
    </header>

    <div class="photo-slider">
      <div class="photo-wrapper">
        <img
          :src="groupChatPhoto || groupChatDefaultAvatar"
          alt="User Photo"
          class="user-photo"
        />
        <button
          v-if="groupChat.ownerId === userId"
          class="menu-button"
          @click="toggleGroupChatMenu"
          ref="groupChatMenuButton">⋮</button>
        <div v-if="groupChatMenuVisible" class="menu-dropdown" ref="groupChatMenuDropdown">
          <button v-if="!groupChatPhoto && groupChat.ownerId === userId" @click="openUploadModal">Загрузить фото</button>
          <button v-if="groupChatPhoto && groupChat.ownerId === userId" @click="openUploadModal">Сменить фото</button>
          <button v-if="groupChatPhoto && groupChat.ownerId === userId" @click="deletePhoto">Удалить фото</button>
        </div>
      </div>
    </div>

    <!--  PAGE BUTTONS  -->
    <div class="profile-details">
      <h2>{{ groupChat.description }}</h2>
      <h2>Публичный: {{ groupChat.isPublic === true ? "да" : "нет" }}</h2>
      <button
          class="profile-button"
          @click="openListMembersModal">Список участников
      </button>
      <button
          v-if="groupChat.ownerId === userId" class="profile-button"
          @click="openAddMembersModal">Добавить участников
      </button>
      <button
          v-if="groupChat.ownerId === userId" class="profile-button"
          @click="openMetadataModal">Сменить название и описание
      </button>
      <button
          v-if="groupChat.ownerId === userId" class="profile-button"
          @click="changeChatStatus">Сделать чат {{ groupChat.isPublic === true ? "закрытым" : "открытым" }}
      </button>
      <button
          v-if="groupChat.ownerId !== userId" class="profile-button"
          @click="leaveFromChat">Покинуть чат
      </button>
      <button
          v-if="groupChat.ownerId === userId" class="profile-button"
          @click="openDeleteModal">Удалить чат
      </button>
    </div>

    <!--  MODAL FOR MEMBERS  -->
    <div v-if="isListMembersModalOpen" class="modal-overlay" @click.self="closeListMembersModal">
      <div class="modal-content">
        <header class="profile-header">
          <button class="back-button" @click="closeListMembersModal">←</button>
          <h2>Участники</h2>
        </header>
        <div
          v-for="(member, index) in groupChatMembers"
          :key="member.id"
          class="member-item"
        >
          <img
            :src="member.userPhoto || defaultAvatar"
            alt="avatar"
            class="member-avatar"
            @click="handleMemberProfileClick(member)"
          />
          <div class="member-info">
            <p class="member-name" @click="handleMemberProfileClick(member)">{{ member.userFirstName }}</p>
            <button
              v-if="userId === groupChat.ownerId && member.userId !== groupChat.ownerId"
              :ref="'groupChatMemberMenuButton-' + index"
              @click="toggleGroupChatMemberMenu(member.userId)"
              class="dropdown-toggle-button"
            >
              ⋮
            </button>
            <div
              v-if="openMemberMenuId === member.userId"
              class="member-menu-dropdown"
              :ref="'groupChatMemberMenuDropdown-' + index"
            >
              <button class="dropdown-item" v-if="member.userId !== groupChat.ownerId" @click="makeOwner(member)">
                Сделать владельцем
              </button>
              <button class="dropdown-item" v-if="member.userId !== groupChat.ownerId" @click="removeMember(member)">
                Удалить участника
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!--  MODAL FOR AVAILABLE USERS TO ADD  -->
    <div v-if="isAddMembersModalOpen" class="modal-overlay" @click.self="closeAddMembersModal">
      <div class="modal-content">
        <header class="profile-header">
          <button class="back-button" @click="closeAddMembersModal">←</button>
          <h2>Добавить пользователей</h2>
        </header>
        <div
          v-for="user in availableUsers"
          :key="user.id"
          class="member-item"
        >
          <img
            :src="user.userPhoto || defaultAvatar"
            alt="avatar"
            class="member-avatar"
            @click="handleUserProfileClick(user)"
          />
          <div class="member-info">
            <p class="member-name" @click="handleUserProfileClick(user)">{{ user.username }}</p>
            <button
              class="profile-button small"
              :class="{ 'cancel-button': selectedUsers.includes(user.id), 'upload-button': !selectedUsers.includes(user.id) }"
              @click="toggleUserSelection(user.id)"
            >
              {{ selectedUsers.includes(user.id) ? 'Отменить' : 'Добавить' }}
            </button>
          </div>
        </div>
        <div class="modal-actions">
          <button class="modal-button danger" @click="submitSelectedUsers">Добавить выбранных</button>
          <button class="modal-button cancel-button" @click="closeAddMembersModal">Закрыть</button>
        </div>
      </div>
    </div>

    <!--  MODAL FOR CHANGE METADATA  -->
    <div v-if="isMetadataModalOpen" class="upload-modal">
      <div class="modal-content">
        <h3>Сменить название и описание</h3>
        <input
          type="text"
          v-model="metadataData.newName"
          placeholder="Новое название"
        />
        <textarea
          v-model="metadataData.newDescription"
          placeholder="Новое описание"
          rows="4"
        ></textarea>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-actions">
          <button class="modal-button upload-button" @click="changeMetadata">Изменить</button>
          <button class="modal-button cancel-button" @click="closeMetadataModal">Отмена</button>
        </div>
      </div>
    </div>

    <!--  MODAL FOR CHANGE PHOTO  -->
    <div v-if="isUploadModalOpen" class="upload-modal">
      <div class="modal-content">
        <h3>Загрузить фото</h3>
        <input type="file" @change="handleFileChange" accept="image/jpeg" />
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-actions">
          <button class="modal-button upload-button" @click="uploadPhoto">Загрузить</button>
          <button class="modal-button cancel-button" @click="closeUploadModal">Отмена</button>
        </div>
      </div>
    </div>

    <!--  MODAL FOR DELETE  -->
    <div v-if="isDeleteModalOpen" class="upload-modal">
      <div class="modal-content">
        <h3>Удалить аккаунт</h3>
        <p>Вы уверены, что хотите удалить чат? Это действие необратимо. Все участники чата, включая вас, потеряют доступ к нему.</p>
        <div class="modal-actions">
          <button class="modal-button danger" @click="deleteGroupChat">Удалить</button>
          <button class="modal-button cancel-button" @click="closeDeleteModal">Отмена</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import groupChatDefaultAvatar from "@/assets/chat/GroupChatDefaultAvatar.png";
import defaultAvatar from "@/assets/user/DefaultAvatar.png";
import router from "@/js/config/router";
import {
  changeGroupChatMetadata, changeGroupChatOwner,
  changeGroupChatPublicStatus,
  deleteGroupChat,
  getGroupChat
} from "@/js/service/groupChatController";
import {jwtDecode} from "jwt-decode";
import authState from "@/js/store/states/authState";
import {getGroupChatPhoto, uploadOrChangePhoto, deletePhoto} from "@/js/service/groupChatPhotoController";
import {getMainUserPhotoByUserId} from "@/js/service/userPhotoController";
import {
  addUsersToGroupChat,
  deleteGroupChatMembers,
  getAvailableUsersToAdd, getGroupChatMembers, leaveFromChat
} from "@/js/service/groupChatMemberController";

export default {
  name: "MainGroupChatComponent",
  data() {
    return {
      defaultAvatar: defaultAvatar,
      groupChatDefaultAvatar: groupChatDefaultAvatar,
      chatId: this.$route.params.id,
      userId: null,
      groupChat: null,
      groupChatPhoto: null,
      groupChatMembers: [],
      availableUsers: [],
      metadataData: {
        newName: "",
        newDescription: ""
      },
      selectedUsers: [],
      groupChatMenuVisible: false,
      openMemberMenuId: null,
      isUploadModalOpen: false,
      isMetadataModalOpen: false,
      isDeleteModalOpen: false,
      isListMembersModalOpen: false,
      isAddMembersModalOpen: false,
      isActionsModalOpen: false,
      selectedFile: null,
      errorMessage: ""
    };
  },
  created() {
    this.userId = jwtDecode(authState.accessToken).sub;
    this.fetchChatData();
    document.addEventListener("click", this.closeGroupChatMenuOnClickOutside);
    document.addEventListener("click", this.closeMemberMenuOnClickOutside);
    document.addEventListener("click", this.handleClickOutsideMenu);
  },
  unmounted() {
    document.removeEventListener("click", this.closeGroupChatMenuOnClickOutside);
    document.removeEventListener("click", this.closeMemberMenuOnClickOutside);
    document.removeEventListener("click", this.handleClickOutsideMenu);
  },
  methods: {
    async fetchChatData() {
      try {
        this.groupChat = await getGroupChat(this.chatId);
        this.groupChatMembers = await getGroupChatMembers(this.chatId)

        this.groupChatPhoto = this.groupChat.filePath
            ? await getGroupChatPhoto(this.groupChat.id)
            : null;

      } catch (error) {
        console.error("Ошибка при загрузке данных чата:", error);
      }
    },
    async loadListMembers() {
      this.groupChatMembers = await Promise.all(
        this.groupChatMembers.map(async (member) => {
          return {
            ...member,
            userPhoto: await getMainUserPhotoByUserId(member.userId),
          };
        })
      );
    },
    async loadListAvailableUsers() {
      this.availableUsers = await getAvailableUsersToAdd(this.chatId);

      this.availableUsers = await Promise.all(
        this.availableUsers.map(async (user) => {
          return {
            ...user,
            userPhoto: await getMainUserPhotoByUserId(user.id),
          };
        })
      );
    },
    async removeMember(member) {
      try {
        const userIds = [];
        userIds.push(member.userId);

        await deleteGroupChatMembers(this.chatId, { userIds });
        router.go(0);
      } catch (error) {
        console.error("Ошибка при удалении участника чата:", error);
      }
    },
    async makeOwner(member) {
      try {
        await changeGroupChatOwner(this.chatId, { userId: member.userId });
        router.go(0);
      } catch (error) {
        console.error("Ошибка при обновлении владельца чата:", error);
      }
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file && file.type === "image/jpeg") {
        this.selectedFile = file;
        this.errorMessage = "";
      } else {
        this.errorMessage = "Пожалуйста, выберите файл в формате JPEG.";
        this.selectedFile = null;
      }
    },
    async uploadPhoto() {
      if (!this.selectedFile) {
        this.errorMessage = "Файл не выбран или имеет неверный формат.";
        return;
      }

      const formData = new FormData();
      formData.append("file", this.selectedFile);

      try {
        await uploadOrChangePhoto(this.chatId, formData);
        router.go(0);
      } catch (error) {
        console.error("Ошибка загрузки фотографии:", error);
      }
    },
    async deletePhoto() {
      try {
        await deletePhoto(this.chatId);
        router.go(0);
      } catch (error) {
        console.error("Ошибка удаления фотографии:", error);
      }
    },
    async changeChatStatus() {
      try {
        await changeGroupChatPublicStatus(this.chatId);
        router.go(0);
      } catch (error) {
        console.error("Ошибка смены статуса чата:", error);
      }
    },
    async leaveFromChat() {
      try {
        await leaveFromChat(this.chatId);
        router.push({ name: "Dialogs" }).catch(() => {});
      } catch (error) {
        console.error("Ошибка про попытке покинуть чат:", error);
      }
    },
    async changeMetadata() {
      const { newName, newDescription } = this.metadataData;

      if (!newName) {
        this.errorMessage = "Название не может быть пустым.";
        return;
      }

      try {
        await changeGroupChatMetadata(this.chatId, {newName, newDescription,});
        this.closeMetadataModal();
        router.go(0);
      } catch (error) {
        this.errorMessage = "Не удалось сменить название и описание. Проверьте данные.";
        console.error("Ошибка смены названия и пароля:", error);
      }
    },
    async deleteGroupChat() {
      try {
        await deleteGroupChat(this.chatId);
        router.push({ name: "Dialogs" }).catch(() => {});
      } catch (error) {
        console.error("Ошибка при удалении чата:", error);
      }
    },
    closeGroupChatMenuOnClickOutside(event) {
      const menu = this.$refs.groupChatMenuDropdown;
      const button = this.$refs.groupChatMenuButton;
      if (
        menu && !menu.contains(event.target) &&
        button && !button.contains(event.target)
      ) {
        this.groupChatMenuVisible = false;
      }
    },
    goBack() {
      router.go(-1);
    },
    toggleGroupChatMemberMenu(memberId, index) {
      if (this.openMemberMenuId === memberId) {
        this.openMemberMenuId = null;
        return;
      }
      this.openMemberMenuId = memberId;
      this.$nextTick(() => {
        const button = this.$refs[`groupChatMemberMenuButton-${index}`];
        const menu = this.$refs[`groupChatMemberMenuDropdown-${index}`];

        if (button && menu) {
          const buttonRect = button.getBoundingClientRect();
          menu.style.top = `${buttonRect.bottom + window.scrollY}px`;
          menu.style.left = `${buttonRect.left + buttonRect.width + window.scrollX}px`;
        }
      });
    },
    closeMemberMenuOnClickOutside(event) {
      const menus = this.$refs.groupChatMemberMenuDropdown;
      if (!menus || !Array.isArray(menus)) return;

      const buttons = this.$refs.groupChatMemberMenuButton;
      menus.forEach((menu, index) => {
        const button = buttons[index];
        if (
          menu &&
          button &&
          !menu.contains(event.target) &&
          !button.contains(event.target)
        ) {
          this.openMemberMenuId = null;
        }
      });
    },
    toggleUserSelection(userId) {
      if (this.selectedUsers.includes(userId)) {
        this.selectedUsers = this.selectedUsers.filter(id => id !== userId);
      } else {
        this.selectedUsers.push(userId);
      }
    },
    async submitSelectedUsers() {
      if (!this.selectedUsers.length) {
        return;
      }

      try {
        await addUsersToGroupChat(this.chatId, { userIds: this.selectedUsers });
        this.closeAddMembersModal();
        router.go(0);
      } catch (error) {
        console.error("Ошибка добавления пользователей:", error);
      }
    },
    toggleGroupChatMenu() {
      this.groupChatMenuVisible = !this.groupChatMenuVisible;
    },
    openUploadModal() {
      this.isUploadModalOpen = true;
    },
    closeUploadModal() {
      this.isUploadModalOpen = false;
      this.selectedFile = null;
    },
    openDeleteModal() {
      this.isDeleteModalOpen = true;
    },
    closeDeleteModal() {
      this.isDeleteModalOpen = false;
    },
    handleUserProfileClick(user) {
      if (user.id === this.groupChat.ownerId) {
        this.goToProfile();
      } else {
        this.goToUserProfile(user.id);
      }
    },
    handleMemberProfileClick(member) {
      if (member.userId === this.groupChat.ownerId) {
        this.goToProfile();
      } else {
        this.goToUserProfile(member.userId);
      }
    },
    goToUserProfile(userId) {
      router.push({ name: "User", params: { id: userId } });
    },
    goToProfile() {
      router.push({ name: "Profile" });
    },
    openMetadataModal() {
      this.isMetadataModalOpen = true;
      this.errorMessage = "";
      this.metadataData = {
        newName: this.groupChat.name,
        newDescription: this.groupChat.description,
      };
    },

    closeMetadataModal() {
      this.isMetadataModalOpen = false;
    },
    async openListMembersModal() {
      this.isListMembersModalOpen = true;
      await this.loadListMembers();
    },
    closeListMembersModal() {
      this.isListMembersModalOpen = false;
    },
    async openAddMembersModal() {
      this.isAddMembersModalOpen = true;
      await this.loadListAvailableUsers();
    },
    closeAddMembersModal() {
      this.isAddMembersModalOpen = false;
    },
    handleClickOutsideMenu(event) {
    const menus = this.$refs.groupChatMemberMenuDropdown;
    if (!menus) return;

    menus.forEach((menu, index) => {
      const button = this.$refs[`groupChatMemberMenuButton-${index}`];
      if (
        menu &&
        button &&
        !menu.contains(event.target) &&
        !button.contains(event.target)
      ) {
        this.openMemberMenuId = null;
      }
    });
  },
  }
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
  margin-bottom: 5px;
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
  margin-bottom: 5px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.menu-button {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 25px;
  background: none;
  border: none;
  color: black;
  cursor: pointer;
  z-index: 10;
}

.menu-dropdown {
  position: absolute;
  top: 30px;
  right: 0;
  background-color: #3C3C3C;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  padding: 10px;
  display: flex;
  flex-direction: column;
  z-index: 100;
}
.member-menu-dropdown {
  position: absolute;
  background-color: #3C3C3C;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  padding: 10px;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.dropdown-toggle-button {
  background: none;
  color: #F9F8F8;
  cursor: pointer;
  padding: 10px;
  font-size: 25px;
  border: none;
  z-index: 10;
}

.menu-dropdown button {
  background: none;
  border: none;
  color: #F9F8F8;
  cursor: pointer;
  padding: 10px;
  text-align: left;
}
.member-menu-dropdown button {
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
.member-menu-dropdown button:hover {
  background-color: #444;
}

.upload-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #2c2c2c;
  padding: 20px;
  border-radius: 10px;
  color: #f9f8f8;
  text-align: center;
  width: 300px;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.modal-button {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.upload-button {
  background-color: #2e7d32;
  color: #fff;
}

.upload-button:hover {
  background-color: #255d27;
}

.cancel-button {
  background-color: #8a1c1c;
  color: #fff;
}

.cancel-button:hover {
  background-color: #611313;
}

.error-message {
  color: #e53935;
  margin-top: 10px;
  font-size: 14px;
}

.profile-button {
  width: 80%;
  padding: 10px;
  font-size: 16px;
  margin-bottom: 5px;
  background-color: #444;
  color: #F9F8F8;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.profile-button.small {
  width: 30%;
}

.profile-button:hover {
  background-color: #555;
}

input[type="text"] {
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

input[type="text"]::placeholder {
  color: #B3B3B3;
  font-style: normal;
}

input[type="text"]:focus {
  outline: none;
  border-color: #888888;
}

textarea {
  width: 90%;
  padding: 8px 10px;
  margin: 10px 0;
  border: 1px solid #CCCCCC;
  border-radius: 4px;
  background-color: #F4F4F4;
  color: #343434;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.2s ease-in-out;
}

textarea::placeholder {
  color: #B3B3B3;
  font-style: normal;
}

textarea:focus {
  outline: none;
  border-color: #888888;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: #222;
  color: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}

.member-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 15px;
}

.member-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.member-name {
  font-size: 16px;
  font-weight: bold;
}
</style>