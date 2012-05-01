package com.rlo.sportum.pager;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class SportumPagerAdapter extends PagerAdapter {
	
	private static final int NUM_LAYOUTS = 2;
	
	private ArrayList<View> layouts;
	
	public SportumPagerAdapter(ArrayList<View> layouts) {
		this.layouts = layouts;
	}
	
	public int getCount() {
        return NUM_LAYOUTS;
    }

    public Object instantiateItem(View collection, int position) {
    	View view = layouts.get(position);
    	
        ((ViewPager) collection).addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }

    @Override
    public Parcelable saveState() {
        return null;
    }
    
}
