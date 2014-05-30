package com.cyber.trafficmap; 

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint; // all these are the list of classes provided in the api
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.MapView.ReticleDrawMode;
import com.google.android.maps.Overlay.Snappable;
import com.google.android.maps.Projection;
import com.google.android.maps.TrackballGestureDetector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.PrintStreamPrinter;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;


public class TrafficMapActivity extends MapActivity implements OnGestureListener, LocationListener {
    /** Called when the activity is first created. */ //LocationListener is used to grab GPS
	
	private MapView map;	
	Drawable d;
	
	String towers;
	MyLocationOverlay compass;
	MapController controller;
	List<Overlay> overlayList;
	Location location;
	LocationManager lm = null;
    TelephonyManager mTelephonyMgr;
    String ts = Context.TELEPHONY_SERVICE;
    
	int lat;
    int longi;
    int latto = 0;
    int longto = 0;
    int latfrom = 0;
    int longfrom = 0;
    int OldPacket_Size = 0;
    
    Button GetRoute;
    EditText StartingPoint, EndingPoint;
    Geocoder gc;
    //Context context = TrafficMapActivity.this;
    ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
    ArrayList<GeoPoint> gpsTraces = new ArrayList<GeoPoint>();
	ArrayList<GeoPoint> gpsTraces_copy = new ArrayList<GeoPoint>();
	
    boolean canGetLocation = false;
    // this is not yet done. It is not working
    private GestureDetector mGestureDetector = null; 

    String device_ID;
    // variable to store speed of the device in m/s
    List<Float> vSpeed = new ArrayList<Float>();
    List<Float> avgSpeed = new ArrayList<Float>();

    // flag for GPS,Network status
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
    
    //DocParser DP;
    //private MapOverlay mmapOverlay = null;
    private OnSingleTapListener singleTapListener;
    private GestureDetector detector;
    //MapView mapView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

       	super.onCreate(savedInstanceState);
       	
       	/*DP = new DocParser("Files/a.txt");
        geoPoints = (ArrayList<GeoPoint>) DP.scanInputFile();*/
        
        setContentView(R.layout.main);
        map = (MapView)findViewById(R.id.mvMain);
        map.setBuiltInZoomControls(true);

        // geoPoints.add(new GeoPoint(lat,longi));
        //this.mmapOverlay = new MapOverlay(this);
        
        overlayList = map.getOverlays();
        controller = map.getController(); // using geopoint
        controller.setZoom(10);
        System.out.println(map.getZoomLevel());
        lat = (int) 39.646166;
    	longi = (int) -79.958184;
        GeoPoint point = new GeoPoint(lat, longi);
        
        
        // WE ARE GETTING DEVICE_ID
        mTelephonyMgr = (TelephonyManager) getSystemService(ts);
        device_ID = mTelephonyMgr.getDeviceId();
        System.out.println("mTelephonyMgr from main function->"+device_ID);
        d = getResources().getDrawable(R.drawable.geopoint); // this is drawable     
        CustomPinpoint custom = new CustomPinpoint(d, TrafficMapActivity.this);
        OverlayItem overlayItem = new OverlayItem(point,"test location","Position");           
        //overlayList.add(custom);

        //placing pin point at current location
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // we set up permissions after this
        System.out.println("Current Location ->"+lm);
        
