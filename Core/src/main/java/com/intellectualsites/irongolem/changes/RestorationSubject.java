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

package com.intellectualsites.irongolem.changes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestorationSubject implements ChangeSubject<Integer, Void> {

    private final ChangeType type;
    private final int id;

    private RestorationSubject(@NotNull final ChangeType type, final int id) {
        this.type = type;
        this.id = id;
    }

    public static RestorationSubject of(@NotNull final ChangeType type, final int id) {
        return new RestorationSubject(type, id);
    }

    @Override public Integer getFrom() {
        return -1;
    }

    @Override public Integer getTo() {
        return this.id;
    }

    @Nullable @Override public Void getOldState() {
        return null;
    }

    @Nullable @Override public Void getNewState() {
        return null;
    }

    @Override public String serializeFrom() {
        return "-1";
    }

    @Override public String serializeTo() {
        return Integer.toString(this.id);
    }

    @Override public byte[] serializeOldState() {
        return new byte[0];
    }

    @Override public byte[] serializeNewState() {
        return new byte[0];
    }

    @Override public @NotNull ChangeType getType() {
        return this.type;
    }

}
