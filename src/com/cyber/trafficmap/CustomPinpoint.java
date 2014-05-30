package com.cyber.trafficmap;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


public class CustomPinpoint extends ItemizedOverlay<OverlayItem>{

	private ArrayList<OverlayItem> pinpoints = new ArrayList<OverlayItem>();
	private Context c;
	
	public CustomPinpoint(Drawable defaultMarker) {// there is where we want to add pinpoint
		super(boundCenter(defaultMarker)); //this is we are bounding the icon center
		// TODO Auto-generated constructor stub
	}

	public CustomPinpoint(Drawable m, Context context) {// there is where we want to add pinpoint at context that has been passed
		// TODO Auto-generated constructor stub
		this(m);
		c = context;
	}
	
	@Override
	protected OverlayItem createItem(int i) { // we are going to pass the values from the array list
		// TODO Auto-generated method stub
		return pinpoints.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return pinpoints.size(); // this returns the size of the arraylist
	}
	
	public void insertPinpoint(OverlayItem item){
		pinpoints.add(item);
		this.populate();
	}
	/*
	//added this code on 9/20
	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub
		Log.e("Tap", "Tap Performed");
		return true;
	}*/

}
