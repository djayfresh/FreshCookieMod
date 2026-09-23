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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Task screen layout (176x222 sheet): task buttons down the left, the chosen task's binding rows on the right with
 * Nearest / Clear buttons, a status line, the four carry slots with the filter slot, then the player inventory.
 */
public class FactoryWorkerScreen extends AbstractContainerScreen<FactoryWorkerMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(FreshCookies.MODID, "textures/gui/container/factory_worker.png");
    private static final int SHEET_SIZE = 256;
    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 222;

    private static final int TASK_X = 8;
    private static final int TASK_Y = 17;
    private static final int TASK_W = 52;
    private static final int TASK_H = 16;

    private static final int BIND_X = 66;
    private static final int BIND_Y = 17;
    private static final int BIND_ROW_H = 26;
    private static final int BIND_BUTTON_W = 14;
    private static final int BIND_BUTTON_H = 12;

    private static final int STATUS_Y = 96;
    private static final int TEXT_COLOR = 0xFF404040;
    private static final int MUTED_COLOR = 0xFF808080;

    private final List<Button> taskButtons = new ArrayList<>();
    private final List<Button> nearestButtons = new ArrayList<>();
    private final List<Button> clearButtons = new ArrayList<>();
    private Button dismissButton;

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
            int y = this.topPos + TASK_Y + type.ordinal() * TASK_H;
            Button button = Button.builder(Component.translatable(type.translationKey()), b -> this.send(FactoryWorkerMenu.BUTTON_TASK_BASE + type.ordinal()))
                    .pos(this.leftPos + TASK_X, y)
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
            Button nearest = Button.builder(Component.translatable("freshcaa.worker.nearest"), b -> this.sendBinding(FactoryWorkerMenu.BUTTON_NEAREST_BASE, rowIndex))
                    .pos(this.leftPos + this.imageWidth - 8 - BIND_BUTTON_W * 2 - 2, y)
                    .size(BIND_BUTTON_W, BIND_BUTTON_H)
                    .tooltip(Tooltip.create(Component.translatable("freshcaa.worker.nearest.tooltip")))
                    .build();
            Button clear = Button.builder(Component.translatable("freshcaa.worker.clear"), b -> this.sendBinding(FactoryWorkerMenu.BUTTON_CLEAR_BASE, rowIndex))
                    .pos(this.leftPos + this.imageWidth - 8 - BIND_BUTTON_W, y)
                    .size(BIND_BUTTON_W, BIND_BUTTON_H)
                    .tooltip(Tooltip.create(Component.translatable("freshcaa.worker.clear.tooltip")))
                    .build();
            this.nearestButtons.add(this.addRenderableWidget(nearest));
            this.clearButtons.add(this.addRenderableWidget(clear));
        }

        this.dismissButton = this.addRenderableWidget(Button.builder(Component.translatable("freshcaa.worker.dismiss"), b -> this.send(FactoryWorkerMenu.BUTTON_DISMISS))
                .pos(this.leftPos + this.imageWidth - 8 - 44, this.topPos + STATUS_Y - 2)
                .size(44, 12)
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
        FactoryWorker worker = this.menu.getWorker();
        if (worker == null) {
            return;
        }
        List<BindingSlot> slots = worker.getTask().type().bindings();
        if (row < slots.size()) {
            this.send(base + slots.get(row).ordinal());
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.refreshButtons();
    }

    private void refreshButtons() {
        FactoryWorker worker = this.menu.getWorker();
        WorkerTask task = worker != null ? worker.getTask() : WorkerTask.EMPTY;
        for (TaskType type : TaskType.values()) {
            Button button = this.taskButtons.get(type.ordinal());
            button.active = type.isImplemented() && task.type() != type;
        }
        List<BindingSlot> slots = task.type().bindings();
        for (int row = 0; row < this.nearestButtons.size(); row++) {
            boolean shown = row < slots.size();
            this.nearestButtons.get(row).visible = shown;
            this.clearButtons.get(row).visible = shown;
            this.clearButtons.get(row).active = shown && task.isBound(slots.get(row));
        }
        this.dismissButton.active = worker != null;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, SHEET_SIZE, SHEET_SIZE);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        FactoryWorker worker = this.menu.getWorker();
        if (worker == null) {
            return;
        }
        WorkerTask task = worker.getTask();
        List<BindingSlot> slots = task.type().bindings();
        int textWidth = this.imageWidth - 8 - BIND_BUTTON_W * 2 - 2 - BIND_X - 2;
        for (int row = 0; row < slots.size(); row++) {
            BindingSlot slot = slots.get(row);
            int y = BIND_Y + row * BIND_ROW_H;
            graphics.text(this.font, Component.translatable(slot.translationKey()), BIND_X, y + 2, TEXT_COLOR, false);
            BlockPos pos = task.binding(slot).orElse(null);
            Component detail = pos == null
                    ? Component.translatable("freshcaa.worker.unbound")
                    : FactoryWorker.describe(worker.level(), pos);
            graphics.text(this.font, clip(detail, textWidth + BIND_BUTTON_W * 2 + 2), BIND_X, y + 13, pos == null ? MUTED_COLOR : TEXT_COLOR, false);
        }
        if (slots.isEmpty()) {
            graphics.text(this.font, Component.translatable("freshcaa.worker.no_task"), BIND_X, BIND_Y + 2, MUTED_COLOR, false);
        }

        TaskStatus status = worker.getStatus();
        Component statusLine = Component.translatable(status.translationKey());
        Component statusDetail = worker.getStatusDetail();
        if (statusDetail != null) {
            statusLine = Component.empty().append(statusLine).append(": ").append(statusDetail);
        }
        graphics.text(this.font, clip(statusLine, this.imageWidth - 16 - 46), 8, STATUS_Y, status.color(), false);
        graphics.text(this.font, Component.translatable("freshcaa.worker.filter"), FactoryWorkerMenu.FILTER_X - 2 - this.font.width(Component.translatable("freshcaa.worker.filter")), FactoryWorkerMenu.FILTER_Y + 5, MUTED_COLOR, false);
    }

    /** Trims a component to the given pixel width and converts it for drawing. */
    private FormattedCharSequence clip(Component text, int width) {
        return Language.getInstance().getVisualOrder(this.font.substrByWidth(text, width));
    }
}
