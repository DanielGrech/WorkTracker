package com.DGSD.WorkTracker.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.DGSD.InvoiceTracker.R;
import com.DGSD.WorkTracker.WTApp;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Shows a dashboard view with large buttons of the most commonly used actions
 * in the application
 * 
 * TODO: Fix Up Icons!
 * 
 * @author Daniel Grech
 */
public class DashboardFragment extends BaseFragment implements
		View.OnClickListener {
	private static final String TAG = DashboardFragment.class.getSimpleName();

	private Button mNewListBtn;
	private Button mNewItemBtn;
	private Button mPreviousListsBtn;
	private Button mSettingsBtn;

	public static DashboardFragment newInstance() {
		DashboardFragment f = new DashboardFragment();

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// This fragment has an options menu
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_dashboard, container, false);

		if(WTApp.DEBUG) {
			Log.v(TAG, "Creating View");
		}
			
		// Get references to dashboard buttons
		mNewListBtn = (Button) v.findViewById(R.id.btn_dashboard_new_list);
		mNewItemBtn = (Button) v.findViewById(R.id.btn_dashboard_new_item);
		mPreviousListsBtn = (Button) v.findViewById(R.id.btn_dashboard_previous_lists);
		mSettingsBtn = (Button) v.findViewById(R.id.btn_dashboard_settings);

		// Set listeners for buttons
		mNewListBtn.setOnClickListener(this);
		mNewItemBtn.setOnClickListener(this);
		mPreviousListsBtn.setOnClickListener(this);
		mSettingsBtn.setOnClickListener(this);
		
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (WTApp.DEBUG)
			Log.v(TAG, "Saving State");

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate our menu resource
		//inflater.inflate(R.menu.dashboard_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO: Implement!
		int id = v.getId();

		switch (id) {
			case R.id.btn_dashboard_new_list: {

				break;
			}
			case R.id.btn_dashboard_previous_lists: {

				break;
			}
			
			case R.id.btn_dashboard_settings: {
				break;
			}
			case R.id.btn_dashboard_new_item: {
				
				break;
			}
		}

	}
}
