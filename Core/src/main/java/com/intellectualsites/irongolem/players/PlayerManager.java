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

package com.intellectualsites.irongolem.players;

import com.google.common.base.Preconditions;
import com.intellectualsites.irongolem.IronGolem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager mainly responsible for handling {@link IGPlayer}
 */
public class PlayerManager {

    private final IronGolem ironGolem;
    private final Map<UUID, IGPlayer> playerMap = new HashMap<>();

    public PlayerManager(@NotNull final IronGolem ironGolem) {
        this.ironGolem = ironGolem;
    }

    /**
     * Get a player wrapper from a given Bukkit player
     *
     * @param player Bukkit player
     * @return Player wrapper
     */
    @NotNull public IGPlayer getPlayer(@NotNull final Player player) {
        Preconditions.checkNotNull(player, "Player may not be null");
        return playerMap.computeIfAbsent(player.getUniqueId(), uuid -> new IGPlayer(this, player));
    }

    /**
     * Remove a player
     *
     * @param player Player to remove
     */
    public void removePlayer(@NotNull final Player player) {
        Preconditions.checkNotNull(player, "Player may not be null");
        this.playerMap.remove(player.getUniqueId());
    }

}
