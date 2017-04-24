package com.summerlab.chords.main;

import com.summerlab.chords.standout.StandOutWindow;
import com.summerlab.chords.standout.WidgetsWindow;

import com.summerlab.chords.R;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnTouchListener;

public class ChatHeadService extends Service {

	private WindowManager windowManager;
	private ImageView chatHead;
	public boolean flagMove;
	int index = 0;
	private final int MAX_CLICK_DISTANCE = 15;
	static BroadcastReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		flagMove = false;
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);

		chatHead.setImageResource(R.drawable.stand_out);
		// LayoutParams chatHeadParam = new LayoutParams(100, 100);
		// chatHead.setLayoutParams(chatHeadParam);
		// chatHead.requestLayout();

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (chatHead != null)
					windowManager.removeView(chatHead);
				stopSelf();
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction("TurnOffStandOutToggle");
		registerReceiver(receiver, filter);

		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display1 = wm.getDefaultDisplay();
		Point size1 = new Point();

		int width1;
		int height1;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

			display1.getSize(size1);
			width1 = size1.x;
			height1 = size1.y;

		} else {
			width1 = display1.getWidth();
			height1 = display1.getHeight();
		}

		int smaller = width1 < height1 ? width1 : height1;

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				smaller / 4, smaller / 4,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		if (StandOutWindow.coor[0] >= 0 && StandOutWindow.coor[1] >= 0) {
			params.x = StandOutWindow.coor[0];
			params.y = StandOutWindow.coor[1];
		}
		windowManager.addView(chatHead, params);
		chatHead.setOnTouchListener(new OnTouchListener() {

			private float x1;
			private float y1;
			private float x2;
			private float y2;
			private float dx;
			private float dy;

			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x1 = event.getRawX();
					y1 = event.getRawY();
					initialX = params.x;
					initialY = params.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					return true;
				case MotionEvent.ACTION_UP:

					x2 = event.getRawX();
					y2 = event.getRawY();
					dx = Math.abs(x2 - x1);
					dy = Math.abs(y2 - y1);
					// windowManager.removeView(chatHead);

					if (dx < MAX_CLICK_DISTANCE && dy < MAX_CLICK_DISTANCE) {
						StandOutWindow.show(getApplication(),
								WidgetsWindow.class, StandOutWindow.DEFAULT_ID);
						stopSelf();
					}

					return true;
				case MotionEvent.ACTION_MOVE:
					flagMove = true;
					params.x = initialX
							+ (int) (event.getRawX() - initialTouchX);
					params.y = initialY
							+ (int) (event.getRawY() - initialTouchY);
					windowManager.updateViewLayout(chatHead, params);
					StandOutWindow.coor[0] = params.x;
					StandOutWindow.coor[1] = params.y;
					return true;
				case MotionEvent.ACTION_OUTSIDE:
					Toast.makeText(getApplicationContext(), "ecec",
							Toast.LENGTH_SHORT).show();
					return true;
				}
				return true;

			}

		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null)
			windowManager.removeView(chatHead);
	}
}
