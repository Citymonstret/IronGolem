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

package com.intellectualsites.irongolem.storage;

import com.intellectualsites.irongolem.changes.Change;
import com.intellectualsites.irongolem.changes.ChangeSubject;
import com.intellectualsites.irongolem.logging.ScheduledQueuingChangeLogger;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Objects;

/**
 * {@link com.intellectualsites.irongolem.logging.ChangeLogger} that logs to SQLite
 */
public class SQLiteLogger extends ScheduledQueuingChangeLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteLogger.class);

    private static final String DDL = 
        "create table if not exists `events`" + "(" + "\tevent_id INTEGER"
        + "\t\tconstraint events_pk" + "\t\t\tprimary key autoincrement,"
        + "\tworld VARCHAR(36) not null," + "\tx INTEGER not null," + "\ty INTEGER not null,"
        + "\tz INTEGER not null," + "\ttimestamp INTEGER not null,"
        + "\tsource VARCHAR(36) not null," + "\ttype varchar(16)," + "\t\"from\" TEXT,"
        + "\t\"to\" TEXT, reason varchar(64))";

    private final Object statementLock = new Object();

    private final File file;
    private Connection connection;
    private PreparedStatement statement;

    public SQLiteLogger(@NotNull final Plugin plugin, final int interval) throws Exception {
        super(plugin, interval, 128);
        Class.forName("org.sqlite.JDBC");
        this.file = new File(plugin.getDataFolder(), "storage.db");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Could not create storage.db");
            }
        }
    }

    @Override protected void startBatch() throws Exception {
        synchronized (this.statementLock) {
            this.statement = this.getConnection().prepareStatement(
                "INSERT INTO `events`(`world`, `x`, `y`, `z`, `timestamp`, `source`, `type`, `from`, `to`, `reason`)" +
                    " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
    }

    @Override protected void persist(@NotNull final Change change) throws Exception {
        synchronized (this.statementLock) {
            final Location location = change.getLocation();
            final ChangeSubject subject = change.getSubject();
            this.statement.setString(1, Objects.requireNonNull(location.getWorld()).getName());
            this.statement.setInt(2, location.getBlockX());
            this.statement.setInt(3, location.getBlockY());
            this.statement.setInt(4, location.getBlockZ());
            this.statement.setLong(5, change.getTimestamp());
            this.statement.setString(6, change.getSource().getName());
            this.statement.setString(7, subject.getType().name());
            this.statement.setString(8, subject.serializeFrom());
            this.statement.setString(9, subject.serializeTo());
            this.statement.setString(10, change.getReason().name());
            // Set params
            this.statement.addBatch();
        }
    }

    @Override protected void finishBatch() throws Throwable {
        synchronized (this.statementLock) {
            if (this.statement != null) {
                this.statement.executeBatch();
            }
            this.statement = null;
        }
    }

    private Connection getConnection() {
        return this.connection;
    }

    @Override public boolean startLogging() {
        final String connectionString = String.format("jdbc:sqlite:%s", this.file.getPath());
        try {
            this.connection = DriverManager.getConnection(connectionString);
        } catch (final Exception e) {
            LOGGER.error("Failed to initialize SQLite connection", e);
        }
        try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement(DDL)) {
            preparedStatement.executeUpdate();
        } catch (final Exception e) {
            LOGGER.error("Failed to create event table", e);
        }
        return this.connection != null;
    }

    @Override public void stopLogger() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (final Throwable throwable) {
                LOGGER.error("Failed to close SQLite connection", throwable);
            }
        }
    }

}
