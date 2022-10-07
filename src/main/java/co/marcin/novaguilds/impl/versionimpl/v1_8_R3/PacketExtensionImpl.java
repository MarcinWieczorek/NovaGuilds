/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2018 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.impl.versionimpl.v1_8_R3;

import co.marcin.novaguilds.api.event.PacketReceiveEvent;
import co.marcin.novaguilds.api.event.PacketSendEvent;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.api.util.reflect.FieldAccessor;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class PacketExtensionImpl implements PacketExtension {
    protected static FieldAccessor<Channel> clientChannelField;
    protected static Field networkManagerField;
    protected static Field playerConnectionField;
    protected static Method handleMethod;
    protected static Method sendPacketMethod;
    protected static Class<?> packetClass;
    protected static Class<?> craftPlayerClass;
    protected static Class<?> entityPlayerClass;
    protected static Class<?> playerConnectionClass;
    protected static Class<?> craftEntityClass;
    protected static Class<?> networkManagerClass;

    static {
        try {
            networkManagerClass = Reflections.getCraftClass("NetworkManager");
            playerConnectionClass = Reflections.getCraftClass("PlayerConnection");
            craftEntityClass = Reflections.getBukkitClass("entity.CraftEntity");
            packetClass = Reflections.getCraftClass("Packet");
            craftPlayerClass = Reflections.getBukkitClass("entity.CraftPlayer");
            entityPlayerClass = Reflections.getCraftClass("EntityPlayer");
            handleMethod = Reflections.getMethod(craftEntityClass, "getHandle");
            sendPacketMethod = Reflections.getMethod(playerConnectionClass, "sendPacket", packetClass);
            playerConnectionField = Reflections.getField(entityPlayerClass, "playerConnection");
            clientChannelField = Reflections.getField(networkManagerClass, Channel.class, 0);
            networkManagerField = Reflections.getField(playerConnectionClass, "networkManager");
        }
        catch(Exception e) {
            LoggerUtils.exception(e);
        }
    }

    /**
     * Gets the Channel
     *
     * @param player the player
     * @return the channel
     */
    private static Channel getChannel(Player player) {
        try {
            Object eP = handleMethod.invoke(player);
            return clientChannelField.get(networkManagerField.get(playerConnectionField.get(eP)));
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

                super.write(ctx, msg, promise);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                try {
                    if(msg == null) {
                        return;
                    }

                    PacketReceiveEvent event = new PacketReceiveEvent(msg, player);
                    ListenerManager.getLoggedPluginManager().callEvent(event);

                    if(event.isCancelled() || event.getPacket() == null) {
                        return;
                    }
                    super.channelRead(ctx, event.getPacket());
                }
                catch(Exception e) {
                    super.channelRead(ctx, msg);
                }
            }
        };

        ChannelPipeline cp = c.pipeline();
        if(cp.names().contains("packet_handler")) {
            if(cp.names().contains("NovaGuilds")) {
                cp.replace("NovaGuilds", "NovaGuilds", handler);
            }
            else {
                cp.addBefore("packet_handler", "NovaGuilds", handler);
            }
        }
    }

    @Override
    public void unregisterChannel() {

    }

    @Override
    public void sendPacket(Player player, Object... packets) {
        try {
            Object handle = Reflections.getHandle(player);
            Object playerConnection = playerConnectionField.get(handle);

            for(Object packet : packets) {
                if(packet == null) {
                    continue;
                }

                if(!packetClass.isInstance(packet)) {
                    throw new IllegalArgumentException("Argument Type missmatch. Expected: " + packetClass.getName() + " got " + packet.getClass());
                }

                sendPacketMethod.invoke(playerConnection, packet);
            }
        }
        catch(Exception e) {
            LoggerUtils.exception(e);
        }
    }
}
