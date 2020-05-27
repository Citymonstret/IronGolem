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
import com.intellectualsites.irongolem.configuration.TranslatableMessage;
import com.intellectualsites.irongolem.players.IGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static final String[] HELP_ARGS = new String[] {"help"};

    private final Collection<SubCommand> subCommands = new LinkedList<>();

    private final IronGolem ironGolem;

    public CommandManager(@NotNull final IronGolem ironGolem) {
        this.ironGolem = ironGolem;
        this.registerSubCommand(new InspectorCommand(ironGolem));
        this.registerSubCommand(new LookupCommand(ironGolem));
        this.registerSubCommand(new RestoreCommand(ironGolem));
    }

    public void registerSubCommand(@NotNull final SubCommand subCommand) {
        this.subCommands.add(subCommand);
    }

    @Override public boolean onCommand(@NotNull final CommandSender sender,
        @NotNull final org.bukkit.command.Command command, @NotNull final String label,
        @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            // TODO: Deal with this
            return false;
        }

        final IGPlayer player = this.ironGolem.getPlayerManager().getPlayer((Player) sender);

        // If no args are provided, we force it to run the help command
        if (args.length == 0) {
            args = HELP_ARGS;
        }

        for (final SubCommand subCommand : this.subCommands) {
            if (subCommand.accepts(args[0])) {
                if (!player.hasPermission(subCommand.getMainAlias())) {
                    player.sendMessage(TranslatableMessage.of("command.not-permitted"));
                    return true;
                }
                final String[] newArgs = new String[args.length - 1];
                if (newArgs.length > 0) {
                    System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                }
                Bukkit.getScheduler().runTaskAsynchronously(this.ironGolem,
                    new CommandExecutionInstance(player, newArgs, subCommand));
                return true;
            }
        }

        player.sendMessage(TranslatableMessage.of("command.not-found"));
        return true;
    }

    @Override @Nullable public List<String> onTabComplete(@NotNull final CommandSender sender,
        @NotNull final org.bukkit.command.Command command, @NotNull final String alias,
        @NotNull final String[] args) {
        if (args.length == 1) {
            return this.subCommands.stream().map(SubCommand::getMainAlias)
                .collect(Collectors.toList());
        }
        for (final SubCommand subCommand : this.subCommands) {
            if (subCommand.accepts(args[0])) {
                final String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                return subCommand.getSuggestions(sender, newArgs);
            }
        }
        return Collections.emptyList();
    }

}
