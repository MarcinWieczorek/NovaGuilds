package co.marcin.novaguilds.command;

import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionValidity;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandRegionBuy implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandRegionBuy(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.region.create")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return false;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}

		if(guild.hasRegion() && !nPlayer.isResizing()) {
			Message.CHAT_GUILD_HASREGIONALREADY.send(sender);
			return true;
		}

		Location sl0 = nPlayer.getSelectedLocation(0);
		Location sl1 = nPlayer.getSelectedLocation(1);

		if(sl0 == null || sl1 == null) {
			Message.CHAT_REGION_AREANOTSELECTED.send(sender);
			return true;
		}

		RegionValidity selectionValidity = plugin.getRegionManager().checkRegionSelect(sl0, sl1);

		if(nPlayer.isResizing() && selectionValidity==RegionValidity.OVERLAPS) {
			List<NovaRegion> regionsOverlaped = plugin.getRegionManager().getRegionsInsideArea(sl0,sl1);
			if(regionsOverlaped.size()==1 && regionsOverlaped.get(0).equals(nPlayer.getGuild().getRegion())) {
				selectionValidity = RegionValidity.VALID;
			}
		}

		if(selectionValidity != RegionValidity.VALID) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.region.notvalid");
			return true;
		}

		int regionsize = plugin.getRegionManager().checkRegionSize(sl0 ,sl1);

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
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notenoughmoney");
			return true;
		}

		if(nPlayer.isResizing()) {
			NovaRegion region = guild.getRegion();
			region.setCorner(nPlayer.getResizingCorner(),nPlayer.getResizingCorner()==0 ? sl0 : sl1);
			region.getCorner(nPlayer.getResizingCorner()).setY(0);
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.region.resized");
		}
		else {
			NovaRegion region = new NovaRegion();
			region.setCorner(0, sl0);
			region.setCorner(1, sl1);
			region.setWorld(nPlayer.getPlayer().getWorld());
			region.setGuild(nPlayer.getGuild());
			plugin.getRegionManager().addRegion(region, guild);
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.region.created");
		}

		if(price > 0) {
			guild.takeMoney(price);
		}

		return true;
	}
}
