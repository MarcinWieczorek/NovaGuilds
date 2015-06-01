package co.marcin.novaguildss.basic;

import co.marcin.novaguildss.NovaGuilds;
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
	private List<ItemStack> createGuildItems = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public NovaGroup(NovaGuilds plugin, String group) {
		name = group;

		if(name.equalsIgnoreCase("admin")) {
			return;
		}

		//createGuildItems
		List<ItemStack> items = new ArrayList<>();
		List<String> itemstr = plugin.getConfig().getStringList("guild.create.groups."+group+".items");
		ItemStack stack;

		for(String anItemstr : itemstr) {
			String[] exp = anItemstr.split(" ");
			String idname;
			String[] dataexp = null;
			byte data = (byte) 0;
			int amount = Integer.parseInt(exp[1]);

			if(exp[0].contains(":")) {
				dataexp = exp[0].split(":");
				idname = dataexp[0];
				data = Byte.parseByte(dataexp[1]);
			} else {
				idname = exp[0];
			}

			stack = new ItemStack(Material.getMaterial(idname.toUpperCase()), amount, (byte) 1);

			if(dataexp != null) {
				stack.getData().setData(data);
			}

			items.add(stack);
		}

		setCreateGuildItems(items);
		setCreateGuildMoney(plugin.getConfig().getDouble("guild.create.groups." + group + ".money"));
		setTeleportDelay(plugin.getConfig().getInt("guild.create.groups." + group + ".tpdelay"));
		setPricePerBlock(plugin.getConfig().getDouble("guild.create.groups." + group + ".region.ppb"));
		setRegionCreateMoney(plugin.getConfig().getDouble("guild.create.groups." + group + ".region.create"));
		setEffectPrice(plugin.getConfig().getDouble("guild.create.groups." + group + ".region.effectprice"));
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
}
