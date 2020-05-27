/*
 *       _____  _       _    _____                                _
 *      |  __ \| |     | |  / ____|                              | |
 *      | |__) | | ___ | |_| (___   __ _ _   _  __ _ _ __ ___  __| |
 *      |  ___/| |/ _ \| __|\___ \ / _` | | | |/ _` | '__/ _ \/ _` |
 *      | |    | | (_) | |_ ____) | (_| | |_| | (_| | | |  __/ (_| |
 *      |_|    |_|\___/ \__|_____/ \__, |\__,_|\__,_|_|  \___|\__,_|
 *                                    | |
 *                                    |_|
 *            PlotSquared plot management system for Minecraft
 *                  Copyright (C) 2020 IntellectualSites
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.intellectualsites.irongolem.queue;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlobalBlockQueue {

    public static GlobalBlockQueue IMP;
    private final int PARALLEL_THREADS;
    private final ConcurrentLinkedDeque<LocalBlockQueue> activeQueues;
    private final ConcurrentLinkedDeque<LocalBlockQueue> inactiveQueues;
    private final ConcurrentLinkedDeque<Runnable> runnables;
    private final AtomicBoolean running;
    private final int targetTime;
    private final QueueProvider provider;
    /**
     * Used to calculate elapsed time in milliseconds and ensure block placement doesn't lag the
     * server
     */
    private long last;
    private long secondLast;
    private double lastPeriod = 0;
    private final RunnableVal2<Long, LocalBlockQueue> SET_TASK =
        new RunnableVal2<Long, LocalBlockQueue>() {
            @Override public void run(Long free, LocalBlockQueue queue) {
                do {
                    boolean more = queue.next();
                    if (!more) {
                        if (inactiveQueues.size() == 0 && activeQueues.size() == 0) {
                            runEmptyTasks();
                        }
                        return;
                    }
                } while ((lastPeriod =
                    ((GlobalBlockQueue.this.secondLast = System.currentTimeMillis())
                        - GlobalBlockQueue.this.last)) < free);
            }
        };

    public GlobalBlockQueue(QueueProvider provider, int threads, int targetTime) {
        this.provider = provider;
        this.activeQueues = new ConcurrentLinkedDeque<>();
        this.inactiveQueues = new ConcurrentLinkedDeque<>();
        this.runnables = new ConcurrentLinkedDeque<>();
        this.running = new AtomicBoolean();
        this.targetTime = targetTime;
        this.PARALLEL_THREADS = threads;
    }

    public LocalBlockQueue getNewQueue(String world, boolean autoQueue) {
        LocalBlockQueue queue = provider.getNewQueue(world);
        if (autoQueue) {
            inactiveQueues.add(queue);
        }
        return queue;
    }

    public void runTask() {
        if (running.get()) {
            return;
        }
        running.set(true);
        TaskManager.runTaskRepeat(() -> {
            if (inactiveQueues.isEmpty() && activeQueues.isEmpty()) {
                lastPeriod = 0;
                GlobalBlockQueue.this.runEmptyTasks();
                return;
            }
            // Server laggy? Skip.
            if (lastPeriod > targetTime) {
                lastPeriod -= targetTime;
                return;
            }
            SET_TASK.value1 = 50 + Math.min(
                (50 + GlobalBlockQueue.this.last) - (GlobalBlockQueue.this.last =
                    System.currentTimeMillis()),
                GlobalBlockQueue.this.secondLast - System.currentTimeMillis());
            SET_TASK.value2 = GlobalBlockQueue.this.getNextQueue();
            if (SET_TASK.value2 == null) {
                return;
            }
            if (!Bukkit.isPrimaryThread()) {
                throw new IllegalStateException(
                    "It shouldn't be possible for placement to occur off the main thread");
            }
            try {
                if (PARALLEL_THREADS <= 1) {
                    SET_TASK.run();
                } else {
                    ArrayList<Thread> threads = new ArrayList<>();
                    for (int i = 0; i < PARALLEL_THREADS; i++) {
                        threads.add(new Thread(SET_TASK));
                    }
                    for (Thread thread : threads) {
                        thread.start();
                    }
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 1);
    }

    /**
     * TODO Documentation needed.
     *
     * @param queue todo
     */
    public void enqueue(LocalBlockQueue queue) {
        inactiveQueues.remove(queue);
        if (queue.size() > 0 && !activeQueues.contains(queue)) {
            activeQueues.add(queue);
        }
    }

    public LocalBlockQueue getNextQueue() {
        long now = System.currentTimeMillis();
        while (!activeQueues.isEmpty()) {
            LocalBlockQueue queue = activeQueues.peek();
            if (queue != null && queue.size() > 0) {
                queue.setModified(now);
                return queue;
            } else {
                activeQueues.poll();
            }
        }
        int size = inactiveQueues.size();
        if (size > 0) {
            Iterator<LocalBlockQueue> iter = inactiveQueues.iterator();
            try {
                int total = 0;
                LocalBlockQueue firstNonEmpty = null;
                while (iter.hasNext()) {
                    LocalBlockQueue queue = iter.next();
                    long age = now - queue.getModified();
                    total += queue.size();
                    if (queue.size() == 0) {
                        if (age > 60000) {
                            iter.remove();
                        }
                        continue;
                    }
                    if (firstNonEmpty == null) {
                        firstNonEmpty = queue;
                    }
                    if (total > 64) {
                        firstNonEmpty.setModified(now);
                        return firstNonEmpty;
                    }
                    if (age > 1000) {
                        queue.setModified(now);
                        return queue;
                    }
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isDone() {
        return activeQueues.size() == 0 && inactiveQueues.size() == 0;
    }

    public void addEmptyTask(final Runnable whenDone) {
        if (this.isDone()) {
            // Run
            this.runEmptyTasks();
            if (whenDone != null) {
                whenDone.run();
            }
            return;
        }
        if (whenDone != null) {
            this.runnables.add(whenDone);
        }
    }

    private synchronized void runEmptyTasks() {
        if (this.runnables.isEmpty()) {
            return;
        }
        final ConcurrentLinkedDeque<Runnable> tmp = new ConcurrentLinkedDeque<>(this.runnables);
        this.runnables.clear();
        for (final Runnable runnable : tmp) {
            runnable.run();
        }
    }

}
