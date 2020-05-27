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

package com.intellectualsites.irongolem.util;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellectualsites.irongolem.IronGolem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Utility that maps usernames to {@link java.util.UUID UUIDs} and vice versa
 */
public class UsernameMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsernameMapper.class);
    private static final String DDL =
        "CREATE TABLE IF NOT EXISTS `usercache` (uuid VARCHAR(32) NOT NULL,"
            + " username VARCHAR(32) NOT NULL, PRIMARY KEY (uuid))";

    private final Cache<UUID, String> usernameCache;
    private final Cache<String, UUID> uuidCache;

    private Connection connection;

    public UsernameMapper(@NotNull final IronGolem plugin) throws Exception {
        this.usernameCache = CacheBuilder.newBuilder().maximumSize(5000).build();
        this.uuidCache = CacheBuilder.newBuilder().maximumSize(5000).build();
        Class.forName("org.sqlite.JDBC");
        final File file = new File(plugin.getDataFolder(), "usercache.db");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Could not create database.db");
            }
        }
        final String connectionString = String.format("jdbc:sqlite:%s", file.getPath());
        try {
            this.connection = DriverManager.getConnection(connectionString);
        } catch (final Exception e) {
            LOGGER.error("Failed to initialize SQLite connection", e);
        }
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(DDL)) {
            preparedStatement.executeUpdate();
        } catch (final Exception e) {
            LOGGER.error("Failed to create event table", e);
        }
    }

    /**
     * Store a username mapping. This cannot be done on the main server thread.
     *
     * @param username Username
     * @param uuid     UUID
     */
    public void storeMapping(@NotNull final String username, @NotNull final UUID uuid) {
        ThreadUtils.catchSync("Mappings can only be stored asynchronously");
        Preconditions.checkNotNull(username, "Username may not be null");
        Preconditions.checkNotNull(uuid, "UUID may not be null");
        this.usernameCache.put(uuid, username);
        this.uuidCache.put(username, uuid);
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(
            "INSERT OR REPLACE INTO `usercache` (`uuid`, `username`) VALUES(?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (final Exception e) {
            LOGGER.error("Failed to store username mapping for UUID: {} and username: {}",
                uuid.toString(), username);
            LOGGER.error("Reason: ", e);
        }
    }

    /**
     * Get a username from a UUID. This cannot be done on the main thread.
     *
     * @param uuid UUID
     * @return Username, or null
     */
    @Nullable public String getUsername(@NotNull final UUID uuid) {
        ThreadUtils.catchSync("Usernames can only be retrieved asynchronously");
        Preconditions.checkNotNull(uuid, "UUID may not be null");
        try {
            return this.usernameCache.get(uuid, () -> {
                try (final PreparedStatement preparedStatement = this.connection
                    .prepareStatement("SELECT `username` FROM `usercache` WHERE `uuid` = ?")) {
                    preparedStatement.setString(1, uuid.toString());
                    try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getString("username");
                        }
                    }
                } catch (final Exception e) {
                    LOGGER.error("Failed to retrieve username for UUID = {}", uuid.toString());
                    LOGGER.error("Reason: ", e);
                }
                return null;
            });
        } catch (final ExecutionException e) {
            LOGGER.error("Failed to retrieve username for UUID = {}", uuid.toString());
            LOGGER.error("Reason: ", e);
        }
        return null;
    }

    /**
     * Get a UUID from a username. This cannot be done on the main thread.
     *
     * @param username Username
     * @return UUID, or null
     */
    @Nullable public UUID getUUID(@NotNull final String username) {
        ThreadUtils.catchSync("UUIDs can only be retrieved asynchronously");
        Preconditions.checkNotNull(username, "Username may not be null");
        try {
            return this.uuidCache.get(username, () -> {
                try (final PreparedStatement preparedStatement = this.connection
                    .prepareStatement("SELECT `uuid` FROM `usercache` WHERE `usernae` = ?")) {
                    preparedStatement.setString(1, username);
                    try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return UUID.fromString(resultSet.getString("uuid"));
                        }
                    }
                } catch (final Exception e) {
                    LOGGER.error("Failed to retrieve UUID for username = {}", username);
                    LOGGER.error("Reason: ", e);
                }
                return null;
            });
        } catch (final ExecutionException e) {
            LOGGER.error("Failed to retrieve UUID for username = {}", username);
            LOGGER.error("Reason: ", e);
        }
        return null;
    }

}
