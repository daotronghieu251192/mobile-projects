package com.summerlab.chords.main;

import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.summerlab.chords.adapter.CustomSongListAdapter;

import com.summerlab.chords.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SongOfArtistResultActivity extends ActionBarActivity {
	private String artistID;
	private String artistName;
	private DataBaseHelper dataBaseHelper;
	private TextView artistNameTxt;
	private ListView songListResult;
	private final String ARTIST_ID_MESSAGE = "ARTIST_ID";
	private final String ARTIST_NAME_MESSAGE = "ARTIST_NAME";
	private final String TYPE = "artist_type";
	private final String signTextSinger = "[Si]";
	private final String signTextAuthor = "[Au]";
	private final String signTextArtist = "[A]";
	private String type = "";
	private ArrayList<Song> songList;
	private CustomSongListAdapter songAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_song_artist);

		final String deviceId = MD5.md5(getContentResolver(),
				Settings.Secure.ANDROID_ID).toUpperCase();

		AdView mAdView = (AdView) findViewById(R.id.adView);
		// AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId)
		// .build();
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
		final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			// notify user you are online
			mAdView.setVisibility(View.VISIBLE);
		} else {
			// notify user you are not online
			mAdView.setVisibility(View.GONE);
		}

		makeActionbar();
		dataBaseHelper = MainActivity.dtBaseHelper;
		songListResult = (ListView) findViewById(R.id.songOfartistList);
		OnClickItemtListView();
		// ----------------get Intent
		Intent intent = getIntent();
		artistID = intent.getStringExtra(ARTIST_ID_MESSAGE);
		type = intent.getStringExtra(TYPE);
		artistName = intent.getStringExtra(ARTIST_NAME_MESSAGE);

		artistNameTxt.setText(artistName);
	}

	public void getListResult() {
		if (type.equals(signTextAuthor)) {
			songList = dataBaseHelper.ListSong_Author(artistID);
		} else if (type.equals(signTextSinger)) {
			songList = dataBaseHelper.ListSong_Singer(artistID);
		} else if (type.equals(signTextArtist)) {
			songList = dataBaseHelper.ListSong_Artist(artistID);
		}
	}

	public void showListResult() {
		songAdapter = new CustomSongListAdapter(this,
				R.layout.custom_song_lisview, songList, dataBaseHelper);
		songListResult.setAdapter(songAdapter);
	}

	public void OnClickItemtListView() {
		songListResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String searchInput = "" + songList.get(position).getID();
				Intent intent = new Intent(getApplicationContext(),
						Chord_Detail.class);
				intent.putExtra(MainActivity.EXTRA_MESSAGE, searchInput);
				startActivity(intent);
			}

		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// ----------------------------
		getListResult();
		showListResult();
	}

	// -------------- Make ActionBar && config searchButton--------
	public void makeActionbar() {
		final ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.artist_actionbar, null);
		artistNameTxt = (TextView) mCustomView.findViewById(R.id.artist_name);
		ImageButton backBtn = (ImageButton) mCustomView
				.findViewById(R.id.backbtn_artistActionbar);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		final int actionBarColor = getResources().getColor(R.color.deep_blue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
	}

}
