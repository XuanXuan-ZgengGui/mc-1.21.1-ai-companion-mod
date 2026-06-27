package com.xuanxuan.aicompanion.client.gui;

import com.xuanxuan.aicompanion.client.config.AiCompanionConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class CloudConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget apiInput;
    private TextFieldWidget modelInput;

    public CloudConfigScreen(Screen parent) {
        super(Text.literal("云端 AI"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int startY = height / 2 - 58;

        apiInput = new TextFieldWidget(textRenderer, centerX - 120, startY + 18, 240, 20, Text.literal("API"));
        apiInput.setText(AiCompanionConfig.cloudApi());
        apiInput.setMaxLength(512);
        addDrawableChild(apiInput);

        modelInput = new TextFieldWidget(textRenderer, centerX - 120, startY + 58, 240, 20, Text.literal("模型名"));
        modelInput.setText(AiCompanionConfig.modelName());
        modelInput.setMaxLength(128);
        addDrawableChild(modelInput);

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.ai_companion.chat"), button -> save(false))
                .dimensions(centerX - 105, startY + 94, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("button.ai_companion.join_game"), button -> save(true))
                .dimensions(centerX + 5, startY + 94, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("返回"), button -> client.setScreen(parent))
                .dimensions(centerX - 50, startY + 124, 100, 20).build());
    }

    private void save(boolean joinGame) {
        AiCompanionConfig.setProviderMode(AiCompanionConfig.ProviderMode.CLOUD);
        AiCompanionConfig.setCloudApi(apiInput.getText());
        AiCompanionConfig.setModelName(modelInput.getText());
        AiCompanionConfig.setChatMode(!joinGame);
        AiCompanionConfig.setJoinedGame(joinGame);
        AiCompanionConfig.save();
        client.setScreen(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int centerX = width / 2;
        int startY = height / 2 - 58;
        context.drawCenteredTextWithShadow(textRenderer, title, centerX, startY - 22, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("button.ai_companion.add_api"), centerX - 120, startY + 6, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, Text.literal("模型名"), centerX - 120, startY + 46, 0xAAAAAA);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("聊天：在游戏内输入 @*模型名 或 @模型名"), centerX, startY + 154, 0xAAAAAA);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
