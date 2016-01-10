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

package co.marcin.novaguilds.command.region;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandRegionBuy implements CommandExecutor, Executor {
	private final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Command command = Command.REGION_BUY;

	public CommandRegionBuy() {
		plugin.getCommandManager().registerExecutor(command, this);
	}
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		execute(sender, args);
		return true;
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

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.hasPermission(nPlayer.isResizing() ? GuildPermission.REGION_RESIZE : GuildPermission.REGION_CREATE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(guild.hasRegion() && !nPlayer.isResizing()) {
			Message.CHAT_GUILD_HASREGIONALREADY.send(sender);
			return;
		}

		Location sl0 = nPlayer.getSelectedLocation(0);
		Location sl1 = nPlayer.getSelectedLocation(1);

		if(sl0 == null || sl1 == null) {
			Message.CHAT_REGION_VALIDATION_NOTSELECTED.send(sender);
			return;
		}

		RegionValidity selectionValidity = plugin.getRegionManager().checkRegionSelect(sl0, sl1);

		if(nPlayer.isResizing() && selectionValidity==RegionValidity.OVERLAPS) {
			List<NovaRegion> regionsOverlaped = plugin.getRegionManager().getRegionsInsideArea(sl0,sl1);
			if(regionsOverlaped.size()==1 && regionsOverlaped.get(0).equals(nPlayer.getGuild().getRegion())) {
				selectionValidity = RegionValidity.VALID;
			}
		}

		if(selectionValidity == RegionValidity.TOOCLOSE) {
			List<NovaGuild> guildsTooClose = plugin.getRegionManager().getGuildsTooClose(sl0, sl1);

			if(guildsTooClose.size() == 1 && guildsTooClose.get(0).equals(nPlayer.getGuild())) {
				selectionValidity = RegionValidity.VALID;
			}
		}

		if(selectionValidity != RegionValidity.VALID) {
			Message.CHAT_REGION_VALIDATION_NOTVALID.send(sender);
			return;
		}

		int regionsize = RegionUtils.checkRegionSize(sl0, sl1);

		//region's price
		double price;
		double ppb = plugin.getGroupManager().getGroup(sender).getRegionPricePerBlock();

		if(nPlayer.isResizing()) {
			price = ppb * (regionsize - guild.getRegion().getSurface());
		}
		else {
			price = ppb * regionsize + plugin.getGroupManager().getGroup(sender).getRegionCreateMoney();
		}

		if(price > 0 && guild.getMoney() < price) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		if(nPlayer.isResizing()) {
			NovaRegion region = guild.getRegion();
			region.setCorner(nPlayer.getResizingCorner(), nPlayer.getResizingCorner() == 0 ? sl0 : sl1);
			region.getCorner(nPlayer.getResizingCorner()).setY(0);
			Message.CHAT_REGION_RESIZE_SUCCESS.send(sender);
		}
		else {
			NovaRegion region = new NovaRegion();
			region.setCorner(0, sl0);
			region.setCorner(1, sl1);
			region.setWorld(nPlayer.getPlayer().getWorld());
			region.setGuild(nPlayer.getGuild());
			plugin.getRegionManager().add(region, guild);
			Message.CHAT_REGION_CREATED.send(sender);
		}

		if(price > 0) {
			guild.takeMoney(price);
		}

		nPlayer.cancelToolProgress();
	}
}
