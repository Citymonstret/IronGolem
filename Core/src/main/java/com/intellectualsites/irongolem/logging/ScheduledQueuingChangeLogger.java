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

package com.intellectualsites.irongolem.logging;

import com.intellectualsites.irongolem.changes.Change;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link QueuingChangeLogger} that runs on a fixed schedule
 */
public abstract class ScheduledQueuingChangeLogger extends QueuingChangeLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledQueuingChangeLogger.class);
    private final BukkitRunnable bukkitRunnable;

    private final int maxBatchSize;
    private final Object taskLock = new Object();

    /**
     * Create a new logger
     *
     * @param plugin Plugin that will schedule the task
     * @param interval Interval (in ticks)
     * @param maxBatchSize The maximum amount of change
     */
    public ScheduledQueuingChangeLogger(final Plugin plugin, final long interval, final int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        this.bukkitRunnable = new ChangeLoggerTask();
        // Schedule the task
        this.bukkitRunnable.runTaskTimerAsynchronously(plugin, 0, interval);
    }

    /**
     * Indicate that a batch should be started. This may
     * or may not do anything, depending on if the implementation
     * supports batching
     */
    protected abstract void startBatch() throws Exception;

    /**
     * Persist the change, or add it to started batch
     * if the scheduler supports batching.
     *
     * @param change Change to persist
     */
    protected abstract void persist(@NotNull final Change change) throws Exception;

    /**
     * Finish a started batch
     *
     * @throws Throwable If anything goes wrong
     */
    protected abstract void finishBatch() throws Throwable;

    /**
     * Get the maximum amount of changes that are
     * allowed to be batched together
     *
     * @return Maximum batch count
     */
    public int getMaxBatchSize() {
        return this.maxBatchSize;
    }

    /**
     * Get the runnable
     *
     * @return Bukkit runnable
     */
    public BukkitRunnable getBukkitRunnable() {
        return this.bukkitRunnable;
    }

    private final class ChangeLoggerTask extends BukkitRunnable {

        @Override public void run() {
            synchronized (taskLock) {
                boolean batchStarted = false;
                int loggedChanges = 0;
                Change change;
                while ((change = pollChange()) != null) {
                    // Start the batch
                    if (!batchStarted) {
                        try {
                            startBatch();
                        } catch (final Exception e) {
                            LOGGER.error("Failed to create batch. Aborting.", e);
                            return;
                        }
                        batchStarted = true;
                    }
                    // Persist the change
                    try {
                        persist(change);
                    } catch (final Exception e) {
                        LOGGER.error("Failed to persist change {}", change);
                        LOGGER.error("Cause: ", e);
                    }
                    // Finish the batch
                    if (++loggedChanges >= getMaxBatchSize()) {
                        try {
                            finishBatch();
                        } catch (final Throwable throwable) {
                            LOGGER.error("Failed to save batch", throwable);
                        }
                        batchStarted = false;
                    }
                }
                // This means there's an unfinished batch
                if (batchStarted) {
                    try {
                        finishBatch();
                    } catch (final Throwable throwable) {
                        LOGGER.error("Failed to save batch", throwable);
                    }
                }
            }
        }

    }

}
