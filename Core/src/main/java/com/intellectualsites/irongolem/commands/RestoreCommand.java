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

import com.intellectualsites.irongolem.IronGolem;
import com.intellectualsites.irongolem.changes.ChangeQuery;
import com.intellectualsites.irongolem.changes.ChangeReason;
import com.intellectualsites.irongolem.changes.PlayerSource;
import com.intellectualsites.irongolem.configuration.TranslatableMessage;
import com.intellectualsites.irongolem.players.IGPlayer;
import com.intellectualsites.irongolem.util.CuboidRegion;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class RestoreCommand extends SubCommand {

    private static final String[] ALIASES = new String[] { "restore", "r" };

    private final CommandFlags commandFlags = new CommandFlags();

    public RestoreCommand(@NotNull final IronGolem ironGolem) {
        super(ironGolem, ALIASES);
        commandFlags.registerFlag(CommandFlags.IntegerFlag.of("range"));
        commandFlags.registerFlag(CommandFlags.EnumFlag.of(ChangeReason.class, "reasons"));
    }

    @Override public void handleCommand(@NotNull final IGPlayer player, @NotNull final String[] args) {
        final Map<String, Object> flags = this.commandFlags.parseFlags(player.getPlayer(), args);
        final Collection<ChangeReason> reasons =
            (Collection<ChangeReason>) flags.getOrDefault("reasons", EnumSet.allOf(ChangeReason.class));
        if (!flags.containsKey("range")) {
            player.sendMessage(TranslatableMessage.of("command.missing.range"));
            return;
        }
        final int range = (int) flags.get("range");

        ChangeQuery.newQuery()
            .inWorld(player.getWorld())
            .inRegion(CuboidRegion.surrounding(player.getLocation(), range))
            .withReasons(reasons)
            .distinctValues()
            .queryChanges()
            .whenComplete(((changes, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    player.sendMessage(TranslatableMessage.of("query.failure"), "message", throwable.getMessage());
                } else {
                    player.getPlayer().sendMessage("yay, starting restoration");
                    IronGolem.getPlugin(IronGolem.class).getRestorationHandler().restore(changes, PlayerSource
                        .of(player), () -> player.getPlayer().sendMessage("am done"));
                }
            }));
    }

    @Override public List<String> getSuggestions(@NotNull final CommandSender sender,
        @NotNull final String[] args) {
        return this.commandFlags.completeFlags(sender, args);
    }

}
