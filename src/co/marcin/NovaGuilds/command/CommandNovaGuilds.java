package co.marcin.NovaGuilds.command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.basic.NovaRegion;
import co.marcin.NovaGuilds.utils.StringUtils;

public class CommandNovaGuilds implements CommandExecutor {
	final NovaGuilds plugin;
	
	public CommandNovaGuilds(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("book")) {
				if(!plugin.DEBUG) return false;
		        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		        BookMeta bm = (BookMeta)book.getItemMeta();
		        bm.setPages(Arrays.asList(new String[] { 
		        	""
		        }));
		        bm.setAuthor("CTRL");
		        bm.setTitle("Guilds Bank");
		        book.setItemMeta(bm);
		        Player player = plugin.getServer().getPlayer(sender.getName());
		        player.getInventory().setItem(8, book);
			}
			else if(args[0].equalsIgnoreCase("tool")) { //TOOL
				new CommandToolGet(plugin).onCommand(sender, cmd, label, args);
			}
			else if(args[0].equalsIgnoreCase("test")) { //tests
				if(!plugin.DEBUG) return false;
				String group = "default";
				
				for(String s : plugin.getConfig().getConfigurationSection("guild.create").getKeys(false)) {
					if(sender.hasPermission("NovaGuilds.guild.group."+s) || s.equalsIgnoreCase("default")) {
						group = s;
						break;
					}
				}
				
				sender.sendMessage(group);
				sender.sendMessage("money: "+plugin.getConfig().getInt("guild.create."+group+".money"));
				for(String item : plugin.getConfig().getStringList("guild.create."+group+".items")) {
					sender.sendMessage(" * "+item);
				}
			}
			else if(args[0].equalsIgnoreCase("bc")) { //BROADCAST
				if(!plugin.DEBUG) return false;
				if(args.length > 1) {
					String msg ="";
					for(int i=1;i<args.length;i++) {
						msg += args[i]+" ";
					}
					
					plugin.broadcast(msg);
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.usage.ng.broadcast");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("admin")) { //Admin commands
				if(sender.hasPermission("NovaGuilds.admin.access")) {
					new CommandAdmin(plugin).onCommand(sender, cmd, label, StringUtils.parseArgs(args, 1));
				}
			}
			else if(args[0].equalsIgnoreCase("hd")) { //HolographicDisplays
				if(!plugin.DEBUG) return false;
				if(args.length>1) { //GUILDINFO
					if(args[1].equalsIgnoreCase("top")) {
						Statement statement;
						
						try {
							statement = plugin.c.createStatement();
							
							Player player = plugin.senderToPlayer(sender);
							Hologram hologram = HologramsAPI.createHologram(plugin,player.getLocation());
							hologram.appendTextLine(StringUtils.fixColors(plugin.getMessages().getString("holographicdisplays.topguilds.header")));
							
							ResultSet res = statement.executeQuery("SELECT `name`,`points` FROM `"+plugin.sqlp+"guilds` ORDER BY `points` DESC LIMIT "+plugin.getMessages().getInt("holographicdisplays.topguilds.toprows"));
							
							int i=1;
							while(res.next()) {
								String rowmsg = plugin.getMessages().getString("holographicdisplays.topguilds.row");
								rowmsg = StringUtils.replace(rowmsg, "{GUILDNAME}", res.getString("name"));
								rowmsg = StringUtils.replace(rowmsg, "{N}", i + "");
								rowmsg = StringUtils.replace(rowmsg, "{POINTS}", res.getString("points"));
								hologram.appendTextLine(StringUtils.fixColors(rowmsg));
								i++;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						return true;
					}
					
					String guildname = args[1];
					Player player = plugin.senderToPlayer(sender);
					Hologram hologram = HologramsAPI.createHologram(plugin,player.getLocation());
					
					NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
					if(guild != null) {
						List<String> guildinfomsg = plugin.getMessages().getStringList("chat.guildinfo.info");
						
						int i;
						List<NovaPlayer> gplayers = guild.getPlayers();
						String leader = guild.getLeaderName();
						String players = "";
						String pcolor = "";
						String leaderp; //String to insert to playername (leader prefix)
						String leaderprefix = plugin.getMessages().getString("chat.guildinfo.leaderprefix"); //leader prefix
						
						if(gplayers.size()>0) {
							for(i=0;i<gplayers.size();i++) {
								NovaPlayer nplayer = gplayers.get(i);
								Player p = plugin.getServer().getPlayer(nplayer.getName());
								
								if(p instanceof Player &&p.isOnline()) {
									pcolor = plugin.getMessages().getString("chat.guildinfo.playercolor.online");
								}
								else {
									pcolor = plugin.getMessages().getString("chat.guildinfo.playercolor.offline");
								}
								
								leaderp = "";
								if(nplayer.getName().equalsIgnoreCase(leader)) {
									leaderp = leaderprefix;
								}
								
								players += pcolor+leaderp+nplayer.getName();
								
								if(i<gplayers.size()-1) players += plugin.getMessages().getString("chat.guildinfo.playerseparator");
							}
						}
						
						for(i=0;i < guildinfomsg.size();i++) {
							boolean skipmsg = false;
							String tagmsg = plugin.config.getString("guild.tag");
							String gmsg = guildinfomsg.get(i);
							
							tagmsg = StringUtils.replace(tagmsg, "{TAG}", guild.getTag());
							tagmsg = StringUtils.replace(tagmsg, "{RANK}", "");
							
							gmsg = StringUtils.replace(gmsg, "{GUILDNAME}", guild.getName());
							gmsg = StringUtils.replace(gmsg, "{LEADER}", guild.getLeaderName());
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
						plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
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
			else if(args[0].equalsIgnoreCase("rg")) { //REGION
				if(args[1].equalsIgnoreCase("buy")) { //create
					if(!plugin.DEBUG) return true;
					if(sender.hasPermission("NovaGuilds.region.create")) {
						NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
						
						if(nPlayer.hasGuild()) {
							NovaGuild guild = nPlayer.getGuild();
							
							if(guild.getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
								if(!guild.hasRegion()) {
									if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
										Location sl1 = nPlayer.getSelectedLocation(0);
										Location sl2 = nPlayer.getSelectedLocation(1);
	
										if(plugin.getRegionManager().checkRegionSelect(sl1, sl2).equals("valid")) {
											double createprice = plugin.getConfig().getDouble("region.createprice");
											double pricepb = plugin.getConfig().getDouble("region.pricepb");
											int regionsize = plugin.getRegionManager().checkRegionSize(nPlayer.getSelectedLocation(0),nPlayer.getSelectedLocation(1));
											double price = pricepb * regionsize + createprice;
											
											if(guild.getMoney() >= price) {
												NovaRegion region = new NovaRegion();
												
												region.setCorner(0,nPlayer.getSelectedLocation(0));
												region.setCorner(1,nPlayer.getSelectedLocation(1));
												
												plugin.getRegionManager().addRegion(region, guild);
												guild.takeMoney(price);
												plugin.getGuildManager().saveGuild(guild);
												plugin.sendMessagesMsg(sender,"chat.region.created");
											}
											else {
												plugin.sendMessagesMsg(sender,"chat.guild.notenoughtmoney");
											}
										}
										else {
											plugin.sendMessagesMsg(sender,"chat.region.notvalid");
										}
									}
									else {
										plugin.sendMessagesMsg(sender,"chat.region.areanotselected");
									}
								}
								else {
									plugin.sendMessagesMsg(sender,"chat.guild.hasregionalready");
								}
							}
							else {
								plugin.sendMessagesMsg(sender,"chat.guild.notleader");
							}
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
						}
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.nopermissions");
					}
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.unknowncmd");
			}
		}
		else {
			String[] info = {
				"NovaGuilds &6#&c"+plugin.pdf.getVersion(),
				"Author: &6Marcin Wieczorek",
				"March, 2015 &4Pol&fand",
				"&bhttp://NovaGuilds.marcin.co/",
				"Latest plugin build: &6#&c{LATEST}"
			};
			
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + "NovaGuilds Information"));
			String latest = StringUtils.getContent("http://NovaGuilds.marcin.co/latest.info");
			
			for(int i=0;i<info.length;i++) {
				info[i] = StringUtils.replace(info[i], "{LATEST}", latest);
				sender.sendMessage(StringUtils.fixColors("&2" + info[i]));
			}
		}
		
		return true;
	}
}
