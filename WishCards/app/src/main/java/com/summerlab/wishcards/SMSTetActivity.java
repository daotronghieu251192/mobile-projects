package com.summerlab.wishcards;

import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.summerlab.wishcards.Adapters.DataBaseHelper;
import com.summerlab.wishcards.Adapters.MyArrayAdapterDetail;

import com.google.android.gms.ads.*;
import com.summerlab.wishcards.Utils.MD5;

import java.util.ArrayList;
import java.util.List;

public class SMSTetActivity extends AppCompatActivity {

    ArrayList<String> mContentList;
    SwipeMenuListView mSmsListView;

    Button mBtnBack;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smstet);
        Intent intent = getIntent();
        int value = intent.getIntExtra("idkey", 0);

        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSmsListView = (SwipeMenuListView) findViewById(R.id.lvLunarDetail);

        Toast.makeText(getApplicationContext(), "Bấm hoặc vuốt sang trái lời chúc bạn muốn sao chép.", Toast.LENGTH_LONG).show();

        try {

            mContentList = new ArrayList<String>();

            TextWishesActivity.myDbHelper = new DataBaseHelper(this);
            TextWishesActivity.myDbHelper.createDataBase();
            TextWishesActivity.myDbHelper.openDataBase();
            mContentList = TextWishesActivity.myDbHelper.getContent(value);
            MyArrayAdapterDetail adapterDM = new MyArrayAdapterDetail(this, R.layout.my_detail_layout, mContentList);
            SwipeMenuCreator creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {

                    // create "delete" item
                    SwipeMenuItem shareitem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    shareitem.setBackground(new ColorDrawable(Color.rgb(255,
                            0, 0)));
                    // set item width
                    shareitem.setWidth(dp2px(60));
                    // set icon
                    shareitem.setIcon(R.drawable.share_100);
                    // add to menu
                    menu.addMenuItem(shareitem);

                }
            };
            mSmsListView.setMenuCreator(creator);
            mSmsListView.setAdapter(adapterDM);
            mSmsListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                    switch (index) {
                        case 0:
                            ShareAll(position);
                            break;

                    }
                    return false;
                }
            });
            mSmsListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
            mSmsListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mSmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.txtdetail);

                ClipboardManager _clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                _clipboard.setText(tv.getText().toString());
                Toast.makeText(getApplicationContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();

            }
        });

        //Add Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.inter_ad_unit_id));
        final String deviceId = MD5.md5(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                displayInterstitial();
            }
        });

    }

    public void displayInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public void ShareviaFaceBook() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Meo Meo Subject");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Meo Meo Text");

        PackageManager pm = SMSTetActivity.this.getBaseContext().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("facebook")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                this.getBaseContext().startActivity(shareIntent);
                break;
            }
        }


    }

    public void ShareAll(int position) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, mContentList.get(position));
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getApplicationContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContentList != null)
            mContentList.clear();
    }
}
