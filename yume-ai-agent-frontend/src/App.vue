<template>
  <router-view />
</template>

<style>
:root {
  --font: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  --radius-sm: 8px;
  --radius-md: 16px;
  --radius-full: 9999px;
  --shadow-card: 0 4px 24px rgba(0, 0, 0, 0.10);
  --shadow-page: 0 0 40px rgba(0, 0, 0, 0.08);
  --bg-page: #f0f2f5;
  --bg-chat: #f5f6fa;
  --bg-bubble-ai: #ffffff;
  --border: #e8eaed;
  --text-primary: #1a1a2e;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;
}

*, *::before, *::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
}

body {
  font-family: var(--font);
  background: var(--bg-page);
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
}

/* Shared scrollbar style */
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #9ca3af; }
</style>
