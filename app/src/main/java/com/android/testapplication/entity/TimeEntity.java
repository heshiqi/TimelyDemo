package com.android.testapplication.entity;

/**
 * Created by he on 17/9/23.
 */

public class TimeEntity implements Cloneable {

    //默认长度
    public static final float DEFAULT_LENGTH = 30f;

    //在指定距离内  动态设置时间刻度值  最大约定为其他刻度的1.5倍
    public static final float MAX_LENGTH = DEFAULT_LENGTH * 1.5f;

    //当前距离与指定距离的比率 指定距离约定为240个像素  可以动态指定 这里我们先写死
    public static final float DEFAULT_OFFSET_Y_DISTANCE = 260f;

    /**
     * 时间刻度的长度
     */
    private float X;
    /**
     * 时间刻度的Y坐标
     */
    private float Y;
    /**
     * 时间刻度的高度
     */
    private int height;
    /**
     * 当前的时间坐标位置
     */
    private int position;

    /**
     * 当前时间轴的显示比例值，根据此值来控制刻度值得显示样式
     */
    private float ratio = 1;

    /**
     * 绘制的时间文本
     */
    private String drawTimeStr = "";

    private int alpha=255;

    /**
     * 指定以当前时间刻度条为基准 上下计算的范围 距离值
     */
    private float offsetYDistance = DEFAULT_OFFSET_Y_DISTANCE;

    public TimeEntity(float x, float y) {
        this.X = x;
        this.Y = y;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        drawTimeStr = (position == 0 || position == 6 || position == 12 || position == 18 || position == 24) ? position + "" : "";
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public float getOffsetYDistance() {
        return offsetYDistance;
    }

    public String getDrawTimeStr() {
        return drawTimeStr;
    }

    public void setOffsetYDistance(float offsetYDistance) {
        this.offsetYDistance = offsetYDistance;
    }

    /**
     * 获取格式化后的时间
     *
     * @return
     */
    public String getFormatTime() {
        StringBuilder sb = new StringBuilder();
        if (position < 10) {
            sb.append("0").append(position).append(":").append("00");
        }else if(position==24){
            sb.append("00").append(":").append("00");
        } else {
            sb.append(position).append(":").append("00");
        }
        return sb.toString();
    }

    public int getAlpha() {
        return alpha;
    }

    /**
     * 重新计算时间刻度样式
     *
     * @param y
     * @return
     */
    public void recalculate(float y) {
        //计算指定点上下的临近指定便宜距离
        int absdistance = (int) Math.abs((this.Y - y));
        ratio = (absdistance / offsetYDistance);
        if (ratio >= 1) {//超过了指定距离  统一设置时间刻度长度为30像素
            ratio = 1;
            drawTimeStr = (position == 0 || position == 6 || position == 12 || position == 18 || position == 24) ? position + "" : "";
            X = DEFAULT_LENGTH;
        } else {
            this.X = DEFAULT_LENGTH + MAX_LENGTH * (1 - ratio);
            drawTimeStr = position + "";

            if((position == 0 || position == 6 || position == 12 || position == 18 || position == 24)&&alpha==255){
                alpha=255;
            }else{
                alpha= (int) (255 * (1 - ratio));
            }
        }
    }

    public boolean isOwnerPosition(float translationY){

        return translationY > this.Y - height / 2 && translationY < this.Y + height / 2;
    }


    @Override
    protected TimeEntity clone() throws CloneNotSupportedException {
        TimeEntity time = new TimeEntity(this.X, this.Y);
        time.setPosition(position);
        time.setHeight(height);
        time.setRatio(ratio);
        time.setOffsetYDistance(offsetYDistance);
        return time;
    }
}
