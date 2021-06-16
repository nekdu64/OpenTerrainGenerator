package com.pg85.otg.gen.resource;

import java.util.List;
import java.util.Random;

import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.interfaces.IBiomeConfig;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IWorldGenRegion;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;

public class SeaGrassResource extends FrequencyResourceBase
{
	private final double tallChance;

	public SeaGrassResource(IBiomeConfig biomeConfig, List<String> args, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
	{
		super(biomeConfig, args, logger, materialReader);
		this.frequency = readInt(args.get(0), 1, 500);
		this.rarity = readRarity(args.get(1));
		this.tallChance = readDouble(args.get(2), 0.0, 1.0);
	}

	@Override
	public void spawn(IWorldGenRegion world, Random random, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingDecorated)
	{
		// Find lowest point
		int y = world.getBlockAboveSolidHeight(x, z, chunkBeingDecorated);

		// TODO: sourceblocks
		LocalMaterialData below = world.getMaterial(x, y - 1, z, chunkBeingDecorated);
		if (below == null || !below.isSolid())
		{
			return;
		}

		LocalMaterialData material = world.getMaterial(x, y, z, chunkBeingDecorated);

		if (material == null)
		{
			return;
		}

		if (material.isLiquid())
		{
			// If the tall chance check succeeds and the above material is also water, place tall seagrass
			if (random.nextDouble() <= this.tallChance && world.getMaterial(x, y + 1, z, chunkBeingDecorated).isLiquid())
			{
				world.setBlock(x, y, z, LocalMaterials.TALL_SEAGRASS_LOWER, null, chunkBeingDecorated, false);
				world.setBlock(x, y + 1, z, LocalMaterials.TALL_SEAGRASS_UPPER, null, chunkBeingDecorated, false);
			} else {
				world.setBlock(x, y, z, LocalMaterials.SEAGRASS, null, chunkBeingDecorated, false);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return "SeaGrass(" + this.frequency + "," + this.rarity + "," + this.tallChance + ")";
	}	
}
