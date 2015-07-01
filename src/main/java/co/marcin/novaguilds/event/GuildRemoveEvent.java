package co.marcin.novaguilds.event;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.AbandonCause;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildRemoveEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final NovaGuild guild;
	private boolean cancelled;
	private AbandonCause cause;

	public GuildRemoveEvent(NovaGuild guild) {
		this.guild = guild;
	}

	public GuildRemoveEvent(NovaGuild guild, AbandonCause cause) {
		this.guild = guild;
		this.cause = cause;
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

	public void setCause(AbandonCause cause) {
		this.cause = cause;
	}

	public AbandonCause getCause() {
		return cause;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
