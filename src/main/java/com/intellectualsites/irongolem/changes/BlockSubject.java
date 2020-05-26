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
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class BlockSubject implements ChangeSubject<BlockData> {

    private final BlockData from;
    private final BlockData to;

    private BlockSubject(@NotNull final BlockData from, @NotNull final BlockData to) {
        this.from = Preconditions.checkNotNull(from, "From may not be null");
        this.to = Preconditions.checkNotNull(to, "To may not be null");
    }

    /**
     * Create a new block subject
     *
     * @param from The original block data
     * @param to   New block data
     * @return Created subject
     */
    @NotNull public static BlockSubject of(@NotNull final BlockData from,
        @NotNull final BlockData to) {
        return new BlockSubject(from, to);
    }

    @Override public String serializeFrom() {
        return this.from.getAsString();
    }

    @Override public String serializeTo() {
        return this.to.getAsString();
    }

    @Override @NotNull public ChangeType getType() {
        return ChangeType.BLOCK;
    }

    @Override public BlockData getFrom() {
        return this.from;
    }

    @Override public BlockData getTo() {
        return this.to;
    }

}
