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

package co.marcin.novaguilds.util.reflect;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class Reflections {
	/**
	 * Gets NMS class
	 *
	 * @param name class name
	 * @return class
	 * @throws ClassNotFoundException when the class doesn't exist
	 */
	public static Class<?> getCraftClass(String name) throws ClassNotFoundException {
		String className = "net.minecraft.server." + getVersion() + name;
		return Class.forName(className);
	}

	/**
	 * Gets CraftBukkit class
	 *
	 * @param name class name
	 * @return class
	 * @throws ClassNotFoundException when the class doesn't exist
	 */
	public static Class<?> getBukkitClass(String name) throws ClassNotFoundException {
		String className = "org.bukkit.craftbukkit." + getVersion() + name;
		return Class.forName(className);
	}

	/**
	 * Gets the handle of an entity
	 *
	 * @param entity target entity
	 * @return entity handle
	 * @throws InvocationTargetException when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 */
	public static Object getHandle(Entity entity) throws InvocationTargetException, IllegalAccessException {
		return getMethod(entity.getClass(), "getHandle").invoke(entity);
	}

	/**
	 * Gets the handle of a world
	 *
	 * @param world target world
	 * @return world handle
	 * @throws InvocationTargetException when something goes wrong
	 * @throws IllegalAccessException    when something goes wrong
	 */
	public static Object getHandle(World world) throws InvocationTargetException, IllegalAccessException {
		return getMethod(world.getClass(), "getHandle").invoke(world);
	}

	/**
	 * Gets a field
	 *
	 * @param cl        class
	 * @param fieldName field name
	 * @return field
	 * @throws NoSuchFieldException when something goes wrong
	 */
	public static Field getField(Class<?> cl, String fieldName) throws NoSuchFieldException {
		return cl.getDeclaredField(fieldName);
	}

	/**
	 * Gets a field
	 *
	 * @param target    class
	 * @param fieldType field type
	 * @param index     index
	 * @param <T>       type
	 * @return field accessor
	 */
	public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
		return getField(target, null, fieldType, index);
	}

	/**
	 * Gets a field
	 *
	 * @param target    class
	 * @param name      field name
	 * @param fieldType field type
	 * @param index     index
	 * @param <T>       type
	 * @return field accessor
	 */
	private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
		for(final Field field : target.getDeclaredFields()) {
			if((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
				field.setAccessible(true);

				return new FieldAccessor<T>() {
					@SuppressWarnings("unchecked")
					@Override
					public T get(Object target) {
						try {
							return (T) field.get(target);
						}
						catch(IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public void set(Object target, Object value) {
						try {
							field.set(target, value);
						}
						catch(IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public boolean hasField(Object target) {
						return field.getDeclaringClass().isAssignableFrom(target.getClass());
					}
				};
			}
		}

		if(target.getSuperclass() != null) {
			return getField(target.getSuperclass(), name, fieldType, index);
		}

		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}

	/**
	 * Gets a private field
	 *
	 * @param cl        class
	 * @param fieldName field name
	 * @return field
	 * @throws NoSuchFieldException when a field doesn't exist
	 */
	public static Field getPrivateField(Class<?> cl, String fieldName) throws NoSuchFieldException {
		Field field = cl.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}

	/**
	 * Gets a method
	 *
	 * @param cl     class
	 * @param method method name
	 * @param args   argument classes
	 * @return method
	 */
	public static Method getMethod(Class<?> cl, String method, Class<?>... args) {
		for(Method m : cl.getMethods()) {
			if(m.getName().equals(method) && classListEqual(args, m.getParameterTypes())) {
				return m;
			}
		}

		return null;
	}

	/**
	 * Gets a method
	 *
	 * @param cl     class
	 * @param method method name
	 * @return method
	 */
	public static Method getMethod(Class<?> cl, String method) {
		for(Method m : cl.getMethods()) {
			if(m.getName().equals(method)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Compares two lists of classess
	 *
	 * @param l1 list 1
	 * @param l2 list 2
	 * @return boolean
	 */
	public static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) {
		if(l1.length != l2.length) {
			return false;
		}

		for(int i = 0; i < l1.length; i++) {
			if(l1[i] != l2[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Gets CraftBukkit version
	 *
	 * @return the version
	 */
	public static String getVersion() {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".";
	}

	public interface ConstructorInvoker {
		/**
		 * Invokes a constructor
		 *
		 * @param arguments argumnets
		 * @return instance
		 */
		Object invoke(Object... arguments);
	}

	public interface MethodInvoker {
		/**
		 * Invokes a method
		 *
		 * @param target    target object
		 * @param arguments arguments
		 * @return returned object
		 */
		Object invoke(Object target, Object... arguments);
	}

	public interface FieldAccessor<T> {
		/**
		 * Gets a field
		 *
		 * @param target target object
		 * @return field
		 */
		T get(Object target);

		/**
		 * Sets a value to a field
		 *
		 * @param target target object
		 * @param value  value
		 */
		void set(Object target, Object value);

		/**
		 * Checks if object has specified field
		 *
		 * @param target target object
		 * @return boolean
		 */
		boolean hasField(Object target);
	}
}
