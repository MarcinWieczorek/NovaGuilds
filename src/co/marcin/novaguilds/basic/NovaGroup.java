package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NovaGroup {

	private final String name;
	private double createGuildMoney = 0;
	private double pricePerBlock = 0;
	private double createRegionMoney = 0;
	private double effectPrice = 0;
	private int teleportDelay = 0;
	private int autoregionSize = 0;
	private List<ItemStack> createGuildItems = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public NovaGroup(NovaGuilds plugin, String group) {
		name = group;

		if(name.equalsIgnoreCase("admin")) {
			autoregionSize = plugin.getConfig().getInt("region.adminautosize");
			return;
		}

		//createGuildItems
		List<ItemStack> items = new ArrayList<>();
		List<String> itemstr = plugin.getConfig().getStringList("guild.create.groups."+group+".items");
		ItemStack stack;

		for(String anItemstr : itemstr) {
			String[] exp = anItemstr.split(" ");
			String idname;
			byte data = (byte)0;
			int amount = Integer.parseInt(exp[1]);

			if(exp[0].contains(":")) {
				String[] dataexp = exp[0].split(":");
				idname = dataexp[0];
				data = Byte.parseByte(dataexp[1]);
			}
			else {
				idname = exp[0];
			}

			Material material = Material.getMaterial(idname.toUpperCase());

			if(material != null) {
				stack = new ItemStack(material, amount, data);
				items.add(stack);
			}
			else {
				plugin.info("Failed to load item "+idname.toUpperCase()+" for group "+name);
			}
		}

		plugin.debug(items.toString());

		//setting all values
		String groupPath = "guild.create.groups." + group + ".";
		setCreateGuildItems(items);
		setCreateGuildMoney(plugin.getConfig().getDouble(groupPath + "money"));
		setTeleportDelay(plugin.getConfig().getInt(groupPath + "tpdelay"));
		setPricePerBlock(plugin.getConfig().getDouble(groupPath + "region.ppb"));
		setRegionCreateMoney(plugin.getConfig().getDouble(groupPath + "region.create"));
		setEffectPrice(plugin.getConfig().getDouble(groupPath + "region.effectprice"));
		setAutoregionSize(plugin.getConfig().getInt(groupPath + "region.autoregionsize"));
	}

	//setters
	public void setCreateGuildMoney(double money) {
		createGuildMoney = money;
	}

	public void setRegionCreateMoney(double money) {
		createRegionMoney = money;
	}

	public void setPricePerBlock(double ppb) {
		pricePerBlock = ppb;
	}

	public void setCreateGuildItems(List<ItemStack> items) {
		createGuildItems = items;
	}

	public void setTeleportDelay(int delay) {
		teleportDelay = delay;
	}

	public void setEffectPrice(double effectPrice) {
		this.effectPrice = effectPrice;
	}

	public void setAutoregionSize(int size) {
		autoregionSize = size;
	}

	//getters
	public double getCreateGuildMoney() {
		return createGuildMoney;
	}

	public String getName() {
		return name;
	}

	public double getPricePerBlock() {
		return pricePerBlock;
	}

	public double getCreateRegionMoney() {
		return createRegionMoney;
	}

	public List<ItemStack> getCreateGuildItems() {
		return createGuildItems;
	}

	public int getTeleportDelay() {
		return teleportDelay;
	}

	public double getEffectPrice() {
		return effectPrice;
	}

	public int getAutoregionSize() {
		return autoregionSize;
	}
}
