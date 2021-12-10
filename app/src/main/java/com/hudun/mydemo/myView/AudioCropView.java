package com.hudun.mydemo.myView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.text.TextPaint;
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
    private static final String TAG = "VIEW_TEXT";
    private static final int IDLE = -1;//初始状态
    private static final int STATE_MOVING_START = 0;//移动左边
    private static final int STATE_MOVING_END = 1;//移动右边
    private static final int STATE_MOVING_CURSOR = 2;//移动cursor
    private static final int TEXT_INDICATOR_SPACING = 10;//文本与操作区的间距
    private float lastX; //记录手指移动的初始值
    private int state = IDLE; //状态指示器
    private float minRange = 100; //最小间隔

    private Bitmap mPlayingCursorBitmap;//指示器
    private Bitmap mAudioWaveBitmap;    //波形图
    private Paint mPaint;
    private TextPaint mTextPaint;   //文字绘制笔
    private int mWidth; //视图宽度
    private int mHeight;    //视图高度

    private Rect pickedSrcRect; //选中的矩形
    private Rect pickedDescRect; //选中的矩形的位置
    private Rect movableRect; //可移动矩形
    private Rect startActionRect;   //手势响应矩形-左
    private Rect endActionRect;     //手势响应矩形-右
    private Rect cursorActionRect;  //手势响应矩形-播放条

    private int movableRectBackground = 0xfffbfafa; //背景颜色
    private final int DEFAULT_COLOR = 0xffff5077;
    private final int DEFAULT_COLOR_PRESSED = 0xffAA3751;
    private int color = DEFAULT_COLOR;//颜色
    private int colorPressed = DEFAULT_COLOR_PRESSED;//按下颜色
    private int total = 100000; //播放总时间
    private int startTime = 0, endTime = 100000;//开始时间,结束时间
    private int playingTime = 0;//正在播放的指示器

    private float actionRectWidth = 80; //手势响应宽度
    private float currentTime = 0f;  //当前时间
    private float startPosition , endPosition, cursorPosition;;  //开始位置,结束位置,当前位置
    private float cursorWidth = 20; //进度条宽度
    private float indicatorRadius = 10; //剪辑条上面圆的半径
    private float indicatorWidth = 5;   //剪辑条宽度

    private float textHeight = 0;
    private int textColor = 0x4D000000;
    private int textSize = 24;
    private PlayingTimeChangeListener playingTimeChangeListener;
    private CropTimeChangeListener cropTimeChangeListener;


    /***
     * 这里写一下剪辑的背景控制逻辑
     *
     */

    /***
     * ------------------------------------------------------------------------------------------------------------------------
     * 这里是一些初始化和系统方法
     */
    private void init(@Nullable AttributeSet attrs){
        if(attrs != null){
            @SuppressLint("Recycle") TypedArray array = getContext().obtainStyledAttributes(attrs,R.styleable.AudioCropView);
            indicatorWidth = array.getDimension(R.styleable.AudioCropView_indicator_width, 5);
            indicatorRadius = array.getDimension(R.styleable.AudioCropView_indicator_radius, 10);
            actionRectWidth = array.getDimension(R.styleable.AudioCropView_action_width, 80);
            startTime = array.getInt(R.styleable.AudioCropView_start_time, 0);
            endTime = array.getInt(R.styleable.AudioCropView_end_time, 100000);
            playingTime = array.getInt(R.styleable.AudioCropView_playing_time, 0);
            cursorWidth = array.getDimension(R.styleable.AudioCropView_cursor_width, 20);
            total = array.getInt(R.styleable.AudioCropView_total, 100000);
            color = array.getColor(R.styleable.AudioCropView_color, DEFAULT_COLOR);
            movableRectBackground = array.getColor(R.styleable.AudioCropView_movable_rect_background, 0xfffbfafa);

            if(startTime<0) startTime = 0;
            else if (startTime > total) startTime = total;
            if(endTime<startTime) endTime = startTime;
            else if (endTime > total) endTime = total;
            if(playingTime < 0) playingTime = 0;
            else if(playingTime > total) playingTime = total;
        }
            //从资源中获取Bitmap对象
            Bitmap bitmapMain = BitmapFactory.decodeResource(getResources(), R.drawable.ic_audio_wave);
            mPlayingCursorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_playing_cursor);
            mAudioWaveBitmap = Bitmap.createBitmap(bitmapMain.getWidth() + 10, bitmapMain.getHeight() *2
                    , bitmapMain.getConfig());
            Canvas canvas = new Canvas(mAudioWaveBitmap);
            canvas.drawBitmap(bitmapMain, 5, mAudioWaveBitmap.getHeight() / 4f, null);
            bitmapMain.recycle();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(textColor);
            mTextPaint.setTextSize(textSize);
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            pickedSrcRect = new Rect();
            pickedDescRect = new Rect();
            startActionRect = new Rect();
            endActionRect = new Rect();
            cursorActionRect = new Rect();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawPickedArea(canvas);
        drawPlayingCursor(canvas);
        drawStartIndicator(canvas);
        drawEndIndicator(canvas);
    }
    /***
     *  手指触摸事件，主要用于进度条和剪辑条的调节
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            /***
             * 点击下去的瞬间，这里要做的事情有：
             * 1：判断点击的是左条，右条，还是播放条
             * 2：根据点击的地方为state赋相应的值
             */
            case MotionEvent.ACTION_DOWN:{
                lastX =  event.getX();
                int y = (int) event.getY();
                if(startActionRect.contains((int)lastX, y)){
                    state = STATE_MOVING_START;
                    Log.d(TAG, "onTouchEvent: left");
                    invalidate();
                    if(cropTimeChangeListener != null){
                        cropTimeChangeListener.onClickLeft();
                    }
                }else if(endActionRect.contains((int)lastX, y)){
                    Log.d(TAG, "onTouchEvent: right");
                    if(cropTimeChangeListener != null){
                        cropTimeChangeListener.onClickRight();
                    }
                    state = STATE_MOVING_END;
                    invalidate();
                }
                else if(cursorActionRect.contains((int)lastX, y)){
                    Log.d(TAG, "onTouchEvent: mid");
                    if(playingTimeChangeListener != null){
                        playingTimeChangeListener.onStartMoving();
                    }
                    state = STATE_MOVING_CURSOR;
                }
                getParent().requestDisallowInterceptTouchEvent(true);   //屏蔽父View的OnTouchEven
                break;
            }
            /***
             * 手指移动，由于前面已经判断过手指落在了哪个控件旁边
             * 因此这里只需要根据前面的判断做分析即可
             * 大致逻辑：剪辑条可以在另一条剪辑条的一侧任意移动，但是不能越过
             *          播放条可以在整个movableRect上移动，但是不能越过
             */
            case MotionEvent.ACTION_MOVE:{
                float dx = event.getX() - lastX;
                switch (state){
                    case STATE_MOVING_START:{
                        startPosition += dx;
                        if(startPosition < getPaddingLeft() + indicatorRadius){
                            startPosition = getPaddingLeft() + indicatorRadius;
                        }
                        if(startPosition > endPosition - minRange){
                            startPosition = endPosition - minRange;
                        }
                        convertPositionToValue();
                        refreshPickedRect();
                        refreshCropActionRect();
                        if(cropTimeChangeListener != null){
                            cropTimeChangeListener.onLeftChanging(startTime);
                        }
                        invalidate();
                        break;
                    }
                    case STATE_MOVING_END:{
                        endPosition += dx;
                        if(endPosition > mWidth - getPaddingRight() - indicatorRadius){
                            endPosition = mWidth - getPaddingRight() - indicatorRadius;
                        }
                        if(endPosition < startPosition + minRange){
                            endPosition = startPosition + minRange;
                        }
                        convertPositionToValue();
                        refreshPickedRect();
                        refreshCropActionRect();
                        if(cropTimeChangeListener != null){
                            cropTimeChangeListener.onRightChanging(endTime);
                        }
                        invalidate();
                        break;
                    }
                    case STATE_MOVING_CURSOR:{
                        cursorPosition += dx;
                        if(cursorPosition < startPosition){
                            cursorPosition = startPosition;
                        }else if(cursorPosition > endPosition){
                            cursorPosition = endPosition;
                        }
                        refreshCursorActionRect();
                        convertCursorPositionToPlayingTime();
                        if(playingTimeChangeListener != null){
                            playingTimeChangeListener.onToMoving(playingTime);
                        }
                        invalidate();
                    }
                }
                lastX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP:{
                if(state == STATE_MOVING_START){
                    if(cropTimeChangeListener != null){
                        cropTimeChangeListener.onLeftChanged((int)startPosition);
                    }
                    cursorPosition = startPosition;
                }
                else if (state == STATE_MOVING_END) {
                    if(cropTimeChangeListener != null){
                        cropTimeChangeListener.onRightChanged((int)endPosition);
                    }
                }
                else if(state == STATE_MOVING_CURSOR){
                    if(playingTimeChangeListener != null){
                        playingTimeChangeListener.onToMoved(playingTime);
                    }
                }
                state = IDLE;
                invalidate();
                break;
            }
        }
        return true;
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
        refreshCropActionRect();
        refreshCursorActionRect();
    }

    /***
     * ------------------------------------------------------------------------------------------------------------------------
     * 这里是内部使用的方法
     */
    /***
     * 绘制图形背景
     * 包括纯色背景和波形图
     * @param canvas
     */
    public void drawBackground(Canvas canvas) {
        mPaint.setColor(movableRectBackground);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, movableRect.top, mWidth, movableRect.bottom,mPaint);
        canvas.drawBitmap(mAudioWaveBitmap
                , new Rect(0,0,mAudioWaveBitmap.getWidth(),mAudioWaveBitmap.getHeight())
                , movableRect, null);
    }



    /***
     * 设置可移动矩形
     */
    void resizeMovableRect(){
        float[] textBounds = getTextBounds(mTextPaint, "00:00");
        textHeight = textBounds[1];
        movableRect = new Rect((int) (getPaddingLeft() + indicatorRadius + 0.5f),
                (int) (getPaddingTop() + 2 * indicatorRadius + 0.5f),
                (int) (mWidth - getPaddingRight() - indicatorRadius + 0.5f),
                (int) (mHeight - getPaddingBottom() - 2 * indicatorRadius - textHeight - TEXT_INDICATOR_SPACING));
    }

    /***
     * 获取文本边界
     * @param paint
     * @param txt
     * @return
     */
    private float[] getTextBounds(Paint paint, String txt) {
        Rect rect = new Rect();
        float[] floats = new float[2];
        paint.getTextBounds(txt, 0, txt.length(), rect);
        int w = rect.width();
        int h = rect.height();
        floats[0] = w;
        floats[1] = h;
        return floats;
    }

    /***
     * todo:1.播放条自动播放 √
     * todo:2.添加剪辑条 √
     * todo:3.剪辑条可调节 √
     * -2021-12-06
     */
    /***
     * todo:1.重新添加剪辑条
     * todo:2.设置外部监听
     */

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
    /**
     * 刷新手势响应位置
     */
    private void refreshCropActionRect() {
        startActionRect.left = (int) (startPosition - actionRectWidth / 2f + 0.5f);
        startActionRect.right = (int) (startPosition + actionRectWidth / 2f + 0.5f);
        startActionRect.top = 0;
        startActionRect.bottom = movableRect.bottom;
        endActionRect.left = (int) (endPosition - actionRectWidth / 2f + 0.5f);
        endActionRect.right = (int) (endPosition + actionRectWidth / 2f + 0.5);
        endActionRect.top = movableRect.top;
        endActionRect.bottom = mHeight;
    }

    /**
     * 刷新指针响应位置
     */
    private void refreshCursorActionRect() {
        cursorActionRect.left = (int) (cursorPosition - actionRectWidth / 2f + 0.5f);
        cursorActionRect.right = (int) (cursorPosition + actionRectWidth / 2f + 0.5f);
        cursorActionRect.top = movableRect.top;
        cursorActionRect.bottom = movableRect.bottom;
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
     * todo setColorFilter 总结
     */

    /***
     *  绘制剪辑开始条
     * @param canvas
     */
    private void drawStartIndicator(Canvas canvas) {
        mPaint.setStrokeWidth(indicatorWidth); //设置画笔粗细
        mPaint.setStyle(Paint.Style.FILL);
        float radius = state == STATE_MOVING_START ? indicatorRadius + 5 : indicatorRadius;
        int color = state == STATE_MOVING_START ? colorPressed : this.color;
        mPaint.setColor(color);
        canvas.drawRoundRect(startPosition - indicatorWidth/2f,
                (float) getPaddingTop() + radius, startPosition + indicatorWidth/2f,
                (float) movableRect.bottom, indicatorRadius,indicatorWidth,
                mPaint);
        canvas.drawCircle(startPosition, getPaddingTop()+radius, indicatorRadius, mPaint);
    }
    /***
     *  绘制剪辑结束条
     * @param canvas
     */
    private void drawEndIndicator(Canvas canvas) {
        mPaint.setStrokeWidth(indicatorWidth); //设置画笔粗细
        mPaint.setStyle(Paint.Style.FILL);
        float radius = state == STATE_MOVING_END ? indicatorRadius + 5 : indicatorRadius;
        int color = state == STATE_MOVING_END ? colorPressed : this.color;
        mPaint.setColor(color);
        canvas.drawRoundRect(endPosition + indicatorWidth/2f,
                (float) getPaddingTop() +  radius, endPosition - indicatorWidth/2f,
                (float) movableRect.bottom, indicatorRadius,indicatorWidth,
                mPaint);
        canvas.drawCircle(endPosition, getPaddingTop()+movableRect.bottom + radius, indicatorRadius, mPaint);
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
        canvas.drawBitmap(mPlayingCursorBitmap, srcRect, dstRect, null);
    }

    public void drawMusicText(Canvas canvas){

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
    /***
     * ------------------------------------------------------------------------------------------------------------------------
     * 这里是暴露给外部的方法和构造方法
     */
    /***
     * 播放条开始播放
     */
    public void startPlaying(float time){
        cursorPosition ++;
        refreshPickedRect();
        postInvalidate();
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

    /***
     * 暴露给外部的方法
     * 1.调整播放进度条，操作-playingTime变量-setPlayingTime
     * 2.调整剪辑头，操作-startTime变量-setStartTime
     * 3.调整剪辑尾，操作-endTime变量-setEndTime
     * 4.设置播放总时间，操作-total变量-setTotal
     * 5.设置一些get方法，主要是time相关
     */

    public void setPlayingTime(int playingTime){
        this.playingTime = playingTime;
        convertPlayingTimeToCursorPosition();
        refreshCursorActionRect();
        postInvalidate();
    }

    public void setStartTime(int startTime){
        this.startTime = startTime;
        convertValueToPosition();
        refreshCropActionRect();
        postInvalidate();
    }
    public void setEndTime(int endTime){
        this.endTime = endTime;
        convertValueToPosition();
        refreshCropActionRect();
        postInvalidate();
    }
    public void setTotal(int total){
        this.total = total;
        convertValueToPosition();
        convertPlayingTimeToCursorPosition();
        refreshCropActionRect();
        refreshPickedRect();
        postInvalidate();
    }

    public int getPlayingTime() {
        return playingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getTotal() {
        return total;
    }

    /***
     * 接口规则：
     * 1.外部类监听本VIew相关事件
     * 2.要监听的事件有播放事件和剪辑事件
     * 3.播放事件-开始，移动中，移动结束，播放结束
     * 4.剪辑事件-点击剪辑条，移动剪辑条，放下剪辑条（分前后剪辑条）
     */
    public interface PlayingTimeChangeListener{
        void onStartMoving();   //开始自动移动
        void onToMoving(int position);  //开始拖动
        void onToMoved(int position);   //拖动结束
        void onEndMoving();     //到终点了
    }
    public interface CropTimeChangeListener{
        void onClickLeft(); //点击左
        void onLeftChanging(int position);
        void onLeftChanged(int position);
        void onClickRight();//点击右
        void onRightChanging(int position);
        void onRightChanged(int position);
    }
    public void setPlayingTimeChangeListener(PlayingTimeChangeListener listener){
        this.playingTimeChangeListener = listener;
    }
    public void setCropTimeChangeListener(CropTimeChangeListener listener){
        this.cropTimeChangeListener = listener;
    }
}

