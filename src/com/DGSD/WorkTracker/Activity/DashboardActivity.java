/**
 * 
 */
package com.DGSD.WorkTracker.Activity;


import android.support.v4.app.Fragment;

import com.DGSD.InvoiceTracker.R;
import com.DGSD.WorkTracker.Fragment.DashboardFragment;


/** 
 * Show the user the login screen
 *
 * @author Daniel Grech
 */
public class DashboardActivity extends BaseActivitySingleFragment {

	@Override
	protected String getActionBarTitle() {
		return getResources().getString(R.string.dashboard_title);
	}

	@Override
	protected Fragment getFragment() {
		return DashboardFragment.newInstance();
	}
		
}
