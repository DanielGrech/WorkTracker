/**
 * 
 */
package com.DGSD.WorkTracker.Service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.DGSD.WorkTracker.Constants;
import com.DGSD.WorkTracker.Constants.Extra;
import com.DGSD.WorkTracker.Constants.ResultType;
import com.DGSD.WorkTracker.WTApp;
import com.DGSD.WorkTracker.Data.Field;
import com.DGSD.WorkTracker.Data.Entity.ItemEntity;
import com.DGSD.WorkTracker.Data.Provider.ItemProvider;

/**
 * A service which provides generic access to the Applications shared services
 * 
 * @author Daniel Grech
 */
public class DatabaseService extends IntentService {
	private static final String TAG = DatabaseService.class.getSimpleName();

	public DatabaseService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	protected void onHandleIntent(Intent inIntent) {
		int type = inIntent.getIntExtra(Extra.DATA_TYPE, -1);

		switch (type) {
			case RequestType.INSERT: {
				Uri uri = inIntent.getParcelableExtra(Extra.URI);
				ContentValues values = inIntent
						.getParcelableExtra(Extra.CONTENT_VALUES);

				if (doInsert(this, uri, values) == null) {
					broadcastError(type);
				} else {
					broadcastResult(type);
				}

				break;
			}

			case RequestType.UPDATE: {
				Uri uri = inIntent.getParcelableExtra(Extra.URI);
				int id = inIntent.getIntExtra(Extra.ID, -1);
				ContentValues values = inIntent
						.getParcelableExtra(Extra.CONTENT_VALUES);

				if (doUpdate(this,
						Uri.withAppendedPath(uri, String.valueOf(id)), values)) {
					broadcastResult(RequestType.UPDATE);
				} else {
					broadcastError(RequestType.UPDATE);
				}

				break;
			}

			case RequestType.SINGLE_DELETE: {
				Uri uri = inIntent.getParcelableExtra(Extra.URI);
				int id = inIntent.getIntExtra(Extra.ID, -1);

				if (doDeleteSingleRecord(this, uri, id) == 1) {
					broadcastResult(RequestType.SINGLE_DELETE);
				} else {
					broadcastError(RequestType.SINGLE_DELETE);
				}
				break;
			}

			case RequestType.SINGLE_QUERY: {
				Uri uri = inIntent.getParcelableExtra(Extra.URI);
				int id = inIntent.getIntExtra(Extra.ID, -1);
				String[] fields = inIntent.getStringArrayExtra(Extra.FIELDS);

				ContentValues values = null;
				try {
					values = getSingleRecord(this, uri, fields, id);
				} catch (Exception e) {
					if (WTApp.DEBUG) {
						Log.d(TAG, "Error getting single record", e);
					}
				}

				if (values != null) {
					broadcastResult(RequestType.SINGLE_QUERY, values);
				} else {
					broadcastError(RequestType.SINGLE_QUERY);
				}

				break;
			}
		}
	}

	/**
	 * Requests all data from the provider
	 * 
	 * Note this is a <b>blocking</b> call and shouldn't be invoked on the UI
	 * thread
	 */
	public static Cursor doQuery(Context c, Uri provider, String[] projection,
			String selection, String[] args, String sortOrder) {
		return c.getContentResolver().query(provider, projection, selection,
				args, sortOrder);
	}

	/**
	 * Inserts values into the database
	 * 
	 * Note this is a <b>blocking</b> call and shouldn't be invoked on the UI
	 * thread
	 */
	public static Uri doInsert(Context c, Uri provider, ContentValues values) {
		return c.getContentResolver().insert(provider, values);
	}

	/**
	 * Updates values in the database
	 * 
	 * Note this is a <b>blocking</b> call and shouldn't be invoked on the UI
	 * thread
	 */
	public static boolean doUpdate(Context c, Uri provider, ContentValues values) {
		return c.getContentResolver().update(provider, values, null, null) > 0;
	}

	/**
	 * Delete a single row in the database
	 * 
	 * Note this is a <b>blocking</b> call and shouldn't be invoked on the UI
	 * thread
	 */
	public static int doDeleteSingleRecord(Context c, Uri provider, int id) {
		return c.getContentResolver().delete(provider,
				Field.ID.getName() + "=?", new String[] { String.valueOf(id) });
	}

	/**
	 * Delete all matching rows from the database
	 * 
	 * Note this is a <b>blocking</b> call and shouldn't be invoked on the UI
	 * thread
	 */
	public static int doDeleteRecordsWhere(Context c, Uri provider, String where) {
		return c.getContentResolver().delete(provider, where, null);
	}