        boolean enabledGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledNW = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!enabledGPS && ! enabledNW){
        	
        	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        	startActivity(intent);
        	
	        }
        else{
	     
	        Criteria crit = new Criteria(); // what kind of criteria we are looking for
	        crit.setAccuracy(Criteria.ACCURACY_COARSE);
	        //I changed this to true
	        towers = lm.getBestProvider(crit, true); //this gives the default best criteria defined above
	        System.out.println("Best provider ->"+towers);
	        //LocationProvider low = lm.getProvider(towers); //this is used for debugging
	        
	        Location location = lm.getLastKnownLocation(towers); // towers is getting best location
	        System.out.println("Last Known Location ->"+location);
	        
	        if(location != null){
	           	//Toast.makeText(getBaseContext(), "success", Toast.LENGTH_LONG).show();
	           	Toast.makeText(getBaseContext(), location.toString(), Toast.LENGTH_LONG).show();
	           	int lat = (int) (location.getLatitude()*1E6);
	           	int longi = (int) (location.getLongitude()*1E6);
	           	GeoPoint ourLocation = new GeoPoint(lat, longi);
	           	
	           	// I am going to collect gps traces
	        	
	           	
	           	try {
	           		gpsTraces.add(ourLocation);
	           		vSpeed.add(location.getSpeed());
	           		System.out.println("Size of gpstraces in createbundle ->"+gpsTraces.size());
	           		System.out.println("Size of gpstraces in createbundle ->"+gpsTraces);
	           		if(gpsTraces.size() > 0)
	           		{
	           			System.out.println("Size is greater than 0. These are the starting points of the route");
	           			// HERE WE ARE CREATING ASYNC TASK FOR STORING GPSTRACES
	           			
	           			new MapMatchingTask().execute(device_ID,gpsTraces,vSpeed);
	           			//new InsertGPSTraceTask().execute(device_ID,gpsTraces,vSpeed);
	           			//gpsTraceCollection gpstrace = new gpsTraceCollection(device_ID,gpsTraces);
	           			//gpstrace.UploadgpsTrace(device_ID,gpsTraces);
	           			
	           			/*HERE WE ARE WRITING TO TEXT FILE*/
	           			
	           			WritetoText w2text = new WritetoText();    			           			
	           			w2text.write(device_ID, gpsTraces, vSpeed, location.getBearing(),location.getTime());
	           		}else
	           		{
	           			System.out.println("Size is not greater than 0. These are the starting points");
	           		}
					
					} catch (Exception e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
	           	
	           		OverlayItem overlayItem1 = new OverlayItem(ourLocation,"Our Location","Position");// this is technically creating a marker
	           		CustomPinpoint custom1 = new CustomPinpoint(d, TrafficMapActivity.this);
	           		custom1.insertPinpoint(overlayItem1);
	           		overlayList.add(custom1); // we are adding our overlay item to the overlay list
	            
	           		// ***************THESE LINES OF CODE ARE FOR SINGLE ITERATION************** //
	            	//	this.mmapOverlay = new MapOverlay(this);
	            	//  map.invalidate();
	                
	           		controller.animateTo(ourLocation);
	            
	            
	            
	           		//***************************************************************************************************//
	           		//HERE WE ARE TRYING TO MAKE A CALLBACK TO A ASYNC TASK WHICH WILL GET ALL THE GPS TRACES INTHAT SURROUNDING AREA
	           		// TAKE THE ZOOM LEVEL AND BOUNDARIES AS INPUT ALL PULL ALL THE VALUES TO SQLITE DATABASE 
	           		//***************************************************************************************************//
	            
	            /*
	           		displayResults d = new displayResults();
	           		Object[] obj;
	           		GeoPoint topLeftGft1 = (GeoPoint) map.getProjection().fromPixels(0, 0);
	           		GeoPoint bottomRightGpt1 = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
	           		String s = "10000";
	           		try {
	           			obj = d.getGeoPoints(s,topLeftGft1,bottomRightGpt1);
	           			geoPoints = (ArrayList<GeoPoint>) obj[0];
	           			System.out.println("map is adjusting for zoom and number of points = "+s);
	           			System.out.println("Total number of geopoints returned from db = "+geoPoints.size());
	           			ArrayList Point_Id = (ArrayList) obj[1];
	           			ArrayList Polyline_Id = (ArrayList) obj[2];
	           			System.out.println("point_Id size returned to main = "+Point_Id.size());
	           			System.out.println("Polyline size returned to main"+" "+Polyline_Id.size());
	           			//GPSTracesDataBase entry = new GPSTracesDataBase(TrafficMapActivity.this);
	           			//entry.open();
	           			//entry.createEntry(Point_Id, Polyline_Id,geoPoints);
	           			//entry.close();
	
	           		} catch (IOException e) {
	           			// TODO Auto-generated catch block
	           			e.printStackTrace();
	           			e.getLocalizedMessage();
	        		}
	           		*/
	        }
	        else
	        {
	           	Toast.makeText(getBaseContext(), "Couldn't get the service provider", Toast.LENGTH_SHORT).show();
	           	//System.out.println(location);
	           	
	           	// WE ARE WRITING THESE LINES OF CODE FOR TESTING UPLOAD GPS TRACES TO DATABASE
	           	gpsTraces.add(point);
				//GPSTracesDataBase entry = new GPSTracesDataBase(TrafficMapActivity.this);
				//entry.open();
				ArrayList Point_Id = new ArrayList();
				ArrayList Polyline_Id = new ArrayList();
				Point_Id.add(1098);
				Polyline_Id.add(1211);			
				//entry.createEntry(Point_Id, Polyline_Id,gpsTraces);
				//entry.close();
	           	//new InsertGPSTraceTask().execute(device_ID,gpsTraces);
	           	/*gpsTraceCollection gpstrace = new gpsTraceCollection(device_ID,gpsTraces);
	   			try {
	   				System.out.println("gpstrace point"+gpsTraces);
	   				gpstrace.UploadgpsTrace(device_ID,gpsTraces);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	   			
	   			
	   			
	        }
	        
	        // Setting the value of location updates for every 1Hz or 10 meters
	        lm.requestLocationUpdates(towers, 1000, (float) 0 , this);
	        
	        /*TASK TO CALL AN UPDATE SERVICE, THEREBY LINKING IT TO MAP UPDATE FUNCTIONALITY*/
	        //Intent intent = new Intent(this,UpdaterService.class);
	        //startService(intent);
	        
	
	        
	        
	        //**************TASK INCOMPLETE. HAVE TO PLUGIN CREATE ROUTE MENU ITEM********************************//
	        
	        GetRoute = (Button) findViewById(R.id.bGetdirections);
	        StartingPoint = (EditText) findViewById(R.id.eTstartpoint);
	        EndingPoint = (EditText) findViewById(R.id.eTendPoint);
	        gc = new Geocoder(this);       
	        
	        
	        detector = new GestureDetector(this,this);
	        System.out.println("detector value-"+detector);
	        detector.setOnDoubleTapListener(new OnDoubleTapListener(){
	        	@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					if (singleTapListener != null) {
						return singleTapListener.onSingleTap(e);
					} else {
						return true;
					}
				}
	
				@Override
				public boolean onDoubleTap(MotionEvent e) {
					Log.i("TrafficMapActivity","ondoubletap"+true);
					 int x = (int)e.getX(), y = (int)e.getY();;  
					 Projection p = map.getProjection();  
					 //map.getController().animateTo(p.fromPixels(x, y));
					 map.getController().zoomInFixing(x, y);  
					 return true;
				}
	
				@Override
				public boolean onDoubleTapEvent(MotionEvent e) {
					return true;
				}
	        	
	        });
	    }      
    }
    

   	/** Glue that makes it work! */
   	public void setOnSingleTapListener(OnSingleTapListener singleTapListener) {
		this.singleTapListener = singleTapListener;
	}
   
   	
   	
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this,UpdaterService.class);
		stopService(intent);
	}


	private class InsertGPSTraceTask extends AsyncTask {
    	@Override
		protected Object doInBackground(Object...params) {
    		System.out.println("we entered async task1");
			// TODO Auto-generated method stub
    		String device_ID = (String) params[0];
    		ArrayList<GeoPoint> gpsTraces1 = (ArrayList<GeoPoint>) params[1];
    		ArrayList veh_speed = (ArrayList) params[2];
    	    OldPacket_Size = gpsTraces1.size();
    	    System.out.println("size of packet we are going to remove in background->"+OldPacket_Size);
    		System.out.println("This is device ID -> "+device_ID+" and "+"points are "+gpsTraces1);
    		
    		//THE BELOW SET OF CODE CAN BE REMOVED IN FUTURE. IT IS FOR UPLOADING INTO SERVER WHICH WILL NOLONGER BE REQUIRED
			gpsTraceCollection gpstrace = new gpsTraceCollection(device_ID,gpsTraces1);//this is to upload to server
   			try {
				// this is we are storing the values in server
   				gpstrace.UploadgpsTrace(device_ID,gpsTraces1);
				System.out.println("insertgpstrace after upload="+gpstrace);
				// SAVING THE VALUES IN LOCAL DATABASE. WE HAVE TO USE IT
   				//GPSTracesDataBase entry = new GPSTracesDataBase(TrafficMapActivity.this);
   				//entry.open();
   				//entry.createEntryforUSER(device_ID,gpsTraces1,veh_speed);
   				//entry.close();
   				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("we entered onpostexecute result="+result);
			}  	  
    	
    }
    
    
    private class MapMatchingTask extends AsyncTask{
    	
    	// IN THIS ASYNC TASK WE ARE GOING TO MAKE MAPMATCHING TO GET DESIRED ORDERED POINTS
		@Override
		protected Object doInBackground(Object...geoPoints_client) {
			// TODO Auto-generated method stub

			String device_ID = (String) geoPoints_client[0];
			ArrayList<GeoPoint> gpsTraces2_client = (ArrayList<GeoPoint>) geoPoints_client[1];
			ArrayList<GeoPoint> gpsTraces3 = (ArrayList<GeoPoint>) geoPoints_client[1];
			ArrayList veh_speed = (ArrayList) geoPoints_client[2];
			System.out.println("we entered Mapmatching async task="+gpsTraces2_client+"size="+gpsTraces2_client.size());
			displayResults d1 = new displayResults();
	   		Object[] obj;
	   		avgSpeed.add(average(veh_speed));
	   		System.out.println(gpsTraces2_client+","+device_ID+","+veh_speed);
	   		   		
	   		//HERE WE HAVE TO LIMIT THE NUMBER OF POINTS AROUND 1KM
	   		/*If we have geopoint here, suppose Z(x,y)
	   		 * then boundary would be top left corner A(x+0.000700,y-0.000811)
	   		 * right bottom corner D(x-0.000700,y+0.000811)
	   		 * therefore left bottom corner C(x-0.000700,y-0.000811)
	   		 * and right top corner B(*x+0.000700,y+0.000811)
	   		 * we are keeping accuracy of 100ms*/
	   		
	   		//GeoPoint topLeftGft3 = (GeoPoint) map.getProjection().fromPixels(0, 0);
	   		//GeoPoint bottomRightGpt3 = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
	   		
	   		//45.987897 "degree" = 45987897 "microdegree"
	   		
	   		double xtopleftcorner = (gpsTraces2_client.get(0).getLatitudeE6()/1E6 + 0.0009);
	   		double ytopleftcorner = (gpsTraces2_client.get(0).getLongitudeE6()/1E6 - (0.000899/Math.cos(xtopleftcorner*Math.PI/180)));
	   			   		
	   		double xbottomrightcorner = (gpsTraces2_client.get(0).getLatitudeE6()/1E6 - 0.0009);
	   		double ybottomrightcorner = (gpsTraces2_client.get(0).getLongitudeE6()/1E6 + (0.000899/Math.cos(xbottomrightcorner*Math.PI/180)));
	   		
	   		GeoPoint topleftcorner = new GeoPoint((int)(xtopleftcorner*1E6),(int)(ytopleftcorner*1E6));
	   		GeoPoint bottomrightcorner = new GeoPoint((int)(xbottomrightcorner*1E6),(int)(ybottomrightcorner*1E6));
	   		System.out.println("geo points"+topleftcorner+","+bottomrightcorner);
	   		System.out.println("boundaries="+xtopleftcorner+","+ytopleftcorner+","+xbottomrightcorner+","+ybottomrightcorner);
	   		String s1 = "100000";
	   		try {
	   			obj = d1.getGeoPoints(s1,topleftcorner,bottomrightcorner);
	   			geoPoints = (ArrayList<GeoPoint>) obj[0];
	   			System.out.println("map is adjusting for zoom and number of points = "+s1);
	   			System.out.println("Total number of geopoints returned from db = "+geoPoints.size());
	   			ArrayList Point_Id = (ArrayList) obj[1];
	   			ArrayList Polyline_Id = (ArrayList) obj[2];
	   			System.out.println("point_Id size returned to main = "+Point_Id.size());
	   			System.out.println("Polyline size returned to main"+" "+Polyline_Id.size());
	   			System.out.println("points="+geoPoints);
	   			int[] MatchingIndex = new int[gpsTraces2_client.size()];
	   			System.out.println("gpstraces2client size="+gpsTraces2_client.size());
	   			System.out.println("first="+gpsTraces2_client.get(0));
//	   			System.out.println("second="+gpsTraces2_client.get(1));
	   			for(int i = 0;i<gpsTraces2_client.size();i++){
	   				MapMatch points2_match = new MapMatch(geoPoints,gpsTraces2_client.get(i));
		   			// WE ARE GETTING THE INDEX OF all MATCHING POINT. SO WE CAN USE THIS FOR FINDING LINK ID
	   				// HERE WE ARE ALSO RETURING -1 IF NO MATCHING POINT IS FOUND. SO WE HAVE DEAL THAT CASE EVEN
		   			MatchingIndex[i] = points2_match.point2pointmatch();
		   			//System.out.println("Matching index from main activity="+MatchingIndex[i]);
	   			}
	   			System.out.println("we came to main activity");
	   			if(MatchingIndex.length > 0){
	   				if(MatchingIndex[0] == -1){
	   					Log.i("log_tag", "No matching point found");
	   				}else
	   				{
	   		   			System.out.println("we got matching point. number of matching points found ="+MatchingIndex.length);
	   					int[] PolylineID = new int[MatchingIndex.length];
	   		   			for(int i = 0;i < MatchingIndex.length;i++){
	   		   				PolylineID[i] = (Integer) Polyline_Id.get(MatchingIndex[i]);
	   		   				//System.out.println("polylieID="+PolylineID[i]);
	   		   			}
	   		   			//float[] results;
	   					//Location.distanceBetween(gpsTraces2_client.get(0), geoPoints.get(0), gpsTraces2_client.get(1), geoPoints.get(1), results);
	   		   			gpsLinkSpeedCollection gpstrace = new gpsLinkSpeedCollection(device_ID,PolylineID,veh_speed);//this is to upload to server
	   		   			gpstrace.UploadLinkProperties(device_ID,PolylineID,veh_speed);
	   				}
	   			}

	   		}catch(Exception e){
	   			e.printStackTrace();
	   		}
	   		// gpsTraceCollection gpstrace = new gpsTraceCollection(device_ID,Polyline_Id);//this is to upload to server
			// this is we are storing the values in server
	   		// gpstrace.UploadgpsTrace(device_ID,gpsTraces1);
	   		gpsTraceCollection gpstrace1 = new gpsTraceCollection(device_ID,gpsTraces3);
	   		try{
	   			gpstrace1.UploadgpsTrace(device_ID,gpsTraces3);
	   		}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
				
			return null;
		}
 	
    }
    
    
    private class TouchOverlay extends com.google.android.maps.Overlay{
        int lastZoomLevel = -1;
        ArrayList<String> linkId_traffic = new ArrayList<String>();;
        public TouchOverlay(ArrayList<String> linkId) {
			// TODO Auto-generated constructor stub
        	linkId_traffic = linkId;
		}
    	/*CHECK FOR THE MOTION EVENT*/
		public boolean onTouchEvent(MotionEvent event, MapView map) {
        	int action = event.getAction();
        	System.out.println("Motion action->"+action);

            if (event.getAction() == 1 || event.getAction() == 2 || event.getAction() == 0) {
                if (lastZoomLevel == -1)
                    lastZoomLevel = map.getZoomLevel();

                GeoPoint topLeftGft =  (GeoPoint) map.getProjection().fromPixels(0, 0);
                GeoPoint bottomRightGpt = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
                if (map.getZoomLevel() != lastZoomLevel) {
                    onZoom(map.getZoomLevel(),linkId_traffic);
                    lastZoomLevel = map.getZoomLevel();
                    System.out.println("Last Zoom level="+lastZoomLevel);
                }
            }
            return false;
        }


    }
    

    public void onZoom(int level, ArrayList<String> linkId_traffic) {

    	if(level<10)
    	{
    		clearmap();
            System.out.println("zoomlevel<10 and ="+ level);
    		String s = "1000";
    		GeoPoint topLeftGft1 = (GeoPoint) map.getProjection().fromPixels(0, 0);
	        GeoPoint bottomRightGpt1 = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
    		mapadjust(s,topLeftGft1,bottomRightGpt1,linkId_traffic);
    	}
    	else if(level<17 && level>9)
    	{
    		clearmap();
            System.out.println("zoomlevel<17 and =" + level);
    		String s = "5000";
    		GeoPoint topLeftGft2 = (GeoPoint) map.getProjection().fromPixels(0, 0);
	        GeoPoint bottomRightGpt2 = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
    		mapadjust(s,topLeftGft2,bottomRightGpt2,linkId_traffic);
    	}
    	else if(level>=17)
    	{
    		clearmap();
    		System.out.println("zoomlevel>16 and ="+ level);
    		GeoPoint topLeftGft3 = (GeoPoint) map.getProjection().fromPixels(0, 0);
	        GeoPoint bottomRightGpt3 = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
    		String s = "10000";
    		mapadjust(s,topLeftGft3,bottomRightGpt3,linkId_traffic);
    	}
    }
    
	
    private void mapadjust(String s, GeoPoint topLeftGft1,
			GeoPoint bottomRightGpt1, ArrayList<String> linkId_traffic) {
    	// TODO Auto-generated method stub
        displayResults d = new displayResults();
        Object[] obj;
    	try {
    		obj = d.getGeoPoints(s,topLeftGft1,bottomRightGpt1);
           	geoPoints = (ArrayList<GeoPoint>) obj[0];
           	System.out.println("map is adjusting for zoom and number of points = "+s);
           	System.out.println("Total number of geopoints returned from db = "+geoPoints.size());
        	ArrayList Point_Id = (ArrayList) obj[1];
        	ArrayList Polyline_Id = (ArrayList) obj[2];
        	System.out.println("point_Id size returned to main = "+Point_Id.size());
        	System.out.println("Polyline size returned to main"+" "+Polyline_Id.size());
    		drawPath(geoPoints, Color.GREEN , Point_Id, Polyline_Id, linkId_traffic);
    		
    		
    		//**************************Here we are trying to update the table to contain all the gps traces************************//
    		// Actually I feel this is a wrong way of doing. But for know I am trying to do it.
    		// once new values are found we have to update the table with new values instead of dropping it
    		// Why do we need to update countygps points when we zoom in. Bydefault we will have max of our county points
    		// to perform map matching we just need our county gps traces. so no need to update unless he crosses the county
    		/*GPSTracesDataBase entry = new GPSTracesDataBase(TrafficMapActivity.this);
			entry.open();
			entry.UpdateCountyGPS(Point_Id, Polyline_Id,gpsTraces);
			entry.close();
    		*/
    		
    		
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		e.getLocalizedMessage();
    		}
        }
    

	private void clearmap() {
		// TODO Auto-generated method stub
		if(!overlayList.isEmpty()){
			overlayList.clear();	
			map.getOverlays().clear();
			map.invalidate();
			}
	}
	

	private void drawPath(ArrayList<GeoPoint> geoPoints, int color,
			ArrayList<Integer> Point_Id, ArrayList<Integer> Polyline_Id, ArrayList<String> linkId_traffic) {
		// TODO Auto-generated method stub
    	overlayList = map.getOverlays();
    	if (Point_Id.get(0) == 0)
    	{
    		System.out.println("There are no routes to display");
    		Toast.makeText(getBaseContext(), "There are no routes to display", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
        	 for (int i = 1; i < geoPoints.size(); i++){
        		 //System.out.println("Polyline_Id->"+i+"="+Polyline_Id.get(i));
        		 //if (Polyline_Id.get(i).equals(Polyline_Id.get(i-1))){
        			if(Point_Id.get(i) == (Point_Id.get(i-1)+1) && Polyline_Id.get(i).equals(Polyline_Id.get(i-1))){
        				System.out.println(i+""+"success");
        				System.out.println("Polyline_Id->"+i+"="+Polyline_Id.get(i));
        				
        				if(linkId_traffic.contains(Polyline_Id.get(i).toString()))
        				{
        					System.out.println("comparision of-"+Polyline_Id.get(i)+","+linkId_traffic.contains(Polyline_Id.get(i)));
        					overlayList.add(new MapOverlay(geoPoints.get(i-1), geoPoints.get(i),Color.RED));
        				}        					
        				else
        					overlayList.add(new MapOverlay(geoPoints.get(i-1), geoPoints.get(i),Color.GREEN));	
        			}			
        	}
        	System.out.println("Drawing of route completed for zoom level");
         	map.invalidate();	
    	}
	}
	

	protected class MapOverlay extends Overlay {

		GeoPoint gp1;
    	GeoPoint gp2;
    	int color; 	
            
       public MapOverlay(GeoPoint gp1, GeoPoint gp2, int color) {
			// TODO Auto-generated constructor stub
    	   this.gp1 = gp1;
    	   this.gp2 = gp2;
    	   this.color = color;
		}

		@Override
	   public void draw(Canvas pC, MapView map, boolean shadow) {
        	//Toast.makeText(getBaseContext(), "shadow" + shadow, Toast.LENGTH_SHORT).show();
            if (shadow)
                return;
      
            Paint lp3;
            lp3 = new Paint();
            lp3.setColor(color);
            lp3.setAntiAlias(true);
            lp3.setStyle(Style.FILL);
            lp3.setStrokeWidth(12);
            lp3.setTextAlign(Paint.Align.LEFT);
            lp3.setTextSize(17);
            lp3.setAlpha(120);
            Projection projection = (Projection) map.getProjection();    
            Point point1 = new Point();
            Point point2 = new Point();
            projection.toPixels(gp1, point1);
            projection.toPixels(gp2, point2);
            pC.drawLine(point1.x, point1.y, point2.x, point2.y, lp3);         
          }
    }    
       
    //*****************This is for creating options menu*******************************//
   
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// Handle item selection
	    switch (item.getItemId()) {
	        case R.id.directions:
	            
	        	
	        	//HERE FOR NOW I AM GOING TO MAKE MAP MATCHING AND SEE I AM ABLE TO DO
	        	
	        	clearmap();
            	String s2 = "5000";
            	GeoPoint topLeftGft4 = (GeoPoint) map.getProjection().fromPixels(0, 0);
    	        GeoPoint bottomRightGpt4 = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
    	        System.out.println("Default displayed boundaries = "+topLeftGft4);
    	        System.out.println("Default displayed boundaries = "+bottomRightGpt4);
        		//mapadjust(s2,topLeftGft4,bottomRightGpt4);
        		System.out.println("Drawing routes after show routes button is clicked is completed");
        		
        		//CREATE A CLASS TO FETCH THE POLYLINEID=LINK_ID FROM THE AVG TABLE
        		LinkIdFetch linkIDs = new LinkIdFetch();
        		ArrayList<String> linkId = linkIDs.getPolylineId();
        		System.out.println("traffic link ids in main activity"+linkId);
        		
	        	TouchOverlay touchOverlay1 = new TouchOverlay(linkId);
	            overlayList.add(touchOverlay1);
	            closeOptionsMenu();
           		
	        	
	        	//Functions
	        	//Log.i("log","dir");
	        	//System.out.println("direc");
	        	//Log.i("startingpoint",StartingPoint.toString());
	        	
	        	//Log.i("log", "passed actionbar");
	        	
	        	//Directions();
	        	
	        	
	        	
	        	
	        	//GetRoute.setOnClickListener(this);

	        	//convert address into geo points using https:
	        	/*int sourcelat = (int) 39.01;
	        	int sourcelon = (int) -79.59;
	        	int destlat = (int) 50.11;
	        	int destlon = (int) -90.22;
	        	String uri = "http://maps.google.com/maps?saddr=" +sourcelat+","+sourcelon+"&daddr="+destlat+","+destlon;
	        	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
	        	intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
	        	startActivity(intent);*/
	            
	            return true;
	        case R.id.exit:
	            
	        	closeOptionsMenu();
	            return true;
	        case R.id.mylocation:
	        	try{
	        		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // we set up permissions after this
		        	
		        	//HERE WE HAVE TO CHECK WHETHER SERVICE IS TURNED ON TO COLLECT GPS
		        	isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		 
		            // getting network status
		            isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		            if (!isGPSEnabled && !isNetworkEnabled) {
		                // no network provider is enabled
		            	showSettingsAlert();

		            }else{
			            Criteria crit = new Criteria(); // what kind of criteria we are looking for
			            crit.setAccuracy(Criteria.ACCURACY_FINE);
			            towers = lm.getBestProvider(crit, false); //this gives the default best criteria defined above      
			            lm.requestLocationUpdates(towers, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        lm.requestLocationUpdates(towers, 500, (float) 0, this);
			            location = lm.getLastKnownLocation(towers); // towers is getting best location
			            	            
			        	if(location != null){
			               	//Toast.makeText(getBaseContext(), "success", Toast.LENGTH_LONG).show();
			               	Toast.makeText(getBaseContext(), location.toString(), Toast.LENGTH_LONG).show();
			               	int lat = (int) (location.getLatitude()*1E6);
			               	int longi = (int) (location.getLongitude()*1E6);
			               	GeoPoint ourLocation = new GeoPoint(lat, longi);
			               	
			               	OverlayItem overlayItem1 = new OverlayItem(ourLocation,"Our Location","Position");// this is technically creating a marker
			               	CustomPinpoint custom1 = new CustomPinpoint(d, TrafficMapActivity.this);
			           		custom1.insertPinpoint(overlayItem1);
			           		overlayList.add(custom1); // we are adding our overlay item to the overlay list
			           		controller.animateTo(ourLocation);
			        	//return true;
			        	}
			        	else
			        	{
			        		Toast.makeText(getBaseContext(), "No location found", Toast.LENGTH_SHORT).show();
			        	}
		            }
	        	} catch(Exception e){
	        		e.printStackTrace();
	        	}	        	

	        case R.id.searchLoc: 
	        	
	        	
	        	return true;
	        case R.id.trafficview:
	        	// here i should be calling to display a layer of traffic data--currentTraffic();
	          	map.setStreetView(false);
	          	boolean flag = map.isTraffic();
	          	if (flag == true)
	          	{
	          		map.setTraffic(false);
	          	}else
	          	{
	          		map.setTraffic(true);
	          	}
	        	
	        	return true;
	        case R.id.satellite:
	        	map.setSatellite(true);
	        	map.setStreetView(false);
	        	map.invalidate();
	        	return true;
	        case R.id.streetView:
	        	map.setSatellite(false);
	        	map.setStreetView(true);
	        	map.invalidate();
	        	return true;
	        case R.id.clear:
	        	clearmap();
	        	return true;
	        case R.id.customtraffic:
	        	clearmap();
            	String s = "5000";
            	GeoPoint topLeftGft1 = (GeoPoint) map.getProjection().fromPixels(0, 0);
    	        GeoPoint bottomRightGpt1 = (GeoPoint) map.getProjection().fromPixels(map.getWidth(), map.getHeight());
    	        System.out.println("Default displayed boundaries = "+topLeftGft1);
    	        System.out.println("Default displayed boundaries = "+bottomRightGpt1);
        		//mapadjust(s,topLeftGft1,bottomRightGpt1);
        		System.out.println("Drawing routes after show routes button is clicked is completed");
	        	//TouchOverlay touchOverlay = new TouchOverlay();
	            //overlayList.add(touchOverlay);
	            closeOptionsMenu();
	        default:
	        	return super.onOptionsItemSelected(item);
	    }
	}

	private void showSettingsAlert() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		 
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	private void Directions() {
		// TODO Auto-generated method stub
		setContentView(R.layout.actionbar);
		Log.i("onclick", "onclick activated");
//    	GetRoute.setOnClickListener(this);
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//compass.disableCompass();
		super.onPause();
		lm.removeUpdates(this);
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//compass.enableCompass();
		super.onResume();// least minDistance meters, AND at least minTime milliseconds
		//update when the location has changed by at least minDistance meters, AND at least minTime milliseconds have passed
		//40miles/hr means 17m/sec. so we for 1000 we need to set minDistance to atleast 15meters
		lm.requestLocationUpdates(towers, 10000, (float) 100, this); // we are requesting location update.on location changed will be called
	}
	

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*public void DataCollection(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		    //final TextView input = new TextView(this);
		    String SignalInformation;
		    //alert.setView(input);
		    alert.setTitle("Information");
		    alert.setMessage("Is speed low due to signal?");
		    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            //String value = input.getText().toString().trim();
		        	SignalInformation = "true";
		            Toast.makeText(getApplicationContext(), "clicked yes", Toast.LENGTH_SHORT).show();
		        }
		    });
		    
		    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	Toast.makeText(getBaseContext(), "clicked on no", Toast.LENGTH_SHORT).show();
		        	SignalInformation = "true";
		        }
		    });
		    alert.create();
		    alert.show();
	}*/
	
	public void onLocationChanged(Location l) {
		// TODO Auto-generated method stub
		clearmap();
		ArrayList<GeoPoint> gpsTraces_debugging = new ArrayList<GeoPoint>();
		List<Float> vSpeed_debugging = new ArrayList<Float>();
		lat = (int) (l.getLatitude()*1E6);
		longi = (int) (l.getLongitude()*1E6);
		GeoPoint ourLocation = new GeoPoint(lat, longi);
		CustomPinpoint custom = new CustomPinpoint(d, TrafficMapActivity.this);
        OverlayItem overlayItem = new OverlayItem(ourLocation,"Our Location","Position");
        custom.insertPinpoint(overlayItem);
        overlayList.add(custom); // we are adding our overlay item to the overlay list
        controller.animateTo(ourLocation);
        // we are uploading gps traces
        try {
        	gpsTraces.add(ourLocation);
        	gpsTraces_debugging.add(ourLocation); //This is for log file
        	System.out.println("On Location changed Updates ->"+gpsTraces.size()+","+"st->"+gpsTraces.get(gpsTraces.size()-1));
        	// Here I am trying to get speed of the device in m/sec
   			vSpeed.add(l.getSpeed());
   			vSpeed_debugging.add(l.getSpeed()); //This is for log file
   			System.out.println("We are trying to collect speed of the object and the current value is = "+vSpeed);
   			Toast.makeText(getBaseContext(), "The speed of device="+vSpeed, Toast.LENGTH_SHORT).show();
   			/*HERE I AM TRYING TO WRITE THE GPS POINTS TO A TEXT FILE IN ANROID*/
   			WritetoText w2text = new WritetoText();
   			//w2text.write(device_ID, gpsTraces, vSpeed, l.getBearing(),l.getTime());
   			w2text.write(device_ID, gpsTraces_debugging, vSpeed_debugging, l.getBearing(),l.getTime());
   			gpsTraces_debugging.clear(); //We are uploading each and every point one by one
   			vSpeed_debugging.clear(); // We are uploading each and every speed one by one
   			if(gpsTraces.size() > 4)
       		{
       			System.out.println("size is greater than 4. These are from location updates->"+gpsTraces.toString());
       			//gpsTraceCollection gpstrace = new gpsTraceCollection(device_ID,gpsTraces);
       			//gpstrace.UploadgpsTrace(device_ID,gpsTraces);

       			// HERE THE CONCEPT OF MATCHING SHOULD APPEAR. AS WE ARE GOING TO UPLOAD POINTS TO SERVER, WE SHOULD MAKE MAP MATCHING BEFORE
       			// THAT AND AFTER CALCULATE AVERAGE OF SPEED AND THEN UPLOAD THEM WITH LINK ID
       			
       			gpsTraces_copy.addAll(gpsTraces);
       			System.out.println("gpsTraces_copy->"+gpsTraces_copy);       			
       			
       			// WE ARE GOING TO CREATE A SEPERATE THREAD FOR MAP MATCHING AND THEN GET ORIGINAL TRACES
       			
       			new MapMatchingTask().execute(device_ID,gpsTraces,vSpeed);
       			
       			
       			
       			Thread.sleep(2000);
       			avgSpeed.add(average(vSpeed));
       			//new InsertGPSTraceTask().execute(device_ID,gpsTraces_copy,vSpeed);
       			gpsTraces.clear();
       			vSpeed.clear();
       			System.out.println("gpsTraces->"+gpsTraces);
       			System.out.println("Number of Gps points after collecting more than 4 ->"+gpsTraces.size());
       			
       			//THIS IS THE NEW STUFF WE ARE DOING TO TEST THE DATA IN MOBILE//////////
       			//checkSQLite check = new checkSQLite(null, device_ID, null, OldPacket_Size);
//       			Thread.sleep(5000);
       			//GPSTracesDataBase entry = new GPSTracesDataBase(TrafficMapActivity.this);
    			//entry.DbDataVerification();
    			//entry.DisplayData();
    			//entry.close();
    			
    			
    			
       		}else
       		{
       			System.out.println("Size is not greater than 4. This is from location updates");
       		}
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	private float average(List<Float> vSpeed2) {
		// TODO Auto-generated method stub
		float k = 0;
		for(int j=0; j<vSpeed2.size(); j++){
			k = k+vSpeed2.get(j);
		}
		k = k/vSpeed2.size();
		return k;
	}
	

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	


	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	



	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.i("TrafficMapActivity","DOWN");
		return true;
	}




	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		Log.i("TrafficMapAcrivity","onFling");
		return true;
	}




	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		Log.i("TrafficMapAcrivity","onScroll");
		return true;
	}




	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.i("TrafficMapAcrivity","onSingleTapUp");
		return true;
	}

 



	
	
	//This is the one we added to convert address string to lat long
	//**********************************************//
	/*
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Log.i("onclick", "onclick activated");
		String addressInput = StartingPoint.getText().toString(); //Get input text
        
		try {
                             
			List<Address> foundAdresses = gc.getFromLocationName(addressInput, 5); //Search addresses
                             
			if (foundAdresses.size() == 0) { //if no address found, display an error
				Dialog locationError = new AlertDialog.Builder(TrafficMapActivity.this)
				.setIcon(0)
				.setTitle("Error")
				.setPositiveButton("ok", null)
				.setMessage("Sorry, your address doesn't exist.")
				.create();
				locationError.show();
				Log.i("address", "address not found");
			}
			else { //else display address on map
				for (int i = 0; i < foundAdresses.size(); ++i) {
					//Save results as Longitude and Latitude
					//@todo: if more than one result, then show a select-list
					Address x = foundAdresses.get(i);
					latto = (int) (x.getLatitude()*1E6);
					longto = (int) (x.getLongitude()*1E6);
				}
				map.setBuiltInZoomControls(true);
				GeoPoint ourLocation1 = new GeoPoint(latto, longto);
				CustomPinpoint custom2 = new CustomPinpoint(d, TrafficMapActivity.this);
				OverlayItem overlayItem1 = new OverlayItem(ourLocation1,"Our Location","Position");
				custom2.insertPinpoint(overlayItem1);
				overlayList.add(custom2); // we are adding our overlay item to the overlay list
				controller.animateTo(ourLocation1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
}