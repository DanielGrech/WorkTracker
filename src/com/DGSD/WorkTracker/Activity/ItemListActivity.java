package com.DGSD.WorkTracker.Activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.WTApp;
import com.DGSD.WorkTracker.Data.Entity.ItemEntity;
import com.DGSD.WorkTracker.Fragment.ItemListFragment;
import com.DGSD.WorkTracker.Fragment.ItemListFragment.OnListItemLongClickListener;
import com.DGSD.WorkTracker.Service.DatabaseService;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ItemListActivity extends BaseActivitySingleFragment implements
		DialogInterface.OnClickListener, OnListItemLongClickListener {
	private static final String TAG = ItemListActivity.class.getName();

	private int mLastItemId = -1;
	private String mLastItemTitle = null;

	private AlertDialog mDeleteConfirmDialog;

	protected ActionMode mLongPressActionMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete_item);
		builder.setMessage(R.string.item_delete_message);
		builder.setPositiveButton(android.R.string.yes, this);
		builder.setNegativeButton(android.R.string.cancel, this);

		mDeleteConfirmDialog = builder.create();
	}

	@Override
	protected String getActionBarTitle() {
		return getResources().getString(R.string.item_list_title);
	}

	@Override
	protected Fragment getFragment() {
		ItemListFragment fragment = ItemListFragment.newInstance();
		fragment.setOnListItemLongClickListener(this);
		return fragment;
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			String itemId = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);

			if (itemId == null) {
				Toast.makeText(this, "Couldn't find an item " + query,
						Toast.LENGTH_SHORT).show();
			} else {
				ItemListFragment fragment = (ItemListFragment) mFragment;
				try {
					fragment.doClick(Integer.valueOf(itemId));
				} catch (NumberFormatException e) {
					if (WTApp.DEBUG) {
						Log.e(TAG, "Error formatting id", e);
					}
				}
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int id) {
		switch (id) {
			case DialogInterface.BUTTON_POSITIVE:
				DatabaseService.requestItemDelete(this, mLastItemId);

				Toast.makeText(this, mLastItemTitle + " deleted",
						Toast.LENGTH_SHORT).show();
				
				if(mLongPressActionMode != null) {
					mLongPressActionMode.finish();
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				mDeleteConfirmDialog.dismiss();
				break;
		}
	}

	protected final class LongPressActionMode implements ActionMode.Callback {

		public LongPressActionMode(String title, int itemId) {
			mLastItemTitle = title;
			mLastItemId = itemId;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			getSupportMenuInflater().inflate(R.menu.item_list_contextual_menu,
					menu);

			mode.setTitle(mLastItemTitle);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int id = item.getItemId();

			switch (id) {
				case R.id.delete:
					mDeleteConfirmDialog.show();
					return true;
			}

			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}
	}

	@Override
	public void onListItemLongClick(int id, ItemEntity item) {
		mLongPressActionMode = startActionMode(new LongPressActionMode(
				item.getName(), id));
	}

}
