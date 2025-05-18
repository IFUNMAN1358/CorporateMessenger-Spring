<template>
  <div class="chat-container">
    <header v-if="chatData" class="chat-header">
      <button class="back-button" @click="goBackToDialogs">← Назад</button>
      <div class="chat-info">
        <img
          :src="chatPhoto"
          alt="Chat Photo"
          class="chat-avatar"
          @click="goToChatDetails"
        />
        <div @click="goToChatDetails" class="chat-name">
          {{ chatData.chat.type === 'PRIVATE' ? chatData.partner?.username : chatData.chat.title }}
        </div>
      </div>
    </header>

    <div class="messages-container" ref="messagesContainer" @scroll="handleScroll" tabindex="0">
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message-item', message.senderId !== userId ? 'left-message' : 'right-message']"
        :data-message-id="message.id"
      >
        <div class="message-body">
          <p
            v-if="chatData.chat.type === 'GROUP' && message.senderId !== userId"
            @click="goToUserProfile(message.senderId)"
            class="user-username"
          >
            {{ message.senderUsername }}
          </p>
          <div v-if="message.files?.length" class="message-files">
            <div v-for="file in message.files" :key="file.id" class="file-item">
              <template v-if="isImage(file.fileName)">
                <img
                  :src="file.previewUrl || PhotoDefaultPlaceholder"
                  alt="Изображение"
                  class="file-image"
                  @click="openImageModal(file, message.id)"
                />
              </template>
              <template v-else>
                <div class="file-placeholder">
                  <a :href="file.url || PhotoDefaultPlaceholder" download :title="file.fileName">
                    {{ file.fileName }}
                  </a>
                </div>
              </template>
            </div>
          </div>

          <div class="message-content">
            <p>{{ message.content }}</p>
          </div>

          <div v-if="message.senderId === userId" class="message-options">
            <button @click="toggleMessageMenu(message.id)" class="menu-toggle">
              ⋮
            </button>
            <div v-if="messageMenuId === message.id" class="message-dropdown">
              <button @click="editMessage(message.id)">Изменить</button>
              <button @click="deleteMessage(message.id)">Удалить</button>
            </div>
          </div>

          <div class="message-meta">
            <span class="message-time">{{ formatTime(message.createdAt) }}</span>
            <span class="message-status" :data-status="message.isRead ? '✓✓' : '✓'"></span>
          </div>

          <div v-if="modalImage" class="modal-image" @click.self="closeImageModal">
            <div class="modal-image-content">
              <img :src="modalImage" alt="Увеличенное изображение" />
              <button class="modal-image-close" @click="closeImageModal">Закрыть</button>
              <a :href="modalImage" download class="modal-image-download">Скачать</a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <footer class="message-footer">
      <div v-if="selectedFiles.length > 0" class="file-modal">
        <header class="file-modal-header">
          <h3 class="file-modal-title">Выбранные файлы</h3>
          <button class="file-modal-close" @click="clearFiles">×</button>
        </header>
        <div class="file-grid">
          <div v-for="(file, index) in selectedFiles" :key="index" class="file-grid-item">
            <template v-if="isImage(file.name)">
              <div class="file-preview-container">
                <img
                  v-if="file.previewUrl"
                  :src="file.previewUrl"
                  alt="Превью"
                  class="file-preview"
                />
                <img
                  v-else
                  :src="createPreviewUrl(file)"
                  alt="Превью"
                  class="file-preview"
                />
                <button class="file-remove" @click="removeFile(index)">×</button>
              </div>
            </template>
            <template v-else>
              <div class="file-name-container">
                <span class="file-name">{{ file.name }}</span>
                <button class="file-remove" @click="removeFile(index)">×</button>
              </div>
            </template>
          </div>
        </div>
      </div>

      <div class="input-container">
        <input
          type="file"
          id="file-input"
          multiple
          @change="handleFileChange"
          class="hidden-file-input"
          accept="image/*,video/*,.pdf,.docx"
        />
        <label for="file-input" class="file-icon">📎</label>
        <input
          type="text"
          v-model="newMessage"
          placeholder="Напишите сообщение..."
          class="message-input"
          @keydown="handleKeyDown"
        />
        <button v-if="editingMessageId" @click="saveEditedMessage" class="send-icon">
          ➤
        </button>
        <button v-else @click="sendMessage" class="send-icon">
          ➤
        </button>
      </div>
    </footer>
  </div>
</template>

