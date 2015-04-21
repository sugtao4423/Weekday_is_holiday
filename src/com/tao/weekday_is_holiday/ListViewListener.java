package com.tao.weekday_is_holiday;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListViewListener implements OnItemClickListener {

	@Override
	public void onItemClick(final AdapterView<?> parent, View view, final int position,
			long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
		String[] items = new String[]{"遅刻を1追加", "欠席を1追加", "ミスった", "この科目を削除"};
		
		//0 = subject_name, 1 = late, 2 = absence
		final String[] selectItem = (String[])parent.getItemAtPosition(position);
		
		builder.setTitle(selectItem[0])
		.setItems(items, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
				
				if(which == 0){
					int lateplus = Integer.parseInt(selectItem[1].substring(3)) + 1;
					//遅刻回数が規定に達したかどうか
					if(lateplus >= pref.getInt("late_x_absence", 3)){
						//遅刻を0に
						MainActivity.db.execSQL("update subjects set late = '0' where subject_name = '" + selectItem[0] + "';");
						//欠席を+1に
						int absenceplus = Integer.parseInt(selectItem[2].substring(3)) + 1;
						MainActivity.db.execSQL("update subjects set absence = '" + String.valueOf(absenceplus)
								+ "' where subject_name = '" + selectItem[0] + "';");
						((MainActivity)parent.getContext()).DataReflesh();
						Toast.makeText(parent.getContext(),
								"遅刻が" + pref.getInt("late_x_absence", 3) + "回に達したので欠席を1増やしました"
								, Toast.LENGTH_LONG).show();
					}else{
						MainActivity.db.execSQL("update subjects set late = '" + String.valueOf(lateplus)
								+ "' where subject_name = '" + selectItem[0] + "';");
						((MainActivity)parent.getContext()).DataReflesh();
						Toast.makeText(parent.getContext(), "遅刻を1増やしました", Toast.LENGTH_SHORT).show();
					}
				}
				
				if(which == 1){
					int absenceplus = Integer.parseInt(selectItem[2].substring(3)) + 1;
					MainActivity.db.execSQL("update subjects set absence = '" + String.valueOf(absenceplus)
								+ "' where subject_name = '" + selectItem[0] + "';");
					((MainActivity)parent.getContext()).DataReflesh();
					Toast.makeText(parent.getContext(), "欠席を1増やしました", Toast.LENGTH_SHORT).show();
				}
				
				if(which == 2){
					AlertDialog.Builder miss = new AlertDialog.Builder(parent.getContext());
					String[] missItem = new String[]{"遅刻を1減らす", "欠席を1減らす"};
					miss.setItems(missItem, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){
								//遅刻が0回かの判定
								if(Integer.parseInt(selectItem[1].substring(3)) == 0){
									//遅刻を規定-1に
									MainActivity.db.execSQL("update subjects set late = '"+
									(pref.getInt("late_x_absence", 3) - 1) +
									"' where subject_name = '" + selectItem[0] + "';");
									//欠席を-1
									int absenceplus = Integer.parseInt(selectItem[2].substring(3)) - 1;
									MainActivity.db.execSQL("update subjects set absence = '" + String.valueOf(absenceplus)
												+ "' where subject_name = '" + selectItem[0] + "';");
								}else{
									//遅刻が!=0だった場合
									int lateoff = Integer.parseInt(selectItem[1].substring(3)) - 1;
									MainActivity.db.execSQL("update subjects set late = '" + String.valueOf(lateoff)
											+ "' where subject_name = '" + selectItem[0] + "';");
								}
								((MainActivity)parent.getContext()).DataReflesh();
								Toast.makeText(parent.getContext(), "遅刻を1減らしました", Toast.LENGTH_SHORT).show();
							}
							
							if(which == 1){
								int absenceplus = Integer.parseInt(selectItem[2].substring(3)) - 1;
								MainActivity.db.execSQL("update subjects set absence = '" + String.valueOf(absenceplus)
											+ "' where subject_name = '" + selectItem[0] + "';");
								((MainActivity)parent.getContext()).DataReflesh();
								Toast.makeText(parent.getContext(), "欠席を1減らしました", Toast.LENGTH_SHORT).show();
							}
						}
					});
					miss.create().show();
				}
				
				if(which == 3){
					AlertDialog.Builder delete = new AlertDialog.Builder(parent.getContext());
					delete.setMessage(selectItem[0] + "を削除してもよろしいですか？")
					.setPositiveButton("OK", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MainActivity.db.execSQL("delete from subjects where subject_name = '" + selectItem[0] + "';");
							((MainActivity)parent.getContext()).DataReflesh();
							Toast.makeText(parent.getContext(), selectItem[0] + "を削除しました", Toast.LENGTH_SHORT).show();
						}
					});
					delete.setNegativeButton("Cancel", null).create().show();
				}
			}
		});
		builder.create().show();
	}
}
