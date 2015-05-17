package co.marcin.NovaGuilds.listener;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.basic.NovaRegion;
import co.marcin.NovaGuilds.utils.StringUtils;

public class ToolListener implements Listener {
	private final NovaGuilds plugin;
	
	public ToolListener(NovaGuilds pl) {
		plugin = pl;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		Material tool = Material.getMaterial(plugin.getConfig().getString("region.tool.item").toUpperCase());
		String toolname = StringUtils.fixColors(plugin.getMessagesString("items.tool.name"));

		if(player.getItemInHand().getType().equals(tool)) {
			if(player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(toolname)) {
				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(player);
				Location pointedlocation = player.getTargetBlock(null, 200).getLocation();
				pointedlocation.setWorld(player.getWorld());

				//Change RegionMode
				if((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && player.isSneaking()) {
					if(!player.hasPermission("novaguilds.tool.check") || !player.hasPermission("novaguilds.region.create")) {
						return;
					}

					event.setCancelled(true);
					nPlayer.setRegionMode(!nPlayer.regionMode());

					String mode;
					if(nPlayer.regionMode()) {
						mode = plugin.getMessages().getString("chat.region.tool.modes.select");
					} else {
						mode = plugin.getMessages().getString("chat.region.tool.modes.check");
					}

					HashMap<String, String> vars = new HashMap<>();
					vars.put("MODE", mode);
					plugin.sendMessagesMsg(event.getPlayer(), "chat.region.tool.toggledmode", vars);
					plugin.debug("toggle=" + plugin.getPlayerManager().getPlayerByName(player.getName()).regionMode());

					if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
						plugin.getRegionManager().sendSquare(player, nPlayer.getSelectedLocation(0), nPlayer.getSelectedLocation(1), null, (byte) 0);
						plugin.getRegionManager().resetCorner(player, nPlayer.getSelectedLocation(0));
						plugin.getRegionManager().resetCorner(player, nPlayer.getSelectedLocation(1));
					}

					nPlayer.setSelectedLocation(0, null);
					nPlayer.setSelectedLocation(1, null);

					if(nPlayer.getSelectedRegion() != null) {
						plugin.getRegionManager().resetHighlightRegion(event.getPlayer(), nPlayer.getSelectedRegion());
					}

					return;
				}


				NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(pointedlocation);

				if(!nPlayer.regionMode()) { //CHECK MODE

					if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						if(!player.hasPermission("novaguilds.tool.check")) { //permissions check
							return;
						}

						if(nPlayer.getSelectedRegion() != null) {
							plugin.getRegionManager().resetHighlightRegion(player, nPlayer.getSelectedRegion());
						}

						if(rgatloc != null) {
							plugin.getRegionManager().highlightRegion(player, rgatloc);
							HashMap<String, String> vars = new HashMap<>();
							vars.put("GUILDNAME", rgatloc.getGuildName());
							plugin.sendMessagesMsg(event.getPlayer(), "chat.region.belongsto", vars);
							nPlayer.setSelectedRegion(rgatloc);
						} else {
							player.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.region.noregionhere")));
							nPlayer.setSelectedRegion(null);
						}
					}
				} else { //CREATE MODE
					if(!event.getAction().equals(Action.PHYSICAL)) {
						if(!player.hasPermission("novaguilds.region.create")) {
							return;
						}

						if(rgatloc == null) {
							Location sl1 = nPlayer.getSelectedLocation(0);
							Location sl2 = nPlayer.getSelectedLocation(1);
							event.setCancelled(true);

							//Corner 1
							if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
								if(nPlayer.getSelectedLocation(0) != null) {
									plugin.getRegionManager().resetCorner(player, nPlayer.getSelectedLocation(0));
									if(nPlayer.getSelectedLocation(1) != null) {
										plugin.getRegionManager().sendSquare(player, sl1, sl2, null, (byte) 0);
									}
								}

								plugin.getRegionManager().setCorner(player, pointedlocation);
								nPlayer.setSelectedLocation(0, pointedlocation);
								sl1 = pointedlocation;
							}

							//Corner 2
							if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
								if(nPlayer.getSelectedLocation(1) != null) {
									plugin.getRegionManager().resetCorner(player, nPlayer.getSelectedLocation(1));
									if(nPlayer.getSelectedLocation(0) != null)
										plugin.getRegionManager().sendSquare(player, sl1, sl2, null, (byte) 0);
								}

								plugin.getRegionManager().setCorner(player, pointedlocation);
								nPlayer.setSelectedLocation(1, pointedlocation);
								sl2 = pointedlocation;
							}

							if(sl1 != null && sl2 != null) {
								String validSelect = plugin.getRegionManager().checkRegionSelect(sl1, sl2);
								byte data = Byte.parseByte("15");

								switch(validSelect) {
									case "valid":  //valid
										if(nPlayer.hasGuild()) {
											data = (byte) 14;
											int regionsize = plugin.getRegionManager().checkRegionSize(sl1, sl2);
											String sizemsg = plugin.getMessages().getString("chat.region.size");
											sizemsg = StringUtils.replace(sizemsg, "{SIZE}", regionsize + "");

											int createprice = plugin.getConfig().getInt("region.createprice");
											int price = plugin.getConfig().getInt("region.pricepb") * regionsize + createprice;

											String pricemsg = plugin.getMessages().getString("chat.region.price");
											pricemsg = StringUtils.replace(pricemsg, "{PRICE}", price + "");

											plugin.sendPrefixMessage(player, sizemsg);
											plugin.sendPrefixMessage(player, pricemsg);

											double guildBalance = nPlayer.getGuild().getMoney();
											if(guildBalance < price) {
												String cnotaffordmsg = plugin.getMessages().getString("chat.region.cnotafford");
												cnotaffordmsg = StringUtils.replace(cnotaffordmsg, "{NEEDMORE}", price - guildBalance + "");
												plugin.sendPrefixMessage(player, cnotaffordmsg);
											} else {
												plugin.sendMessagesMsg(player, "chat.region.selectsuccess");
											}
										} else {
											plugin.sendMessagesMsg(player, "chat.region.mustveguild");
										}
										break;
									case "toosmall": {
										String msg = plugin.getMessages().getString("chat.region.toosmall");
										msg = StringUtils.replace(msg, "{MINSIZE}", plugin.getConfig().getInt("region.minsize") + "");
										plugin.sendPrefixMessage(player, msg);
										break;
									}
									case "toobig": {
										String msg = plugin.getMessages().getString("chat.region.toobig");
										msg = StringUtils.replace(msg, "{MAXSIZE}", plugin.getConfig().getInt("region.maxsize") + "");
										plugin.sendPrefixMessage(player, msg);
										break;
									}
									case "overlaps":
										//TODO
										//NovaRegion rgoverlaped = plugin.getRegionManager().regionInsideArea(sl1,sl2);
										//plugin.getRegionManager().highlightRegion(player, rgoverlaped);
										plugin.sendMessagesMsg(player, "chat.region.overlaps");
										break;
								}

								plugin.getRegionManager().sendSquare(player, sl1, sl2, Material.WOOL, data);
								plugin.getRegionManager().setCorner(player, sl1);
								plugin.getRegionManager().setCorner(player, sl2);
							}
						} else {
							plugin.sendMessagesMsg(player, "chat.region.regionhere");
						}
					}
				}
			}
		}
	}
}
