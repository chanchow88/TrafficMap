package com.cyber.trafficmap;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

public class MapMatch {

	ArrayList<GeoPoint> gpsPoints_server = new ArrayList<GeoPoint>();
	GeoPoint gpsTraces_client;
	
	public MapMatch(ArrayList<GeoPoint> gpsTraces, GeoPoint geoPoint) {
		// TODO Auto-generated constructor stub
		gpsPoints_server = gpsTraces;
		gpsTraces_client = geoPoint;
	}
	
	/* HERE WE HAVE RUN MAP MATCHING.
	 AS PER THE PAPER I READ, PERFORM EITHER POINT TO POINT, POINT TO CURVE OR CURVE TO CURVE*/
	
	/*/http://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points
	double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
	double y = (lat2 - lat1);
	double d = Math.sqrt(x * x + y * y) * R;
	*/

	public int point2pointmatch() {
		// TODO Auto-generated method stub
		System.out.println("we entered map matching");
		int R = 6371; //radius of earth
		double[] distancebtw2 = new double[gpsPoints_server.size()];
		System.out.println("we crossed double distancebtw2="+distancebtw2);
		for(int i = 0;i<gpsPoints_server.size() ;i++){
			//System.out.println("we entered loop");
			double x = (gpsPoints_server.get(i).getLongitudeE6()/1E6 - gpsTraces_client.getLongitudeE6()/1E6) * Math.cos((gpsPoints_server.get(i).getLatitudeE6() + gpsTraces_client.getLatitudeE6()) / (2*1E6));
			//System.out.println("we entered loop assign Y");
			double y = ((gpsPoints_server.get(0).getLatitudeE6() - gpsTraces_client.getLatitudeE6())/1E6);
			distancebtw2[i] = Math.sqrt(x * x + y * y) * R;
		}
		System.out.println("we came out of loop");
		return getMinValue(distancebtw2);
	}
	

	public static int getMinValue(double[] distancebtw2)
			   throws ArrayIndexOutOfBoundsException {
		System.out.println("we entered getminvalue");
			     if(distancebtw2.length <= 0)
			       throw new ArrayIndexOutOfBoundsException ("Array has to have at least one element");
			     int minIndex = 0;
			     for(int i=1, l=distancebtw2.length; i < l; ++i) {
			    	 //System.out.println("we entered loop for min value");
			         if(distancebtw2[i] < distancebtw2[minIndex]) minIndex = i;
			       }
			     System.out.println("we are out of min distance point loop"+distancebtw2[minIndex]+"index="+minIndex);
			     //HERE WE HAVE TO DECIDE WHETHER THE POINT WE GOT IS CORRECT OR NOT
			     // WE WILL GET SEVERAL CASES.EXAMPLE SHORTED DISTANCE POINT MAY HAVE DISTANCE 1 KM AWAY WHICH IS A FALSE POINT.
			     // SO WE NEED TO MAKE SURE TO ELIMINATE SUCH CASES.
			     if(distancebtw2[minIndex] > 0.005){//hoping the output from above is in kms
			    	 //minIndex = -1;
			     }
			     return minIndex;
			   }
	
}
