//
// IronGolem - A Minecraft block logging plugin
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.
//

package com.intellectualsites.irongolem.util;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A region containing two corners
 */
public class CuboidRegion {

    private final Vector minimumPoint;
    private final Vector maximumPoint;

    protected CuboidRegion(@NotNull final Vector point1, @NotNull final Vector point2) {
        this.minimumPoint = new Vector(Math.min(point1.getBlockX(), point2.getBlockX()), Math.min(point1.getBlockY(), point2.getBlockY()), Math.min(point1.getBlockZ(), point2.getBlockZ()));
        this.maximumPoint = new Vector(Math.max(point1.getBlockX(), point2.getBlockX()), Math.max(point1.getBlockY(), point2.getBlockY()), Math.max(point1.getBlockZ(), point2.getBlockZ()));
    }

    /**
     * Get the minimum point (least positive corner) of the region
     *
     * @return Minimum point
     */
    @NotNull public Vector getMinimumPoint() {
        return this.minimumPoint;
    }

    /**
     * Get the maximum point (most positive corner) of the region
     *
     * @return Minimum point
     */
    @NotNull public Vector getMaximumPoint() {
        return this.maximumPoint;
    }

    /**
     * Get the width of the region (X-axis)
     *
     * @return Region width
     */
    public int getWidth() {
        return this.getMaximumPoint().getBlockX() - this.getMinimumPoint().getBlockX() + 1;
    }

    /**
     * Get the height of the region (Y-axis)
     *
     * @return Region height
     */
    public int getHeight() {
        return this.getMaximumPoint().getBlockY() - this.getMinimumPoint().getBlockY() + 1;
    }

    /**
     * Get the depth of the region (Z-axis)
     *
     * @return Region depth
     */
    public int getDepth() {
        return this.getMaximumPoint().getBlockZ() - this.getMinimumPoint().getBlockZ() + 1;
    }

    /**
     * Get the number of blocks in the region
     *
     * @return Number of blocks
     */
    public long getSize() {
        return (long) this.getWidth() * (long) this.getHeight() * (long) this.getDepth();
    }

    public static CuboidRegion of(@NotNull final Vector minimumPoint, @NotNull final Vector maximumPoint) {
        return new CuboidRegion(minimumPoint, maximumPoint);
    }

}
