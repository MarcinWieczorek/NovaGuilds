package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.ItemStackUtils;
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

	private double regionPricePerBlock = 0;
	private double regionCreateMoney = 0;
	private int regionAutoSize = 0;

	@SuppressWarnings("deprecation")
	public NovaGroup(NovaGuilds plugin, String group) {
		name = group;

		if(name.equalsIgnoreCase("admin")) {
			regionAutoSize = plugin.getConfig().getInt("region.adminautosize");
			return;
		}

		//createGuildItems
//		List<ItemStack> items = new ArrayList<>();
//		List<String> itemstr = plugin.getConfig().getStringList("guild.create.groups."+group+".items");
//		ItemStack stack;
//
//		for(String anItemstr : itemstr) {
//			String[] exp = anItemstr.split(" ");
//			String idname;
//			byte data = (byte)0;
//			int amount = Integer.parseInt(exp[1]);
//
//			if(exp[0].contains(":")) {
//				String[] dataexp = exp[0].split(":");
//				idname = dataexp[0];
//				data = Byte.parseByte(dataexp[1]);
//			}
//			else {
//				idname = exp[0];
//			}
//
//			Material material = Material.getMaterial(idname.toUpperCase());
//
//			if(material != null) {
//				stack = new ItemStack(material, amount, data);
//				items.add(stack);
//			}
//			else {
//				plugin.info("Failed to load item "+idname.toUpperCase()+" for group "+name);
//			}
//		}

		//setting all values
		String groupPath = "guild.create.groups." + group + ".";
		guildCreateItems = ItemStackUtils.stringToItemStackList(plugin.getConfig().getStringList(groupPath + "guild.create.items"));
		guildCreateMoney = plugin.getConfig().getDouble(groupPath + "guild.create.money");
		guildTeleportDelay = plugin.getConfig().getInt(groupPath + "guild.home.tpdelay");
		guildHomeItems = ItemStackUtils.stringToItemStackList(plugin.getConfig().getStringList(groupPath + "guild.home.tpdelay"));
		guildJoinItems = ItemStackUtils.stringToItemStackList(plugin.getConfig().getStringList(groupPath + "guild.home.tpdelay"));
		regionPricePerBlock = plugin.getConfig().getDouble(groupPath + "region.ppb");
		regionCreateMoney = plugin.getConfig().getDouble(groupPath + "region.create");
		guildEffectPrice = plugin.getConfig().getDouble(groupPath + "region.effectprice");
		regionAutoSize = plugin.getConfig().getInt(groupPath + "region.autoregionsize");
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
}
