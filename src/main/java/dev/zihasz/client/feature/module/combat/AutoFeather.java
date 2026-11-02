package dev.zihasz.client.feature.module.combat;

import dev.zihasz.client.feature.module.Category;
import dev.zihasz.client.feature.module.Module;
import dev.zihasz.client.feature.settings.Setting;
import dev.zihasz.client.feature.settings.SettingBuilder;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoFeather extends Module {
    private final Setting<Double> fallSpeed = new SettingBuilder<>(0.5).name("FallSpeed").range(0.1, 2.0).build(this);

    public AutoFeather() {
        super("AutoFeather", "Кидает перл, если падаешь или откидываешься.", Category.COMBAT);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || nullCheck()) return;

        // Если летим вниз быстрее чем нужно - кидаем перл
        if (mc.player.motionY < -fallSpeed.getValue()) {
            int slot = findPearl();
            if (slot != -1) {
                mc.player.inventory.currentItem = slot;
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
            }
        }
    }

    private int findPearl() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ENDER_PEARL) {
                return i;
            }
        }
        return -1;
    }
                  }
