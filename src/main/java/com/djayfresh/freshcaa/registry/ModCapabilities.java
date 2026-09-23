package com.djayfresh.freshcaa.registry;

import com.djayfresh.freshcaa.block.entity.SunDryingTableBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

/** Item-handler capabilities: the Sun Drying Table (sided, lens slots hidden) and the worker's carry inventory. */
public final class ModCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.Item.BLOCK,
                (level, pos, state, blockEntity, side) -> blockEntity instanceof SunDryingTableBlockEntity table ? new WorldlyContainerWrapper(table, side) : null,
                ModBlocks.SUN_DRYING_TABLE.get());
        event.registerEntity(Capabilities.Item.ENTITY, ModEntities.FACTORY_WORKER.get(),
                (worker, context) -> VanillaContainerWrapper.of(worker.getCarry()));
    }

    private ModCapabilities() {}
}
