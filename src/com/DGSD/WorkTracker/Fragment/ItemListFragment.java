package com.DGSD.WorkTracker.Fragment;

import java.text.NumberFormat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.DGSD.WorkTracker.Constants.Extra;
import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.Activity.NewItemActivity;
import com.DGSD.WorkTracker.Data.Field;
import com.DGSD.WorkTracker.Data.Entity.ItemEntity;
import com.DGSD.WorkTracker.Data.Provider.ItemProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * 
 * @author Daniel Grech
 */
public class ItemListFragment extends BaseFragment implements ViewBinder,
		LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener,
		OnItemLongClickListener {

	private ListView mList;

	private String mSortBy;

	private SimpleCursorAdapter mAdapter;

	private static final NumberFormat mCurrencyFormat = NumberFormat
			.getCurrencyInstance();

	private OnListItemLongClickListener mOnListItemLongClickListener;

	private static class CursorCols {
		public static int id = -1;
		public static int name = -1;
		public static int desc = -1;
		public static int price = -1;
	}

	public static ItemListFragment newInstance() {
		return new ItemListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mSortBy = Field.ITEM_NAME.getName();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_item_list, container, false);

		mList = (ListView) v.findViewById(R.id.list);
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);

		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.item_list_item, null,
				new String[] { Field.ID.getName() },
				new int[] { R.id.item_list_item_wrapper }, 0);
		mAdapter.setViewBinder(this);

		mList.setAdapter(mAdapter);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.item_list_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		int id = menuItem.getItemId();

		switch (id) {
			case R.id.search:
				getActivity().onSearchRequested();
				return true;
			case R.id.add:
				Intent intent = new Intent(getActivity(), NewItemActivity.class);
				startActivity(intent);
				return true;
			case R.id.sort_by_name:
				mSortBy = Field.ITEM_NAME.getName();
				getLoaderManager().restartLoader(0, null, this);
				return true;
			case R.id.sort_by_price:
				mSortBy = Field.ITEM_PRICE.getName();
				getLoaderManager().restartLoader(0, null, this);
				return true;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
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
		return new CursorLoader(getActivity(), ItemProvider.CONTENT_URI, null,
				null, null, mSortBy);
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int col) {
		if (view == null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
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

	@Override
	public boolean onItemLongClick(AdapterView<?> list, View view, int pos,
			long id) {
		if (mOnListItemLongClickListener != null) {
			ViewHolder holder = (ViewHolder) view.getTag();

			if (holder != null) {
				mOnListItemLongClickListener.onListItemLongClick(holder.id,
						holder.item);
			}

		}

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int pos, long id) {
		ViewHolder holder = (ViewHolder) view.getTag();

		if (holder != null) {
			doClick(holder.id);
		}
	}

	public void setOnListItemLongClickListener(OnListItemLongClickListener l) {
		mOnListItemLongClickListener = l;
	}

	public void doClick(int itemId) {
		Intent intent = new Intent(getActivity(), NewItemActivity.class);
		intent.putExtra(Extra.ID, itemId);

		getActivity().startActivity(intent);
	}

	private static class ViewHolder {
		public int id;
		public ItemEntity item;
		public TextView price;
		public TextView name;
		public TextView desc;
		public ImageView image;
	}

	public static interface OnListItemLongClickListener {
		public void onListItemLongClick(int id, ItemEntity item);
	}
}
