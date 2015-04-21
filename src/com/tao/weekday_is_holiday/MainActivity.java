package com.tao.weekday_is_holiday;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ListView mainList;
	CustomAdapter adapter;
	static SQLiteDatabase db;
	Cursor cursor;
	String[] content, DataBase_Titles;
	List<String> inserted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		adapter = new CustomAdapter(this);
		mainList = (ListView)findViewById(R.id.mainList);
		mainList.setOnItemClickListener(new ListViewListener());
		mainList.setAdapter(adapter);
		
		DataBase_Titles = new String[]{"subject_name", "late", "absence"};
		inserted = new ArrayList<String>();
		
		db = new SQLHelper(this).getWritableDatabase();
		DataReflesh();
	}
	
	public void DataReflesh(){
		adapter.clear();
		inserted.clear();
		if(cursor != null)
			cursor.close();
		cursor = db.query("subjects", DataBase_Titles, null, null, null, null, null);
		boolean mov = cursor.moveToFirst();
		while(mov){
			content = new String[]{cursor.getString(0),"遅刻：" + cursor.getString(1),"欠席：" + cursor.getString(2)};
			adapter.add(content);
			inserted.add(cursor.getString(0));
			mov = cursor.moveToNext();
		}
	}
	
	//教科の追加
	public void addSubject(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText subject = new EditText(this);
		builder.setTitle("追加する教科名を入力").setView(subject)
		.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String subject_name = subject.getText().toString();
				if(subject_name.isEmpty()){
					Toast.makeText(MainActivity.this, "教科名を入力してください", Toast.LENGTH_SHORT).show();
				}else{
					boolean already = false;
					for(int i = 0; inserted.size() > i; i++){ //既に教科が登録されているか
						if(inserted.get(i).equals(subject_name))
							already = true;
					}
					if(already){
						Toast.makeText(MainActivity.this, "既に登録されています", Toast.LENGTH_SHORT).show();
					}else{
						ContentValues values = new ContentValues();
						values.put("subject_name", subject.getText().toString());
						values.put("late", "0");
						values.put("absence", "0");
						db.insert("subjects", null, values);
						DataReflesh();
					}
				}
			}
		});
		builder.setNegativeButton("Cancel", null).create().show();
	}
	
	//遅刻x回で1回の欠時
	public void late_x_absence(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		final EditText edit = new EditText(this);
		edit.setInputType(InputType.TYPE_CLASS_NUMBER);
		edit.setText(String.valueOf(pref.getInt("late_x_absence", 3)));
		builder.setMessage("遅刻x回で1の欠時").setView(edit)
		.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pref.edit().putInt("late_x_absence", Integer.parseInt(edit.getText().toString())).commit();
				Toast.makeText(MainActivity.this, edit.getText().toString() + "回で登録しました", Toast.LENGTH_SHORT).show();
			}
		});
		builder.create().show();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		cursor.close();
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.addSubject){
			addSubject();
			return true;
		}
		if(id == R.id.setting){
			late_x_absence();
		}
		return super.onOptionsItemSelected(item);
	}
}
