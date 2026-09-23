package com.djayfresh.freshcaa.item;

import com.djayfresh.freshcaa.block.WorkPostBlock;
import com.djayfresh.freshcaa.entity.FactoryWorker;
import com.djayfresh.freshcaa.entity.task.WorkerTransfers;
import com.djayfresh.freshcaa.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Clipboard records blocks for a worker: right-click containers, machines or a Work Post to note them down, then
 * right-click a hired worker to hand the list over. Sneak-right-click in the air to wipe it.
 */
public class ClipboardItem extends Item {
    public static final int MAX_POSITIONS = 8;

    public ClipboardItem(Item.Properties properties) {
        super(properties);
    }

    public static List<BlockPos> positions(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.POSITIONS.get(), List.of());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        boolean recordable = state.getBlock() instanceof WorkPostBlock || WorkerTransfers.isContainer(level, pos);
        if (!recordable) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        List<BlockPos> positions = new ArrayList<>(positions(stack));
        if (positions.contains(pos)) {
            player.sendOverlayMessage(Component.translatable("freshcaa.clipboard.already", FactoryWorker.describe(level, pos)));
            return InteractionResult.SUCCESS;
        }
        if (positions.size() >= MAX_POSITIONS) {
            player.sendOverlayMessage(Component.translatable("freshcaa.clipboard.full", MAX_POSITIONS));
            return InteractionResult.SUCCESS;
        }
        positions.add(pos.immutable());
        stack.set(ModDataComponents.POSITIONS.get(), List.copyOf(positions));
        player.sendOverlayMessage(Component.translatable("freshcaa.clipboard.recorded", FactoryWorker.describe(level, pos), positions.size(), MAX_POSITIONS));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() || positions(stack).isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            stack.remove(ModDataComponents.POSITIONS.get());
            player.sendOverlayMessage(Component.translatable("freshcaa.clipboard.cleared"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof FactoryWorker worker) || !worker.isHired()) {
            return InteractionResult.PASS;
        }
        List<BlockPos> positions = positions(stack);
        if (positions.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!player.level().isClientSide()) {
            worker.applyClipboard(positions);
            worker.openTaskScreen(player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        List<BlockPos> positions = positions(stack);
        if (positions.isEmpty()) {
            lines.accept(Component.translatable("freshcaa.clipboard.empty").withStyle(ChatFormatting.GRAY));
            return;
        }
        int index = 1;
        for (BlockPos pos : positions) {
            lines.accept(Component.translatable("freshcaa.clipboard.entry", index++, pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
        }
    }
}
