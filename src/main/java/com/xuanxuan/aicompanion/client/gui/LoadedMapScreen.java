package com.xuanxuan.aicompanion.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

public final class LoadedMapScreen extends Screen {
    private static final int RADIUS = 16;
    private static final int CELL_SIZE = 6;
    private static final int GAP = 1;

    public LoadedMapScreen() {
        super(Text.translatable("screen.ai_companion.map"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 18, 0xFFFFFF);

        if (client == null || client.player == null || client.world == null) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("请先进入一个世界。"), width / 2, height / 2, 0xFF5555);
            return;
        }

        ChunkPos center = client.player.getChunkPos();
        int mapSize = (RADIUS * 2 + 1) * (CELL_SIZE + GAP);
        int startX = (width - mapSize) / 2;
        int startY = 44;

        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int chunkX = center.x + dx;
                int chunkZ = center.z + dz;
                boolean loaded = client.world.getChunkManager().isChunkLoaded(chunkX, chunkZ);
                int color = loaded ? 0xFF4CAF50 : 0xFF000000;

                if (dx == 0 && dz == 0) {
                    color = 0xFFFFD54F;
                }

                int x = startX + (dx + RADIUS) * (CELL_SIZE + GAP);
                int y = startY + (dz + RADIUS) * (CELL_SIZE + GAP);
                context.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, color);
            }
        }

        int legendY = startY + mapSize + 12;
        context.drawTextWithShadow(textRenderer, Text.literal("黄色：玩家所在区块"), startX, legendY, 0xFFFFD54F);
        context.drawTextWithShadow(textRenderer, Text.literal("绿色：已加载区块"), startX, legendY + 12, 0xA5D6A7);
        context.drawTextWithShadow(textRenderer, Text.literal("黑色：未加载区块"), startX, legendY + 24, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("按 ESC 返回游戏"), width / 2, height - 24, 0xAAAAAA);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
