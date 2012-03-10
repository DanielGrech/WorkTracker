package com.DGSD.WorkTracker.Data.Provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.DGSD.WorkTracker.Constants.Authority;
import com.DGSD.WorkTracker.WTApp;
import com.DGSD.WorkTracker.Data.Field;
import com.DGSD.WorkTracker.Data.Table;
import com.DGSD.WorkTracker.Data.WTDatabase;

/**
 * Description: Base instance for all providers
 * 
 * @author Daniel Grech
 */
public class ListProvider extends ContentProvider {
	protected static final String TAG = ListProvider.class.getSimpleName();

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ Authority.LIST_PROVIDER + "/" + Table.ITEMS);
	
	public static final Uri SEARCH_URI = Uri.parse("content://"
			+ Authority.LIST_PROVIDER + "/" + SearchManager.SUGGEST_URI_PATH_QUERY);

	public static final int LIST_ITEM = 0x01;
	public static final int LIST = 0x02;
	public static final int SEARCH = 0x03;

	protected static final UriMatcher mURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		mURIMatcher.addURI(Authority.LIST_PROVIDER, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
		mURIMatcher.addURI(Authority.LIST_PROVIDER, Table.LISTS.getName() + "/_list_item", LIST_ITEM);
		mURIMatcher.addURI(Authority.LIST_PROVIDER, Table.LISTS.getName() + "/_list", LIST);
	}
	
	protected WTDatabase mDatabase;

	@Override
	public boolean onCreate() {
		mDatabase = new WTDatabase(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		int uriType = mURIMatcher.match(uri);
		
		if(uriType == LIST) {
			String tables = new StringBuilder().append(Table.LISTS)
											   .append(" LEFT OUTER JOIN ")
											   .append(Table.LIST_ITEMS)
											   .append(" ON ")
											   .append(Table.LISTS)
											   .append(".")
											   .append(Field.ID)
											   .append(" = ")
											   .append(Table.LIST_ITEMS)
											   .append(".")
											   .append(Field.LIST_ID)
											   .toString();
			
			projection = new String[] {
					Table.LISTS.getName() + "." + Field.ID.getName(),
					Field.DATE.getName(),
					Field.LIST_NAME.getName(),
					Field.LOCATION_NAME.getName(),
					Field.LOCATION_LAT.getName(),
					Field.LOCATION_LON.getName(),
					Field.ITEM_CODE.getName(),
					Field.ITEM_NAME.getName(),
					Field.ITEM_DESC.getName(),
					Field.ITEM_PRICE.getName(),
					Field.QUANTITY.getName()
			};
			
			queryBuilder.setTables(tables);
		} else if(uriType == LIST_ITEM) {
			queryBuilder.setTables(Table.LIST_ITEMS.getName());
		} else if(uriType == SEARCH) {
			
		} else {
			if(WTApp.DEBUG) {
				Log.w(TAG, "Unknown URI: " + uri);
			}
			return null;
		}

		try {
			Cursor cursor = null;

			cursor = queryBuilder.query(mDatabase.getReadableDatabase(),
					projection, selection, selectionArgs, null, null, sort);
			
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		} catch (Exception e) {
			if(WTApp.DEBUG) {
				Log.e(TAG, "Error querying database: " + e.toString());
			}
			return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		int uriType = mURIMatcher.match(uri);

		if (uriType == LIST || uriType == LIST_ITEM) {
			return String.valueOf(uriType);
		} else {
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();

		int uriType = mURIMatcher.match(uri);
		
		try {
			long newId = -1;
			if(uriType == LIST) {
				newId = sqlDB.insertOrThrow(Table.LISTS.getName(), null, values);
			} else if(uriType == LIST_ITEM) {
				newId = sqlDB.insertOrThrow(Table.LIST_ITEMS.getName(), null, values);
			} else {
				throw new IllegalArgumentException("Invalid uri: " + uri);
			}
			
			if (newId > 0) {
				Uri newUri = ContentUris.withAppendedId(uri, newId);
				getContext().getContentResolver().notifyChange(uri, null);
				return newUri;
			} else {
				throw new SQLException("Failed to insert row into " + uri);
			}
		} catch (SQLiteConstraintException e) {
			if(WTApp.DEBUG) {
				Log.e(TAG, "ContentValues: " + values);
				Log.e(TAG, "Ignoring constraint failure", e);
			}
		} catch (Exception e) {
			if(WTApp.DEBUG) {
				Log.e(TAG, "Error inserting into database", e);
			}
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = mURIMatcher.match(uri);

		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();

		try {
			int rowsAffected = 0;
			if (uriType == LIST) {
				rowsAffected = sqlDB.delete(Table.LISTS.getName(), selection, selectionArgs);
			} else if (uriType == LIST_ITEM) {
				rowsAffected = sqlDB.delete(Table.LIST_ITEMS.getName(), selection, selectionArgs);
			} else {
				throw new IllegalArgumentException("Unknown or Invalid URI "
						+ uri);
			}

			getContext().getContentResolver().notifyChange(uri, null);
			return rowsAffected;
		} catch (Exception e) {
			if(WTApp.DEBUG) {
				Log.e(TAG, "Error deleting item: " + e.toString());
			}
			return 0;
		}
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection,
			String[] selectionArgs) {
		int uriType = mURIMatcher.match(uri);

		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();

		try {
			int rowsAffected = 0;
			if (uriType == LIST) {
				rowsAffected = sqlDB.update(Table.LISTS.getName(), contentValues,
						selection, selectionArgs);
			} else if (uriType == LIST_ITEM) {
				rowsAffected = sqlDB.update(Table.LIST_ITEMS.getName(), contentValues,
						selection, selectionArgs);
			} else {
				throw new IllegalArgumentException("Unknown or Invalid URI "
						+ uri);
			}

			getContext().getContentResolver().notifyChange(uri, null);
			return rowsAffected;
		} catch (Exception e) {
			if(WTApp.DEBUG) {
				Log.e(TAG, "Error updating entry: " + e.toString());
			}
			return 0;
		}
	}
}
