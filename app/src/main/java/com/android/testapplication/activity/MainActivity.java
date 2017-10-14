package com.android.testapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.android.testapplication.R;
import com.android.testapplication.widget.TimeBarView;
import com.android.testapplication.widget.TimeTranslationView;

public class MainActivity extends AppCompatActivity implements TimeBarView.OnTimeChangedListener{

    private TimeTranslationView moveView;
    private TimeBarView timeBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveView = (TimeTranslationView) findViewById(R.id.moveView);
        timeBar = (TimeBarView) findViewById(R.id.timeBar);
        moveView.setOnViewTranslationListener(timeBar);
        timeBar.setOnTimeChangedListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveView.initTranslationY(0);
            }
        },500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int[] location = new int[2];
        moveView.getLocationOnScreen(location);
    }

    @Override
    public void onTimeChanged(String time) {
        moveView.setTimeText(time);
    }
}
