package com.cyber.trafficmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LinkIdFetch {
	String a;
	JSONArray link_Ids = null;
	ArrayList<String> ids = new ArrayList<String>();
	public ArrayList<String> getPolylineId() {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.102//gpslinkfetch.php");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
			//System.out.println(response.getStatusLine()); // Examine the response status
			HttpEntity entity = response.getEntity(); // Get hold of the response entity
			if (response != null) {
	            InputStream in = response.getEntity().getContent(); 
	            a = convertStreamToString(in);
	            Log.i("Read from Server", a);
	            link_Ids = new JSONArray(a);
	            for (int i = 0; i < link_Ids.length(); i++) {  // **line 2**
	                JSONObject childJSONObject = link_Ids.getJSONObject(i);
	                ids.add(childJSONObject.getString("Link_Id"));
	           }
	        }
			System.out.println("polyline ids="+ids.size()+"first point="+ids.get(0));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // executing the request
          catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ids;
		
	}
	
	private static String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}

}
