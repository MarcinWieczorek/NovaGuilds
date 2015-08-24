package co.marcin.novaguilds.event;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected Entity clickedEntity;
	private EntityUseAction action;
	private boolean cancelled = false;

	public enum EntityUseAction {
		ATTACK,
		INTERACT;
	}

	public static EntityUseAction actionFromProtocolLib(EnumWrappers.EntityUseAction action) {
		return EntityUseAction.valueOf(action.name());
	}

	public PlayerInteractEntityEvent(final Player who, final Entity clickedEntity, EnumWrappers.EntityUseAction action) {
		super(who);
		this.clickedEntity = clickedEntity;
		this.action = actionFromProtocolLib(action);
	}

	public PlayerInteractEntityEvent(final Player who, final Entity clickedEntity, EntityUseAction action) {
		super(who);
		this.clickedEntity = clickedEntity;
		this.action = action;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	/**
	 * Gets the entity that was rightclicked by the player.
	 *
	 * @return entity right clicked by player
	 */
	public Entity getEntity() {
		return this.clickedEntity;
	}

	public EntityUseAction getAction() {
		return action;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}