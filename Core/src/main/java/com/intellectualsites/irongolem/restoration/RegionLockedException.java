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

package com.intellectualsites.irongolem.restoration;

import com.intellectualsites.irongolem.util.CuboidRegion;
import org.jetbrains.annotations.NotNull;

/**
 * Indicates that a {@link com.intellectualsites.irongolem.util.CuboidRegion}
 * is locked
 */
public class RegionLockedException extends Exception {

    private final CuboidRegion region;

    public RegionLockedException(@NotNull final CuboidRegion region) {
        this.region = region;
    }

    @NotNull public CuboidRegion getRegion() {
        return this.region;
    }

}
