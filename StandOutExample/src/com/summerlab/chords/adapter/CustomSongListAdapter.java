package com.summerlab.chords.adapter;

import java.util.ArrayList;

import com.summerlab.chords.main.DataBaseHelper;
import com.summerlab.chords.main.ShowResultActivity;
import com.summerlab.chords.main.Song;

import com.summerlab.chords.R;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSongListAdapter extends ArrayAdapter<Song> {
	
	private Activity mcontext;
	private ArrayList<Song> songList;
	private int resourceID;
	private DataBaseHelper database;

	public CustomSongListAdapter(Activity context, int resource,
			ArrayList<Song> objects, DataBaseHelper databaseHelper) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.mcontext = context;
		this.songList = objects;
		this.resourceID = resource;
		this.database = databaseHelper;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mcontext.getLayoutInflater().inflate(resourceID, null);
		}
		final Song song = songList.get(position);
		if (song != null)
		{
			TextView songName= (TextView) convertView.findViewById(R.id.songName_resultScreen);
			TextView firstLyric= (TextView) convertView.findViewById(R.id.songFirstLyric_resultScreen);
			final ImageView favoriteBtn = (ImageView) convertView.findViewById(R.id.favoriteImg_resultSceen);
			songName.setText(song.getSongTitle());
			firstLyric.setText(song.getFirstLyric());
			if (song.isFavorite()) {
				favoriteBtn.setImageResource(R.drawable.heartfull);
			}
			else {
				favoriteBtn.setImageResource(R.drawable.favorite);
			}
			
			favoriteBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (song.isFavorite()) 
					{
						database.deleteFavorite(song.getID());
						song.setFavorite(false);
						favoriteBtn.setImageResource(R.drawable.favorite);
						ShowResultActivity.flag_changed = true;
					}
					else {
						database.addFavorite(song.getID());
						song.setFavorite(true);
						favoriteBtn.setImageResource(R.drawable.heartfull);
						ShowResultActivity.flag_changed = true;
					}
				}
			});
			
		}
		return convertView;
	}
	
	
}
