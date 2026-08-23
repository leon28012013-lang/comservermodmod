package net.cyberbound.comservermod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "comservermod")
public class ModEnchantmentEvents {

    public static final ResourceKey<Enchantment> REINFORCED_BREAKER = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("comservermod", "reinforced_breaker")
    );

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        BlockState state = event.getState();
        if (state.is(Blocks.REINFORCED_DEEPSLATE)) {
            ItemStack tool = event.getEntity().getMainHandItem();
            event.getEntity().level().registryAccess().registry(Registries.ENCHANTMENT).ifPresent(registry -> {
                registry.getHolder(REINFORCED_BREAKER).ifPresent(enchantmentHolder -> {
                    if (tool.getEnchantmentLevel(enchantmentHolder) > 0) {
                        event.setNewSpeed(event.getOriginalSpeed() * 12.0F);
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().is(Blocks.REINFORCED_DEEPSLATE)) {
            ItemStack tool = event.getPlayer().getMainHandItem();
            event.getLevel().registryAccess().registry(Registries.ENCHANTMENT).ifPresent(registry -> {
                registry.getHolder(REINFORCED_BREAKER).ifPresent(enchantmentHolder -> {
                    if (tool.getEnchantmentLevel(enchantmentHolder) > 0) {
                        double x = event.getPos().getX() + 0.5;
                        double y = event.getPos().getY() + 0.5;
                        double z = event.getPos().getZ() + 0.5;

                        if (event.getLevel() instanceof Level level) {
                            ItemEntity drop = new ItemEntity(level, x, y, z, new ItemStack(Items.REINFORCED_DEEPSLATE));
                            level.addFreshEntity(drop);
                        }
                    }
                });
            });
        }
    }
}