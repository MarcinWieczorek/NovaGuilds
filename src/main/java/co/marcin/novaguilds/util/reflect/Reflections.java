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

package co.marcin.novaguilds.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class Reflections {
	public static Class<?> getCraftClass(String name) {
		String className = "net.minecraft.server." + getVersion() + name;
		Class<?> c = null;
		try {
			c = Class.forName(className);
		}
		catch (Exception e) {
			LoggerUtils.exception(e);
		}
		return c;
	}

	public static Class<?> getBukkitClass(String name) {
		String className = "org.bukkit.craftbukkit." + getVersion() + name;
		Class<?> c = null;
		try {
			c = Class.forName(className);
		}
		catch (Exception e) {
			LoggerUtils.exception(e);
		}
		return c;
	}

	public static Object getHandle(Entity entity) {
		try {
			return getMethod(entity.getClass(), "getHandle").invoke(entity);
		}
		catch (Exception e){
			LoggerUtils.exception(e);
			return null;
		}
	}

	public static Object getHandle(World world) {
		try {
			return getMethod(world.getClass(), "getHandle").invoke(world);
		}
		catch (Exception e){
			LoggerUtils.exception(e);
			return null;
		}
	}

	public static Field getField(Class<?> cl, String field_name) {
		try {
			return cl.getDeclaredField(field_name);
		}
		catch (Exception e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
		return getField(target, null, fieldType, index);
	}

	private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
		for (final Field field : target.getDeclaredFields()) {
			if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
				field.setAccessible(true);

				return new FieldAccessor<T>() {
					@SuppressWarnings("unchecked")
					@Override
					public T get(Object target) {
						try {
							return (T) field.get(target);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}

					@Override
					public void set(Object target, Object value) {
						try {
							field.set(target, value);
						} catch (IllegalAccessException e) {
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

		if (target.getSuperclass() != null)
			return getField(target.getSuperclass(), name, fieldType, index);
		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}

	public static Field getPrivateField(Class<?> cl, String field_name) {
		try {
			Field field = cl.getDeclaredField(field_name);
			field.setAccessible(true);
			return field;
		}
		catch (Exception e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	public static Method getMethod(Class<?> cl, String method, Class<?>... args) {
		for (Method m : cl.getMethods())
			if (m.getName().equals(method) && classListEqual(args, m.getParameterTypes()))
				return m;
		return null;
	}

	public static Method getMethod(Class<?> cl, String method) {
		for (Method m : cl.getMethods())
			if (m.getName().equals(method))
				return m;
		return null;
	}

	public static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) {
		boolean equal = true;
		if (l1.length != l2.length)
			return false;
		for (int i = 0; i < l1.length; i++)
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		return equal;
	}

	public static String getVersion(){
		String name = Bukkit.getServer().getClass().getPackage().getName();
		String version = name.substring(name.lastIndexOf('.') + 1) + ".";
		return version;
	}

	public interface ConstructorInvoker {
		Object invoke(Object... arguments);
	}

	public interface MethodInvoker {
		Object invoke(Object target, Object... arguments);
	}

	public interface FieldAccessor<T> {
		T get(Object target);
		void set(Object target, Object value);
		boolean hasField(Object target);
	}

}