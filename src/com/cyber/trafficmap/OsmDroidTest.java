/*package com.cyber.trafficmap;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import android.app.Activity;
import android.os.Bundle;

public class OsmDroidTest extends Activity {
    *//** Called when the activity is first created. *//*
    private MapController mapController;
    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mvMain);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(15);
        GeoPoint point2 = new GeoPoint(51496994, -134733);
        mapController.setCenter(point2);
    }
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
}   




package com.cyber.trafficmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;

//Lot's of imports here... see full source

public class simpleGoogleMaps extends MapActivity {
	protected boolean isRouteDisplayed() { return false; }
	private MapView myMap;
	private Button btnSearch;
	private EditText adress;
	private Geocoder gc;
	private double lat;
	private double lon;
     
     
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionbar);
     
		myMap = (MapView) findViewById(R.id.simpleGM_map); //Get map from XML
		btnSearch = (Button) findViewById(R.id.simpleGM_btn_search); //Get button from XML
		adress = (EditText) findViewById(R.id.simpleGM_adress); //Get address from XML
     
		gc = new Geocoder(this); //create new geocoder instance
             
		btnSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String addressInput = adress.getText().toString(); //Get input text
                             
				try {
                                     
					List<Address> foundAdresses = gc.getFromLocationName(addressInput, 5); //Search addresses
                                     
    		if (foundAdresses.size() == 0) { //if no address found, display an error
    			Dialog locationError = new AlertDialog.Builder(simpleGoogleMaps.this)
    			.setIcon(0)
    			.setTitle("Error")
    			.setPositiveButton(R.string.ok, null)
    			.setMessage("Sorry, your address doesn't exist.")
    			.create();
    			locationError.show();
    		}
    		else { //else display address on map
    			for (int i = 0; i < foundAdresses.size(); ++i) {
    				//Save results as Longitude and Latitude
    				//@todo: if more than one result, then show a select-list
    				Address x = foundAdresses.get(i);
    				lat = x.getLatitude();
    				lon = x.getLongitude();
          }
        navigateToLocation((lat * 1000000), (lon * 1000000), myMap); //display the found address
        }
      }
      catch (Exception e) {
        //@todo: Show error message
      }
                             
    }
  });
  }
 
*//**
* Navigates a given MapView to the specified Longitude and Latitude
*//*
public static void navigateToLocation (double latitude, double longitude, MapView mv) {
	GeoPoint p = new GeoPoint((int) latitude, (int) longitude); //new GeoPoint
	mv.displayZoomControls(true); //display Zoom (seems that it doesn't work yet)
	MapController mc = mv.getController();
	mc.animateTo(p); //move map to the given point
	int zoomlevel = mv.getMaxZoomLevel(); //detect maximum zoom level
  	mc.setZoom(zoomlevel - 1); //zoom
  	mv.setSatellite(false); //display only "normal" mapview    
	}
}*/