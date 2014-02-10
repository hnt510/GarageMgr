package dao;

import org.ninto.garagemgr.LoginActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "myDatabase.db";
	private static final String DATABASE_TABLE = "GarageMgr";
	private static final int DATABASE_VERSION = 1;
	private static final String CAR_NUMBER = "CAR_NUMBER";
	private static final String NAME = "NAME";
	private static final String PHONE_NUMBER = "PHONE_NUMBER";
	private static final String TIME = "TIME";
	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "create table " +
			DATABASE_TABLE + " (" + CAR_NUMBER +
					" TEXT primary key, " +
					NAME + " TEXT not null, " +
					PHONE_NUMBER + " TEXT, " +
					TIME + " TEXT);";
	public SqlHelper(Context context, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
					// Called when no database exists in disk and the helper class needs
					// to create a new one.
	@Override
	public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
					//Called when there is a database version mismatch meaning that
					//the version of the database on disk needs to be upgraded to
					//the current version.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
						//Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " +
				oldVersion + " to " +newVersion + ", which will destroy all old data");
						//Upgrade the existing database to conform to the new
						//version. Multiple previous versions can be handled by
						//comparing oldVersion and newVersion values.
						//The simplest case is to drop the old table and create a new one.
						db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
						//Create a new one.
						onCreate(db);
					}
	
	public boolean insert(String name,String carNumber,String phoneNumber,String time){
		// Create a new row of values to insert.
		ContentValues newValues = new ContentValues();
		// Assign values for each row.
		newValues.put(NAME, name);
		newValues.put(CAR_NUMBER, carNumber);
		newValues.put(PHONE_NUMBER, phoneNumber);
		newValues.put(TIME, time);
		
		//SqlHelper helper = new SqlHelper(null, time, null, 0);
		SQLiteDatabase db = getWritableDatabase();
		//db.execSQL("DROP TABLE IF EXISTS GarageMgr"); 
		if(db.insert(SqlHelper.DATABASE_TABLE, null, newValues)==-1)
				return false;
			else return true;
		}
	public Cursor query(String carNum) throws SQLException{
		// Specify the result column projection. Return the minimum set
		// of columns required to satisfy your requirements.
		String[] result_columns = new String[] {
				CAR_NUMBER, NAME, PHONE_NUMBER, TIME};
		// Specify the where clause that will limit our results.
		String where = CAR_NUMBER + "=" + carNum;
		// Replace these with valid SQL statements as necessary.
		String whereArgs[] = null;
		String groupBy = null;
		String having = null;
		String order = null;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(DATABASE_TABLE,
				result_columns, where,
				whereArgs, groupBy, having, order);
		if(cursor != null) {
			   cursor.moveToFirst();
		      }
		return cursor;	
		}
	}