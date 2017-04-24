package com.summerlab.chords.main;

import java.io.IOException;
import java.util.ArrayList;

import com.summerlab.chords.R;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.summerlab.chords.adapter.ImageAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LookUpChordActivity extends ActionBarActivity {
	TextView tvC, tvD, tvEb, tvF, tvG, tvA, tvBb, tvSelected;
	String[] fileList;
	ArrayList<String> selectedChord;
	GridView myGridView;
	ImageView mainSVG;
	ImageAdapter adapter;
	ImageView imgnext, imgback;
	String nameCurrentChord;
	int currIndex = 1;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		makeActionbar();

		setContentView(R.layout.activity_look_up_chord);
		tvC = (TextView) findViewById(R.id.tvC);
		tvD = (TextView) findViewById(R.id.tvD);
		tvEb = (TextView) findViewById(R.id.tvEb);
		tvF = (TextView) findViewById(R.id.tvF);
		tvG = (TextView) findViewById(R.id.tvG);
		tvA = (TextView) findViewById(R.id.tvA);
		tvBb = (TextView) findViewById(R.id.tvBb);

		tvC.setOnClickListener(getChordListener("C"));
		tvD.setOnClickListener(getChordListener("D"));
		tvEb.setOnClickListener(getChordListener("Eb"));
		tvF.setOnClickListener(getChordListener("F"));
		tvG.setOnClickListener(getChordListener("G"));
		tvA.setOnClickListener(getChordListener("A"));
		tvBb.setOnClickListener(getChordListener("B"));

		imgnext = (ImageView) findViewById(R.id.chord_next);
		imgback = (ImageView) findViewById(R.id.chord_back);

		imgnext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (currIndex < 10)
					currIndex++;
				else {
					currIndex = 1;
				}
				try {
					SVG svg = SVG.getFromAsset(getAssets(), currIndex + "/"
							+ nameCurrentChord);
					Picture picture = svg.renderToPicture();
					Drawable drawable = new PictureDrawable(picture);
					mainSVG.setImageDrawable(drawable);
				} catch (SVGParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		imgback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (currIndex > 1)
					currIndex--;
				else {
					currIndex = 10;
				}
				try {
					SVG svg = SVG.getFromAsset(getAssets(), currIndex + "/"
							+ nameCurrentChord);
					Picture picture = svg.renderToPicture();
					Drawable drawable = new PictureDrawable(picture);
					mainSVG.setImageDrawable(drawable);
				} catch (SVGParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		selectedChord = new ArrayList<String>();
		String path = "1";
		try {
			fileList = getAssets().list(path);
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].startsWith("C")) {
					selectedChord.add("1/" + fileList[i]);
				}
			}
			// TODO: add file name to an array list
		} catch (Exception e) {
			// TODO: handle exception
		}

		myGridView = (GridView) findViewById(R.id.grid_view);
		adapter = new ImageAdapter(getApplicationContext(), selectedChord);
		myGridView.setAdapter(adapter);

		mainSVG = (ImageView) findViewById(R.id.main_svg);

		mainSVG.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		nameCurrentChord = "C.svg";
		tvSelected = tvC;
		tvSelected.setTextColor(getResources().getColor(R.color.orange));

		try {
			SVG svg = SVG.getFromAsset(getAssets(), "1/" + nameCurrentChord);
			Picture picture = svg.renderToPicture();
			Drawable drawable = new PictureDrawable(picture);
			mainSVG.setImageDrawable(drawable);
		} catch (SVGParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				nameCurrentChord = selectedChord.get(position);
				nameCurrentChord = nameCurrentChord.split("/")[1];
				currIndex = 1;
				try {
					SVG svg = SVG.getFromAsset(getAssets(),
							selectedChord.get(position));
					Picture picture = svg.renderToPicture();
					Drawable drawable = new PictureDrawable(picture);
					mainSVG.setImageDrawable(drawable);
				} catch (SVGParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private OnClickListener getChordListener(final String chord) {
		// TODO Auto-generated method stub
		View.OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedChord.clear();
				tvSelected.setTextColor(Color.BLACK);
				((TextView) v).setTextColor(mContext.getResources().getColor(
						R.color.orange));
				tvSelected = (TextView) v;

				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].startsWith(chord)) {
						selectedChord.add("1/" + fileList[i]);
					}
				}
				nameCurrentChord = selectedChord.get(0).split("/")[1];
				currIndex = 1;
				adapter.notifyDataSetChanged();

				String s = ((TextView) v).getText().toString();
				try {
					SVG svg = SVG.getFromAsset(getAssets(), "1/" + s + ".svg");
					Picture picture = svg.renderToPicture();
					Drawable drawable = new PictureDrawable(picture);
					mainSVG.setImageDrawable(drawable);
				} catch (SVGParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		return listener;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.look_up_chord, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	public void makeActionbar() {
		final ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.chords_actionbar, null);
		ImageButton backBtn = (ImageButton) mCustomView
				.findViewById(R.id.backbtn_Actionbar);
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
