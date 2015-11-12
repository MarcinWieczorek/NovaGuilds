/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.LoggerUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.command.CommandSender;

public class CommandAdminReload implements Executor {
	private final Commands command;

	public CommandAdminReload(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}
		
		Message.CHAT_RELOAD_RELOADING.send(sender);

		//Remove holograms
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			for(Hologram h : HologramsAPI.getHolograms(plugin)) {
				h.delete();
			}
		}

		//plugin's vars from config
		plugin.getConfigManager().reload();
		Message.CHAT_RELOAD_CONFIG.send(sender);

		//Connecting to database
		if(!plugin.getDatabaseManager().isConnected()) {
			if(plugin.getConfigManager().getDataStorageType() == DataStorageType.MYSQL) {
				plugin.getDatabaseManager().connectToMysql();
			}
			else if(plugin.getConfigManager().getDataStorageType() == DataStorageType.SQLITE) {
				plugin.getDatabaseManager().connectToSQLite();
			}
		}

		plugin.getDatabaseManager().mysqlReload();
		Message.CHAT_RELOAD_MYSQL.send(sender);

		//messages
		plugin.getMessageManager().load();
		Message.CHAT_RELOAD_MESSAGES.send(sender);

		//regions
		plugin.getRegionManager().load();
		Message.CHAT_RELOAD_REGIONS.send(sender);

		//guilds
		plugin.getGuildManager().load();
		Message.CHAT_RELOAD_GUILDS.send(sender);

		//players
		plugin.getPlayerManager().load();
		Message.CHAT_RELOAD_PLAYERS.send(sender);

		//groups
		plugin.getGroupManager().load();
		Message.CHAT_RELOAD_GROUPS.send(sender);

		LoggerUtils.info("Post checks running");
		plugin.getGuildManager().postCheck();
		plugin.getRegionManager().postCheck();

		//all done
		Message.CHAT_RELOAD_RELOADED.send(sender);
	}
}