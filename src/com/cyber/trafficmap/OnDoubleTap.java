package com.cyber.trafficmap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

public class OnDoubleTap extends MapView {

	  private long lastTouchTime = -1;

	  public OnDoubleTap(Context context, AttributeSet attrs) {

	    super(context, attrs);
	  }

	  @Override
	  public boolean onInterceptTouchEvent(MotionEvent ev) {

	    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	    	System.out.println("in ondoubletap class");
	      long thisTime = System.currentTimeMillis();
	      if (thisTime - lastTouchTime < 250) {
	    	  System.out.println("in ondoubletap class thistime<250");
	        // Double tap
	        this.getController().zoomInFixing((int) ev.getX(), (int) ev.getY());
	        lastTouchTime = -1;

	      } else {
	    	  System.out.println("in ondoubletap class>250");
	        // Too slow 
	        lastTouchTime = thisTime;
	      }
	    }

	    return super.onInterceptTouchEvent(ev);
	  }
	}
