package com.summerlab.wishcards;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.summerlab.wishcards.Adapters.ImageAdapter;
import com.summerlab.wishcards.Utils.MD5;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class CollectionActivity extends AppCompatActivity {

    Context mContext;
    GridView mCollectionGridView;
    Button mBtnBack;

    ArrayList<String> mCollectionPathList;
    ImageAdapter mCollectionAdapter;
    Dialog mItemDialog;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        mContext = this.getApplicationContext();

        mAdView = (AdView) findViewById(R.id.ad_view);
        final String deviceId = MD5.md5(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mCollectionGridView = (GridView) findViewById(R.id.gridview_collection);
        mBtnBack = (Button) findViewById(R.id.btn_back);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mItemDialog = new Dialog(CollectionActivity.this);
        mItemDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mItemDialog.setContentView(R.layout.dialog_collection_item);

        boolean checkExist = getCollections();

        if (checkExist) {
            mCollectionAdapter = new ImageAdapter(mContext, R.layout.list_item_image, mCollectionPathList, ImageAdapter.IMAGE_STORAGE_TYPE);
            mCollectionGridView.setAdapter(mCollectionAdapter);

            mCollectionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                    final Dialog itemDialog = new Dialog(CollectionActivity.this);
                    itemDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    itemDialog.setContentView(R.layout.dialog_collection_item);

                    ImageView image = (ImageView) itemDialog.findViewById(R.id.item_img);
                    Button deleteButton = (Button) itemDialog.findViewById(R.id.btn_item_delete);
                    Button shareButton = (Button) itemDialog.findViewById(R.id.btn_item_share);

                    Uri uri = Uri.fromFile(new File(mCollectionPathList.get(position)));

                    Picasso.with(mContext).load(uri).into(image);

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            File file = new File(mCollectionPathList.get(position));
                            file.delete();
                            mCollectionPathList.remove(position);
                            itemDialog.dismiss();
                            mCollectionAdapter.notifyDataSetChanged();
                            itemDialog.dismiss();
                        }
                    });

                    shareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/png");
                            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mCollectionPathList.get(position))));
                            startActivity(Intent.createChooser(share, "Share Image via "));
                        }
                    });

                    itemDialog.show();
                }
            });
        }

    }

    private boolean getCollections() {

        mCollectionPathList = new ArrayList<String>();
        File collectionFolder = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath()
                + "/WishCards/");

        if (collectionFolder.exists()) {
            for (String s : collectionFolder.list()) {
                mCollectionPathList.add(0, Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/WishCards/" + s);
            }
            return true;
        }

        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCollectionPathList != null)
            mCollectionPathList.clear();
    }
}
