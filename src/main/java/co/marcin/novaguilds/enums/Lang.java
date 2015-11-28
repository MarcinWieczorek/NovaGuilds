package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.Charset;

public enum Lang {
	EN_EN,
	PL_PL,
	DE_DE,
	ZH_CN(Charset.forName("GBK")),
	CUSTOM;

	private Charset charset;

	Lang() {
		charset = Charset.forName("UTF-8");
	}

	Lang(Charset charset) {
		this.charset = charset;
	}

	public Charset getCharset() {
		return charset;
	}

	private void setCharset(Charset charset) {
		this.charset = charset;
	}

	public static Lang fromFile(File file) throws IOException {
		try {
			String langName = StringUtils.replace(StringUtils.replace(file.getName().toUpperCase(), "-", "_"), ".YML", "");
			return Lang.valueOf(langName);
		}
		catch(Exception e) {
			BufferedReader brTest = new BufferedReader(new FileReader(file));
			String line = brTest.readLine();

			if(line.startsWith("#")) {
				line = line.substring(1);
				LoggerUtils.info("Detected custom enconding for file "+file.getName()+": "+line);
				Lang lang = Lang.CUSTOM;
				lang.setCharset(Charset.forName(line));
				return lang;
			}

			LoggerUtils.info("Found custom translation, applying default translation UTF-8");
			LoggerUtils.info("Add #ENCODING to the first line of your language file");
			LoggerUtils.info("Please consider sharing your translation with the community");
			return Lang.CUSTOM;
		}
	}

	public static YamlConfiguration loadConfiguration(File file) throws IOException {
		return YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), Lang.fromFile(file).getCharset()));
	}
}
