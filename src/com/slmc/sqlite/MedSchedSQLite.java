package com.slmc.sqlite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.slmc.models.MedIntakeRecord;
import com.slmc.models.MedSched;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MedSchedSQLite extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 7;
	private static final String DATABASE_NAME = "MedDBase";

	private static final String TABLE_MED_SCHED = "med_schedtbl";
	private static final String MED_ID = "MED_SCHED_ID";
	private static final String PIN = "PIN";
	private static final String MED_NAME = "MED_NAME";
	private static final String FREQ = "FREQ";
	private static final String START_DATE = "START_DATE";
	private static final String END_DATE = "END_DATE";
	private static final String START_TIME = "START_TIME";

	private static final String TABLE_MED_INTAKE = "med_intake";

	private static final String ACTUAL_DATETIME_TAKEN = "ACTUAL_DATETIME_TAKEN";
	
	private static final String UPLOADED = "UPLOADED";

	public MedSchedSQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MED_SCHED_TABLE = "CREATE TABLE med_schedtbl( "
				+ "MED_SCHED_ID int(11) NOT NULL,"
				+ "PIN varchar(11) DEFAULT NULL,"
				+ "MED_NAME varchar(100) DEFAULT NULL,"
				+ "FREQ varchar(20) DEFAULT NULL, "
				+ "START_DATE date DEFAULT NULL, "
				+ "END_DATE date DEFAULT NULL,"
				+ "START_TIME varchar(45) DEFAULT NULL,"
				+ "PRIMARY KEY (MED_SCHED_ID)" + ");";

		db.execSQL(CREATE_MED_SCHED_TABLE);

		String CREATE_MED_INTAKE_TABLE = "CREATE TABLE med_intake( "
				+ "MED_INTAKE_ID INTEGER," + "PIN varchar(10),"
				+ "MED_NAME varchar(100) DEFAULT NULL,"
				+ "ACTUAL_DATETIME_TAKEN varchar(50) DEFAULT NULL, "
				+ "UPLOADED varchar(3) DEFAULT 'false', "
				+ "PRIMARY KEY (MED_INTAKE_ID)" + ");";

		db.execSQL(CREATE_MED_INTAKE_TABLE);

		// db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS med_schedtbl");
		db.execSQL("DROP TABLE IF EXISTS med_intake");
		this.onCreate(db);
		// db.close();
	}

	public void removeAllMedScheds() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_MED_SCHED);
		// db.close();
	}

	public void addAllMedSched(MedSched[] medScheds) {

		SQLiteDatabase db = this.getWritableDatabase();

		for (int i = 0; i < medScheds.length; i++) {
			MedSched medSched = new MedSched();
			medSched = medScheds[i];
			ContentValues values = new ContentValues();
			values.put(MED_ID, medSched.getMedSchedId());
			values.put(PIN, medSched.getPin());
			values.put(MED_NAME, medSched.getMedName());
			values.put(FREQ, medSched.getFreq());
			values.put(START_DATE, medSched.getStartDate());
			values.put(END_DATE, medSched.getEndDate());
			values.put(START_TIME, medSched.getStartTime());
			db.insert(TABLE_MED_SCHED, null, values);
		}

		// db.close();
	}

	public List<MedSched> getAllMedScheds() {
		List<MedSched> listMedScheds = new LinkedList<MedSched>();

		String query = "SELECT  * FROM " + TABLE_MED_SCHED;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		MedSched medSched = null;
		if (cursor.moveToFirst()) {
			do {
				medSched = new MedSched();
				medSched.setMedSchedId(Integer.parseInt(cursor.getString(0)));
				medSched.setPin(cursor.getString(1));
				medSched.setMedName(cursor.getString(2));
				medSched.setFreq(cursor.getString(3));
				medSched.setStartDate(cursor.getString(4));
				medSched.setEndDate(cursor.getString(5));
				medSched.setStartTime(cursor.getString(6));

				listMedScheds.add(medSched);
			} while (cursor.moveToNext());
		}
		// db.close();
		return listMedScheds;
	}

	public void addMedIntakeRecord(MedIntakeRecord medIntakeRecord) {

		SQLiteDatabase db = this.getWritableDatabase();
		
		if ((medIntakeRecord.isUploaded() == null)) {
			medIntakeRecord.setUploaded("false");
		}
		
		if (!(recordExist(medIntakeRecord))) {
			ContentValues values = new ContentValues();
			values.put(PIN, medIntakeRecord.getPin());
			values.put(MED_NAME, medIntakeRecord.getMedName());
			values.put(ACTUAL_DATETIME_TAKEN, medIntakeRecord.getActualDateTime());
			values.put(UPLOADED, medIntakeRecord.isUploaded());
			db.insert(TABLE_MED_INTAKE, null, values);
		}
		// db.close();
	}
	
	public boolean recordExist(MedIntakeRecord medIRec){
		List<MedIntakeRecord> listMedIntakeRecords = new LinkedList<MedIntakeRecord>();
		
		String query2 = "SELECT  * FROM " + TABLE_MED_INTAKE + " where PIN LIKE '"
				+ medIRec.getPin() +"' AND MED_NAME LIKE'"+ medIRec.getMedName()+"' AND ACTUAL_DATETIME_TAKEN LIKE '"+medIRec.getActualDateTime()+"';";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query2, null);
		
		MedIntakeRecord medIntakeRecord = null;
		if (cursor.moveToFirst()) {
			do {
				medIntakeRecord = new MedIntakeRecord();
				medIntakeRecord.setPin(cursor.getString(1));
				medIntakeRecord.setMedName(cursor.getString(2));
				medIntakeRecord.setActualDateTime(cursor.getString(3));
				listMedIntakeRecords.add(medIntakeRecord);
			} while (cursor.moveToNext());
		}

		if (listMedIntakeRecords.size()>0) {
			return true;
		}else {
			return false;
		}
				
	}
	
	public void updateLocalMedIntakeRecord(MedIntakeRecord medIntakeRecord) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PIN, medIntakeRecord.getPin());
		values.put(MED_NAME, medIntakeRecord.getMedName());
		values.put(ACTUAL_DATETIME_TAKEN, medIntakeRecord.getActualDateTime());
		values.put(UPLOADED, "true");	
		String[] whereClauseVals = new String[] {medIntakeRecord.getPin(), medIntakeRecord.getMedName(), medIntakeRecord.getActualDateTime()};
		db.update(TABLE_MED_INTAKE, values, "PIN = ? AND MED_NAME = ? AND ACTUAL_DATETIME_TAKEN = ?", whereClauseVals);
		
		Log.d("Dumaan sa update", whereClauseVals.toString());
	}
	

	public List<MedIntakeRecord> getAllMedIntakeRecords(String pin, String medName) {
		List<MedIntakeRecord> listMedIntakeRecords = new LinkedList<MedIntakeRecord>();

		String query = "SELECT  * FROM " + TABLE_MED_INTAKE + " where PIN='"
				+ pin +"';";
		
		String query2 = "SELECT  * FROM " + TABLE_MED_INTAKE + " where PIN='"
				+ pin +"' AND MED_NAME LIKE'"+ medName+"';";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor;
		if (medName==null) {
			cursor = db.rawQuery(query, null);
		}else {
			cursor = db.rawQuery(query2, null);
		}
		

		MedIntakeRecord medIntakeRecord = null;
		if (cursor.moveToFirst()) {
			do {
				medIntakeRecord = new MedIntakeRecord();
				medIntakeRecord.setPin(cursor.getString(1));
				medIntakeRecord.setMedName(cursor.getString(2));
				medIntakeRecord.setActualDateTime(cursor.getString(3));
				medIntakeRecord.setUploaded(cursor.getString(4));
				listMedIntakeRecords.add(medIntakeRecord);
			} while (cursor.moveToNext());
		}

		// db.close();

		return listMedIntakeRecords;
	}

	public String getPreviousDateTimeIntake(String pin, String medName) {
		String previousDTIntake = null;
		String query = "SELECT MAX(MED_INTAKE_ID), ACTUAL_DATETIME_TAKEN FROM med_intake where PIN='"
				+ pin + "' AND MED_NAME='" + medName + "'";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				previousDTIntake = cursor.getString(1);
				if (previousDTIntake == null) {
					previousDTIntake = "NO INTAKE HISTORY";
				}
			} while (cursor.moveToNext());

		}
		// db.close();
		return previousDTIntake;
	}

	public List<String> getMedNames(String pin) {
		List<String> medNames = new ArrayList<String>();

		String query = "SELECT DISTINCT MED_NAME FROM med_schedtbl where PIN='"
				+ pin  + "'";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				medNames.add( cursor.getString(0));
			} while (cursor.moveToNext());

		}
		return medNames;
	}

	public void clearAllIntakeHistory() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_MED_INTAKE);
		// db.close();
	}

}