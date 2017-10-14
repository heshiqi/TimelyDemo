package com.android.testapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.android.testapplication.entity.TimeEntity;

import java.util.ArrayList;

/**
 * Created by he on 17/9/23.
 * 时间条
 */

public class TimeBarView extends View implements TimeTranslationView.OnViewTranslationListener {

    private Paint mLinePaint = null;    // 固定线画笔
    private static final int CONTROL_WIDTH = 5;    // 控制点连线线宽

    private Paint mTextPaint = null;    // 文字画笔
    private static final int TEXT_SIZE = 30;    // 文字画笔尺寸

    private int mTextSize = TEXT_SIZE;
    private int mTextHeight;//文本的高度
    private int mTextSpace = 5;//文本与刻度的间距


    public TimeBarView(Context context) {
        this(context, null);
    }

    public TimeBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // 固定线画笔
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(CONTROL_WIDTH);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);

        // 文字画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        setTextHeight();
    }

    void setTextHeight() {
        Rect rect = new Rect();
        mTextPaint.getTextBounds("8", 0, 1, rect);
        mTextHeight = rect.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initTimes(getMeasuredHeight());
        }
    }

    private ArrayList<TimeEntity> times = new ArrayList<>();

    public void initTimes(float height) {
        times.clear();
        int item = (int) ((height - 126) / 24);
        for (int i = 0; i <= 24; i++) {
            TimeEntity time = new TimeEntity(30f, item * i + 63);
            time.setHeight(item);
            time.setPosition(i);
            times.add(time);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        for (TimeEntity time : times) {
            canvas.drawLine(0, time.getY(), time.getX(), time.getY(), mLinePaint);
            if (TextUtils.isEmpty(time.getDrawTimeStr())) {
                continue;
            }

            mTextSize = (int) (TEXT_SIZE * (1 + (1 - time.getRatio())));
            mTextPaint.setTextSize(mTextSize);
//            mTextPaint.setAlpha((int) (255 * (1 - time.getRatio())));
            mTextPaint.setAlpha(time.getAlpha());
            setTextHeight();
            canvas.drawText(time.getPosition() + "", time.getX() + mTextSpace, time.getY() + mTextHeight / 2, mTextPaint);
        }
    }


    @Override
    public void onViewTranslation(int x, int verticalCenter) {
        for (TimeEntity time : times) {
            if (time.isOwnerPosition(verticalCenter)) {
                if (onTimeChangedListener != null) {
                    onTimeChangedListener.onTimeChanged(time.getFormatTime());
                }
            }
            time.recalculate(verticalCenter);

        }
        invalidate();
    }

    private OnTimeChangedListener onTimeChangedListener;

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.onTimeChangedListener = onTimeChangedListener;
    }

    /**
     * 时间轴改变事件监听
     */
    public interface OnTimeChangedListener {
        /**
         * 时间改变回调
         */
        void onTimeChanged(String time);
    }
}
