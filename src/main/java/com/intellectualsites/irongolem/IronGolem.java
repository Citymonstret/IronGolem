package com.intellectualsites.irongolem;

import com.intellectualsites.irongolem.listeners.BlockListener;
import com.intellectualsites.irongolem.logging.ChangeLogger;
import com.intellectualsites.irongolem.storage.SQLiteLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IronGolem extends JavaPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(IronGolem.class);

    private ChangeLogger changeLogger;

    @Override public void onEnable() {
        if (!this.getDataFolder().exists() && !this.getDataFolder().mkdir()) {
            LOGGER.error("Failed to create data folder");
        }
        try {
            this.changeLogger = new SQLiteLogger(this, 20);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.changeLogger.startLogging();
        Bukkit.getPluginManager().registerEvents(new BlockListener(this.changeLogger), this);
    }

    @Override public void onDisable() {
        this.changeLogger.stopLogger();
    }

}
