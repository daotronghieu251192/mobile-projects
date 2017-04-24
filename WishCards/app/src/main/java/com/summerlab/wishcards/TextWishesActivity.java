
package com.summerlab.wishcards;

import android.database.SQLException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.summerlab.wishcards.Adapters.DataBaseHelper;
import com.summerlab.wishcards.Adapters.MyArrayAdapter;
import com.summerlab.wishcards.Utils.DanhMuc;
import com.summerlab.wishcards.Utils.MD5;

import java.io.IOException;
import java.util.ArrayList;

public class TextWishesActivity extends AppCompatActivity {

    ListView lvLunar;
    static ArrayList<DanhMuc> allDm;
    static ArrayList<String> allDmName;

    static DataBaseHelper myDbHelper;

    private Button mBtnBack;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textwish);
        //getSupportActionBar().hide();

        mAdView = (AdView) findViewById(R.id.ad_view);
        final String deviceId = MD5.md5(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        DataBaseHelper myDbHelper = new DataBaseHelper(this);

        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();
            myDbHelper.getAllTable();
            allDm = myDbHelper.getAllDM();
            
            allDmName = new ArrayList<String>();
            for (int i = 0; i < allDm.size(); i++) {
                
                allDmName.add(allDm.get(i).getName());
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();

        }
        MyArrayAdapter adapterDM = new MyArrayAdapter(this, R.layout.my_item_layout, allDmName);
        lvLunar = (ListView) findViewById(R.id.lvLunar);
        lvLunar.setAdapter(adapterDM);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (allDm != null)
            allDm.clear();
        if (allDmName != null)
            allDmName.clear();
    }
}
