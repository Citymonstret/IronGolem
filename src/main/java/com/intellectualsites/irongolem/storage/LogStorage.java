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

package com.intellectualsites.irongolem.storage;

import com.intellectualsites.irongolem.changes.Change;
import com.intellectualsites.irongolem.changes.ChangeQuery;
import com.intellectualsites.irongolem.logging.ChangeLogger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link LogStorage} is any object that is able to persist
 * {@link Change changes}
 * and retrieve them at a later stage
 */
public interface LogStorage extends ChangeLogger {

    /**
     * Query for changes
     *
     * @param query Query
     * @return Future that completes with the changes. This will never return null,
     *         but may complete exceptionally if anything goes wrong.
     */
    CompletableFuture<Collection<Change>> queryChanges(@NotNull final ChangeQuery query);

}
