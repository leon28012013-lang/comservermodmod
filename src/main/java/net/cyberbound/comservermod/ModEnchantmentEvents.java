package net.cyberbound.comservermod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = ComServerMod.MODID)
public class ModEnchantmentEvents {

    public static final ResourceKey<Enchantment> REINFORCED_BREAKER = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ComServerMod.MODID, "reinforced_breaker")
    );

    /** Verstaerkter Tiefenschiefer steht in keinem mineable-Tag: 82 s von Hand, damit rund 7 s. */
    private static final float SPEED_FACTOR = 12.0F;

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!event.getState().is(Blocks.REINFORCED_DEEPSLATE)) {
            return;
        }
        if (!hasReinforcedBreaker(event.getEntity())) {
            return;
        }
        // getNewSpeed(), nicht getOriginalSpeed(): sonst werden die Boni anderer
        // Listener (z. B. der mining-Perk von Reskillable) verworfen.
        event.setNewSpeed(event.getNewSpeed() * SPEED_FACTOR);
    }

    /**
     * LOWEST, damit jeder Listener, der den Abbau noch abbrechen will (Claim-Schutz,
     * Reskillable), vorher drankommt. Sonst laege das Item schon am Boden, waehrend der
     * Block stehen bleibt -- das waere beliebig oft wiederholbar.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getState().is(Blocks.REINFORCED_DEEPSLATE)) {
            return;
        }
        // Im Kreativmodus droppt in Vanilla nichts, sonst waere das eine freie Quelle.
        if (event.getPlayer().isCreative()) {
            return;
        }
        if (!hasReinforcedBreaker(event.getPlayer())) {
            return;
        }
        if (event.getLevel() instanceof Level level) {
            // popResource statt eigener ItemEntity: prueft isClientSide und die Gameregel
            // doTileDrops und setzt die uebliche Aufhebe-Verzoegerung.
            Block.popResource(level, event.getPos(), new ItemStack(Items.REINFORCED_DEEPSLATE));
        }
    }

    private static boolean hasReinforcedBreaker(Player player) {
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) {
            return false;
        }
        return player.level().registryAccess().registry(Registries.ENCHANTMENT)
                .flatMap(registry -> registry.getHolder(REINFORCED_BREAKER))
                .map(holder -> tool.getEnchantmentLevel(holder) > 0)
                .orElse(false);
    }
}
