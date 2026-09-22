package com.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Icon;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.fresh.cookies.CookieMod;

public class GrapeVine extends BlockCrops
{
	@SideOnly(Side.CLIENT)
	private Icon[] iconArray;
	private int cropSize; //1 based, number of segments 
	public GrapeVine(int par1)
	{
		setUnlocalizedName("Grape Vine");
		cropSize = 6;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        // TODO Auto-generated constructor stub
	}

	@SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
		if (par2 < 0 || par2 >= cropSize)
        {
            par2 = cropSize-1;
        }

        return this.iconArray[par2];
    }

    /**
     * Generate a seed ItemStack for this crop.
     */
    protected Item getSeedItem()
    {
        return CookieMod.grapeSeeds;
    }

    @SideOnly(Side.CLIENT)
    /**
     * Generate a seed ItemStack for this crop.
     */
    public Item getItem()
    {
        return CookieMod.grapeSeeds;
    }


    /**
     * Generate a crop produce ItemStack for this crop.
     */
    protected Item getCropItem()
    {
        return CookieMod.grapeSeeds;
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

    
    /**
     * Returns the items to drop on destruction.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return this.getSeedItem();
    }
    
    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons()
    {
        this.iconArray = new Icon[cropSize];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            //this.iconArray[i] = par1IconRegister.registerIcon(CookieMod.modid + ":GrapeVine" + "_stage_" + i);
        }
    }
}
