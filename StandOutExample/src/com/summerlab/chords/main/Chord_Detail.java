package com.summerlab.chords.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.summerlab.chords.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.summerlab.chords.adapter.SvgImageAdapter;
import com.summerlab.chords.main.ChordReader.ChrodProperties;

public class Chord_Detail extends ActionBarActivity implements SwipeInterface {

	TextView myTextView;

	ImageButton btnArrow;
	ImageButton btnArrow2;
	private boolean isTableExpanded = false;
	private boolean isTableExpanded2 = false;
	LinearLayout chordControlLayout;
	ScrollView scrollChordText;
	Button btnToneUp, btnToneDown;
	Button btnFontUp, btnFontDown;
	Button btnScrollUp, btnScrollDown;
	ChordReader chordReader;
	float fontSize = 8;
	public static String total;
	int indexScroll;

	List<Integer> lstOpenBracket;
	List<Integer> lstCloseBracket;
	ArrayList<String> chordList;

	public static String songName, singerName, artistName;
	public static int songID;
	String[] chords = { "A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#",
			"G", "G#" };
	String[] chordsForSearch = { "Bb", "1", "C#", "4", "Eb", "6", "F#", "9",
			"G#", "11", "A", "0", "B", "2", "C", "3", "D", "5", "E", "7", "F",
			"8", "G", "10" };
	String myString;
	ArrayList<String> chordForLv, myArrayChord;
	SvgImageAdapter mySvgImageAdapter;
	private DataBaseHelper dataBaseHelper;
	private CountDownTimer timer;
	public static String searchInput = "";
	ImageView imgChord;
	ImageButton btnChordBack, btnChorNext, btnFavourite;
	int chord_index = 0;
	SVG svg;
	Dialog dialog;

