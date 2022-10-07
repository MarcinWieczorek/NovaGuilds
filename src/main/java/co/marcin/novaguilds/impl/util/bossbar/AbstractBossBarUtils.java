/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2018 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.util.bossbar;

import co.marcin.novaguilds.api.util.IBossBarUtils;
import co.marcin.novaguilds.util.CompatibilityUtils;
import org.bukkit.entity.Player;

public abstract class AbstractBossBarUtils implements IBossBarUtils {
    @Override
    public void setMessage(String message) {
        for(Player player : CompatibilityUtils.getOnlinePlayers()) {
            setMessage(player, message);
        }
    }

    @Override
    public void setMessage(String message, float percent) {
        for(Player player : CompatibilityUtils.getOnlinePlayers()) {
            setMessage(player, message, percent);
        }
    }

    @Override
    public void setMessage(String message, int seconds) {
        for(Player player : CompatibilityUtils.getOnlinePlayers()) {
            setMessage(player, message, seconds);
        }
    }
}
