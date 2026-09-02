package net.cyberbound.comservermod.procedures;

import net.cyberbound.comservermod.ComServerMod;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;

@EventBusSubscriber(modid = ComServerMod.MODID)
public class PaleOakMakingProcedure {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event == null || event.getLevel() == null) return;

        if (event.getLevel() instanceof Level level) {
            BlockPos pos = event.getPos();
            Block placedBlock = level.getBlockState(pos).getBlock();

            ComServerMod.LOGGER.info("[PaleOak] Placed: {}", placedBlock);

            if (placedBlock == Blocks.DARK_OAK_SAPLING) {
                ComServerMod.LOGGER.info("[PaleOak] Dark Oak Sapling detected, checking biome...");

                float temp = level.getBiome(pos).value().getBaseTemperature();
                ComServerMod.LOGGER.info("[PaleOak] Biome temperature: {}", temp);

                if (temp >= 1.5f) {
                    ResourceLocation paleOakId = ResourceLocation.fromNamespaceAndPath("minecraft", "pale_oak_sapling");
                    boolean exists = BuiltInRegistries.BLOCK.containsKey(paleOakId);

                    ComServerMod.LOGGER.info("[PaleOak] Pale Oak Sapling registered: {}", exists);

                    if (exists) {
                        Block paleOakBlock = BuiltInRegistries.BLOCK.get(paleOakId);

                        if (paleOakBlock != null && paleOakBlock != Blocks.AIR) {
                            level.setBlock(pos, paleOakBlock.defaultBlockState(), 3);
                            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);

                            if (level instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(
                                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5,
                                    12, 0.2, 0.2, 0.2, 0.02
                                );
                            }
                            ComServerMod.LOGGER.info("[PaleOak] Successfully replaced!");
                        }
                    }
                } else {
                    ComServerMod.LOGGER.info("[PaleOak] Biome not hot enough (threshold: 1.5).");
                }
            }
        }
    }
}