	private int speed = 125;
	private final int MIN_TIME_STEP = 25;
	private final int MAX_TIME_STEP = 125;
	private final int STEP_TIME = 25;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chord_detail);

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

		dataBaseHelper = MainActivity.dtBaseHelper;
		// -------------------
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.actionbar_chorddetail, null);

		myArrayChord = new ArrayList<String>();

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		chordForLv = new ArrayList<String>();
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		actionBar.setCustomView(actionBarLayout, params);
		actionBar.setDisplayShowCustomEnabled(true);

		final int actionBarColor = getResources().getColor(R.color.deep_blue);
		actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		Intent intent = getIntent();
		searchInput = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		searching(searchInput);
		TextView songNameTextView = (TextView) findViewById(R.id.song_name);
		songNameTextView.setText(songName);
		TextView singerNameTextView = (TextView) findViewById(R.id.song_singer);
		singerNameTextView.setText(singerName);

		btnFavourite = (ImageButton) findViewById(R.id.favourite);
		//
		setFavouriteButton();

		MainActivity.hasChord = true;
		// myString =
		// "[C]khi anh đưa mắt nhìn em qua tâm [C#7#5]gương\nTa đã gặp [Am]nhau, bối rối thật [F]lâu\nđêm nay [C]dư�?ng như những ánh [F]mắt\n Muốn [G]đi kiếm tìn nhau\n[C]Anh muốn noi với em những đi�?u thật [Em]lớn lao...[Bb]\n[F]Sẽ luôn ở đây, nơi tim anh [Em]tình yêu bất [F]tận";

		// myString
		// ="[Dm]Ve [Dm] [E] [Bb]kêu [F] [C]g�?i [A7]hè sang ; phượng v�? khơi ni�?m nhớ, giây phút chia tay là đây [Dm] [E] [Bb]";

		chordReader = new ChordReader(myString);

		total = "\n\n\n\n\n" + chordReader.getChord();

		Intent standOutIntent = new Intent();
		standOutIntent.setAction("SetLyricForStandout");
		standOutIntent.putExtra("lyric", total);
		standOutIntent.putExtra("songname", songName);
		standOutIntent.putExtra("id", searchInput);
		sendBroadcast(standOutIntent);

		// --------- set font chord
		btnFontUp = (Button) findViewById(R.id.btn_textsizeup);
		btnFontDown = (Button) findViewById(R.id.btn_textsizedown);

		// --------- set scroll chord
		scrollChordText = (ScrollView) findViewById(R.id.scroll_chord_text);
		setAnimScrollView();
		ActivitySwipeDetector swipe = new ActivitySwipeDetector(
				getApplicationContext(), this);
		scrollChordText.setOnTouchListener(swipe);

		// ----------
		myTextView = (TextView) findViewById(R.id.chord_text);

		initializeSpannable();
		getDataForListView();

		myTextView.setOnTouchListener(swipe);

		btnArrow = (ImageButton) findViewById(R.id.arrow_chord);
		chordControlLayout = (LinearLayout) findViewById(R.id.chord_detail_controlchord);

		btnArrow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isTableExpanded) {
					collapseTableOfContent(chordControlLayout);
					btnArrow.setRotation(0);
					isTableExpanded = false;
				} else {
					expandTableOfContent(chordControlLayout);
					btnArrow.setRotation(180);
					isTableExpanded = true;
				}

			}
		});

		dialog = new Dialog(Chord_Detail.this);
		dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialog.setTitle("Cách bấm hợp âm");
		dialog.setContentView(R.layout.dialog_layout);
		dialog.setCancelable(true);

		btnChordBack = (ImageButton) dialog.findViewById(R.id.btn_chord_back);
		btnChorNext = (ImageButton) dialog.findViewById(R.id.btn_chord_next);

		btnChorNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				chord_index++;
				if (chord_index == 10)
					chord_index = 0;

				try {
					svg = SVG.getFromAsset(getAssets(),
							myArrayChord.get(chord_index));
					Picture picture = svg.renderToPicture();
					Drawable drawable = new PictureDrawable(picture);
					imgChord.setImageDrawable(drawable);
				} catch (SVGParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		btnChordBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				chord_index--;
				if (chord_index == -1)
					chord_index = 9;
				SVG svg;
				try {
					svg = SVG.getFromAsset(getAssets(),
							myArrayChord.get(chord_index));
					Picture picture = svg.renderToPicture();
					Drawable drawable = new PictureDrawable(picture);
					imgChord.setImageDrawable(drawable);
				} catch (SVGParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		final LinearLayout layoutlv = (LinearLayout) findViewById(R.id.chord_detail_controlchord_vertical);
		btnArrow2 = (ImageButton) findViewById(R.id.arrow_chord_2);
		HorizontalListView lvhopam = (HorizontalListView) findViewById(R.id.lvhopam);
		chordList = new ArrayList<String>();
		for (int i = 0; i < chordForLv.size(); i++) {
			chordList.add("1/" + chordForLv.get(i) + ".svg");
		}

		mySvgImageAdapter = new SvgImageAdapter(this,
				R.layout.lv_layout_custom, chordList);

		lvhopam.setAdapter(mySvgImageAdapter);
		mySvgImageAdapter.notifyDataSetChanged();

		btnArrow2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isTableExpanded2) {
					v.setRotation(0);
					layoutlv.setVisibility(View.GONE);
					isTableExpanded2 = false;
				} else {
					v.setRotation(180);
					layoutlv.setVisibility(View.VISIBLE);
					isTableExpanded2 = true;
				}

			}

		});

		btnToneUp = (Button) findViewById(R.id.btn_toneup);

		btnToneUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toneUp();

			}

		});
		btnToneDown = (Button) findViewById(R.id.btn_tonedown);
		btnToneDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toneDown();
			}
		});
		btnFontDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fontSize--;
				if (fontSize < 1)
					fontSize = fontSize + 1;
				myTextView.setTextSize(fontSize);
			}
		});
		btnFontUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fontSize++;
				if (fontSize > 30) {
					fontSize = fontSize - 1;
				}
				myTextView.setTextSize(fontSize);
			}
		});

		btnScrollDown = (Button) findViewById(R.id.btn_autoscrolldown);
		speedDownAutoScroll();

		btnScrollUp = (Button) findViewById(R.id.btn_autoscrollup);
		speedUpAutoScroll();

		myTextView.measure(0, 0);
		scrollChordText.measure(0, 0);
		int valueMinus = scrollChordText.getHeight()
				- myTextView.getMeasuredHeight();
		if (valueMinus < 0) {
			TranslateAnimation mAnimation = new TranslateAnimation(0, 0, 0,
					valueMinus);
			mAnimation.setDuration((-valueMinus * 4)); // Set custom
														// duration.
			mAnimation.setStartOffset(500); // Set custom offset.
			mAnimation.setRepeatMode(Animation.RESTART);
			mAnimation.setRepeatCount(Animation.INFINITE);

			// myTextView.startAnimation(mAnimation);
		}

	}

	protected void toneDown() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(total);
		ArrayList<Integer> endBrackets = new ArrayList<Integer>();

		for (int i = 0; i < lstOpenBracket.size(); i++) {
			int islonger = 0;
			String subChord = total.substring(lstOpenBracket.get(i) - 1,
					lstCloseBracket.get(i));

			for (int j = 0; j < chordsForSearch.length; j += 2) {
				if (subChord.contains(chordsForSearch[j])) {
					String chordNew;
					if (Integer.parseInt(chordsForSearch[j + 1]) == 0) {
						chordNew = chords[11];
					} else {
						chordNew = chords[Integer
								.parseInt(chordsForSearch[j + 1]) - 1];
					}

					subChord = subChord.replace(chordsForSearch[j], chordNew);
					if (chordNew.length() > chordsForSearch[j].length()) {
						islonger = 1;
						char c = total.charAt(lstCloseBracket.get(i));
						if (total.charAt(lstCloseBracket.get(i)) == '\n') {

							endBrackets.add(lstCloseBracket.get(i) + 1);

							sb.replace(lstOpenBracket.get(i) - 1,
									lstCloseBracket.get(i) + 1, subChord);
						} else
							sb.replace(lstOpenBracket.get(i) - 1,
									lstCloseBracket.get(i) + 1, subChord);

					} else if (chordNew.length() < chordsForSearch[j].length()) {
						islonger = -1;
						sb.replace(lstOpenBracket.get(i) - 1,
								lstCloseBracket.get(i), subChord + " ");
					} else if (chordNew.length() == chordsForSearch[j].length()) {
						islonger = 0;
						sb.replace(lstOpenBracket.get(i) - 1,
								lstCloseBracket.get(i), subChord);
					}
					break;
				}
			}
		}
		int count = 0;
		for (Integer i : endBrackets) {
			sb.insert(i + count, "\n");
			count++;
		}
		total = sb.toString();
		updatelistHopamtotal();
		initializeSpannable();
		getDataForListView();

		chordList.clear();
		for (int i = 0; i < chordForLv.size(); i++) {
			chordList.add("1/" + chordForLv.get(i) + ".svg");
		}

		mySvgImageAdapter.notifyDataSetChanged();
	}

	protected void toneUp() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(total);
		ArrayList<Integer> endBrackets = new ArrayList<Integer>();

		for (int i = 0; i < lstOpenBracket.size(); i++) {
			int islonger = 0;
			String subChord = total.substring(lstOpenBracket.get(i) - 1,
					lstCloseBracket.get(i));
			for (int j = 0; j < chordsForSearch.length; j += 2) {
				if (subChord.contains(chordsForSearch[j])) {
					String chordNew;
					if (Integer.parseInt(chordsForSearch[j + 1]) == 11) {
						chordNew = chords[0];
					} else {
						chordNew = chords[Integer
								.parseInt(chordsForSearch[j + 1]) + 1];
					}

					subChord = subChord.replace(chordsForSearch[j], chordNew);
					if (chordNew.length() > chordsForSearch[j].length()) {
						islonger = 1;
						char c = total.charAt(lstCloseBracket.get(i));
						if (total.charAt(lstCloseBracket.get(i)) == '\n') {

							endBrackets.add(lstCloseBracket.get(i) + 1);

							sb.replace(lstOpenBracket.get(i) - 1,
									lstCloseBracket.get(i) + 1, subChord);
						} else
							sb.replace(lstOpenBracket.get(i) - 1,
									lstCloseBracket.get(i) + 1, subChord);

					} else if (chordNew.length() < chordsForSearch[j].length()) {
						islonger = -1;
						sb.replace(lstOpenBracket.get(i) - 1,
								lstCloseBracket.get(i), subChord + " ");
					} else if (chordNew.length() == chordsForSearch[j].length()) {
						islonger = 0;
						sb.replace(lstOpenBracket.get(i) - 1,
								lstCloseBracket.get(i), subChord);
					}
					break;
				}
			}
		}
		int count = 0;
		for (Integer i : endBrackets) {
			sb.insert(i + count, "\n");
			count++;
		}
		total = sb.toString();
		updatelistHopamtotal();
		initializeSpannable();
		getDataForListView();
		chordList.clear();
		for (int i = 0; i < chordForLv.size(); i++) {
			chordList.add("1/" + chordForLv.get(i) + ".svg");
		}
		mySvgImageAdapter.notifyDataSetChanged();
	}

	private void getDataForListView() {
		chordForLv = new ArrayList<String>();
		for (int i = 0; i < chordReader.listHopAmTotal.size(); i++) {
			String aString = chordReader.listHopAmTotal.get(i).content;
			aString = aString.replaceFirst("\\[", "");
			aString = aString.replaceFirst("\\]", "");
			if (aString.indexOf("*") != -1)
				aString = aString.substring(0, aString.indexOf("*"));
			chordForLv.add(aString);

		}
		Set<String> hasset = new HashSet<String>();
		hasset.addAll(chordForLv);
		chordForLv.clear();
		chordForLv.addAll(hasset);

	}

	private void updatelistHopamtotal() {
		int indexOpenBracket = total.indexOf("[");
		int indexCloseBracket = total.indexOf("]");
		chordReader.listHopAmTotal.clear();
		while (indexOpenBracket >= 0) {

			ChrodProperties myChrod = new ChrodProperties();
			System.out.println(indexOpenBracket + "---" + indexCloseBracket);

			myChrod.index = indexOpenBracket;
			myChrod.length = indexCloseBracket - indexOpenBracket + 1;
			myChrod.content = total.substring(indexOpenBracket,
					indexCloseBracket + 1);
			chordReader.listHopAmTotal.add(myChrod);
			indexOpenBracket = total.indexOf("[", indexOpenBracket + 1);
			indexCloseBracket = total.indexOf("]", indexCloseBracket + 1);

		}
	}

	private void initializeSpannable() {
		// TODO Auto-generated method stub
		myTextView.setText(total);
		myTextView.setTypeface(Typeface.MONOSPACE);

		chord_index = 0;
		myTextView.setMovementMethod(LinkMovementMethod.getInstance());
		myTextView.setText(total, BufferType.SPANNABLE);

		lstOpenBracket = lstCharBracket(total, "[");
		lstCloseBracket = lstCharBracket(total, "]");

		Spannable mySpannable = (Spannable) myTextView.getText();
		for (int i = 0; i < lstCloseBracket.size(); i++) {

			final CustomSpannable myCustomSpannable = new CustomSpannable(i,
					mySpannable);
			ClickableSpan myClickableSpan = new ClickableSpan() {
				@Override
				public void onClick(View widget) { /* do something */
					// Toast.makeText(
					// getApplicationContext(),
					// myCustomSpannable.position
					// + chordReader.listHopAmTotal
					// .get(myCustomSpannable.position).content,
					// 1).show();
					// there are a lot of settings, for dialog, check them all
					// out!
					imgChord = (ImageView) dialog
							.findViewById(R.id.imageViewChord);
					TextView warning = (TextView) dialog
							.findViewById(R.id.warning);

					imgChord.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
					myArrayChord = new ArrayList<String>();
					String content = chordReader.listHopAmTotal
							.get(myCustomSpannable.position).content;
					content = content.replaceFirst("\\[", "");
					content = content.replaceFirst("\\]", "");
					if (content.indexOf("*") != -1)
						content = content.substring(0, content.indexOf("*"));

					for (int i = 1; i <= 10; i++) {
						myArrayChord.add(i + "/" + content + ".svg");
					}

					try {
						

						svg = SVG
								.getFromAsset(getAssets(), myArrayChord.get(0));

						Picture picture = svg.renderToPicture();
						Drawable drawable = new PictureDrawable(picture);

						imgChord.setImageDrawable(drawable);
						warning.setVisibility(View.GONE);

					} catch (SVGParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						imgChord.setImageDrawable(getResources().getDrawable(
								R.drawable.stand_out));
						warning.setVisibility(View.VISIBLE);
						e.printStackTrace();
					}

					dialog.show();

				}
			};
			mySpannable.setSpan(myClickableSpan, lstOpenBracket.get(i) - 1,
					lstCloseBracket.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private void expandTableOfContent(final View v) {
		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int width = v.getMeasuredWidth();

		final int targetWidth = width;

		v.getLayoutParams().width = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				v.getLayoutParams().width = (int) (targetWidth * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration((int) (targetWidth / v.getContext().getResources()
				.getDisplayMetrics().density) + 500);
		v.startAnimation(a);
	}

	private void collapseTableOfContent(final View v) {
		// TODO Auto-generated method stub
		final int initialWidth = v.getMeasuredWidth();

		Animation a = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				// TODO Auto-generated method stub
				if (interpolatedTime == 1)
					v.setVisibility(View.GONE);
				else {
					v.getLayoutParams().width = initialWidth
							- (int) (initialWidth * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				// TODO Auto-generated method stub
				return true;
			}
		};

		a.setDuration((int) (initialWidth / v.getContext().getResources()
				.getDisplayMetrics().density) + 500);
		v.startAnimation(a);
	}

	private void setFavouriteButton() {
		// TODO Auto-generated method stub

		if (dataBaseHelper.isFavorite(songID)) {
			btnFavourite.setImageResource(R.drawable.heartfull);
		} else {
			btnFavourite.setImageResource(R.drawable.favorite);
		}
		btnFavourite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (songID == 0)
					return;
				if (dataBaseHelper.isFavorite(songID)) {
					dataBaseHelper.deleteFavorite(songID);
					btnFavourite.setImageResource(R.drawable.favorite);
					ShowResultActivity.flag_changed = true;
				} else {
					dataBaseHelper.addFavorite(songID);
					btnFavourite.setImageResource(R.drawable.heartfull);
					ShowResultActivity.flag_changed = true;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.chord__detail, menu);
		// RelativeLayout badgeLayout = (RelativeLayout)
		// menu.findItem(R.id.badge)
		// .getActionView();

		return true;
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public List<Integer> lstCharBracket(String str, String typeBracket) {
		List<Integer> lst = new ArrayList<Integer>();
		String[] arayChar = str.split("");
		for (int i = 0; i < arayChar.length; i++) {
			if (arayChar[i].equals(typeBracket)) {
				lst.add(i);
			}
		}
		return lst;

	}

	public class CustomSpannable {
		public int position;
		public Spannable spannable;

		public CustomSpannable(int _position, Spannable _Spannable) {
			position = _position;
			spannable = _Spannable;
		}
	}

	// ----------Code cua Hieu -----------
	// ---------Searching by song name
	private void searching(String input) {
		ArrayList<MakeRelation> songs_data = dataBaseHelper
				.searchBySongID(input);

		myString = "";
		for (MakeRelation data : songs_data) {
			ArrayList<Song> songs = data.getSongs();
			ArrayList<Artist> artists = data.getArtists();
			ArrayList<Singer> singers = data.getSingers();
			ArrayList<Chord> chords = data.getChords();
			for (Song song : songs) {
				// songTextView.append(song.getSongTitle() + "\n");
				// contentTextView.append(song.getSongContent() + "\n");
				myString = song.getSongContent();
				songName = song.getSongTitle();
				songID = song.getID();

			}
			for (Artist artist : artists) {
				// authorTextView.append(artist.getName() + "\n");
				artistName = artist.getName();
			}
			for (Singer singer : singers) {
				// singerTextView.append(singer.getName() + "\n");
				singerName = singer.getName();
			}
			for (Chord chord : chords) {
				// chordTextView.append(chord.getName() + " - ");
			}
		}
	}

	// -------- set Animation of Content TextView -----------
	public void setAnimScrollView() {
		if (timer != null) {
			timer.cancel();
		}
		if (speed == MAX_TIME_STEP)
			return;
		timer = new CountDownTimer(speed * 1000, speed) {

			public void onTick(long millisUntilFinished) {
				scrollChordText.smoothScrollBy(0, 1);
			}

			public void onFinish() {
				timer.start();
			}

		}.start();
	}

	// ---------- speed up auto scroll
	public void speedUpAutoScroll() {

		btnScrollUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (speed != MIN_TIME_STEP) {
					speed = speed - STEP_TIME;
					setAnimScrollView();
				}
			}
		});
	}

	// ------------- speed up auto scroll
	public void speedDownAutoScroll() {

		btnScrollDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (speed != MAX_TIME_STEP) {
					speed = speed + STEP_TIME;
					setAnimScrollView();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (timer != null) {
			timer.cancel();
		}
		super.onDestroy();
	}

	@Override
	public void onLeftToRight(View v) {
		// TODO Auto-generated method stub
		if (isTableExpanded) {
			collapseTableOfContent(chordControlLayout);
			btnArrow.setRotation(0);
			isTableExpanded = false;
		}
	}

	@Override
	public void onRightToLeft(View v) {
		// TODO Auto-generated method stub
		if (!isTableExpanded) {
			expandTableOfContent(chordControlLayout);
			btnArrow.setRotation(180);
			isTableExpanded = true;
		}
	}
}
