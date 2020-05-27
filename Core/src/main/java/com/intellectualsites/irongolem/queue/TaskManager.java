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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TaskManager {

    public static TaskManager IMP;

    public static void runTaskRepeat(@Nullable final Runnable runnable, final int interval) {
        if (runnable != null) {
            if (IMP == null) {
                throw new IllegalArgumentException("disabled");
            }
            IMP.taskRepeat(runnable, interval);
        }
    }

    public <T> T sync(@NotNull final RunnableVal<T> function) {
        return sync(function, Integer.MAX_VALUE);
    }

    public <T> T sync(@NotNull final RunnableVal<T> function, final int timeout) {
        if (Bukkit.isPrimaryThread()) {
            function.run();
            return function.value;
        }
        final AtomicBoolean running = new AtomicBoolean(true);
        final RuntimeExceptionRunnableVal<T> run =
            new RuntimeExceptionRunnableVal<>(function, running);
        TaskManager.IMP.task(run);
        try {
            synchronized (function) {
                while (running.get()) {
                    function.wait(timeout);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (run.value != null) {
            throw run.value;
        }
        return function.value;
    }

    public abstract void taskRepeat(@NotNull final Runnable runnable, final int interval);

    public abstract void task(@NotNull final Runnable runnable);

}
