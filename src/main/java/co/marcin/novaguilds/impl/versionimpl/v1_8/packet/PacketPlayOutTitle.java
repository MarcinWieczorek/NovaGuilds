package co.marcin.novaguilds.impl.versionimpl.v1_8.packet;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("ConstantConditions")
public class PacketPlayOutTitle extends AbstractPacket {
	protected static final NovaGuilds plugin = NovaGuilds.getInstance();
	protected static Class<?> packetTitleClass;
	protected static Class<?> titleActionsClass;
	protected static Class<?> chatSerializerClass;
	protected static Class<?> chatBaseComponentClass;

	public enum EnumTitleAction {
		TITLE(0),
		SUBTITLE(1),
		TIMES(2),
		CLEAR(3),
		RESET(4);

		private final Object[] actions = titleActionsClass.getEnumConstants();
		private final Object action;

		/**
		 * The constructor
		 *
		 * @param id NMS enum ID
		 */
		EnumTitleAction(int id) {
			action = actions[id];
		}

		/**
		 * Gets the action
		 *
		 * @return NMS enum object
		 */
		public Object getCraftAction() {
			return action;
		}
	}

	static {
		try {
			packetTitleClass = Reflections.getCraftClass("PacketPlayOutTitle");
			titleActionsClass = Reflections.getCraftClass("PacketPlayOutTitle$EnumTitleAction");
			chatSerializerClass = Reflections.getCraftClass("IChatBaseComponent$ChatSerializer");
			chatBaseComponentClass = Reflections.getCraftClass("IChatBaseComponent");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * The constructor
	 *
	 * @param action action
	 * @param json   json
	 * @throws NoSuchMethodException     if version incompatible
	 * @throws IllegalAccessException    if version incompatible
	 * @throws InvocationTargetException if version incompatible
	 * @throws InstantiationException    if version incompatible
	 */
	public PacketPlayOutTitle(EnumTitleAction action, String json) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		this(action, json, -1, -1, -1);
	}

	/**
	 * The constructor
	 *
	 * @param action  action
	 * @param json    json
	 * @param fadeIn  fade in time
	 * @param stay    stay time
	 * @param fadeOut fade out time
	 * @throws NoSuchMethodException     if version incompatible
	 * @throws IllegalAccessException    if version incompatible
	 * @throws InvocationTargetException if version incompatible
	 * @throws InstantiationException    if version incompatible
	 */
	public PacketPlayOutTitle(EnumTitleAction action, String json, int fadeIn, int stay, int fadeOut) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Object serialized = null;

		//Serialize json
		if(json != null) {
			serialized = Reflections.getMethod(chatSerializerClass, "a", String.class).invoke(null, json);
		}

		packet = packetTitleClass.getConstructor(titleActionsClass, chatBaseComponentClass, Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(action.getCraftAction(), serialized, fadeIn, stay, fadeOut);
	}
}
