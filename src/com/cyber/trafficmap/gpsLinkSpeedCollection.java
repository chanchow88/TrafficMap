package com.cyber.trafficmap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

public class gpsLinkSpeedCollection {
	
	InputStream is;
	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	HttpResponse response;
	String device_ID;
	int[] Link_ID = null;//here it may be wrong
	List LinkSpeeds;
	public gpsLinkSpeedCollection(String device_ID, int[] polylineID,
			List<Float> Speeds) {
		// TODO Auto-generated constructor stub
		this.device_ID = device_ID;
		this.LinkSpeeds = Speeds;
		this.Link_ID = polylineID;
	}
	
	public void UploadLinkProperties(String device_ID2, int[] polylineID, List<Float> Speeds) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("we entered uploading stage of link id with speed and device->"+device_ID2+"speed->"+Speeds);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.102//gpsLinkSpeedCollection.php");
		System.out.println("device_Id->"+device_ID2);
		// HERE WE APPEND THE THREE PARAMETERS INTO A SINGLE LIST
		//LinkSpeed.add(device_ID2);
		System.out.println("polyline id in upload stage="+polylineID.length);
		for(int i=0;i<polylineID.length;i++){
			//LinkSpeed.add(polylineID[i]);
			System.out.println("linkspeeds="+LinkSpeeds);
		}
		JSONArray mJSONArray = new JSONArray();
		//mJSONArray.put(device_ID2);
		//mJSONArray.put(polylineID.toString());
		//mJSONArray.put(avgSpeed);
		
		JSONObject json = new JSONObject();
		json.put("device_ID", new JSONArray(Arrays.asList(device_ID2)));
		json.put("Link speed",LinkSpeeds);
		//json.put("LinkID", new JSONArray(Arrays.asList(Link_ID)));
		
		for(int i=0;i<polylineID.length;i++){
			mJSONArray.put(Link_ID[i]);
			//json.put("LinkID", polylineID[i]);
			//LinkSpeed.add(polylineID[i]);
			//System.out.println("linkspeed="+LinkSpeed);
		}
		json.put("LinkID", mJSONArray);
		System.out.println("json obj="+json.toString().getBytes("UTF8"));
		
		System.out.println("Json array display - > "+mJSONArray);
		//StringEntity se1 = new StringEntity(mJSONArray.toString(),"UTF-8");
		StringEntity se1 = new StringEntity(json.toString(),"UTF-8");
		httppost.setEntity(se1);
		System.out.println("se1="+se1);
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-type", "application/json");
		try{
			response = httpclient.execute(httppost);
			System.out.println("Response from gpslinkspeeeduploadingps..."+response.getStatusLine());
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
	        ArrayList Points = new ArrayList<String>();
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
