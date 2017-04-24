package com.summerlab.wishcards;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.summerlab.wishcards.Adapters.ImageHowToAdapter;
import com.summerlab.wishcards.Utils.MD5;

public class MainActivity extends AppCompatActivity {

    private final static int MY_WRITE_EXTERNAL_STORAGE = 1;

    private LinearLayout layTextWishes, layCardWishes, layCollection, layHowto;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu_dashboard);

        String version = getAndroidVersion();

        

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE
                    );
                }
            }
        }

        //Add Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.inter_ad_unit_id));
        final String deviceId = MD5.md5(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        layTextWishes = (LinearLayout) findViewById(R.id.btn_text_wish);
        layCardWishes = (LinearLayout) findViewById(R.id.btn_card_wish);
        layCollection = (LinearLayout) findViewById(R.id.btn_collection);
        layHowto = (LinearLayout) findViewById(R.id.btn_howto);

        layTextWishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TextWishesActivity.class);
                startActivity(intent);
            }
        });

        layCardWishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CardWishesActivity.class);
                startActivity(intent);
            }
        });

        layCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                startActivity(intent);
            }
        });

        layHowto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog howtoDialog = new Dialog(MainActivity.this, R.style.CustomDialog);
                howtoDialog.setContentView(R.layout.dialog_how_to);
                ViewPager viewPager = (ViewPager) howtoDialog.findViewById(R.id.view_pager);
                ImageHowToAdapter adapter = new ImageHowToAdapter(MainActivity.this);
                viewPager.setAdapter(adapter);

                Button btnBack = (Button) howtoDialog.findViewById(R.id.btn_back);
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        howtoDialog.dismiss();
                    }
                });

                displayInterstitial();

                howtoDialog.show();

                Toast.makeText(MainActivity.this,"Vuốt sang phải để xem hướng dẫn.", Toast.LENGTH_LONG).show();

            }
        });


    }

    public void displayInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    
                } else {

                   
                    //finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
