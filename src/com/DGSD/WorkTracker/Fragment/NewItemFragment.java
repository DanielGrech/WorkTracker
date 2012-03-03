package com.DGSD.WorkTracker.Fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.DGSD.WorkTracker.Constants.Extra;
import com.DGSD.WorkTracker.Constants.ResultType;
import com.DGSD.WorkTracker.MoneyValueFilter;
import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.Data.Field;
import com.DGSD.WorkTracker.Data.Entity.ItemEntity;
import com.DGSD.WorkTracker.Service.DatabaseService;
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
public class NewItemFragment extends BaseFragment implements
		DialogInterface.OnClickListener {

	private int mInitialId = -1;
	private ItemEntity mIntialItem;

	private StatefulEditText mItemNameInput;
	private StatefulEditText mPriceInput;
	private StatefulEditText mItemDescriptionInput;
	private Button mNewImageButton;

	private AlertDialog mDeleteConfirmDialog;
	private AlertDialog mChangedDataConfirmDialog;

	public static NewItemFragment newInstance() {
		return NewItemFragment.newInstance(-1);
	}

	public static NewItemFragment newInstance(int id) {
		NewItemFragment fragment = new NewItemFragment();

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

		if (getArguments() != null) {
			mInitialId = getArguments().getInt(Extra.ID, -1);
		}

		if (mInitialId >= 0) {
			DatabaseService.requestItemQuery(getActivity(), mInitialId);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.delete_item);
		builder.setMessage(R.string.item_delete_message);
		builder.setPositiveButton(android.R.string.yes, this);
		builder.setNegativeButton(android.R.string.cancel, this);

		mDeleteConfirmDialog = builder.create();

		builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.changed_data);
		builder.setMessage(R.string.changed_data_message);
		builder.setPositiveButton(android.R.string.yes, this);
		builder.setNegativeButton(android.R.string.cancel, this);

		mChangedDataConfirmDialog = builder.create();

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_new_item, container, false);

		mItemNameInput = (StatefulEditText) v
				.findViewById(R.id.item_details_name);
		mItemDescriptionInput = (StatefulEditText) v
				.findViewById(R.id.item_details_description);
		mPriceInput = (StatefulEditText) v
				.findViewById(R.id.item_details_price);
		mNewImageButton = (Button) v.findViewById(R.id.btn_add_new_image);

		mPriceInput.setFilters(new InputFilter[] { new MoneyValueFilter(2) });

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.new_item_menu, menu);

		if (mInitialId >= 0) {
			menu.findItem(R.id.delete).setVisible(true);
		} else {
			menu.findItem(R.id.delete).setVisible(false);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if(mDeleteConfirmDialog != null && mDeleteConfirmDialog.isShowing()){
			mDeleteConfirmDialog.dismiss();
		}
		
		if(mChangedDataConfirmDialog != null && mChangedDataConfirmDialog.isShowing()) {
			mChangedDataConfirmDialog.dismiss();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		int id = menuItem.getItemId();

		switch (id) {
			case R.id.save: {
				if (isValid()) {

					ItemEntity item = getItem();

					if (mInitialId < 0) {
						// Add new item
						DatabaseService.requestItemInsert(getActivity(), item);
						Toast.makeText(getActivity(), "Item Saved",
								Toast.LENGTH_SHORT).show();
					} else {
						// Update the existing item
						DatabaseService.requestItemUpdate(getActivity(),
								mInitialId, item);

						Toast.makeText(getActivity(), "Item Updated",
								Toast.LENGTH_SHORT).show();
					}

					getActivity().finish();
				}

				return true;
			}

			case R.id.delete: {
				mDeleteConfirmDialog.show();
				return true;
			}
			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	@Override
	protected boolean doRegisterReceivers() {
		return true;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(Extra.DATA_TYPE, -1);
		String resultType = intent.getAction();

		switch (type) {
			case DatabaseService.RequestType.SINGLE_QUERY: {
				if (resultType == ResultType.SUCCESS) {
					// Restore the values of the query
					ContentValues values = intent
							.getParcelableExtra(Extra.CONTENT_VALUES);
					if (values != null) {
						mIntialItem = new ItemEntity();
						mIntialItem.setName(values.getAsString(Field.ITEM_NAME
								.getName()));
						mIntialItem.setDescription(values
								.getAsString(Field.ITEM_DESC.getName()));
						mIntialItem.setPrice(values.getAsFloat(Field.ITEM_PRICE
								.getName()));

						mItemNameInput.setText(mIntialItem.getName());
						mItemDescriptionInput.setText(mIntialItem
								.getDescription());
						mPriceInput.setText(String.valueOf(mIntialItem
								.getPrice()));
					}
				} else if (resultType == ResultType.ERROR) {
					Toast.makeText(getActivity(), "Error retrieving item",
							Toast.LENGTH_SHORT).show();
					mInitialId = -1;

					getActivity().invalidateOptionsMenu();
				}
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int id) {
		switch (id) {
			case DialogInterface.BUTTON_POSITIVE:
				if (dialog == mDeleteConfirmDialog) {
					DatabaseService
							.requestItemDelete(getActivity(), mInitialId);

					Toast.makeText(getActivity(),
							mItemNameInput.getText() + " deleted",
							Toast.LENGTH_SHORT).show();

					getActivity().finish();
				} else {
					getActivity().finish();
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				if(dialog == mDeleteConfirmDialog) {
					mDeleteConfirmDialog.dismiss();
				} else {
					mChangedDataConfirmDialog.dismiss();
				}
				
				break;
		}
	}

	public String getName() {
		return mItemNameInput.getText() == null ? null : mItemNameInput
				.getText().toString().trim();
	}

	public String getDescription() {
		return mItemDescriptionInput.getText() == null ? null
				: mItemDescriptionInput.getText().toString().trim();
	}

	public float getPrice() {
		if (mPriceInput.getText() == null
				|| mPriceInput.getText().toString().trim().length() == 0) {
			return 0.0F;
		}

		float price = 0.0F;

		try {
			price = Float.parseFloat(mPriceInput.getText().toString());
		} catch (NumberFormatException e) {
			// o well..
		}

		return price;
	}

	public ItemEntity getItem() {
		return new ItemEntity(getName(), getDescription(), getPrice());
	}

	public boolean isValid() {
		boolean valid = true;

		if (mItemNameInput.getText() == null
				|| mItemNameInput.getText().toString().trim().length() == 0) {
			valid = false;
			mItemNameInput.setError("Please enter an item name");
		} else if (mPriceInput.getText() != null
				|| mPriceInput.getText().toString().trim().length() > 0) {
			String priceText = mPriceInput.getText().toString();

			float price = 0.0F;
			try {
				price = Float.parseFloat(priceText);
			} catch (NumberFormatException e) {
				// O well..
			}

			if (price < 0) {
				valid = false;
				mPriceInput
						.setError("Price must be greater than or equal to 0");
			}
		}

		return valid;
	}
	
	public boolean onExit() {
		if (mInitialId >= 0) {
			// We are updating a record
			if (!getItem().equals(mIntialItem)) {
				// We are updating and some changes were made!
				mChangedDataConfirmDialog.show();
				return false;
			}
		}
		
		return true;
	}
}