<script>
import GroupChatDefaultAvatar from '@/assets/images/GroupChatDefaultAvatar.png';
import UserDefaultAvatar from '@/assets/images/UserDefaultAvatar.png';
import PhotoDefaultPlaceholder from '@/assets/images/PhotoDefaultPlaceholder.png';
import {jwtDecode} from "jwt-decode";
import authStore from "@/store/authStore";
import {fetchGetAllChats} from "@/api/resources/chat";
import {fetchDownloadMyMainUserPhoto} from "@/api/resources/userPhoto";
import {fetchDownloadMainGroupChatPhotoByChatId} from "@/api/resources/chatPhoto";
import router from "@/router/router";
import {
  fetchCreateMessage,
  fetchDeleteMessage, fetchDownloadMessageFile, fetchGetAllMessages,
  fetchReadMessage,
  fetchUpdateMessageContent
} from "@/api/resources/message";
import {Stomp} from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default {
  name: "ChatComponent",
  data() {
    return {
      chatId: this.$route.params.id,
      chatData: null,
      chatPhoto: null,
      userId: null,
      messages: [],
      newMessage: "",
      selectedFiles: [],
      messageMenuId: null,
      UserDefaultAvatar,
      GroupChatDefaultAvatar,
      PhotoDefaultPlaceholder,
      page: 0,
      size: 20,
      isFetching: false,
      stompClient: null,
      modalImage: null,
      observer: null,
      editingMessageId: null,
    };
  },
  async created() {
    this.userId = jwtDecode(authStore.state.accessToken).sub;
    await this.fetchChatData();
    await this.fetchMessages();
    this.setupWebSocket();
    this.$nextTick(() => {
      this.scrollToBottom();
    });
  },
  mounted() {
    this.observer = new IntersectionObserver(this.handleVisibleMessages, {
      root: this.$refs.messagesContainer,
      rootMargin: "0px 0px 200px 0px",
      threshold: 0.1,
    });

    this.$nextTick(() => {
      this.observeMessages();
    });
  },
  unmounted() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
    if (this.observer) {
      this.observer.disconnect();
    }
    this.selectedFiles.forEach((file) => {
      if (file.previewUrl) URL.revokeObjectURL(file.previewUrl);
    });
  },
  methods: {
    async fetchChatData() {
      try {
        const chats = await fetchGetAllChats();
        this.chatData = chats.find((chat) => chat.chat.id === Number(this.chatId));
        if (!this.chatData) {
          console.error("Chat not found");
        }

        if (this.chatData.chat.type === "PRIVATE" && this.chatData.partner?.id) {
          try {
            this.chatPhoto = await fetchDownloadMyMainUserPhoto("small");
          } catch (error) {
            console.error("Failed to load user photo:", error);
            this.chatPhoto = this.UserDefaultAvatar;
          }
        } else if (this.chatData.chat.type === "GROUP" && this.chatData.photo?.id) {
          try {
            this.chatPhoto = await fetchDownloadMainGroupChatPhotoByChatId(
              this.chatId,
              "small"
            );
          } catch (error) {
            console.error("Failed to load group chat photo:", error);
            this.chatPhoto = this.GroupChatDefaultAvatar;
          }
        } else {
          this.chatPhoto =
            this.chatData.chat.type === "PRIVATE"
              ? this.UserDefaultAvatar
              : this.GroupChatDefaultAvatar;
        }
      } catch (error) {
        console.error("Ошибка при загрузке данных чата:", error);
        router.push({ name: "Dialogs" });
      }
    },
    async fetchMessages(page = 0) {
      if (this.isFetching) return;
      this.isFetching = true;
      try {
        const messages = await fetchGetAllMessages(this.chatId, page, this.size);
        const formattedMessages = await Promise.all(
          messages.map(async (msg) => {
            const files = msg.files || [];
            const processedFiles = await Promise.all(
              files.map(async (file) => {
                const url = await fetchDownloadMessageFile(
                  this.chatId,
                  msg.messageId,
                  file.id,
                  "small"
                );
                return {
                  id: file.id,
                  fileName: file.fileName,
                  url,
                  previewUrl: this.isImage(file.fileName) ? url : null,
                };
              })
            );
            return {
              id: msg.messageId,
              chatId: msg.chatId,
              senderId: msg.senderId,
              senderUsername: msg.senderUsername,
              content: msg.content,
              hasFiles: msg.hasFiles,
              isChanged: msg.isChanged,
              isRead: msg.isRead,
              createdAt: msg.createdAt,
              files: processedFiles,
            };
          })
        );

        if (page === 0) {
          this.messages = formattedMessages.reverse();
        } else {
          this.messages = [...formattedMessages.reverse(), ...this.messages];
        }

        this.$nextTick(() => {
          this.observeMessages();
        });
      } catch (error) {
        console.error("Ошибка при загрузке сообщений:", error);
      } finally {
        this.isFetching = false;
      }
    },
    observeMessages() {
      this.messages.forEach((message) => {
        const messageElement = document.querySelector(
          `[data-message-id="${message.id}"]`
        );
        if (messageElement) {
          this.observer.observe(messageElement);
        }
      });
    },
    setupWebSocket() {
      this.stompClient = Stomp.over(
        () => new SockJS(`${process.env.VUE_APP_BACK_BASE_URL}/ws`)
      );
      this.stompClient.reconnectDelay = 3000;

      this.stompClient.activate();
      this.stompClient.onConnect = () => {
        this.stompClient.subscribe(`/topic/chat/${this.chatId}`, async (message) => {
          const wsMessageResponse = JSON.parse(message.body);
          const { type, data } = wsMessageResponse;
          switch (type) {
            case "CREATE":
              await this.handleCreateMessage(data);
              break;
            case "READ":
              await this.handleReadMessage(data);
              break;
            case "UPDATE_CONTENT":
              await this.handleUpdateContentMessage(data);
              break;
            case "DELETE":
              this.handleDeleteMessage(data);
              break;
            default:
              console.warn("Неизвестный тип сообщения:", type);
          }
        });
      };
      this.stompClient.onStompError = (error) => {
        console.error("WebSocket connection error:", error);
      };
    },
    async handleCreateMessage(messageResponse) {
      const files = messageResponse.files || [];
      const processedFiles = await Promise.all(
        files.map(async (file) => {
          const url = await fetchDownloadMessageFile(
            this.chatId,
            messageResponse.messageId,
            file.id,
            "small"
          );
          return {
            id: file.id,
            fileName: file.fileName,
            url,
            previewUrl: this.isImage(file.fileName) ? url : null,
          };
        })
      );
      const formattedMessage = {
        id: messageResponse.messageId,
        chatId: messageResponse.chatId,
        senderId: messageResponse.senderId,
        senderUsername: messageResponse.senderUsername,
        content: messageResponse.content,
        hasFiles: messageResponse.hasFiles,
        isChanged: messageResponse.isChanged,
        isRead: messageResponse.isRead,
        createdAt: messageResponse.createdAt,
        files: processedFiles,
      };
      this.messages.push(formattedMessage);
      this.$nextTick(() => {
        const messageElement = document.querySelector(
          `[data-message-id="${formattedMessage.id}"]`
        );
        if (messageElement) {
          this.observer.observe(messageElement);
        }
        this.scrollToBottom();
      });
    },
    async handleReadMessage(messageResponse) {
      const message = this.messages.find((msg) => msg.id === messageResponse.messageId);
      if (message) {
        message.isRead = true;
        this.$forceUpdate();
      }
    },
    async handleUpdateContentMessage(messageResponse) {
      const messageIndex = this.messages.findIndex(
        (msg) => msg.id === messageResponse.messageId
      );
      if (messageIndex !== -1) {
        this.messages[messageIndex] = {
          ...this.messages[messageIndex],
          content: messageResponse.content,
          isChanged: true,
        };
        this.$forceUpdate();
      }
      this.messageMenuId = null;
    },
    handleDeleteMessage(messageResponse) {
      this.messages = this.messages.filter((msg) => msg.id !== messageResponse.messageId);
      this.messageMenuId = null;
    },
    isImage(fileName) {
      const imageExtensions = ["jpg", "jpeg", "png", "gif", "bmp", "webp"];
      const extension = fileName.split(".").pop().toLowerCase();
      return imageExtensions.includes(extension);
    },
    async openImageModal(file, messageId) {
      try {
        this.modalImage = await fetchDownloadMessageFile(
          this.chatId,
          messageId,
          file.id,
          "big"
        );
      } catch (error) {
        console.error("Ошибка при открытии изображения:", error);
      }
    },
    closeImageModal() {
      this.modalImage = null;
    },
    createPreviewUrl(file) {
      if (file && file.type.startsWith("image/")) {
        try {
          file.previewUrl = URL.createObjectURL(file);
          return file.previewUrl;
        } catch (error) {
          console.error("Ошибка создания preview URL:", error);
          return null;
        }
      }
      return null;
    },
    handleFileChange(event) {
      const files = Array.from(event.target.files);
      if (this.selectedFiles.length + files.length > 10) {
        alert("Нельзя выбрать больше 10 файлов.");
        return;
      }
      this.selectedFiles = [...this.selectedFiles, ...files].slice(0, 10);
    },
    clearFiles() {
      this.selectedFiles.forEach((file) => {
        if (file.previewUrl) URL.revokeObjectURL(file.previewUrl);
      });
      this.selectedFiles = [];
    },
    removeFile(index) {
      const file = this.selectedFiles[index];
      if (file.previewUrl) URL.revokeObjectURL(file.previewUrl);
      this.selectedFiles.splice(index, 1);
    },
    formatTime(timestamp) {
      if (!timestamp) return "";
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    },
    goBackToDialogs() {
      router.push({ name: "Dialogs" });
    },
    goToUserProfile(userId) {
      router.push({ name: "User", params: { id: userId } });
    },
    goToChatDetails() {
      if (this.chatData.chat.type === "PRIVATE") {
        router.push({ name: "User", params: { id: this.chatData.partner.id } });
      } else {
        router.push({ name: "MainGroupChat", params: { id: this.chatId } });
      }
    },
    toggleMessageMenu(messageId) {
      this.messageMenuId = this.messageMenuId === messageId ? null : messageId;
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    },
    async handleScroll() {
      const container = this.$refs.messagesContainer;
      if (container.scrollTop === 0 && !this.isFetching) {
        this.isFetching = true;
        const prevScrollHeight = container.scrollHeight;
        this.page++;
        await this.fetchMessages(this.page);
        this.$nextTick(() => {
          const newScrollHeight = container.scrollHeight;
          container.scrollTop = newScrollHeight - prevScrollHeight;
        });
      }
    },
    handleKeyDown(event) {
      if (event.key === "Enter") {
        event.preventDefault();
        if (this.editingMessageId) {
          this.saveEditedMessage();
        } else {
          this.sendMessage();
        }
      }
    },
    async sendMessage() {
      if (!this.newMessage.trim() && this.selectedFiles.length === 0) return;
      try {
        const firstFile = this.selectedFiles[0];
        await fetchCreateMessage(this.chatId, this.newMessage, firstFile);
        this.newMessage = "";
        this.clearFiles();
        this.page = 0;
        this.scrollToBottom();
      } catch (error) {
        console.error("Ошибка при отправке сообщения:", error);
      }
    },
    editMessage(messageId) {
      this.editingMessageId = messageId;
      const message = this.messages.find((msg) => msg.id === messageId);
      if (message) {
        this.newMessage = message.content || "";
      }
      this.messageMenuId = null;
    },
    async saveEditedMessage() {
      if (!this.newMessage.trim()) return;
      try {
        await fetchUpdateMessageContent(this.chatId, this.editingMessageId, this.newMessage);
        this.editingMessageId = null;
        this.newMessage = "";
      } catch (error) {
        console.error("Ошибка при сохранении сообщения:", error);
      }
    },
    async deleteMessage(messageId) {
      try {
        await fetchDeleteMessage(this.chatId, messageId);
      } catch (error) {
        console.error("Ошибка при удалении сообщения:", error);
      }
    },
    async handleVisibleMessages(entries) {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          const messageId = entry.target.dataset.messageId;
          const message = this.messages.find((m) => m.id === messageId);
          if (
            message &&
            message.senderId !== this.userId &&
            !message.isRead &&
            this.chatData
          ) {
            await fetchReadMessage(this.chatId, messageId);
            message.isRead = true;
          }
        }
      }
    },
  },
};
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: #252525;
  overflow: hidden;
}

