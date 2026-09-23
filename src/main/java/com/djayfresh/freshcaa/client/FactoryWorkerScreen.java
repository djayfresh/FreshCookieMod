package com.djayfresh.freshcaa.client;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.entity.FactoryWorker;
import com.djayfresh.freshcaa.entity.task.BindingSlot;
import com.djayfresh.freshcaa.entity.task.TaskStatus;
import com.djayfresh.freshcaa.entity.task.TaskType;
import com.djayfresh.freshcaa.entity.task.WorkerTask;
import com.djayfresh.freshcaa.menu.FactoryWorkerMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Task screen (176x222 sheet). Top half: task buttons in a column on the left (the current one outlined in gold), the
 * task's binding rows on the right (label with N / X buttons, then the bound block's icon and coordinates; hover for the
 * full name). A full-width status line under a divider. Then the carry slots, Dismiss, the filter slot, and the player
 * inventory. Every coordinate here must match {@code tools/gui/factory_worker_assets.py}.
 */
public class FactoryWorkerScreen extends AbstractContainerScreen<FactoryWorkerMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(FreshCookies.MODID, "textures/gui/container/factory_worker.png");
    private static final int SHEET_SIZE = 256;
    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 222;

    // Task column: five buttons, 14 high on a 15 px pitch, from y 17 to 91.
    private static final int TASK_X = 8;
    private static final int TASK_Y = 17;
    private static final int TASK_W = 52;
    private static final int TASK_H = 14;
    private static final int TASK_PITCH = 15;

    // Binding panel: up to three rows of 26 px, from y 17 to 95. Label line on top, icon + coordinates below.
    private static final int BIND_X = 67;
    private static final int BIND_Y = 17;
    private static final int BIND_ROW_H = 26;
    private static final int BIND_BUTTON_W = 12;
    private static final int BIND_BUTTON_H = 10;
    private static final int BIND_RIGHT = 168;

    private static final int STATUS_Y = 99;
    private static final int STATUS_W = 160;

    private static final int DISMISS_X = 90;
    private static final int DISMISS_Y = 108;
    private static final int DISMISS_W = 46;
    private static final int DISMISS_H = 16;

    private static final int TEXT_COLOR = 0xFF404040;
    private static final int MUTED_COLOR = 0xFF808080;
    private static final int SELECTED_OUTLINE = 0xFFFFD24A;

    private final List<Button> taskButtons = new ArrayList<>();
    private final List<Button> nearestButtons = new ArrayList<>();
    private final List<Button> clearButtons = new ArrayList<>();
    private @Nullable Button dismissButton;

    public FactoryWorkerScreen(FactoryWorkerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        this.taskButtons.clear();
        this.nearestButtons.clear();
        this.clearButtons.clear();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

        for (TaskType type : TaskType.values()) {
            Button button = Button.builder(Component.translatable(type.translationKey()), b -> this.send(FactoryWorkerMenu.BUTTON_TASK_BASE + type.ordinal()))
                    .pos(this.leftPos + TASK_X, this.topPos + TASK_Y + type.ordinal() * TASK_PITCH)
                    .size(TASK_W, TASK_H)
                    .build();
            if (!type.isImplemented()) {
                button.setTooltip(Tooltip.create(Component.translatable("freshcaa.worker.task.coming_soon")));
            }
            this.taskButtons.add(this.addRenderableWidget(button));
        }

        for (int row = 0; row < BindingSlot.values().length; row++) {
            int y = this.topPos + BIND_Y + row * BIND_ROW_H;
            int rowIndex = row;
            this.nearestButtons.add(this.addRenderableWidget(Button.builder(Component.translatable("freshcaa.worker.nearest"),
                            b -> this.sendBinding(FactoryWorkerMenu.BUTTON_NEAREST_BASE, rowIndex))
                    .pos(this.leftPos + BIND_RIGHT - BIND_BUTTON_W * 2 - 1, y)
                    .size(BIND_BUTTON_W, BIND_BUTTON_H)
                    .tooltip(Tooltip.create(Component.translatable("freshcaa.worker.nearest.tooltip")))
                    .build()));
            this.clearButtons.add(this.addRenderableWidget(Button.builder(Component.translatable("freshcaa.worker.clear"),
                            b -> this.sendBinding(FactoryWorkerMenu.BUTTON_CLEAR_BASE, rowIndex))
                    .pos(this.leftPos + BIND_RIGHT - BIND_BUTTON_W, y)
                    .size(BIND_BUTTON_W, BIND_BUTTON_H)
                    .tooltip(Tooltip.create(Component.translatable("freshcaa.worker.clear.tooltip")))
                    .build()));
        }

        this.dismissButton = this.addRenderableWidget(Button.builder(Component.translatable("freshcaa.worker.dismiss"), b -> this.send(FactoryWorkerMenu.BUTTON_DISMISS))
                .pos(this.leftPos + DISMISS_X, this.topPos + DISMISS_Y)
                .size(DISMISS_W, DISMISS_H)
                .tooltip(Tooltip.create(Component.translatable("freshcaa.worker.dismiss.tooltip")))
                .build());
        this.refreshButtons();
    }

    private void send(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private void sendBinding(int base, int row) {
        List<BindingSlot> slots = this.currentTask().type().bindings();
        if (row < slots.size()) {
            this.send(base + slots.get(row).ordinal());
        }
    }

    private WorkerTask currentTask() {
        FactoryWorker worker = this.menu.getWorker();
        return worker != null ? worker.getTask() : WorkerTask.EMPTY;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.refreshButtons();
    }

    private void refreshButtons() {
        WorkerTask task = this.currentTask();
        for (TaskType type : TaskType.values()) {
            // The current task stays clickable (it just re-selects itself) so it doesn't look disabled; a gold
            // outline marks it instead.
            this.taskButtons.get(type.ordinal()).active = type.isImplemented();
        }
        List<BindingSlot> slots = task.type().bindings();
        for (int row = 0; row < this.nearestButtons.size(); row++) {
            boolean shown = row < slots.size();
            this.nearestButtons.get(row).visible = shown;
            this.clearButtons.get(row).visible = shown;
            this.clearButtons.get(row).active = shown && task.isBound(slots.get(row));
        }
        if (this.dismissButton != null) {
            this.dismissButton.active = this.menu.getWorker() != null;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, SHEET_SIZE, SHEET_SIZE);
    }

    /** Drawn after the widgets and translated to the panel's top-left corner. */
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        FactoryWorker worker = this.menu.getWorker();
        if (worker == null) {
            return;
        }
        WorkerTask task = worker.getTask();

        int selected = task.type().ordinal();
        graphics.outline(TASK_X - 1, TASK_Y + selected * TASK_PITCH - 1, TASK_W + 2, TASK_H + 2, SELECTED_OUTLINE);

        List<BindingSlot> slots = task.type().bindings();
        if (slots.isEmpty()) {
            int lineY = BIND_Y + 2;
            for (FormattedCharSequence line : this.font.split(Component.translatable("freshcaa.worker.no_task"), BIND_RIGHT - BIND_X)) {
                graphics.text(this.font, line, BIND_X, lineY, MUTED_COLOR, false);
                lineY += 10;
            }
        }
        int labelWidth = BIND_RIGHT - BIND_BUTTON_W * 2 - 3 - BIND_X;
        for (int row = 0; row < slots.size(); row++) {
            BindingSlot slot = slots.get(row);
            int y = BIND_Y + row * BIND_ROW_H;
            graphics.text(this.font, this.clip(Component.translatable(slot.translationKey()), labelWidth), BIND_X, y + 1, TEXT_COLOR, false);
            BlockPos pos = task.binding(slot).orElse(null);
            if (pos == null) {
                graphics.text(this.font, Component.translatable("freshcaa.worker.unbound"), BIND_X, y + 14, MUTED_COLOR, false);
                continue;
            }
            ItemStack icon = worker.level().getBlockState(pos).getBlock().asItem().getDefaultInstance();
            graphics.item(icon, BIND_X, y + 10);
            Component coords = Component.translatable("freshcaa.worker.coords", pos.getX(), pos.getY(), pos.getZ());
            graphics.text(this.font, this.clip(coords, BIND_RIGHT - BIND_X - 18), BIND_X + 18, y + 14, TEXT_COLOR, false);
        }

        TaskStatus status = worker.getStatus();
        Component statusLine = Component.translatable(status.translationKey());
        Component detail = worker.getStatusDetail();
        if (detail != null) {
            statusLine = Component.empty().append(statusLine).append(": ").append(detail);
        }
        graphics.text(this.font, this.clip(statusLine, STATUS_W), 8, STATUS_Y, status.color(), false);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        FactoryWorker worker = this.menu.getWorker();
        if (worker == null) {
            return;
        }
        int x = mouseX - this.leftPos;
        int y = mouseY - this.topPos;

        // Full block name and position for a bound row.
        WorkerTask task = worker.getTask();
        List<BindingSlot> slots = task.type().bindings();
        for (int row = 0; row < slots.size(); row++) {
            int top = BIND_Y + row * BIND_ROW_H + 10;
            BlockPos pos = task.binding(slots.get(row)).orElse(null);
            if (pos != null && x >= BIND_X && x < BIND_RIGHT && y >= top && y < top + 16) {
                graphics.setTooltipForNextFrame(this.font, FactoryWorker.describe(worker.level(), pos), mouseX, mouseY);
                return;
            }
        }

        // The whole status line, in case it was clipped.
        if (x >= 8 && x < 8 + STATUS_W && y >= STATUS_Y - 1 && y < STATUS_Y + 9 && worker.getStatusDetail() != null) {
            Component line = Component.empty().append(Component.translatable(worker.getStatus().translationKey())).append(": ").append(worker.getStatusDetail());
            graphics.setTooltipForNextFrame(this.font, line, mouseX, mouseY);
            return;
        }

        // Explain the empty filter slot.
        if (this.hoveredSlot != null && this.hoveredSlot.index == FactoryWorker.CARRY_SLOTS && !this.hoveredSlot.hasItem()
                && this.menu.getCarried().isEmpty()) {
            graphics.setTooltipForNextFrame(this.font, Component.translatable("freshcaa.worker.filter.tooltip"), mouseX, mouseY);
        }
    }

    /** Trims a component to the given pixel width and converts it for drawing. */
    private FormattedCharSequence clip(Component text, int width) {
        return Language.getInstance().getVisualOrder(this.font.substrByWidth(text, width));
    }
}
