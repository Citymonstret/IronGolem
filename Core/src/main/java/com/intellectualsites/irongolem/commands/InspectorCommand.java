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

import com.intellectualsites.irongolem.inspector.Inspector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class InspectorCommand extends SubCommand {

    private static final String[] ALIASES = new String[] {"inspector", "i"};

    private final CommandFlags commandFlags = new CommandFlags();

    public InspectorCommand() {
        super(ALIASES);
        this.commandFlags.registerFlag(CommandFlags.IntegerFlag.of("range", "r"));
        this.commandFlags.registerFlag(CommandFlags.IntegerFlag.of("limit", "l"));
    }

    @Override public void handleCommand(@NotNull final Player player, @NotNull final String[] args) {
        final Map<String, Object> parsedFlags = this.commandFlags.parseFlags(player, args);
        final int range = (int) parsedFlags.getOrDefault("range", 10);
        final int limit = (int) parsedFlags.getOrDefault("limit", 100);
        player.sendMessage("here u go");
        player.sendMessage("- range: " + range);
        player.sendMessage("- limit: " + limit);
        player.getInventory().addItem(Inspector.createInspector(player).buildItemStack());
    }

    @Override public List<String> getSuggestions(@NotNull final CommandSender sender,
        @NotNull final String[] args) {
        return this.commandFlags.completeFlags(sender, args);
    }

}
