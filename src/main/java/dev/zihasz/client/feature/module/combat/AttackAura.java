package dev.zihasz.client.feature.module.combat;

import dev.zihasz.client.feature.module.Category;
import dev.zihasz.client.feature.module.Module;
import dev.zihasz.client.feature.settings.Setting;
import dev.zihasz.client.feature.settings.SettingBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AttackAura extends Module {
    private final Setting<Double> range = new SettingBuilder<>(4.5).name("Range").range(3.0, 6.0).build(this);
    private final Setting<Boolean> players = new SettingBuilder<>(true).name("Players").build(this);
    private final Setting<Boolean> mobs = new SettingBuilder<>(true).name("Mobs").build(this);
    private final Setting<Boolean> rotate = new SettingBuilder<>(true).name("Rotate").build(this);

    public AttackAura() {
        super("AttackAura", "Ебашит всех вокруг без кликов.", Category.COMBAT);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || nullCheck()) return;

        EntityLivingBase target = findTarget();
        if (target != null) {
            if (rotate.getValue()) {
                // Ебанутая быстрая ротация прямо на цель
                double diffX = target.posX - mc.player.posX;
                double diffZ = target.posZ - mc.player.posZ;
                double diffY = target.posY + target.getEyeHeight() / 2 - (mc.player.posY + mc.player.getEyeHeight());
                double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

                float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
                float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

                mc.player.rotationYaw = yaw;
                mc.player.rotationPitch = pitch;
            }

            // Бьем с учетом кулдауна (чтобы не забанили за спам)
            if (mc.player.getCooledAttackStrength(0) >= 0.9F) {
                mc.playerController.attackEntity(mc.player, target);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    private EntityLivingBase findTarget() {
        EntityLivingBase target = null;
        double closestDist = range.getValue();

        if (players.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player == mc.player || player.isDead || player.getHealth() <= 0) continue;
                double dist = mc.player.getDistance(player);
                if (dist < closestDist) {
                    closestDist = dist;
                    target = player;
                }
            }
        }

        if (mobs.getValue()) {
            for (EntityMob mob : mc.world.getEntities(EntityMob.class, null)) {
                if (mob.isDead || mob.getHealth() <= 0) continue;
                double dist = mc.player.getDistance(mob);
                if (dist < closestDist) {
                    closestDist = dist;
                    target = mob;
                }
            }
        }

        return target;
    }
}
