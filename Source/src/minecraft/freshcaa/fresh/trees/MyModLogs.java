package freshcaa.fresh.trees;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;

public class MyModLogs extends BlockLog
{
	public static final String[] woodType = new String[] { "pecan", "macadamia" };
	private Icon tree_Top[];
	private Icon tree_Side[];

	public MyModLogs(int par1)
	{
		super(par1);
		setHardness(2.0f);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return blockID;
	}

	@SideOnly(Side.CLIENT)
	protected Icon getSideIcon(int par1)
	{
		return blockID == ItemLoader.pecanLog.blockID? tree_Side[0] : tree_Side[1];
	}

	@SideOnly(Side.CLIENT)
	protected Icon getEndIcon(int par1)
	{
		return blockID == ItemLoader.pecanLog.blockID? tree_Top[0] : tree_Top[1];
	}

	@SideOnly(Side.CLIENT)
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs,
			List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));//the last value represends the index into woodType
		par3List.add(new ItemStack(par1, 1, 1));//the last value represends the index into woodType
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		this.tree_Side = new Icon[woodType.length];
		this.tree_Top = new Icon[woodType.length];

		for (int i = 0; i < this.tree_Side.length; ++i)
		{
			tree_Side[i] = iconRegister.registerIcon(CookieMod.modid + ":"
					+ "log_" + woodType[i]);
			tree_Top[i] = iconRegister.registerIcon(CookieMod.modid + ":"
					+ "log_" + woodType[i] + "_top");
		}
		//tree_Top = iconRegister.registerIcon("djf:textures/blocks/PeacanLog_Top");
		//tree_Side = iconRegister.registerIcon("djf:textures/blocks/PeacanLog_Side");
	}
}
