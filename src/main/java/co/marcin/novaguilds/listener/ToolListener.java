package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.util.RegionUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ToolListener implements Listener {
	private final NovaGuilds plugin;
	
	public ToolListener(NovaGuilds pl) {
		plugin = pl;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		Material tool = Material.getMaterial(plugin.getConfig().getString("region.tool.item").toUpperCase());
		String toolname = StringUtils.fixColors(plugin.getMessageManager().getMessagesString("items.tool.name"));

		if(player.getItemInHand().getType().equals(tool)) {
			if(player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(toolname)) {
				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

				//Spigot and Cauldron (1.8/1.7.10)
				Location pointedLocation = player.getTargetBlock((Set<Material>)null, 200).getLocation(); //TODO: spigot
				//Location pointedLocation = player.getTargetBlock(null, 200).getLocation(); //TODO: CAULDRON

				pointedLocation.setWorld(player.getWorld());

				//Change RegionMode
				if((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && player.isSneaking()) {
					if(!player.hasPermission("novaguilds.tool.check") || !player.hasPermission("novaguilds.region.create")) {
						return;
					}

					//remove region highlight
					if(nPlayer.getSelectedRegion() != null) {
						RegionUtils.highlightRegion(event.getPlayer(), nPlayer.getSelectedRegion(), null);
					}

					event.setCancelled(true);
					nPlayer.setRegionMode(!nPlayer.regionMode());

					String mode;
					if(nPlayer.regionMode()) {
						mode = plugin.getMessageManager().getMessagesString("chat.region.tool.modes.select");

						if(nPlayer.hasGuild() && nPlayer.isLeader() && nPlayer.getGuild().hasRegion()) {
							//RegionUtils.setCorner(player,nPlayer.getGuild().getRegion().getCorner(0),Material.GOLD_BLOCK);
							//RegionUtils.setCorner(player,nPlayer.getGuild().getRegion().getCorner(1),Material.GOLD_BLOCK);
							RegionUtils.highlightRegion(player,nPlayer.getGuild().getRegion(),Material.GOLD_BLOCK);
							nPlayer.setSelectedRegion(nPlayer.getGuild().getRegion());
							plugin.debug("golden corners");
						}
					}
					else {
						mode = plugin.getMessageManager().getMessagesString("chat.region.tool.modes.check");
					}

					HashMap<String, String> vars = new HashMap<>();
					vars.put("MODE", mode);
					plugin.getMessageManager().sendMessagesMsg(player, "chat.region.tool.toggledmode", vars);
					plugin.debug("toggle=" + plugin.getPlayerManager().getPlayer(player).regionMode());

					if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
						RegionUtils.sendSquare(player, nPlayer.getSelectedLocation(0), nPlayer.getSelectedLocation(1), null, (byte) 0);
						RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(0));
						RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(1));
					}

					//unselect corners
					nPlayer.setSelectedLocation(0, null);
					nPlayer.setSelectedLocation(1, null);

					//disable resizing mode
					nPlayer.setResizing(false);

					return;
				}


				NovaRegion region = plugin.getRegionManager().getRegionAtLocation(pointedLocation);

				if(!nPlayer.regionMode()) { //CHECK MODE
					if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						if(!player.hasPermission("novaguilds.tool.check")) { //permissions check
							return;
						}

						if(nPlayer.getSelectedRegion() != null && !(nPlayer.getGuild().hasRegion() && nPlayer.getGuild().getRegion().equals(nPlayer.getSelectedRegion()))) {
							RegionUtils.highlightRegion(event.getPlayer(), nPlayer.getSelectedRegion(), null);
						}

						if(region != null) {
							RegionUtils.highlightRegion(player, region, Material.DIAMOND_BLOCK);
							HashMap<String, String> vars = new HashMap<>();
							vars.put("GUILDNAME", region.getGuildName());
							plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.belongsto", vars);
							nPlayer.setSelectedRegion(region);
						}
						else {
							plugin.getMessageManager().sendMessagesMsg(player,"chat.region.noregionhere");
							nPlayer.setSelectedRegion(null);
						}
					}
				}
				else { //CREATE MODE
					if(!event.getAction().equals(Action.PHYSICAL)) {
						if(region != null && !nPlayer.isResizing()) { //resizing
							if(!player.hasPermission("novaguilds.region.resize")) {
								return;
							}

							//plugin.debug("guild null=" + (region.getGuild() == null));
							if(region.getGuild().isMember(nPlayer) && nPlayer.isLeader()) {
								Location pointedCornerLocation = pointedLocation.clone();
								pointedCornerLocation.setY(0);
								//pointedCornerLocation = pointedCornerLocation.getBlock().getLocation();
								//plugin.debug("y: "+pointedCornerLocation.getBlockY() + " / " + region.getCorner(0).getBlockY() + " / " + region.getCorner(1).getBlockY());
								//plugin.debug("0=" + pointedCornerLocation.distance(region.getCorner(0).getBlock().getLocation()));
								//plugin.debug("1=" + pointedCornerLocation.distance(region.getCorner(1).getBlock().getLocation()));

								if(pointedCornerLocation.distance(region.getCorner(0).getBlock().getLocation()) < 1 || pointedCornerLocation.distance(region.getCorner(1).getBlock().getLocation()) < 1) { //clicked a corner
									int corner = 1;

									if(pointedCornerLocation.distance(region.getCorner(0)) < 1) {
										corner = 0;
									}

									nPlayer.setResizing(true);
									nPlayer.setResizingCorner(corner);
									plugin.getMessageManager().sendMessagesMsg(player,"chat.region.resizing");
									RegionUtils.sendSquare(player, nPlayer.getSelectedLocation(0), nPlayer.getSelectedLocation(1), null, (byte) 0);
									nPlayer.setSelectedLocation(0, null);
									nPlayer.setSelectedLocation(1, null);
									nPlayer.setSelectedLocation(corner == 1 ? 0 : 1, region.getCorner(corner == 1 ? 0 : 1));
								}
							}
						}
						else {
							if(!player.hasPermission("novaguilds.region.create")) {
								return;
							}

							Location sl0 = nPlayer.getSelectedLocation(0);
							Location sl1 = nPlayer.getSelectedLocation(1);
							event.setCancelled(true);

							//Corner 1
							if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
								if(!nPlayer.isResizing()) {
									if(nPlayer.getSelectedLocation(0) != null) {
										RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(0));

										if(nPlayer.getSelectedLocation(1) != null) {
											RegionUtils.sendSquare(player, sl0, sl1, null, (byte) 0);
										}
									}

									RegionUtils.setCorner(player, pointedLocation, Material.EMERALD_BLOCK);
									nPlayer.setSelectedLocation(0, pointedLocation);
									sl0 = pointedLocation;
								}
							}

							//Corner 2
							if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
								if(nPlayer.isResizing()) {
									if(nPlayer.getSelectedLocation(nPlayer.getResizingCorner()) != null) {
										RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(nPlayer.getResizingCorner()));

										if(nPlayer.getSelectedLocation(nPlayer.getResizingCorner()==0 ? 1 : 0) != null) {
											RegionUtils.sendSquare(player, sl0, sl1, null, (byte) 0);
										}
									}

									if(nPlayer.getResizingCorner()==0)
										sl0=pointedLocation;
									else
										sl1=pointedLocation;

									nPlayer.setSelectedLocation(nPlayer.getResizingCorner(), pointedLocation);
								}
								else {
									if(nPlayer.getSelectedLocation(1) != null) {
										RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(1));

										if(nPlayer.getSelectedLocation(0) != null) {
											RegionUtils.sendSquare(player, sl0, sl1, null, (byte) 0);
										}
									}

									nPlayer.setSelectedLocation(1, pointedLocation);
									sl1 = pointedLocation;
								}

								RegionUtils.setCorner(player, pointedLocation, Material.EMERALD_BLOCK);
							}

							if(sl0 != null && sl1 != null) {
								RegionValidity validSelect = plugin.getRegionManager().checkRegionSelect(sl0, sl1);
								byte data = (byte)15;

								//When resizing if overlaps player's region
								if(nPlayer.isResizing() && validSelect==RegionValidity.OVERLAPS) {
									List<NovaRegion> regionsOverlaped = plugin.getRegionManager().getRegionsInsideArea(sl0,sl1);
									if(regionsOverlaped.size()==1 && regionsOverlaped.get(0).equals(nPlayer.getGuild().getRegion())) {
										validSelect = RegionValidity.VALID;
									}
								}

								switch(validSelect) {
									case VALID:  //valid
										if(nPlayer.hasGuild()) {
											int regionsize = plugin.getRegionManager().checkRegionSize(sl0, sl1);
											double price;
											double ppb = plugin.getGroupManager().getGroup(player).getRegionPricePerBlock();

											if(nPlayer.isResizing()) {
												data = (byte) 6;
												price = ppb * (regionsize - nPlayer.getGuild().getRegion().getSurface());
												plugin.debug(ppb+" * ("+regionsize+" - "+nPlayer.getGuild().getRegion().getSurface()+") = "+price);
											}
											else {
												data = (byte) 14;
												price = ppb * regionsize + plugin.getGroupManager().getGroup(player).getRegionCreateMoney();
											}

											String sizemsg = plugin.getMessageManager().getMessagesString("chat.region.size");
											sizemsg = StringUtils.replace(sizemsg, "{SIZE}", regionsize + "");

											String pricemsg = plugin.getMessageManager().getMessagesString("chat.region.price");
											pricemsg = StringUtils.replace(pricemsg, "{PRICE}", price + "");

											plugin.getMessageManager().sendPrefixMessage(player, sizemsg);

											if(price > 0) {
												plugin.getMessageManager().sendPrefixMessage(player, pricemsg);

												double guildBalance = nPlayer.getGuild().getMoney();
												if(guildBalance < price) {
													String cnotaffordmsg = plugin.getMessageManager().getMessagesString("chat.region.cnotafford");
													cnotaffordmsg = StringUtils.replace(cnotaffordmsg, "{NEEDMORE}", price - guildBalance + "");
													plugin.getMessageManager().sendPrefixMessage(player, cnotaffordmsg);
													return;
												}
											}

											plugin.getMessageManager().sendMessagesMsg(player, "chat.region.selectsuccess");
										}
										else {
											plugin.getMessageManager().sendMessagesMsg(player, "chat.region.mustveguild");
										}
										break;
									case TOOSMALL:
										String msg = plugin.getMessageManager().getMessagesString("chat.region.toosmall");
										msg = StringUtils.replace(msg, "{MINSIZE}", plugin.getConfig().getInt("region.minsize") + "");
										plugin.getMessageManager().sendPrefixMessage(player, msg);
										break;
									case TOOBIG:
										msg = plugin.getMessageManager().getMessagesString("chat.region.toobig");
										msg = StringUtils.replace(msg, "{MAXSIZE}", plugin.getConfig().getInt("region.maxsize") + "");
										plugin.getMessageManager().sendPrefixMessage(player, msg);
										break;
									case OVERLAPS:
										//TODO
										//NovaRegion rgoverlaped = plugin.getRegionManager().regionInsideArea(sl1,sl2);
										//plugin.getRegionManager().highlightRegion(player, rgoverlaped);
										plugin.getMessageManager().sendMessagesMsg(player, "chat.region.overlaps");
										break;
									case TOOCLOSE:
										plugin.getMessageManager().sendMessagesMsg(player, "chat.guild.tooclose");
										break;
								}

								//corners and rectangles
								RegionUtils.sendSquare(player, sl0, sl1, Material.WOOL, data);
								RegionUtils.setCorner(player, sl0, Material.EMERALD_BLOCK);
								RegionUtils.setCorner(player, sl1, Material.EMERALD_BLOCK);
							}
						}
					}
				}
			}
		}
	}
}
