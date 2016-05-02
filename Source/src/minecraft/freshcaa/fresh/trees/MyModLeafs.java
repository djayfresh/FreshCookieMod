package freshcaa.fresh.trees;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.minecraft.world.ColorizerLeaves;

public class MyModLeafs extends BlockLeaves
{
	public static final String[] LEAF_TYPES = new String[] {"pecan", "macadamia"};
	public static final String[][] LEAF_TEXTURES = new String[][] {{"leaves_pecan_opaque", "leaves_macadamia_opaque"}, {"leaves_pecan", "leaves_macadamia"}};
	private Icon[][] iconArray = new Icon[2][];
    private int iconType;
	
    public MyModLeafs(int par1)
    {
    	super(par1);
        this.setTickRandomly(true);
        setHardness(0.01f);
        setStepSound(soundGrassFootstep);
    }
    
    public int quantityDropped(Random par1Random)
    {
        return par1Random.nextInt(15) <= 3 ? 1 : 0;
    }
    
    public int idDropped(int par1, Random random, int par3)
    {
    	if(blockID == CookieMod.pecanLeaf.blockID)
    		return random.nextInt(10) <= 4 ? CookieMod.pecanSapling.blockID : CookieMod.pecan.itemID;
    	if(blockID == CookieMod.macadamiaLeaf.blockID)
    		return random.nextInt(10) <= 4 ? CookieMod.macadamiaSapling.blockID : CookieMod.whiteMacadamia.itemID;
    	
        return Block.sapling.blockID;
    }
    
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
    	if(CookieMod.pecanLeaf.blockID == blockID)
    	{
    		return iconArray[iconType][0];
    	}
    	else if(CookieMod.macadamiaLeaf.blockID == blockID)
    	{
    		return iconArray[iconType][1];
    	}
    	
        return (par2 & 2) == 1 ? this.iconArray[this.iconType][1] : this.iconArray[this.iconType][0];
    }
    
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    { 
        double d0 = 0.5D;
        double d1 = 1.0D;
        return ColorizerFoliage.getFoliageColor(d0, d1);
    }

    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
 
        if (CookieMod.pecanLeaf.blockID == blockID)
        {
            return ColorizerLeaves.getFoliageColorPecan();
        }
        else if (CookieMod.macadamiaLeaf.blockID == blockID)
        {
            return ColorizerLeaves.getFoliageColorMacadamia();
        }
        else
        {
            int i1 = 0;
            int j1 = 0;
            int k1 = 0;

            for (int l1 = -1; l1 <= 1; ++l1)
            {
                for (int i2 = -1; i2 <= 1; ++i2)
                {
                    int j2 = par1IBlockAccess.getBiomeGenForCoords(par2 + i2, par4 + l1).getBiomeFoliageColor();
                    i1 += (j2 & 16711680) >> 16;
                    j1 += (j2 & 65280) >> 8;
                    k1 += j2 & 255;
                }
            }

            return (i1 / 9 & 255) << 16 | (j1 / 9 & 255) << 8 | k1 / 9 & 255;
        }
    }
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
    	super.updateTick(par1World, par2, par3, par4, par5Random);
    	
    }
    
    @SideOnly(Side.CLIENT)
    public void setGraphicsLevel(boolean par1)
    {
        this.graphicsLevel = par1;
        this.iconType = par1 ? 0 : 1;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        for (int i = 0; i < LEAF_TEXTURES.length; ++i)
        {
            this.iconArray [i] = new Icon[LEAF_TEXTURES[i].length];

            for (int j = 0; j < LEAF_TEXTURES[i].length; ++j)
            {
                this.iconArray[i][j] = par1IconRegister.registerIcon(CookieMod.modid + ":" + LEAF_TEXTURES[i][j]);
            }
        }
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return !this.graphicsLevel;
    }
    
    @Override
    public boolean isShearable(ItemStack item, World world, int x, int y, int z)
    {
        return true;
    }

    @Override
    public boolean isLeaves(World world, int x, int y, int z)
    {
        return true;
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return !this.graphicsLevel && i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
}
