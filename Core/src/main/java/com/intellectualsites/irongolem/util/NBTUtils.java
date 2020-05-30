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

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.CompoundTagBuilder;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NBTOutputStream;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class NBTUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NBTUtils.class);
    private static final CompoundTag EMPTY = CompoundTagBuilder.create().build();

    private NBTUtils() {
    }

    @NotNull public static byte[] compoundToBytes(@NotNull final CompoundTag tag) {
        if (tag.getValue().isEmpty()) {
            return new byte[0];
        }
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
             final NBTOutputStream nbtOutputStream = new NBTOutputStream(new GZIPOutputStream(byteArrayOutputStream))) {
            nbtOutputStream.writeNamedTag("IG", tag);
            return byteArrayOutputStream.toByteArray();
        } catch (final Exception e) {
            LOGGER.error("Failed to write compound", e);
        }
        return new byte[0];
    }

    @NotNull public static CompoundTag bytesToCompound(@NotNull final byte[] bytes) {
        if (bytes.length == 0) {
            return EMPTY;
        }
        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             final NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(byteArrayInputStream))) {
            return (CompoundTag) nbtInputStream.readNamedTag().getTag();
        } catch (final Exception e) {
            LOGGER.error("Failed to read compound", e);
        }
        return EMPTY;
    }

}
