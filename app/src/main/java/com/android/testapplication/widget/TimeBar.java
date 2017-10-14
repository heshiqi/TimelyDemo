package com.android.testapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.testapplication.util.Utils;
import com.android.testapplication.entity.TimeEntity;

import java.util.ArrayList;

/**
 * Created by he on 17/9/23.
 */

public class TimeBar extends View {

    private static final int FRAME = 24;  // 100帧

    private ArrayList<PointF> mControlPoints = new ArrayList<>(5);   // 控制点集

    private ArrayList<PointF> mBezierPoints = null; // 曲线点集

    private Paint mLinePaint = null;    // 固定线画笔
    private static final int CONTROL_WIDTH = 5;    // 控制点连线线宽

    private int statusBarHeight = 0;

    public TimeBar(Context context) {
        this(context, null);
    }

    public TimeBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        statusBarHeight = Utils.getStatusBarHeight(context);

        // 固定线画笔
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.LTGRAY);
        mLinePaint.setStrokeWidth(CONTROL_WIDTH);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initControlPoints(getMeasuredHeight()/2);
        initTimes(getMeasuredHeight());
    }


    private int topframe = 6;
    private int bottomframe = 12;

    private ArrayList<TimeEntity> times = new ArrayList<>();
    void initTimes(float height) {
        times.clear();
        int item = (int) ((height - 126) / 24);
        for (int i = 0; i <= 24; i++) {
            TimeEntity time = new TimeEntity(30f, item * i+63);
            Log.d("hh","y="+time.getY());
            time.setHeight(item);
            time.setPosition(i);
            times.add(time);
        }
    }

    private void initControlPoints(float verticalCenter) {
        mControlPoints.clear();
        mControlPoints.add(new PointF(200, 63));
        mControlPoints.add(new PointF(50, verticalCenter / 3 * 2));
        mControlPoints.add(new PointF(50, verticalCenter));
//        mControlPoints.add(new PointF(50, verticalCenter));
//        mControlPoints.add(new PointF(50, getMeasuredHeight()));
        mBezierPoints = buildTopBezierPoints();

        mControlPoints.clear();
        mControlPoints.add(new PointF(50, verticalCenter));

        mControlPoints.add(new PointF(50, getMeasuredHeight() - 63));
        mBezierPoints.addAll(buildBottomBezierPoints());
    }


    public void changePoint(float verticalCenter) {
        for (TimeEntity time:times){
            time.recalculate(verticalCenter);
        }

        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {

//        for (PointF pointF : mBezierPoints) {
//            canvas.drawLine(0, pointF.y, pointF.x, pointF.y, mLinePaint);
//        }

        for (TimeEntity time : times) {
            canvas.drawLine(0, time.getY(), time.getX(), time.getY(), mLinePaint);
        }
    }


    /**
     * 创建Bezier点集
     *
     * @return
     */
    private ArrayList<PointF> buildTopBezierPoints() {
        ArrayList<PointF> points = new ArrayList<>();
        int order = mControlPoints.size() - 1;
        float delta = 1.0f / topframe;
        for (float t = 0; t <= 1; t += delta) {
            // Bezier点集
            points.add(new PointF(deCasteljauX(order, 0, t), deCasteljauY(order, 0, t)));
        }
        return points;
    }

    private ArrayList<PointF> buildBottomBezierPoints() {
        ArrayList<PointF> points = new ArrayList<>();
        int order = mControlPoints.size() - 1;
        float delta = 1.0f / bottomframe;
        for (float t = 0; t <= 1; t += delta) {
            // Bezier点集
            points.add(new PointF(deCasteljauX(order, 0, t), deCasteljauY(order, 0, t)));
        }
        return points;
    }


    /**
     * deCasteljau算法
     *
     * @param i 阶数
     * @param j 点
     * @param t 时间
     * @return
     */
    private float deCasteljauX(int i, int j, float t) {
        float result = 0;
        if (i == 1) {
            result = (1 - t) * mControlPoints.get(j).x + t * mControlPoints.get(j + 1).x;
        } else {
            result = (1 - t) * deCasteljauX(i - 1, j, t) + t * deCasteljauX(i - 1, j + 1, t);
        }
        return result;
    }

    /**
     * deCasteljau算法
     *
     * @param i 阶数
     * @param j 点
     * @param t 时间
     * @return
     */
    private float deCasteljauY(int i, int j, float t) {
        float result = 0;
        if (i == 1) {
            result = (1 - t) * mControlPoints.get(j).y + t * mControlPoints.get(j + 1).y;
        } else {
            result = (1 - t) * deCasteljauY(i - 1, j, t) + t * deCasteljauY(i - 1, j + 1, t);
        }
        return result;
    }


    void reverseY(ArrayList<PointF> points) {
        int first, last;
        int count = points.size();
        for (first = 0, last = count - 1; first <= last; first++, last--) {
            swap(points.get(first), points.get(last));
        }
    }

    void swap(PointF p1, PointF p2) {
        float temp = p1.y;
        p1.y = p2.y;
        p2.y = temp;
    }

}
