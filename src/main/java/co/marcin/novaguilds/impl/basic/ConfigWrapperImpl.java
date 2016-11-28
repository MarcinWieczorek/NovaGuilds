package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.ConfigWrapper;
import co.marcin.novaguilds.impl.util.AbstractVarKeyApplicable;
import co.marcin.novaguilds.manager.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfigWrapperImpl extends AbstractVarKeyApplicable<ConfigWrapper> implements ConfigWrapper {
	private static final ConfigManager cM = NovaGuilds.getInstance() == null ? null : NovaGuilds.getInstance().getConfigManager();
	private String path;
	private boolean fixColors;

	public ConfigWrapperImpl(String path, boolean fixColors) {
		this.path = path;
		this.fixColors = fixColors;
	}

	@Override
	public String getName() {
		return StringUtils.replace(path, ".", "_").toUpperCase();
	}

	@Override
	public String getPath() {
		if(path == null) {
			throw new IllegalArgumentException("Path has not been set!");
		}

		return path;
	}

	@Override
	public String getString() {
		String r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof String ? (String) cM.getEnumConfig(this) : cM.getString(path, vars, fixColors);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public List<String> getStringList() {
		List<String> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<String>) cM.getEnumConfig(this) : cM.getStringList(path, vars, fixColors);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public List<ItemStack> getItemStackList() {
		List<ItemStack> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<ItemStack>) cM.getEnumConfig(this) : cM.getItemStackList(path, vars);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public List<Material> getMaterialList() {
		List<Material> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<Material>) cM.getEnumConfig(this) : cM.getMaterialList(path, vars);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public long getLong() {
		long r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Long ? (long) cM.getEnumConfig(this) : cM.getLong(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public double getDouble() {
		double r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Double ? (double) cM.getEnumConfig(this) : cM.getDouble(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public int getInt() {
		int r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Integer ? (int) cM.getEnumConfig(this) : cM.getInt(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public boolean getBoolean() {
		boolean r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Boolean ? (boolean) cM.getEnumConfig(this) : cM.getBoolean(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public int getSeconds() {
		int r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Integer ? (int) cM.getEnumConfig(this) : cM.getSeconds(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof ItemStack ? (ItemStack) cM.getEnumConfig(this) : cM.getItemStack(path, vars);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public Material getMaterial() {
		Material r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Material ? (Material) cM.getEnumConfig(this) : cM.getMaterial(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public byte getMaterialData() {
		byte r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Byte ? (byte) cM.getEnumConfig(this) : cM.getMaterialData(path);
		cM.putInCache(this, r);
		return r;
	}

	@Override
	public double getPercent() {
		return getDouble() / 100;
	}

	@Override
	public ConfigurationSection getConfigurationSection() {
		return cM.getConfig().getConfigurationSection(path);
	}

	@Override
	public void set(Object obj) {
		cM.set(this, obj);
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void setFixColors(boolean b) {
		this.fixColors = b;
	}

	@Override
	public <E extends Enum> E toEnum(Class<E> clazz) {
		for(E enumConstant : clazz.getEnumConstants()) {
			if(enumConstant.name().equalsIgnoreCase(getString())) {
				return enumConstant;
			}
		}

		return null;
	}
}
