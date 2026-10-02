# 上下文工程模板架构

`core.context.ContextManager` 管理七类上下文：系统提示词、会话消息、工具消息序列、长期记忆、相关上下文、结构化输出提示和执行历史。按固定顺序组装每轮请求；会话和工具消息按消息边界裁剪。记忆仅保存在进程内。

`core.llm.LlmClient` 通过 Java HttpClient 请求兼容 OpenAI Chat Completions 的 DeepSeek API，保留响应中的工具调用 JSON。`core.agent.SimpleAgent` 负责模型与工具的循环、工具结果回填及最大轮次限制。`core.agent.MultiAgent` 顺序调用 researcher、executor，并将两者真实输出交给 coordinator 汇总。`core.tool.ToolManager` 注册工具、声明 JSON Schema、执行工具，并限制内置文件工具访问在工作目录内。自定义工具需要自行管理权限。

`evaluation.Evaluation` 提供单次运行事件记录和工具调用/耗时统计；`Evaluator` 比较预期 Agent/工具集合，`Dataset` 包含行为数据集示例。`TemplateSelfTest` 使用假模型离线验证工具循环、多 Agent 和评估。示例采用进程内存，流式响应尚未实现。

边界：这是教学用起点。生产使用需补齐鉴权、脱敏、超时重试策略、并发隔离、持久化、观测与针对具体模型的兼容性验证。
