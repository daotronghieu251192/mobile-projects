package com.summerlab.wishcards;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.squareup.picasso.Picasso;
import com.summerlab.wishcards.Adapters.ImageAdapter;
import com.summerlab.wishcards.Utils.DisplayAnimation;
import com.summerlab.wishcards.Utils.MD5;
import com.summerlab.wishcards.Utils.Utils;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class CardWishesActivity extends AppCompatActivity {

    private Context mContext;
    public static final int MODE_TEXT = 1;
    public static final int MODE_STICKER = 2;
    public static int mMode;
    public static String mFileName = "";

    public static boolean mEraserMode;

    private DisplayAnimation displayAnimation;

    private SourceImageView mSourceView;
    private EditText mEditTextWish;

    private Spinner spnFontStyle;
    private Spinner spnFontSize;
    private Spinner spnStickerCat;

    private ImageButton mColorPickerView, mBtnTheme, mBtnSwitchMode, mBtnEraserMode;
    private GridView mBackgroundGridview;
    private Spinner spnBackgroundCat;

    private Button mBtnBack, mBtnShare, mBtnSave, mBtnNew;

    private ArrayAdapter<String> mFontStyleAdapter;
    private ArrayAdapter<Integer> mFontSizeAdapter;
    private ArrayAdapter<String> mBackgroundCatAdapter;

    private List<Integer> mFontSizeList;


    private TwoWayView mStickerListView;
    private ArrayList<String> mImagePathList;
    private ArrayList<String> mStickerPathList;
    private List<String> mCatList;

    private ImageAdapter mBackgroundAdapter;

    int mR, mG, mB, mRGB;
    int width, height;

    Dialog mEditDialog;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_card_wishes);

        mContext = CardWishesActivity.this;

        mMode = MODE_TEXT;
        mEraserMode = false;
        mFileName = "";

        displayAnimation = new DisplayAnimation();

        final Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

