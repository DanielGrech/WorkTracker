package com.DGSD.WorkTracker.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public abstract class BaseActivity extends SherlockFragmentActivity {

	private static final String KEY_LOADING_COUNTER = "_loading_counter";
	
	protected ActionBar mActionBar;

	protected FragmentManager mFragmentManager;
	
	protected Fragment mFragment;
	
	protected int mLoadingCounter = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		//Set up the action bar
		mActionBar = getSupportActionBar();
		
		//Present our fragment
		mFragmentManager = getSupportFragmentManager();
		
	    if (savedInstanceState != null) {
			mLoadingCounter = savedInstanceState.getInt(KEY_LOADING_COUNTER, 0);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		
		if (mLoadingCounter > 0) {
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_LOADING_COUNTER, mLoadingCounter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	        case android.R.id.home: {
	        	//Action to take when pressing the home button
	            finish();
	            return true;
	        }
	        default: {
	            return super.onOptionsItemSelected(item);
	        }
	    }
	}

	public void showProgressBar() {
		mLoadingCounter++;
		setSupportProgressBarIndeterminateVisibility(true);
	}

	public void hideProgressBar() {
		mLoadingCounter--;
	
		if (mLoadingCounter < 0) {
			mLoadingCounter = 0;
		}
	
		// Check if we are waiting for any other progressable items.
		if (mLoadingCounter == 0) {
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}

}