package com.summerlab.screenfilter;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_OVERLAY = 1;
    private final static int REQUEST_CODE_WRITE_SETTING = 2;

    ImageView brown_img, green_img, reddeep_img, custom_img;
    SeekBar redsb, greensb, bluesb, alphasb, contrastsb, brightsb;
    TextView redtv, bluetv, greentv, alphtv, contrasttv, brighttv;
    SwitchCompat activatebtn;
    TextView brightautoBtn, bright25Btn, bright50Btn, bright100Btn;
    TextView contrast0Btn, contrast50Btn, contrast100Btn;
    private ContentResolver contentResolver;
    Window window;
    private SharedPreferences pref;
    private int a, r, g, b, c, count2;
    public static final String PREF_NAME = "pref";

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {

            checkDrawOverlayPermission();

            checkWriteSettingPermission();
        }
        //Add Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.inter_ad_id));
        final String deviceId = MD5.md5(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        brown_img = (ImageView) findViewById(R.id.color1_img);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / 8, width / 8);
        brown_img.setLayoutParams(layoutParams);
        brown_img.requestLayout();

        green_img = (ImageView) findViewById(R.id.color2_img);
        green_img.setLayoutParams(layoutParams);
        green_img.requestLayout();

        reddeep_img = (ImageView) findViewById(R.id.color3_img);
        reddeep_img.setLayoutParams(layoutParams);
        reddeep_img.requestLayout();

        custom_img = (ImageView) findViewById(R.id.color4_img);
        custom_img.setLayoutParams(layoutParams);
        custom_img.requestLayout();

        redsb = (SeekBar) findViewById(R.id.redseekbar);
        redsb.getThumb().setColorFilter(Color.rgb(221, 44, 0), PorterDuff.Mode.SRC_IN);
        redsb.getProgressDrawable().setColorFilter(Color.rgb(221, 44, 0), PorterDuff.Mode.SRC_IN);

        greensb = (SeekBar) findViewById(R.id.greenseekbar);
        greensb.getThumb().setColorFilter(Color.rgb(76, 175, 80), PorterDuff.Mode.SRC_IN);
        greensb.getProgressDrawable().setColorFilter(Color.rgb(76, 175, 80), PorterDuff.Mode.SRC_IN);

        bluesb = (SeekBar) findViewById(R.id.blueseekbar);
        bluesb.getThumb().setColorFilter(Color.rgb(3, 169, 244), PorterDuff.Mode.SRC_IN);
        bluesb.getProgressDrawable().setColorFilter(Color.rgb(3, 169, 244), PorterDuff.Mode.SRC_IN);


        alphasb = (SeekBar) findViewById(R.id.alphaseekbar);
        alphasb.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        alphasb.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);


        brightsb = (SeekBar) findViewById(R.id.brightseekbar);

        contrastsb = (SeekBar) findViewById(R.id.contrastsb);
        activatebtn = (SwitchCompat) findViewById(R.id.switchactivate);

        contentResolver = getContentResolver();

        redtv = (TextView) findViewById(R.id.redtxt);
        greentv = (TextView) findViewById(R.id.greentxt);
        bluetv = (TextView) findViewById(R.id.bluetxt);
        brighttv = (TextView) findViewById(R.id.brighttxt);
        contrasttv = (TextView) findViewById(R.id.contrasttxt);
        alphtv = (TextView) findViewById(R.id.alphatxt);

        this.pref = this.getSharedPreferences("pref", 0);
        this.a = this.pref.getInt("a", 0);
        this.r = this.pref.getInt("r", 0);
        this.g = this.pref.getInt("g", 0);
        this.b = this.pref.getInt("b", 0);
        this.c = this.pref.getInt("c", 0);

        redsb.setProgress(this.r);
        greensb.setProgress(this.g);
        bluesb.setProgress(this.b);
        alphasb.setProgress(255 - this.a);
        contrastsb.setProgress(this.c);
        contrasttv.setText(this.c + "%");
        Listener myListener = new Listener((MainActivity) this);
        redsb.setOnSeekBarChangeListener(myListener);
        bluesb.setOnSeekBarChangeListener(myListener);
        greensb.setOnSeekBarChangeListener(myListener);
        alphasb.setOnSeekBarChangeListener(myListener);
        contrastsb.setOnSeekBarChangeListener(myListener);
        window = getWindow();

        brightautoBtn = (TextView) findViewById(R.id.brightauto);
        bright25Btn = (TextView) findViewById(R.id.bright25);
        bright50Btn = (TextView) findViewById(R.id.bright50);
        bright100Btn = (TextView) findViewById(R.id.bright100);

        contrast0Btn = (TextView) findViewById(R.id.contrast0);
        contrast50Btn = (TextView) findViewById(R.id.contrast50);
        contrast100Btn = (TextView) findViewById(R.id.contrast100);

        ButtonListeners buttonsListeners = new ButtonListeners();
        brightautoBtn.setOnClickListener((View.OnClickListener) buttonsListeners);
        bright25Btn.setOnClickListener((View.OnClickListener) buttonsListeners);
        bright50Btn.setOnClickListener((View.OnClickListener) buttonsListeners);
        bright100Btn.setOnClickListener((View.OnClickListener) buttonsListeners);
        contrast0Btn.setOnClickListener((View.OnClickListener) buttonsListeners);
        contrast50Btn.setOnClickListener((View.OnClickListener) buttonsListeners);
        contrast100Btn.setOnClickListener((View.OnClickListener) buttonsListeners);

        brightsb = (SeekBar) (findViewById(R.id.brightseekbar));
        brightsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, i);
                WindowManager.LayoutParams layoutpars = window.getAttributes();
                //Set the brightness of this window
                if (i < 25) {
                    i = 25;
                    brightsb.setProgress(25);
                }
                layoutpars.screenBrightness = i / (float) 255;

                brighttv.setText(((i * 100 / 255)) + "%");
                //Apply attribute changes to this window
                window.setAttributes(layoutpars);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (SAservice.state) {
            activatebtn.setChecked(true);
        }
        activatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Switched", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this.getBaseContext(), (Class) SAservice.class);
                if (activatebtn.isChecked()) {
                    MainActivity.this.startService(intent);
                } else {
                    MainActivity.this.sendBroadcast(new Intent("com.netmanslab.sa.STOPSTART2"));
                }
                MainActivity.this.save();
            }
        });
        // custom_img.setBackgroundColor(Color.rgb(this.r, this.g, this.b));
        brown_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.access_3(MainActivity.this, 40);
                MainActivity.access_0(MainActivity.this, 89);
                MainActivity.access_1(MainActivity.this, 62);
                MainActivity.access_2(MainActivity.this, 26);
                MainActivity.this.save();
                SAservice.setAlpha(MainActivity.access_5(MainActivity.this));
                SAservice.setRGB(MainActivity.access_6(MainActivity.this), MainActivity.access_7(MainActivity.this), MainActivity.access_8(MainActivity.this));
                SAservice.setContrast(MainActivity.access_9(MainActivity.this));
                MainActivity.access_10(MainActivity.this);

                custom_img.setBackgroundColor(Color.rgb(pref.getInt("r", 0), pref.getInt("g", 0), pref.getInt("b", 0)));
                greensb.setProgress(pref.getInt("g", 0));
                bluesb.setProgress(pref.getInt("b", 0));
                redsb.setProgress(pref.getInt("r", 0));
                alphasb.setProgress(255 - 40);
            }
        });
        green_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom_img.setBackgroundColor(Color.rgb(44, 100, 0));
                MainActivity.access_3(MainActivity.this, 40);
                MainActivity.access_0(MainActivity.this, 44);
                MainActivity.access_1(MainActivity.this, 100);
                MainActivity.access_2(MainActivity.this, 0);
                MainActivity.this.save();
                SAservice.setAlpha(MainActivity.access_5(MainActivity.this));
                SAservice.setRGB(MainActivity.access_6(MainActivity.this), MainActivity.access_7(MainActivity.this), MainActivity.access_8(MainActivity.this));
                SAservice.setContrast(MainActivity.access_9(MainActivity.this));
                MainActivity.access_10(MainActivity.this);


                greensb.setProgress(100);
                bluesb.setProgress(0);
                redsb.setProgress(44);
                alphasb.setProgress(255 - 40);
            }
        });
        reddeep_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.access_3(MainActivity.this, 40);
                MainActivity.access_0(MainActivity.this, 100);
                MainActivity.access_1(MainActivity.this, 0);
                MainActivity.access_2(MainActivity.this, 51);
                MainActivity.this.save();
                SAservice.setAlpha(MainActivity.access_5(MainActivity.this));
                SAservice.setRGB(MainActivity.access_6(MainActivity.this), MainActivity.access_7(MainActivity.this), MainActivity.access_8(MainActivity.this));
                SAservice.setContrast(MainActivity.access_9(MainActivity.this));
                MainActivity.access_10(MainActivity.this);
                custom_img.setBackgroundColor(Color.rgb(100, 0, 51));

                greensb.setProgress(0);
                bluesb.setProgress(51);
                redsb.setProgress(100);
                alphasb.setProgress(255 - 40);
            }
        });
    }

    private void save() {
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putInt("a", this.a);
        editor.putInt("r", this.r);
        editor.putInt("g", this.g);
        editor.putInt("b", this.b);
        editor.putInt("c", this.c);
        editor.commit();

    }

    static void access_0(MainActivity mainActivity, int n) {

        mainActivity.r = n;
    }

    static void access_1(MainActivity mainActivity, int n) {

        mainActivity.g = n;
    }

    static void access_2(MainActivity mainActivity, int n) {

        mainActivity.b = n;
    }

    static void access_3(MainActivity mainActivity, int n) {
        mainActivity.a = n;
    }

    static void access_4(MainActivity mainActivity, int n) {

        mainActivity.c = n;
    }

    static int access_5(MainActivity mainActivity) {

        return mainActivity.a;
    }

    static int access_6(MainActivity mainActivity) {

        return mainActivity.r;
    }

    static int access_7(MainActivity mainActivity) {

        return mainActivity.g;
    }

    static int access_8(MainActivity mainActivity) {

        return mainActivity.b;
    }

    static int access_9(MainActivity mainActivity) {

        return mainActivity.c;
    }

    static void access_10(MainActivity mainActivity) {

        mainActivity.setText();
    }

    private void setText() {
        this.redtv.setText((CharSequence) ("Red " + this.r));
        this.greentv.setText((CharSequence) ("Green " + this.g));
        this.bluetv.setText((CharSequence) ("Blue " + this.b));
        this.alphtv.setText((CharSequence) ("Alpha " + (100 - (int) (100.0f * ((float) this.a / 255.0f))) + "%"));
        this.contrasttv.setText(this.c + "%");
    }

    private class Listener implements SeekBar.OnSeekBarChangeListener {
        MainActivity mainActivity = null;

        private Listener(MainActivity mainActivity1) {
            this.mainActivity = mainActivity1;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            switch (seekBar.getId()) {
                case R.id.redseekbar: {
                    MainActivity.access_0(MainActivity.this, i);
                    break;
                }
                case R.id.greenseekbar: {
                    MainActivity.access_1(MainActivity.this, i);
                    break;
                }
                case R.id.blueseekbar: {
                    MainActivity.access_2(MainActivity.this, i);
                    break;
                }
                case R.id.alphaseekbar: {
                    MainActivity.access_3(MainActivity.this, 255 - i);
                    if (i < 127) {
                        alphasb.setProgress(127);
                        MainActivity.access_3(MainActivity.this, 128);
                    }
                    break;
                }
                case R.id.contrastsb: {
                    MainActivity.access_4(MainActivity.this, i);
                    break;
                }
            }
            MainActivity.this.save();
            SAservice.setAlpha(MainActivity.access_5(MainActivity.this));
            custom_img.setBackgroundColor(Color.rgb(MainActivity.this.r, MainActivity.this.g, MainActivity.this.b));
            SAservice.setRGB(MainActivity.access_6(MainActivity.this), MainActivity.access_7(MainActivity.this), MainActivity.access_8(MainActivity.this));
            SAservice.setContrast(MainActivity.access_9(MainActivity.this));
            MainActivity.access_10(MainActivity.this);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }


    }

    private class ButtonListeners implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.brightauto) {
                try {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    int brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                    WindowManager.LayoutParams layoutpars = window.getAttributes();
                    //Set the brightness of this window
                    layoutpars.screenBrightness = brightness / (float) 255;
                    //Apply attribute changes to this window
                    window.setAttributes(layoutpars);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (view.getId() == R.id.bright25) {

                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
                WindowManager.LayoutParams layoutpars = window.getAttributes();
                //Set the brightness of this window
                layoutpars.screenBrightness = 25 / (float) 255;
                //Apply attribute changes to this window
                window.setAttributes(layoutpars);

            } else if (view.getId() == R.id.bright50) {

                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 127);
                WindowManager.LayoutParams layoutpars = window.getAttributes();
                //Set the brightness of this window
                layoutpars.screenBrightness = 0.5f;
                //Apply attribute changes to this window
                window.setAttributes(layoutpars);

            } else if (view.getId() == R.id.bright100) {

                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
                WindowManager.LayoutParams layoutpars = window.getAttributes();
                //Set the brightness of this window
                layoutpars.screenBrightness = 1f;
                //Apply attribute changes to this window
                window.setAttributes(layoutpars);

            } else if (view.getId() == R.id.contrast0) {

                MainActivity.access_4(MainActivity.this, 0);
                contrastsb.setProgress(0);

            } else if (view.getId() == R.id.contrast50) {

                MainActivity.access_4(MainActivity.this, 50);
                contrastsb.setProgress(50);

            } else if (view.getId() == R.id.contrast100) {

                MainActivity.access_4(MainActivity.this, 100);
                contrastsb.setProgress(100);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_OVERLAY);
        }
    }

    public void checkWriteSettingPermission() {
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_WRITE_SETTING);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_OVERLAY) {
            if (!Settings.canDrawOverlays(this)) {
                // continue here - permission was not granted
                Toast.makeText(this, "You have to grant permission to use this application. Thank you !", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if (requestCode == REQUEST_CODE_WRITE_SETTING) {
            if (!Settings.System.canWrite(this)) {
                // continue here - permission was not granted
                Toast.makeText(this, "You have to grant permission to use this application. Thank you !", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
