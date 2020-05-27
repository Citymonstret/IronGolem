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
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class QueryEvent extends PlayerEvent {

    private final ChangeQuery query;

    protected QueryEvent(@NotNull final ChangeQuery query, @NotNull final Player player) {
        super(player);
        this.query = query;
    }

    /**
     * Get the query. Modifications on the query
     * will affect the outcome of the action
     *
     * @return The change query
     */
    @NotNull public final ChangeQuery getQuery() {
        return this.query;
    }

}
