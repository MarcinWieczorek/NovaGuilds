package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class AbstractTitle implements Title {
	/* Title text and color */
	protected String title = "";
	protected ChatColor titleColor = ChatColor.WHITE;

	/* Subtitle text and color */
	protected String subtitle = "";
	protected ChatColor subtitleColor = ChatColor.WHITE;

	/* Title timings */
	protected int fadeInTime = -1;
	protected int stayTime = -1;
	protected int fadeOutTime = -1;
	protected boolean ticks = false;

	/**
	 * The constructor
	 */
	public AbstractTitle() {
		this("", "", -1, -1, -1);
	}

	/**
	 * The constructor
	 *
	 * @param title title string
	 */
	public AbstractTitle(String title) {
		this(title, "", -1, -1, -1);
	}

	/**
	 * The constructor
	 *
	 * @param title    title string
	 * @param subtitle subtitle string
	 */
	public AbstractTitle(String title, String subtitle) {
		this(title, subtitle, -1, -1, -1);
	}

	/**
	 * Copy title
	 *
	 * @param title Title
	 */
	public AbstractTitle(Title title) {
		this(title.getTitle(), title.getSubtitle(), title.getFadeInTime(), title.getStayTime(), title.getFadeOutTime());
		titleColor = title.getTitleColor();
		subtitleColor = title.getSubtitleColor();
		ticks = title.getTicks();
	}

	/**
	 * Create a new title
	 *
	 * @param title       Title text
	 * @param subtitle    Subtitle text
	 * @param fadeInTime  Fade in time
	 * @param stayTime    Stay on screen time
	 * @param fadeOutTime Fade out time
	 */
	public AbstractTitle(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		this.title = title;
		this.subtitle = subtitle;
		this.fadeInTime = fadeInTime;
		this.stayTime = stayTime;
		this.fadeOutTime = fadeOutTime;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@Override
	public String getSubtitle() {
		return subtitle;
	}

	@Override
	public ChatColor getTitleColor() {
		return titleColor;
	}

	@Override
	public ChatColor getSubtitleColor() {
		return subtitleColor;
	}

	@Override
	public int getFadeInTime() {
		return fadeInTime;
	}

	@Override
	public int getFadeOutTime() {
		return fadeOutTime;
	}

	@Override
	public int getStayTime() {
		return stayTime;
	}

	@Override
	public boolean getTicks() {
		return ticks;
	}

	@Override
	public void setTitleColor(ChatColor color) {
		titleColor = color;
	}

	@Override
	public void setSubtitleColor(ChatColor color) {
		subtitleColor = color;
	}

	@Override
	public void setFadeInTime(int time) {
		fadeInTime = time;
	}

	@Override
	public void setFadeOutTime(int time) {
		fadeOutTime = time;
	}

	@Override
	public void setStayTime(int time) {
		stayTime = time;
	}

	@Override
	public void setTimingsToTicks() {
		ticks = true;
	}

	@Override
	public void setTimingsToSeconds() {
		ticks = false;
	}

	@Override
	public void broadcast() {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			send(player);
		}
	}
}
