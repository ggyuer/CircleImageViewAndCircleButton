package com.example.administrator.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.myview.DefindView;

public class MainActivity extends AppCompatActivity {

    private DefindView defindView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defindView = (DefindView) findViewById(R.id.defind);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        /** 帧动画调用的最好时机，
         * 特别注意，AnimationDrawable的start()方法不能在Activity的onCreate方法中调运，
         * 因为AnimationDrawable还未完全附着到window上，
         * 所以最好的调运时机是onWindowFocusChanged()方法中。
         */
    }
}
