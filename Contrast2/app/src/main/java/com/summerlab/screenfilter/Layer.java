package com.summerlab.screenfilter;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by sev_user on 12/23/2015.
 */

public class Layer extends View {
    private  int a;
    private  int r;
    private  int g;
    private  int b;
    public Layer(Context context) {
        super(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(this.a, this.r, this.g, this.b);

    }
    public  void  setColor(int n,int n2,int n3,int n4)
    {
        this.a= n;
        this.r= n2;
        this.g= n3;
        this.b= n4;
        this.invalidate();
    }
}
