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
import com.intellectualsites.irongolem.changes.Change;
import com.intellectualsites.irongolem.changes.ChangeQuery;
import com.intellectualsites.irongolem.changes.ChangeReason;
import com.intellectualsites.irongolem.changes.ChangeSubject;
import com.intellectualsites.irongolem.configuration.TranslatableMessage;
import com.intellectualsites.irongolem.events.PlayerLookupChangesEvent;
import com.intellectualsites.irongolem.players.IGPlayer;
import com.intellectualsites.irongolem.util.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class LookupCommand extends SubCommand {

    private static final String[] ALIASES = new String[] { "lookup", "l" };

    private final CommandFlags commandFlags = new CommandFlags();

    public LookupCommand(@NotNull final IronGolem ironGolem) {
        super(ironGolem, ALIASES);
        commandFlags.registerFlag(CommandFlags.IntegerFlag.of("range"));
        commandFlags.registerFlag(CommandFlags.EnumFlag.of(ChangeReason.class, "reasons"));
        commandFlags.registerFlag(CommandFlags.BooleanFlag.of("distinct"));
    }

    @Override public void handleCommand(@NotNull final IGPlayer player, @NotNull final String[] args) {
        final Map<String, Object> flags = this.commandFlags.parseFlags(player.getPlayer(), args);
        final Collection<ChangeReason> reasons =
            (Collection<ChangeReason>) flags.getOrDefault("reasons", EnumSet.allOf(ChangeReason.class));
        if (!flags.containsKey("range")) {
            player.sendMessage(TranslatableMessage.of("command.missing.range"));
            return;
        }
        final boolean distinct = (boolean) flags.getOrDefault("distinct", false);
        final int range = (int) flags.get("range");

        final ChangeQuery query = ChangeQuery.newQuery()
            .inWorld(player.getWorld())
            .inRegion(CuboidRegion.surrounding(player.getLocation(), range))
            .withReasons(reasons);
        if (distinct) {
            query.distinctValues();
        }
        final PlayerLookupChangesEvent
            playerLookupChangesEvent = new PlayerLookupChangesEvent(query, player.getPlayer());
        Bukkit.getPluginManager().callEvent(playerLookupChangesEvent);
        if (playerLookupChangesEvent.isCancelled()) {
            return;
        }
        query.queryChanges()
            .whenComplete(((changes, throwable) -> {
            if (throwable != null) {
                // TODO FIX
                throwable.printStackTrace();
                player.sendMessage(TranslatableMessage.of("query.failure"), "message", throwable.getMessage());
            } else {
                player.getPlayer().sendMessage("changes at that loc");
                for (final Change change : changes.getChanges()) {
                    final ChangeSubject<?, ?> subject = change.getSubject();
                    player.getPlayer().sendMessage(String
                        .format("- %s -> %s at %d", subject.serializeFrom(), subject.serializeTo(), change.getTimestamp()));
                }
            }
        }));
    }

    @Override public List<String> getSuggestions(@NotNull final CommandSender sender,
        @NotNull final String[] args) {
        return this.commandFlags.completeFlags(sender, args);
    }

}
