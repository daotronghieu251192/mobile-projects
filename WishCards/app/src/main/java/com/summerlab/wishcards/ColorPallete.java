package com.summerlab.wishcards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class ColorPallete extends AppCompatActivity implements ColorPicker.OnColorChangedListener {

    private ColorPicker picker;
    private OpacityBar opacityBar;
    private Button button;
    private SVBar svBar;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_pallete);

        picker =(ColorPicker) findViewById(R.id.picker);
        svBar =(SVBar) findViewById(R.id.svbar);
//        opacityBar =(OpacityBar) findViewById(R.id.opacitybar);
//        button = (Button) findViewById(R.id.btn1);
//        textView =(TextView) findViewById(R.id.tv1);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView.setTextColor(picker.getColor());
                picker.setOldCenterColor(picker.getColor());
            }
        });

    }





    @Override
    public void onColorChanged(int i) {

    }
}
