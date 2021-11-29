package com.pg85.otg.gen.resource;

import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.IWorldGenRegion;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;
import com.pg85.otg.util.materials.MaterialProperties;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.Random;

public class BerryBush {

    protected enum SparseOption {
        Sparse,
        Decorated
    }

    protected static void spawnBerryBushes(IWorldGenRegion worldGenregion, Random random, int centerX, int centerZ, int frequency, int minAltitude, int maxAltitude, MaterialSet sourceBlocks, SparseOption sparseOption) {
        int centerY = worldGenregion.getHighestBlockAboveYAt(centerX, centerZ);

        if (centerY < Constants.WORLD_DEPTH) {
            return;
        }

        LocalMaterialData worldMaterial;

        // Fix y position
        while (
                (
                        //stay in y bounds
                        (centerY >= Constants.WORLD_DEPTH && centerY < Constants.WORLD_HEIGHT) &&
                                //null check
                                (worldMaterial = worldGenregion.getMaterial(centerX, centerY, centerZ)) != null &&
                                //if air or leaves
                                (
                                        worldMaterial.isAir() ||
                                                worldMaterial.isLeaves()
                                ) &&
                                worldGenregion.getMaterial(centerX, centerY - 1, centerZ) != null
                ) && (
                        centerY > 0
                )
        ) {
            //move down
            centerY--;
        }
        centerY++;

        // Try to place BERRY BUSH
        int x;
        int y;
        int z;

        int xzBounds = (sparseOption == SparseOption.Sparse) ? 7 : 5;
        int yBounds = (sparseOption == SparseOption.Sparse) ? 4 : 3;
        frequency += (sparseOption == SparseOption.Sparse) ? 0 : 10;

        for (int i = 0; i < frequency; i++) {
            x = centerX + random.nextInt(xzBounds) - random.nextInt(xzBounds);
            y = centerY + random.nextInt(yBounds) - random.nextInt(yBounds);
            z = centerZ + random.nextInt(xzBounds) - random.nextInt(xzBounds);
            //spawn if the block is in min/max altitude and block below is a source block
            if (
                    (worldMaterial = worldGenregion.getMaterial(x, y, z)) != null &&
                            worldMaterial.isAir() &&
                            (
                                    (worldMaterial = worldGenregion.getMaterial(x, y - 1, z)) != null &&
                                            sourceBlocks.contains(worldMaterial)
                            ) &&
                            (
                                    y >= minAltitude && y < maxAltitude
                            )
            ) {
                //set block directly so we can set the age of the berry bush
                worldGenregion.setBlock(x, y, z, LocalMaterials.BERRY_BUSH.withProperty(MaterialProperties.AGE_0_3, random.nextInt(4)));
            }
        }

    }
}