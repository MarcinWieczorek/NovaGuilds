package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NovaGroup {

	private final String name;
	private double guildCreateMoney = 0;
	private List<ItemStack> guildCreateItems = new ArrayList<>();

	private List<ItemStack> guildHomeItems = new ArrayList<>();
	private double guildHomeMoney = 0;

	private List<ItemStack> guildJoinItems = new ArrayList<>();
	private double guildJoinMoney;

	private double guildEffectPrice = 0;
	private int guildTeleportDelay = 0;

	private List<ItemStack> guildBuylifeItems = new ArrayList<>();
	private double guildBuylifeMoney = 0;

	private double regionPricePerBlock = 0;
	private double regionCreateMoney = 0;
	private int regionAutoSize = 0;

	@SuppressWarnings("deprecation")
	public NovaGroup(NovaGuilds plugin, String group) {
		name = group;
		LoggerUtils.info("Loading group '" + name + "'...");

		if(name.equalsIgnoreCase("admin")) {
			regionAutoSize = plugin.getConfig().getInt("region.adminautosize");
		}
		else {
			//setting all values
			ConfigurationSection section = plugin.getConfig().getConfigurationSection("groups." + group);
			guildCreateItems = ItemStackUtils.stringToItemStackList(section.getStringList("guild.create.items"));
			guildCreateMoney = section.getDouble("guild.create.money");

			guildTeleportDelay = section.getInt("guild.home.tpdelay");

			guildHomeItems = ItemStackUtils.stringToItemStackList(section.getStringList("guild.home.items"));
			guildJoinItems = ItemStackUtils.stringToItemStackList(section.getStringList("guild.join.items"));

			regionPricePerBlock = section.getDouble("region.ppb");
			regionCreateMoney = section.getDouble("region.createmoney");
			guildEffectPrice = section.getDouble("effectprice");
			regionAutoSize = section.getInt("region.autoregionsize");

			guildHomeMoney = section.getDouble("guild.home.money");
			guildJoinMoney = section.getDouble("guild.join.money");

			guildBuylifeItems = ItemStackUtils.stringToItemStackList(section.getStringList("guild.buylife.items"));
			guildBuylifeMoney = section.getDouble("guild.buylife.money");

			//check values
			if(guildCreateItems == null) {
				guildCreateItems = new ArrayList<>();
			}

			if(guildHomeItems == null) {
				guildHomeItems = new ArrayList<>();
			}

			if(guildJoinItems == null) {
				guildJoinItems = new ArrayList<>();
			}

			if(guildBuylifeItems == null) {
				guildBuylifeItems = new ArrayList<>();
			}
		}
	}

	public static NovaGroup get(CommandSender sender) {
		return NovaGuilds.getInst().getGroupManager().getGroup(sender);
	}

	public String getName() {
		return name;
	}

	public int getGuildTeleportDelay() {
		return guildTeleportDelay;
	}

	public int getRegionAutoSize() {
		return regionAutoSize;
	}

	public double getGuildCreateMoney() {
		return guildCreateMoney;
	}

	public double getGuildEffectPrice() {
		return guildEffectPrice;
	}

	public double getRegionPricePerBlock() {
		return regionPricePerBlock;
	}

	public double getRegionCreateMoney() {
		return regionCreateMoney;
	}

	public List<ItemStack> getGuildCreateItems() {
		return guildCreateItems;
	}

	//guild home
	public double getGuildHomeMoney() {
		return guildHomeMoney;
	}

	public List<ItemStack> getGuildHomeItems() {
		return guildHomeItems;
	}

	//guild join
	public double getGuildJoinMoney() {
		return guildJoinMoney;
	}

	public List<ItemStack> getGuildJoinItems() {
		return guildJoinItems;
	}

	//buylife
	public double getGuildBuylifeMoney() {
		return guildBuylifeMoney;
	}

	public List<ItemStack> getGuildBuylifeItems() {
		return guildBuylifeItems;
	}
}
