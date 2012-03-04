package com.DGSD.WorkTracker.Data;

/**
 * The tables used in the WT database
 * 
 * @author Daniel Grech
 */
public class Table {
	
	/**
	 * Table to hold saved items only
	 */
	public static final Table ITEMS = new Table("_items", new Field[] {
		Field.ID,
		Field.ITEM_NAME,
		Field.ITEM_DESC,
		Field.ITEM_PRICE
	});
	
	/**
	 * Table to hold items associated with a list
	 */
	public static final Table LIST_ITEMS = new Table("_list_items", new Field[] {
		Field.ID,
		Field.LIST_ID,
		Field.ITEM_CODE,
		Field.ITEM_NAME,
		Field.ITEM_DESC,
		Field.ITEM_PRICE,
		Field.QUANTITY
	});
	
	/**
	 * Table to hold users individual lists
	 */
	public static final Table LISTS = new Table("_lists", new Field[] {
		Field.ID,
		Field.DATE,
		Field.LIST_NAME,
		Field.LOCATION_NAME,
		Field.LOCATION_LAT,
		Field.LOCATION_LON
	});
	
	private String name;
	private Field[] fields;

	public Table(String n, Field[] f) {
		name = n;
		fields = f;
	}
	
	public String getName() {
		return name;
	}

	public Field[] getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return name;
	}

	public String[] getFieldNames() {
		String[] results = new String[fields.length];

		for (int i = 0, size = fields.length; i < size; i++) {
			results[i] = fields[i].getName();
		}

		return results;
	}
	
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String OPEN_BRACKET = "(";
	public static final String CLOSE_BRACKET = ")";
	public static final String CREATE_TABLE = "CREATE TABLE";
	public static final String DROP_TABLE = "DROP TABLE";
	
	public static String doDropTable(Table table) {
		return new StringBuilder().append(DROP_TABLE).append(SPACE)
				.append(table.getName()).toString();
	}

	public static String doCreateTable(Table table) {
		StringBuilder builder = new StringBuilder().append(CREATE_TABLE)
				.append(SPACE).append(table.getName()).append(SPACE)
				.append(OPEN_BRACKET);

		// Ensure that a comma does not appear on the last iteration
		String comma = "";
		Field[] fields = table.getFields();
		for (Field field : fields) {
			builder.append(comma);
			comma = COMMA;

			builder.append(field.getName());
			builder.append(SPACE);
			builder.append(field.getType());
			builder.append(SPACE);

			if (field.getConstraint() != null) {
				builder.append(field.getConstraint());
			}
		}

		builder.append(CLOSE_BRACKET);

		return builder.toString();

	}
}