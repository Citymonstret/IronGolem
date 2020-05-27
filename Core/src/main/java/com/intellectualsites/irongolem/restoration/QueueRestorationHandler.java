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

package com.intellectualsites.irongolem.restoration;

import com.intellectualsites.irongolem.IronGolem;
import com.intellectualsites.irongolem.changes.BlockSubject;
import com.intellectualsites.irongolem.changes.Change;
import com.intellectualsites.irongolem.changes.ChangeReason;
import com.intellectualsites.irongolem.changes.ChangeSource;
import com.intellectualsites.irongolem.changes.ChangeSubject;
import com.intellectualsites.irongolem.changes.ChangeType;
import com.intellectualsites.irongolem.changes.Changes;
import com.intellectualsites.irongolem.queue.BukkitTaskManager;
import com.intellectualsites.irongolem.queue.GlobalBlockQueue;
import com.intellectualsites.irongolem.queue.LocalBlockQueue;
import com.intellectualsites.irongolem.queue.QueueProvider;
import com.intellectualsites.irongolem.queue.RunnableVal;
import com.intellectualsites.irongolem.queue.TaskManager;
import com.intellectualsites.irongolem.util.BlockWrapper;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Restoration handler based on the PlotSquared queue system
 */
public class QueueRestorationHandler implements RestorationHandler {

    /**
     * Cache of mapping x,y,z coordinates to the chunk array<br>
     * - Used for efficient world generation<br>
     */
    public static short[][] x_loc;
    public static short[][] y_loc;
    public static short[][] z_loc;
    public static short[][][] CACHE_I = null;
    public static short[][][] CACHE_J = null;

    public static void initCache() {
        if (x_loc == null) {
            x_loc = new short[16][4096];
            y_loc = new short[16][4096];
            z_loc = new short[16][4096];
            for (int i = 0; i < 16; i++) {
                int i4 = i << 4;
                for (int j = 0; j < 4096; j++) {
                    int y = i4 + (j >> 8);
                    int a = j - ((y & 0xF) << 8);
                    int z1 = a >> 4;
                    int x1 = a - (z1 << 4);
                    x_loc[i][j] = (short) x1;
                    y_loc[i][j] = (short) y;
                    z_loc[i][j] = (short) z1;
                }
            }
        }
        if (CACHE_I == null) {
            CACHE_I = new short[256][16][16];
            CACHE_J = new short[256][16][16];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 256; y++) {
                        short i = (short) (y >> 4);
                        short j = (short) ((y & 0xF) << 8 | z << 4 | x);
                        CACHE_I[y][x][z] = i;
                        CACHE_J[y][x][z] = j;
                    }
                }
            }
        }
    }

    private final IronGolem ironGolem;

    public QueueRestorationHandler(@NotNull final IronGolem plugin, final Class<? extends LocalBlockQueue> queue) {
        this.ironGolem = plugin;
        // Init the coordinate cache
        initCache();
        // Set the task manager implementation
        TaskManager.IMP = new BukkitTaskManager(plugin);
        // Set the global queue implementation
        GlobalBlockQueue.IMP = new GlobalBlockQueue(
            QueueProvider.of(queue, queue),
            1, 65 /* TODO: Make configurable */);
        GlobalBlockQueue.IMP.runTask();
    }

    @Override public void restore(@NotNull final Changes changes, @NotNull final ChangeSource source, @NotNull final Runnable completionTask) {
        if (!changes.isDistinct()) {
            throw new IllegalArgumentException("Only distinct change sets can be restored to");
        }
        // Calculate the changes
        final RunnableVal<List<Change>> changesTask = new RunnableVal<List<Change>>(new LinkedList<>()) {
            @Override public void run(final List<Change> value) {
                for (final Change change : changes.getChanges()) {
                    final ChangeSubject<?, ?> subject = change.getSubject();
                    if (subject.getType() != ChangeType.BLOCK) {
                        continue; // TODO: Fix this
                    }
                    final BlockSubject blockSubject = (BlockSubject) subject;
                    final Change newChange = new Change.ChangeBuilder()
                        .atLocation(change.getLocation())
                        .withSource(source)
                        .withReason(ChangeReason.RESTORATION)
                        .withSubject(BlockSubject.of(change.getLocation().getBlock(), BlockWrapper.of(blockSubject.getFrom()))).build();
                    value.add(newChange);
                }
            }
        };
        // We need to log the restoration changes
        this.ironGolem.getChangeLogger().logChanges(TaskManager.IMP.sync(changesTask));
        final LocalBlockQueue localBlockQueue = GlobalBlockQueue.IMP.getNewQueue(changes.getWorld().getName(), false);
        for (final Change change : changes.getChanges()) {
            final ChangeSubject<?, ?> subject = change.getSubject();
            if (subject.getType() != ChangeType.BLOCK) {
                continue; // TODO: Fix this
            }
            final BlockSubject blockSubject = (BlockSubject) subject;
            final Location location = change.getLocation();
            localBlockQueue
                .setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                    new BlockWrapper(blockSubject.getFrom(), blockSubject.serializeOldState()));
        }
        localBlockQueue.enqueue();
        GlobalBlockQueue.IMP.addEmptyTask(completionTask);
    }

}
