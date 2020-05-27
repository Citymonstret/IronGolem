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

package com.intellectualsites.irongolem.changes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.intellectualsites.irongolem.util.CuboidRegion;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A collection of changes, often as a result of a {@link ChangeQuery}
 */
public class Changes {

    private final World world;
    private final Multimap<Vector, Change> changes;
    private final CuboidRegion region;

    public Changes(@NotNull final CuboidRegion region, @NotNull final World world, @NotNull final Collection<Change> changes) {
        this.world = Preconditions.checkNotNull(world, "World may not be null");
        this.region = Preconditions.checkNotNull(region, "Region may not be null");
        this.changes = MultimapBuilder.hashKeys().linkedListValues().build();
        for (final Change change : changes) {
            this.changes.put(change.getLocation().toVector(), change);
        }
    }

    /**
     * Get all changes in this change set
     *
     * @return All changes
     */
    @NotNull public Collection<Change> getChanges() {
        return this.changes.values();
    }

    /**
     * Get the total number of changes
     *
     * @return Number of changes
     */
    public int getSize() {
        return this.changes.size();
    }

    /**
     * Get the world the changes took place in
     *
     * @return World
     */
    @NotNull public World getWorld() {
        return this.world;
    }

    /**
     * Get the region the changes took place in
     *
     * @return Region
     */
    @NotNull public CuboidRegion getRegion() {
        return this.region;
    }

    /**
     * Get all changes at a specified location
     *
     * @param vector Location
     * @return Changes at the location
     */
    @NotNull public Collection<Change> getChangesAt(@NotNull final Vector vector) {
        return this.changes.get(vector);
    }

    /**
     * Check whether or not this change
     * set has distinct values at every location
     *
     * @return True if there's only one change at every location
     */
    public boolean isDistinct() {
        for (final Vector vector : this.changes.keys()) {
            if (this.changes.get(vector).size() > 1) {
                return false;
            }
        }
        return true;
    }

}
