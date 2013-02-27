package com.mayqlzu.apphere;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class Database extends SQLiteOpenHelper {

	/*
	 * members table scheme:
	 * id	name 	number
	 * 0	xiaoma	13681749102
	 * 1	pangzi	13877787683
	 * 2	laowang	13687700773
	 * ...
	 * people may have the same name, so use id to differentiate them
	 */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "appHereDB";
    
    private static final String MEMBERS_TABLE = "members";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    
    private static final String CREATE_MEMBERS_TABLE_IF_NOT_EXISTS =
                "CREATE TABLE IF NOT EXISTS " + MEMBERS_TABLE + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT, " +
                NUMBER + " TEXT);";

    Database(Context context) {
    	// open if exist; else create
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEMBERS_TABLE_IF_NOT_EXISTS);
       
        // for test
        db.execSQL("INSERT INTO " + MEMBERS_TABLE + " VALUES (1, 'xiaomp', '13681749102' ); ");
        db.execSQL("INSERT INTO " + MEMBERS_TABLE + " VALUES (2, 'peipei', '15021461916' ); ");
        
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
	
	public Cursor queryAllMemberNames(){
		return this.getReadableDatabase().query(
				/* 
				 * must make sure column _id exit in returned Cursor, 
				 * even though we don't bind it to ListView
				 * or SimpleCursorAdapter.swapCursor() will report an exception
				 */
				MEMBERS_TABLE, new String[]{BaseColumns._ID, NAME}, null, null, null, null, null);
	}
}