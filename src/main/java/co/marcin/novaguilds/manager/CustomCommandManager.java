package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.command.*;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CustomCommandManager {
	private final NovaGuilds plugin;
	private final HashMap<String,String> aliases = new HashMap<>();
	private final HashMap<ItemStack,String> guiCommands = new HashMap<>();
	private ItemStack topItem;

	public CustomCommandManager(NovaGuilds plugin) {
		this.plugin = plugin;
		registerCommands();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("aliases");

		for(String key : section.getKeys(false)) {
			for(String alias : section.getStringList(key)) {
				aliases.put(alias,key);
			}
		}

		//GUI commands
		//guiCommands.put(new ItemStack(Material.EYE_OF_ENDER,1),"g home");

		guiCommands.clear();
		ConfigurationSection sectionGUI = plugin.getConfig().getConfigurationSection("gguicmd");

		for(String key : sectionGUI.getKeys(false)) {
			LoggerUtils.debug(key);
			String gcmd = key.replaceAll("_", " ");
			LoggerUtils.debug(gcmd);
			ItemStack is = ItemStackUtils.stringToItemStack(sectionGUI.getString(key));
			if(is != null) LoggerUtils.debug(is.toString());

			if(is != null) {
				if(key.equalsIgnoreCase("top")) {
					topItem = is;
				}
				else {
					guiCommands.put(is, gcmd);
				}
			}
		}

		LoggerUtils.info("Enabled");
	}

	public String getMainCommand(String alias) {
		return aliases.get(alias);
	}

	public boolean existsAlias(String alias) {
		return aliases.containsKey(alias);
	}

	private void registerCommands() {
		plugin.getCommand("novaguilds").setExecutor(new CommandNovaGuilds(plugin));
		plugin.getCommand("ng").setExecutor(new CommandNovaGuilds(plugin));
		plugin.getCommand("nga").setExecutor(new CommandAdmin(plugin));

		plugin.getCommand("abandon").setExecutor(new CommandGuildAbandon(plugin));
		plugin.getCommand("guild").setExecutor(new CommandGuild(plugin));
		plugin.getCommand("gi").setExecutor(new CommandGuildInfo(plugin));
		plugin.getCommand("create").setExecutor(new CommandGuildCreate(plugin));
		plugin.getCommand("nghome").setExecutor(new CommandGuildHome(plugin));
		plugin.getCommand("join").setExecutor(new CommandGuildJoin(plugin));
		plugin.getCommand("leave").setExecutor(new CommandGuildLeave(plugin));

		plugin.getCommand("invite").setExecutor(new CommandGuildInvite(plugin));
		plugin.getCommand("guildmenu").setExecutor(new CommandGuildMenu(plugin));

		plugin.getCommand("playerinfo").setExecutor(new CommandPlayerInfo(plugin));
	}

	public String getGuiCommand(ItemStack itemStack) {
		return guiCommands.get(itemStack);
	}

	public Set<ItemStack> getGuiItems() {
		return guiCommands.keySet();
	}

	public void updateGuiTop() {int limit = Integer.parseInt(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_TOPROWS.get()); //TODO move to config
		int i=1;
		List<String> lore = new ArrayList<>();
		HashMap<String, String> vars = new HashMap<>();

		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			vars.clear();
			vars.put("GUILDNAME", guild.getName());
			vars.put("N", String.valueOf(i));
			vars.put("POINTS", String.valueOf(guild.getPoints()));
			lore.add(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.vars(vars).get());
			i++;
		}

		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(topItem.getType());
		meta.setDisplayName(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		meta.setLore(lore);
		topItem.setItemMeta(meta);
		guiCommands.put(topItem, "g top");
	}

	public void setupGuildMenu() {
		guiCommands.clear();
	}
}
