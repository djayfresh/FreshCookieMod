package freshcaa.minecraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.container.ContainerSunTable;
import freshcaa.minecraft.tileEntity.TileEntitySunTable;

public class GuiSunTable extends GuiContainer
{
	public static final ResourceLocation texture = new ResourceLocation(CookieMod.modid + ":textures/gui/SuntableGUI.png");
	public TileEntitySunTable sunTableEntity;
	
	public GuiSunTable(InventoryPlayer inventoryPlayer, TileEntitySunTable entity)
	{
		super(new ContainerSunTable(inventoryPlayer, entity));
		sunTableEntity = entity;
		xSize = 176;
		ySize = 166;
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String name = this.sunTableEntity.isInvintoryNameLocalized() ? this.sunTableEntity.getInventoryName() : I18n.getString(this.sunTableEntity.getInventoryName());

		fontRenderer.drawString(name, xSize/2 - fontRenderer.getStringWidth(name)/2, 6, 4210752); //some randome color 4210752
		fontRenderer.drawString(I18n.getString("container.inventory"), 8, ySize-94, 4210752); //some randome color 4210752
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{	
		GL11.glColor4f(1f, 1f, 1f, 1f); //Black
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize); 
		
		if(sunTableEntity.isInSun)
		{
			drawTexturedModalRect(guiLeft + 56, guiTop + 36, 176, 0, 14, 14); //Flame
			drawTexturedModalRect(guiLeft + 54, guiTop + 51, 176, 31, 20, 20); //Sun
		}
		
		int remainingCookTime = sunTableEntity.getCookProgressScaled(24);
		drawTexturedModalRect(guiLeft + 79, guiTop + 34, 176, 14, remainingCookTime + 1, 16);
		
	}

}
