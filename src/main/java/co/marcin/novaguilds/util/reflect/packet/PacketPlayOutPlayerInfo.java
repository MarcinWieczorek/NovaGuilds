package co.marcin.novaguilds.util.reflect.packet;

import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import com.google.common.base.Charsets;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import java.util.UUID;

@SuppressWarnings("ALL")
public class PacketPlayOutPlayerInfo {

	private static final Class<?> packetClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
	private static final Class<?>[] typesClass = new Class<?>[] { String.class, boolean.class, int.class };
	private static int type = 0;

	static{
		try {
			if(packetClass.getConstructor(typesClass) == null) type = 1;
		} catch (Exception e) {
			type = 1;
		}
	}

	public static Object getPacket(String s, boolean b, int i){
		try {
			if(type == 0){
				return packetClass.getConstructor(typesClass).newInstance(s, b, i);
			}
			else if(type == 1) {
				UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + s).getBytes(Charsets.UTF_8));
				GameProfile profile = null;

				try {
					if(type == 2)
						profile = GameProfile.class.getConstructor(new Class<?>[]{
								String.class,
								String.class
						}).newInstance(uuid.toString(), s);
					else if(type == 1)
						profile = GameProfile.class.getConstructor(new Class<?>[]{
								UUID.class,
								String.class
						}).newInstance(uuid, s);
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				Class<?> clazz = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
				Object packet = packetClass.getConstructor().newInstance();
				Reflections.getPrivateField(clazz, "username").set(packet, s);
				Reflections.getPrivateField(clazz, "gamemode").set(packet, 1);
				Reflections.getPrivateField(clazz, "ping").set(packet, i);
				Reflections.getPrivateField(clazz, "player").set(packet, profile);

				if(!b) {
					Reflections.getPrivateField(clazz, "action").set(packet, 4);
				}

				return packet;
			}
		}
		catch (Exception e){
			LoggerUtils.exception(e);
		}
		return null;
	}

}