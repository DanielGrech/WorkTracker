package com.DGSD.WorkTracker;

import android.app.Application;

import com.DGSD.WorkTracker.Activity.DashboardActivity;
import com.DGSD.WorkTracker.Utils.DiagnosticUtils;

public class WTApp extends Application {
	public static boolean DEBUG = false;

	/**
	 * @returns The class of the 'home' activity
	 */
	public static Class<?> getHomeClass() {
		return DashboardActivity.class;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		DEBUG = DiagnosticUtils.isDebugOn(this);

		if(DEBUG) {
			DiagnosticUtils.strictModeOn();
		}
	}
}
