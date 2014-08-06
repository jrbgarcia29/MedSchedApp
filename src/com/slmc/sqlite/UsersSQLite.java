package com.slmc.sqlite;

import com.slmc.models.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UsersSQLite extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "MedDB";

	public UsersSQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_USERS_TABLE = "CREATE TABLE users( "
				+ "USER_ID int(11) NOT NULL,"
				+ "PIN varchar(11) DEFAULT NULL,"
				+ "STATUS varchar(20) DEFAULT NULL,"
				+ "PRIMARY KEY (USER_ID, PIN)" + ");";

		db.execSQL(CREATE_USERS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS users");
		this.onCreate(db);
	}

	private static final String TABLE_NAME = "users";

	private static final String USER_ID = "USER_ID";
	private static final String PIN = "PIN";
	private static final String STATUS = "STATUS";

	public void addUser(User user) {

		SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(USER_ID, user.getUserId());
			values.put(PIN, user.getPin());
			values.put(STATUS, user.getStatus());

			db.insert(TABLE_NAME, null, values);
	
		db.close();
	}

	public String getOLUser() {
		String pin=null;

		String query = "SELECT  * FROM " + TABLE_NAME + " where status='ONLINE'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {		
				pin = cursor.getString(1);
			} while (cursor.moveToNext());
		}		
		return pin;
	}

	public void setUserStatus(String pin){
		
		String query = "UPDATE users SET status='OFFLINE' WHERE PIN='"+ pin +"';";
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(query);
		
	}
}