.chat-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #333;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.back-button {
  font-size: 20px;
  color: #f9f8f8;
  background: none;
  border: none;
  cursor: pointer;
}

.chat-info {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
}

.chat-name {
  font-size: 16px;
  color: #f9f8f8;
  cursor: pointer;
}

.user-username {
  cursor: pointer;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 5px 0;
}

.messages-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
  max-width: 70%;
  padding: 10px;
  border-radius: 8px;
  position: relative;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.left-message {
  align-self: flex-start;
  background-color: #333;
  color: #f9f8f8;
}

.right-message {
  align-self: flex-end;
  background-color: #444;
  color: #fff;
}

.message-content p {
  margin: 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.message-options {
  position: absolute;
  top: 0;
  right: 0;
}

.message-status {
  font-size: 10px;
  display: inline-block;
  width: 16px;
  text-align: center;
}

.message-status::before {
  content: attr(data-status);
  font-size: inherit;
}

.menu-toggle {
  background: none;
  border: none;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-top: 5px;
  color: rgba(255, 255, 255, 0.6);
  gap: 20px;
}

.message-files {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 10px;
  margin: 10px 0;
  padding: 5px;
  background-color: #444;
  border-radius: 8px;
}

.file-item {
  flex: 1 1 calc(50% - 10px);
  max-width: calc(50% - 10px);
  box-sizing: border-box;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #555;
  border-radius: 5px;
  overflow: hidden;
}

.file-item:only-child {
  flex: 1 1 100%;
  max-width: 100%;
}

.file-item img.file-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  border-radius: 5px;
  transition: transform 0.2s ease-in-out;
}

