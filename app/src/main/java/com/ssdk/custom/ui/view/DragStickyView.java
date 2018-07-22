package com.ssdk.custom.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.ssdk.custom.R;
import com.ssdk.custom.utils.GeometryUtil;


/**
 * Created by zlc on 2018/6/29.
 * 仿QQ小红点
 */

public class DragStickyView extends View{


    private static final String TAG = "DragStickyView";
    private Paint mPaint;
    private int mViewWidth;
    private int mViewHeight;
    //固定的圆圆心坐标和半径
    private PointF mFixedPoint;
    private float mFixedRadius;
    //拖拽的圆圆心坐标和半径
    private PointF mDragPoint;
    private float mDragRadius;
    //拖拽范围最大圆的圆心坐标和半径 最大拖拽范围
    private float mMaxDragRange;
    private float mDragMaxRadius;
    private PointF mDragMaxPoint;
    //控制点的坐标
    private PointF mControlPoint;
    //固定圆和拖拽圆切点坐标
    private PointF[] mFixedTangentPoint;
    private PointF[] mDragTangentPoint;
    //触摸拖拽移动范围大小
    private float mDragMoveRange;
    //是否超出了范围
    private boolean mIsOutRange = false;
    //是否在超出范围外松手
    private boolean mIsOutUp = false;
    //动画中固定圆的最小半径
    private float mMinFixedRadius;

    private Path mPath;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public DragStickyView(Context context) {
        this(context,null);
    }

