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

package com.intellectualsites.irongolem.restoration;

import com.intellectualsites.irongolem.changes.ChangeSource;
import com.intellectualsites.irongolem.changes.Changes;
import org.jetbrains.annotations.NotNull;

/**
 * Handler responsible for restoring {@link com.intellectualsites.irongolem.changes.Change changes}
 */
public interface RestorationHandler {

    /**
     * Restore a list of changes by reverting the states of the subjects
     * to the "from" state.
     * <p>
     * This may do all of the updates at once, or it may schedule tasks
     * that run over an extended amount of time. Because of this, there is
     * no guarantee that the restoration will finish immediately.
     *
     * @param changes        Changes to restore to
     * @param source         Source of the restoration
     * @param completionTask Task that runs when the restoration has been finished
     */
    void restore(@NotNull final Changes changes, @NotNull final ChangeSource source, @NotNull final Runnable completionTask);

}