.file-item img.file-image:hover {
  transform: scale(1.05);
}

.file-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: 0.8em;
  text-align: center;
  color: #f9f8f8;
  background-color: #616161;
  border-radius: 5px;
  overflow: hidden;
}

.message-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #2c2c2c;
  padding: 5px 10px;
  border-radius: 4px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  z-index: 10;
}

.message-dropdown button {
  background: none;
  border: none;
  padding: 5px;
  color: #f9f8f8;
  cursor: pointer;
  text-align: left;
}

.message-dropdown button:hover {
  background-color: #444;
}

.message-footer {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  padding: 15px;
  background-color: #333;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.input-container {
  display: flex;
  align-items: center;
  width: 95%;
  background-color: #444;
  border-radius: 8px;
  padding: 5px 10px;
}

.message-input {
  flex: 1;
  font-size: 16px;
  color: #f9f8f8;
  background-color: transparent;
  border: none;
  outline: none;
}

.hidden-file-input {
  display: none;
}

.file-icon {
  margin-right: 10px;
  font-size: 20px;
  color: #f9f8f8;
  cursor: pointer;
}

.send-icon {
  margin-left: 10px;
  font-size: 20px;
  color: #f9f8f8;
  background: none;
  border: none;
  cursor: pointer;
}

.modal-image {
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  overflow: hidden;
}

