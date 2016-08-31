package com.example.administrator.myview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.example.administrator.myapplication.R;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class CircleImageView extends ImageView {

    private static final String TAG = "CircleImageView";
    //缩放类型
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    //默认边界宽度和颜色
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    //默认边界是否有覆盖物
    private static final boolean DEFAULT_BORDER_OVERLAY = false;
    //显示图片的矩形
    private  final RectF mDrawableRectf = new RectF();
    //显示图片加外边界的矩形
    private final  RectF mBorderRectf = new RectF();
    //矩阵处理图片
    private final Matrix mShaderMatrix = new Matrix();
    //关联mBitmapShader（位图渲染）的画笔，使canvas在执行的时候可以切割原图（mBitmapPaint是关联了原图的画笔）
    private final Paint mBitmapPaint = new Paint();
    //外边界的画笔,与原图无关
    private final Paint mBorderPaint = new Paint();
    //定义圆形的默认颜色和宽度
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    //位图
    private Bitmap mBitmap;
    //位图渲染
    private BitmapShader mBitmapShader;
    //位图高度的宽度
    private int mBitmapWidth;
    private int mBitmapHeight;
    //图片半径
    private float mDrawableRadius;
    //带边框的图片半径
    private float mBorderRadius;
    //颜色过滤器
    private ColorFilter mColorFilter;

    //默认参数
    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverLay;


    public CircleImageView(Context context) {
        super(context);
        Log.d(TAG,"CircleImageView -- 构造函数1");
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        Log.d(TAG,"CircleImageView -- 构造函数2");
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView,defStyleAttr,0);
        mBorderWidth = array.getDimensionPixelSize(R.styleable.CircleImageView_border_width,DEFAULT_BORDER_WIDTH);
        mBorderColor = array.getColor(R.styleable.CircleImageView_border_color,DEFAULT_BORDER_COLOR);
        mBorderOverLay = array.getBoolean(R.styleable.CircleImageView_border_overlay,DEFAULT_BORDER_OVERLAY);
        array.recycle();
        Log.d(TAG,"CircleImageView -- 构造函数3");
        init();
    }

    /**
     * 保证setup函数的代码在构造方法执行完毕之后执行。
     */
    public void init(){
        super.setScaleType(SCALE_TYPE);
        mReady = true;
        if(mSetupPending){
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public  ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public  void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() == null){
            return;
        }
        //绘制内圆
        canvas.drawCircle(getWidth()/2,getHeight()/2,mDrawableRadius,mBitmapPaint);
        //绘制外边界
        if(mBorderWidth != 0){
            canvas.drawCircle(getWidth()/2,getHeight()/2,mBorderRadius,mBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
        Log.d(TAG,"------onSizeChanged");
    }

    public int getmBorderColor() {
        return mBorderColor;
    }

    public void setmBorderColor(int borderColor) {
        if(mBorderColor == borderColor){
          return;
        }
        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        postInvalidate();
    }


    public void setBorderColorResource(@ColorRes int borderColorRes) {
        setmBorderColor(ContextCompat.getColor(getContext(),borderColorRes));
    }

    public int getmBorderWidth() {
        return mBorderWidth;
    }

    public void setmBorderWidth(int borderWidth) {
        if(mBorderWidth == borderWidth){
            return;
        }
        mBorderWidth = borderWidth;
       setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverLay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverLay) {
            return;
        }
        mBorderOverLay = borderOverlay;
        setup();
    }
    /**
     * 以下四个函数都是
     * 复写ImageView的setImageXxx()方法
     * 注意这个函数先于构造函数调用之前调用
     * @param bm
     */

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getmBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getmBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getmBitmapFromDrawable(getDrawable());
        setup();
        Log.d(TAG,"-------setImageURI");
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if(mColorFilter == cf){
            return;
        }
        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        postInvalidate();
    }

    private Bitmap getmBitmapFromDrawable(Drawable drawable){
        if (drawable == null){
            return null;
        }
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable){
               bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,COLORDRAWABLE_DIMENSION,BITMAP_CONFIG);
            }else{
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),BITMAP_CONFIG);
            }
            //在canvas初始化的时候就传入了一个空的bitmap
            // 最后canvas中绘画的内容都被绘制到了bitmap中，从而得到了我们需要的bitmap
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0,0,getWidth(),getHeight());
            drawable.draw(canvas);
            return bitmap;
        }catch (OutOfMemoryError e){
            return null;
        }
    }

    private void setup(){

        if(!mReady){
            mSetupPending = true;
            return;
        }

        if (mBitmap == null){
            return;
        }

        //构建渲染器，用mBItmap位图来绘制渲染区域，参数值代表，如果图片太小的话，就拉伸。
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //设置画笔反锯齿
        mBitmapPaint.setAntiAlias(true);
        //设置画笔渲染器
        mBitmapPaint.setShader(mBitmapShader);
        //设置边界画笔样式
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔为空心
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        //取原图的宽高
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        //设置含边显示区域，就是CircleImageView的实际大小
        mBorderRectf.contains(0,0,getWidth(),getHeight());
        //计算含边界的ImageView的半径
        mBorderRadius = Math.min((mBorderRectf.height() - mBorderWidth)/2,(mBorderRectf.width() - mBorderWidth)/2);
        //初始显示图片为mBorderRectf(CircleImageView)的实际大小
        mDrawableRectf.set(mBorderRectf);
        if(!mBorderOverLay){
            //demo里始终执行
            //通过inset方法  使得图片显示的区域从mBorderRect大小上下左右内移边界的宽度形成区域，
            // 查看xml边界宽度为2dp（3px）,所以方形边长为就是160-4=156dp(234px)
            mDrawableRectf.inset(mBorderWidth,mBorderWidth);
        }
        //计算内圆的半径（出去边框的半径）
        mDrawableRadius = Math.min(mDrawableRectf.height()/2,(mDrawableRectf.width())/2);

        //设置渲染器的变换矩阵，即bitmap以何种形式缩放
        updateShaderMatrix();

        //手动触发ondraw()函数 完成最终的绘制
        postInvalidate();
    }

    private void updateShaderMatrix(){
        float scale;
        float dx = 0;
        float dy = 0;
        mShaderMatrix.set(null);
        //取最小缩放比例
        if((mBitmapHeight/mDrawableRectf.height()) > (mBitmapWidth/mDrawableRectf.width())){
            //y轴缩放 x轴平移 使得图片的y轴方向的边的尺寸缩放到图片显示区域（mDrawableRect）一样）
            scale = mDrawableRectf.height() / (float) mBitmapHeight;
            dx = (mDrawableRectf.width() - mBitmapWidth * scale) * 0.5f;
        }else{
            scale = mDrawableRectf.width() / (float)mBitmapWidth;
            dy = (mDrawableRectf.height() - mBitmapHeight * scale) * 0.5f;
        }
        //缩放
        mShaderMatrix.setScale(scale,scale);
        //平移
        mShaderMatrix.setTranslate((int) (dx + 0.5f) + mDrawableRectf.left,((int)(dy + 0.5f) + mDrawableRectf.top));
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }
}
