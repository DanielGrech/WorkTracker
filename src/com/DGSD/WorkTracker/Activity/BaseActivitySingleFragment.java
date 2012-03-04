/**
 * 
 */
package com.DGSD.WorkTracker.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.DGSD.WorkTracker.R;

/** 
 * Show the user the login screen
 *
 * @author Daniel Grech
 */
public abstract class BaseActivitySingleFragment extends BaseActivity {
	static final String KEY_FRAGMENT = "_fragment";
	
	protected abstract String getActionBarTitle();
	
	protected abstract Fragment getFragment();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_with_single_fragment);
		
		mActionBar.setTitle(getActionBarTitle());
		
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
}
