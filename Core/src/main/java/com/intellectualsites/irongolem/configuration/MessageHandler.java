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

package com.intellectualsites.irongolem.configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.intellectualsites.irongolem.IronGolem;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MessageHandler {

    private static final Gson GSON =
        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private static MessageHandler instance;
    private final Map<String, String> messages = Maps.newHashMap();

    public MessageHandler(@NotNull final IronGolem ironGolem) {
        instance = this;
        ironGolem.saveResource("messages_en.json", false);
        try (final JsonReader reader = GSON.newJsonReader(Files
            .newReader(new File(ironGolem.getDataFolder(), "messages_en.json"),
                StandardCharsets.UTF_8))) {
            final JsonObject object = GSON.fromJson(reader, JsonObject.class);
            for (final Map.Entry<String, JsonElement> elements : object.entrySet()) {
                messages.put(elements.getKey(), elements.getValue().getAsString());
            }
        } catch (final IOException e) {
            LOGGER.error("Failed to load messages", e);
        }
    }

    public static MessageHandler getInstance() {
        return instance;
    }

    /**
     * Get a translation from a translation key
     *
     * @param key Translation Key
     * @return Translation
     * @throws IllegalArgumentException If the translation does not exist
     */
    @NotNull public String getTranslation(@NotNull final String key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        final String value = this.messages.get(key);
        if (value == null) {
            throw new IllegalArgumentException("There is no message with that key: " + key);
        }
        return value;
    }

}
