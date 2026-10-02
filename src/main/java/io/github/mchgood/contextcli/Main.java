package io.github.mchgood.contextcli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class Main {
    private static final Pattern NAME = Pattern.compile("[a-z][a-z0-9-]*");
    private static final Pattern PACKAGE = Pattern.compile("[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+");
    private static final List<String> FILES = List.of("pom.xml", ".gitignore", ".env.example", "README.md", "docs/ARCHITECTURE.md",
            "src/main/java/App.java", "src/main/java/Json.java", "src/main/java/LlmClient.java",
            "src/main/java/ContextManager.java", "src/main/java/ToolManager.java", "src/main/java/Agent.java",
            "src/main/java/Evaluation.java");

    public static void main(String[] args) {
        try {
            String name = null, packageName = null, output = ".";
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--name" -> name = value(args, ++i);
                    case "--package" -> packageName = value(args, ++i);
                    case "--output" -> output = value(args, ++i);
                    case "--help", "-h" -> { usage(); return; }
                    default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
                }
            }
            Scanner scanner = new Scanner(System.in);
            if (name == null) { System.out.print("Project name: "); name = scanner.nextLine().trim(); }
            if (packageName == null) { System.out.print("Java package (e.g. com.example.agent): "); packageName = scanner.nextLine().trim(); }
            if (!NAME.matcher(name).matches() || !PACKAGE.matcher(packageName).matches()) {
                throw new IllegalArgumentException("Use a lowercase artifact name [a-z][a-z0-9-]* and package like com.example.agent");
            }
            Path parent = Path.of(output).toAbsolutePath().normalize();
            Files.createDirectories(parent);
            Path target = parent.resolve(name);
            if (Files.exists(target)) throw new IllegalArgumentException("Directory already exists: " + target);
            Path staging = Files.createTempDirectory(parent, ".context-cli-");
            try {
                for (String file : FILES) {
                    String destination = file.startsWith("src/main/java/")
                            ? "src/main/java/" + packageName.replace('.', '/') + "/" + file.substring("src/main/java/".length()) : file;
                    Path path = staging.resolve(destination);
                    Files.createDirectories(path.getParent());
                    try (InputStream in = Main.class.getResourceAsStream("/template/" + file)) {
                        if (in == null) throw new IOException("Missing template resource: " + file);
                        String content = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                                .replace("{{PROJECT_NAME}}", name).replace("{{PACKAGE_NAME}}", packageName);
                        Files.writeString(path, content, StandardCharsets.UTF_8);
                    }
                }
                Files.move(staging, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception e) {
                try (var paths = Files.walk(staging)) {
                    paths.sorted(java.util.Comparator.reverseOrder()).forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) {} });
                }
                throw e;
            }
            System.out.println("Created " + target + "\nNext: cd " + name + " && mvn package");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String value(String[] args, int index) {
        if (index >= args.length) throw new IllegalArgumentException("Missing option value");
        return args[index];
    }
    private static void usage() { System.out.println("Usage: java -jar context-cli-template-java.jar --name my-agent --package com.example.agent [--output DIR]"); }
}
