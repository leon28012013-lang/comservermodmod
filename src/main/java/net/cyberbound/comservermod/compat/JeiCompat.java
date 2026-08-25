package net.cyberbound.comservermod.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;

import net.cyberbound.comservermod.ComServerMod;
import net.cyberbound.comservermod.ModEnchantmentEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;

/**
 * JEI-Anbindung. Alles, was ein normales Rezept ist, zeigt JEI von allein an --
 * hier stehen nur die Dinge, die JEI nicht sehen kann: Verhalten, das im Code
 * steckt (Reinforced Breaker) und Herkunftshinweise zu Items, die in dieser Welt
 * ohne die Rezepte dieser Mod unerreichbar waeren.
 *
 * Die Klasse wird ausschliesslich vom JEI-Client geladen und niemals auf dem
 * dedizierten Server -- deshalb ist der Zugriff auf Minecraft.getInstance() hier
 * zulaessig.
 */
@JeiPlugin
public class JeiCompat implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(ComServerMod.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Im Code, nicht in einem Rezept: die Verzauberung beschleunigt den Abbau um das
        // Zwoelffache und laesst den Block sich selbst droppen. Abbaubar ist er auch ohne
        // sie -- nur dauert es 82 Sekunden und es faellt nichts heraus.
        registration.addIngredientInfo(
                Blocks.REINFORCED_DEEPSLATE,
                Component.translatable("jei.comservermod.info.reinforced_deepslate"));

        // Die Verzauberung hat bewusst keine Vanilla-Effekte ("effects": {}), also
        // steht im Tooltip nichts darueber, was sie tut. Hier steht es.
        ItemStack book = reinforcedBreakerBook();
        if (!book.isEmpty()) {
            registration.addItemStackInfo(
                    book,
                    Component.translatable("jei.comservermod.info.reinforced_breaker"));
        }

        // Optionale Fremd-Items: nur beschriften, wenn die jeweilige Mod da ist.
        addInfoIfPresent(registration, "minecraft:sulfur", "jei.comservermod.info.sulfur");
        addInfoIfPresent(registration, "minecraft:sulfur_spike", "jei.comservermod.info.sulfur");
        addInfoIfPresent(registration, "minecraft:cinnabar", "jei.comservermod.info.cinnabar");
        addInfoIfPresent(registration, "minecraft:pale_oak_sapling", "jei.comservermod.info.pale_oak_sapling");
        addInfoIfPresent(registration, "irons_spellbooks:lightning_rod", "jei.comservermod.info.lightning_rod");
    }

    private static void addInfoIfPresent(IRecipeRegistration registration, String itemId, String translationKey) {
        ResourceLocation id = ResourceLocation.tryParse(itemId);
        if (id == null) {
            return;
        }
        BuiltInRegistries.ITEM.getOptional(id).ifPresent(item ->
                registration.addItemStackInfo(new ItemStack(item), Component.translatable(translationKey)));
    }

    /**
     * Baut das verzauberte Buch, das auch im Kreativtab und im Rezept steht.
     * Verzauberungen sind seit 1.21 eine Datapack-Registry, es braucht also die
     * Registries der laufenden Verbindung. Ist noch keine da, wird der Eintrag
     * still uebersprungen.
     */
    private static ItemStack reinforcedBreakerBook() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return ItemStack.EMPTY;
        }
        RegistryAccess registries = minecraft.level.registryAccess();
        return registries.registry(Registries.ENCHANTMENT)
                .flatMap(registry -> registry.getHolder(ModEnchantmentEvents.REINFORCED_BREAKER))
                .map(holder -> {
                    ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(holder, 1));
                    return stack;
                })
                .orElse(ItemStack.EMPTY);
    }
}
