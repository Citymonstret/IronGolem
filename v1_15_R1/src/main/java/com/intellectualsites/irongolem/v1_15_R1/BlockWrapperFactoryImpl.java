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
package com.intellectualsites.irongolem.v1_15_R1;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.intellectualsites.irongolem.util.BlockWrapper;
import com.intellectualsites.irongolem.util.BlockWrapperFactory;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.Chunk;
import net.minecraft.server.v1_15_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.TileEntity;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BlockWrapperFactoryImpl extends BlockWrapperFactory {

    public BlockWrapperFactoryImpl() {
        BlockWrapperFactory.setFactory(this);
    }

    @Override public BlockWrapper createWrapper(@NotNull Block block) {
        final Chunk chunk = ((CraftChunk) block.getChunk()).getHandle();
        final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        final TileEntity tileEntity = chunk.getTileEntity(blockPosition);
        byte[] blockState = new byte[0];
        if (tileEntity != null) {
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            tileEntity.save(nbtTagCompound);
            final ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
            try {
                NBTCompressedStreamTools.a(nbtTagCompound, byteArrayDataOutput);
                blockState = byteArrayDataOutput.toByteArray();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return new BlockWrapper(block.getBlockData(), blockState);
    }
}
