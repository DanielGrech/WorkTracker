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
import android.text.TextUtils;
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
public class ItemProvider extends ContentProvider {
	protected static final String TAG = ItemProvider.class.getSimpleName();

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ Authority.ITEM_PROVIDER + "/" + Table.ITEMS);
	
	public static final Uri SEARCH_URI = Uri.parse("content://"
			+ Authority.ITEM_PROVIDER + "/" + SearchManager.SUGGEST_URI_PATH_QUERY);

	public static final int MULTIPLE = 0x01;
	public static final int SINGLE = 0x02;
	public static final int SEARCH = 0x03;

	protected static final UriMatcher mURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		mURIMatcher.addURI(Authority.ITEM_PROVIDER, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
		mURIMatcher.addURI(Authority.ITEM_PROVIDER, Table.ITEMS.getName(), MULTIPLE);
		mURIMatcher.addURI(Authority.ITEM_PROVIDER, Table.ITEMS.getName() + "/#", SINGLE);
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
		queryBuilder.setTables(Table.ITEMS.getName());

		int uriType = mURIMatcher.match(uri);

		if (uriType == SINGLE) {
			// We want to get a single item
			queryBuilder.appendWhere(new StringBuilder().append(Field.ID)
					.append("=").append(uri.getLastPathSegment()).toString());
		} else if (uriType == MULTIPLE) {
			// No filter. Return all fields
		} else if(uriType == SEARCH) {
			String query = uri.getLastPathSegment().toLowerCase();

			// Search location name & history extra item
			String where = new StringBuilder().append(Field.ITEM_NAME)
							   .append(" LIKE '%")
							   .append(query)
							   .append("%' OR ")
							   .append(Field.ITEM_DESC)
							   .append(" LIKE '%")
							   .append(query)
							   .append("%'")
							   .toString();
							   
			
			queryBuilder.appendWhere(where);
		} else {
			if(WTApp.DEBUG) {
				Log.w(TAG, "Unknown URI: " + uri);
			}
			return null;
		}

		try {
			Cursor cursor = null;
			
			if(uriType == SEARCH) {
				projection = new String[] {
						Field.ID.getName(),
						Field.ITEM_NAME.getName() + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1,
						Field.ITEM_DESC.getName() + " as " + SearchManager.SUGGEST_COLUMN_TEXT_2,
						Field.ITEM_NAME.getName() + " as " + SearchManager.SUGGEST_COLUMN_QUERY,
						Field.ID.getName() + " as " + SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA
				};
			}

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

		if (uriType == SINGLE || uriType == MULTIPLE) {
			return String.valueOf(uriType);
		} else {
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = mURIMatcher.match(uri);

		if (uriType != MULTIPLE) {
			throw new IllegalArgumentException("Invalid URI for insert");
		}

		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();

		try {
			long newID = sqlDB.insertOrThrow(Table.ITEMS.getName(), null, values);

			if (newID > 0) {
				Uri newUri = ContentUris.withAppendedId(uri, newID);
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
			if (uriType == SINGLE) {
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsAffected = sqlDB.delete(Table.ITEMS.getName(),
							new StringBuilder().append(Field.ID).append("=")
									.append(id).toString(), null);
				} else {
					rowsAffected = sqlDB.delete(Table.ITEMS.getName(), new StringBuilder()
							.append(selection).append(" and ").append(Field.ID)
							.append("=").append(id).toString(), selectionArgs);
				}
			} else if (uriType == MULTIPLE) {
				rowsAffected = sqlDB.delete(Table.ITEMS.getName(), selection,
						selectionArgs);
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
			if (uriType == SINGLE) {
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsAffected = sqlDB.update(Table.ITEMS.getName(), contentValues,
							new StringBuilder().append(Field.ID).append("=")
									.append(id).toString(), null);
				} else {
					rowsAffected = sqlDB.update(
							Table.ITEMS.getName(),
							contentValues,
							new StringBuilder().append(selection)
									.append(" and ").append(Field.ID)
									.append("=").append(id).toString(),
							selectionArgs);
				}
			} else if (uriType == MULTIPLE) {
				rowsAffected = sqlDB.update(Table.ITEMS.getName(), contentValues,
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
