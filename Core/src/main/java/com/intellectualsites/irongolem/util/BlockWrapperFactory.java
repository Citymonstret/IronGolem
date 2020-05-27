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

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public abstract class BlockWrapperFactory {

    private static BlockWrapperFactory factory;

    public static void setFactory(@NotNull final BlockWrapperFactory factory) {
        BlockWrapperFactory.factory = factory;
    }

    public static BlockWrapperFactory getFactory() {
        return factory;
    }

    public abstract BlockWrapper createWrapper(@NotNull final Block block);

}
