package com.hp.wxcontrol.util;

import static com.hp.wxcontrol.util.Constants.TAG;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBUtil extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME="/data/data/com.hp.wxcontrol/databases/wxcontrol.db";//数据库名称  
    private static final int SCHEMA_VERSION=2;//版本号,则是升级之后的,升级方法请看onUpgrade方法里面的判断  
    
	public DBUtil(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION); 
	}

	public void insertAction(int codeId, int code, String extras, Context context) {

		Log.d(TAG, "DBUtil.insertAction.start");
		
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("codeId", codeId);
		values.put("code", code);
		values.put("json", extras);
		
		long result = db.insert("action", null, values);
		
		db.close();
		
		Log.d(TAG, "DBUtil.insertAction.end|result=" + result);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS action " +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT, codeId INTEGER, code INTEGER, json VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
