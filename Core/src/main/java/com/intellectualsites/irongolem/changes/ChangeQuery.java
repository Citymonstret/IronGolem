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
import com.intellectualsites.irongolem.IronGolem;
import com.intellectualsites.irongolem.util.CuboidRegion;
import com.intellectualsites.irongolem.util.PointRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * A query for {@link Change changes}
 */
public class ChangeQuery {

    private CuboidRegion region;
    private World world;
    private int limit = Short.MAX_VALUE;
    private boolean distinct = false;

    private ChangeQuery() {
    }

    /**
     * Create a new {@link ChangeQuery}
     *
     * @return New query
     */
    public static ChangeQuery newQuery() {
        return new ChangeQuery();
    }

    /**
     * Query for changes in a specific {@link World}
     *
     * @param world World to query in
     * @return The query instance
     */
    public ChangeQuery inWorld(@NotNull final World world) {
        this.world = Preconditions.checkNotNull(world, "World may not be null");
        return this;
    }

    /**
     * Query for changes in a {@link CuboidRegion cuboid region}
     *
     * @param region Region
     * @return The query instance
     */
    public ChangeQuery inRegion(@NotNull final CuboidRegion region) {
        this.region = Preconditions.checkNotNull(region, "Region may not be null");
        return this;
    }

    /**
     * Query for changes at a specific location
     *
     * @param location Location
     * @return The query instance
     */
    public ChangeQuery atLocation(@NotNull final Location location) {
        Preconditions.checkNotNull(location, "Location may not be null");
        this.world = location.getWorld();
        this.region = PointRegion.at(location.toVector());
        return this;
    }

    /**
     * Only query for the oldest available value at any given location
     *
     * @return The query instance
     */
    public ChangeQuery distinctValues() {
       this.distinct = true;
       return this;
    }

    /**
     * Limit the amount of {@link Change changes} that are queried for
     *
     * @param limit Change limit
     * @return The query instance
     */
    public ChangeQuery withLimit(int limit) {
        if (limit == -1) {
            limit = Integer.MAX_VALUE;
        }
        Preconditions.checkState(limit > 0, "Limit has to be positive");
        this.limit = limit;
        return this;
    }

    /**
     * Get the region that is queried in
     *
     * @return Region
     */
    public CuboidRegion getRegion() {
        return this.region;
    }

    /**
     * Get the world that is queried in
     *
     * @return World
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Get the query size limit
     *
     * @return Query size limit
     */
    public int getLimit() {
        return this.limit;
    }

    /**
     * Whether or not distinct values should
     * be queried for
     *
     * @return Whether or not the query is for distinct values
     */
    public boolean shouldUseDistinct() {
        return this.distinct;
    }

    /**
     * Query for the results
     *
     * @return Future that completes with the results in reverse chronological order
     */
    public CompletableFuture<Changes> queryChanges() {
        Preconditions.checkNotNull(this.world, "World may not be null");
        Preconditions.checkNotNull(this.region, "Region may not be null");
        Preconditions.checkState(this.limit > 0, "Limit has to be positive");
        return IronGolem.getPlugin(IronGolem.class).getChangeLogger().queryChanges(this)
            .thenApply(changeList -> new Changes(this.region, this.world, changeList));
    }

}
