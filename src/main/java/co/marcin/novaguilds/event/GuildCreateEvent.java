package co.marcin.novaguilds.event;

import co.marcin.novaguilds.basic.NovaGuild;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildCreateEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final NovaGuild guild;
	private boolean cancelled;

	public GuildCreateEvent(NovaGuild guild) {
		this.guild = guild;
	}

	public NovaGuild getGuild() {
		return guild;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
