package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.api.util.DatabaseAnalyzer;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAnalyzerImpl implements DatabaseAnalyzer {
	private final Connection connection;
	private final Map<String, String> sqlStructure = new HashMap<>();
	private final Map<Integer, String> sqlNames = new HashMap<>();
	private final Map<String, String> tableStructure = new HashMap<>();
	private final List<Missmatch> missmatches = new ArrayList<>();

	public DatabaseAnalyzerImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void analyze(String table, String sql) throws SQLException {
		if(!existsTable(table)) {
			addTable(sql);
		}

		missmatches.clear();
		getSqlStructure(sql);
		getTableStructure(table);

		List<String> sqlKeys = new ArrayList<>();
		sqlKeys.addAll(sqlNames.values());

		//search for ADD
		if(tableStructure.size() < sqlKeys.size()) {
			int newindex = tableStructure.size();

			for(int i = newindex; i < sqlStructure.size(); i++) {
				MissmatchImpl missmatch = new MissmatchImpl();
				missmatch.setModificationType(ModificationType.ADD);
				missmatch.setTable(table);
				String name = sqlKeys.get(i - 1);

				if(!tableStructure.keySet().contains(name)) {
					missmatch.setColumnName(name);
					missmatch.setColumnType(sqlStructure.get(name));
					missmatch.setIndex(i);

					missmatches.add(missmatch);
				}
			}
		}
	}

	@Override
	public void update() throws SQLException {
		sort();
		for(Missmatch Missmatch : missmatches) {
			LoggerUtils.debug(Missmatch.getModificationType().name() + ": " + Missmatch.getIndex() + " " + Missmatch.getColumnName() + " " + Missmatch.getColumnType());
			switch(Missmatch.getModificationType()) {
				case ADD:
					addColumn(Missmatch);
					break;
			}
		}
	}

	@Override
	public List<Missmatch> getMissmatches() {
		return missmatches;
	}

	private void addColumn(Missmatch Missmatch) throws SQLException {
		String sql = "ALTER TABLE `" + Missmatch.getTableName() + "` ADD COLUMN `" + Missmatch.getColumnName() + "` " + Missmatch.getColumnType() + " NOT NULL;";
		Statement statement = connection.createStatement();
		statement.execute(sql);
		LoggerUtils.info("Added new column " + Missmatch.getColumnName() + " to table " + Missmatch.getTableName());
	}

	private void addTable(String sql) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute(sql);
		LoggerUtils.info("Added new table");
	}

	private void sort() {
		Collections.sort(missmatches, new Comparator<Missmatch>() {
			public int compare(Missmatch o1, Missmatch o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
	}

	private void getSqlStructure(String sql) {
		String[] cols = org.apache.commons.lang.StringUtils.split(sql, ",\r\n");
		HashMap<String, String> map = new HashMap<>();
		sqlNames.clear();

		int i = 0;
		for(String c : cols) {
			if(c.startsWith("  `")) {
				String[] split = StringUtils.split(c, ' ');
				String name = org.apache.commons.lang.StringUtils.replace(split[0], "`", "");
				map.put(name, split[1]);

				sqlNames.put(i, name);
				i++;
			}
		}

		sqlStructure.clear();
		sqlStructure.putAll(map);
	}

	private void getTableStructure(String table) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet columns = databaseMetaData.getColumns(null, null, table, null);
		HashMap<String, String> map = new HashMap<>();

		while(columns.next()) {
			String columnName = columns.getString("COLUMN_NAME");
			String columnType = columns.getString("TYPE_NAME");

			map.put(columnName, columnType);
		}
		columns.close();

		tableStructure.clear();
		tableStructure.putAll(map);
	}

	private boolean existsTable(String table) throws SQLException {
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while(rs.next()) {
			if(rs.getString(3).equalsIgnoreCase(table)) {
				return true;
			}
		}

		return false;
	}

	public class MissmatchImpl implements DatabaseAnalyzer.Missmatch {
		private int index;
		private String table;
		private String columnName;
		private String columnType;
		private ModificationType modificationType;

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public ModificationType getModificationType() {
			return modificationType;
		}

		@Override
		public String getColumnName() {
			return columnName;
		}

		@Override
		public String getColumnType() {
			return columnType;
		}

		@Override
		public String getTableName() {
			return table;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setModificationType(ModificationType modificationType) {
			this.modificationType = modificationType;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public void setColumnType(String columnType) {
			this.columnType = columnType;
		}

		public void setTable(String table) {
			this.table = table;
		}
	}
}
