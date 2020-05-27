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

package com.intellectualsites.irongolem.listeners;

import com.intellectualsites.irongolem.inspector.Inspector;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InspectorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Inspector.INSPECTOR_MATERIAL) {
            return;
        }
        final Inspector inspector = Inspector.fromItem(event.getPlayer(), event.getItem());
        if (inspector != null) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            if (event.getClickedBlock() != null) {
                inspector.inspectLocation(event.getClickedBlock().getLocation(), event.getAction().name().startsWith("RIGHT"));
            }
        }
    }

}
