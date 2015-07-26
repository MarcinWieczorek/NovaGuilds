package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGroup;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandNovaGuilds implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandNovaGuilds(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			String[] info = {
					"NovaGuilds &6#&c"+plugin.getBuild(),
					"Authors: &6Marcin (CTRL) Wieczorek&2, &dartur9010",
					"2015 &4Pol&fand",
					"&bhttp://novaguilds.pl/",
					"Latest plugin build: &6#&c{LATEST}"
			};

			plugin.getMessageManager().sendPrefixMessage(sender, "NovaGuilds Information");
			String latest = StringUtils.getContent("http://novaguilds.pl/latest");

			for(int i=0;i<info.length;i++) {
				info[i] = StringUtils.replace(info[i], "{LATEST}", latest);
				sender.sendMessage(StringUtils.fixColors("&2" + info[i]));
			}
			return true;
		}

			if(args[0].equalsIgnoreCase("book")) {
				if(!sender.hasPermission("novaguilds.test.book")) {
					Message.CHAT_NOPERMISSIONS.send(sender);
					return true;
				}

				if(!plugin.getConfigManager().isDebugEnabled()) {
					return false;
				}

		        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		        BookMeta bm = (BookMeta)book.getItemMeta();
		        bm.setPages(Arrays.asList(new String[] { 
		        	""
		        }));
		        bm.setAuthor("CTRL");
		        bm.setTitle("Guilds Bank");
		        book.setItemMeta(bm);
		        Player player = plugin.getServer().getPlayer(sender.getName());
		        player.getInventory().addItem(book);
			}
			else if(args[0].equalsIgnoreCase("tool")) { //TOOL
				new CommandToolGet(plugin).onCommand(sender, cmd, label, args);
			}
			else if(args[0].equalsIgnoreCase("bank")) { //bank
				if(!sender.hasPermission("novaguilds.test.bank")) {
					Message.CHAT_NOPERMISSIONS.send(sender);
					return true;
				}

				if(sender instanceof Player) {
					NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
					if(nPlayer.hasGuild()) {
						((Player) sender).getInventory().addItem(plugin.getConfigManager().getGuildBankItem());
					}
				}
			}
			else if(args[0].equalsIgnoreCase("admin")) { //Admin commands
				if(sender.hasPermission("novaguilds.admin.access")) {
					new CommandAdmin(plugin).onCommand(sender, cmd, label, StringUtils.parseArgs(args, 1));
				}
			}
			else if(args[0].equalsIgnoreCase("group")) { //Admin commands
				NovaGroup group = plugin.getGroupManager().getGroup(sender);

				if(args.length > 1) {
					group = plugin.getGroupManager().getGroup(args[1]);
					if(group == null) {
						sender.sendMessage("Invalid group");
						return true;
					}
				}

				sender.sendMessage("name = "+group.getName());
				sender.sendMessage("guildCreateMoney = "+group.getGuildCreateMoney());
				sender.sendMessage("guildHomeMoney = "+group.getGuildHomeMoney());
				sender.sendMessage("guildJoinMoney = "+group.getGuildJoinMoney());
				sender.sendMessage("guildCreateItems = " + group.getGuildCreateItems().toString());
				sender.sendMessage("guildHomeItems = " + group.getGuildHomeItems().toString());
				sender.sendMessage("guildJoinItems = " + group.getGuildJoinItems().toString());
				sender.sendMessage("guildTeleportDelay = "+ group.getGuildTeleportDelay()+"s");
				sender.sendMessage("regionCreateMoney = "+ group.getRegionCreateMoney());
				sender.sendMessage("regionPricePerBlock = "+ group.getRegionPricePerBlock());
			}
			else if(args[0].equalsIgnoreCase("version")) {
				String version = plugin.getServer().getVersion();
				LoggerUtils.debug(plugin.getServer().getBukkitVersion());
				LoggerUtils.debug(plugin.getServer().getVersion());
				sender.sendMessage(version);
			}
			else if(args[0].equalsIgnoreCase("hd")) { //HolographicDisplays
				if(!sender.hasPermission("novaguilds.test.hd")) {
					Message.CHAT_NOPERMISSIONS.send(sender);
					return true;
				}
				if(args.length>1) { //GUILDINFO
					if(args[1].equalsIgnoreCase("top")) {
						int limit = Integer.parseInt(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_TOPROWS.get());
						int i=1;
						HashMap<String, String> vars = new HashMap<>();
						for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
							vars.clear();
							vars.put("GUILDNAME", guild.getName());
							vars.put("N", String.valueOf(i));
							vars.put("POINTS", String.valueOf(guild.getPoints()));
							Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.vars(vars).send(sender);
							i++;
						}
						return true;
					}
					
					String guildname = args[1];
					Player player = (Player)sender;
					Hologram hologram = HologramsAPI.createHologram(plugin,player.getLocation());
					
					NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
					if(guild != null) {
						List<String> guildinfomsg = plugin.getMessageManager().getMessages().getStringList("chat.guildinfo.info");
						
						int i;
						List<NovaPlayer> gplayers = guild.getPlayers();
						String leader = guild.getLeader().getName();
						String players = "";
						String pcolor;
						String leaderp; //String to insert to playername (leader prefix)
						String leaderprefix = plugin.getMessageManager().getMessagesString("chat.guildinfo.leaderprefix"); //leader prefix
						
						if(!gplayers.isEmpty()) {
							for(i=0;i<gplayers.size();i++) {
								NovaPlayer nPlayer = gplayers.get(i);
								Player p = plugin.getServer().getPlayer(nPlayer.getName());
								
								if(p != null && p.isOnline()) {
									pcolor = plugin.getMessageManager().getMessagesString("chat.guildinfo.playercolor.online");
								}
								else {
									pcolor = plugin.getMessageManager().getMessagesString("chat.guildinfo.playercolor.offline");
								}
								
								leaderp = "";
								if(nPlayer.getName().equalsIgnoreCase(leader)) {
									leaderp = leaderprefix;
								}
								
								players += pcolor+leaderp+nPlayer.getName();
								
								if(i<gplayers.size()-1) {
									players += plugin.getMessageManager().getMessagesString("chat.guildinfo.playerseparator");
								}
							}
						}
						
						for(i=0;i < guildinfomsg.size();i++) {
							boolean skipmsg = false;
							String tagmsg = plugin.getConfig().getString("guild.tag");
							String gmsg = guildinfomsg.get(i);
							
							tagmsg = StringUtils.replace(tagmsg, "{TAG}", guild.getTag());
							tagmsg = StringUtils.replace(tagmsg, "{RANK}", "");
							
							gmsg = StringUtils.replace(gmsg, "{GUILDNAME}", guild.getName());
							gmsg = StringUtils.replace(gmsg, "{LEADER}", guild.getLeader().getName());
							gmsg = StringUtils.replace(gmsg, "{TAG}", tagmsg);
							gmsg = StringUtils.replace(gmsg, "{MONEY}", guild.getMoney() + "");
							gmsg = StringUtils.replace(gmsg, "{PLAYERS}", players);
							
							if(gmsg.contains("{SP_X}") || gmsg.contains("{SP_Y}") || gmsg.contains("{SP_Z}")) {
								Location sp = guild.getSpawnPoint();
								if(sp != null) {
									gmsg = StringUtils.replace(gmsg, "{SP_X}", sp.getBlockX() + "");
									gmsg = StringUtils.replace(gmsg, "{SP_Y}", sp.getBlockY() + "");
									gmsg = StringUtils.replace(gmsg, "{SP_Z}", sp.getBlockZ() + "");
								}
								else {
									skipmsg = true;
								}
							}
							
							if(!skipmsg) {
								hologram.appendTextLine(StringUtils.fixColors(gmsg));
							}
						}
					}
					else {
						Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
					}
				}
				else {
					for(Hologram h: HologramsAPI.getHolograms(plugin)) {
						h.delete();
					}
				}
			}
			else if(args[0].equalsIgnoreCase("guild") || args[0].equalsIgnoreCase("g")) { // command /g
					new CommandGuild(plugin).onCommand(sender, cmd, label, StringUtils.parseArgs(args, 1));
			}
			else if(args[0].equalsIgnoreCase("entity")) {
				if(!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player)sender;
				sender.sendMessage("Nearby entites");
				for(Entity entity : player.getNearbyEntities(10,10,10)) {
					sender.sendMessage(entity.getType().name());
					sender.sendMessage(entity.toString());
					sender.sendMessage("-");
				}
			}
			else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { //help
				ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
				BookMeta bm = (BookMeta)book.getItemMeta();
				//plugin.getMessageManager().loadMessages();
				List<String> pages = plugin.getMessageManager().getMessages().getStringList("book.help.pages");
				List<String> pagesColor = new ArrayList<>();
				for(String page : pages) {
					pagesColor.add(StringUtils.fixColors(page));
				}

				bm.setPages(pagesColor);
				bm.setAuthor("CTRL");
				bm.setTitle(StringUtils.fixColors(plugin.getMessageManager().getMessagesString("book.help.title")));
				book.setItemMeta(bm);
				Player player = plugin.getServer().getPlayer(sender.getName());
				player.getInventory().addItem(book);
			}
			else {
				Message.CHAT_UNKNOWNCMD.send(sender);
			}
		
		return true;
	}
}
