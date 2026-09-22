package com.fresh.trees;

import java.util.List;
import java.util.Random;

import javax.swing.Icon;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.fresh.cookies.CookieMod;

public class MyModLogs extends BlockLog
{
	public static final String[] woodType = new String[] { "pecan", "macadamia" };
	private Icon tree_Top[];
	private Icon tree_Side[];

	public MyModLogs(int par1)
	{
		setHardness(1.5f);
	}

	@SideOnly(Side.CLIENT)
	protected Icon getSideIcon(int par1)
	{
		return this == CookieMod.pecanLog? tree_Side[0] : tree_Side[1];
	}

	@SideOnly(Side.CLIENT)
	protected Icon getEndIcon(int par1)
	{
		return this == CookieMod.pecanLog? tree_Top[0] : tree_Top[1];
	}

	@SideOnly(Side.CLIENT)
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(itemIn, 1, 0));//the last value represends the index into woodType
        list.add(new ItemStack(itemIn, 1, 1));//the last value represends the index into woodType
    }

	public void registerIcons()
	{
		this.tree_Side = new Icon[woodType.length];
		this.tree_Top = new Icon[woodType.length];

		for (int i = 0; i < this.tree_Side.length; ++i)
		{
			//tree_Side[i] = iconRegister.registerIcon(CookieMod.modid + ":"
			//		+ "log_" + woodType[i]);
			//tree_Top[i] = iconRegister.registerIcon(CookieMod.modid + ":"
			//		+ "log_" + woodType[i] + "_top");
		}
		//tree_Top = iconRegister.registerIcon("djf:textures/blocks/PeacanLog_Top");
		//tree_Side = iconRegister.registerIcon("djf:textures/blocks/PeacanLog_Side");
	}
}
