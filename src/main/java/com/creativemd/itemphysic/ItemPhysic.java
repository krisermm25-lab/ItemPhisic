package com.creativemd.itemphysic;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod(ItemPhysic.MOD_ID)
public class ItemPhysic {
    public static final String MOD_ID = "itemphysic";

    private static final double RANGE = 5.5D;
    private static final int COOLDOWN = 7;

    private int cooldown = 0;

    public ItemPhysic() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLClientSetupEvent event) {
        System.out.println("[ItemPhysic] Realistic item physics loaded!");
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null || mc.level == null) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        AxisAlignedBB box = player.getBoundingBox().inflate(RANGE);
        List<LivingEntity> targets = mc.level.getEntitiesOfClass(LivingEntity.class, box, 
            e -> e != player && e.isAlive() && player.canSee(e));

        if (!targets.isEmpty()) {
            LivingEntity target = targets.stream()
                .min((a, b) -> Double.compare(a.distanceTo(player), b.distanceTo(player)))
                .get();

            player.attack(target);
            player.swing(player.getUsedItemHand());

            cooldown = COOLDOWN;
        }
    }
}
