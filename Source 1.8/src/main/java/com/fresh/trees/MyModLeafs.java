package com.fresh.trees;

import java.util.List;
import java.util.Random;

import javax.swing.Icon;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.fresh.cookies.CookieMod;
import com.minecraft.world.ColorizerLeaves;

public class MyModLeafs extends BlockLeaves
{
	public static final String[] LEAF_TYPES = new String[] {"pecan", "macadamia"};
	public static final String[][] LEAF_TEXTURES = new String[][] {{"leaves_pecan", "leaves_macadamia"}, {"leaves_pecan_opaque", "leaves_macadamia_opaque"}};
	private Icon[][] iconArray = new Icon[2][];
    private int iconType;
	
    public MyModLeafs(int par1)
    {
        this.setTickRandomly(true);
        setHardness(0.01f);
        setStepSound(soundTypeGrass);
    }
    
    public int quantityDropped(Random par1Random)
    {
        return par1Random.nextInt(15) <= 3 ? 1 : 0;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
    	if(this == CookieMod.pecanLeaf)
    		return rand.nextInt(10) <= 4 ? Item.getItemFromBlock(CookieMod.pecanSapling) : CookieMod.pecan;
    	if(this == CookieMod.macadamiaLeaf)
    		return rand.nextInt(10) <= 4 ? Item.getItemFromBlock(CookieMod.macadamiaSapling) : CookieMod.whiteMacadamia;
    	
        return Item.getItemFromBlock(Blocks.sapling);
    }
    
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
    	if(CookieMod.pecanLeaf == this)
    	{
    		return iconArray[iconType][0];
    	}
    	else if(CookieMod.macadamiaLeaf == this)
    	{
    		return iconArray[iconType][1];
    	}
        return (par2 & 3) == 1 ? this.iconArray[this.iconType][1] : ((par2 & 3) == 3 ? this.iconArray[this.iconType][3] : ((par2 & 3) == 2 ? this.iconArray[this.iconType][2] : this.iconArray[this.iconType][0]));
    }
    
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    { 
        return ColorizerLeaves.getFoliageColor();
    }

    @SideOnly(Side.CLIENT)
    public int getRenderColor(int par1)
    {
        return CookieMod.macadamiaLeaf == this ? ColorizerLeaves.getFoliageColorMacadamia() : ColorizerLeaves.getFoliageColorPecan();
    }

    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        if (CookieMod.pecanLeaf == this)
        {
            return ColorizerLeaves.getFoliageColorPecan();
        }
        else if (CookieMod.macadamiaLeaf == this)
        {
            return ColorizerLeaves.getFoliageColorMacadamia();
        }
        else
        {
            return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons()
    {
        for (int i = 0; i < LEAF_TEXTURES.length; ++i)
        {
            this.iconArray [i] = new Icon[LEAF_TEXTURES[i].length];

            for (int j = 0; j < LEAF_TEXTURES[i].length; ++j)
            {
                //this.iconArray[i][j] = par1IconRegister.registerIcon(CookieMod.modid + ":" + LEAF_TEXTURES[i][j]);
            }
        }
    }
    @Override
    protected int getSaplingDropChance(IBlockState state)
    {
        return 10;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        IBlockState state = world.getBlockState(pos);
        return new java.util.ArrayList(java.util.Arrays.asList(new ItemStack(this, 1, ((BlockPlanks.EnumType)state.getValue(BlockNewLeaf.VARIANT)).getMetadata() - 4)));
    }

    
	@Override
	public BlockPlanks.EnumType getWoodType(int meta)
    {
        return BlockPlanks.EnumType.byMetadata((meta & 3) + 4);
    }
}
