package com.minecraft.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;

import com.fresh.cookies.CookieMod;

public class PecanTreeWorldGenerator extends WorldGenerator
{
	/** The minimum height of a generated tree. */
    private final int minTreeHeight;

    /** True if this tree should grow Vines. */
    private final boolean vinesGrow;

    /** The metadata value of the wood to use in tree generation. */
    private final int metaWood;

    /** The metadata value of the leaves to use in tree generation. */
    private final int metaLeaves;
    
   
	
    public PecanTreeWorldGenerator(boolean par1)
    {
        this(par1, 4, 0, 0, false);
    }

    public PecanTreeWorldGenerator(boolean unknown, int minTreeHeight, int metaWood, int metaLeaves, boolean vinesGrow)
    {
        super(unknown);
        this.minTreeHeight = minTreeHeight;
        this.metaWood = metaWood;
        this.metaLeaves = metaLeaves;
        this.vinesGrow = vinesGrow;
    }
    
    public boolean generate(World par1World, Random par2Random, BlockPos pos)
    {
        int l = par2Random.nextInt(3) + this.minTreeHeight;
        boolean flag = true;
        int par3 = pos.getX();
        int par4 = pos.getY();
        int par5 = pos.getZ();

        if (par4 >= 1 && par4 + l + 1 <= 256)
        {
            int i1;
            byte b0;
            int j1;
            Block block;

            for (i1 = par4; i1 <= par4 + 1 + l; ++i1)
            {
                b0 = 1;

                if (i1 == par4)
                {
                    b0 = 0;
                }

                if (i1 >= par4 + 1 + l - 2)
                {
                    b0 = 2;
                }

                for (int l1 = par3 - b0; l1 <= par3 + b0 && flag; ++l1)
                {
                    for (j1 = par5 - b0; j1 <= par5 + b0 && flag; ++j1)
                    {
                        if (i1 >= 0 && i1 < 256)
                        {
                            block = par1World.getBlockState(pos).getBlock();

                            if (!par1World.isAirBlock(pos) &&
                                !block.isLeaves(par1World, pos) &&
                                 block != Blocks.grass &&
                                 block != Blocks.dirt &&
                                !block.isWood(par1World, pos))
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                Block soil = par1World.getBlockState(pos.add(0, -1, 0)).getBlock();
                boolean isSoil = (soil != null && soil.canSustainPlant(par1World, new BlockPos(par3, par4 - 1, par5), EnumFacing.UP, (IPlantable)Blocks.sapling));

                if (isSoil && par4 < 256 - l - 1)
                {
                    soil.onPlantGrow(par1World, pos.add(0, -1, 0), pos);
                    b0 = 3;
                    byte b1 = 0;
                    int i2;
                    int j2;
                    int k2;

                    for (j1 = par4 - b0 + l; j1 <= par4 + l; ++j1)
                    {
                        int k1 = j1 - (par4 + l);
                        i2 = b1 + 1 - k1 / 2;

                        for (j2 = par3 - i2; j2 <= par3 + i2; ++j2)
                        {
                            k2 = j2 - par3;

                            for (int l2 = par5 - i2; l2 <= par5 + i2; ++l2)
                            {
                                int i3 = l2 - par5;

                                if (Math.abs(k2) != i2 || Math.abs(i3) != i2 || par2Random.nextInt(2) != 0 && k1 != 0)
                                {
                                    Block block2 = par1World.getBlockState(new BlockPos(j2, j1, l2)).getBlock();

                                    if (block2 == null || block2.canBeReplacedByLeaves(par1World, new BlockPos(j2, j1, l2)))
                                    {
                                    	func_175905_a(par1World, new BlockPos(j2, j1, l2), CookieMod.pecanLog, metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (j1 = 0; j1 < l; ++j1)
                    {
                        block = par1World.getBlockState(new BlockPos(par3, par4 + j1, par5)).getBlock();

                        if (block == null || block.isAir(par1World, new BlockPos(par3, par4 + j1, par5)) || block.isLeaves(par1World, new BlockPos(par3, par4 + j1, par5)))
                        {
                        	func_175905_a(par1World, new BlockPos(par3, par4 + j1, par5),CookieMod.pecanLog, this.metaWood);

                            if (this.vinesGrow && j1 > 0)
                            {
                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(new BlockPos(par3 - 1, par4 + j1, par5)))
                                {
                                	func_175905_a(par1World, new BlockPos(par3 - 1, par4 + j1, par5),(Blocks.vine), 8);
                                }

                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(new BlockPos(par3 + 1, par4 + j1, par5)))
                                {
                                	func_175905_a(par1World, new BlockPos(par3 + 1, par4 + j1, par5), (Blocks.vine), 2);
                                }

                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(new BlockPos(par3, par4 + j1, par5 - 1)))
                                {
                                	func_175905_a(par1World, new BlockPos(par3, par4 + j1, par5 - 1), (Blocks.vine), 1);
                                }

                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(new BlockPos(par3, par4 + j1, par5 + 1)))
                                {
                                	func_175905_a(par1World, new BlockPos(par3, par4 + j1, par5 + 1), Blocks.vine, 4);
                                }
                            }
                        }
                    }
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

}
