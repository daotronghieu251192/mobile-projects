package com.summerlab.chords.adapter;

import java.util.ArrayList;

import com.summerlab.chords.main.CategoryData;

import com.summerlab.chords.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeListviewAdapter extends ArrayAdapter<CategoryData> {

	private Activity context = null;
	private ArrayList<CategoryData> myArray = null;
	private int layoutId;

	public HomeListviewAdapter(Activity context, int resource,
			ArrayList<CategoryData> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myArray = objects;
		this.layoutId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = context.getLayoutInflater().inflate(layoutId, null);
		}
		if (myArray.size() > 0 && position >= 0) {
			CategoryData data = myArray.get(position);
			ImageView image = (ImageView) convertView.findViewById(R.id.home_listview_icon);
			if (image != null)
			{
				int imageRes = data.getImageRes();
				image.setImageResource(imageRes);
			}
			TextView titleTextView = (TextView) convertView.findViewById(R.id.home_listview_title);
			if (titleTextView != null)
			{
				String title = data.getTitle();
				titleTextView.setText(title);
			}
			TextView countTextView = (TextView) convertView.findViewById(R.id.home_listview_number);
			if (countTextView != null) {
				int count = data.getCount();
				countTextView.setText("" + count);
			}
		}

		return convertView;
	}

}
