package com.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Icon;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.fresh.cookies.CookieMod;

public class PeanutPlant extends BlockCrops
{
	@SideOnly(Side.CLIENT)
	private Icon[] iconArray;

	public PeanutPlant(int par1)
	{
		setUnlocalizedName("Peanut Plant");
		float f = 0.5f;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
		// TODO Auto-generated constructor stub
	}

	@SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
		if (par2 < 0 || par2 > 4)
        {
            par2 = 4;
        }

        return this.iconArray[par2];
    }

    /**
     * Generate a seed ItemStack for this crop.
     */
    protected Item getSeedItem()
    {
        return CookieMod.peanutSeeds;
    }

    /**
     * Generate a crop produce ItemStack for this crop.
     */
    protected Item getCropItem()
    {
        return CookieMod.peanutSeeds;
    }
    
    public Item getItemDropped(IBlockState par1, Random par2Random, int par3)
    {
        return this.getSeedItem();
    }
    
    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param state Current state
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        Random rand = world instanceof World ? ((World)world).rand : RANDOM;

        int count = quantityDropped(state, fortune, rand);
        for(int i = 0; i < count; i++)
        {
            Item item = this.getItemDropped(state, rand, fortune);
            if (item != null)
            {
                ret.add(new ItemStack(item, 1, this.damageDropped(state)));
            }
        }
        return ret;
    }

    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons()
    {
        this.iconArray = new Icon[5];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            //this.iconArray[i] = par1IconRegister.registerIcon(CookieMod.modid + ":PeanutPlant" + "_" + i);
        }
    }
}
