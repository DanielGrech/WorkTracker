package com.DGSD.WorkTracker.View;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.DGSD.WorkTracker.R;
import com.DGSD.WorkTracker.WTApp;

/**
 * Widget which shows an edit text with a button to its right. This widget
 * handles its own state after a configuration change
 * 
 * @author Daniel Grech
 */
public class NumberPickerView extends FrameLayout implements OnClickListener {
	private static final String TAG = NumberPickerView.class.getSimpleName();
	
	private StatefulEditText mText;
	private Button mPlusBtn;
	private Button mMinusBtn;

	public NumberPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context)
				.inflate(R.layout.number_picker, this, true);

		mText = (StatefulEditText) findViewById(R.id.number);
		mPlusBtn = (Button) findViewById(R.id.plus);
		mMinusBtn = (Button) findViewById(R.id.minus);

		mPlusBtn.setOnClickListener(this);
		mMinusBtn.setOnClickListener(this);
	}

	public void setNumber(int num) {
		mText.setText(String.valueOf(num));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		int currentVal = 0;
		try {
			currentVal = Integer.valueOf(mText.getText().toString());
		} catch(NumberFormatException e) {
			//O well..
			if(WTApp.DEBUG) {
				Log.w(TAG, "Error getting value", e);
			}
			currentVal = 0;
		}
			
		switch(id) {
			case R.id.plus:
				mText.setText(String.valueOf(currentVal+1));
				break;
			case R.id.minus:
				if((currentVal - 1) < 0) {
					mText.setText("0");
				} else {
					mText.setText(String.valueOf(currentVal - 1));
				}
				
				break;
		}
	}

	@Override
	protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
		if (getId() != NO_ID) {
			Parcel p = Parcel.obtain();
			p.writeString(mText.getText() == null ? null : mText.getText()
					.toString());
			p.setDataPosition(0);
			container.put(getId(), new NumberPickerViewParcelable(p));
		}
	}

	@Override
	protected void dispatchRestoreInstanceState(
			SparseArray<Parcelable> container) {
		if (getId() != NO_ID) {
			Parcelable p = container.get(getId());
			if (p != null && p instanceof NumberPickerViewParcelable) {
				NumberPickerViewParcelable npvp = (NumberPickerViewParcelable) p;
				mText.setText(npvp.getValue());
			}
		}
	}

	static class NumberPickerViewParcelable implements Parcelable {

		private String mValue;

		private NumberPickerViewParcelable(Parcel in) {
			mValue = in.readString();
		}

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(mValue);
		}

		String getValue() {
			return mValue;
		}

		public static final Parcelable.Creator<NumberPickerViewParcelable> CREATOR = new Parcelable.Creator<NumberPickerViewParcelable>() {

			public NumberPickerViewParcelable createFromParcel(Parcel source) {
				return new NumberPickerViewParcelable(source);
			}

			public NumberPickerViewParcelable[] newArray(int size) {
				return new NumberPickerViewParcelable[size];
			}
		};

	}
}