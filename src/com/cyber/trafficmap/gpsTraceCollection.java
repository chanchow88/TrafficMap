package com.cyber.trafficmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class gpsTraceCollection
{
	InputStream is;
	ArrayList<GeoPoint> gpstraces = new ArrayList<GeoPoint>();
	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	public ArrayList<String> Points;
	HttpResponse response;
	String device_Id;
	
	public gpsTraceCollection(String device_ID2, ArrayList<GeoPoint> gpsTraces2) {
		// TODO Auto-generated constructor stub
		gpstraces= gpsTraces2;
		device_Id = device_ID2;
	}

	
	public void UploadgpsTrace(String device_ID2, ArrayList<GeoPoint> gpstraces) throws Exception
	{
		System.out.println("we entered uploading stage ->"+gpstraces+"geo point size->"+gpstraces.size());
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.102//gpsUpload.php");
		System.out.println("device_Id->"+device_ID2);
		List conv_gpstraces = gpstraces;

		JSONObject j = new JSONObject();
		j.put("device_Id", device_Id);
		j.put("gpstraces", gpstraces);
		//JSONArray mJSONArray = new JSONArray(j.toString());
		StringEntity se1 = new StringEntity(j.toString(),"UTF-8");
		
		
		
		
		//HERE WE ARE APPENDING DEIVCE ID TO LIST ABOVE
		//conv_gpstraces.add(device_ID2);
		//System.out.println("after adding the string"+conv_gpstraces);
		//JSONArray mJSONArray = new JSONArray(conv_gpsraces);
		//System.out.println("Json array display - > "+mJSONArray);
		//StringEntity se1 = new StringEntity(mJSONArray.toString(),"UTF-8");
		httppost.setEntity(se1);
		System.out.println("se1="+se1);
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-type", "application/json");
		try{
			response = httpclient.execute(httppost);
			System.out.println("Response from uploadingps..."+response);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch (HttpResponseException e) {
			System.err.println(e.getMessage());
		}
		try{	
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        System.out.println("bufferreader output from server="+reader); 
	        ArrayList<String> s = new ArrayList<String>();
	        String contents = null;
	        contents =reader.readLine();
	        System.out.println("contents from server="+contents);
	        Points = new ArrayList<String>();
	        String[] strPoint=contents.split("%");
	        for(String str:strPoint)
	        {
	        	System.out.println("String response="+str);
	        	Points.add(str);
	        }
		}catch(Exception e){
	        Log.e("log_tag", "Error converting result "+e.toString());
		}
	}	
}