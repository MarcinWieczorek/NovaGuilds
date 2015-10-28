/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.EntityUseAction;
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
			EntityUseAction action = EntityUseAction.valueOf(packetPlayInUseEntity.c().name());
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
