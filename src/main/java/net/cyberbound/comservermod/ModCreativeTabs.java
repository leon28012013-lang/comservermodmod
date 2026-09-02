package net.cyberbound.comservermod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ComServerMod.MODID);

    public static final Supplier<CreativeModeTab> COMSERVER_TAB = CREATIVE_MODE_TABS.register("comserver_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.comservermod.comserver_tab"))
                    .icon(() -> new ItemStack(Items.DIAMOND))
                    .displayItems((parameters, output) -> {

                        // ==================== BLÖCKE ====================

                        // VARIANTE A (Automatisch): Fügt JEDEN Block aus ModBlocks automatisch hinzu!
                        ModBlocks.BLOCKS.getEntries().forEach(blockHolder -> {
                            output.accept(blockHolder.get());
                        });

                        // ==================== VERZAUBERUNGEN ====================
                        parameters.holders().lookup(Registries.ENCHANTMENT).ifPresent(registry -> {
                            registry.get(ModEnchantmentEvents.REINFORCED_BREAKER).ifPresent(holder -> {
                                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                                EnchantmentHelper.updateEnchantments(book, mutable -> mutable.set(holder, 1));
                                output.accept(book);
                            });
                        });

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}