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

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.CompoundTagBuilder;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for blocks that contain both their data
 * and serialized state (if there is any)
 */
public class BlockWrapper {

    public static final BlockWrapper AIR = of(BlockTypes.AIR.getDefaultState());
    public static final CompoundTag EMPTY = CompoundTagBuilder.create().build();

    private final BlockState blockData;
    private final CompoundTag blockState;

    private BlockWrapper(@NotNull final BlockState blockData, @Nullable final CompoundTag blockState) {
        this.blockData = Preconditions.checkNotNull(blockData, "Block data may not be null");
        this.blockState = blockState == null ? EMPTY : blockState;
    }

    @NotNull public BlockState getBlockData() {
        return this.blockData;
    }

    @NotNull public CompoundTag getBlockState() {
        return this.blockState;
    }

    public static BlockWrapper of(@NotNull final BlockData blockData) {
        return new BlockWrapper(BukkitAdapter.adapt(blockData), EMPTY);
    }

    public static BlockWrapper of(@NotNull final BlockState blockData, @NotNull final CompoundTag tag) {
        return new BlockWrapper(blockData, tag);
    }

    public static BlockWrapper of(@NotNull final BlockData blockData, @NotNull final CompoundTag tag) {
        return new BlockWrapper(BukkitAdapter.adapt(blockData), tag);
    }

    public static BlockWrapper of(@NotNull final BlockState blockData) {
        return new BlockWrapper(blockData, EMPTY);
    }

    public static BlockWrapper of(@NotNull final BaseBlock baseBlock) {
        return new BlockWrapper(baseBlock.toImmutableState(), baseBlock.getNbtData());
    }

    public static BlockWrapper of(@NotNull final Block block) {
        return of(BukkitAdapter.adapt(block.getWorld()).getFullBlock(BukkitAdapter.asBlockVector(block.getLocation())));
    }

}
