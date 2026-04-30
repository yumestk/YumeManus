<template>
  <div class="home">
    <header class="hero">
      <div class="hero-logo">✨</div>
      <h1>Yume AI Agent</h1>
      <p>选择一个应用，开始你的 AI 体验</p>
    </header>

    <main class="cards">
      <div class="card love-card" @click="$router.push('/love')">
        <div class="card-icon">💕</div>
        <div class="card-body">
          <h2>AI 恋爱大师</h2>
          <p>专业的恋爱顾问，为你解答情感困惑，提供贴心建议</p>
        </div>
        <span class="card-btn love-btn">开始聊天 →</span>
      </div>

      <div class="card manus-card" @click="$router.push('/manus')">
        <div class="card-icon">🤖</div>
        <div class="card-body">
          <h2>AI 超级智能体</h2>
          <p>强大的多工具智能体，帮你规划、搜索、执行复杂任务</p>
        </div>
        <span class="card-btn manus-btn">开始聊天 →</span>
      </div>
    </main>

    <footer class="home-footer">
      Powered by Spring AI · DashScope ·
      <a href="https://github.com/yumestk" target="_blank" rel="noopener">GitHub @yumestk</a>
    </footer>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px 24px;
  background: linear-gradient(150deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

/* Hero */
.hero {
  text-align: center;
  color: #fff;
  margin-bottom: 48px;
}

.hero-logo {
  font-size: 3rem;
  margin-bottom: 12px;
  filter: drop-shadow(0 0 16px rgba(255,255,255,0.4));
}

.hero h1 {
  font-size: clamp(1.8rem, 5vw, 2.8rem);
  font-weight: 700;
  letter-spacing: -0.5px;
  margin-bottom: 10px;
  background: linear-gradient(90deg, #a78bfa, #60a5fa, #f472b6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero p {
  font-size: clamp(0.9rem, 2.5vw, 1.05rem);
  color: rgba(255,255,255,0.6);
}

/* Cards */
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  width: 100%;
  max-width: 640px;
}

.card {
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 28px 24px;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0,0,0,0.3);
}

.love-card:hover { border-color: rgba(244, 114, 182, 0.5); }
.manus-card:hover { border-color: rgba(96, 165, 250, 0.5); }

.card-icon {
  font-size: 2.4rem;
  line-height: 1;
}

.card-body h2 {
  font-size: 1.15rem;
  font-weight: 600;
  color: #fff;
  margin-bottom: 6px;
}

.card-body p {
  font-size: 0.875rem;
  color: rgba(255,255,255,0.55);
  line-height: 1.6;
}

.card-btn {
  display: inline-block;
  padding: 9px 20px;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 500;
  align-self: flex-start;
  transition: opacity 0.2s;
}

.card-btn:hover { opacity: 0.85; }

.love-btn {
  background: linear-gradient(135deg, #f472b6, #a855f7);
  color: #fff;
}

.manus-btn {
  background: linear-gradient(135deg, #60a5fa, #6366f1);
  color: #fff;
}

/* Footer */
.home-footer {
  margin-top: 40px;
  font-size: 0.78rem;
  color: rgba(255,255,255,0.3);
  letter-spacing: 0.5px;
}

.home-footer a {
  color: rgba(255,255,255,0.55);
  text-decoration: none;
  transition: color 0.2s;
}

.home-footer a:hover { color: rgba(255,255,255,0.9); }

/* Mobile */
@media (max-width: 480px) {
  .hero { margin-bottom: 32px; }
  .cards { grid-template-columns: 1fr; max-width: 100%; }
  .card { padding: 22px 18px; }
}
</style>
