package com.DGSD.WorkTracker.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.DGSD.WorkTracker.Constants.Extra;
import com.DGSD.WorkTracker.MoneyValueFilter;
import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.Activity.ItemListActivity;
import com.DGSD.WorkTracker.Data.Entity.ItemEntity;
import com.DGSD.WorkTracker.View.NumberPickerView;
import com.DGSD.WorkTracker.View.StatefulEditText;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * TODO Add '$' to the front of the price input
 * 
 * TODO Add images
 * 
 * @author Daniel Grech
 */
public class AddItemToListFragment extends BaseFragment implements
		OnClickListener {

	private static final int KEY_GET_SAVED_ITEM = 0x01;

	private Button mGetFromSavedItemsBtn;
	private AutoCompleteTextView mItemNameInput;
	private StatefulEditText mCodeInput;
	private StatefulEditText mPriceInput;
	private StatefulEditText mItemDescriptionInput;
	private NumberPickerView mQuantityPicker;

	public static AddItemToListFragment newInstance() {
		return AddItemToListFragment.newInstance(-1);
	}

	public static AddItemToListFragment newInstance(int id) {
		AddItemToListFragment fragment = new AddItemToListFragment();

		if (id >= 0) {
			Bundle args = new Bundle();
			args.putInt(Extra.ID, id);
			fragment.setArguments(args);
		}

		return fragment;
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
				.inflate(R.layout.fragment_edit_list, container, false);

		mGetFromSavedItemsBtn = (Button) v
				.findViewById(R.id.populate_from_saved_items_btn);

		mItemNameInput = (AutoCompleteTextView) v.findViewById(R.id.item_name);
		mCodeInput = (StatefulEditText) v.findViewById(R.id.item_code);
		mItemDescriptionInput = (StatefulEditText) v
				.findViewById(R.id.item_description);
		mPriceInput = (StatefulEditText) v.findViewById(R.id.item_price);
		mQuantityPicker = (NumberPickerView) v.findViewById(R.id.item_quantity);

		mGetFromSavedItemsBtn.setOnClickListener(this);
		mPriceInput.setFilters(new InputFilter[] { new MoneyValueFilter(2) });

		return v;
	}

	@Override
	protected boolean doRegisterReceivers() {
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_item_to_list_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		int id = menuItem.getItemId();

		switch (id) {
			case R.id.add_to_list:
				System.err.println("ADD TO LIST");
				return true;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.populate_from_saved_items_btn:
				Intent intent = new Intent(getActivity(),
						ItemListActivity.class);

				startActivityForResult(intent, KEY_GET_SAVED_ITEM);
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		switch (requestCode) {
			case KEY_GET_SAVED_ITEM:
				if (resultCode == Activity.RESULT_OK) {
					ItemEntity item = intent.getParcelableExtra(Extra.ITEM);
					if (item != null) {
						mItemNameInput.setText(item.getName());
						mItemDescriptionInput.setText(item.getDescription());
						mPriceInput.setText(String.valueOf(item.getPrice()));
					}
				}
				break;

			default:
				super.onActivityResult(requestCode, resultCode, intent);
				break;
		}
	}

}
