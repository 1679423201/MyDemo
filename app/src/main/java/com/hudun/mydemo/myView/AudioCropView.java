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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

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
    private int movableRectBackgroundColor = 0xfffbfafa; //背景颜色
    private Bitmap mPlayingCursorBitmap;//指示器
    private Bitmap mAudioWaveBitmap;    //波形图
    private Paint mPaint;
    private int mWidth; //视图宽度
    private int mHeight;    //视图高度

    private Rect pickedSrcRect; //选中的矩形
    private Rect pickedDescRect; //选中的矩形的位置
    private Rect movableRect; //可移动矩形
    private Rect mSrcRect;  //定义图像大小的矩形
    private Rect mDestRect; //定义图像位置的矩形
    private Rect bgDestRect;
    private Rect bgSrcRect;

    private int cropBackground = 0xffff5077;
    private int movableRectBackground = 0xfffbfafa;
    private final int DEFAULT_COLOR = 0xffff5077;
    private final int DEFAULT_COLOR_PRESSED = 0xffAA3751;
    private int color = DEFAULT_COLOR;//颜色
    private int total = 100000;
    private int startTime = 0, endTime = 100000;//开始时间,结束时间
    private int playingTime = 0;//正在播放的指示器

    private float currentTime = 0f;  //当前时间
    private float startPosition , endPosition, cursorPosition;;  //开始位置,结束位置,当前位置
    private float cursorWidth = 30; //进度条左右的空白宽度
    private float indicatorRadius = 10; //进度条上面圆的半径
    private float indicatorWidth = 5;   //进度条宽度
    private float textHeight = 0;
    private static final int TEXT_INDICATOR_SPACING = 10;//文本与操作区的间距

    private static final String TAG = "VIEW_TEXT";

    /***
     * 这里写一下剪辑的背景控制逻辑
     *
     */


    private void init(@Nullable AttributeSet attrs){
            //从资源中获取Bitmap对象
            Bitmap bitmapMain = BitmapFactory.decodeResource(getResources(), R.drawable.ic_audio_wave);
            mPlayingCursorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_playing_cursor);
            mAudioWaveBitmap = Bitmap.createBitmap(bitmapMain.getWidth() + 10, bitmapMain.getHeight() *2
                    , bitmapMain.getConfig());
            Canvas canvas = new Canvas(mAudioWaveBitmap);
            canvas.drawBitmap(bitmapMain, 5, mAudioWaveBitmap.getHeight() / 4f, null);
            bitmapMain.recycle();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            pickedSrcRect = new Rect();
            pickedDescRect = new Rect();
    }




    /***
     * 绘制图形背景
     * 包括纯色背景和波形图
     * @param canvas
     */
    public void drawBackground(Canvas canvas) {
        mPaint.setColor(movableRectBackground);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, movableRect.top, mWidth, movableRect.top,mPaint);
        canvas.drawBitmap(mAudioWaveBitmap
                , new Rect(0,0,mAudioWaveBitmap.getWidth(),mAudioWaveBitmap.getHeight())
                , movableRect, null);
    }

    /***
     * 绘制播放位置指示器
     * @param canvas
     */
    public void drawPlayingCursor(Canvas canvas){
        Rect srcRect = new Rect(0, 0, mPlayingCursorBitmap.getWidth(), mPlayingCursorBitmap.getHeight());
        Rect dstRect = new Rect((int) (cursorPosition - cursorWidth / 2f + 0.5f),
                movableRect.top,
                (int) (cursorPosition + cursorWidth / 2f + 0.5f),
                movableRect.bottom);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawBitmap(mPlayingCursorBitmap, srcRect, dstRect, paint);
//        Rect srcRect = new Rect(0, 0, mPlayingCursorBitmap.getWidth(), mPlayingCursorBitmap.getHeight());
//        canvas.drawBitmap(mPlayingCursorBitmap, srcRect, pickedDescRect, null);
    }

    /***
     * 设置可移动矩形
     */
    void resizeMovableRect(){
        movableRect = new Rect((int) (getPaddingLeft() + indicatorRadius + 0.5f),
                (int) (getPaddingTop() + 2 * indicatorRadius + 0.5f),
                (int) (mWidth - getPaddingRight() - indicatorRadius + 0.5f),
                (int) (mHeight - getPaddingBottom() - 2 * indicatorRadius - textHeight - TEXT_INDICATOR_SPACING));
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
        cursorPosition ++;
        postInvalidate();
    }

    /***
     * 刷新剪辑框选中区域
     */
    private void refreshPickedRect() {
        float offsetStartRatio = (startPosition - getPaddingLeft() - indicatorRadius) / movableRect.width();
        float offsetEndRatio = (endPosition - getPaddingLeft() - indicatorRadius) / movableRect.width();
        pickedSrcRect.left = (int) (mAudioWaveBitmap.getWidth() * offsetStartRatio + 0.5f);
        pickedSrcRect.right = (int) (mAudioWaveBitmap.getWidth() * offsetEndRatio + 0.5f);
        pickedSrcRect.top = 0;
        pickedSrcRect.bottom = mAudioWaveBitmap.getHeight();

        pickedDescRect.left = (int) (startPosition + 0.5f);
        pickedDescRect.right = (int) (endPosition + 0.5f);
        pickedDescRect.top = movableRect.top;
        pickedDescRect.bottom = movableRect.bottom;

    }
    /***
     * 设置剪辑进度条
     * @param canvas
     */
    public void setClipBar(Canvas canvas){
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(99);
        canvas.drawRect(pickedSrcRect.left, 0, pickedSrcRect.left+20, mHeight, mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(pickedSrcRect.left+10, 20, 20, mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(pickedSrcRect.right-20, 0, pickedSrcRect.right, mHeight, mPaint);
        canvas.drawCircle(pickedSrcRect.right-10, mHeight-20, 20, mPaint);
    }

    /***
     *  绘制选中区域
     * @param canvas
     */
    private void drawPickedArea(Canvas canvas) {
        mPaint.setColor(ColorUtils.setAlphaComponent(color, 25));
        Paint filterPaint = new Paint();
        filterPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        canvas.drawRect(pickedDescRect, mPaint);
        canvas.drawBitmap(mAudioWaveBitmap, pickedSrcRect, pickedDescRect, filterPaint);
    }




    /***
     *  手指触摸事件，主要用于进度条调节
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, " onDraw: ");
        drawBackground(canvas);
        drawPickedArea(canvas);
        drawPlayingCursor(canvas);
        setClipBar(canvas);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, " onChange: ");
        mWidth = w;
        mHeight = h;
        resizeMovableRect();
        convertValueToPosition();
        convertPlayingTimeToCursorPosition();
        refreshPickedRect();

    }
    /**
     * 将值转化为位置
     */
    private void convertValueToPosition() {
        startPosition = getPaddingLeft() + indicatorRadius + movableRect.width() * (startTime / (float) total);
        endPosition = getPaddingLeft() + indicatorRadius + movableRect.width() * (endTime / (float) total);
    }

    /**
     * 将位置转化为值
     */
    private void convertPositionToValue() {
        startTime = (int) (total * ((startPosition - movableRect.left) / movableRect.width()) + 0.5f);
        endTime = (int) (total * ((endPosition - movableRect.left) / movableRect.width()) + 0.5f);
    }
    /**
     * 将播放时间转化为播放cursor的像素位置
     */
    private void convertPlayingTimeToCursorPosition() {
        cursorPosition = (int) (movableRect.left + movableRect.width() * (playingTime * 1.0f / total));
    }

    private void convertCursorPositionToPlayingTime() {
        playingTime = (int) (total * ((cursorPosition - movableRect.left) / movableRect.width()) + 0.5f);
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


    public interface ControlPlaying{
        void startPlaying();
    }
}

