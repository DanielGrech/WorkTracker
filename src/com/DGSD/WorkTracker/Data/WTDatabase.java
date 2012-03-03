/**
 * 
 */
package com.DGSD.WorkTracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.DGSD.WorkTracker.WTApp;

/**
 * Provides an interface & helper methods to the local Sqlite3 instance
 * 
 * @author Daniel Grech
 */
public class WTDatabase extends SQLiteOpenHelper {
	private static final String TAG = WTDatabase.class.getSimpleName();

	private static final int VERSION = 1;

	public static final String DATABASE_NAME = "work_tracker.db";

	public WTDatabase(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (WTApp.DEBUG) {
			Log.i(TAG, "Creating database: " + DATABASE_NAME);
		}

		db.execSQL(Table.doCreateTable(Table.ITEMS));
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(Table.doDropTable(Table.ITEMS));
		
		this.onCreate(db);
	}
}
