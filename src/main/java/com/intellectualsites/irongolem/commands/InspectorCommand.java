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

import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.bukkit.plugin.PluginCommandBody;
import com.intellectualsites.commands.bukkit.senders.PlayerCaller;
import com.intellectualsites.irongolem.inspector.Inspector;

@CommandDeclaration(
    command = "inspector",
    usage = "/ig inspector",
    description = "Receive the inspector tool"
)
public class InspectorCommand extends PluginCommandBody {

    @Override public boolean onCommand(final CommandInstance instance) {
        final PlayerCaller playerCaller = (PlayerCaller) instance.getCaller();
        playerCaller.message("here u get tool");
        playerCaller.getPlayer().getInventory()
            .addItem(Inspector.createInspector(playerCaller.getPlayer()).buildItemStack());
        return true;
    }

}
