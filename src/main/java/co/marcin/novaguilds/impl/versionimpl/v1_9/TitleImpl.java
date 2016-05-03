package co.marcin.novaguilds.impl.versionimpl.v1_9;

import co.marcin.novaguilds.api.util.Title;
import co.marcin.novaguilds.impl.util.AbstractTitle;
import co.marcin.novaguilds.impl.versionimpl.v1_9.packet.PacketPlayOutTitle;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.entity.Player;

public class TitleImpl extends AbstractTitle {
	public TitleImpl() {
		super("", "", -1, -1, -1);
	}

	public TitleImpl(String title) {
		super(title, "", -1, -1, -1);
	}

	public TitleImpl(String title, String subtitle) {
		super(title, subtitle, -1, -1, -1);
	}

	public TitleImpl(Title title) {
		super(title);
	}

	public TitleImpl(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		super(title, subtitle, fadeInTime, stayTime, fadeOutTime);
	}

	@Override
	public void send(Player player) {
		resetTitle(player);

		try {
			if(fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1) {
				new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeInTime * (ticks ? 1 : 20), stayTime * (ticks ? 1 : 20), fadeOutTime * (ticks ? 1 : 20)).send(player);
			}

			String titleJson = "{\"text\":\"" + StringUtils.fixColors(title) + "\"}";
			new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJson).send(player);

			if(subtitle != null) {
				String subtitleJson = "{\"text\":\"" + StringUtils.fixColors(subtitle) + "\"}";
				new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJson).send(player);
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void clearTitle(Player player) {
		try {
			new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.CLEAR, null);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void resetTitle(Player player) {
		try {
			new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, null);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}
}
