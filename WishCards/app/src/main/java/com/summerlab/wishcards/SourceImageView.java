package com.summerlab.wishcards;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.renderscript.Type;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.summerlab.wishcards.Adapters.Sticker;
import com.summerlab.wishcards.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class SourceImageView extends ImageView {

    public final static int STICKER_SIZE = 300;
    Context mContext;
    float x = 0f, y = 0f;
    float xText = 0f, yText = 0f;
    String mTextContent = "";
    float mTextSize = 24f;
    int mTextColor = 0;
    String mTextStyle = "";
    Paint paint;
    int dpi = 0;

    ArrayList<Sticker> mStickerList;
    int selectedSticker;
    Rect mBounds = new Rect();

    public SourceImageView(Context context) {
        super(context);
    }

    public SourceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStickerList = new ArrayList<Sticker>();

    }

    public SourceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public SourceImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (paint == null)
            paint = new Paint();

        paint.setTextSize(mTextSize);

        if (mTextColor != 0)
            paint.setColor(mTextColor);

        if (!mTextStyle.equals("")) {
            Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + mTextStyle + ".ttf");
            paint.setTypeface(font);
        }

        if (xText == 0 && yText == 0) {
            xText = getWidth() / 4;
            yText = getHeight() / 4;
        }

        //canvas.drawText(mTextContent, x, y, paint);
        drawMultilineText(mTextContent, xText, yText, paint, canvas);

        if (mStickerList.size() > 0) {
            for (int i = 0; i < mStickerList.size(); i++) {
                try {
                    Sticker sticker = mStickerList.get(i);
                    InputStream ims = mContext.getAssets().open(sticker.getPath());
                    Drawable d = Drawable.createFromStream(ims, null);
                    d.setBounds(sticker.getRect());
                    d.draw(canvas);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // load image as Drawable
        }

    }


    void drawMultilineText(String str, float x, float y, Paint paint, Canvas canvas) {
        int lineHeight = 0;
        int yoffset = 0;
        String[] lines = str.split("\n");

        // set height of each line (height of text + 20%)
        paint.getTextBounds("Ig", 0, 2, mBounds);
        lineHeight = (int) ((float) mBounds.height());
        // draw each line
        for (int i = 0; i < lines.length; ++i) {
            canvas.drawText(lines[i], x - (paint.measureText(lines[i]) / 2), y + yoffset, paint);
            yoffset = yoffset + lineHeight;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_DOWN:
                if (CardWishesActivity.mMode == CardWishesActivity.MODE_STICKER && mStickerList.size() > 0) {
                    selectedSticker = -1;
                    for (int i = mStickerList.size() - 1; i >= 0; i--) {
                        Rect rect = mStickerList.get(i).getRect();
                        if (event.getX() >= rect.left && event.getX() <= rect.right && event.getY() >= rect.top && event.getY() <= rect.bottom) {
                            if (!CardWishesActivity.mEraserMode) {
                                selectedSticker = i;
                            } else {
                                mStickerList.remove(i);
                            }
                            break;
                        }

                    }
                }
                break;

            default:
                break;
        }

        x = event.getX() > 0 ? event.getX() : 0;
        y = event.getY() > 0 ? event.getY() : 0;

        if (CardWishesActivity.mMode == CardWishesActivity.MODE_TEXT) {
            xText = x;
            yText = y;
        } else if (CardWishesActivity.mMode == CardWishesActivity.MODE_STICKER) {
            if (mStickerList.size() > 0 && selectedSticker != -1) {
                Sticker newSticker = mStickerList.get(selectedSticker);
                newSticker.setRect(new Rect((int) x - STICKER_SIZE / 2, (int) y - STICKER_SIZE / 2, (int) x + STICKER_SIZE / 2, (int) y + STICKER_SIZE / 2));
            }
        }

        invalidate();
        return true;
    }

    public void setImageContent(String content) {
        mTextContent = content;
    }

    public void setTextSize(int size) {
        if (dpi == 0) {
            dpi = Utils.getDPI();
        }
        if (dpi != 0) {
            
            mTextSize = (float) ((float) size * (float) ((float) dpi / (float) 160));
        } else {
            
            mTextSize = size;
        }

        
    }

    public void setTextColor(int rgb) {
        this.mTextColor = rgb;
    }

    public void setTextStyle(String font) {
        mTextStyle = font;
    }

    public String saveImageToStorage() {
        setDrawingCacheEnabled(true);

        Bitmap bm = getDrawingCache();
        String filename = saveImageFile(bm);
        setDrawingCacheEnabled(false);
        return filename;
    }

    private String saveImageFile(Bitmap bm) {
        FileOutputStream out = null;
        String fileName = getFileName();

        try {
            out = new FileOutputStream(fileName);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);

            Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private String getFileName() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "WishCards");

        if (!file.exists()) {
            file.mkdir();
        }
        String uriString = "";
        if (CardWishesActivity.mFileName.equals("")) {
            CardWishesActivity.mFileName = System.currentTimeMillis() + "";
        }
        uriString = file.getAbsolutePath() + "/" + CardWishesActivity.mFileName + ".png";

        return uriString;
    }

    public void setSticker(String path) {
        Sticker sticker = new Sticker();
        sticker.setPath(path);
        sticker.setRect(new Rect((int) x, (int) y, (int) x + STICKER_SIZE, (int) y + STICKER_SIZE));
        mStickerList.add(sticker);
        invalidate();
    }

    public void initialize() {
        x = 0f;
        y = 0f;
        xText = 0f;
        yText = 0f;
        mTextContent = "";
        mTextSize = 24f;
        dpi = 0;

        mStickerList = new ArrayList<Sticker>();
        mBounds = new Rect();
    }
}
