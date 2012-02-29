package com.DGSD.WorkTracker.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.DGSD.WorkTracker.Constants;
import com.DGSD.WorkTracker.PortableReceiver;
import com.DGSD.WorkTracker.Receiver;
import com.DGSD.WorkTracker.WTApp;
import com.actionbarsherlock.app.SherlockDialogFragment;

/** 
 * Base class for fragments which allows inheriting classes to
 * receive broadcasts dynamically
 *
 * @author Daniel Grech
 */
public abstract class BaseFragment extends SherlockDialogFragment implements Receiver {
	private static final String TAG = BaseFragment.class.getSimpleName();
	
		
	/**
	 * Callback to give this fragment a chance to register to receive
	 * updates. This method should be overridden in a subclass if 
	 * it wishes to receive broadcasts
	 */
	protected boolean doRegisterReceivers() {
		return false;
	}
	
	/**
	 * Portable receiver to dynamically register and unregister other broadcast
	 * receivers
	 */
	protected PortableReceiver mReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mReceiver = new PortableReceiver();
		mReceiver.setReceiver(this);
	}
	
	@Override
    public void onResume() {
        super.onResume();

        // Register the receiver
        if(doRegisterReceivers()) {
        	IntentFilter filter = new IntentFilter();
    		filter.addAction(Constants.ResultType.SUCCESS);
    		filter.addAction(Constants.ResultType.NO_RESULT);
    		filter.addAction(Constants.ResultType.ERROR);

    		getActivity().registerReceiver(mReceiver, filter,
    				Constants.Permissions.RECEIVE_BROADCASTS, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        
        try {
        	getActivity().unregisterReceiver(mReceiver);
        }catch (IllegalArgumentException e) {
        	Log.e(TAG, "Error unregistering receiver");
        }
    }

    /**
     * Callback when receiving a broadcast event
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	if(WTApp.DEBUG) Log.w(TAG, "No onReceive() listener registered");
    }
}
