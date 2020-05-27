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

package com.intellectualsites.irongolem.listeners;

import com.intellectualsites.irongolem.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    private final IronGolem ironGolem;

    public PlayerListener(@NotNull final IronGolem ironGolem) {
        this.ironGolem = ironGolem;
    }

    @EventHandler public void onPreJoin(@NotNull final AsyncPlayerPreLoginEvent event) {
        this.ironGolem.getUsernameMapper().storeMapping(event.getName(), event.getUniqueId());
    }

    @EventHandler public void onJoin(@NotNull final PlayerJoinEvent event) {
        this.ironGolem.getPlayerManager().getPlayer(event.getPlayer());
    }

    @EventHandler public void onQuit(@NotNull final PlayerQuitEvent event) {
        this.ironGolem.getPlayerManager().removePlayer(event.getPlayer());
    }

}