	/**
	 * Returns a single rows worth of ContentValues.
	 * 
	 * Note this is a <b>blocking</b> call and shouldn't be invoked on the UI
	 * thread
	 */
	public static ContentValues getSingleRecord(Context c, Uri provider,
			String[] fields, int id) {

		return getSingleRecord(c, provider, fields, Field.ID + "=?",
				new String[] { String.valueOf(id) });
	}

	/**
	 * Returns a single rows worth of ContentValues.
	 * 
	 * Note this is a <b>blocking</b> call and shouldn't be invoked on the UI
	 * thread
	 */
	public static ContentValues getSingleRecord(Context c, Uri provider,
			String[] fields, String selection, String[] args) {
		Cursor cursor = c.getContentResolver().query(provider, fields,
				selection, args, null);

		try {
			if (cursor == null || !cursor.moveToFirst()) {
				return null;
			} else {
				ContentValues values = new ContentValues();

				String[] colNames = cursor.getColumnNames();

				for (String colName : colNames) {
					values.put(colName,
							cursor.getString(cursor.getColumnIndex(colName)));
				}
				return values;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private void broadcastResult(int type) {
		Intent intent = new Intent(ResultType.SUCCESS);
		intent.putExtra(Extra.DATA_TYPE, type);
		sendBroadcast(intent, Constants.Permissions.RECEIVE_BROADCASTS);
	}

	private void broadcastResult(int type, ContentValues values) {
		Intent intent = new Intent(ResultType.SUCCESS);
		intent.putExtra(Extra.DATA_TYPE, type);
		intent.putExtra(Extra.CONTENT_VALUES, values);
		sendBroadcast(intent, Constants.Permissions.RECEIVE_BROADCASTS);
	}

	private void broadcastNoData(int type) {
		Intent intent = new Intent(ResultType.NO_RESULT);
		intent.putExtra(Extra.DATA_TYPE, type);
		sendBroadcast(intent, Constants.Permissions.RECEIVE_BROADCASTS);
	}

	private void broadcastError(int type) {
		Intent intent = new Intent(ResultType.ERROR);
		intent.putExtra(Extra.DATA_TYPE, type);
		sendBroadcast(intent, Constants.Permissions.RECEIVE_BROADCASTS);
	}

	public static synchronized void requestItemInsert(Context c, ItemEntity item) {
		Intent intent = new Intent(c, DatabaseService.class);
		intent.putExtra(Extra.DATA_TYPE, RequestType.INSERT);
		intent.putExtra(Extra.URI, ItemProvider.CONTENT_URI);

		ContentValues values = new ContentValues();
		values.put(Field.ITEM_NAME.getName(), item.getName());
		values.put(Field.ITEM_DESC.getName(), item.getDescription());
		values.put(Field.ITEM_PRICE.getName(), item.getPrice());

		intent.putExtra(Extra.CONTENT_VALUES, values);

		c.startService(intent);
	}

	public static synchronized void requestItemQuery(Context c, int id) {
		Intent intent = new Intent(c, DatabaseService.class);
		intent.putExtra(Extra.DATA_TYPE, RequestType.SINGLE_QUERY);
		intent.putExtra(Extra.URI, ItemProvider.CONTENT_URI);
		intent.putExtra(Extra.ID, id);

		c.startService(intent);
	}

	public static synchronized void requestItemDelete(Context c, int id) {
		Intent intent = new Intent(c, DatabaseService.class);
		intent.putExtra(Extra.DATA_TYPE, RequestType.SINGLE_DELETE);
		intent.putExtra(Extra.URI, ItemProvider.CONTENT_URI);
		intent.putExtra(Extra.ID, id);

		c.startService(intent);
	}

	public static synchronized void requestItemUpdate(Context c, int id,
			ItemEntity item) {
		Intent intent = new Intent(c, DatabaseService.class);
		intent.putExtra(Extra.DATA_TYPE, RequestType.UPDATE);
		intent.putExtra(Extra.URI, ItemProvider.CONTENT_URI);
		intent.putExtra(Extra.ID, id);

		ContentValues values = new ContentValues();
		values.put(Field.ITEM_NAME.getName(), item.getName());
		values.put(Field.ITEM_DESC.getName(), item.getDescription());
		values.put(Field.ITEM_PRICE.getName(), item.getPrice());

		intent.putExtra(Extra.CONTENT_VALUES, values);

		c.startService(intent);
	}

	public static final class RequestType {
		public static final int INSERT = 0x00000001;
		public static final int SINGLE_DELETE = 0x00000002;
		public static final int UPDATE = 0x00000003;
		public static final int SINGLE_QUERY = 0x00000004;
	}
}
