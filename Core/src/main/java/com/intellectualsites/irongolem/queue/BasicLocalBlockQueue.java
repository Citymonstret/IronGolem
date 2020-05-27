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
import com.intellectualsites.irongolem.util.BlockWrapper;
import com.intellectualsites.irongolem.util.MathUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;

public abstract class BasicLocalBlockQueue extends LocalBlockQueue {

    private final String world;
    private final ConcurrentHashMap<Long, LocalChunk> blockChunks = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<LocalChunk> chunks = new ConcurrentLinkedDeque<>();
    private long modified;
    private LocalChunk lastWrappedChunk;
    private int lastX = Integer.MIN_VALUE;
    private int lastZ = Integer.MIN_VALUE;
    private boolean setbiome = false;

    public BasicLocalBlockQueue(String world) {
        this.world = world;
        this.modified = System.currentTimeMillis();
    }

    public abstract LocalChunk getLocalChunk(int x, int z);

    @Override public abstract BlockWrapper getBlock(int x, int y, int z);

    public abstract void setComponents(LocalChunk lc)
        throws ExecutionException, InterruptedException;

    @Override public final String getWorld() {
        return world;
    }

    @Override public final boolean next() {
        lastX = Integer.MIN_VALUE;
        lastZ = Integer.MIN_VALUE;
        try {
            if (this.blockChunks.size() == 0) {
                return false;
            }
            synchronized (blockChunks) {
                LocalChunk chunk = chunks.poll();
                if (chunk != null) {
                    blockChunks.remove(chunk.longHash());
                    return this.execute(chunk);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean execute(@NotNull LocalChunk lc)
        throws ExecutionException, InterruptedException {
        this.setComponents(lc);
        return true;
    }

    @Override public final int size() {
        return chunks.size();
    }

    @Override public final long getModified() {
        return modified;
    }

    @Override public final void setModified(long modified) {
        this.modified = modified;
    }

    @Override public boolean setBlock(int x, int y, int z, BlockWrapper data) {
        if ((y > 255) || (y < 0)) {
            return false;
        }
        int cx = x >> 4;
        int cz = z >> 4;
        if (cx != lastX || cz != lastZ) {
            lastX = cx;
            lastZ = cz;
            long pair = (long) (cx) << 32 | (cz) & 0xFFFFFFFFL;
            lastWrappedChunk = this.blockChunks.get(pair);
            if (lastWrappedChunk == null) {
                lastWrappedChunk = this.getLocalChunk(x >> 4, z >> 4);
                lastWrappedChunk.setBlock(x & 15, y, z & 15, data);
                LocalChunk previous = this.blockChunks.put(pair, lastWrappedChunk);
                if (previous == null) {
                    return chunks.add(lastWrappedChunk);
                }
                this.blockChunks.put(pair, previous);
                lastWrappedChunk = previous;
            }
        }
        lastWrappedChunk.setBlock(x & 15, y, z & 15, data);
        return true;
    }

    public abstract class LocalChunk {
        public final BasicLocalBlockQueue parent;
        public final int z;
        public final int x;

        public BlockWrapper[][] baseblocks;

        public LocalChunk(BasicLocalBlockQueue parent, int x, int z) {
            this.parent = parent;
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        public abstract void setBlock(final int x, final int y, final int z, final BlockWrapper block);

        public long longHash() {
            return MathUtils.pairInt(x, z);
        }

        @Override public int hashCode() {
            return MathUtils.pair((short) x, (short) z);
        }
    }


    public class BasicLocalChunk extends LocalChunk {

        public BasicLocalChunk(BasicLocalBlockQueue parent, int x, int z) {
            super(parent, x, z);
            baseblocks = new BlockWrapper[16][];
        }

        @Override public void setBlock(int x, int y, int z, BlockWrapper block) {
            this.setInternal(x, y, z, block);
        }

        private void setInternal(final int x, final int y, final int z, final BlockWrapper baseBlock) {
            final int i = QueueRestorationHandler.CACHE_I[y][x][z];
            final int j = QueueRestorationHandler.CACHE_J[y][x][z];
            BlockWrapper[] array = baseblocks[i];
            if (array == null) {
                array = (baseblocks[i] = new BlockWrapper[4096]);
            }
            array[j] = baseBlock;
        }
    }
}
