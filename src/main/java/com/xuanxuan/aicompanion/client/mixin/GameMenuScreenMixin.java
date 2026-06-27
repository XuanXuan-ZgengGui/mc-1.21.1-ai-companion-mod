package com.xuanxuan.aicompanion.client.mixin;

import com.xuanxuan.aicompanion.client.gui.AiProviderScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void aiCompanion$addProviderButton(CallbackInfo ci) {
        int buttonWidth = 204;
        int buttonHeight = 20;
        int x = width / 2 - buttonWidth / 2;
        int y = height / 4 + 144;

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.ai_companion.join_ai"), button ->
                client.setScreen(new AiProviderScreen(this))
        ).dimensions(x, y, buttonWidth, buttonHeight).build());
    }
}
