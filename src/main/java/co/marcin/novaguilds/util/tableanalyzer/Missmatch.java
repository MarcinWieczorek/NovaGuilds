package co.marcin.novaguilds.util.tableanalyzer;

public class Missmatch {
	private int index;
	private String table;
	private String columnName;
	private String columnType;
	private final ModificationType modificationType;

	public Missmatch(ModificationType modificationType) {
		this.modificationType = modificationType;
	}

	//getters
	public int getIndex() {
		return index;
	}

	public ModificationType getModificationType() {
		return modificationType;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public String getTable() {
		return table;
	}

	//setters
	public void setIndex(int index) {
		this.index = index;
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
