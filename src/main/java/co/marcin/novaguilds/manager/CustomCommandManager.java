package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.command.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Set;

public class CustomCommandManager {
	private final NovaGuilds plugin;
	private final HashMap<String,String> aliases = new HashMap<>();
	private final HashMap<ItemStack,String> guiCommands = new HashMap<>();

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
		guiCommands.put(new ItemStack(Material.EYE_OF_ENDER,1),"g home");
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
	}

	public String getGuiCommand(ItemStack itemStack) {
		return guiCommands.get(itemStack);
	}

	public Set<ItemStack> getGuiItems() {
		return guiCommands.keySet();
	}

	public void setupGuildMenu() {
		guiCommands.clear();
	}
}
