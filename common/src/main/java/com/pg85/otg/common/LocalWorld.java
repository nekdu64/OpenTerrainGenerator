package com.pg85.otg.common;

import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.customobjects.SpawnableObject;
import com.pg85.otg.customobjects.bofunctions.EntityFunction;
import com.pg85.otg.customobjects.structures.CustomStructureCache;
import com.pg85.otg.exception.BiomeNotFoundException;
import com.pg85.otg.generator.ChunkBuffer;
import com.pg85.otg.generator.ObjectSpawner;
import com.pg85.otg.generator.biome.BiomeGenerator;
import com.pg85.otg.network.ConfigProvider;
import com.pg85.otg.util.BiomeIds;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.bo3.NamedBinaryTag;
import com.pg85.otg.util.minecraft.defaults.TreeType;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public interface LocalWorld
{
    public String getName();

    public String getWorldSettingsName();
    
	public int getDimensionId();

    public long getSeed();

	public File getWorldSaveDir();

    public ConfigProvider getConfigs();
    
	public ObjectSpawner getObjectSpawner();
	
    public CustomStructureCache getStructureCache();
    	
	public WorldSession getWorldSession();

	public void deleteWorldSessionData();
	   
    /**
     * Gets the height the base terrain of the world is capped at. Resources
     * ignore this limit.
     *
     * @return The height the base terrain of the world is capped at.
     */
    public int getHeightCap();

    /**
     * Returns the vertical scale of the world. 128 blocks is the normal
     * scale, 256 doubles the scale, 64 halves the scale, etc. Only powers of
     * two will be returned.
     *
     * @return The vertical scale of the world.
     */
    public int getHeightScale();
    
	
	// Biomes

    /**
     * Gets the biome generator.
     * @return The biome generator.
     */
    public BiomeGenerator getBiomeGenerator();
    
	/**
     * Creates a LocalBiome instance for the given biome.
     * @param biomeConfig The settings for the biome, which are saved in
     * the LocalBiome instance.
     * @param requestedBiomeIds The ids of the biome, used to register the
     * LocalBiome instance. The implementation is allowed to use another id if
     * necessary.
     * @return The LocalBiome instance.
     */
    public LocalBiome createBiomeFor(BiomeConfig biomeConfig, BiomeIds biomeIds, ConfigProvider configProvider, boolean isReload);

    /**
     * Gets how many different biome ids are in the world. Biome ids will start
     * at zero, so a returned value of 1024 means that the biome ids range from
     * 0 to 1023, inclusive.
     *
     * @return How many different biome ids are in the world.
     */
    public int getMaxBiomesCount();

    /**
     * Gets how many different biome ids Minecraft can actually save to the map
     * files. Biome ids will start at zero, so a returned value of 256 means
     * that the biome ids range from 0 to 255, inclusive. Biomes outside of this
     * range, but inside the range of {@link #getMaxBiomesCount()} must have a
     * ReplaceToBiomeName setting bringing their saved id back into the normal
     * range.
     *
     * @return How many different biome ids are in the save files.
     */
    public int getMaxSavedBiomesCount();

    public ArrayList<LocalBiome> getAllBiomes();

    public LocalBiome getBiomeByOTGIdOrNull(int id);
    
    public LocalBiome getFirstBiomeOrNull();

    public LocalBiome getBiomeByNameOrNull(String name);

    /**
     * Calculates the biome at the given coordinates. This is usually taken
     * from the biome generator, but this can be changed using the
     * configuration files. In that case it is read from the chunk data.
     *
     * @param x The block x.
     * @param z The block z.
     * @return The biome at the given coordinates.
     * @throws BiomeNotFoundException If the biome id is invalid.
     * @see #getCalculatedBiome(int, int) to always use the biome generator.
     * @see #getSavedBiome(int, int) to always use the chunk data.
     */
    public LocalBiome getBiome(int x, int z) throws BiomeNotFoundException;

    /**
     * Gets the (stored) biome at the given coordinates.
     *
     * @param x The block x.
     * @param z The block z.
     * @return The name of the saved biome at the given coordinates.
     */
    public String getSavedBiomeName(int x, int z);

    /**
     * Gets the biome as generated by the biome generator.
     * @param x The block x.
     * @param z The block z.
     * @return The biome.
     */
    public LocalBiome getCalculatedBiome(int x, int z);

	public int getRegisteredBiomeId(String resourceLocation);
    
    // Default generators
    
    public void prepareDefaultStructures(int chunkX, int chunkZ, boolean dry);

    public boolean placeDungeon(Random rand, int x, int y, int z);

    public boolean placeFossil(Random rand, ChunkCoordinate chunkCoord);

    public boolean placeTree(TreeType type, Random rand, int x, int y, int z);

    public boolean placeDefaultStructures(Random rand, ChunkCoordinate chunkCoord);

    /**
     * Gets a structure part in Mojang's structure format.
     * @param name Full name of the structure.
     * @return The structure, or null if it does not exist.
     */
    SpawnableObject getMojangStructurePart(String name);

	public boolean chunkHasDefaultStructure(Random rand, ChunkCoordinate chunk);
    
    // Mobs / entities
    
    /**
     * Since Minecraft Beta 1.8, friendly mobs are mainly spawned during the
     * terrain generation. Calling this method will place the mobs.
     * @param biome      Biome to place the mobs of.
     * @param random     Random number generator.
     * @param chunkCoord The chunk to spawn the mobs in.
     */
    public void placePopulationMobs(LocalBiome biome, Random random, ChunkCoordinate chunkCoord);

	void spawnEntity(EntityFunction<?> entityData, ChunkCoordinate chunkBeingPopulated);
    
    // Population start and end
    
    /**
     * Marks the given chunks as being populated. No new chunks may be created. Implementations may cache the chunk.
     * @param chunkCoord The chunk being populated.
     * @throws IllegalStateException If another chunks is being populated. Call {@link #endPopulation()} first.
     * @see #endPopulation()
     */
    public void startPopulation(ChunkCoordinate chunkCoord);

    /**
     * Stops the population step. New chunks may be created again. Implementations may cache the chunk.
     * @throws IllegalStateException If no chunk was being populated.
     * @see #startPopulation(ChunkCoordinate)
     */
    public void endPopulation();

    // Blocks
        
    public LocalMaterialData getMaterial(int x, int y, int z, ChunkCoordinate chunkBeingPopulated);

    public int getBlockAboveLiquidHeight(int x, int z, ChunkCoordinate chunkBeingPopulated);

    public int getBlockAboveSolidHeight(int x, int z, ChunkCoordinate chunkBeingPopulated);

    public int getHighestBlockAboveYAt(int x, int z, ChunkCoordinate chunkBeingPopulated);
    
    public int getHighestBlockYAt(int x, int z, boolean findSolid, boolean findLiquid, boolean ignoreLiquid, boolean ignoreSnow, boolean ignoreLeaves, ChunkCoordinate chunkBeingPopulated);

	public int getHeightMapHeight(int x, int z, ChunkCoordinate chunkBeingPopulated);
    
    public int getLightLevel(int x, int y, int z, ChunkCoordinate chunkBeingPopulated);

	public void setBlock(int x, int y, int z, LocalMaterialData material, NamedBinaryTag metaDataTag, ChunkCoordinate chunkBeingPopulated);
	
	public LocalMaterialData[] getBlockColumnInUnloadedChunk(int x, int z);
	
    /**
     * Executes ReplacedBlocks.
     *
     * <p>
     * During terrain population, four chunks are guaranteed to be loaded:
     * (chunkX, chunkZ), (chunkX + 1, chunkZ), (chunkX, chunkZ + 1) and
     * (chunkX + 1, chunkZ + 1). All populators use an 8-block offset from the
     * top left chunk, and populate an area of 16x16 blocks from there. This
     * allows them to extend 8 blocks from their population area without
     * hitting potentially unloaded chunks.
     *
     * <p>
     * Populators may place blocks in already populated chunks, which would
     * cause those blocks to be never replaced. ReplacedBlocks uses the same
     * 8-block offset to minimize this risk.
     * @see ChunkCoordinate#getPopulatingChunk(int, int) Explanation about the
     * population offset.
     * @param chunkCoord The top left chunk for ReplacedBlocks.
     * 
     * Update: This comment is no longer accurate, all 4 chunks are replaceblock'd for each chunk populated.
     */
    public void replaceBlocks(ChunkCoordinate chunkCoord);
    	
	// Chunks
	
	boolean isInsidePregeneratedRegion(ChunkCoordinate chunk);

	public ChunkCoordinate getSpawnChunk();

	public boolean generateModdedCaveGen(int x, int z, ChunkBuffer chunkBuffer);

	public boolean isInsideWorldBorder(ChunkCoordinate chunkCoordinate);

	public boolean isBO4Enabled();
	
	public void updateSpawnPointY(ChunkCoordinate chunkBeingPopulated);
}
