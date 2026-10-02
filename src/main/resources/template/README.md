# {{PROJECT_NAME}}

由 [context-cli-template-java](https://github.com/mchgood/context-cli-template-java) 生成。Java 17、Maven、无第三方运行依赖。

```bash
export DEEPSEEK_API_KEY=你的密钥
mvn package
java -cp target/classes {{PACKAGE_NAME}}.App "你好"
java -cp target/classes {{PACKAGE_NAME}}.App --tools "列出当前目录"
java -cp target/classes {{PACKAGE_NAME}}.App --multi "分析并制定方案"
mvn test-compile
java -cp target/classes:target/test-classes {{PACKAGE_NAME}}.TemplateSelfTest
```

工具模式下有 `read_file` 与 `list_files`，可用 `ToolManager.register` 扩展。`--multi` 顺序运行 researcher 与 executor，再由 coordinator 汇总两者输出。文件访问限制在当前工作目录；仅在可信目录运行。支持 `DEEPSEEK_BASE_URL` 和 `DEEPSEEK_MODEL` 环境变量。`docs/ARCHITECTURE.md` 介绍扩展点。

也可运行 `{{PACKAGE_NAME}}.examples.SimpleChatExample`、`MultiChatExample` 与 `EvaluationExample`，分别演示工具对话、多 Agent 与行为评估。
