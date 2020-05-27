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

/**
 * Something that was changed in a {@link Change}
 */
public interface ChangeSubject<D, S> {

    /**
     * Get the original state
     *
     * @return Original state
     */
    D getFrom();

    /**
     * Get the new state
     *
     * @return New state
     */
    D getTo();

    /**
     * Get the old subject state
     *
     * @return Subject state
     */
    S getOldState();

    /**
     * Get the new subject state
     *
     * @return Subject state
     */
    S getNewState();

    /**
     * Serialize the subject to a string
     *
     * @return Serialized string
     */
    String serializeFrom();

    /**
     * Serialize the subject to a string
     *
     * @return Serialized string
     */
    String serializeTo();

    /**
     * Serialize the old subject state
     *
     * @return Serialized string
     */
    byte[] serializeOldState();

    /**
     * Serialize the new subject state
     *
     * @return Serialized string
     */
    byte[] serializeNewState();

    /**
     * Get the change type
     *
     * @return Change type
     */
    @NotNull ChangeType getType();

}