.modal-image-content {
  position: relative;
  max-width: 80vw;
  max-height: 80vh;
  width: auto;
  height: auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.modal-image-content img {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
}

.modal-image-close,
.modal-image-download {
  position: absolute;
  background: rgba(50, 50, 50, 0.9);
  border: none;
  color: #fff;
  padding: 8px 12px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.4);
  transition: background 0.2s;
  text-decoration: none;
}

.modal-image-close {
  top: 15px;
  right: 15px;
}

.modal-image-download {
  top: 55px;
  right: 15px;
}

.modal-image-close:hover,
.modal-image-download:hover {
  background: rgba(70, 70, 70, 1);
}

.file-modal {
  padding: 15px;
  background-color: #2b2b2b;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  color: #f9f9f9;
}

.file-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.file-modal-title {
  font-size: 16px;
  font-weight: bold;
  color: #f9f9f9;
}

.file-modal-close {
  background: none;
  border: none;
  color: #f9f9f9;
  font-size: 20px;
  cursor: pointer;
}

.file-modal-close:hover {
  color: #d9534f;
}

.file-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.file-grid-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1 1 calc(20% - 8px);
  max-width: calc(20% - 8px);
  box-sizing: border-box;
  background-color: #444;
  border: 1px solid #555;
  border-radius: 6px;
  overflow: hidden;
  text-align: center;
  padding: 5px;
}

.file-preview-container {
  position: relative;
  width: 100%;
  height: 60px;
}

.file-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.file-remove {
  position: absolute;
  top: 2px;
  right: 2px;
  background: rgba(0, 0, 0, 0.7);
  border: none;
  color: #f9f9f9;
  font-size: 12px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  cursor: pointer;
}

.file-remove:hover {
  background: #d9534f;
}

.file-name-container {
  position: relative;
  padding: 3px;
}

.file-name {
  font-size: 12px;
  color: #f9f9f9;
  word-break: break-word;
  text-align: center;
}

@media (max-width: 768px) {
  .chat-container {
    width: 100%;
    max-width: 100%;
  }

  .message-item {
    padding: 8px;
  }

  .left-message,
  .right-message {
    max-width: 80%;
  }

  .file-item {
    flex: 1 1 calc(33.33% - 10px);
    max-width: calc(33.33% - 10px);
  }

  .file-grid-item {
    flex: 1 1 calc(33.33% - 8px);
    max-width: calc(33.33% - 8px);
  }

  .file-preview {
    height: 50px;
  }

  .file-name {
    font-size: 10px;
  }
}
</style>