package com.xuanxuan.aicompanion.client.gui;

import com.xuanxuan.aicompanion.client.AiCompanionClient;
import com.xuanxuan.aicompanion.client.config.AiCompanionConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class LocalModelScreen extends Screen {
    private final Screen parent;
    private final AiCompanionConfig.ProviderMode providerMode;
    private TextFieldWidget modelInput;

    public LocalModelScreen(Screen parent, AiCompanionConfig.ProviderMode providerMode) {
        super(Text.literal("本地服务商"));
        this.parent = parent;
        this.providerMode = providerMode;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int startY = height / 2 - 46;

        modelInput = new TextFieldWidget(textRenderer, centerX - 120, startY + 42, 240, 20, Text.literal("模型名"));
        modelInput.setText(AiCompanionConfig.modelName());
        modelInput.setMaxLength(128);
        addDrawableChild(modelInput);

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.ai_companion.chat"), button -> save(false))
                .dimensions(centerX - 105, startY + 78, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("button.ai_companion.join_game"), button -> save(true))
                .dimensions(centerX + 5, startY + 78, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("返回"), button -> client.setScreen(parent))
                .dimensions(centerX - 50, startY + 108, 100, 20).build());
    }

    private void save(boolean joinGame) {
        AiCompanionConfig.setProviderMode(providerMode);
        AiCompanionConfig.setModelName(modelInput.getText());
        AiCompanionConfig.setChatMode(!joinGame);
        AiCompanionConfig.setJoinedGame(joinGame);
        AiCompanionConfig.save();
        if (joinGame) {
            AiCompanionClient.addChatMessage(Text.literal("[" + AiCompanionConfig.modelName() + "] 已加入游戏。输入 @*" + AiCompanionConfig.modelName() + " 开始聊天。"));
        }
        client.setScreen(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int centerX = width / 2;
        int startY = height / 2 - 46;
        String provider = providerMode == AiCompanionConfig.ProviderMode.LOCAL_OLLAMA ? "本地 ollama" : "本地 llama.cpp";
        String endpoint = providerMode == AiCompanionConfig.ProviderMode.LOCAL_OLLAMA ? "127.0.0.1:11434" : "127.0.0.1:8080";
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("服务商：" + provider), centerX, startY - 24, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("服务商 IP：" + endpoint), centerX, startY - 8, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, Text.literal("模型名"), centerX - 120, startY + 30, 0xAAAAAA);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("聊天：在游戏内输入 @*模型名 或 @模型名"), centerX, startY + 138, 0xAAAAAA);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
