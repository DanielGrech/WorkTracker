package com.DGSD.WorkTracker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * A class to decide whether the user has already logged in before or not
 * and direct them to the appropriate screen
 * 
 * @author Daniel Grech
 */
public class StartupChoiceActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = null;
        /*
        if(SharedPrefsService.isLoggedIn(this)) {
            if(BHApplication.DEBUG) {
            	Log.d(TAG, "Logged in before. Redirecting to main activity");
            }
            
            //TODO: It should be a preference whether to direct to dashboard or to calendar!
            intent = new Intent(this, DashboardActivity.class);
        } else {
        	if(BHApplication.DEBUG) {
        		Log.d(TAG, "Havent logged in before. Redirecting to welcome activity");
        	}
            intent = new Intent(this, WelcomeActivity.class);
        } */
        
        intent = new Intent(this, DashboardActivity.class);
        //Pass on any extras we've already received
        intent.putExtras(getIntent());

        //Make our choice!
        startActivity(intent);
        finish();
    }
}