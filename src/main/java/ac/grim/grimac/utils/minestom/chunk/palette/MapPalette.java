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

/*
 * This class was taken from MCProtocolLib.
 *
 * https://github.com/Steveice10/MCProtocolLib
 */

package ac.grim.grimac.utils.minestom.chunk.palette;

import ac.grim.grimac.utils.minestom.chunk.NetStreamInput;

import java.util.HashMap;

/**
 * A palette backed by a map.
 */
public class MapPalette implements Palette {
    private final int maxId;

    private final int[] idToState;
    // TODO: Can we use fastutils here?
    private final HashMap<Object, Integer> stateToId = new HashMap<>();
    private int nextId = 0;

    public MapPalette(int bitsPerEntry) {
        this.maxId = (1 << bitsPerEntry) - 1;

        this.idToState = new int[this.maxId + 1];
    }

    public MapPalette(int bitsPerEntry, NetStreamInput in) {
        this(bitsPerEntry);

        int paletteLength = in.readVarInt();
        for (int i = 0; i < paletteLength; i++) {
            int state = in.readVarInt();
            this.idToState[i] = state;
            this.stateToId.putIfAbsent(state, i);
        }
        this.nextId = paletteLength;
    }

    @Override
    public int size() {
        return this.nextId;
    }

    @Override
    public int stateToId(int state) {
        Integer id = this.stateToId.get(state);
        if (id == null && this.size() < this.maxId + 1) {
            id = this.nextId++;
            this.idToState[id] = state;
            this.stateToId.put(state, id);
        }

        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public int idToState(int id) {
        if (id >= 0 && id < this.size()) {
            return this.idToState[id];
        } else {
            return 0;
        }
    }
}
