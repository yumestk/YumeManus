# MODULES.md

# Yume AI Agent 模块说明

> 本文按“职责 + 关键类 + 输入输出 + 扩展点”介绍项目模块。

---

## 1. 模块总览

| 模块 | 目录 | 职责 |
| --- | --- | --- |
| 启动模块 | `src/main/java/com/yume/yumeaiagent/` | 应用启动与全局装配 |
| 接口层 | `controller/` | 对外提供 AI、SSE、健康检查接口 |
| 应用编排层 | `app/` | 串联模型、记忆、RAG、工具调用 |
| Agent 核心层 | `agent/` | 自主规划、步骤执行、状态管理 |
| Advisor 层 | `advisor/` | 日志、推理增强、记忆增强 |
| RAG 层 | `rag/` | 文档加载、向量存储、查询增强、检索策略 |
| Tool 层 | `tools/` | 可被 LLM 调用的工具集合 |
| 记忆层 | `chatmemory/` | 文件持久化会话记忆实现 |
| 配置层 | `config/` | CORS 与全局配置扩展 |
| 常量层 | `constant/` | 文件路径等共享常量 |
| 前端模块 | `yume-ai-agent-frontend/` | 对话页面与 SSE 消费 |
| MCP 子模块 | `yume-image-search-mcp-server/` | 独立工具服务（图片搜索） |

---

## 2. 启动模块

## 2.1 `YuAiAgentApplication`

- 路径：`src/main/java/com/yume/yumeaiagent/YuAiAgentApplication.java`
- 职责：Spring Boot 启动入口。
- 说明：当前代码排除了 `DataSourceAutoConfiguration`，用于简化开发/部署；启用 PGVector 时需要按注释恢复数据库自动配置。

---

## 3. 接口层（Controller）

## 3.1 `AiController`

- 路径：`src/main/java/com/yume/yumeaiagent/controller/AiController.java`
- 主要接口：

| 接口 | 方法 | 说明 |
| --- | --- | --- |
| `/api/ai/love_app/chat/sync` | GET | 恋爱大师同步对话 |
| `/api/ai/love_app/chat/sse` | GET | 恋爱大师 SSE 文本流 |
| `/api/ai/love_app/char/sse` | GET | ServerSentEvent 形式流式输出 |
| `/api/ai/love_app/chat/sse_emitter` | GET | `SseEmitter` 形式流式输出 |
| `/api/ai/manus/chat` | GET | 超级智能体多步执行流 |

## 3.2 `HealthController`

- 路径：`src/main/java/com/yume/yumeaiagent/controller/HealthController.java`
- 接口：`/api/health`
- 作用：服务可用性探针。

---

## 4. 应用编排层（App）

## 4.1 `LoveApp`

- 路径：`src/main/java/com/yume/yumeaiagent/app/LoveApp.java`
- 角色：项目“业务中枢”，封装对外核心能力。

### 主要能力方法

| 方法 | 说明 |
| --- | --- |
| `doChat` | 多轮对话（同步） |
| `doChatByStream` | 多轮对话（SSE） |
| `doChatWithReport` | 结构化输出恋爱报告 |
| `doChatWithRag` | RAG 检索增强问答 |
| `doChatWithTools` | 本地工具调用 |
| `doChatWithMcp` | MCP 服务调用 |

### 关键依赖

- `ChatClient`
- `MessageChatMemoryAdvisor`
- `QuestionAnswerAdvisor`
- `ToolCallback[]`
- `ToolCallbackProvider`（MCP）

### 扩展点

- 切换模型（DashScope / Ollama）
- 叠加更多 Advisor
- 按场景切换不同 RAG 策略

---

## 5. Agent 核心层

## 5.1 类结构

| 类 | 作用 |
| --- | --- |
| `BaseAgent` | 定义通用执行框架（状态、步数、run/runStream） |
| `ReActAgent` | 定义 think/act 模板方法 |
| `ToolCallAgent` | 实现工具选择与执行闭环 |
| `YumeManus` | 通用超级智能体实例 |
| `AgentState` | IDLE/Running/Finished/Error |

## 5.2 执行机制

1. 用户输入进入 `run` / `runStream`。
2. 循环执行 `step()`。
3. `think()` 决定工具调用计划。
4. `act()` 执行工具并回写上下文。
5. 调用终止工具或达到最大步数后结束。

## 5.3 `YumeManus`

- 预置系统提示词：强调任务导向、多工具协同。
- 预置 next-step 提示词：引导主动规划。
- 最大步数：20。

---

## 6. Advisor 层

## 6.1 `MyLoggerAdvisor`

- 路径：`advisor/MyLoggerAdvisor.java`
- 能力：打印 AI 请求和响应，支持 call 和 stream 两种模式。
- 用途：调试 Prompt 与结果质量。

