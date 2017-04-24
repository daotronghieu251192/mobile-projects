package com.summerlab.chords.standout;

import com.summerlab.chords.main.Chord_Detail;
import com.summerlab.chords.main.MainActivity;
import com.summerlab.chords.standout.ui.Window;

import com.summerlab.chords.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class WidgetsWindow extends MultiWindow {
	public static final int DATA_CHANGED_TEXT = 0;
	static TextView status, songTitle;
	static String lyric;
	static String songname;
	static BroadcastReceiver receiver;
	private CountDownTimer timer;
	ImageView btnScrollUp, btnScrollDown, btnClose;
	private int speed = 125;
	private final int MIN_TIME_STEP = 25;
	private final int MAX_TIME_STEP = 125;
	private final int STEP_TIME = 25;
	ScrollView scrollChordText;

	@Override
	public void createAndAttachView(final int id, FrameLayout frame) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.widgets, frame, true);
		View parent = (View) frame.getParent();

		status = (TextView) view.findViewById(R.id.status);
		status.setText(Chord_Detail.total);
		songTitle = (TextView) parent.findViewById(R.id.title);
		songTitle.setText(Chord_Detail.songName);

		scrollChordText = (ScrollView) view
				.findViewById(R.id.scroll_chord_text_standout);
		setAnimScrollView();

		btnScrollDown = (ImageView) parent
				.findViewById(R.id.standout_scrolldown);
		btnScrollUp = (ImageView) parent.findViewById(R.id.standout_scrollup);

		speedDownAutoScroll();

		speedUpAutoScroll();

		btnClose = (ImageView) parent.findViewById(R.id.standout_close);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent standOutIntent = new Intent();
				standOutIntent.setAction("TurnOffStandOutToggle");
				sendBroadcast(standOutIntent);

				closeAll();
				stopSelf();
			}
		});

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Bundle extras = intent.getExtras();
				if (extras != null) {
					if (extras.containsKey("lyric")) {
						lyric = extras.get("lyric").toString();
						status.setText(lyric);
					}
					if (extras.containsKey("songname")) {
						songname = extras.get("songname").toString();
						songTitle.setText(songname);
					}
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction("SetLyricForStandout");
		registerReceiver(receiver, filter);

	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, MainActivity.windowWidth,
				MainActivity.windowHeight, StandOutWindow.coor[0],
				StandOutWindow.coor[1]);
	}

	@Override
	public String getAppName() {
		return getResources().getString(R.string.app_name);
	}

	@Override
	public int getThemeStyle() {
		return android.R.style.Theme_Light;
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

		btnScrollDown.setOnClickListener(new View.OnClickListener() {

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

		btnScrollUp.setOnClickListener(new View.OnClickListener() {

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

}