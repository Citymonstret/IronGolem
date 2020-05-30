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

package com.intellectualsites.irongolem.v1_15_R1;

import com.intellectualsites.irongolem.queue.BasicLocalBlockQueue;
import com.intellectualsites.irongolem.restoration.QueueRestorationHandler;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import io.papermc.lib.PaperLib;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.EnumDirection;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.inventory.InventoryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class BukkitLocalQueue extends BasicLocalBlockQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(BukkitLocalQueue.class);

    private static final EnumDirection[] NEIGHBOUR_ORDER = {
        EnumDirection.WEST, EnumDirection.EAST,
        EnumDirection.DOWN, EnumDirection.UP,
        EnumDirection.NORTH, EnumDirection.SOUTH
    };

    public BukkitLocalQueue(String world) {
        super(world);
    }

    @Override public LocalChunk getLocalChunk(int x, int z) {
        return new BasicLocalChunk(this, x, z) {
        };
    }

    @Override public final void setComponents(LocalChunk lc) {
        setBaseBlocks(lc);
    }

    public void setBaseBlocks(LocalChunk localChunk) {
        World worldObj = Bukkit.getWorld(getWorld());
        BukkitWorld bukkitWorld = (BukkitWorld) BukkitAdapter.adapt(worldObj);

        if (worldObj == null) {
            throw new NullPointerException("World cannot be null.");
        }
        final Consumer<Chunk> chunkConsumer = chunk -> {
            final net.minecraft.server.v1_15_R1.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
            for (int layer = 0; layer < localChunk.baseblocks.length; layer++) {
                BaseBlock[] blocksLayer = localChunk.baseblocks[layer];
                if (blocksLayer != null) {
                    for (int j = 0; j < blocksLayer.length; j++) {
                        if (blocksLayer[j] != null) {
                            BaseBlock block = blocksLayer[j];
                            int x = QueueRestorationHandler.x_loc[layer][j];
                            int y = QueueRestorationHandler.y_loc[layer][j];
                            int z = QueueRestorationHandler.z_loc[layer][j];

                            final BlockPosition position = new BlockPosition(x, y, z);

                            final Block existingBlock = chunk.getBlock(x, y, z);
                            final BlockState existingBlockState = existingBlock.getState();

                            // Clear containers
                            if (existingBlockState instanceof InventoryHolder) {
                                ((InventoryHolder) existingBlockState).getInventory().clear();
                            }

                            bukkitWorld.setBlock(BlockVector3.at(x, y, z), block, true);
                            /*
                            // We use a hacky workaround here to not have to deal with
                            // tile entities. Basically, if we have any NBT data to set
                            // then we are not going to be using NMS
                            if (block.getBlockState().length > 0 || oldData.getBlock() instanceof ITileEntity) {
                                LOGGER.info("Using slow block setting method because we have a block state");

                            } else {
                                final IBlockData newData = ((CraftBlockData) block.getBlockData()).getState();
                                final IBlockData setData = nmsChunk.setType(position, newData, false);

                                if (setData != null || oldData == newData) {
                                    try {
                                        if (block.getBlockState().length > 0) {
                                            final InputStream stream = ByteSource.wrap(block.getBlockState()).openStream();
                                            final NBTTagCompound nbtTagCompound =
                                                NBTCompressedStreamTools.a(stream);
                                            if (nbtTagCompound != null) {
                                                nbtTagCompound.setInt("x", x);
                                                nbtTagCompound.setInt("y", y);
                                                nbtTagCompound.setInt("z", z);
                                                final TileEntity tileEntity =
                                                    nmsChunk.getWorld().getTileEntity(position);
                                                if (tileEntity != null) {
                                                    tileEntity.load(nbtTagCompound);
                                                }
                                            }
                                        }
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (setData != null) {
                                    // Update lighting
                                    nmsChunk.getWorld().getChunkProvider().getLightEngine().a(position);
                                    if (nmsChunk.getState().isAtLeast(PlayerChunk.State.TICKING)) {
                                        ((ChunkProviderServer) nmsChunk.getWorld().getChunkProvider())
                                            .flagDirty(position);
                                    }
                                    for (final EnumDirection direction : NEIGHBOUR_ORDER) {
                                        final BlockPosition shifted = position.shift(direction);
                                        nmsChunk.getWorld().getType(shifted).doPhysics(nmsChunk.getWorld(),
                                            shifted, oldData.getBlock(), position, false);
                                    }
                                    if (newData.isComplexRedstone()) {
                                        nmsChunk.getWorld().updateAdjacentComparators(position, newData.getBlock());
                                    }
                                    oldData.b(nmsChunk.getWorld(), position, 2);
                                    newData.a(nmsChunk.getWorld(), position, 2);
                                    newData.b(nmsChunk.getWorld(), position, 2);
                                    nmsChunk.getWorld().a(position, oldData, newData);
                                } else {
                                    LOGGER.error("The new data is null...");
                                }
                            }
                            */
                        }
                    }
                }
            }

            /*
            final PlayerChunkMap playerChunkMap = ((WorldServer) nmsChunk.getWorld()).getChunkProvider().playerChunkMap;
            final PlayerChunk playerChunk = playerChunkMap.visibleChunks.get(ChunkCoordIntPair.pair(chunk.getX(), chunk.getZ()));
            if (playerChunk != null && playerChunk.hasBeenLoaded()) {
                playerChunkMap.a(playerChunk);
                // Once we're done with the chunk, we re-send them to all nearby players
                final PacketPlayOutMapChunk chunkPacket = new PacketPlayOutMapChunk(nmsChunk, 65535);
                final PacketPlayOutLightUpdate lightPacket = new PacketPlayOutLightUpdate(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()),
                    nmsChunk.getWorld().getChunkProvider().getLightEngine());
                for (final Player player : worldObj.getPlayers()) {
                    final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(chunkPacket);
                    connection.sendPacket(lightPacket);
                }
                chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
            }
             */
        };
        PaperLib.getChunkAtAsync(worldObj, localChunk.getX(), localChunk.getZ(), true)
            .thenAccept(chunkConsumer);
    }

}
