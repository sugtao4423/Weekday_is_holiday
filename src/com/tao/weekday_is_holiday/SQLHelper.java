package com.tao.weekday_is_holiday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

	public SQLHelper(Context context) {
		//DataBaseName is "DataBase"
		super(context, "DataBase", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		//create tables in "DataBase"
		db.execSQL("create table subjects("
				+ "subject_name text not null,"
				+ "late integer,"
				+ "absence integer"
				+ ");");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//if SQL's version was changed, call
	}

}
