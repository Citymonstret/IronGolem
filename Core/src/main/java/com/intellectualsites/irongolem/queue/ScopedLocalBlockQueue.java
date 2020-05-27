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

import com.intellectualsites.irongolem.util.BlockWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ScopedLocalBlockQueue extends DelegateLocalBlockQueue {

    private final int minX;
    private final int minY;
    private final int minZ;

    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private final int dx;
    private final int dy;
    private final int dz;

    public ScopedLocalBlockQueue(LocalBlockQueue parent, Location min, Location max) {
        super(parent);
        this.minX = min.getBlockX();
        this.minY = min.getBlockY();
        this.minZ = min.getBlockZ();

        this.maxX = max.getBlockX();
        this.maxY = max.getBlockY();
        this.maxZ = max.getBlockZ();

        this.dx = maxX - minX;
        this.dy = maxY - minY;
        this.dz = maxZ - minZ;
    }

    @Override public boolean setBlock(int x, int y, int z, BlockWrapper data) {
        return x >= 0 && x <= dx && y >= 0 && y <= dy && z >= 0 && z <= dz && super
            .setBlock(x + minX, y + minY, z + minZ, data);
    }

    public Location getMin() {
        return new Location(Bukkit.getWorld(getWorld()), minX, minY, minZ);
    }

    public Location getMax() {
        return new Location(Bukkit.getWorld(getWorld()), maxX, maxY, maxZ);
    }

}
