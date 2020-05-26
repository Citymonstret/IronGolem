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

package com.intellectualsites.irongolem.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class SubCommand {

    private final String[] aliases;

    public SubCommand(@NotNull final String[] aliases) {
        this.aliases = aliases;
    }

    public boolean accepts(@NotNull final String name) {
        for (final String alias : this.aliases) {
            if (alias.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public String getMainAlias() {
        return this.aliases[0];
    }

    public List<String> getSuggestions(@NotNull final CommandSender sender, @NotNull final String[] args) {
        return Collections.emptyList();
    }

    public abstract void handleCommand(@NotNull final Player player, @NotNull final String[] args);


}
