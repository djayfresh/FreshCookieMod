package freshcaa.fresh.cookies;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import freshcaa.minecraft.world.MacadamiaTreeWorldGenerator;
import freshcaa.minecraft.world.PecanTreeWorldGenerator;

public class WorldGeneratorDjf implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		// TODO Auto-generated method stub
		switch (world.provider.dimensionId)
		{
		//case -1: generateNether(world, random,chunkX*16,chunkZ*16);
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateSurface(World world, Random random, int BlockX,
			int BlockZ)
	{
		for (int i = 0; i < 10; i++)
		{
			int Xcoord = BlockX + random.nextInt(16);
			int Zcoord = BlockZ + random.nextInt(16);
			int Ycoord = random.nextInt(16);
			
			//(new WorldGenMinable(CookieMod.amethystOre.blockID, 4)).generate(world, random, Xcoord, Ycoord, Zcoord);
		}
		
		for(int i = 0; i < 10; i++) //the size represends the proability of spwaning 
		{
			int xCoord = BlockX + random.nextInt(30);
			int yCoord = random.nextInt(90);
			int zCoord = BlockZ + random.nextInt(30);
			
			(new PecanTreeWorldGenerator(false, 6, 0, 0, false)).generate(world, random, xCoord, yCoord, zCoord);
		}
		
		for(int i = 0; i < 10; i++) //the size represends the proability of spwaning 
		{
			
			int xCoord2 = BlockX + random.nextInt(20);
			int yCoord2 = random.nextInt(90);
			int zCoord2 = BlockZ + random.nextInt(20);
			
			(new MacadamiaTreeWorldGenerator(false, 3, 0, 0, false)).generate(world, random, xCoord2, yCoord2, zCoord2);
		}
	}
}
