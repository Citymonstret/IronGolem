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

package com.intellectualsites.irongolem.logging;

import com.intellectualsites.irongolem.changes.Change;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A {@link java.util.Queue} backed {@link ChangeLogger}
 */
public abstract class QueuingChangeLogger implements ChangeLogger {

    private final Queue<Change> changeQueue;

    public QueuingChangeLogger() {
        this.changeQueue = new LinkedBlockingQueue<>();
    }

    @Override public void logChange(@NotNull final Change change) {
        this.changeQueue.add(change);
    }

    protected Change pollChange() {
        return this.changeQueue.poll();
    }

}
