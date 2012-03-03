package com.DGSD.WorkTracker.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.DGSD.WorkTracker.R;

public class SectionHeading extends LinearLayout {
	private TextView mText;
	
    public SectionHeading(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        LayoutInflater.from(context).inflate(R.layout.section_header, this, true);
        
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SectionHeading, 0, 0);
        
        mText = (TextView) findViewById(R.id.text);
        mText.setText(array.getString(R.styleable.SectionHeading_text));
        
        array.recycle();
    }
    
    public void setText(String text) {
    	if(mText != null) {
    		mText.setText(text);
    	}
    }
    
    public String getText() {
    	return (mText != null) ? mText.getText().toString() : "";
    }
}