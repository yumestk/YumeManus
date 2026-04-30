<template>
  <div class="chat-page">
    <div class="chat-header">
      <button class="back-btn" @click="$router.push('/')">
        <span class="back-icon">←</span>
        <span class="back-label">返回</span>
      </button>
      <div class="header-title">
        <span class="header-avatar">🤖</span>
        <span>AI 超级智能体</span>
      </div>
      <span class="status-badge" :class="{ active: isStreaming }">
        {{ isStreaming ? '执行中' : '就绪' }}
      </span>
    </div>

    <div class="message-list" ref="messageListRef">
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">⚡</div>
        <p>告诉智能体你想完成什么任务～</p>
      </div>

      <div
        v-for="msg in messages"
        :key="msg.id"
        class="message-row"
        :class="msg.role"
      >
        <div class="avatar" :class="msg.role">
          <span v-if="msg.role === 'user'">我</span>
          <span v-else>🤖</span>
        </div>
        <div class="bubble-wrap">
          <div class="bubble" :class="[msg.role, { streaming: msg.streaming }]">
            <span v-if="msg.content">{{ msg.content }}</span>
            <span v-else-if="msg.streaming" class="thinking">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <input
        v-model="inputText"
        type="text"
        placeholder="输入任务描述…"
        @keydown.enter="handleSend"
        :disabled="isStreaming"
        maxlength="500"
      />
      <button class="send-btn" @click="handleSend" :disabled="isStreaming || !inputText.trim()">
        <span v-if="!isStreaming">发送</span>
        <span v-else class="sending-dots"><span class="dot"></span><span class="dot"></span><span class="dot"></span></span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onUnmounted } from 'vue'
import { generateUUID } from '../utils/uuid.js'

const messages = ref([])
const inputText = ref('')
const isStreaming = ref(false)
const messageListRef = ref(null)
let currentEventSource = null

onUnmounted(() => { if (currentEventSource) currentEventSource.close() })

function scrollToBottom() {
  nextTick(() => {
    if (messageListRef.value)
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  })
}

function handleSend() {
  const text = inputText.value.trim()
  if (!text || isStreaming.value) return

  inputText.value = ''
  isStreaming.value = true

  messages.value.push({ id: generateUUID(), role: 'user', content: text, streaming: false })
  const aiMsg = { id: generateUUID(), role: 'ai', content: '', streaming: true }
  messages.value.push(aiMsg)
  scrollToBottom()

  if (currentEventSource) currentEventSource.close()

  const url = `/api/ai/manus/chat?message=${encodeURIComponent(text)}`
  const es = new EventSource(url)
  currentEventSource = es

  es.onmessage = (event) => {
    // Each SSE event = one completed step, append with a trailing newline
    aiMsg.content += event.data + '\n'
    scrollToBottom()
  }

  es.onerror = () => {
    es.close()
    currentEventSource = null
    aiMsg.streaming = false
    // Trim trailing newline left by the last chunk
    aiMsg.content = aiMsg.content.trimEnd()
    isStreaming.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
/* ── Layout ── */
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 860px;
  margin: 0 auto;
  background: #fff;
  box-shadow: var(--shadow-page);
}

/* ── Header ── */
.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #60a5fa 0%, #6366f1 100%);
  color: #fff;
  flex-shrink: 0;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(255,255,255,0.18);
  border: none;
  color: #fff;
  padding: 6px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 0.875rem;
  transition: background 0.15s;
  white-space: nowrap;
}
.back-btn:hover { background: rgba(255,255,255,0.28); }

.header-title {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1rem;
  font-weight: 600;
}
.header-avatar { font-size: 1.2rem; }

.status-badge {
  font-size: 0.72rem;
  padding: 3px 10px;
  border-radius: var(--radius-full);
  background: rgba(255,255,255,0.18);
  white-space: nowrap;
  transition: background 0.2s;
}
.status-badge.active { background: rgba(255,255,255,0.32); }

/* ── Message list ── */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  background: var(--bg-chat);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin: auto;
  color: var(--text-muted);
}
.empty-icon { font-size: 2.5rem; }
.empty-state p { font-size: 0.9rem; }

/* ── Message row ── */
.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.message-row.user { flex-direction: row-reverse; }

/* ── Avatar ── */
.avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
}
.avatar.user {
  background: linear-gradient(135deg, #60a5fa, #6366f1);
  color: #fff;
}
.avatar.ai {
  background: #fff;
  font-size: 1.2rem;
  border: 1px solid #e0e7ff;
}

/* ── Bubble ── */
.bubble-wrap { display: flex; flex-direction: column; max-width: min(65%, 480px); }

.bubble {
  padding: 10px 14px;
  border-radius: 18px;
  line-height: 1.65;
  font-size: 0.925rem;
  word-break: break-word;
  white-space: pre-wrap;
  text-align: left;
}

.bubble.user {
  background: linear-gradient(135deg, #60a5fa, #6366f1);
  color: #fff;
  border-bottom-right-radius: 4px;
  align-self: flex-end;
}

.bubble.ai {
  background: var(--bg-bubble-ai);
  color: var(--text-primary);
  border-bottom-left-radius: 4px;
  border: 1px solid var(--border);
  align-self: flex-start;
}

.bubble.streaming::after {
  content: '▋';
  animation: blink 0.9s step-end infinite;
  color: #6366f1;
  font-size: 0.8em;
}

/* ── Thinking dots ── */
.thinking {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  padding: 2px 0;
}
.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #818cf8;
  animation: bounce 1.2s ease-in-out infinite;
}
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

.sending-dots { display: inline-flex; gap: 3px; align-items: center; }
.sending-dots .dot { background: rgba(255,255,255,0.8); width: 5px; height: 5px; }

@keyframes blink { 50% { opacity: 0; } }
@keyframes bounce { 0%, 80%, 100% { transform: scale(0.7); opacity: 0.5; } 40% { transform: scale(1); opacity: 1; } }

/* ── Input area ── */
.input-area {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
  background: #fff;
  flex-shrink: 0;
}

.input-area input {
  flex: 1;
  padding: 10px 16px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-full);
  font-size: 0.925rem;
  font-family: var(--font);
  outline: none;
  transition: border-color 0.2s;
  background: var(--bg-chat);
  color: var(--text-primary);
  min-width: 0;
}
.input-area input:focus { border-color: #60a5fa; background: #fff; }
.input-area input:disabled { opacity: 0.6; }

.send-btn {
  padding: 10px 22px;
  background: linear-gradient(135deg, #60a5fa, #6366f1);
  color: #fff;
  border: none;
  border-radius: var(--radius-full);
  font-size: 0.925rem;
  font-family: var(--font);
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.2s, transform 0.1s;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 72px;
}
.send-btn:hover:not(:disabled) { opacity: 0.88; transform: scale(1.02); }
.send-btn:disabled { opacity: 0.45; cursor: not-allowed; }

/* ── Responsive ── */
@media (max-width: 600px) {
  .chat-header { padding: 10px 12px; gap: 8px; }
  .back-label { display: none; }
  .back-btn { padding: 6px 10px; }
  .message-list { padding: 14px 10px; gap: 14px; }
  .bubble-wrap { max-width: 78%; }
  .input-area { padding: 10px 10px; gap: 8px; }
  .send-btn { padding: 10px 16px; min-width: 60px; }
}
</style>
