/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2022 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ac.grim.grimac.utils.minestom.chunk;

import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.chunk.palette.DataPalette;
import ac.grim.grimac.utils.minestom.chunk.palette.PaletteType;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class CompensatedChunk {
    private static final int AIR = 0;

    private int blockCount;
    private @NotNull
    final DataPalette chunkData;
    private @NotNull
    final DataPalette biomeData;

    public CompensatedChunk() {
        this(0, DataPalette.createForChunk(), DataPalette.createForBiome());
    }

    public CompensatedChunk(final int blockCount, final @NotNull DataPalette chunkData, final @NotNull DataPalette biomeData) {
        this.blockCount = blockCount;
        this.chunkData = chunkData;
        this.biomeData = biomeData;
    }

    public static CompensatedChunk read(NetStreamInput in)  {
        short blockCount = in.readShort();

        DataPalette chunkPalette = DataPalette.read(in, PaletteType.CHUNK);
        DataPalette biomePalette = DataPalette.read(in, PaletteType.BIOME);
        return new CompensatedChunk(blockCount, chunkPalette, biomePalette);
    }

    public int getBlockId(int x, int y, int z) {
        return this.chunkData.get(x, y, z);
    }

    public MinestomWrappedBlockState get(int x, int y, int z) {
        return MinestomWrappedBlockState.getByGlobalId(getBlockId(x, y, z), true);
    }

    public void set(int x, int y, int z, int state) {
        int curr = this.chunkData.set(x, y, z, state);
        if (state != AIR && curr == AIR) {
            this.blockCount++;
        } else if (state == AIR && curr != AIR) {
            this.blockCount--;
        }
    }

    public boolean isEmpty() {
        return this.blockCount == 0;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    public @NotNull DataPalette getChunkData() {
        return chunkData;
    }

    public @NotNull DataPalette getBiomeData() {
        return biomeData;
    }
}
