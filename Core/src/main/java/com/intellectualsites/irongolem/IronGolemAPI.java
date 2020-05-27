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

package com.intellectualsites.irongolem;

import com.intellectualsites.irongolem.logging.ChangeLogger;
import com.intellectualsites.irongolem.players.PlayerManager;
import com.intellectualsites.irongolem.restoration.RestorationHandler;
import com.intellectualsites.irongolem.util.UsernameMapper;
import org.jetbrains.annotations.NotNull;

public interface IronGolemAPI {

    /**
     * Get the change logger implementation used
     * on the server
     *
     * @return Change logger
     */
    @NotNull ChangeLogger getChangeLogger();

    /**
     * Get the restoration handler implementation used
     * on the server
     *
     * @return Restoration handler
     */
    @NotNull RestorationHandler getRestorationHandler();

    /**
     * Get the username mapper
     *
     * @return Username mapper
     */
    @NotNull UsernameMapper getUsernameMapper();

    /**
     * Get the player manager
     *
     * @return Player manager
     */
    @NotNull PlayerManager getPlayerManager();

}