//        Transition enterTrans = new Explode();
//        getWindow().setEnterTransition(enterTrans);

        mSourceView = (SourceImageView) findViewById(R.id.img_background);
        mEditTextWish = (EditText) findViewById(R.id.edit_text_wish);

        spnFontStyle = (Spinner) findViewById(R.id.select_font_style);
        spnFontSize = (Spinner) findViewById(R.id.select_font_size);
        spnStickerCat = (Spinner) findViewById(R.id.select_sticker_cat);

        mColorPickerView = (ImageButton) findViewById(R.id.color_picker);
        mBtnTheme = (ImageButton) findViewById(R.id.btn_theme);
        mBtnSwitchMode = (ImageButton) findViewById(R.id.btn_switch_mode);
        mBtnEraserMode = (ImageButton) findViewById(R.id.btn_eraser_mode);

        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnShare = (Button) findViewById(R.id.btn_share);
        mBtnSave = (Button) findViewById(R.id.btn_save);
        mBtnNew = (Button) findViewById(R.id.btn_new);

        mStickerListView = (TwoWayView) findViewById(R.id.list_sticker);

        //SET ERASER
        mBtnEraserMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEraserMode) {
                    mEraserMode = false;
                    mBtnEraserMode.setImageResource(R.drawable.eraser_icon_disable);
                } else {
                    mEraserMode = true;
                    mBtnEraserMode.setImageResource(R.drawable.eraser_icon_enable);
                }
            }
        });
        mBtnEraserMode.setVisibility(View.INVISIBLE);

        //SET TEXT WISH
        mEditDialog = new Dialog(mContext, R.style.full_screen_dialog);
        mEditDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mEditDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                    return true;
                }
                return false;
            }
        });


        mEditDialog.setContentView(R.layout.dialog_edit_text_wish);
        mEditDialog.setCanceledOnTouchOutside(true);

        final EditText editText = (EditText) mEditDialog.findViewById(R.id.edit_text_dialog);


        editText.setText(mEditTextWish.getText().toString());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mEditTextWish.setText(editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditTextWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEditDialog.show();
                editText.requestFocus();
                mEditDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


            }
        });

        mEditTextWish.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSourceView.setImageContent(charSequence.toString());
                mSourceView.invalidate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //GET BACKGROUND LIST
        mBtnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCatList = new ArrayList<String>();

                mImagePathList = new ArrayList<String>();

                try {
                    String[] catArr = Utils.getFileNamesFromAssets(mContext, "background");
                    if (catArr.length > 0) {
                        mCatList = Arrays.asList(catArr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //mBackgroundAdapter = new ImageAdapter(mContext, R.layout.list_item_image, mImageList);
                mBackgroundAdapter = new ImageAdapter(mContext, R.layout.list_item_image, mImagePathList, ImageAdapter.IMAGE_ASSET_TYPE);

                try {
                    String[] fileList;
                    fileList = Utils.getFileNamesFromAssets(mContext, "background/" + mCatList.get(0));
                    if (fileList.length > 0) {
                        for (String name : fileList) {
                            InputStream ims = mContext.getAssets().open("background/" + mCatList.get(0) + "/" + name);
                            // load image as Drawable
                            mImagePathList.add("background/" + mCatList.get(0) + "/" + name);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                final Dialog themeDialog = new Dialog(mContext);
                themeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                themeDialog.setContentView(R.layout.dialog_background_list);

                mBackgroundGridview = (GridView) themeDialog.findViewById(R.id.gridview_background);
                spnBackgroundCat = (Spinner) themeDialog.findViewById(R.id.select_background_cat);

                mBackgroundCatAdapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item, mCatList);
                spnBackgroundCat.setAdapter(mBackgroundCatAdapter);
                spnBackgroundCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String category = spnBackgroundCat.getSelectedItem().toString();
                        mImagePathList.clear();
                        try {
                            String[] imageArr = Utils.getFileNamesFromAssets(mContext, "background/" + category);

                            if (imageArr.length > 0) {
                                for (String name : imageArr) {
                                    InputStream ims = mContext.getAssets().open("background/" + category + "/" + name);
                                    // load image as Drawable
                                    Drawable d = Drawable.createFromStream(ims, null);
                                    mImagePathList.add("background/" + category + "/" + name);
                                }

                                mBackgroundAdapter.notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                mBackgroundGridview.setAdapter(mBackgroundAdapter);
                mBackgroundGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        
                        String selectedItem = mBackgroundAdapter.getItem(i);
                        Picasso.with(mContext).load("file:///android_asset/" + selectedItem).into(mSourceView);
                        themeDialog.dismiss();
                    }
                });
                themeDialog.show();

            }
        });

        //GET FONT STYLE LIST
        try {
            String[] fontArray = Utils.getFileNamesFromAssets(this.getApplicationContext(), "fonts");
            CharSequence s1 = ".ttf";
            CharSequence s2 = "";

            for (int i = 0; i < fontArray.length; i++) {
                fontArray[i] = fontArray[i].replace(s1, s2);
            }

            List<String> fontStyleList = Arrays.asList(fontArray);

            mFontStyleAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, fontStyleList);
            spnFontStyle.setAdapter(mFontStyleAdapter);

            spnFontStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mSourceView.setTextStyle(spnFontStyle.getSelectedItem().toString());
                    mSourceView.invalidate();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        //GET FONT SIZE LIST
        Integer[] fontSizeArray = {18, 20, 26, 28, 36, 48, 72};
        mFontSizeList = Arrays.asList(fontSizeArray);
        mFontSizeAdapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, mFontSizeList);
        spnFontSize.setAdapter(mFontSizeAdapter);
        mSourceView.setTextSize(mFontSizeList.get(spnFontSize.getSelectedItemPosition()));
        spnFontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSourceView.setTextSize(mFontSizeList.get(i));
                mSourceView.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //GET COLOR FROM COLORPICKER

        mRGB = Color.BLACK;
        mColorPickerView.setBackgroundColor(mRGB);
        mSourceView.setTextColor(mRGB);
        mColorPickerView.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));
        mColorPickerView.setOnClickListener(new View.OnClickListener() {
            private com.larswerkman.holocolorpicker.ColorPicker picker;
            private Button button;
            private SVBar svBar;

            @Override
            public void onClick(View view) {
                final Dialog colorPickerDialog = new Dialog(mContext);
                colorPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                colorPickerDialog.setContentView(R.layout.activity_color_pallete);
                picker = (com.larswerkman.holocolorpicker.ColorPicker) colorPickerDialog.findViewById(R.id.picker);
                picker.setOldCenterColor(((ColorDrawable) mColorPickerView.getBackground()).getColor());
                svBar = (SVBar) colorPickerDialog.findViewById(R.id.svbar);
                button = (Button) colorPickerDialog.findViewById(R.id.btn_confirm);

                picker.addSVBar(svBar);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRGB = picker.getColor();
                        mColorPickerView.setBackgroundColor(mRGB);
                        mSourceView.setTextColor(mRGB);
                        mSourceView.invalidate();
                        colorPickerDialog.dismiss();
                    }
                });

                colorPickerDialog.show();
            }
        });


        //SET FUNCTIONAL BUTTONS
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(mContext);
                alerDialogBuilder.setMessage(R.string.confirm_save);
                alerDialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSourceView.saveImageToStorage();
                        finish();
                    }
                });

                alerDialogBuilder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                alerDialogBuilder.create().show();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (mFileName.equals(""))
                    mSourceView.saveImageToStorage();
                else {
                    AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(mContext);
                    alerDialogBuilder.setMessage(getResources().getString(R.string.confirm_merge1) + mFileName + getResources().getString(R.string.confirm_merge2));
                    alerDialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mSourceView.saveImageToStorage();
                        }
                    });

                    alerDialogBuilder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    alerDialogBuilder.create().show();
                }


            }
        });

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = mSourceView.saveImageToStorage();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fileName)));
                startActivity(Intent.createChooser(share, "Share Image via "));
            }
        });

        mBtnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView();
                mFileName = "";
                mEditTextWish.setText("");
                Toast.makeText(mContext, "Tạo mới...", Toast.LENGTH_SHORT).show();
            }
        });


        //SET SWITCH BUTTONS
        mBtnSwitchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mStickerPathList == null || mStickerPathList.size() == 0) {
                    initStickers();
                }

                LinearLayout stickerLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.sticker_spinner_layout);
                LinearLayout stickerContentLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.sticker_content_layout);

                LinearLayout textLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.text_spinner_layout);
                LinearLayout textContentLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.text_content_layout);
                switch (mMode) {
                    case MODE_TEXT:
                        mMode = MODE_STICKER;
                        mBtnSwitchMode.setImageResource(R.drawable.sticker_icon);
                        textLayout.setVisibility(View.GONE);
                        textContentLayout.setVisibility(View.GONE);
                        stickerLayout.setVisibility(View.VISIBLE);
                        stickerContentLayout.setVisibility(View.VISIBLE);
                        mBtnEraserMode.setVisibility(View.VISIBLE);
                        break;

                    case MODE_STICKER:
                        mMode = MODE_TEXT;
                        mBtnSwitchMode.setImageResource(R.drawable.text_icon);
                        stickerLayout.setVisibility(View.GONE);
                        stickerContentLayout.setVisibility(View.GONE);
                        textLayout.setVisibility(View.VISIBLE);
                        textContentLayout.setVisibility(View.VISIBLE);
                        mBtnEraserMode.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        //INIT DATA
        initView();

        //Add Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.inter_ad_unit_id));
        final String deviceId = MD5.md5(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
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

    private void initStickers() {
        //GET STICKER CAT LIST
        try {
            String[] stickerCatArr = Utils.getFileNamesFromAssets(mContext, "stickers");

            ArrayAdapter<String> stickerCatAdapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item, Arrays.asList(stickerCatArr));
            spnStickerCat.setAdapter(stickerCatAdapter);

            final String defaultStickerCat = spnStickerCat.getSelectedItem().toString();

            final String[] fileList = Utils.getFileNamesFromAssets(mContext, "stickers/" + defaultStickerCat);
            mStickerPathList = new ArrayList<String>();
            if (fileList.length > 0) {
                for (String name : fileList) {
                    mStickerPathList.add("stickers/" + defaultStickerCat + "/" + name);
                }
            }

            final ImageAdapter stickerAdapter = new ImageAdapter(mContext, R.layout.list_item_image, mStickerPathList, ImageAdapter.IMAGE_ASSET_TYPE);
            mStickerListView.setAdapter(stickerAdapter);

            spnStickerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String stickerCat = spnStickerCat.getSelectedItem().toString();
                    try {
                        String[] list = Utils.getFileNamesFromAssets(mContext, "stickers/" + stickerCat);

                        mStickerPathList.clear();
                        if (list.length > 0) {
                            for (String name : list) {
                                mStickerPathList.add("stickers/" + stickerCat + "/" + name);
                            }

                            stickerAdapter.notifyDataSetChanged();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        mStickerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mSourceView.setSticker(mStickerPathList.get(position));
            }
        });
    }

    private void initView() {
        mSourceView.setTextStyle(spnFontStyle.getSelectedItem().toString());
        mSourceView.setTextSize(mFontSizeList.get(0));
        mSourceView.initialize();
        try {
//            InputStream ims = mContext.getAssets().open("default.PNG");
//            // load image as Drawable
//            Drawable d = Drawable.createFromStream(ims, null);
//            mSourceView.setImageDrawable(d);
            Picasso.with(mContext).load("file:///android_asset/default.PNG").into(mSourceView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSourceView.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        
        if (mEditDialog != null && mEditDialog.isShowing()) {
            
            mEditDialog.dismiss();
        }

        super.onBackPressed();
    }
}
