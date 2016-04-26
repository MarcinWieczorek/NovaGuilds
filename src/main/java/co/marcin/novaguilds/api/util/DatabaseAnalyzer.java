package co.marcin.novaguilds.api.util;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseAnalyzer {
	enum ModificationType {
		/**
		 * Adds a column at the end of a table
		 */
		ADD,

		/**
		 * Adds a column in the middle of a table
		 */
		ADD_INSIDE,

		/**
		 * Removes a column
		 */
		REMOVE,

		/**
		 * Moves a column and pushes all columns after
		 */
		MOVE,

		/**
		 * Renames a column
		 */
		RENAME,

		/**
		 * Changes column type
		 */
		CHANGETYPE
	}

	/**
	 * Analyzes a table
	 *
	 * @param table table name
	 * @param sql target table create code
	 * @throws SQLException when anything bad happens
	 */
	void analyze(String table, String sql) throws SQLException;

	/**
	 * Updates the database
	 *
	 * @throws SQLException when anything bad happens
	 */
	void update() throws SQLException;

	/**
	 * Gets missmatches
	 *
	 * @return missmatch list
	 */
	List<Missmatch> getMissmatches();

	interface Missmatch {
		/**
		 * Gets column index
		 *
		 * @return the index
		 */
		int getIndex();

		/**
		 * Gets modification type
		 *
		 * @return modification type
		 */
		ModificationType getModificationType();

		/**
		 * Gets column name
		 *
		 * @return column name
		 */
		String getColumnName();

		/**
		 * Gets column type
		 *
		 * @return column type
		 */
		String getColumnType();

		/**
		 * Gets table name
		 *
		 * @return table name
		 */
		String getTableName();
	}
}
