package co.marcin.novaguilds.impl.versionimpl.v1_9.packet;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("ConstantConditions")
public class PacketPlayOutTitle {
	protected static final NovaGuilds plugin = NovaGuilds.getInstance();
	protected static Class<?> packetTitle = Reflections.getCraftClass("PacketPlayOutTitle");
	protected static Class<?> titleActionsClass = Reflections.getCraftClass("PacketPlayOutTitle$EnumTitleAction");
	protected static final Class<?> chatSerializerClass = Reflections.getCraftClass("IChatBaseComponent$ChatSerializer");
	protected static final Class<?> chatBaseComponentClass = Reflections.getCraftClass("IChatBaseComponent");
	private final Object packet;

	public enum EnumTitleAction {
		TITLE(0),
		SUBTITLE(1),
		TIMES(2),
		CLEAR(3),
		RESET(4);

		private final Object[] actions = titleActionsClass.getEnumConstants();
		private final Object action;

		EnumTitleAction(int id) {
			action = actions[id];
		}

		public Object getCraftAction() {
			return action;
		}
	}

	static {
		try {
			packetTitle = Reflections.getCraftClass("PacketPlayOutTitle");
			titleActionsClass = Reflections.getCraftClass("PacketPlayOutTitle$EnumTitleAction");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}


	public PacketPlayOutTitle(EnumTitleAction action, String json) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		this(action, json, -1, -1, -1);
	}

	public PacketPlayOutTitle(EnumTitleAction action, String json, int fadeIn, int stay, int fadeOut) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Object serialized = null;

		//Serialize json
		if(json != null) {
			serialized = Reflections.getMethod(chatSerializerClass, "a", String.class).invoke(null, json);
		}

		packet = packetTitle.getConstructor(titleActionsClass, chatBaseComponentClass, Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(action.getCraftAction(), serialized, fadeIn, stay, fadeOut);
	}

	public void send(Player player) {
		PacketSender.sendPacket(player, packet);
	}
}