## 6.2 `ReReadingAdvisor`

- 路径：`advisor/ReReadingAdvisor.java`
- 能力：将用户问题追加“Read the question again”提示。
- 用途：用于尝试提升复杂问题推理稳定性（可选）。

---

## 7. RAG 模块

## 7.1 文档处理

| 组件 | 作用 |
| --- | --- |
| `LoveAppDocumentLoader` | 批量加载 Markdown 文档并附加元信息 |
| `MyTokenTextSplitter` | 文档切分（当前作为可选能力） |
| `MyKeywordEnricher` | 补充关键词元信息 |

知识文档路径：`src/main/resources/document/*.md`

## 7.2 向量存储

| 组件 | 状态 | 说明 |
| --- | --- | --- |
| `LoveAppVectorStoreConfig` | 启用 | 基于 `SimpleVectorStore` 的默认存储 |
| `PgVectorVectorStoreConfig` | 预留 | 手动配置 `PgVectorStore`，当前注解关闭 |

## 7.3 检索增强

| 组件 | 作用 |
| --- | --- |
| `QueryRewriter` | 对用户查询做改写，提升召回质量 |
| `LoveAppRagCustomAdvisorFactory` | 构建带过滤和增强策略的 RAG Advisor |
| `LoveAppContextualQueryAugmenterFactory` | 定义空上下文兜底回答模板 |
| `LoveAppRagCloudAdvisorConfig` | 接入 DashScope 云知识库检索 |

---

## 8. Tool 模块

## 8.1 统一注册

- 类：`ToolRegistration`
- 作用：将全部工具组装成 `ToolCallback[]` 注入模型调用链。

## 8.2 本地工具清单

| 工具类 | 主要能力 |
| --- | --- |
| `FileOperationTool` | 文件读写 |
| `WebSearchTool` | 搜索引擎查询（SearchAPI） |
| `WebScrapingTool` | 网页 HTML 抓取 |
| `ResourceDownloadTool` | URL 资源下载 |
| `TerminalOperationTool` | 终端命令执行 |
| `PDFGenerationTool` | PDF 生成 |
| `TerminateTool` | 任务终止 |

## 8.3 工具扩展方式

1. 新建工具类并使用 `@Tool` 标注方法。
2. 在 `ToolRegistration` 中注册实例。
3. Agent 自动可见该工具并可调用。

---

## 9. 记忆模块

## 9.1 `FileBasedChatMemory`

- 路径：`chatmemory/FileBasedChatMemory.java`
- 实现：`ChatMemory`
- 存储：Kryo 序列化到本地文件。

当前 `LoveApp` 默认使用内存窗口记忆 `MessageWindowChatMemory`；文件记忆实现保留，便于切换长期会话。

---

## 10. 配置模块

## 10.1 应用配置文件

| 文件 | 用途 |
| --- | --- |
| `application.yml` | 主配置（profile 默认 local） |
| `application-local.yml` | 本地运行配置 |
| `application-prod.yml` | 生产配置 |
| `mcp-servers.json` | stdio MCP 服务声明 |

## 10.2 关键配置项

- `spring.ai.dashscope.api-key`
- `spring.ai.mcp.client.*`
- `spring.datasource.*`
- `search-api.api-key`
- `server.port / context-path`

## 10.3 `CorsConfig`

- 全局放行跨域，支持前端本地开发联调。

---

## 11. 前端模块（Vue3）

目录：`yume-ai-agent-frontend/src`

| 文件 | 作用 |
| --- | --- |
| `views/Home.vue` | 首页应用入口（恋爱大师/超级智能体） |
| `views/LoveMaster.vue` | 恋爱大师对话页，消费 SSE |
| `views/ManusAgent.vue` | 超级智能体对话页，展示步骤输出 |
| `router/index.js` | 路由配置 |
| `utils/uuid.js` | 会话 ID 生成 |

工程特点：

- 使用 `EventSource` 接收流式响应。
- `vite.config.js` 代理 `/api` 到后端端口 `8124`。

---

## 12. MCP 子模块

目录：`yume-image-search-mcp-server`

| 组件 | 作用 |
| --- | --- |
| `YumeImageSearchMcpServerApplication` | MCP Server 启动与工具注册 |
| `ImageSearchTool` | 通过 Pexels API 搜索图片 |
| `application-sse.yml` | SSE 模式配置 |
| `application-stdio.yml` | stdio 模式配置 |

该子模块用于演示“工具外部化”能力，是主系统 MCP 接入的实践样例。

---

## 13. 配置与运行建议

1. 开发阶段：先用 `SimpleVectorStore` 跑通核心链路。
2. 进阶阶段：接入 PGVector 与云知识库做效果对比。
3. 工具安全：对终端工具增加命令白名单策略。
4. 展示准备：保留 2~3 个可复现问答样例，便于面试讲解。