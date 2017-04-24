package com.summerlab.chords.main;

import java.util.ArrayList;

import com.summerlab.chords.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArtistListview extends ArrayAdapter<String>{
	
	private Activity mcontext;
	private int resourceID;
	private ArrayList<String> artistList;
	private int index;
	
	public CustomArtistListview(Activity context, int resource,
			ArrayList<String> objects, int tabNumber) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.mcontext = context;
		this.resourceID = resource;
		this.artistList = objects;
		this.index = tabNumber;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mcontext.getLayoutInflater().inflate(resourceID, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon_artist_listview);
		if (index==1)
		{
			icon.setImageResource(R.drawable.artist);
		}
		else if (index==2) {
			icon.setImageResource(R.drawable.singer);
		}
		TextView artistName = (TextView) convertView.findViewById(R.id.name_artist_listview);
		artistName.setText(artistList.get(position));
		return convertView;
		
	}

	
}
