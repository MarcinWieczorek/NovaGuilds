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
