package com.summerlab.wishcards.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.summerlab.wishcards.R;

import java.util.ArrayList;

/**
 * Created by sev_user on 12/28/2015.
 */
public class MyArrayAdapterDetail extends ArrayAdapter<String> {
    Activity context = null;
    ArrayList<String> myArray = null;
    int layoutID;


    public MyArrayAdapterDetail(Activity context, int layoutID, ArrayList<String> arr) {
        super(context, layoutID, arr);
        this.context = context;
        this.layoutID = layoutID;
        this.myArray = arr;

    }

    private class Holder {
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutID, null);
            holder = new Holder();
            holder.textView = (TextView) v.findViewById(R.id.txtdetail);
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        
        holder.textView.setText( myArray.get(position));
       

        return v;
    }

    @Override
    public int getCount() {
        return myArray.size();
    }

    @Override
    public String getItem(int position) {
        return myArray.get(position);
    }

}
