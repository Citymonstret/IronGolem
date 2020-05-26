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

package com.intellectualsites.irongolem;

import com.intellectualsites.irongolem.commands.CommandManager;
import com.intellectualsites.irongolem.listeners.BlockListener;
import com.intellectualsites.irongolem.listeners.InspectorListener;
import com.intellectualsites.irongolem.logging.ChangeLogger;
import com.intellectualsites.irongolem.restoration.QueueRestorationHandler;
import com.intellectualsites.irongolem.restoration.RestorationHandler;
import com.intellectualsites.irongolem.storage.SQLiteLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class IronGolem extends JavaPlugin implements IronGolemAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(IronGolem.class);

    private ChangeLogger changeLogger;
    private RestorationHandler restorationHandler;

    @Override public void onEnable() {
        if (!this.getDataFolder().exists() && !this.getDataFolder().mkdir()) {
            LOGGER.error("Failed to create data folder");
        }
        try {
            this.changeLogger = new SQLiteLogger(this, 20);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        try {
            this.restorationHandler = new QueueRestorationHandler(this);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        if (this.changeLogger == null || !this.changeLogger.startLogging()) {
            LOGGER.error("Failed to start change logger");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            Bukkit.getPluginManager().registerEvents(new BlockListener(this.changeLogger), this);
            Bukkit.getPluginManager().registerEvents(new InspectorListener(), this);
            Objects.requireNonNull(getCommand("irongolem")).setExecutor(new CommandManager());
        }
        Bukkit.getServicesManager().register(IronGolemAPI.class, this, this,
            ServicePriority.Highest);
    }

    @NotNull public ChangeLogger getChangeLogger() {
        return this.changeLogger;
    }

    @NotNull @Override public RestorationHandler getRestorationHandler() {
        return this.restorationHandler;
    }

    @Override public void onDisable() {
        this.changeLogger.stopLogger();
    }

}
