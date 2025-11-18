package com.alexthw.ars_ip_compat.registry;

import com.alexthw.ars_ip_compat.item.ImmersiveWarpScroll;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.alexthw.ars_ip_compat.ImmersiveArsP.MODID;

public class ModRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);

    public static void registerRegistries(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    public static DeferredHolder<Item, Item> IMMERSIVE_WARP_SCROLL = ITEMS.register("immersive_warp_scroll", () -> new ImmersiveWarpScroll(false));

    public static DeferredHolder<Item, Item> IMMERSIVE_STABILIZED_WARP_SCROLL = ITEMS.register("immersive_stabilized_warp_scroll", () -> new ImmersiveWarpScroll(true));

}
