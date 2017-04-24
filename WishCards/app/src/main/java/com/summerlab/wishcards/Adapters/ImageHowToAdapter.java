package com.summerlab.wishcards.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.summerlab.wishcards.R;

public class ImageHowToAdapter extends PagerAdapter {
    Context context;
    private int[] GalImages = new int[]{
            R.drawable.intro1,
            R.drawable.intro2, R.drawable.intro3, R.drawable.intro4, R.drawable.intro5
    };

    public ImageHowToAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return GalImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.menu_margin);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        imageView.setImageResource(GalImages[position]);
        Picasso.with(context).load(GalImages[position]).into(imageView);
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
