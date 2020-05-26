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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.commodore.Commodore;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

public class CommodoreHandler {

    private CommodoreHandler() {
    }

    public static void registerCompletions(@NotNull final Commodore commodore, @NotNull final
        PluginCommand command) {
        commodore.register(command, LiteralArgumentBuilder.literal("irongolem")
            .then(LiteralArgumentBuilder.literal("inspector"))
        );
    }

}
