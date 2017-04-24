package com.summerlab.chords.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.summerlab.chords.adapter.CustomSongListAdapter;

import com.summerlab.chords.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMuxer.OutputFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class ShowResultActivity extends ActionBarActivity {
	private DataBaseHelper dataBaseHelper;
	private ListView songListView;
	private ListView authorListView;
	private ListView singerListView;
	private ListView favoriteListView;
	private String searchInputString;
	private AutoCompleteTextView searchCompleteTextView;
	private TabHost tab;
	private final String signTextSong = "[S]";
	private final String signTextArtist = "[A]";
	private final String signTextSinger = "[Si]";
	private final String signTextAuthor = "[Au]";
	private Searched markSearched;
	private final String ARTIST_ID_MESSAGE = "ARTIST_ID";
	private final String ARTIST_NAME_MESSAGE = "ARTIST_NAME";
	private final String TYPE = "artist_type";
	private ArrayList<Song> songList;
	private ArrayList<Song> favoriteList;
	private ArrayList<Artist> singerList;
	private ArrayList<Artist> authorList;
	private CustomSongListAdapter songAdapter;
	private CustomSongListAdapter favoriteAdapter;
	public static boolean flag_changed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_layout);

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

		// --------- get Intent
		Intent intent = getIntent();
		searchInputString = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		int tabInitial = Integer.parseInt(intent
				.getStringExtra(MainActivity.TAB_SELECT_ID));
		// --------------------
		dataBaseHelper = MainActivity.dtBaseHelper;
		// ---------Initial component
		songListView = (ListView) findViewById(R.id.songListView);
		OnClickSong();
		authorListView = (ListView) findViewById(R.id.authorListView);
		OnClickAuthor();
		singerListView = (ListView) findViewById(R.id.singerListView);
		OnClickSinger();
		favoriteListView = (ListView) findViewById(R.id.favoriteListView);
		OnClickFavorite();

		markSearched = new Searched();
		// -------------
		makeActionbar();
		setAutoCompleteTextView();
		// ----------load Tab host
		loadTab(tabInitial);
	}

	public void loadTab(int initialTab) {
		tab = (TabHost) findViewById(android.R.id.tabhost);
		tab.setup();

		TabHost.TabSpec spec;
		// Make tab Song
		spec = tab.newTabSpec("tabSongs");
		spec.setContent(R.id.tabSongs);
		View tabView = createTabView(this, "Songs");
		spec.setIndicator(tabView);
		tab.addTab(spec);
		// Make tab Authors
		spec = tab.newTabSpec("tabAuthors");
		spec.setContent(R.id.tabAuthors);
		tabView = createTabView(this, "Authors");
		spec.setIndicator(tabView);
		tab.addTab(spec);
		// Make tab Singers
		spec = tab.newTabSpec("tabSingers");
		spec.setContent(R.id.tabSingers);
		tabView = createTabView(this, "Singers");
		spec.setIndicator(tabView);
		tab.addTab(spec);
		// Make tab Favorites
		spec = tab.newTabSpec("tabFavorites");
		spec.setContent(R.id.tabFavorites);
		tabView = createTabView(this, "Favorites");
		spec.setIndicator(tabView);
		tab.addTab(spec);

		tab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (flag_changed) {
					setSongResultListview(getSongResult(searchInputString));
					markSearched.song = true;
					setFavoriteResultListview(getFavoriteResult(searchInputString));
					markSearched.favorite = true;
					flag_changed = false;
				}

				int currentTab = tab.getCurrentTab();
				switch (currentTab) {
				case 0:
					if (!markSearched.song) {
						setSongResultListview(getSongResult(searchInputString));
						markSearched.song = true;
					}
					break;
				case 1:
					if (!markSearched.author) {
						setAuthorResultListview(getAuthorResult(searchInputString));
						markSearched.author = true;
					}
					break;
				case 2:
					if (!markSearched.singer) {
						setSingerResultListview(getSingerResult(searchInputString));
						markSearched.singer = true;
					}
					break;
				case 3:
					if (!markSearched.favorite) {
						setFavoriteResultListview(getFavoriteResult(searchInputString));
						markSearched.favorite = true;
					}
					break;
				default:
					break;
				}
			}
		});

		switch (initialTab) {
		case 0:
			setSongResultListview(getSongResult(searchInputString));
			markSearched.song = true;
			break;
		case 1:
			tab.setCurrentTab(1);
			break;
		case 2:
			tab.setCurrentTab(2);
			break;
		case 3:
			tab.setCurrentTab(3);
			break;
		default:
			break;
		}
	}

	private static View createTabView(Context context, String tabText) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_custom,
				null, false);
		TextView tv = (TextView) view.findViewById(R.id.tabTitleText);
		tv.setText(tabText);
		return view;
	}

	// -------------- Make ActionBar && config search button --------
	public void makeActionbar() {
		final ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.song_detail_actionbar,
				null);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		final int actionBarColor = getResources().getColor(R.color.deep_blue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
	}

	public ArrayList<String> getSongResult(String searchInput) {
		if (songList == null) {
			songList = new ArrayList<Song>();
		}
		songList.clear();
		songList = dataBaseHelper.searchFunctionII(searchInput);
		ArrayList<String> songNameList = new ArrayList<String>();
		for (Song song : songList) {
			songNameList.add(song.getSongTitle());
		}
		// -----------
		return songNameList;
	}

	public ArrayList<String> getAuthorResult(String searchInput) {
		if (authorList == null) {
			authorList = new ArrayList<Artist>();
		}
		authorList.clear();
		authorList = dataBaseHelper.searchFunctionIII(searchInput);
		ArrayList<String> artistNameList = new ArrayList<String>();
		for (Artist author : authorList) {
			artistNameList.add(author.getName());
		}
		// -----------
		return artistNameList;
	}

	public ArrayList<String> getSingerResult(String searchInput) {
		if (singerList == null) {
			singerList = new ArrayList<Artist>();
		}
		singerList.clear();
		singerList = dataBaseHelper.searchFunctionIV(searchInput);
		ArrayList<String> singerNameList = new ArrayList<String>();
		for (Artist singer : singerList) {
			singerNameList.add(singer.getName());
		}
		// -----------
		return singerNameList;
	}

	public ArrayList<String> getFavoriteResult(String searchInput) {
		if (favoriteList == null) {
			favoriteList = new ArrayList<Song>();
		}
		favoriteList.clear();
		favoriteList = dataBaseHelper.getFavoriteSong(searchInput);
		ArrayList<String> songFavoriteList = new ArrayList<String>();
		for (Song song : favoriteList) {
			songFavoriteList.add(song.getSongTitle());
		}
		return songFavoriteList;
	}

	public void setSongResultListview(ArrayList<String> songsResult) {
		songAdapter = new CustomSongListAdapter(this,
				R.layout.custom_song_lisview, songList, dataBaseHelper);
		songListView.setAdapter(songAdapter);
	}

	public void setAuthorResultListview(ArrayList<String> authorResult) {
		CustomArtistListview adapter = new CustomArtistListview(this,
				R.layout.artist_listview_layout, authorResult, 1);
		authorListView.setAdapter(adapter);
	}

	public void setSingerResultListview(ArrayList<String> singerResult) {
		CustomArtistListview adapter = new CustomArtistListview(this,
				R.layout.artist_listview_layout, singerResult, 2);
		singerListView.setAdapter(adapter);
	}

	public void setFavoriteResultListview(ArrayList<String> favoriteResult) {
		favoriteAdapter = new CustomSongListAdapter(this,
				R.layout.custom_song_lisview, favoriteList, dataBaseHelper);
		favoriteListView.setAdapter(favoriteAdapter);
	}

	// ------- config AutoComplete TextView
	private void setAutoCompleteTextView() {
		// ------ Config AutoCompleteTextView----------
		searchCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_songdetail);
		AutoTextViewAdapter searchAdapter = new AutoTextViewAdapter(this,
				R.layout.autocomplete_textview_layout, new ArrayList<String>(),
				dataBaseHelper);
		searchCompleteTextView.setAdapter(searchAdapter);

		searchCompleteTextView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						hideSoftKeyboard();
						String inputName = (String) parent
								.getItemAtPosition(position);
						String inputID = MainActivity.idList_completeTextview
								.get(position);
						searchCompleteTextView.setText("");
						if (inputName.contains(signTextSong)) {
							Intent intent = new Intent(getApplicationContext(),
									Chord_Detail.class);
							intent.putExtra(MainActivity.EXTRA_MESSAGE, inputID);
							startActivity(intent);
						} else if (inputName.contains(signTextArtist)) {
							Intent intent = new Intent(getApplicationContext(),
									SongOfArtistResultActivity.class);
							intent.putExtra(ARTIST_ID_MESSAGE, inputID);
							intent.putExtra(TYPE, signTextArtist);
							intent.putExtra(ARTIST_NAME_MESSAGE,
									inputName.substring(3));
							startActivity(intent);
						}
					}

				});

		searchCompleteTextView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							searchInputString = searchCompleteTextView
									.getText().toString();
							if (searchInputString.isEmpty()) {
								markSearched.song = true;
								markSearched.singer = true;
								markSearched.author = true;
								markSearched.favorite = true;
								return false;
							}
							markSearched.song = false;
							markSearched.author = false;
							markSearched.singer = false;
							markSearched.favorite = false;
							tab.setCurrentTab(0);
							if (!markSearched.song) {
								markSearched.song = true;
								setSongResultListview(getSongResult(searchInputString));
							}
							searchCompleteTextView.dismissDropDown();
							hideSoftKeyboard();
							return true;
						}
						return false;
					}
				});
	}

	// ----------------- On Click Song
	public void OnClickSong() {
		songListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int searchInput = songList.get(position).getID();
				Intent intent = new Intent(getApplicationContext(),
						Chord_Detail.class);
				intent.putExtra(MainActivity.EXTRA_MESSAGE, "" + searchInput);
				startActivity(intent);
			}
		});
	}

	// ----------------- On Click Author
	public void OnClickAuthor() {
		authorListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String name = (String) parent.getItemAtPosition(position);
				int authorID = authorList.get(position).getID();
				Intent intent = new Intent(getApplicationContext(),
						SongOfArtistResultActivity.class);
				intent.putExtra(ARTIST_ID_MESSAGE, "" + authorID);
				intent.putExtra(TYPE, signTextAuthor);
				intent.putExtra(ARTIST_NAME_MESSAGE, name);
				startActivity(intent);
			}

		});
	}

	// ----------------- On Click Singer
	public void OnClickSinger() {
		singerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String name = (String) parent.getItemAtPosition(position);
				int singerID = singerList.get(position).getID();
				Intent intent = new Intent(getApplicationContext(),
						SongOfArtistResultActivity.class);
				intent.putExtra(ARTIST_ID_MESSAGE, "" + singerID);
				intent.putExtra(TYPE, signTextSinger);
				intent.putExtra(ARTIST_NAME_MESSAGE, name);
				startActivity(intent);
			}

		});
	}

	// ---------- On CLick Favorite
	public void OnClickFavorite() {
		favoriteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int searchInput = favoriteList.get(position).getID();
				Intent intent = new Intent(getApplicationContext(),
						Chord_Detail.class);
				intent.putExtra(MainActivity.EXTRA_MESSAGE, "" + searchInput);
				startActivity(intent);
			}

		});
	}

	// --------------------------------------
	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchCompleteTextView.getWindowToken(), 0);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (flag_changed) {
			setSongResultListview(getSongResult(searchInputString));
			markSearched.song = true;
			setFavoriteResultListview(getFavoriteResult(searchInputString));
			markSearched.favorite = true;
			flag_changed = false;
		}
	}

	class Searched {
		boolean song = false;
		boolean author = false;
		boolean singer = false;
		boolean favorite = false;
	}

}
