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

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A "change" is any action that IronGolem can log, and restore
 */
public class Change {

    private final ChangeSource source;
    private final Location location;
    private final ChangeSubject<?, ?> subject;
    private final ChangeReason reason;
    private final long timestamp;

    private Change(@NotNull final ChangeSource source, @NotNull final Location location,
        @NotNull final ChangeSubject<?, ?> subject, @NotNull final ChangeReason reason, final long timestamp) {
        this.source = source;
        this.location = location;
        this.subject = subject;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public static ChangeBuilder newBuilder() {
        return new ChangeBuilder();
    }

    /**
     * Get the source of the change
     *
     * @return Change source
     */
    @NotNull public ChangeSource getSource() {
        return this.source;
    }

    /**
     * Get the change location
     *
     * @return Change location
     */
    @NotNull public Location getLocation() {
        return this.location;
    }

    /**
     * Get the subject of the change
     *
     * @return Change subject
     */
    @NotNull public ChangeSubject<?, ?> getSubject() {
        return this.subject;
    }

    /**
     * Get the change reason
     *
     * @return Change reason
     */
    @NotNull public ChangeReason getReason() {
        return this.reason;
    }

    /**
     * Get the event time stamp
     *
     * @return Time stamp
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    public static final class ChangeBuilder {

        private ChangeSource source;
        private Location location;
        private ChangeSubject<?, ?> subject;
        private ChangeReason reason;
        private long time = System.currentTimeMillis();

        @NotNull public ChangeBuilder withSource(@NotNull final ChangeSource source) {
            this.source = Preconditions.checkNotNull(source, "Source cannot be null");
            return this;
        }

        @NotNull public ChangeBuilder atLocation(@NotNull final Location location) {
            this.location = Preconditions.checkNotNull(location, "Location may not be null");
            return this;
        }

        @NotNull public ChangeBuilder withSubject(@NotNull final ChangeSubject<?, ?> subject) {
            this.subject = Preconditions.checkNotNull(subject, "Subject may not be null");
            return this;
        }

        @NotNull public ChangeBuilder withReason(@NotNull final ChangeReason reason) {
            this.reason = Preconditions.checkNotNull(reason, "Reason may not be null");
            return this;
        }

        @NotNull public ChangeBuilder atTime(final long time) {
            this.time = time;
            return this;
        }

        public Change build() {
            Preconditions.checkNotNull(source, "Source needs to be set");
            Preconditions.checkNotNull(location, "Location needs to be set");
            Preconditions.checkNotNull(location, "Subject may not be null");
            Preconditions.checkNotNull(reason, "Reason may not be null");
            return new Change(this.source, this.location, this.subject, this.reason, this.time);
        }

    }

}
