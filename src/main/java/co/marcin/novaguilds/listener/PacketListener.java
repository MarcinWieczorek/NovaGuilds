package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.event.PacketReceiveEvent;
import co.marcin.novaguilds.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PacketListener implements Listener {
	private final NovaGuilds plugin;

	public PacketListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onPacketReceive(PacketReceiveEvent event) {
		if(event.getPacketName().equals("PacketPlayInUseEntity")) {
			PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity) event.getPacket();
			PlayerInteractEntityEvent.EntityUseAction action = PlayerInteractEntityEvent.EntityUseAction.valueOf(packetPlayInUseEntity.c().name());
			Class<?> useEntityClass = Reflections.getCraftClass("PacketPlayInUseEntity");
			Reflections.FieldAccessor<Integer> useEntityA = Reflections.getField(useEntityClass, int.class, 0);
			int id = useEntityA.get(packetPlayInUseEntity);

			Entity entity = null;
			for(Entity e : event.getPlayer().getNearbyEntities(5, 5, 5)) {
				if(e.getEntityId() == id) {
					entity = e;
				}
			}

			if(entity == null) {
				LoggerUtils.debug("PacketPlayInUseEntity - Entity is null. ID=" + id);
				return;
			}

			PlayerInteractEntityEvent clickEvent = new PlayerInteractEntityEvent(event.getPlayer(), entity, action);
			plugin.getServer().getPluginManager().callEvent(clickEvent);
		}
	}
}
