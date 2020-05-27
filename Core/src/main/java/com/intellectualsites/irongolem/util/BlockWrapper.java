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

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper for blocks that contain both their data
 * and serialized state (if there is any)
 */
public class BlockWrapper {

    public static final BlockWrapper AIR = of(Material.AIR.createBlockData());

    private final BlockData blockData;
    private final byte[] blockState;

    public BlockWrapper(@NotNull final BlockData blockData, @NotNull final byte[] blockState) {
        this.blockData = blockData;
        this.blockState = blockState;
    }

    @NotNull public BlockData getBlockData() {
        return this.blockData;
    }

    @NotNull public byte[] getBlockState() {
        return this.blockState;
    }

    public static BlockWrapper of(@NotNull final BlockData blockData) {
        return new BlockWrapper(blockData, new byte[0]);
    }

}
