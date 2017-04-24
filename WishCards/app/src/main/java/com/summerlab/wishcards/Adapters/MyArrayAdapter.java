package com.summerlab.wishcards.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.summerlab.wishcards.R;
import com.summerlab.wishcards.SMSTetActivity;

import java.util.ArrayList;

/**
 * Created by sev_user on 12/28/2015.
 */
public class MyArrayAdapter extends ArrayAdapter<String> {

    Activity context= null;
    ArrayList<String> myArray= null;
    int layoutID;


    public MyArrayAdapter(Activity context, int layoutID, ArrayList<String> arr) {
        super(context, layoutID,arr);
        this.context = context;
        this.layoutID = layoutID;
        this.myArray= arr;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= context.getLayoutInflater();
        convertView= inflater.inflate(layoutID,null);
        if(myArray.size()>0 && position >=0)
        {
            final TextView tv=(TextView) convertView.findViewById(R.id.txtitem);
            tv.setText(myArray.get(position));

        }
        convertView.findViewById(R.id.btnitem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i= new Intent(context,SMSTetActivity.class);
                i.putExtra("idkey",(position+1));
                context.startActivity(i);
            }
        });
      //  convertView.setElevation((14-position)*5);
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        //FrameLayout.LayoutParams params= new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height /8);
        //params.setMargins(10,0,10,10);
        convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,height/8));

       return  convertView;
    }
}
