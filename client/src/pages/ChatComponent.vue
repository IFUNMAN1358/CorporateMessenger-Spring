<template>
  <div class="chat-container">
    <header v-if="chatData" class="chat-header">
      <button class="back-button" @click="goBackToDashboard">← Назад</button>
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
      <div v-if="isFetching && page > 0" class="loading-spinner-top">
        <div class="spinner"></div>
        <p>Загрузка старых сообщений...</p>
      </div>
      <div v-if="isLoading" class="loading-spinner">
        <div class="spinner"></div>
        <p>Загрузка сообщений...</p>
      </div>
      <div v-if="messages.length === 0 && !isLoading" class="no-messages">
        <p>Нет сообщений</p>
      </div>
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message-item', message.senderId !== userId ? 'left-message' : 'right-message']"
        :data-message-id="message.id"
      >
        <div class="message-body">
          <p
            v-if="chatData.chat.type === 'GROUP' && message.senderId !== userId"
            @click="goToProfile(message.senderId)"
            class="user-username"
          >
            {{ message.senderUsername }}
          </p>
          <div v-if="message.messageFiles?.length" class="message-files">
            <div v-for="file in message.messageFiles" :key="file.id" class="file-item">
              <template v-if="isImage(file.fileName, file.mimeType)">
                <div class="file-image-container">
                  <img
                    v-if="file.previewUrl && file.previewUrl !== PhotoDefaultPlaceholder"
                    :src="file.previewUrl"
                    alt="Изображение"
                    class="file-image"
                    @click="openImageModal(file, message.id)"
                    @error="handleImageError(file, message.id)"
                  />
                  <div v-else class="image-loading">
                    <div class="spinner"></div>
                    <p>Загрузка...</p>
                  </div>
                </div>
              </template>
              <template v-else>
                <div class="file-placeholder">
                  <span class="file-icon">{{ getFileIcon(file.fileName, file.mimeType) }}</span>
                  <a
                    :href="file.url || PhotoDefaultPlaceholder"
                    :download="file.fileName"
                    :title="file.fileName"
                    @click.prevent="handleFileDownload(file)"
                  >
                    {{ file.fileName || 'Скачать файл' }}
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
        </div>
      </div>
    </div>

    <footer class="message-footer">
      <div v-if="isFileError" class="file-error">
        {{ fileErrorMessage }}
      </div>
      <div v-if="selectedFiles.length > 0" class="file-modal">
        <header class="file-modal-header">
          <h3 class="file-modal-title">Выбранные файлы</h3>
          <button class="file-modal-close" @click="clearFiles">×</button>
        </header>
        <div class="file-grid">
          <div v-for="(file, index) in selectedFiles" :key="index" class="file-grid-item">
            <template v-if="isImage(file.name, file.type)">
              <div class="file-preview-container">
                <img
                  :src="file.previewUrl || PhotoDefaultPlaceholder"
                  alt="Превью"
                  class="file-preview"
                  @error="handlePreviewImageError(index)"
                />
                <button class="file-remove" @click="removeFile(index)">×</button>
              </div>
            </template>
            <template v-else>
              <div class="file-name-container">
                <span class="file-icon">{{ getFileIcon(file.name, file.type) }}</span>
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
          accept="image/jpeg,image/png,video/mp4,application/pdf"
        />
        <label for="file-input" class="file-icon">📎</label>
        <input
          type="text"
          v-model="newMessage"
          placeholder="Напишите сообщение..."
          class="message-input"
          @keydown="handleKeyDown"
        />
        <button v-if="editingMessageId" @click="saveEditedMessage" class="send-icon" :disabled="isSending">
          ➤
        </button>
        <button v-else @click="sendMessage" class="send-icon" :disabled="isSending">
          ➤
        </button>
      </div>
    </footer>

    <div v-if="modalImage" class="modal-image" @click.self="closeImageModal">
      <div class="modal-image-content">
        <img :src="modalImage" alt="Увеличенное изображение" @error="handleModalImageError" />
        <button class="modal-image-close" @click="closeImageModal">Закрыть</button>
        <a :href="modalImage" download class="modal-image-download">Скачать</a>
      </div>
    </div>
  </div>
</template>

