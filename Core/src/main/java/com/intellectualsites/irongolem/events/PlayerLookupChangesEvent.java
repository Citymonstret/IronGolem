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

package com.intellectualsites.irongolem.events;

import com.intellectualsites.irongolem.changes.ChangeQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link org.bukkit.event.player.PlayerEvent} lookup
 * {@link com.intellectualsites.irongolem.changes.Change changes}
 */
public class PlayerLookupChangesEvent extends QueryEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList handlerList() {
        return handlerList;
    }

    private boolean cancelled = false;

    public PlayerLookupChangesEvent(@NotNull final ChangeQuery query, @NotNull final Player player) {
        super(query, player);
    }

    @Override @NotNull public HandlerList getHandlers() {
        return handlerList();
    }

    @Override public boolean isCancelled() {
        return this.cancelled;
    }

    @Override public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

}
