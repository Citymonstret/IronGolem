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

import com.intellectualsites.irongolem.changes.BlockSubject;
import com.intellectualsites.irongolem.changes.Change;
import com.intellectualsites.irongolem.changes.ChangeReason;
import com.intellectualsites.irongolem.changes.PlayerSource;
import com.intellectualsites.irongolem.logging.ChangeLogger;
import com.intellectualsites.irongolem.util.BlockWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.jetbrains.annotations.NotNull;

public class BlockListener implements Listener {

    private final ChangeLogger logger;

    public BlockListener(@NotNull final ChangeLogger logger) {
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Change change = Change.newBuilder().atLocation(event.getBlock().getLocation())
            .withSource(PlayerSource.of(event.getPlayer())).withSubject(BlockSubject.of(event.getBlock(),
                BlockWrapper.AIR)).withReason(ChangeReason.BLOCK_BREAK).build();
        logger.logChange(change);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Change change = Change.newBuilder().atLocation(event.getBlock().getLocation())
            .withSource(PlayerSource.of(event.getPlayer()))
            .withSubject(BlockSubject.of(BlockWrapper.of(event.getBlockReplacedState().getBlockData()),
                event.getBlockPlaced()))
            .withReason(ChangeReason.BLOCK_PLACE).build();
        logger.logChange(change);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(final BlockBurnEvent event) {

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(final BlockExplodeEvent event) {

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent event) {

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeaveDecay(final LeavesDecayEvent event) {

    }

}
