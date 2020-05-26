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

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandHandlingOutput;
import com.intellectualsites.commands.CommandResult;
import com.intellectualsites.commands.bukkit.senders.ConsoleCaller;
import com.intellectualsites.commands.bukkit.senders.PlayerCaller;
import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.irongolem.IronGolem;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandManager extends Command implements CommandExecutor, TabCompleter {

    public CommandManager() {
        this.getManagerOptions().setPrintStacktrace(false);
        this.getManagerOptions().setUseAdvancedPermissions(false);
        this.getManagerOptions().setRequirePrefix(false);
        this.getManagerOptions().setUsageFormat("");
        if (CommodoreProvider.isSupported()) {
            final IronGolem plugin = IronGolem.getPlugin(IronGolem.class);
            final Commodore commodore = CommodoreProvider.getCommodore(plugin);
            CommodoreHandler.registerCompletions(commodore,
                Objects.requireNonNull(plugin.getCommand("irongolem")));
        }
        this.createCommand(new InspectorCommand());
    }

    @Override public CommandResult handle(final CommandCaller caller, final String[] args) {
        final CommandResult result = super.handle(caller, args);
        if (result.getCommandResult() != CommandHandlingOutput.SUCCESS) {
            // TODO: Fix
            caller.message("something went wrong i guess");
        }
        return result;
    }

    @Override public boolean onCommand(@NotNull final CommandSender sender,
        @NotNull final  org.bukkit.command.Command command, @NotNull final String label,
        @NotNull final String[] args) {
        final CommandCaller<?> commandSender;
        if (sender instanceof Player) {
            commandSender = new PlayerCaller((Player) sender);
        } else {
            commandSender = ConsoleCaller.instance;
        }
        return this.handle(commandSender, args).getCommandResult() == CommandHandlingOutput.SUCCESS;
    }

    @Override @Nullable public List<String> onTabComplete(@NotNull final CommandSender sender,
        final @NotNull org.bukkit.command.Command command, @NotNull final String alias,
        @NotNull String[] args) {
        return Collections.emptyList();
    }

}
