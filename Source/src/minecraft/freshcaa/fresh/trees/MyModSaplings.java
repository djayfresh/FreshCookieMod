package freshcaa.fresh.trees;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import freshcaa.fresh.cookies.CookieMod;
import freshcaa.fresh.load.ItemLoader;
import freshcaa.minecraft.world.MacadamiaTreeWorldGenerator;
import freshcaa.minecraft.world.PecanTreeWorldGenerator;

public class MyModSaplings extends BlockSapling
{
	public static final String[] WOOD_TYPES = new String[] {"pecan", "macadamia"};
    @SideOnly(Side.CLIENT)
    private Icon[] saplingIcon;

	public MyModSaplings(int par1)
	{
		super(par1);
		float f = 0.4F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F,
				0.5F + f);
		setHardness(0.0f);
	}

	@SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
        return ItemLoader.pecanSapling.blockID == blockID? saplingIcon[0] : saplingIcon[1];
    }

	public void growTree(World par1World, int par2, int par3, int par4,
			Random par5Random)
	{
		if (!TerrainGen
				.saplingGrowTree(par1World, par5Random, par2, par3, par4))
			return;

		int l = par1World.getBlockMetadata(par2, par3, par4) & 3;
		Object object = null;
		int i1 = 0;
		int j1 = 0;
		boolean flag = false;

		if (l == 1)
		{
			object = new WorldGenTaiga2(true);
		} else if (l == 2)
		{
			object = new WorldGenForest(true);
		} else if (l == 3)
		{
			for (i1 = 0; i1 >= -1; --i1)
			{
				for (j1 = 0; j1 >= -1; --j1)
				{
					if (this.isSameSapling(par1World, par2 + i1, par3, par4
							+ j1, 3)
							&& this.isSameSapling(par1World, par2 + i1 + 1,
									par3, par4 + j1, 3)
							&& this.isSameSapling(par1World, par2 + i1, par3,
									par4 + j1 + 1, 3)
							&& this.isSameSapling(par1World, par2 + i1 + 1,
									par3, par4 + j1 + 1, 3))
					{
						object = new WorldGenHugeTrees(true,
								10 + par5Random.nextInt(20), 3, 3);
						flag = true;
						break;
					}
				}

				if (object != null)
				{
					break;
				}
			}

			if (object == null)
			{
				j1 = 0;
				i1 = 0;
				//Needs to be My own generator
				object = ItemLoader.pecanSapling.blockID == blockID ? new PecanTreeWorldGenerator(true, 4 + par5Random.nextInt(7), 3,
						3, false) : new MacadamiaTreeWorldGenerator(true, 4 + par5Random.nextInt(7), 3,
								3, false);
			}
		} else
		{
			//Needs to be My own generator
			object = ItemLoader.pecanSapling.blockID == blockID ? new PecanTreeWorldGenerator(true) : new MacadamiaTreeWorldGenerator(true);

			if (par5Random.nextInt(10) == 0)
			{
				object = new WorldGenBigTree(true);
			}
		}

		if (flag)
		{
			par1World.setBlock(par2 + i1, par3, par4 + j1, 0, 0, 4);
			par1World.setBlock(par2 + i1 + 1, par3, par4 + j1, 0, 0, 4);
			par1World.setBlock(par2 + i1, par3, par4 + j1 + 1, 0, 0, 4);
			par1World.setBlock(par2 + i1 + 1, par3, par4 + j1 + 1, 0, 0, 4);
		} else
		{
			par1World.setBlock(par2, par3, par4, 0, 0, 4);
		}

		if (!((WorldGenerator) object).generate(par1World, par5Random, par2
				+ i1, par3, par4 + j1))
		{
			if (flag)
			{
				par1World.setBlock(par2 + i1, par3, par4 + j1, this.blockID, l,
						4);
				par1World.setBlock(par2 + i1 + 1, par3, par4 + j1,
						this.blockID, l, 4);
				par1World.setBlock(par2 + i1, par3, par4 + j1 + 1,
						this.blockID, l, 4);
				par1World.setBlock(par2 + i1 + 1, par3, par4 + j1 + 1,
						this.blockID, l, 4);
			} else
			{
				par1World.setBlock(par2, par3, par4, this.blockID, l, 4);
			}
		}
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
		this.saplingIcon = new Icon[WOOD_TYPES.length];

		for (int i = 0; i < this.saplingIcon.length; ++i)
		{
			this.saplingIcon[i] = par1IconRegister.registerIcon(CookieMod.modid + ":sapling_" + WOOD_TYPES[i]);
		}
	}
}
