package com.summerlab.chords.adapter;

import java.io.IOException;
import java.util.ArrayList;
import com.summerlab.chords.R;
import android.app.Activity;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.caverock.androidsvg.*;

public class SvgImageAdapter extends ArrayAdapter<String> {

	Activity context = null;
	ArrayList<String> myArray = null;
	int layoutId;

	public SvgImageAdapter(Activity context, int layoutId, ArrayList<String> arr) {
		super(context, layoutId, arr);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layoutId = layoutId;
		this.myArray = arr;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(layoutId, null);
		if (myArray.size() > 0 && position >= 0) {
			final ImageView imageView = (ImageView) convertView
					.findViewById(R.id.lv_eachchord);

			imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

			try {
				SVG svg = SVG.getFromAsset(context.getAssets(),
						myArray.get(position));
				Picture picture = svg.renderToPicture();
				Drawable drawable = new PictureDrawable(picture);
				imageView.setImageDrawable(drawable);

			} catch (SVGParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				imageView.setVisibility(View.GONE);
				e.printStackTrace();
			}
		}
		return convertView;
	}

}
