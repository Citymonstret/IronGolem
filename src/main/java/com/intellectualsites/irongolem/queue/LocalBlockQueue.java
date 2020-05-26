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

import org.bukkit.block.data.BlockData;

public abstract class LocalBlockQueue {

    public abstract boolean next();

    public abstract int size();

    public abstract long getModified();

    public abstract void setModified(long modified);

    /**
     * Sets the block at the coordinates provided to the given id.
     *
     * @param x    the x coordinate from from 0 to 15 inclusive
     * @param y    the y coordinate from from 0 (inclusive) - maxHeight(exclusive)
     * @param z    the z coordinate from 0 to 15 inclusive
     * @param data the data to set the block to
     */
    public abstract boolean setBlock(final int x, final int y, final int z, final BlockData data);

    public abstract BlockData getBlock(int x, int y, int z);

    public abstract String getWorld();

    public boolean enqueue() {
        return GlobalBlockQueue.IMP.enqueue(this);
    }

}
