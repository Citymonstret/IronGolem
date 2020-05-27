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

import com.intellectualsites.irongolem.configuration.Message;
import me.minidigger.minimessage.bungee.MiniMessageParser;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * {@link org.bukkit.entity.Player} wrapper class
 */
public class IGPlayer {

    private final PlayerManager playerManager;
    private final Player player;

    IGPlayer(@NotNull final PlayerManager playerManager, @NotNull final Player player) {
        this.playerManager = playerManager;
        this.player = player;
    }

    /**
     * Get the player manager that created
     * this player wrapper
     *
     * @return Player manager
     */
    @NotNull public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    /**
     * Get the Bukkit player that this
     * instance wraps
     *
     * @return Bukkit player
     */
    @NotNull public Player getPlayer() {
        return this.player;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IGPlayer igPlayer = (IGPlayer) o;
        return getPlayer().equals(igPlayer.getPlayer());
    }

    @Override public int hashCode() {
        return Objects.hash(getPlayer());
    }

    /**
     * Send a message to the player
     *
     * @param message      Message to send
     * @param placeholders Alternating placeholder keys and values
     */
    public void sendMessage(@NotNull final Message message, @NotNull final String... placeholders) {
        this.player.spigot().sendMessage(MiniMessageParser.parseFormat(message.getMessage(), placeholders));
    }

    /**
     * Check if a player has a specific permission
     *
     * @param permission Permission, will be prefixed with "irongolem."
     * @return True if the player has the specified permission
     */
    public boolean hasPermission(@NotNull final String permission) {
        return this.player.hasPermission(permission);
    }

    /**
     * Get the player UUID
     *
     * @return Player UUID
     */
    @NotNull public UUID getUUID() {
        return this.player.getUniqueId();
    }

    /**
     * Get the world the player is currently in
     *
     * @return Player world
     */
    @NotNull public World getWorld() {
        return this.player.getWorld();
    }

    /**
     * Get the location of the player in
     * the world
     *
     * @return Player location
     */
    @NotNull public Vector getLocation() {
        return this.player.getLocation().toVector();
    }

}
