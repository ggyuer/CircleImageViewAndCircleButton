package com.example.administrator.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.administrator.myapplication.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
/**
 * Created by Administrator on 2016/8/25 0025.
 */
public class DefindView extends View {

    public final static String TAG = "DefindView";
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mRect;
    private Paint mPaint;

    private String textTitle;
    private int textColor;
    private float textSize;

    public DefindView(Context context) {
        this(context,null);
    }

    public DefindView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DefindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DefindView,defStyleAttr,0);
        int n = typedArray.getIndexCount();
        Log.d(TAG,"-------n-------->"+n);
        for (int i = 0; i < n; i++){
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.DefindView_textTitle:
                    textTitle = typedArray.getString(attr);
                    Log.d(TAG,"-------textTitle-------->"+textTitle);
                    break;
                case R.styleable.DefindView_textSize:
                    textSize = typedArray.getDimension(attr,10f);
                    Log.d(TAG,"-------attr-------->"+attr);
                    Log.d(TAG,"-------textSize-------->"+textSize);
                    break;
                case R.styleable.DefindView_textColor:
                    textColor = typedArray.getColor(attr, Color.GRAY);
                    break;

            }
        }
        typedArray.recycle();
        /**
         * 绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setTextSize(textSize);
       // mPaint.setColor(textColor);
        mRect = new Rect();//建一个矩形
        mPaint.getTextBounds(textTitle,0,textTitle.length(),mRect);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textTitle = getTextTitle();
                //postInvalidate();//刷新界面
                new Thread(new MyThread()).start();
                //invalidate();//单独调用线程不是安全的，违背单例模式，通过上面的方法才是正确调用
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                invalidate();
            }
        }
    };

    class MyThread implements Runnable{
        @Override
        public void run() {
            if (!Thread.currentThread().isInterrupted()){
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }

    private String getTextTitle(){

        Random random = new Random();
        Set<Integer> set = new HashSet<>();
        while (set.size()<4){
            int randomInteger = random.nextInt(10);
            set.add(randomInteger);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set){
            sb.append(""+i);
        }
        return sb.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
        mPaint.setColor(textColor);
        canvas.drawText(textTitle,getWidth() / 2 - mRect.width() / 2, getHeight() / 2 + mRect.height() / 2,mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthModle = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightModle = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if(widthModle == MeasureSpec.EXACTLY){
            width = widthSize;
            Log.d(TAG,"-------widthSize-------->"+widthSize);
        }else {
            mPaint.setTextSize(textSize);
            mPaint.getTextBounds(textTitle,0,textTitle.length(),mRect);
            float textWidth = mRect.width();
            int desire = (int)(getPaddingLeft()+textWidth+getPaddingRight());
            Log.d(TAG,"-------mRect.width()-------->"+getPaddingLeft()+"/"+textWidth+"/"+getPaddingRight());
            width = desire;
            Log.d(TAG,"-------width-------->"+width);
        }

        if(heightModle == MeasureSpec.EXACTLY){
            height = heightSize;
            Log.d(TAG,"-------heightSize-------->"+heightSize);
        }else{
            mPaint.setTextSize(textSize);
            mPaint.getTextBounds(textTitle, 0, textTitle.length(), mRect);
            float textHeight = mRect.height();
            Log.d(TAG,"-------mRect.height()-------->"+getPaddingTop() +"/"+ textHeight + "/"+getPaddingBottom());
            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            height = desired;
            Log.d(TAG,"-------height-------->"+height);
        }

        setMeasuredDimension(width,height);

        Log.d(TAG,"-------widthMeasureSpec-------->"+widthMeasureSpec);
        Log.d(TAG,"-------heightMeasureSpec-------->"+heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
