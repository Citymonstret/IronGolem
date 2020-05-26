/*
 *       _____  _       _    _____                                _
 *      |  __ \| |     | |  / ____|                              | |
 *      | |__) | | ___ | |_| (___   __ _ _   _  __ _ _ __ ___  __| |
 *      |  ___/| |/ _ \| __|\___ \ / _` | | | |/ _` | '__/ _ \/ _` |
 *      | |    | | (_) | |_ ____) | (_| | |_| | (_| | | |  __/ (_| |
 *      |_|    |_|\___/ \__|_____/ \__, |\__,_|\__,_|_|  \___|\__,_|
 *                                    | |
 *                                    |_|
 *            PlotSquared plot management system for Minecraft
 *                  Copyright (C) 2020 IntellectualSites
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.intellectualsites.irongolem.queue;

import com.intellectualsites.irongolem.restoration.QueueRestorationHandler;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;

import java.util.function.Consumer;

public class BukkitLocalQueue extends BasicLocalBlockQueue {

    public BukkitLocalQueue(String world) {
        super(world);
    }

    @Override public LocalChunk getLocalChunk(int x, int z) {
        return new BasicLocalChunk(this, x, z) {
        };
    }

    @Override public BlockData getBlock(int x, int y, int z) {
        World worldObj = Bukkit.getWorld(getWorld());
        if (worldObj != null) {
            Block block = worldObj.getBlockAt(x, y, z);
            return block.getBlockData();
        } else {
            return Material.AIR.createBlockData();
        }
    }

    @Override public final void setComponents(LocalChunk lc) {
        setBaseBlocks(lc);
    }

    public void setBaseBlocks(LocalChunk localChunk) {
        World worldObj = Bukkit.getWorld(getWorld());
        if (worldObj == null) {
            throw new NullPointerException("World cannot be null.");
        }
        final Consumer<Chunk> chunkConsumer = chunk -> {
            for (int layer = 0; layer < localChunk.baseblocks.length; layer++) {
                BlockData[] blocksLayer = localChunk.baseblocks[layer];
                if (blocksLayer != null) {
                    for (int j = 0; j < blocksLayer.length; j++) {
                        if (blocksLayer[j] != null) {
                            BlockData block = blocksLayer[j];
                            int x = QueueRestorationHandler.x_loc[layer][j];
                            int y = QueueRestorationHandler.y_loc[layer][j];
                            int z = QueueRestorationHandler.z_loc[layer][j];

                            final Block existing = chunk.getBlock(x, y, z);
                            final BlockData existingData = existing.getBlockData();

                            if (existingData.matches(block)) {
                                continue;
                            }

                            if (existing.getState() instanceof Container) {
                                ((Container) existing.getState()).getInventory().clear();
                            }

                            existing.setBlockData(block, false);

                            // TODO: Use NMS chunks

                            /*

                            existing.setType(BukkitAdapter.adapt(block.getBlockType()), false);
                            existing.setBlockData(blockData, false);
                            if (block.hasNbtData()) {
                                CompoundTag tag = block.getNbtData();
                                StateWrapper sw = new StateWrapper(tag);

                                sw.restoreTag(worldObj.getName(), existing.getX(), existing.getY(),
                                    existing.getZ());
                            }


                             */
                        }
                    }
                }
            }
        };
        PaperLib.getChunkAtAsync(worldObj, localChunk.getX(), localChunk.getZ(), true)
            .thenAccept(chunkConsumer);
    }

}
