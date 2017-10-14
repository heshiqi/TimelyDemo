package com.android.testapplication.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.testapplication.util.Utils;

/**
 * Created by he on 17/9/23.
 */

public class TimeTranslationView extends FrameLayout{


    private TextView timeText;
    private int statusBarHeight;

    private float offsetY;

    private OnViewTranslationListener translationListener;

    public TimeTranslationView(@NonNull Context context) {
        this(context, null);
    }

    public TimeTranslationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeTranslationView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        statusBarHeight = Utils.getStatusBarHeight(context);
        timeText=new TextView(context);
        timeText.setTextSize(20);
        timeText.setTextColor(Color.WHITE);
        timeText.setText("00:00");
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        addView(timeText,layoutParams);
    }

    public void setOnViewTranslationListener(OnViewTranslationListener translationListener) {
        this.translationListener = translationListener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float translationY = event.getRawY() - statusBarHeight - offsetY;
            if (translationY < 0) {
                translationY = 0;
            }
            if (translationY > ((ViewGroup) getParent()).getMeasuredHeight() - getHeight()) {
                translationY = ((ViewGroup) getParent()).getMeasuredHeight() - getHeight();
            }
            setTranslationY(translationY);
            if (translationListener != null) {
                translationListener.onViewTranslation((int) event.getRawX(), (int) (getTranslationY() + getHeight() / 2/*取View的垂直中心点*/));
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTextAnimation(event.getX()>getWidth()/2);
            offsetY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            offsetY = 0;
              recoverAnimation();
        }

        return true;
    }

    public void initTranslationY(float translationY){
        setTranslationY(translationY);
        if (translationListener != null) {
            translationListener.onViewTranslation(0, (int)getTranslationY()+ + getHeight() / 2);
        }
    }

    void recoverAnimation(){
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(timeText, "translationX", timeText.getTranslationX(), 0);
        fadeAnim.setDuration(300);
        fadeAnim.start();
    }

    void startTextAnimation(boolean isLeft){
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(timeText, "translationX", 0f, isLeft?-timeText.getWidth()*2:timeText.getWidth()*2);
        fadeAnim.setDuration(300);
        fadeAnim.start();
    }

    public void setTimeText(String time){
        timeText.setText(time);
    }

    public interface OnViewTranslationListener {

        void onViewTranslation(int x, int y);

    }
}
