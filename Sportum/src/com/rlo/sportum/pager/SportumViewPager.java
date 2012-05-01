package com.rlo.sportum.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SportumViewPager extends ViewPager {

    private boolean pagingEnabled;

    public SportumViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        pagingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pagingEnabled) {
            return super.onTouchEvent(event);
        }
  
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (pagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }
 
        return false;
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }
    
    public boolean isPagingEnabled() {
    	return pagingEnabled;
    }
    
}