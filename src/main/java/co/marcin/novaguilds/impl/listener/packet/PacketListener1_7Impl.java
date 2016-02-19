/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.impl.listener.packet;

import co.marcin.novaguilds.api.util.AbstractListener;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.event.PacketReceiveEvent;
import co.marcin.novaguilds.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.util.reflect.Reflections;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class PacketListener1_7Impl extends AbstractListener {
	@EventHandler
	public void onPacketReceive(PacketReceiveEvent event) {
		switch(event.getPacketName()) {
			case "PacketPlayInUseEntity":
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
					return;
				}

				PlayerInteractEntityEvent clickEvent = new PlayerInteractEntityEvent(event.getPlayer(), entity, action);
				plugin.getServer().getPluginManager().callEvent(clickEvent);
				break;
		}
	}
}
