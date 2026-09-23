package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.FreshCookies;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FreshCookies.MODID);

    /** Block positions recorded on a Clipboard, in the order they were clicked. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<BlockPos>>> POSITIONS = DATA_COMPONENTS.registerComponentType("positions",
            builder -> builder.persistent(BlockPos.CODEC.listOf()).networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list())));

    private ModDataComponents() {}
}
