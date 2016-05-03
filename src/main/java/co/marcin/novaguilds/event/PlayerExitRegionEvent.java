package co.marcin.novaguilds.event;

import co.marcin.novaguilds.api.basic.NovaRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerExitRegionEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private NovaRegion region;

	/**
	 * The constructor
	 *
	 * @param who    player
	 * @param region region
	 */
	public PlayerExitRegionEvent(Player who, NovaRegion region) {
		super(who);
		this.region = region;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the region
	 *
	 * @return exited region
	 */
	public NovaRegion getRegion() {
		return region;
	}

	/**
	 * Gets handler list
	 *
	 * @return the handler list
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