<script>
import GroupChatDefaultAvatar from '@/assets/images/GroupChatDefaultAvatar.png';
import UserDefaultAvatar from '@/assets/images/UserDefaultAvatar.png';
import PhotoDefaultPlaceholder from '@/assets/images/PhotoDefaultPlaceholder.png';
import { jwtDecode } from 'jwt-decode';
import authStore from '@/store/authStore';
import { fetchGetAllChats } from '@/api/resources/chat';
import { fetchDownloadMainUserPhotoByUserId } from '@/api/resources/userPhoto';
import { fetchDownloadMainGroupChatPhotoByChatId } from '@/api/resources/chatPhoto';
import router from '@/router/router';
import {
  fetchCreateMessage,
  fetchDeleteMessage,
  fetchDownloadMessageFile,
  fetchGetAllMessages,
  fetchReadMessage,
  fetchUpdateMessageContent,
} from '@/api/resources/message';
import { Client } from '@stomp/stompjs';

export default {
  name: 'ChatComponent',
  data() {
    return {
      chatId: null,
      chatData: null,
      chatPhoto: null,
      userId: jwtDecode(authStore.state.accessToken).sub,
      messages: [],
      newMessage: '',
      selectedFiles: [],
      messageMenuId: null,
      UserDefaultAvatar,
      GroupChatDefaultAvatar,
      PhotoDefaultPlaceholder,
      page: 0,
      size: 20,
      isFetching: false,
      isSending: false,
      isLoading: false,
      hasMoreMessages: true,
      stompClient: null,
      modalImage: null,
      observer: null,
      editingMessageId: null,
      blobUrlCache: new Map(),
      failedFiles: new Set(),
      isFileError: false,
      fileErrorMessage: '',
      isScrollProcessing: false,
    };
  },
  async created() {
    try {
      this.isLoading = true;
      this.chatId = this.$route.params.id;
      if (!this.chatId || isNaN(Number(this.chatId))) {
        console.error('Invalid chatId:', this.chatId);
        router.push({ name: 'Dashboard' });
        return;
      }
      await this.fetchChatData();
      await this.fetchMessages();
      this.setupWebSocket();
      this.$nextTick(() => {
        this.scrollToBottom();
      });
    } catch (error) {
      console.error('Error in created:', error);
      this.isFileError = true;
      this.fileErrorMessage = 'Не удалось загрузить чат. Попробуйте позже.';
    } finally {
      this.isLoading = false;
    }
  },
  mounted() {
    this.observer = new IntersectionObserver(this.handleVisibleMessages, {
      root: this.$refs.messagesContainer,
      rootMargin: '0px 0px 200px 0px',
      threshold: 0.1,
    });

    this.$nextTick(() => {
      this.observeMessages();
    });
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
    if (this.observer) {
      this.observer.disconnect();
    }
    this.selectedFiles.forEach((file) => {
      if (file.previewUrl && file.previewUrl !== this.PhotoDefaultPlaceholder) {
        URL.revokeObjectURL(file.previewUrl);
      }
    });
    this.blobUrlCache.forEach((cachedFile) => {
      if (cachedFile.url && cachedFile.url !== this.PhotoDefaultPlaceholder) {
        URL.revokeObjectURL(cachedFile.url);
      }
      if (cachedFile.previewUrl && cachedFile.previewUrl !== this.PhotoDefaultPlaceholder) {
        URL.revokeObjectURL(cachedFile.previewUrl);
      }
    });
    this.blobUrlCache.clear();
    this.failedFiles.clear();
  },
  methods: {
    debounce(func, wait) {
      let timeout;
      return function executedFunction(...args) {
        const later = () => {
          clearTimeout(timeout);
          func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
      };
    },
    async fetchChatData() {
      try {
        const chats = await fetchGetAllChats();
        this.chatData = chats.find((chat) => chat.chat.id === Number(this.chatId));
        if (!this.chatData) {
          console.error('Chat not found for chatId:', this.chatId);
          router.push({ name: 'Dashboard' });
          return;
        }

        if (this.chatData.chat.type === 'PRIVATE' && this.chatData.partner?.id) {
          this.chatPhoto = await fetchDownloadMainUserPhotoByUserId(this.chatData.partner.id, 'small').catch(() => this.UserDefaultAvatar);
          if (!this.chatPhoto || this.chatPhoto === this.PhotoDefaultPlaceholder) {
            this.chatPhoto = this.UserDefaultAvatar;
          }
        } else if (this.chatData.chat.type === 'GROUP' && this.chatData.photo?.id) {
          this.chatPhoto = await fetchDownloadMainGroupChatPhotoByChatId(this.chatId, 'small').catch(() => this.GroupChatDefaultAvatar);
        } else {
          this.chatPhoto = this.chatData.chat.type === 'PRIVATE' ? this.UserDefaultAvatar : this.GroupChatDefaultAvatar;
        }
      } catch (error) {
        console.error('Ошибка при загрузке данных чата:', error);
        router.push({ name: 'Dashboard' });
      }
    },
    async fetchMessages(page = 0) {
      this.isFetching = true;
      try {
        const messages = await fetchGetAllMessages(this.chatId, page, this.size);
        if (!messages || messages.length === 0) {
          this.hasMoreMessages = false;
          return;
        }

        if (messages.length < this.size) {
          this.hasMoreMessages = false;
        }

        const formattedMessages = await Promise.all(
          messages.map(async (msg) => {
            const files = msg.messageFiles || [];
            const processedFiles = await Promise.all(
              files.map(async (file) => {
                const cacheKey = `${msg.message.id}-${file.id}`;
                if (this.failedFiles.has(cacheKey)) {
                  return {
                    id: String(file.id),
                    fileName: file.fileName || 'unknown',
                    url: this.PhotoDefaultPlaceholder,
                    previewUrl: this.PhotoDefaultPlaceholder,
                    mimeType: file.mimeType || this.inferMimeType(file.fileName),
                  };
                }

                let cachedFile = this.blobUrlCache.get(cacheKey);
                let url;
                try {
                  url = await fetchDownloadMessageFile(this.chatId, msg.message.id, file.id, 'big');
                } catch (error) {
                  console.error(`Failed to fetch file ${file.id}:`, error);
                  url = this.PhotoDefaultPlaceholder;
                  this.failedFiles.add(cacheKey);
                }
                cachedFile = {
                  url: url,
                  previewUrl: this.isImage(file.fileName, file.mimeType) ? url : this.PhotoDefaultPlaceholder,
                  mimeType: file.mimeType || this.inferMimeType(file.fileName),
                };
                this.blobUrlCache.set(cacheKey, cachedFile);

                return {
                  id: String(file.id),
                  fileName: file.fileName || 'unknown',
                  url: cachedFile.url,
                  previewUrl: cachedFile.previewUrl,
                  mimeType: cachedFile.mimeType,
                };
              })
            );
            return {
              id: String(msg.message.id),
              chatId: msg.message.chatId,
              senderId: String(msg.message.senderId),
              senderUsername: msg.message.senderUsername || '',
              content: msg.message.content || '',
              hasFiles: msg.message.hasFiles || false,
              isChanged: msg.message.isChanged || false,
              isRead: msg.message.isRead || false,
              createdAt: msg.message.createdAt,
              messageFiles: processedFiles,
              readMessages: msg.readMessages || [],
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
          if (page === 0) {
            this.scrollToBottom();
          }
        });
      } catch (error) {
        console.error('Error in fetchMessages:', error);
        this.isFileError = true;
        this.fileErrorMessage = `Ошибка загрузки сообщений: ${error.message}`;
        this.hasMoreMessages = false;
      } finally {
        this.isFetching = false;
      }
    },
    observeMessages() {
      this.messages.forEach((message) => {
        const messageElement = document.querySelector(`[data-message-id="${message.id}"]`);
        if (messageElement) {
          this.observer.observe(messageElement);
        } else {
          console.warn(`Элемент сообщения с ID ${message.id} не найден для наблюдения`);
        }
      });
    },
    setupWebSocket() {
      if (!this.chatId) {
        console.error('Cannot setup WebSocket: chatId is invalid:', this.chatId);
        return;
      }
      const brokerURL = `${process.env.VUE_APP_BACK_WS_URL}/api/ws/chat`;
      console.log('Connecting to WebSocket at:', brokerURL);
      this.stompClient = new Client({
        brokerURL,
        connectHeaders: {
          Authorization: `Bearer ${authStore.state.accessToken}`,
          'X-Service-Name': process.env.VUE_APP_BACK_X_SERVICE_NAME,
        },
        reconnectDelay: 3000,
        onConnect: () => {
          console.log('WebSocket connected for chatId:', this.chatId);
          console.log('Subscribing to:', `/topic/chat/${this.chatId}`);
          this.stompClient.subscribe(`/topic/chat/${this.chatId}`, async (message) => {
            try {
              const wsMessageResponse = JSON.parse(message.body);
              const { type, messageResponse } = wsMessageResponse;
              switch (type) {
                case 'CREATE':
                  await this.handleCreateMessage(messageResponse);
                  break;
                case 'READ':
                  await this.handleReadMessage(messageResponse);
                  break;
                case 'UPDATE_CONTENT':
                  await this.handleUpdateContentMessage(messageResponse);
                  break;
                case 'DELETE':
                  await this.handleDeleteMessage(messageResponse);
                  break;
                default:
                  console.warn('Неизвестный тип сообщения:', type);
              }
            } catch (error) {
              console.error('Error processing WebSocket message:', error, 'Raw message:', message.body);
            }
          }, { id: `chat-${this.chatId}` });
        },
        onStompError: (frame) => {
          console.error('STOMP error:', frame.headers['message'], frame.body);
        },
        onWebSocketError: (error) => {
          console.error('WebSocket error:', error);
        },
        onDisconnect: () => {
          console.warn('WebSocket disconnected');
        },
      });
      this.stompClient.onWebSocketClose = () => {
        console.warn('WebSocket connection closed');
      };
      this.stompClient.activate();
    },
    async handleCreateMessage(messageResponse) {
      try {
        const files = messageResponse.messageFiles || [];
        const processedFiles = await Promise.all(
          files.map(async (file) => {
            const cacheKey = `${messageResponse.message.id}-${file.id}`;
            if (this.failedFiles.has(cacheKey)) {
              return {
                id: String(file.id),
                fileName: file.fileName || 'unknown',
                url: this.PhotoDefaultPlaceholder,
                previewUrl: this.PhotoDefaultPlaceholder,
                mimeType: file.mimeType || this.inferMimeType(file.fileName),
              };
            }

            let cachedFile = this.blobUrlCache.get(cacheKey);
            let url = await fetchDownloadMessageFile(this.chatId, messageResponse.message.id, file.id, 'big');
            if (url === this.PhotoDefaultPlaceholder) {
              console.warn(`Failed to fetch WebSocket file ${file.id}, marking as failed`);
              this.failedFiles.add(cacheKey);
            }
            cachedFile = {
              url: url,
              previewUrl: this.isImage(file.fileName, file.mimeType) ? url : this.PhotoDefaultPlaceholder,
              mimeType: file.mimeType || this.inferMimeType(file.fileName),
            };
            this.blobUrlCache.set(cacheKey, cachedFile);

            return {
              id: String(file.id),
              fileName: file.fileName || 'unknown',
              url: cachedFile.url,
              previewUrl: cachedFile.previewUrl,
              mimeType: cachedFile.mimeType,
            };
          })
        );
        const formattedMessage = {
          id: String(messageResponse.message.id),
          chatId: messageResponse.message.chatId,
          senderId: String(messageResponse.message.senderId),
          senderUsername: messageResponse.message.senderUsername || '',
          content: messageResponse.message.content || '',
          hasFiles: messageResponse.message.hasFiles || false,
          isChanged: messageResponse.message.isChanged || false,
          isRead: messageResponse.message.isRead || false,
          createdAt: messageResponse.message.createdAt,
          messageFiles: processedFiles,
          readMessages: messageResponse.readMessages || [],
        };
        this.messages = [...this.messages, formattedMessage];
        this.$nextTick(() => {
          const messageElement = document.querySelector(`[data-message-id="${formattedMessage.id}"]`);
          if (messageElement) {
            this.observer.observe(messageElement);
          } else {
            console.warn('Message element not found for ID:', formattedMessage.id);
          }
          this.scrollToBottom();
        });
      } catch (error) {
        console.error('Error in handleCreateMessage:', error);
      }
    },
    async handleReadMessage(messageResponse) {
      try {
        const messageIndex = this.messages.findIndex((msg) => msg.id === String(messageResponse.message.id));
        if (messageIndex !== -1) {
          const updatedMessage = {
            ...this.messages[messageIndex],
            isRead: messageResponse.message.isRead || false,
            readMessages: messageResponse.readMessages || [],
          };
          this.messages.splice(messageIndex, 1, updatedMessage);
        } else {
          console.warn('Message not found for READ event, ID:', messageResponse.message.id);
        }
      } catch (error) {
        console.error('Ошибка в handleReadMessage:', error);
      }
    },
    async handleUpdateContentMessage(messageResponse) {
      try {
        const messageIndex = this.messages.findIndex((msg) => msg.id === String(messageResponse.message.id));
        if (messageIndex !== -1) {
          const updatedMessage = {
            ...this.messages[messageIndex],
            content: messageResponse.message.content || '',
            isChanged: messageResponse.message.isChanged || false,
            readMessages: messageResponse.readMessages || [],
          };
          this.messages.splice(messageIndex, 1, updatedMessage);
          this.messageMenuId = null;
          this.$nextTick(() => {
            this.scrollToBottom();
          });
        } else {
          console.warn('Message not found for UPDATE_CONTENT event, ID:', messageResponse.message.id);
        }
      } catch (error) {
        console.error('Ошибка в handleUpdateContentMessage:', error);
      }
    },
    async handleDeleteMessage(messageResponse) {
      try {
        const messageId = String(messageResponse.message.id);
        const initialLength = this.messages.length;
        const message = this.messages.find((msg) => msg.id === messageId);
        if (message && message.messageFiles) {
          message.messageFiles.forEach((file) => {
            const cacheKey = `${messageId}-${file.id}`;
            const cachedFile = this.blobUrlCache.get(cacheKey);
            if (cachedFile) {
              if (cachedFile.url && cachedFile.url !== this.PhotoDefaultPlaceholder) {
                URL.revokeObjectURL(cachedFile.url);
              }
              if (cachedFile.previewUrl && cachedFile.previewUrl !== this.PhotoDefaultPlaceholder) {
                URL.revokeObjectURL(cachedFile.previewUrl);
              }
              this.blobUrlCache.delete(cacheKey);
              this.failedFiles.delete(cacheKey);
            }
          });
        }
        this.messages = this.messages.filter((msg) => msg.id !== messageId);
        if (this.messages.length === initialLength) {
          console.warn('Message not found for DELETE event, ID:', messageId);
        }
        this.messageMenuId = null;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error('Ошибка в handleDeleteMessage:', error);
      }
    },
    isImage(fileName, mimeType = '') {
      if (!fileName && !mimeType) return false;
      const imageExtensions = ['jpg', 'jpeg', 'png'];
      const extension = fileName?.split('.').pop()?.toLowerCase();
      return imageExtensions.includes(extension) || mimeType.startsWith('image/');
    },
    inferMimeType(fileName) {
      const extension = fileName?.split('.').pop()?.toLowerCase();
      const mimeMap = {
        jpg: 'image/jpeg',
        jpeg: 'image/jpeg',
        png: 'image/png',
        mp4: 'video/mp4',
        pdf: 'application/pdf',
      };
      return mimeMap[extension] || 'application/octet-stream';
    },
    getFileIcon(fileName, mimeType = '') {
      if (this.isImage(fileName, mimeType)) return '🖼️';
      if (mimeType.includes('video')) return '🎥';
      if (mimeType.includes('pdf')) return '📄';
      return '📎';
    },
    async openImageModal(file, messageId) {
      try {
        const cacheKey = `${messageId}-${file.id}`;
        if (this.failedFiles.has(cacheKey)) {
          this.modalImage = this.PhotoDefaultPlaceholder;
          return;
        }

        let cachedFile = this.blobUrlCache.get(cacheKey);
        if (!cachedFile || cachedFile.url === this.PhotoDefaultPlaceholder) {
          const url = await fetchDownloadMessageFile(this.chatId, messageId, file.id, 'big');
          if (url === this.PhotoDefaultPlaceholder) {
            this.failedFiles.add(cacheKey);
          }
          cachedFile = {
            url: url,
            previewUrl: this.isImage(file.fileName, file.mimeType) ? url : this.PhotoDefaultPlaceholder,
            mimeType: file.mimeType || this.inferMimeType(file.fileName),
          };
          this.blobUrlCache.set(cacheKey, cachedFile);
        }
        this.modalImage = cachedFile.url;
      } catch (error) {
        console.error('Ошибка при открытии изображения:', error);
        this.modalImage = this.PhotoDefaultPlaceholder;
      }
    },
    closeImageModal() {
      if (this.modalImage && this.modalImage !== this.PhotoDefaultPlaceholder) {
        this.modalImage = null;
      }
    },
    createPreviewUrl(file) {
      if (file?.type?.startsWith('image/')) {
        try {
          return URL.createObjectURL(file);
        } catch (error) {
          console.error('Ошибка создания previewUrl:', error);
          return this.PhotoDefaultPlaceholder;
        }
      }
      return this.PhotoDefaultPlaceholder;
    },
    handleFileChange(event) {
      const files = Array.from(event.target.files);
      this.isFileError = false;
      this.fileErrorMessage = '';

      const maxFiles = 10;
      const maxSize = 5 * 1024 * 1024;
      const allowedTypes = ['image/jpeg', 'image/png', 'video/mp4', 'application/pdf'];

      if (this.selectedFiles.length + files.length > maxFiles) {
        this.isFileError = true;
        this.fileErrorMessage = `Максимум ${maxFiles} файлов`;
        return;
      }

      const validFiles = files.filter((file) => {
        if (!allowedTypes.includes(file.type)) {
          this.isFileError = true;
          this.fileErrorMessage = `Недопустимый тип файла: ${file.name}. Разрешены: JPEG, PNG, MP4, PDF`;
          return false;
        }
        if (file.size > maxSize) {
          this.isFileError = true;
          this.fileErrorMessage = `Файл ${file.name} слишком большой (максимум 5 МБ)`;
          return false;
        }
        return true;
      });

      if (this.isFileError) return;

      this.selectedFiles = [
        ...this.selectedFiles,
        ...validFiles.map((file) => {
          const isImage = this.isImage(file.name, file.type);
          return {
            name: file.name,
            type: file.type,
            size: file.size,
            file,
            previewUrl: isImage ? this.createPreviewUrl(file) : null,
          };
        }),
      ].slice(0, maxFiles);
    },
    clearFiles() {
      this.selectedFiles.forEach((file) => {
        if (file.previewUrl && file.previewUrl !== this.PhotoDefaultPlaceholder) {
          URL.revokeObjectURL(file.previewUrl);
        }
      });
      this.selectedFiles = [];
      this.isFileError = false;
      this.fileErrorMessage = '';
    },
    removeFile(index) {
      const file = this.selectedFiles[index];
      if (file.previewUrl && file.previewUrl !== this.PhotoDefaultPlaceholder) {
        URL.revokeObjectURL(file.previewUrl);
      }
      this.selectedFiles.splice(index, 1);
      this.isFileError = false;
      this.fileErrorMessage = '';
    },
    async handleFileDownload(file) {
      if (file.url === this.PhotoDefaultPlaceholder) {
        console.warn(`Invalid file URL for ${file.fileName}, using placeholder`);
        this.isFileError = true;
        this.fileErrorMessage = `Не удалось скачать файл: ${file.fileName}`;
        return;
      }
      try {
        const response = await fetch(file.url);
        if (!response.ok) {
          console.error(`Failed to download file ${file.fileName}: ${response.status}`);
          this.isFileError = true;
          this.fileErrorMessage = `Не удалось скачать файл: ${file.fileName}`;
          return;
        }
        const blob = await response.blob();
        if (blob.size === 0) {
          console.warn(`Blob is empty for file ${file.fileName}`);
          this.isFileError = true;
          this.fileErrorMessage = `Файл ${file.fileName} пуст`;
          return;
        }
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = file.fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(link.href);
      } catch (error) {
        console.error(`Error downloading file ${file.fileName}:`, error);
        this.isFileError = true;
        this.fileErrorMessage = `Ошибка при скачивании файла: ${file.fileName}`;
      }
    },
    formatTime(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    goBackToDashboard() {
      router.push({ name: 'Dashboard' });
    },
    goToProfile(userId) {
      router.push({ name: 'Profile', params: { id: userId } });
    },
    goToChatDetails() {
      if (this.chatData.chat.type === 'PRIVATE') {
        router.push({ name: 'Profile', params: { id: this.chatData.partner.id } });
      } else {
        router.push({ name: 'GroupChat', params: { id: this.chatId } });
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
    handleScroll() {
      if (this.isScrollProcessing) {
        return;
      }

      const debouncedScroll = this.debounce(async () => {
        const container = this.$refs.messagesContainer;
        if (!container || this.isFetching || !this.hasMoreMessages) {
          return;
        }

        const scrollTop = container.scrollTop;
        const scrollHeight = container.scrollHeight;
        const clientHeight = container.clientHeight;
        const scrollPercentage = scrollTop / (scrollHeight - clientHeight);

        if (scrollPercentage < 0.25) {
          this.isScrollProcessing = true;
          this.isFetching = true;
          const prevScrollHeight = scrollHeight;
          const prevScrollTop = scrollTop;

          try {
            this.page++;
            await this.fetchMessages(this.page);
            this.$nextTick(() => {
              const newScrollHeight = container.scrollHeight;
              container.scrollTop = newScrollHeight - prevScrollHeight + prevScrollTop;
            });
          } catch (error) {
            console.error('fetchMessages failed in handleScroll:', error);
            this.page--;
            this.isFileError = true;
            this.fileErrorMessage = `Ошибка загрузки старых сообщений: ${error.message}`;
          } finally {
            this.isFetching = false;
            this.isScrollProcessing = false;
          }
        }
      }, 300);
      debouncedScroll.call(this);
    },
    async handleKeyDown(event) {
      if (event.key === 'Enter') {
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
        this.isSending = true;
        const filesToSend = this.selectedFiles.map((f) => f.file);
        await fetchCreateMessage(this.chatId, this.newMessage, filesToSend);
        this.newMessage = '';
        this.clearFiles();
        this.scrollToBottom();
      } catch (error) {
        console.error('Ошибка при отправке сообщения:', error);
        this.isFileError = true;
        this.fileErrorMessage = 'Ошибка при отправке сообщения';
      } finally {
        this.isSending = false;
      }
    },
    editMessage(messageId) {
      this.editingMessageId = messageId;
      const message = this.messages.find((msg) => msg.id === messageId);
      if (message) {
        this.newMessage = message.content || '';
      }
      this.messageMenuId = null;
    },
    async saveEditedMessage() {
      if (!this.newMessage.trim()) return;
      try {
        this.isSending = true;
        await fetchUpdateMessageContent(this.chatId, this.editingMessageId, this.newMessage);
        this.editingMessageId = null;
        this.newMessage = '';
      } catch (error) {
        console.error('Ошибка при сохранении сообщения:', error);
        this.isFileError = true;
        this.fileErrorMessage = 'Ошибка при сохранении сообщения';
      } finally {
        this.isSending = false;
      }
    },
    async deleteMessage(messageId) {
      try {
        this.isSending = true;
        await fetchDeleteMessage(this.chatId, messageId);
      } catch (error) {
        console.error('Ошибка при удалении сообщения:', error);
        this.isFileError = true;
        this.fileErrorMessage = 'Ошибка при удалении сообщения';
      } finally {
        this.isSending = false;
      }
    },
    async handleVisibleMessages(entries) {
      const container = this.$refs.messagesContainer;
      if (!container) {
        console.warn('Messages container not found');
        return;
      }

      const scrollTop = container.scrollTop;
      const scrollHeight = container.scrollHeight;
      const clientHeight = container.clientHeight;
      const scrollPercentage = scrollTop / (scrollHeight - clientHeight);
      const isAtBottom = scrollPercentage > 0.9;

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
            try {
              await fetchReadMessage(this.chatId, messageId);
              if (isAtBottom) {
                this.scrollToBottom();
              }
            } catch (error) {
              console.error('Ошибка при отметке сообщения как прочитанного:', error);
            }
          }
        }
      }
    },
    handleImageError(file, messageId) {
      console.error(`Image load error for file ${file.id} in message ${messageId}`);
      const cacheKey = `${messageId}-${file.id}`;
      this.failedFiles.add(cacheKey);
      this.blobUrlCache.delete(cacheKey);
      const messageIndex = this.messages.findIndex((msg) => msg.id === messageId);
      if (messageIndex !== -1) {
        const updatedFiles = this.messages[messageIndex].messageFiles.map((f) =>
          f.id === file.id
            ? {
                ...f,
                url: this.PhotoDefaultPlaceholder,
                previewUrl: this.PhotoDefaultPlaceholder,
              }
            : f
        );
        const updatedMessage = {
          ...this.messages[messageIndex],
          messageFiles: updatedFiles,
        };
        this.messages.splice(messageIndex, 1, updatedMessage);
      }
    },
    handleModalImageError() {
      console.error('Modal image load error');
      this.modalImage = this.PhotoDefaultPlaceholder;
    },
    handlePreviewImageError(index) {
      console.error(`Preview image load error for file at index ${index}`);
      this.selectedFiles[index].previewUrl = this.PhotoDefaultPlaceholder;
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
  position: relative;
}

.chat-header {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #333;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 800px;
  z-index: 10;
  height: 80px;
  box-sizing: border-box;
}

.back-button {
  font-size: clamp(1rem, 3vw, 1.25rem);
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
  cursor: pointer;
}

.chat-name {
  font-size: clamp(0.875rem, 3vw, 1rem);
  color: #f9f8f8;
  cursor: pointer;
}

.user-username {
  cursor: pointer;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 5px 0;
}

.messages-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  padding: 20px;
  min-height: 0;
  box-sizing: border-box;
  margin-top: 80px;
  margin-bottom: 80px;
}

.no-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.loading-spinner p {
  margin-top: 0.5rem;
}

.loading-spinner-top {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  color: #f9f8f8;
  font-size: clamp(0.875rem, 3vw, 1rem);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f9f8f8;
  border-top: 4px solid transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
  font-size: clamp(0.875rem, 3vw, 1rem);
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
  font-size: clamp(1rem, 3vw, 1.125rem);
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  margin-top: 5px;
  color: rgba(255, 255, 255, 0.6);
  gap: 20px;
}

.message-files {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 8px;
  margin: 8px 0;
  padding: 4px;
  background-color: #444;
  border-radius: 8px;
}

.file-item {
  flex: 1 1 calc(33.33% - 8px);
  max-width: calc(33.33% - 8px);
  min-width: 80px;
  aspect-ratio: 1/1;
  max-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #555;
  border-radius: 5px;
  overflow: hidden;
}

.file-item:only-child {
  flex: 1 1 auto;
  max-width: 120px;
  max-height: 120px;
  aspect-ratio: 1/1;
}

.file-image-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.file-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  border-radius: 5px;
  transition: transform 0.2s ease-in-out;
}

.file-image:hover {
  transform: scale(1.05);
}

.image-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #555;
  color: #f9f8f8;
  font-size: clamp(0.625rem, 2vw, 0.75rem);
  border-radius: 5px;
}

.image-loading p {
  margin-top: 0.5rem;
}

.file-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: clamp(0.625rem, 2vw, 0.75rem);
  text-align: center;
  color: #f9f8f8;
  background-color: #616161;
  border-radius: 5px;
  overflow: hidden;
  padding: 4px;
}

.file-placeholder a {
  color: #f9f8f8;
  text-decoration: none;
  word-break: break-word;
  padding: 2px;
  display: block;
  width: 100%;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-placeholder a:hover {
  background-color: #666;
}

.file-icon {
  margin-bottom: 4px;
  font-size: clamp(0.875rem, 2.5vw, 1rem);
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
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
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
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 800px;
  z-index: 10;
  height: 80px;
  box-sizing: border-box;
}

.file-error {
  color: #ff4d4d;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
  margin-bottom: 10px;
  text-align: center;
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
  font-size: clamp(0.875rem, 3vw, 1rem);
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
  font-size: clamp(1.25rem, 3vw, 1.5rem);
  color: #f9f8f8;
  cursor: pointer;
}

.send-icon {
  margin-left: 10px;
  font-size: clamp(1.25rem, 3vw, 1.5rem);
  color: #f9f8f8;
  background: none;
  border: none;
  cursor: pointer;
}

.send-icon:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  font-size: clamp(0.875rem, 3vw, 1rem);
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
  margin-bottom: 10px;
}

.file-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.file-modal-title {
  font-size: clamp(0.875rem, 3vw, 1rem);
  font-weight: bold;
  color: #f9f9f9;
}

.file-modal-close {
  background: none;
  border: none;
  color: #f9f9f9;
  font-size: clamp(1rem, 3vw, 1.25rem);
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
  flex: 1 1 calc(25% - 8px);
  max-width: calc(25% - 8px);
  aspect-ratio: 1/1;
  max-height: 60px;
  box-sizing: border-box;
  background-color: #444;
  border: 1px solid #555;
  border-radius: 6px;
  overflow: hidden;
  text-align: center;
  padding: 4px;
}

.file-preview-container {
  position: relative;
  width: 100%;
  height: 100%;
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.file-name {
  font-size: clamp(0.625rem, 2vw, 0.75rem);
  color: #f9f9f9;
  word-break: break-word;
  text-align: center;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .chat-container {
    width: 100%;
    max-width: 100%;
  }

  .chat-header {
    padding: 10px;
    height: 60px;
    max-width: 100%;
  }

  .messages-container {
    margin-top: 60px;
    margin-bottom: 60px;
    padding: 10px;
  }

  .message-item {
    padding: 8px;
  }

  .left-message,
  .right-message {
    max-width: 80%;
  }

  .file-item {
    flex: 1 1 calc(50% - 8px);
    max-width: calc(50% - 8px);
    min-width: 70px;
    max-height: 70px;
    aspect-ratio: 1/1;
  }

  .file-item:only-child {
    max-width: 100px;
    max-height: 100px;
    aspect-ratio: 1/1;
  }

  .file-image {
    width: 100%;
    height: 100%;
  }

  .file-placeholder {
    font-size: clamp(0.5625rem, 1.8vw, 0.6875rem);
    padding: 3px;
  }

  .file-icon {
    font-size: clamp(0.75rem, 2vw, 0.875rem);
    margin-bottom: 3px;
  }

  .file-grid-item {
    flex: 1 1 calc(50% - 8px);
    max-width: calc(50% - 8px);
    max-height: 50px;
    aspect-ratio: 1/1;
  }

  .file-preview {
    width: 100%;
    height: 100%;
  }

  .file-name {
    font-size: clamp(0.5625rem, 1.8vw, 0.6875rem);
    margin-top: 3px;
  }

  .message-footer {
    padding: 10px;
    height: 60px;
    max-width: 100%;
  }
}
</style>