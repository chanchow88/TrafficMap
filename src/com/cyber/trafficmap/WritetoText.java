package com.cyber.trafficmap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class WritetoText {


	public void write(String device_ID, ArrayList<GeoPoint> gpsTraces,
			List<Float> vSpeed, float f, long l) {
		// TODO Auto-generated method stub
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "Traces");
	        //File root1 = new File(context.getFilesDir(), "Traces"); 
	        if (!root.exists()) {
	            root.mkdirs();
	            Log.i("Writetotext","found root");
	        }
	        JSONObject obj = new JSONObject();
	        obj.put("device_ID", device_ID);
	        obj.put("gpsTraces", gpsTraces);
	        obj.put("Speed", vSpeed);
	        // obj.put("Bearing", f);
	        obj.put("Time", l);
	        
	        File gpxfile = new File(root, "gpsCollection.txt");
	        //FileWriter writer = new FileWriter(gpxfile);
	        if(gpxfile.exists()){
	        	FileWriter writer = new FileWriter(gpxfile,true);
	        	writer.append(gpsTraces.toString());
		        writer.append(device_ID);
		        writer.append(vSpeed.toString());
		        writer.append(Long.toString(l));
		        // Calendar c = Calendar.getInstance();
		        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        // String formattedDate = df.format(c.getTime());
		        // writer.append(formattedDate);
		        // formattedDate have current date/time
		        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
		        // Now we display formattedDate value in TextView
	        	//writer.write(obj.toString());
		        writer.flush();
		        writer.close();
	        }
		    else
		    {
		    	FileWriter writer = new FileWriter(gpxfile);
	        	writer.append(gpsTraces.toString());
		        writer.append(device_ID);
		        writer.append(vSpeed.toString());
		    	//writer.write(obj.toString());
		        writer.flush();
		        writer.close();
	        }
	        
	        
	        Log.i("Write2Text","inserted");
	        //Toast.makeText(WritetoText.this, "Saved", Toast.LENGTH_SHORT).show();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
