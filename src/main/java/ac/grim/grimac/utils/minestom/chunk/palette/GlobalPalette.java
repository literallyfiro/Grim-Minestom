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

/**
 * A global palette that maps 1:1.
 */
//TODO Equals & hashcode
public class GlobalPalette implements Palette {
    @Override
    public int size() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int stateToId(int state) {
        return state;
    }

    @Override
    public int idToState(int id) {
        return id;
    }
}
