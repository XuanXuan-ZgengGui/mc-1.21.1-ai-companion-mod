package com.xuanxuan.aicompanion.client.entity;

import com.mojang.authlib.GameProfile;
import com.xuanxuan.aicompanion.client.config.AiCompanionConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public final class CompanionEntityManager {
    public static final String COMPANION_NAME = "XuanXuan-ZhengGui";

    private static OtherClientPlayerEntity companion;
    private static UUID skinUuid = UUID.randomUUID();
    private static int tickCounter;

    private CompanionEntityManager() {
    }

    public static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null || !AiCompanionConfig.joinedGame()) {
            remove(client);
            return;
        }

        if (companion == null || companion.isRemoved() || companion.getWorld() != client.world) {
            spawn(client);
        }

        tickCounter++;
        if (tickCounter % 10 == 0) {
            keepNearPlayer(client);
        }
    }

    public static void respawn(MinecraftClient client) {
        skinUuid = UUID.randomUUID();
        remove(client);
        if (client.world != null && client.player != null && AiCompanionConfig.joinedGame()) {
            spawn(client);
        }
    }

    public static void remove(MinecraftClient client) {
        if (companion == null) {
            return;
        }

        if (client.world != null && !companion.isRemoved()) {
            client.world.removeEntity(companion.getId(), Entity.RemovalReason.DISCARDED);
        }
        companion = null;
    }

    private static void spawn(MinecraftClient client) {
        GameProfile profile = new GameProfile(skinUuid, COMPANION_NAME);
        companion = new OtherClientPlayerEntity(client.world, profile);
        companion.setCustomName(Text.literal(COMPANION_NAME));
        companion.setCustomNameVisible(true);

        Vec3d spawnPos = companionPosition(client, 2.0D);
        companion.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, client.player.getYaw(), 0.0F);
        client.world.addEntity(companion);
    }

    private static void keepNearPlayer(MinecraftClient client) {
        if (companion == null || client.player == null) {
            return;
        }

        double squaredDistance = companion.squaredDistanceTo(client.player);
        if (squaredDistance > 144.0D) {
            Vec3d position = companionPosition(client, 2.0D);
            companion.refreshPositionAndAngles(position.x, position.y, position.z, client.player.getYaw(), 0.0F);
        } else if (squaredDistance > 16.0D) {
            Vec3d target = companionPosition(client, 2.5D);
            Vec3d movement = target.subtract(companion.getPos()).multiply(0.18D);
            companion.setVelocity(movement.x, movement.y, movement.z);
        } else {
            companion.setVelocity(0.0D, companion.getVelocity().y, 0.0D);
        }
    }

    private static Vec3d companionPosition(MinecraftClient client, double distance) {
        float yawRadians = client.player.getYaw() * MathHelper.RADIANS_PER_DEGREE;
        double offsetX = -MathHelper.sin(yawRadians) * distance;
        double offsetZ = MathHelper.cos(yawRadians) * distance;
        return client.player.getPos().add(offsetX, 0.0D, offsetZ);
    }
}
