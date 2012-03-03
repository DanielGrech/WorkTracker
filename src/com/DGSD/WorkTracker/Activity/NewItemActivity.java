package com.DGSD.WorkTracker.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.DGSD.WorkTracker.Constants.Extra;
import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.Fragment.NewItemFragment;
import com.actionbarsherlock.view.MenuItem;


public class NewItemActivity extends BaseActivitySingleFragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected String getActionBarTitle() {
		return getResources().getString(R.string.new_item_title);
	}

	@Override
	protected Fragment getFragment() {
		int id = getIntent().getIntExtra(Extra.ID, -1);
		return NewItemFragment.newInstance(id);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
            	NewItemFragment frag = (NewItemFragment) mFragment;
            	if(frag.onExit()) {
            		finish();
            	}
            	
            	return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
	
	@Override
	public void onBackPressed() {
		NewItemFragment frag = (NewItemFragment) mFragment;
    	if(frag.onExit()) {
    		finish();
    	}
	}
}
