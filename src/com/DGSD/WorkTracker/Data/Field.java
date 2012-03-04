package com.DGSD.WorkTracker.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a column in a table
 * 
 * @author Daniel Grech
 */
public class Field implements Parcelable {
	/* Common Fields */
	public static final Field ID = new Field("_id", "integer", "primary key");
	public static final Field ITEM_NAME = new Field("_item_name", "text");
	public static final Field ITEM_DESC = new Field("_item_desc", "text");
	public static final Field ITEM_PRICE = new Field("_item_price", "real");
	public static final Field DATE = new Field("_date", "integer");
	public static final Field LOCATION_NAME = new Field("_location_name", "text");
	public static final Field LOCATION_LAT = new Field("_lat", "real");
	public static final Field LOCATION_LON = new Field("_lon", "real");
	public static final Field LIST_NAME = new Field("_list_name", "text");
	public static final Field LIST_ID = new Field("_list_id", "integer");
	public static final Field ITEM_CODE = new Field("_item_code", "text");
	public static final Field QUANTITY = new Field("_quantity", "integer");
	public static final Field TOTAL_PRICE = new Field("_total_price", "real");
	public static final Field TOTAL_NUM_ITEMS = new Field("_total_num_items", "integer");
	
	private String name;
	private String type;
	private String constraint;

	public Field(String n, String t, String c) {
		name = n;
		type = t;
		constraint = c;
	}

	public Field(String n, String t) {
		this(n, t, null);
	}
	
	public Field(Parcel in) {
		name = in.readString();
		type = in.readString();
		constraint = in.readString();
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}
	
	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(type);
		dest.writeString(constraint);
	}

	public static final Parcelable.Creator<Field> CREATOR = new Parcelable.Creator<Field>() {
		public Field createFromParcel(Parcel in) {
			return new Field(in);
		}

		public Field[] newArray(int size) {
			return new Field[size];
		}
	};
}
