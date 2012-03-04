package com.DGSD.WorkTracker.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.DGSD.WorkTracker.Constants.Extra;
import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.Activity.NewItemActivity;
import com.DGSD.WorkTracker.Data.Field;
import com.DGSD.WorkTracker.Data.Entity.ItemEntity;
import com.DGSD.WorkTracker.View.SavedItemListView;
import com.DGSD.WorkTracker.View.SavedItemListView.ViewHolder;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * 
 * @author Daniel Grech
 */
public class ItemListFragment extends BaseFragment implements
		OnItemClickListener, OnItemLongClickListener {

	private SavedItemListView mList;

	private OnListItemLongClickListener mOnListItemLongClickListener;

	public static ItemListFragment newInstance() {
		return new ItemListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_item_list, container, false);

		mList = (SavedItemListView) v.findViewById(R.id.list);
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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
				mList.setSortBy(Field.ITEM_NAME.getName() + " COLLATE NOCASE");
				return true;
			case R.id.sort_by_price:
				mList.setSortBy(Field.ITEM_PRICE.getName());
				return true;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
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
			doClick(holder.id, holder.item);
		}
	}

	public void setOnListItemLongClickListener(OnListItemLongClickListener l) {
		mOnListItemLongClickListener = l;
	}

	public void doClick(int itemId, ItemEntity item) {
		Intent activityIntent = getActivity().getIntent();
		if(activityIntent.getIntExtra(Extra.REQUEST_CODE, -1) != -1) {
			//This activity was started from startActivityFromResult()
			activityIntent.putExtra(Extra.ITEM, item);
			getActivity().setResult(Activity.RESULT_OK, activityIntent);
			getActivity().finish();
		} else {
			Intent intent = new Intent(getActivity(), NewItemActivity.class);
			intent.putExtra(Extra.ID, itemId);

			getActivity().startActivity(intent);
		}
	}

	public static interface OnListItemLongClickListener {
		public void onListItemLongClick(int id, ItemEntity item);
	}
}
