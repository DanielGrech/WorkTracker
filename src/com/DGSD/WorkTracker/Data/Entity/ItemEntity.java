package com.DGSD.WorkTracker.Data.Entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemEntity implements Parcelable {

	private String mName;
	private String mDescription;
	private float mPrice = 0.0F;

	public ItemEntity() {
		
	}
	
	public ItemEntity(String name, String desc, float price) {
		mName = name;
		mDescription = desc;
		mPrice = price;
	}

	private static ItemEntity fromParcel(Parcel in) {
		return new ItemEntity(in.readString(), in.readString(), in.readFloat());
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		this.mDescription = description;
	}

	public float getPrice() {
		return mPrice;
	}

	public void setPrice(float price) {
		this.mPrice = price;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeString(mDescription);
		dest.writeFloat(mPrice);
	}

	public static final Parcelable.Creator<ItemEntity> CREATOR = new Parcelable.Creator<ItemEntity>() {
		public ItemEntity createFromParcel(Parcel in) {
			return ItemEntity.fromParcel(in);
		}

		public ItemEntity[] newArray(int size) {
			return new ItemEntity[size];
		}
	};
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ItemEntity) || obj == null) {
			return false;
		}
		
		ItemEntity item = (ItemEntity) obj;
		
		return areEqual(item.getName(), mName) &&
			   areEqual(item.getDescription(), mDescription) &&
			   item.getPrice() == mPrice;
	}
	
	/**
	 * Null safe check on object equality
	 */
	private boolean areEqual(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

}
