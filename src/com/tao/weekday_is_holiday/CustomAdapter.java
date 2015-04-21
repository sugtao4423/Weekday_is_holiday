package com.tao.weekday_is_holiday;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String[]> {
	
	private LayoutInflater mInflater;

	public CustomAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}
	class ViewHolder{
		TextView subject, late, absence;
	}
	@Override
	public View getView(int position, View view, ViewGroup parent){
		ViewHolder holder;
		String[] array = getItem(position);
		
		if(view == null){
			view = mInflater.inflate(R.layout.custom_list, null);
			
			holder = new ViewHolder();
			holder.subject = (TextView)view.findViewById(R.id.subject_name);
			holder.late = (TextView)view.findViewById(R.id.late);
			holder.absence = (TextView)view.findViewById(R.id.absence);
			
			view.setTag(holder);
		}else
			holder = (ViewHolder)view.getTag();
		
		if(position % 2 == 0)
			view.setBackgroundResource(R.drawable.position0);
		else
			view.setBackgroundResource(R.drawable.position1);
		
		holder.subject.setText(array[0]);
		holder.late.setText(array[1]);
		holder.absence.setText(array[2]);
		
		return view;
	}
}
