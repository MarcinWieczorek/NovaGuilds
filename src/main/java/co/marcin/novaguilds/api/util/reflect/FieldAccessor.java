package co.marcin.novaguilds.api.util.reflect;

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

	/**
	 * Sets the field as not final
	 */
	void setNotFinal();
}
