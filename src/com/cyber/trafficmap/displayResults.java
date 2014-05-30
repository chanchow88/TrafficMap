package com.cyber.trafficmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import com.google.android.maps.GeoPoint;

public class displayResults{

	InputStream is;
	String result = "";
	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	
	//public ArrayList<GeoPoint> getGeoPoints(String s) throws IOException
	public Object[] getGeoPoints(String s, GeoPoint Xs, GeoPoint Ys) throws IOException
	{	
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.102//getGeoPoints.php"); //posting the uri to connect
		//HttpPost httppost = new HttpPost("http://10.0.2.2//getGeoPoints.php"); //posting the uri to connect in emulator
		//HttpPost httppost = new HttpPost("http://localhost//getGeoPoints.php"); //posting the uri to connect in same computer
		nameValuePairs.add(new BasicNameValuePair("Polyline_Id",s));
		String X1 = ""+(Xs.getLatitudeE6()/1E6);
		System.out.println(X1);
		String Y1 = ""+(Xs.getLongitudeE6()/1E6);
		System.out.println(Y1);
		String X2 = ""+(Ys.getLatitudeE6()/1E6);
		System.out.println(X2);
		String Y2 = ""+(Ys.getLongitudeE6()/1E6);
		System.out.println(Y2);
		System.out.println("X1="+X1+" "+"Y1="+Y1);
		System.out.println("X2="+X2+" "+"Y2="+Y2);
		//adding other values
		nameValuePairs.add(new BasicNameValuePair("Y1",X1));//THESE ARE FLIPPED AS PER DATABASE REQ
		nameValuePairs.add(new BasicNameValuePair("X1",Y1));//THESE ARE FLIPPED AS PER DATABASE REQ
		nameValuePairs.add(new BasicNameValuePair("Y2",X2));//THESE ARE FLIPPED AS PER DATABASE REQ
		nameValuePairs.add(new BasicNameValuePair("X2",Y2));//THESE ARE FLIPPED AS PER DATABASE REQ
		
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpclient.execute(httppost); // executing the request
		//System.out.println(response.getStatusLine()); // Examine the response status
		HttpEntity entity = response.getEntity(); // Get hold of the response entity
		//System.out.println(entity);
		ArrayList<GeoPoint> Points = new ArrayList<GeoPoint>();
		ArrayList<Integer> Point_Id = new ArrayList<Integer>();
		ArrayList<Integer> Polyline_Id = new ArrayList<Integer>();
		if (entity != null){
			is = entity.getContent();
			//System.out.println("is passed");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			//System.out.println("reader pass");
			String contents = null;
		    contents =reader.readLine();
		    //System.out.println("readline pass");
			//System.out.println("contents"+""+contents);
		    
			//have to handle case where there are no routes between the points passed
			if (contents != null)
			{
				String[] strPoint=contents.split("%");
				System.out.println("strPoint"+""+strPoint);
				for(String str:strPoint)
				{
			       //System.out.println(str);
			       String[] XY =str.split(":");
			       //System.out.println(XY[3]+" "+XY[2]);
			       Points.add(new GeoPoint((int) (Double.parseDouble(XY[3])*1E6),(int) (Double.parseDouble(XY[2])*1E6)) );
			       Point_Id.add(Integer.parseInt(XY[1]));
			       Polyline_Id.add(Integer.parseInt(XY[0]));
			       //System.out.println(Polyline_Id.get(index));
			   	}
			}
			else
			{
				System.out.println("There are no traffic routes");
				Points.add(Xs);
				Point_Id.add(0);
				Polyline_Id.add(0);
				System.out.println(Point_Id.get(0));
			}  
				System.out.println(Polyline_Id.size());
				is.close();
			}
		return new Object[]{Points,Point_Id,Polyline_Id};
	//return Points;
	}

}