package com.DGSD.WorkTracker.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.TabsAdapter;
import com.DGSD.WorkTracker.Fragment.AddItemToListFragment;
import com.DGSD.WorkTracker.Fragment.ItemListFragment;
import com.actionbarsherlock.app.ActionBar;


public class EditListActivity extends BaseActivity{

	private static final String KEY_ADD_ITEM_FRAGMENT= "_add_item_fragment";
	private static final String KEY_ITEM_LIST_FRAGMENT= "_item_list_fragment";
	
	private ViewPager mPager;
	private TabsAdapter mTabsAdapter;
	
	private AddItemToListFragment mAddItemFragment;
	
	private ItemListFragment mItemList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_with_single_pager);
		
		if(savedInstanceState != null) {
			mAddItemFragment = (AddItemToListFragment) mFragmentManager.getFragment(savedInstanceState, KEY_ADD_ITEM_FRAGMENT);
			mItemList = (ItemListFragment) mFragmentManager.getFragment(savedInstanceState, KEY_ITEM_LIST_FRAGMENT);
        }
		
		if(mAddItemFragment == null) {
			mAddItemFragment = AddItemToListFragment.newInstance();
		}
		
		if(mItemList == null) {
			mItemList = ItemListFragment.newInstance();
		}
		
		 mPager = (ViewPager) findViewById(R.id.pager);
		 
		mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        mTabsAdapter = new TabsAdapter(this, mActionBar, mPager);

        mTabsAdapter.addTab(mActionBar.newTab().setText(R.string.add_item_tab), mAddItemFragment);
        mTabsAdapter.addTab(mActionBar.newTab().setText(R.string.item_list_tab), mItemList);
	}
}
