package com.hudun.mydemo.myView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
    private int MOVE_NULL = 0;  //不移动
    private int MOVE_LEFT = 1;  //移动左边
    private int MOVE_RIGHT = 2; //移动右边
    private int MOVE_ALL = 3;   //移动全部==不移动
    private int frag = 0; //手指按下位置，变量为以上4种

    private Paint mPaint;
    private Bitmap mAudioWaveBitmap;    //波形图
    private int mWidth; //视图宽度
    private int mHeight;    //视图高度
    private Rect movableRect; //可移动矩形
    private int movableRectBackgroundColor = 0xfffbfafa; //背景颜色
    private Rect mSrcRect;  //定义图像大小的矩形
    private Rect mDestRect; //定义图像位置的矩形
    private Bitmap mPlayingCursorBitmap;//指示器
    private Rect controlDestRect; //控制指示器位置的Rect
    private float startTime = 0f;    //开始时间
    private float finishTime = 100f;   //结束时间
    private float currentTime = 0f;  //当前时间

    private static final String TAG = "VIEW_TEXT";


    private void init(@Nullable AttributeSet attrs){
            //从资源中获取Bitmap对象
            Bitmap bitmapMain = BitmapFactory.decodeResource(getResources(), R.drawable.ic_audio_wave);
            mPlayingCursorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_playing_cursor);
            mAudioWaveBitmap = Bitmap.createBitmap(bitmapMain.getWidth() + 10, bitmapMain.getHeight() *2
                    , bitmapMain.getConfig());
            Canvas canvas = new Canvas(mAudioWaveBitmap);
            mSrcRect = new Rect(0,0,bitmapMain.getWidth(),bitmapMain.getHeight());
            mDestRect = new Rect(5,mAudioWaveBitmap.getHeight()/4,bitmapMain.getWidth(),mAudioWaveBitmap.getHeight()/4*3);
            canvas.drawBitmap(bitmapMain, mSrcRect, mDestRect, null);
            bitmapMain.recycle();
            mPaint = new Paint();
            controlDestRect = new Rect();
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
        canvas.drawRect(0, 0, mWidth, mHeight,mPaint);
        canvas.drawBitmap(mAudioWaveBitmap
                , new Rect(5,0,mAudioWaveBitmap.getWidth(),mAudioWaveBitmap.getHeight())
                , new Rect(5,0,mWidth, mHeight), nPaint);
    }

    /***
     * 绘制播放位置指示器
     * @param canvas
     */
    public void drawPlayingCursor(Canvas canvas){
        Rect srcRect = new Rect(0, 0, mPlayingCursorBitmap.getWidth(), mPlayingCursorBitmap.getHeight());
        canvas.drawBitmap(mPlayingCursorBitmap, srcRect, controlDestRect, null);
    }

    /***
     * 设置可移动矩形
     */
    void resizeMovableRect(){
        movableRect = new Rect(0,0,mWidth,mHeight);
    }

    /***
     * todo:1.播放条自动播放 √
     * todo:2.添加剪辑条 √
     * todo:3.剪辑条可调节 √
     * -2021-12-06
     */


    /***
     * 播放条开始播放
     */
    public void startPlaying(float time){
        controlDestRect.left = (int) (time/finishTime * mWidth);
        controlDestRect.right = (int) (time/finishTime * mWidth) + mPlayingCursorBitmap.getWidth();
        postInvalidate();
    }

    /***
     * 设置剪辑进度条
     * @param canvas
     */
    public void setClipBar(Canvas canvas){
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(99);
        canvas.drawRect(movableRect.left+30, 0, movableRect.left+50, mHeight, mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(movableRect.left+40, 40, 40, mPaint);
        mPaint.setAlpha(99);
        canvas.drawRect(movableRect.right-60, 0, movableRect.right-40, mHeight, mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(movableRect.right-50, mHeight-40, 40, mPaint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        float x = event.getRawX();
        float y = event.getRawY();
        Log.d(TAG, "onTouchEvent: x == " +x);
        Log.d(TAG, "onTouchEvent: y == " +y);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                frag = isMove(x,y); //记录手指按下位置
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                Log.d(TAG, "onTouchEvent:  === " + isMove(x,y));
                if(frag== 0) break;
                if(frag == 3) break;
                if(frag == 1){
                    movableRect.left = (int) x - 30;
                }else if(frag == 2){
                    movableRect.right = (int) x + 50;
                }
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP:break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, " onDraw: ");
        drawBackground(canvas);
        drawPlayingCursor(canvas);
        setClipBar(canvas);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, " onChange: ");
        mWidth = w;
        mHeight = h;
        resizeMovableRect();
        controlDestRect = new Rect(0, 0, mPlayingCursorBitmap.getWidth(), mHeight);
    }
    public AudioCropView(Context context) {
        super(context);
        init(null);
    }

    public AudioCropView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
        init(attrs);
    }

    public AudioCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    int isMove(float x, float y){
        float xLeft = (x-(movableRect.left+30));
        float xRight = (x-(movableRect.right-50));
        float Y = (y*y);
        if(xLeft > -20 && xLeft < 20){
            if(xRight > -20 && xRight < 20){
                return 3;
            }
            return 1;
        }
        if(xRight > -20 && xRight < 20){
            return 2;
        }
        return 0;
    }
    public interface ControlPlaying{
        void startPlaying();
    }
}

