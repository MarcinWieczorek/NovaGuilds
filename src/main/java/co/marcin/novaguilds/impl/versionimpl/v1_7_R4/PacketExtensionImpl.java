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

package co.marcin.novaguilds.impl.versionimpl.v1_7_R4;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.event.PacketReceiveEvent;
import co.marcin.novaguilds.api.event.PacketSendEvent;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class PacketExtensionImpl extends co.marcin.novaguilds.impl.versionimpl.v1_8_R3.PacketExtensionImpl {
	private static Reflections.FieldAccessor<Channel> clientChannel;
	private static Field playerConnection;
	private static Field networkManager;
	private static Method handleMethod;
	protected static Class<?> packetClass;
	protected static Class<?> craftPlayerClass;

	static {
		try {
			clientChannel = Reflections.getField(Reflections.getCraftClass("NetworkManager"), Channel.class, 0);
			playerConnection = Reflections.getField(Reflections.getCraftClass("EntityPlayer"), "playerConnection");
			networkManager = Reflections.getField(Reflections.getCraftClass("PlayerConnection"), "networkManager");
			handleMethod = Reflections.getMethod(Reflections.getBukkitClass("entity.CraftEntity"), "getHandle");
			packetClass = Reflections.getCraftClass("Packet");
			craftPlayerClass = Reflections.getBukkitClass("entity.CraftPlayer");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets the channel
	 *
	 * @param player player
	 * @return the Channel
	 */
	private static Channel getChannel(Player player) {
		try {
			Object eP = handleMethod.invoke(player);
			return clientChannel.get(networkManager.get(playerConnection.get(eP)));
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	@Override
	public void registerPlayer(final Player player) {
		Channel c = getChannel(player);
		ChannelHandler handler = new ChannelDuplexHandler() {
			@Override
			public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				PacketSendEvent event = new PacketSendEvent(msg, player);
				ListenerManager.getLoggedPluginManager().callEvent(event);

				if(event.isCancelled() || event.getPacket() == null) {
					return;
				}

				super.write(ctx, event.getPacket(), promise);
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				PacketReceiveEvent event = new PacketReceiveEvent(msg, player);
				ListenerManager.getLoggedPluginManager().callEvent(event);
				if(event.isCancelled() || event.getPacket() == null) {
					return;
				}

				super.channelRead(ctx, event.getPacket());
			}

		};
		ChannelPipeline cp = c.pipeline();
		cp.addBefore("packet_handler", "NovaGuilds", handler);
	}

	@Override
	public void unregisterChannel() {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			getChannel(player).pipeline().remove("NovaGuilds");
		}
	}
}
