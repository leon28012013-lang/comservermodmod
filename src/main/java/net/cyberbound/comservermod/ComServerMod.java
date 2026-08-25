package net.cyberbound.comservermod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ComServerMod.MODID)
public class ComServerMod {
    public static final String MODID = "comservermod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ComServerMod(IEventBus modEventBus) {
        ModCreativeTabs.register(modEventBus);
    }
}
