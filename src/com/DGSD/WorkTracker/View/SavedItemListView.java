package com.DGSD.WorkTracker.View;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.Data.Field;
import com.DGSD.WorkTracker.Data.Entity.ItemEntity;
import com.DGSD.WorkTracker.Data.Provider.ItemProvider;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * List to show saved items
 * 
 * @author Daniel Grech
 */
public class SavedItemListView extends ListView implements
		LoaderManager.LoaderCallbacks<Cursor>, ViewBinder {

	private String mSortBy;

	private SimpleCursorAdapter mAdapter;

	private static final NumberFormat mCurrencyFormat = NumberFormat
			.getCurrencyInstance();

	private static class CursorCols {
		public static int id = -1;
		public static int name = -1;
		public static int desc = -1;
		public static int price = -1;
	}

	public static class ViewHolder {
		public int id;
		public ItemEntity item;
		public TextView price;
		public TextView name;
		public TextView desc;
		public ImageView image;
	}

	public SavedItemListView(Context context, AttributeSet set) {
		super(context, set);
		init();
	}
	
	
	public SavedItemListView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		mSortBy = Field.ITEM_NAME.getName() + " COLLATE NOCASE";

		mAdapter = new SimpleCursorAdapter(getContext(),
				R.layout.item_list_item, null,
				new String[] { Field.ID.getName() },
				new int[] { R.id.item_list_item_wrapper }, 0);
		mAdapter.setViewBinder(this);

		setAdapter(mAdapter);
		
		((SherlockFragmentActivity) getContext()).getSupportLoaderManager()
		.initLoader(0, null, this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getContext(), ItemProvider.CONTENT_URI, null,
				null, null, mSortBy);
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int col) {
		if (view == null) {

			LayoutInflater inflater = ((Activity) getContext())
					.getLayoutInflater();
			view = inflater.inflate(R.layout.item_list_item, null);
		}

		if (CursorCols.id < 0) {
			CursorCols.id = cursor.getColumnIndex(Field.ID.getName());
			CursorCols.name = cursor.getColumnIndex(Field.ITEM_NAME.getName());
			CursorCols.desc = cursor.getColumnIndex(Field.ITEM_DESC.getName());
			CursorCols.price = cursor
					.getColumnIndex(Field.ITEM_PRICE.getName());
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.price = (TextView) view.findViewById(R.id.price);
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.desc = (TextView) view.findViewById(R.id.description);
			holder.image = (ImageView) view.findViewById(R.id.image);
		}

		holder.id = cursor.getInt(CursorCols.id);
		holder.name.setText(cursor.getString(CursorCols.name));
		holder.desc.setText(cursor.getString(CursorCols.desc));
		holder.price.setText(mCurrencyFormat.format(cursor
				.getFloat(CursorCols.price)));

		if (holder.item == null) {
			holder.item = new ItemEntity();
		}

		holder.item.setName(cursor.getString(CursorCols.name));
		holder.item.setDescription(cursor.getString(CursorCols.desc));
		holder.item.setPrice(cursor.getFloat(CursorCols.price));

		view.setTag(holder);
		return true;
	}

	public void setSortBy(String sortBy) {
		mSortBy = sortBy;

		((SherlockFragmentActivity) getContext()).getSupportLoaderManager()
				.restartLoader(0, null, this);
	}
}
