package com.hudun.mydemo.myView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.hudun.mydemo.R;

/**
 * <pre>
 *      @ClassName AudioCropView
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2021/12/1 16:35
 *      @Desc    : 模仿音频编辑音频进度条创建的View
 *      @Version :1.0
 * </pre>
 */
public class AudioCropView extends View {
    private Paint mPaint;
    private Bitmap mAudioWaveBitmap;    //波形图
    private int mWidth; //视图宽度
    private int mHeight;    //视图高度
    private Rect movableRect; //可移动矩形
    private int movableRectBackgroundColor = 0xfffbfafa; //背景颜色
    Rect mSrcRect;  //定义图像大小的矩形
    Rect mDestRect; //定义图像位置的矩形
    private Bitmap bitmapMain;

    public AudioCropView(Context context) {
        super(context);
        init(null);
    }

    public AudioCropView(Context context, @Nullable AttributeSet attrs) {
        this(context,null,0);
        init(attrs);
    }

    public AudioCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs){
            //从资源中获取Bitmap对象
            bitmapMain = BitmapFactory.decodeResource(getResources(), R.drawable.ic_audio_wave);
            mAudioWaveBitmap = Bitmap.createBitmap(bitmapMain.getWidth() + 10, bitmapMain.getHeight() *2
                    , bitmapMain.getConfig());
            Canvas canvas = new Canvas(mAudioWaveBitmap);
            mSrcRect = new Rect(0,0,bitmapMain.getWidth(),bitmapMain.getHeight());
            mDestRect = new Rect(0,mAudioWaveBitmap.getHeight()/4,bitmapMain.getWidth(),mAudioWaveBitmap.getHeight()/4*3);
            canvas.drawBitmap(bitmapMain, mSrcRect, mDestRect, null);
            bitmapMain.recycle();
            mPaint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        resizeMovableRect();
    }

    /***
     * 绘制图形背景
     * 包括纯色背景和波形图
     * @param canvas
     */
    public void drawBackground(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        mPaint.setAlpha(60);
        mPaint.setStyle(Paint.Style.FILL);
        Paint nPaint = new Paint();
        nPaint.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
        nPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, movableRect.top, mWidth, movableRect.bottom,mPaint);
        canvas.drawBitmap(mAudioWaveBitmap
                , new Rect(5,0,mAudioWaveBitmap.getWidth(),mAudioWaveBitmap.getHeight())
                , movableRect, nPaint);
    }

    /***
     * 绘制播放位置指示器
     * @param canvas
     */
    public void drawPlayingCursor(Canvas canvas){

    }

    /***
     * 设置可移动矩形
     */
    void resizeMovableRect(){
        movableRect = new Rect(0,0,mWidth,mHeight);
    }
}

