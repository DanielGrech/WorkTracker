/**
 * 
 */
package com.DGSD.WorkTracker.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.DGSD.InvoiceTracker.R;
import com.DGSD.WorkTracker.Utils.IntentUtils;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

/** 
 * Show the user the login screen
 *
 * @author Daniel Grech
 */
public abstract class BaseActivitySingleFragment extends SherlockFragmentActivity {
	private static final String KEY_FRAGMENT = "_fragment";
	
	protected ActionBar mActionBar;
	
	protected FragmentManager mFragmentManager;
	
	protected Fragment mFragment;
	
	protected abstract String getActionBarTitle();
	
	protected abstract Fragment getFragment();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_with_single_fragment);
		
		//Set up the action bar
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getActionBarTitle());
		
		//Present our fragment
		mFragmentManager = getSupportFragmentManager();
		
		if(savedInstanceState != null) {
			mFragment =  mFragmentManager.getFragment(savedInstanceState, KEY_FRAGMENT);
        }

        if(mFragment == null) {
        	mFragment = getFragment();

            mFragmentManager.beginTransaction()
			                .replace(R.id.container, mFragment)
			                .commit();
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mFragment != null && mFragment.isAdded()) {
            mFragmentManager.putFragment(outState, KEY_FRAGMENT, mFragment);
        }
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
            	//Action to take when pressing the home button
                IntentUtils.goHome(this);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
	
}
