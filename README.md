# context-cli-template-java

基于 [WakeUp-Jin/context-cli-template](https://github.com/WakeUp-Jin/context-cli-template) 的上下文工程脚手架思路，使用 Java 17 实现的 CLI 和可运行项目模板。生成项目提供 DeepSeek 兼容 Chat Completions API、工具调用循环、六类上下文、Agent、事件与评估示例。

## 使用

```bash
mvn package
java -jar target/context-cli-template-java-1.0.0.jar --name my-agent --package com.example.agent
cd my-agent
export DEEPSEEK_API_KEY=你的密钥
mvn package
java -cp target/classes com.example.agent.App "你好"
java -cp target/classes com.example.agent.App --tools "列出当前目录"
```

`--name` 是项目目录及 artifactId，`--package` 是 Java 包名；也可不传参数按提示输入。`--output` 指定生成目录的父目录。CLI 不覆盖已有目录，也不会自动安装依赖。生成的项目仅依赖 Java 17 标准库，构建无需第三方运行库。

环境变量：`DEEPSEEK_API_KEY` 必填；`DEEPSEEK_BASE_URL` 默认 `https://api.deepseek.com`；`DEEPSEEK_MODEL` 默认 `deepseek-chat`。工具读文件限制在启动目录内，请在可信的工作目录运行。若模型反复发起工具调用，最多执行 8 轮。

架构见生成项目中的 `docs/ARCHITECTURE.md`。本项目是 Java 实现，并非上游的逐文件翻译。上游仓库 LICENSE 是 Apache-2.0，尽管其 README/package.json 标示 MIT；来源和许可证以实际 LICENSE 为准。

## 验证

```bash
mvn package
java -jar target/context-cli-template-java-1.0.0.jar --name demo --package org.example.demo
cd demo && mvn package
```
