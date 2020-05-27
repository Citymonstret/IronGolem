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

package com.intellectualsites.irongolem.util;

import org.bukkit.Bukkit;

public final class ThreadUtils {

    private ThreadUtils() {
    }

    /**
     * Throws {@link IllegalStateException} if the method
     * is called from the server main thread
     *
     * @param message Message describing the issue
     */
    public static void catchSync(final String message) {
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Throws {@link IllegalStateException} if the method
     * is not called from the server main thread
     *
     * @param message Message describing the issue
     */
    public static void catchAsync(final String message) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException(message);
        }
    }

}
