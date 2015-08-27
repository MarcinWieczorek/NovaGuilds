package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.command.*;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Set;

public class CustomCommandManager {
	private final NovaGuilds plugin;
	private final HashMap<String,String> aliases = new HashMap<>();
	private final HashMap<ItemStack,String> guiCommands = new HashMap<>();
	private ItemStack topItem;
	private final HashMap<Commands, Executor> executors = new HashMap<>();

	public CustomCommandManager(NovaGuilds plugin) {
		this.plugin = plugin;
		plugin.setCommandManager(this);
		registerCommands();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("aliases");

		for(String key : section.getKeys(false)) {
			for(String alias : section.getStringList(key)) {
				aliases.put(alias,key);
			}
		}

		setupGuildMenu();

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
		plugin.getCommand("join").setExecutor(new CommandGuildJoin(plugin));
		plugin.getCommand("leave").setExecutor(new CommandGuildLeave(plugin));

		plugin.getCommand("invite").setExecutor(new CommandGuildInvite(plugin));
		plugin.getCommand("guildmenu").setExecutor(new CommandGuildMenu(plugin));

		plugin.getCommand("playerinfo").setExecutor(new CommandPlayerInfo(plugin));

		//register custom executors
		new CommandGuildBuyLife(Commands.GUILD_BUYLIFE);
		new CommandGuildBuySlot(Commands.GUILD_BUYSLOT);
		new CommandGuildCompass(Commands.GUILD_COMPASS);
		new CommandToolGet(Commands.TOOL_GET);
		new CommandGuildAlly(Commands.GUILD_ALLY);
		new CommandGuildBankPay(Commands.GUILD_BANK_PAY);
		new CommandGuildBankWithdraw(Commands.GUILD_BANK_WITHDRAW);
		new CommandGuildEffect(Commands.GUILD_EFFECT);
		new CommandGuildHome(Commands.GUILD_HOME);
		new CommandGuildKick(Commands.GUILD_KICK);
		new CommandGuildLeader(Commands.GUILD_LEADER);
		new CommandGuildPvpToggle(Commands.GUILD_PVPTOGGLE);
		new CommandGuildTop(Commands.GUILD_TOP);
		new CommandGuildRequiredItems(Commands.GUILD_REQUIREDITEMS);
//		new CommandGuildWar(Commands.GUILD_WAR);
		new CommandAdminGuildList(Commands.ADMIN_GUILD_LIST);

		//AdminHologram
		new CommandAdminHologram(Commands.ADMIN_HOLOGRAM_ACCESS);
	}

	public String getGuiCommand(ItemStack itemStack) {
		return guiCommands.get(itemStack);
	}

	public Set<ItemStack> getGuiItems() {
		return guiCommands.keySet();
	}

	public void updateGuiTop() {
		guiCommands.remove(topItem);

		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(topItem.getType());
		meta.setDisplayName(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		meta.setLore(plugin.getGuildManager().getTopGuilds());
		topItem.setItemMeta(meta);
		guiCommands.put(topItem, "g top");
	}

	public void setupGuildMenu() {
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
	}

	public void registerExecutor(Commands command, Executor executor) {
		executors.put(command, executor);
	}

	public Executor getExecutor(Commands command) {
		return executors.get(command);
	}
}
