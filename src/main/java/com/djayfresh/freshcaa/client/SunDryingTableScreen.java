package com.djayfresh.freshcaa.client;

import com.djayfresh.freshcaa.FreshCookies;
import com.djayfresh.freshcaa.menu.SunDryingTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * Sun Drying Table GUI. Uses the 256x256 sheet: the 176x166 background at (0,0), the flame at (176,0),
 * the progress arrow at (176,14), the sun at (176,31) and an empty slot frame at (176,52) used for the
 * four upgrade slots.
 */
public class SunDryingTableScreen extends AbstractContainerScreen<SunDryingTableMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(FreshCookies.MODID, "textures/gui/container/sun_drying_table.png");
    private static final int SHEET_SIZE = 256;
    private static final int ARROW_WIDTH = 24;
    private static final int SLOT_FRAME_U = 176;
    private static final int SLOT_FRAME_V = 52;

    public SunDryingTableScreen(SunDryingTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, SHEET_SIZE, SHEET_SIZE);

        for (int[] pos : SunDryingTableMenu.UPGRADE_SLOT_POSITIONS) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + pos[0] - 1, y + pos[1] - 1, SLOT_FRAME_U, SLOT_FRAME_V, 18, 18, SHEET_SIZE, SHEET_SIZE);
        }

        if (this.menu.isInSun()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 56, y + 36, 176.0F, 0.0F, 14, 14, SHEET_SIZE, SHEET_SIZE);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 54, y + 51, 176.0F, 31.0F, 20, 20, SHEET_SIZE, SHEET_SIZE);
        }

        int arrow = Mth.ceil(this.menu.getProgress() * ARROW_WIDTH);
        if (arrow > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + 79, y + 34, 176.0F, 14.0F, arrow, 16, SHEET_SIZE, SHEET_SIZE);
        }
    }
}
