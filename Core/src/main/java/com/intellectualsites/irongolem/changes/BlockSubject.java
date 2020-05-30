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
import com.intellectualsites.irongolem.util.BlockWrapper;
import com.intellectualsites.irongolem.util.NBTUtils;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ChangeSubject} involving block data
 */
public class BlockSubject implements ChangeSubject<BlockState, CompoundTag> {

    private final BlockState from;
    private final BlockState to;
    private final CompoundTag oldState;
    private final CompoundTag newState;

    private final boolean oldFull;
    private final boolean newFull;

    private BlockSubject(@NotNull final BlockWrapper from, @NotNull final BlockWrapper to) {
        this.from = Preconditions.checkNotNull(from, "From may not be null").getBlockData();
        this.to = Preconditions.checkNotNull(to, "To may not be null").getBlockData();
        this.oldState = from.getBlockState();
        this.newState = to.getBlockState();

        this.oldFull = !this.oldState.getValue().isEmpty();
        this.newFull = !this.newState.getValue().isEmpty();
    }

    /**
     * Create a new block subject
     *
     * @param from The original block data
     * @param to   New block data
     * @return Created subject
     */
    @NotNull public static BlockSubject of(@NotNull final BlockWrapper from,
        @NotNull final BlockWrapper to) {
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

    @Override public BlockState getFrom() {
        return this.from;
    }

    @Override public BlockState getTo() {
        return this.to;
    }

    @Override public CompoundTag getOldState() {
        return this.oldState;
    }

    @Override public CompoundTag getNewState() {
        return this.newState;
    }

    @Override public byte[] serializeNewState() {
        if (this.newState == null || !this.oldFull) {
            return new byte[0];
        }
        return NBTUtils.compoundToBytes(this.newState);
    }

    @Override public byte[] serializeOldState() {
        if (this.newState == null || !this.oldFull) {
            return new byte[0];
        }
        return NBTUtils.compoundToBytes(this.oldState);
    }

    @NotNull public BaseBlock getFromFull() {
        if (this.oldFull) {
            return Preconditions.checkNotNull(this.getFrom().toBaseBlock(this.oldState),
                "Failed to create base block");
        } else {
            return this.getFrom().toBaseBlock();
        }
    }

    @NotNull public BaseBlock getToFull() {
        if (this.newFull) {
            return Preconditions.checkNotNull(this.getTo().toBaseBlock(this.newState),
                "Failed to create base block");
        } else {
            return this.getTo().toBaseBlock();
        }
    }

}
