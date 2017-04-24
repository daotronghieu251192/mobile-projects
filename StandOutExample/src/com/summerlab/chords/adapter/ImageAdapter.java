package com.summerlab.chords.adapter;

import java.io.IOException;
import java.util.ArrayList;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class ImageAdapter extends BaseAdapter {

	ArrayList<String> arr;
	Context context;
int size;
	public ImageAdapter(Context c, ArrayList<String> myarr) {
		context = c;
		arr = myarr;
		size = getSmall();
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arr.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView = new ImageView(context);

		imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		try {
			SVG svg = SVG.getFromAsset(context.getAssets(), arr.get(position));
			Picture picture = svg.renderToPicture();
			Drawable drawable = new PictureDrawable(picture);
			imageView.setImageDrawable(drawable);
			imageView.setLayoutParams(new GridView.LayoutParams(size/3, size/3));

		} catch (SVGParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageView;
	}
	public int getSmall()
	{
		WindowManager wm = (WindowManager) context
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

		int smaller = width1 < height1 ? width1 :height1;
		return smaller;
	}

}
