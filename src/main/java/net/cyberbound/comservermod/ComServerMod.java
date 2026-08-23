package net.cyberbound.comservermod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(ComServerMod.MODID)
public class ComServerMod {
    public static final String MODID = "comservermod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ComServerMod(IEventBus modEventBus, ModContainer modContainer) {
        ModCreativeTabs.register(modEventBus);
    }
}