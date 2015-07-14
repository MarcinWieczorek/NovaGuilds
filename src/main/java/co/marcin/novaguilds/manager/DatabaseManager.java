package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import code.husky.mysql.MySQL;
import code.husky.sqlite.SQLite;

import java.sql.*;

public class DatabaseManager {
	private final NovaGuilds plugin;
	private long mySQLReconnectStamp = System.currentTimeMillis();
	private MySQL mySQL;
	private Connection connection = null;

	public DatabaseManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public void mysqlReload() {
		if(plugin.getConfigManager().getDataStorageType() != DataStorageType.MYSQL) {
			return;
		}

		long stamp = System.currentTimeMillis();

		if(stamp - mySQLReconnectStamp > 3000) {
			try {
				mySQL.closeConnection();
				try {
					connection = mySQL.openConnection();
					LoggerUtils.info("MySQL reconnected");
					mySQLReconnectStamp = System.currentTimeMillis();
				}
				catch (ClassNotFoundException e) {
					LoggerUtils.exception(e);
				}
			}
			catch (SQLException e1) {
				LoggerUtils.exception(e1);
			}
		}
	}

	public void connectToMysql() {
		mySQL = new MySQL(plugin,
				plugin.getConfig().getString("mysql.host"),
				plugin.getConfig().getString("mysql.port"),
				plugin.getConfig().getString("mysql.database"),
				plugin.getConfig().getString("mysql.username"),
				plugin.getConfig().getString("mysql.password")
		);

		try {
			connection = mySQL.openConnection();
		}
		catch(SQLException|ClassNotFoundException e) {
			LoggerUtils.exception(e);
		}

		LoggerUtils.info("Connected to MySQL database");
	}

	public void connectToSQLite() {
		SQLite sqlite = new SQLite(plugin, "sqlite.db");
		try {
			connection = sqlite.openConnection();
		}
		catch(SQLException|ClassNotFoundException e) {
			LoggerUtils.exception(e);
		}

		LoggerUtils.info("Connected to SQLite database");
	}

	public void setupTables() {
		try {
			if(plugin.getConfigManager().getDataStorageType() != DataStorageType.FLAT) {
				DatabaseMetaData md = getConnection().getMetaData();
				ResultSet rs = md.getTables(null, null, plugin.getConfigManager().getDatabasePrefix() + "%", null);
				if(!rs.next()) {
					LoggerUtils.info("Couldn't find tables in the base. Creating...");
					String[] SQLCreateCode = getSQLCreateCode();
					if(SQLCreateCode.length != 0) {
						try {
							for(String tableCode : SQLCreateCode) {
								createTable(tableCode);
								LoggerUtils.info("Tables added to the database!");
							}
						}
						catch(SQLException e) {
							LoggerUtils.info("Could not create tables. Switching to secondary storage.");
							plugin.getConfigManager().setToSecondaryDataStorageType();
							LoggerUtils.exception(e);
						}
					}
					else {
						LoggerUtils.info("Couldn't find SQL create code for tables!");
						plugin.getConfigManager().setDataStorageType(DataStorageType.FLAT);
					}
				}
				else {
					LoggerUtils.info("No database config required.");
				}
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	//true=mysql, false=sqlite
	private String[] getSQLCreateCode() {
		int index = plugin.getConfigManager().getDataStorageType()==DataStorageType.MYSQL ? 0 : 1;

		String url = "http://novaguilds.marcin.co/sqltables.txt";
		String sql = StringUtils.getContent(url);

		String[] types = sql.split("--TYPE--");
		return types[index].split("--");
	}

	private void createTable(String sql) throws SQLException {
		mysqlReload();
		Statement statement;
		sql = StringUtils.replace(sql, "{SQLPREFIX}", plugin.getConfigManager().getDatabasePrefix());
		statement = getConnection().createStatement();
		statement.executeUpdate(sql);
	}

	public Connection getConnection() {
		return connection;
	}
}
