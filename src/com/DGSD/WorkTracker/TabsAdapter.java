package com.DGSD.WorkTracker;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;


/**
 * @author Daniel Grech
 */
public class TabsAdapter extends FragmentPagerAdapter implements
		ViewPager.OnPageChangeListener, ActionBar.TabListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = TabsAdapter.class.getSimpleName();
	private final ActionBar mActionBar;
	private final ViewPager mViewPager;
	private ArrayList<Fragment> mFragments;
	private ViewPager.OnPageChangeListener onChangeListener;

	public TabsAdapter(FragmentActivity context, ActionBar actionBar,
			ViewPager pager) {
		super(context.getSupportFragmentManager());
		mActionBar = actionBar;
		mFragments = new ArrayList<Fragment>();
		mViewPager = pager;
		if (pager != null) {
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}
	}

	public void addTab(ActionBar.Tab tab, Fragment f) {
		mFragments.add(f);
		mActionBar.addTab(tab.setTabListener(this));
		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		onChangeListener = listener;
	}

	@Override
	public int getCount() {

		return mFragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return mFragments.get(position);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);

		if (onChangeListener != null) {
			onChangeListener.onPageSelected(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	public Fragment getCurrentItem() {
		return mFragments.get(mViewPager.getCurrentItem());
	}

	@Override
	public void onTabSelected(Tab tab) {
		if (mViewPager != null) {
			mViewPager.setCurrentItem(tab.getPosition());
		}
		
	}

	@Override
	public void onTabUnselected(Tab tab) {
		
	}

	@Override
	public void onTabReselected(Tab tab) {
		
	}
}
