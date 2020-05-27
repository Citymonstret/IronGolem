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
import com.intellectualsites.irongolem.changes.ChangeQuery;
import com.intellectualsites.irongolem.changes.ChangeReason;
import com.intellectualsites.irongolem.changes.ChangeSource;
import com.intellectualsites.irongolem.changes.ChangeSubject;
import com.intellectualsites.irongolem.logging.ScheduledQueuingChangeLogger;
import com.intellectualsites.irongolem.util.CuboidRegion;
import com.intellectualsites.irongolem.util.SourceFactory;
import com.intellectualsites.irongolem.util.SubjectFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * {@link com.intellectualsites.irongolem.logging.ChangeLogger} that logs to SQLite
 */
public class SQLiteLogger extends ScheduledQueuingChangeLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteLogger.class);

    private static final String DDL =
         "create table if not exists `events`(`event_id` INTEGER constraint `events_pk` PRIMARY KEY autoincrement, "
       + "`world` VARCHAR(36) NOT NULL, `x` INTEGER NOT NULL, `y` INTEGER NOT NULL, `z` INTEGER NOT NULL, "
       + "`timestamp` INTEGER NOT NULL, `source` VARCHAR(36) NOT NULL, `type` VARCHAR(16), `from` TEXT, "
       + "`to` TEXT, `old_state` BLOB, `new_state` BLOB, `reason` VARCHAR(64))";

    private final Object statementLock = new Object();
    private final SourceFactory sourceFactory = new SourceFactory();
    private final SubjectFactory subjectFactory = new SubjectFactory();

    private final File file;
    private final Plugin plugin;
    private Connection connection;
    private PreparedStatement statement;

    public SQLiteLogger(@NotNull final Plugin plugin, final int interval) throws Exception {
        super(plugin, interval, 128);
        Class.forName("org.sqlite.JDBC");
        this.file = new File(plugin.getDataFolder(), "database.db");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Could not create database.db");
            }
        }
        this.plugin = plugin;
    }

    @Override protected void startBatch() throws Exception {
        synchronized (this.statementLock) {
            this.statement = this.getConnection().prepareStatement(
                "INSERT INTO `events`(`world`, `x`, `y`, `z`, `timestamp`, `source`, `type`, `from`, `to`, `old_state`, `new_state`, `reason`)"
                    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
    }

    @Override protected void persist(@NotNull final Change change) throws Exception {
        synchronized (this.statementLock) {
            final Location location = change.getLocation();
            final ChangeSubject<?, ?> subject = change.getSubject();
            this.statement.setString(1, Objects.requireNonNull(location.getWorld()).getName());
            this.statement.setInt(2, location.getBlockX());
            this.statement.setInt(3, location.getBlockY());
            this.statement.setInt(4, location.getBlockZ());
            this.statement.setLong(5, change.getTimestamp());
            this.statement.setString(6, change.getSource().getName());
            this.statement.setString(7, subject.getType().name());
            this.statement.setString(8, subject.serializeFrom());
            this.statement.setString(9, subject.serializeTo());
            this.statement.setBytes(10, subject.serializeOldState());
            this.statement.setBytes(11, subject.serializeNewState());
            this.statement.setString(12, change.getReason().name());
            // Set params
            this.statement.addBatch();
        }
    }

    @Override
    public CompletableFuture<List<Change>> queryChanges(@NotNull final ChangeQuery query) {
        final CompletableFuture<List<Change>> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final List<Change> changes = new LinkedList<>();
            synchronized (this.statementLock) {
                try {
                    final CuboidRegion region = query.getRegion();

                    final StringBuilder builder = new StringBuilder("SELECT * FROM `events` WHERE `world` = ? AND `x` >= ? AND `x` <= ? AND `y` >= ? AND `y` <= ? AND `z` >= ? AND `z` <= ?");
                    if (query.shouldUseDistinct()) {
                        builder.append(" AND `event_id` IN (SELECT MIN(`event_id`) FROM `events` WHERE `world` = ? AND `x` >= ? AND `x` <= ? AND `y` >= ? AND `y` <= ? AND `z` >= ? AND `z` <= ? GROUP BY `world`, `x`, `y`, `z`)");
                    }

                    // Append reasons
                    builder.append(" AND `reason` IN (");
                    final Iterator<ChangeReason> reasons = query.getReasons().iterator();
                    while (reasons.hasNext()) {
                        builder.append('\'').append(reasons.next().name()).append('\'');
                        if (reasons.hasNext()) {
                            builder.append(", ");
                        }
                    }
                    builder.append(")");

                    if (query.getChangeSource() != null) {
                        builder.append(" AND `source` = ?");
                    }

                    builder.append(" LIMIT ?");

                    try (final PreparedStatement statement = this.getConnection().prepareStatement(builder.toString())) {
                        int index = 1;
                        statement.setString(index++, query.getWorld().getName());
                        statement.setInt(index++, region.getMinimumPoint().getBlockX());
                        statement.setInt(index++, region.getMaximumPoint().getBlockX());
                        statement.setInt(index++, region.getMinimumPoint().getBlockY());
                        statement.setInt(index++, region.getMaximumPoint().getBlockY());
                        statement.setInt(index++, region.getMinimumPoint().getBlockZ());
                        statement.setInt(index++, region.getMaximumPoint().getBlockZ());
                        if (query.shouldUseDistinct()) {
                            statement.setString(index++, query.getWorld().getName());
                            statement.setInt(index++, region.getMinimumPoint().getBlockX());
                            statement.setInt(index++, region.getMaximumPoint().getBlockX());
                            statement.setInt(index++, region.getMinimumPoint().getBlockY());
                            statement.setInt(index++, region.getMaximumPoint().getBlockY());
                            statement.setInt(index++, region.getMinimumPoint().getBlockZ());
                            statement.setInt(index++, region.getMaximumPoint().getBlockZ());
                        }
                        if (query.getChangeSource() != null) {
                            statement.setString(index++, query.getChangeSource().getName());
                        }
                        statement.setInt(index, query.getLimit());
                        try (final ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                final Location location = new Location(Bukkit.getWorld(resultSet.getString("world")),
                                    resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                                final ChangeSource source = this.sourceFactory.getSource(resultSet.getString("source"));
                                if (source == null) {
                                    LOGGER.warn("Skipping change because of invalid source: {}", source);
                                    continue;
                                }
                                final byte[] oldState = resultSet.getBytes("old_state");
                                final byte[] newState = resultSet.getBytes("new_state");
                                final ChangeSubject<?, ?> subject = this.subjectFactory.getSubject(resultSet.getString("type"),
                                    resultSet.getString("from"), resultSet.getString("to"), oldState, newState);
                                if (subject == null) {
                                    LOGGER.warn("Skipping change because of invalid subject");
                                    continue;
                                }
                                final Change change = Change.newBuilder()
                                    .atLocation(location)
                                    .atTime(resultSet.getLong("timestamp"))
                                    .withSource(source)
                                    .withReason(ChangeReason.valueOf(resultSet.getString("reason")))
                                    .withSubject(subject)
                                    .build();
                                changes.add(change);
                            }
                        }
                    }
                } catch (final SQLException throwable) {
                    future.completeExceptionally(throwable);
                    return;
                }
            }
            future.complete(changes);
        }); return future;
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
        try (final PreparedStatement preparedStatement = this.getConnection()
            .prepareStatement(DDL)) {
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
