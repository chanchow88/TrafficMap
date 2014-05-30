package com.cyber.trafficmap;

import java.io.File;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class GPSTracesDataBase {
	
	public static final String KEY_POLYLINEID = "_id";
	public static final String KEY_PARTID = "Part_id";
	public static final String KEY_X = "X";
	public static final String KEY_Y = "Y";
	public static final String KEY_POINTID = "Point_id";
	public static final String KEY_ID = "_id";
	public static final String KEY_DEVICEID = "Device_id";
	private static final String VEHICLE_SPEED = "Veh_Speed";
	
	private static final String DATABASE_NAME = "TRAFFICMAP";
	private static final String DATABASE_TABLE1 = "CountyGPSPoints";
	private static final String DATABASE_TABLE2 = "gpstraces";
	private static final int DATABASE_VERSION = 1;
		
	// we are setting instance of class;
	// we are setting context of our class;
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	private String[] allColumns = { KEY_X, KEY_Y, KEY_POINTID, KEY_POLYLINEID};
	private String[] allColumnsforUser = { KEY_X, KEY_Y, KEY_DEVICEID, KEY_ID, VEHICLE_SPEED};	      
	
	//I am trYing to delete the database I created every time I try to create it
	private static final String DB_PATH = "data/data/trafficmap/databases/TRAFFICMAP";

	private static class DbHelper extends SQLiteOpenHelper {

		
		public DbHelper(Context context) {
			//THE BELOW STATEMENT SHOULD CREATE AN EMPTY DATABASE
			//super(context, null, null, 1);
			//THIS IS BEING MODIFIED. THE BELOW IS ORIGINAL
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			//System.out.println("you entered oncreate");
			doDbCheck();
			//db.delete(DATABASE_TABLE1, null, null);
			db.execSQL("create table if not exists "+ DATABASE_TABLE1 + " (" + KEY_POLYLINEID + " integer, " 
					 + KEY_X + " text, "+ KEY_Y + " text, " + KEY_POINTID + " integer);"
					);
			//db.delete(DATABASE_TABLE2, null, null);
			db.execSQL("create table if not exists " + DATABASE_TABLE2 + " (" + KEY_ID + " integer primary key autoincrement, " +
					KEY_DEVICEID + " text, " +  KEY_X + " text, "+ KEY_Y + " text, "+ VEHICLE_SPEED + " real);"
					);
		}

		private void doDbCheck() {
			// TODO Auto-generated method stub
			try{
				System.out.println("You entered dbcheck");
				File file = new File(DB_PATH);
				//file.delete();
				System.out.println("status return for file.delete()="+file.delete());
			}catch(Exception ex)
			{
				ex.getStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE1);
			onCreate(db);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE1);
			db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE2);
			onCreate(db);
		}


	}
	
	public GPSTracesDataBase(Context c){
		ourContext = c;
	}
	
	// we have to open our database and write to it and close it
	public GPSTracesDataBase open(){
		ourHelper = new DbHelper(ourContext); // we are creating a database variable with a context
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	// we have to close after we finished it
	public synchronized void close(){
		//ourHelper.close();
		ourDatabase.close();
	}


	public void createEntry(ArrayList point_Id, ArrayList polyline_Id,
			ArrayList<GeoPoint> geoPoints)
	{
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		for (int i = 0; i<geoPoints.toArray().length ; i++){
			cv.put(KEY_POINTID, point_Id.get(i).toString());
			cv.put(KEY_POLYLINEID, polyline_Id.get(i).toString());
			cv.put(KEY_X, geoPoints.get(i).getLatitudeE6());
			cv.put(KEY_Y, geoPoints.get(i).getLongitudeE6());
		}
		//ourDatabase.delete(DATABASE_TABLE1, null, null);
		long insertId = ourDatabase.insert(DATABASE_TABLE1, null, cv);
		System.out.println("the value of insert id after inserting the values into db for table countygps is ="+insertId);
		Cursor cursor =  ourDatabase.query(DATABASE_TABLE1, allColumns, KEY_POLYLINEID + "=" +insertId, null, null, null, null);
		cursor.moveToFirst();
		cursor.close();
	}

	public void UpdateCountyGPS(ArrayList point_Id, ArrayList polyline_Id,
			ArrayList<GeoPoint> gpsTraces) {
		// TODO Auto-generated method stub
		//SQLiteDatabase db = ourHelper.getWritableDatabase();// Here I am trying to drop the table with old values
		//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
		//ourHelper.onCreate(db);
		ContentValues cv = new ContentValues();// Again I am trying to add new table
		for (int i = 0; i<gpsTraces.toArray().length ; i++){
			cv.put(KEY_POINTID, point_Id.get(i).toString());
			cv.put(KEY_POLYLINEID, polyline_Id.get(i).toString());
			cv.put(KEY_X, gpsTraces.get(i).getLatitudeE6());
			cv.put(KEY_Y, gpsTraces.get(i).getLongitudeE6());
		}
		long insertId = ourDatabase.insert(DATABASE_TABLE1, null, cv);
		System.out.println("the value of insert id after inserting the values into db for gpstraces is ="+insertId);
		Cursor cursor =  ourDatabase.query(DATABASE_TABLE1, allColumns, null , null, null, null, null);
		cursor.moveToFirst();
		cursor.close();
	}

	public void createEntryforUSER(String device_ID,
			ArrayList<GeoPoint> gpsTraces1, ArrayList veh_speed) {
		// TODO Auto-generated method stub
		// For this we are not deleting the table. because we need to add on gps traces to table where as for
		System.out.println("We are trying to collect speed of the object and the current value is = "+veh_speed);
		ContentValues cv = new ContentValues();
		for (int i = 0; i<gpsTraces1.toArray().length ; i++){
			cv.put(KEY_DEVICEID, device_ID);
			cv.put(KEY_X, gpsTraces1.get(i).getLatitudeE6());
			cv.put(KEY_Y, gpsTraces1.get(i).getLongitudeE6());
			cv.put(VEHICLE_SPEED, veh_speed.get(i).toString());// THIS HAS TO BE CHECKED. SPEED IS RETURNED IN M/S
		}
		long insertId = ourDatabase.insert(DATABASE_TABLE2, null, cv);
		System.out.println("the value of insert id after inserting the values into db is = "+insertId);
		Cursor cursor =  ourDatabase.query(DATABASE_TABLE2, allColumnsforUser, KEY_ID + "=" +insertId, null, null, null, null);
		cursor.moveToFirst();
		cursor.close();
	}
	
	public GPSTracesDataBase DbDataVerification(){
		ourHelper = new DbHelper(ourContext); // we are creating a database variable with a context
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void DisplayData(){
		Cursor cursor1 =  ourDatabase.query(DATABASE_TABLE1, allColumns, null, null, null, null, null);
		//startManagingCursor(cursor1);
		cursor1.moveToFirst();
		while(cursor1.moveToNext()){
			String qaw = cursor1.getString(cursor1.getColumnIndex(KEY_POLYLINEID));
			System.out.println("Printing the value in mobile database="+qaw);
			Toast.makeText(ourContext, "data from db="+qaw, Toast.LENGTH_SHORT).show();
		}
		cursor1.close();
	}
}

