package com.summerlab.chords.main;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.summerlab.chords.adapter.HomeListviewAdapter;
import com.summerlab.chords.standout.StandOutWindow;
import com.summerlab.chords.standout.WidgetsWindow;

import com.summerlab.chords.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	public static DataBaseHelper dtBaseHelper;
	private String searchInput = "";
	private AutoCompleteTextView singleComplete;
	private ListView categoryListView;
	private ArrayList<CategoryData> categoryDatas;
	private final String signTextSong = "[S]";
	private final String signTextArtist = "[A]";
	private AutoTextViewAdapter searchAdapter;
	public final static String EXTRA_MESSAGE = "com.app.hopam";
	final static String TAB_SELECT_ID = "tabID.selected";
	private final String ARTIST_ID_MESSAGE = "ARTIST_ID";
	private final String ARTIST_NAME_MESSAGE = "ARTIST_NAME";
	private final String TYPE = "artist_type";
	private SwitchCompat btnSetting;
	public static int statusBarHeight;
	public static int windowHeight = 400;
	public static int windowWidth = 400;
	public static boolean isFullScreen = true;
	public static boolean hasChord = false;
	public static ArrayList<String> idList_completeTextview;
	LinearLayout chord_currentLay;
	TextView songNametxt, singerNametxt;
	private HomeListviewAdapter categoryAdapter;

	static BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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

		getWidthHeight();

		songNametxt = (TextView) findViewById(R.id.home_song_name);
		singerNametxt = (TextView) findViewById(R.id.home_singer_name);
		chord_currentLay = (LinearLayout) findViewById(R.id.current_chord_layout);
		chord_currentLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (hasChord) {
					Intent intent = new Intent(getApplicationContext(),
							Chord_Detail.class);
					intent.putExtra(EXTRA_MESSAGE, "" + Chord_Detail.songID);
					startActivity(intent);
				}
			}
		});

		statusBarHeight = getStatusBarHeight();
		isFullScreen = isFullScreen();

		btnSetting = (SwitchCompat) findViewById(R.id.btn_setting);

		btnSetting
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// The toggle is enabled
							if (hasChord
									&& !isMyServiceRunning(ChatHeadService.class)) {
								StandOutWindow.show(getApplicationContext(),
										WidgetsWindow.class,
										StandOutWindow.DEFAULT_ID);
							}

						} else {
							// The toggle is disabled
							if (hasChord) {
								StandOutWindow.closeAll(
										getApplicationContext(),
										WidgetsWindow.class);

								Intent chatHeadIntent = new Intent(
										MainActivity.this,
										ChatHeadService.class);
								stopService(chatHeadIntent);

								Intent standOutIntent = new Intent(
										MainActivity.this, WidgetsWindow.class);
								stopService(standOutIntent);

							}
						}
					}
				});

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				turnOffToggle();
				Toast.makeText(context, "Floating Chord's turned off !",
						Toast.LENGTH_SHORT).show();
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction("TurnOffStandOutToggle");
		registerReceiver(receiver, filter);

		// -----------------------Initial Data--------------
		try {
			dtBaseHelper = new DataBaseHelper(getApplicationContext());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		idList_completeTextview = new ArrayList<String>();
		setAutoCompleteTextView();
		hideSoftKeyboard();
		showCategoryData();
	}

	private void getWidthHeight() {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display1 = wm.getDefaultDisplay();
		Point size1 = new Point();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

			display1.getSize(size1);
			windowWidth = size1.x;
			windowHeight = size1.y / 2;

		} else {
			windowWidth = display1.getWidth();
			windowHeight = display1.getHeight() / 2;
		}
	}

	protected void turnOffToggle() {
		// TODO Auto-generated method stub
		btnSetting.setChecked(false);
		btnSetting.requestLayout();
	}

	// -------- get Category data -----
	public void showCategoryData() {
		categoryListView = (ListView) findViewById(R.id.categoryListview);
		categoryDatas = dtBaseHelper.getCategoryDatas();
		categoryAdapter = new HomeListviewAdapter(this,
				R.layout.home_listview_layout, categoryDatas);
		categoryListView.setAdapter(categoryAdapter);
		categoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Intent songIntent = new Intent(getApplicationContext(),
							ShowResultActivity.class);
					songIntent.putExtra(EXTRA_MESSAGE, "");
					songIntent.putExtra(TAB_SELECT_ID, "0");
					startActivity(songIntent);
					break;
				case 1:
					Intent authorIntent = new Intent(getApplicationContext(),
							ShowResultActivity.class);
					authorIntent.putExtra(EXTRA_MESSAGE, "");
					authorIntent.putExtra(TAB_SELECT_ID, "1");
					startActivity(authorIntent);
					break;

				case 2:
					Intent singerIntent = new Intent(getApplicationContext(),
							ShowResultActivity.class);
					singerIntent.putExtra(EXTRA_MESSAGE, "");
					singerIntent.putExtra(TAB_SELECT_ID, "2");
					startActivity(singerIntent);
					break;
				case 3:

					Intent lookupChordIntent = new Intent(
							getApplicationContext(), LookUpChordActivity.class);
					lookupChordIntent.putExtra(EXTRA_MESSAGE, "");
					startActivity(lookupChordIntent);
					break;
				case 4:
					Intent favoriteIntent = new Intent(getApplicationContext(),
							ShowResultActivity.class);
					favoriteIntent.putExtra(EXTRA_MESSAGE, "");
					favoriteIntent.putExtra(TAB_SELECT_ID, "3");
					startActivity(favoriteIntent);
					break;

				default:
					break;
				}
			}
		});
	}

	// ------- config AutoComplete TextView
	private void setAutoCompleteTextView() {
		singleComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_home);
		// ------ Config AutoCompleteTextView----------
		searchAdapter = new AutoTextViewAdapter(this,
				R.layout.autocomplete_textview_layout, new ArrayList<String>(),
				dtBaseHelper);
		singleComplete.setAdapter(searchAdapter);

		singleComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				searchInput = (String) parent.getItemAtPosition(position);
				String input = idList_completeTextview.get(position);
				singleComplete.setText("");
				if (searchInput.contains(signTextSong)) {

					Intent intent = new Intent(getApplicationContext(),
							Chord_Detail.class);
					intent.putExtra(EXTRA_MESSAGE, input);
					startActivity(intent);
				} else if (searchInput.contains(signTextArtist)) {
					Intent intent = new Intent(getApplicationContext(),
							SongOfArtistResultActivity.class);
					intent.putExtra(ARTIST_ID_MESSAGE, input);
					intent.putExtra(TYPE, signTextArtist);
					intent.putExtra(ARTIST_NAME_MESSAGE,
							searchInput.substring(3));
					startActivity(intent);
				}

			}

		});

		singleComplete
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							searchInput = singleComplete.getText().toString();
							if (searchInput.isEmpty())
								return false;
							Intent showResultIntent = new Intent(
									getApplicationContext(),
									ShowResultActivity.class);
							showResultIntent.putExtra(EXTRA_MESSAGE,
									searchInput);
							showResultIntent.putExtra(TAB_SELECT_ID, "0");
							startActivity(showResultIntent);
							return true;
						}
						hideSoftKeyboard();
						return false;
					}
				});
	}

	// -------------- Make ActionBar --------
	private void makeActionbar() {
		final ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(
				R.layout.custom_home_actionbar_layout, null);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		final int actionBarColor = getResources().getColor(R.color.deep_blue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
	}

	// --------------------------------------
	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(singleComplete.getWindowToken(), 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (dtBaseHelper != null)
			dtBaseHelper.close();
		super.onDestroy();
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public boolean isFullScreen() {
		int flg = getWindow().getAttributes().flags;
		boolean flag = false;
		if ((flg & 1024) == 1024) {
			flag = true;
		}
		return flag;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (hasChord == false) {
			chord_currentLay.setVisibility(View.GONE);
			btnSetting.setEnabled(false);

		} else {
			btnSetting.setEnabled(true);
			chord_currentLay.setVisibility(View.VISIBLE);
			songNametxt.setText(Chord_Detail.songName);
			singerNametxt.setText(Chord_Detail.singerName);
		}

		if (isMyServiceRunning(WidgetsWindow.class)) {
			btnSetting.setChecked(true);
		} else {
			btnSetting.setChecked(false);
		}
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
