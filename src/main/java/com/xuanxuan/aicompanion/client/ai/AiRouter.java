package com.xuanxuan.aicompanion.client.ai;

import com.xuanxuan.aicompanion.client.config.AiCompanionConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class AiRouter {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private AiRouter() {
    }

    public static String reply(String prompt) {
        if (AiCompanionConfig.modelName().isBlank()) {
            return "请先在 AI 设置界面输入模型名。";
        }

        return switch (AiCompanionConfig.providerMode()) {
            case CLOUD -> cloudReply(prompt);
            case LOCAL_OLLAMA -> ollamaReply(prompt);
            case LOCAL_LLAMA_CPP -> llamaCppReply(prompt);
        };
    }

    private static String cloudReply(String prompt) {
        if (AiCompanionConfig.cloudApi().isBlank()) {
            return "请先点击“云端”并添加 API。";
        }

        return "云端 API 已保存，当前版本已保留接入入口。请在 AiRouter.cloudReply 中按你的云端服务协议补充请求格式。你的问题是：" + prompt;
    }

    private static String ollamaReply(String prompt) {
        String body = """
                {"model":"%s","prompt":"%s","stream":false}
                """.formatted(json(AiCompanionConfig.modelName()), json(prompt));

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(AiCompanionConfig.localEndpoint()))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                return "ollama 请求失败：" + response.statusCode();
            }
            String parsed = extractJsonString(response.body(), "response");
            return parsed.isBlank() ? response.body() : parsed;
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return "无法连接本地 ollama 服务，请确认 127.0.0.1:11434 已启动。";
        }
    }

    private static String llamaCppReply(String prompt) {
        String body = """
                {"prompt":"%s","n_predict":256}
                """.formatted(json(prompt));

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(AiCompanionConfig.localEndpoint()))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                return "llama.cpp 请求失败：" + response.statusCode();
            }
            String parsed = extractJsonString(response.body(), "content");
            return parsed.isBlank() ? response.body() : parsed;
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return "无法连接本地 llama.cpp 服务，请确认 127.0.0.1:8080 已启动。";
        }
    }

    private static String json(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String extractJsonString(String json, String key) {
        String marker = "\"" + key + "\":";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            return "";
        }

        int start = json.indexOf('"', keyIndex + marker.length());
        if (start < 0) {
            return "";
        }

        StringBuilder value = new StringBuilder();
        boolean escaping = false;
        for (int i = start + 1; i < json.length(); i++) {
            char character = json.charAt(i);
            if (escaping) {
                value.append(switch (character) {
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 't' -> '\t';
                    default -> character;
                });
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else if (character == '"') {
                return value.toString();
            } else {
                value.append(character);
            }
        }
        return "";
    }
}
