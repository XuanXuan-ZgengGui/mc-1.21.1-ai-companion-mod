package com.xuanxuan.aicompanion.client.gui;

import com.xuanxuan.aicompanion.client.config.AiCompanionConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class LocalProviderScreen extends Screen {
    private final Screen parent;

    public LocalProviderScreen(Screen parent) {
        super(Text.literal("本地 AI"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int startY = height / 2 - 36;

        addDrawableChild(ButtonWidget.builder(Text.literal("ollama"), button ->
                client.setScreen(new LocalModelScreen(this, AiCompanionConfig.ProviderMode.LOCAL_OLLAMA))
        ).dimensions(centerX - 105, startY, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("llama.cpp"), button ->
                client.setScreen(new LocalModelScreen(this, AiCompanionConfig.ProviderMode.LOCAL_LLAMA_CPP))
        ).dimensions(centerX + 5, startY, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("返回"), button ->
                client.setScreen(parent)
        ).dimensions(centerX - 50, startY + 36, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 70, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("选择本地服务商 IP 类型"), width / 2, height / 2 - 54, 0xAAAAAA);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
