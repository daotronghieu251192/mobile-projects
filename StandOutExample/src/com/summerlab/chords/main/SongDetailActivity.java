package com.summerlab.chords.main;

import java.util.ArrayList;

import com.summerlab.chords.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class SongDetailActivity extends Activity {
	private TextView songTextView;
	private TextView authorTextView;
	private TextView singerTextView;
	private TextView chordTextView;
	private TextView contentTextView;
	private ScrollView contentScrollView;
	private String searchInput = "";
	private AutoCompleteTextView singleComplete;
	private DataBaseHelper dataBaseHelper;
	private CountDownTimer timer;
	private Button speedUpBtn;
	private Button speedDownBtn;
	private Button stopAutoScrollBtn;
	private int markSize = 0;
	private int speed = 25;
	private final int MIN_SPEED = 1;
	private final int MAX_SPEED = 35;
	private final int STEP_TIME = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_detail_layout);
		makeActionbar();
		// ----------------- Initial component -------------
		songTextView = (TextView) findViewById(R.id.songTextView);
		authorTextView = (TextView) findViewById(R.id.authorTextView);
		singerTextView = (TextView) findViewById(R.id.singerTextView);
		chordTextView = (TextView) findViewById(R.id.chordTextView);
		contentTextView = (TextView) findViewById(R.id.contentTextView);
		contentScrollView = (ScrollView) findViewById(R.id.contentScrollView);
		singleComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_songdetail);
		dataBaseHelper = MainActivity.dtBaseHelper;
		speedUpBtn = (Button) findViewById(R.id.speedUpBtn);
		speedUpAutoScroll();
		speedDownBtn = (Button) findViewById(R.id.speedDownBtn);
		speedDownAutoScroll();
		stopAutoScrollBtn = (Button) findViewById(R.id.stopAutoScrollBtn);
		stopAutoScroll();
		// ----------------------------
		Intent intent = getIntent();
		searchInput = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		searching(searchInput);

		setAutoCompleteTextView();
	}

	// ---------- speed up auto scroll
	public void speedUpAutoScroll() {
		speedUpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (speed != MIN_SPEED) {
					speed = speed - STEP_TIME;
					setAnimScrollView();
				}
			}
		});
	}

	// ------------- speed up auto scroll
	public void speedDownAutoScroll() {
		speedDownBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (speed != MAX_SPEED) {
					speed = speed + STEP_TIME;
					setAnimScrollView();
				}
			}
		});
	}

	// ------------- stop auto scroll
	public void stopAutoScroll() {
		stopAutoScrollBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (timer != null) {
					timer.cancel();
				}
			}
		});
	}

	// ---------Searching by song name
	private void searching(String input) {
		ArrayList<MakeRelation> songs_data = dataBaseHelper
				.searchBySongID(input);
		songTextView.setText("");
		authorTextView.setText("");
		singerTextView.setText("");
		chordTextView.setText("");
		contentTextView.setText("");
		for (MakeRelation data : songs_data) {
			ArrayList<Song> songs = data.getSongs();
			ArrayList<Artist> artists = data.getArtists();
			ArrayList<Singer> singers = data.getSingers();
			ArrayList<Chord> chords = data.getChords();
			for (Song song : songs) {
				songTextView.append(song.getSongTitle() + "\n");
				contentTextView.append(song.getSongContent() + "\n");
			}
			for (Artist artist : artists) {
				authorTextView.append(artist.getName() + "\n");
			}
			for (Singer singer : singers) {
				singerTextView.append(singer.getName() + "\n");
			}
			for (Chord chord : chords) {
				chordTextView.append(chord.getName() + " - ");
			}
		}
		hideSoftKeyboard();
	}

	// -------- set Animation of Content TextView -----------
	public void setAnimScrollView() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new CountDownTimer(speed * 1000, speed) {

			@Override
			public void onTick(long millisUntilFinished) {
				contentScrollView.smoothScrollBy(0, 1);
				/*
				 * if (markSize == contentScrollView.getScrollY()) { markSize =
				 * 0; contentScrollView.scrollTo(0, 0); } else { markSize =
				 * contentScrollView.getScrollY(); }
				 */

			}

			@Override
			public void onFinish() {
				timer.start();
			}

		}.start();
	}

	// -------------- Make ActionBar && config searchButton--------
	public void makeActionbar() {
		final ActionBar mActionBar = getActionBar();
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

	// --------------------------------------
	private void hideSoftKeyboard() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(singleComplete.getWindowToken(), 0);
	}

	// ------- config AutoComplete TextView
	public void setAutoCompleteTextView() {
		
		// ------ Config AutoCompleteTextView----------
		AutoTextViewAdapter searchAdapter = new AutoTextViewAdapter(this,
				R.layout.autocomplete_textview_layout, new ArrayList<String>(),
				dataBaseHelper);
		singleComplete.setAdapter(searchAdapter);
		singleComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				searchInput = (String) parent.getItemAtPosition(position);
				String input = searchInput.substring(3);
				singleComplete.setText("");
				searching(input);
			}

		});
		singleComplete
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							hideSoftKeyboard();
							searchInput = singleComplete.getText().toString();
							if (searchInput.isEmpty())
								return false;
							Intent showResultIntent = new Intent(
									getApplicationContext(),
									ShowResultActivity.class);
							showResultIntent.putExtra(
									MainActivity.EXTRA_MESSAGE, searchInput);
							startActivity(showResultIntent);
							return true;
						}
						return false;
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (timer != null) {
			timer.cancel();
		}
		super.onDestroy();
	}

}
