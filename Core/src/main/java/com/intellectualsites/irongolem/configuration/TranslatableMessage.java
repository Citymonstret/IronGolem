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


package com.intellectualsites.irongolem.configuration;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public final class TranslatableMessage implements Message {

    private final String key;

    private TranslatableMessage(@NotNull final String key) {
        this.key = key;
    }

    /**
     * Create a new translatable message with a given message key
     *
     * @param key Message key
     * @return Message instance
     */
    public static TranslatableMessage of(@NotNull final String key) {
        return new TranslatableMessage(Preconditions.checkNotNull(key, "Key may not be null"));
    }

    /**
     * Get the translated message
     *
     * @return Translated message
     */
    public String toString() {
        return this.key;
    }

    @Override public String getMessage() {
        return MessageHandler.getInstance().getTranslation("prefix") +
            MessageHandler.getInstance().getTranslation(this.key);
    }

}
