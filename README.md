# context-cli-template-java

基于 [WakeUp-Jin/context-cli-template 的 template 目录](https://github.com/WakeUp-Jin/context-cli-template/tree/main/template) 的 Java 17 脚手架。生成项目包含 DeepSeek Chat Completions、可扩展工具、七类上下文、单/多 Agent、事件评估、数据集和离线自检。

## 使用

```bash
mvn package
java -jar target/context-cli-template-java-1.0.0.jar --name my-agent --package com.example.agent
cd my-agent
export DEEPSEEK_API_KEY=你的密钥
mvn package
java -cp target/classes com.example.agent.App "你好"
java -cp target/classes com.example.agent.App --tools "列出当前目录"
java -cp target/classes com.example.agent.App --multi "分析并制定方案"
java -cp target/classes:target/test-classes com.example.agent.TemplateSelfTest
```

`--name` 是项目目录及 artifactId，`--package` 是 Java 包名；也可不传参数按提示输入。`--output` 指定生成目录的父目录。CLI 不覆盖已有目录，也不会自动安装依赖。生成的项目仅依赖 Java 17 标准库，构建无需第三方运行库。

环境变量：`DEEPSEEK_API_KEY` 必填；`DEEPSEEK_BASE_URL` 默认 `https://api.deepseek.com`；`DEEPSEEK_MODEL` 默认 `deepseek-chat`。工具读文件限制在启动目录内，请在可信的工作目录运行。若模型反复发起工具调用，最多执行 8 轮。

架构见生成项目中的 `docs/ARCHITECTURE.md`。本项目按上游模板功能模块重新实现，保留 Java 风格的包结构。上游仓库 LICENSE 是 Apache-2.0，尽管其 README/package.json 标示 MIT；来源和许可证以实际 LICENSE 为准。

| 上游 `template/src` | Java 生成项目 |
| --- | --- |
| `config`, `core/llm` | `config.Environment`, `core.llm` |
| `core/context/modules` | 七个 `core.context.modules` 类及 `ContextManager` |
| `core/tool` | `ToolManager` 内置文件工具与注册接口 |
| `core/agent`, `promptManager` | `SimpleAgent`, `MultiAgent`, `Prompts` |
| `evaluation`, `examples` | `Evaluation`, `Evaluator`, `Dataset`, 三个示例及离线自检 |

## 验证

```bash
mvn package
java -jar target/context-cli-template-java-1.0.0.jar --name demo --package org.example.demo
cd demo && mvn package
```