    public DragStickyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dip2px(2));
        mPaint.setAntiAlias(true);

        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //wrap_content
        int width = dip2px(250);
        int height =  dip2px(300);
        width = widthMode == MeasureSpec.AT_MOST ? width : widthSize;
        height = heightMode == MeasureSpec.AT_MOST ? height : heightSize;
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewWidth = w;
        this.mViewHeight = h;

        //初始化各个坐标点
        float mCenterX = mViewWidth / 2;//dip2px(180);
        float mCenterY = mViewHeight / 2;//dip2px(260);
        mFixedPoint = new PointF(mCenterX,mCenterY);
        mDragPoint = new PointF(mCenterX,mCenterY);
        mDragMaxPoint = new PointF(mCenterX,mCenterY);
        mControlPoint = new PointF();
        mFixedTangentPoint = new PointF[2];
        mDragTangentPoint = new PointF[2];

        //初始化各个半径
        mFixedRadius = dip2px(12);
        mDragRadius = dip2px(14);
        mMinFixedRadius = dip2px(8);
        mDragMaxRadius = dip2px(80);
        mMaxDragRange = mDragMaxRadius;

        //初始化 WindowManager和mParams  用于添加范围外爆炸图片 做动画处理
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        mPaint.setStyle(Paint.Style.FILL);
        //拖拽出了范围后将不再绘制连接部分和固定圆
        if( !mIsOutRange) {
            //画固定圆
            float mFixedRadius = updateFixedCircleRadius();
            canvas.drawCircle(mFixedPoint.x, mFixedPoint.y, mFixedRadius, mPaint);
            drawBezier(canvas);
        }
        drawDragCircle(canvas);
        drawRangeCircle(canvas);
        canvas.restore();
    }

    //画贝塞尔曲线
    private void drawBezier(Canvas canvas){

        //计算拖拽过程中固定圆的半径
        float mFixedRadius = updateFixedCircleRadius();
        //控制点设置为两圆心点连线的中心坐标
        mControlPoint.set((mFixedPoint.x + mDragPoint.x) / 2,(mFixedPoint.y + mDragPoint.y) / 2);
        //计算斜率 和 切点坐标
        float dy = mDragPoint.y - mFixedPoint.y;
        float dx = mDragPoint.x - mFixedPoint.x;
        //k1 * k2 = -1 斜率k = (y0 - y1)/(x0 - x1)
        if(dx == 0){
            mFixedTangentPoint = GeometryUtil.getIntersectionPoints(mFixedPoint,mFixedRadius,0f);
            mDragTangentPoint = GeometryUtil.getIntersectionPoints(mDragPoint, mDragRadius,0f);
        }else{
            float k1 = dy / dx;
            float k2 = -1 / k1;
            mFixedTangentPoint = GeometryUtil.getIntersectionPoints(mFixedPoint,mFixedRadius,k2);
            mDragTangentPoint = GeometryUtil.getIntersectionPoints(mDragPoint, mDragRadius,k2);
        }

        //需要重置 否则线会重叠
        mPath.reset();
        //移动起点到固定圆的外切点
        mPath.moveTo(mFixedTangentPoint[0].x,mFixedTangentPoint[0].y);
        //绘制二阶贝塞尔曲线 需要一个控制点 和终点（拖拽圆的一个切点）
        mPath.quadTo(mControlPoint.x,mControlPoint.y, mDragTangentPoint[0].x, mDragTangentPoint[0].y);
        //连接拖拽圆的另一个外切点
        mPath.lineTo(mDragTangentPoint[1].x, mDragTangentPoint[1].y);
        //再次绘制二阶贝塞尔曲线 需要一个控制点 和终点（固定圆的另外一个切点）
        mPath.quadTo(mControlPoint.x,mControlPoint.y,mFixedTangentPoint[1].x,mFixedTangentPoint[1].y);
        //闭合曲线
        mPath.close();
        //绘制path
        canvas.drawPath(mPath,mPaint);
    }

    //画拖拽圆
    private void drawDragCircle(Canvas canvas){
        //当在范围外松手的时候不再绘制拖拽圆
        if( !mIsOutUp) {
            canvas.drawCircle(mDragPoint.x, mDragPoint.y, mDragRadius, mPaint);
        }
    }

    //画范围控制圆
    private void drawRangeCircle(Canvas canvas) {
        mPaint.setStrokeWidth(dip2px(1));
        mPaint.setStyle(Paint.Style.STROKE);
        //画范围控制圆
        canvas.drawCircle(mDragMaxPoint.x, mDragMaxPoint.y, mDragMaxRadius,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mIsOutRange = false;
                float startX = event.getX();
                float startY = event.getY();
                updateDragCenterXY(startX,startY);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                mDragMoveRange = drawDistance();
                //超出了拖拽的最大范围
                if(mDragMoveRange > mMaxDragRange){
                    mIsOutRange = true;
                }else{
                    //mIsOutRange=false  因为移出一次后就算它移出过了
                    mIsOutUp = false;
                }
                updateDragCenterXY(moveX,moveY);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                //防止误操作
                mDragMoveRange = drawDistance();
                touchUp(event.getX(),event.getY());
                break;
        }
        return true;
    }

    /**
     * 手指松手事件处理
     */
    private void touchUp(float touchX,float touchY){
        //拖拽已经超出了最大范围
        if(mIsOutRange){
            //手指抬起/松手时是否停留在最大范围内
            //松手在范围外
            if(mDragMoveRange > mMaxDragRange){
                mIsOutUp = true;
                outRangePlayAnimation(touchX,touchY);
                invalidate();
            }else{
                mIsOutUp = false;
                //松手在范围内 因为要回到原位置 所以拖拽圆的圆心坐标用固定圆的圆心坐标代替
                updateDragCenterXY(mFixedPoint.x,mFixedPoint.y);
            }
        }else{
            //拖拽一直在范围内
            resilienceAnimation();
        }
    }

    /**
     * 更新拖拽圆的圆心坐标
     * @param x
     * @param y
     */
    private void updateDragCenterXY(float x, float y){
        mDragPoint.set(x,y);
        invalidate();
    }

    //拖拽的距离大小
    private float drawDistance(){
        return GeometryUtil.getDistanceBetween2Points(mFixedPoint, mDragPoint);
    }

    /**
     * 计算拖拽过程中固定圆的半径
     */
    private float updateFixedCircleRadius(){
        float distance = (float) Math.sqrt(Math.pow(mFixedPoint.x - mDragPoint.x,2) +
               Math.pow(mFixedPoint.y - mDragPoint.y,2));
        distance = Math.min(distance,mMaxDragRange);
        float percent = distance * 1.0f / mMaxDragRange;
        return mFixedRadius + (mMinFixedRadius - mFixedRadius) * percent;
    }

    /**
     *  移动的时候一直在范围内，最后在范围内做松手回弹动画处理
     */
    private void resilienceAnimation() {

        final PointF startPoint = new PointF(mDragPoint.x,
                mDragPoint.y);
        final PointF endPoint = new PointF(mFixedPoint.x,
                mFixedPoint.y);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f);
        animator.setInterpolator(new OvershootInterpolator(4.0f));
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                PointF byPercent = GeometryUtil.getPointByPercent(
                        startPoint, endPoint, fraction);
                updateDragCenterXY(byPercent.x, byPercent.y);
            }
        });
        animator.start();
    }

    /**
     * 超出范围 松手动画处理
     */
    private void outRangePlayAnimation(float touchX,float touchY){

//        final  ImageView imageView = new ImageView(getContext());
//        imageView.setImageResource(R.drawable.out_anim);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        final AnimationDrawable animDrawable = (AnimationDrawable) imageView.getDrawable();
//        // 这里得到的是其真实的大小，因为此时还得不到其测量值
//        final int imgWidth = imageView.getDrawable().getIntrinsicWidth();
//        final int imgHeight = imageView.getDrawable().getIntrinsicHeight();
        final ImageView imageView = (ImageView) mLayoutInflater.inflate(R.layout.drag_img, null, false);
        imageView.setImageResource(R.drawable.out_anim);
        final AnimationDrawable animDrawable = (AnimationDrawable) imageView.getDrawable();
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x= (int) mDragPoint.x - imageView.getWidth() / 2;
        mParams.y= (int) mDragPoint.y - imageView.getHeight() / 2;


        if(imageView.getParent() == null) {
            mWindowManager.addView(imageView, mParams);
        }else{
            Log.e(TAG,"已经有小姐姐了，你还要几个！");
        }
        if(!animDrawable.isRunning()) {
            animDrawable.start();
        }else{
            animDrawable.stop();
            animDrawable.start();
        }

        long duration = getAnimationDuration(animDrawable);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                animDrawable.stop();
                imageView.clearAnimation();
                mWindowManager.removeViewImmediate(imageView);
                //爆炸动画之后复位 为了多次演示 实际项目中下面两行代码需要注释掉
                mIsOutUp = false;
                updateDragCenterXY(mFixedPoint.x,mFixedPoint.y);
            }
        },duration);
    }

    /**
     * 得到帧动画的执行时间
     */
    private long getAnimationDuration(AnimationDrawable animationDrawable) {
        long duration = 0;
        for(int i=0; i < animationDrawable.getNumberOfFrames(); i++){
            duration += animationDrawable.getDuration(i);
        }
        return duration;
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
