package freshcaa.minecraft.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;

public class PeanutPlant extends BlockCrops
{
	@SideOnly(Side.CLIENT)
	private Icon[] iconArray;

	private int cropSize; //1 based, number of segments 
	public PeanutPlant(int par1)
	{
		super(par1);
		setUnlocalizedName("Peanut Plant");
		cropSize = 4;
		
        this.setBlockBounds(0, 0, 0, 1.0f, 0.1f, 1.0f);
		// TODO Auto-generated constructor stub
	}

	@SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int metadata)
    {
		if (metadata < 0 || metadata > cropSize)
        {
            metadata = cropSize;
        }
		
//		float sizeOffset = metadata / (cropSize * 1.0f);
//		if(sizeOffset < 0.1f)
//			sizeOffset = 0.1f;
//
//		System.out.println(String.format("Peanut Size: %1$.3f, meta: %2$d", sizeOffset, metadata));
//		
//		float f = 0.5f;
//        this.setBlockBounds(0, 0, 0, 1.0f, sizeOffset, 1.0f);
        return this.iconArray[metadata];
    }

    /**
     * Generate a seed ItemStack for this crop.
     */
    protected int getSeedItem()
    {
        return ItemLoader.peanutSeeds.itemID;
    }

    /**
     * Generate a crop produce ItemStack for this crop.
     */
    protected int getCropItem()
    {
        return ItemLoader.peanutSeeds.itemID;
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return this.getSeedItem();
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.getSeedItem();
    }
    
    @Override 
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = super.getBlockDropped(world, x, y, z, metadata, fortune);

        if (metadata >= 4)
        {
            for (int n = 0; n < 4 + fortune; n++)
            {
                if (world.rand.nextInt(15) <= metadata)
                {
                    ret.add(new ItemStack(this.getSeedItem(), 1, 0));
                }
            }
        }

        return ret;
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[5];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon(CookieMod.modid + ":PeanutPlant" + "_" + i);
        }
    }
}
