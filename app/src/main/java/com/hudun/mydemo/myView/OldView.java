package com.hudun.mydemo.myView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.hudun.mydemo.R;


public class OldView extends View {
    private static final int IDLE = -1;//初始状态
    private static final int STATE_MOVING_START = 0;//移动左边
    private static final int STATE_MOVING_END = 1;//移动右边
    private static final int STATE_MOVING_CURSOR = 2;//移动cursor

    private static final int TEXT_INDICATOR_SPACING = 10;//文本与操作区的间距
    private int state = IDLE;

    private int mWidth;
    private int mHeight;
    private Bitmap mAudioWaveBitmap;//背景波形图
    private Bitmap mPlayingCursorBitmap;//播放位置指示图片
    private Rect startActionRect;
    private Rect endActionRect;
    private Rect cursorActionRect;
    private float indicatorRadius = 10; //进度条上面圆的半径
    private float indicatorWidth = 5;   //进度条宽度
    private float actionRectWidth = 80;
    private float startPosition;
    private float endPosition = 10;
    private float cursorPosition;
    private int total = 100000;
    private int startTime = 0, endTime = 100000;
    private float lastX;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private Rect pickedSrcRect, pickedDstRect;
    private Rect movableRect;
    private float textHeight = 0;


    private final int DEFAULT_COLOR = 0xffff5077;
    private final int DEFAULT_COLOR_PRESSED = 0xffAA3751;
    private int color = DEFAULT_COLOR;//颜色
    private int colorPressed = DEFAULT_COLOR_PRESSED;//按下颜色
    private int textColor = 0x4D000000;//文本颜色
    private int textSize = 24;
    private float cursorWidth = 20; //进度条左右的空白宽度
    private boolean initialized;
    private boolean keepInCropArea;
    private boolean cursorMovable;//Cursor是否允许滑动
    private int minTimeRange = 1000;//最小的时间间隔
    private boolean cropEnabled = true;

    private int playingTime;//正在播放的指示器

    private int movableRectBackground = 0xfffbfafa;


    //事件
    private ValueChangeListener valueChangeListener;
    private PlayingTimeChangeListener playingTimeChangeListener;
    private float minRange;


    public int getMovableRectBackground() {
        return movableRectBackground;
    }

    public void setMovableRectBackground(int movableRectBackground) {
        this.movableRectBackground = movableRectBackground;
        postInvalidate();
    }

    /**
     * 设置开始值结束值变化的监听
     *
     * @param valueChangeListener
     */
    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    /**
     * 设置播放位置变更事件
     *
     * @param playingTimeChangeListener
     */
    public void setPlayingTimeChangeListener(PlayingTimeChangeListener playingTimeChangeListener) {
        this.playingTimeChangeListener = playingTimeChangeListener;
    }


    public boolean isCursorMovable() {
        return cursorMovable;
    }

    public void setCursorMovable(boolean cursorMovable) {
        this.cursorMovable = cursorMovable;
    }

    public boolean isKeepInCropArea() {
        return keepInCropArea;
    }

    public void setKeepInCropArea(boolean keepInCropArea) {
        this.keepInCropArea = keepInCropArea;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        postInvalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        postInvalidate();
    }

    public int getColorPressed() {
        return colorPressed;
    }

    public void setColorPressed(int colorPressed) {
        this.colorPressed = colorPressed;
        postInvalidate();
    }

    /**
     * 是否允许剪辑
     */
    public boolean isCropEnabled() {
        return cropEnabled;
    }

    /**
     * 设置是否允许拖拽剪辑功能
     *
     * @param cropEnabled
     */
    public void setCropEnabled(boolean cropEnabled) {
        this.cropEnabled = cropEnabled;
        resizeMovableRect();
        postInvalidate();
    }

    public int getPlayingTime() {
        return playingTime;
    }

    /**
     * 设置播放位置
     *
     * @param playingTime
     */
    public void setPlayingTime(int playingTime) {
        this.playingTime = playingTime;
        if (initialized) {
            convertPlayingTimeToCursorPosition();
            refreshCursorActionRect();
        }
        postInvalidate();
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        if (initialized) {
            convertValueToPosition();
            convertPlayingTimeToCursorPosition();
            refreshCropActionRect();
            refreshPickedRect();
            postInvalidate();
        }


    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
        if (initialized) {
            convertValueToPosition();
            refreshCropActionRect();
            refreshPickedRect();
            postInvalidate();
        }
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
        if (initialized) {
            convertValueToPosition();
            refreshCropActionRect();
            refreshPickedRect();
            postInvalidate();
        }

    }

    /**
     * 设置裁剪时间间隔
     *
     * @param startTime
     * @param endTime
     */
    public void setTimeRange(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        if (initialized) {
            convertValueToPosition();
            refreshCropActionRect();
            refreshPickedRect();
            postInvalidate();
        }
    }

    public OldView(Context context) {
        super(context);
        init(null);
    }

    public OldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private static final String TAG = "CROP_VIEW_TEXT";
    public void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            Log.d(TAG, "init: 加载 attrs");
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AudioCropView);
            ColorStateList colorStateList = a.getColorStateList(R.styleable.AudioCropView_color);
            if (colorStateList != null) {
                Log.d(TAG, "init: 波形图 的 colorList");
                this.color = colorStateList.getDefaultColor();
                colorPressed = colorStateList.getColorForState(new int[]{android.R.attr.state_pressed}, DEFAULT_COLOR_PRESSED);
            }

            //从attrs中获取一些属性
            indicatorWidth = a.getDimensionPixelSize(R.styleable.AudioCropView_indicator_width, 5);
            indicatorRadius = a.getDimensionPixelSize(R.styleable.AudioCropView_indicator_radius, 10);
            actionRectWidth = a.getDimensionPixelSize(R.styleable.AudioCropView_action_width, 80);
            cropEnabled = a.getBoolean(R.styleable.AudioCropView_crop_enabled, true);
            cursorMovable = a.getBoolean(R.styleable.AudioCropView_cursor_movable, false);
            total = a.getInt(R.styleable.AudioCropView_total, 100000);
            startTime = a.getInt(R.styleable.AudioCropView_start_time, 0);
            movableRectBackground = a.getColor(R.styleable.AudioCropView_movable_rect_background,0xB2FFFFFF);

            if (startTime < 0) {
                startTime = 0;
            } else if (startTime > total) {
                startTime = total;
            }
            endTime = a.getInt(R.styleable.AudioCropView_end_time, 100000);
            if (endTime < startTime) {
                endTime = startTime;
            } else if (endTime > total) {
                endTime = total;
            }
            playingTime = a.getInt(R.styleable.AudioCropView_playing_time, 0);
            if (playingTime < 0) {
                playingTime = 0;
            } else if (playingTime > total) {
                playingTime = total;
            }
            textColor = a.getColor(R.styleable.AudioCropView_text_color, 0x4D000000);
            textSize = a.getDimensionPixelSize(R.styleable.AudioCropView_text_size, 24);
            cursorWidth = a.getDimensionPixelSize(R.styleable.AudioCropView_cursor_width, 20);
            keepInCropArea = a.getBoolean(R.styleable.AudioCropView_keep_in_cropArea, false);
            a.recycle();
        }


        try {
            Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_audio_wave);
            //给位图上下留点空间
            mAudioWaveBitmap = Bitmap.createBitmap(tempBitmap.getWidth() + 10, tempBitmap.getHeight() * 2, tempBitmap.getConfig());
            Canvas canvas = new Canvas(mAudioWaveBitmap);
            canvas.drawBitmap(tempBitmap, 5, mAudioWaveBitmap.getHeight() / 4f, null);
            tempBitmap.recycle();
            mPlayingCursorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_playing_cursor);
        } catch (OutOfMemoryError e) {
            System.gc();
            Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_audio_wave);
            //给位图上下留点空间
            mAudioWaveBitmap = Bitmap.createBitmap(tempBitmap.getWidth() + 10, tempBitmap.getHeight() * 2, tempBitmap.getConfig());
            Canvas canvas = new Canvas(mAudioWaveBitmap);
            canvas.drawBitmap(tempBitmap, 5, mAudioWaveBitmap.getHeight() / 4f, null);
            tempBitmap.recycle();
            mPlayingCursorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_playing_cursor);
            e.printStackTrace();
        }


        startActionRect = new Rect();
        endActionRect = new Rect();
        cursorActionRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        pickedSrcRect = new Rect();
        pickedDstRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);

        if (cropEnabled) {
            drawPickedArea(canvas);
        }
        drawPlayingCursor(canvas);
        if (cropEnabled) {
            drawStartIndicator(canvas);
            drawEndIndicator(canvas);
        }

    }

    /**
     * 绘制播放位置指示器
     *
     * @param canvas
     */
    private void drawPlayingCursor(Canvas canvas) {

        Rect srcRect = new Rect(0, 0, mPlayingCursorBitmap.getWidth(), mPlayingCursorBitmap.getHeight());
        Rect dstRect = new Rect((int) (cursorPosition - cursorWidth / 2f + 0.5f),
                movableRect.top,
                (int) (cursorPosition + cursorWidth / 2f + 0.5f),
                movableRect.bottom);
        canvas.drawBitmap(mPlayingCursorBitmap, srcRect, dstRect, null);
    }


    /**
     * 绘制选中区域
     *
     * @param canvas
     */
    private void drawPickedArea(Canvas canvas) {
        mPaint.setColor(ColorUtils.setAlphaComponent(color, 25));
        Paint filterPaint = new Paint();
        filterPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        canvas.drawRect(pickedDstRect, mPaint);
        canvas.drawBitmap(mAudioWaveBitmap, pickedSrcRect, pickedDstRect, filterPaint);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(movableRectBackground);
        canvas.drawRect(0, movableRect.top, mWidth, movableRect.bottom, mPaint);
        canvas.drawBitmap(mAudioWaveBitmap,
                new Rect(0, 0, mAudioWaveBitmap.getWidth(), mAudioWaveBitmap.getHeight()),
                movableRect,
                null);
    }

    /**
     * 绘制结束指示器
     *
     * @param canvas
     */
    private void drawEndIndicator(Canvas canvas) {
        mPaint.setStrokeWidth(indicatorWidth);
        mPaint.setStyle(Paint.Style.FILL);
        float radius = state == STATE_MOVING_END ? indicatorRadius + 2 : indicatorRadius;
        int color = state == STATE_MOVING_END ? colorPressed : this.color;
        mPaint.setColor(color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(endPosition - indicatorWidth / 2f,
                    (float) movableRect.top,
                    endPosition + indicatorWidth / 2f,
                    movableRect.bottom + indicatorRadius,
                    indicatorWidth,
                    indicatorWidth,
                    mPaint);
        } else {
            canvas.drawRect(endPosition - indicatorWidth / 2f,
                    (float) movableRect.top,
                    endPosition + indicatorWidth / 2f,
                    movableRect.bottom + indicatorRadius,
                    mPaint);
        }
        canvas.drawCircle(endPosition,
                movableRect.bottom + indicatorRadius,
                radius,
                mPaint);
    }

    /**
     * 绘制开始指示器
     *
     * @param canvas
     */
    private void drawStartIndicator(Canvas canvas) {
        mPaint.setStrokeWidth(indicatorWidth);
        mPaint.setStyle(Paint.Style.FILL);
        float radius = state == STATE_MOVING_START ? indicatorRadius + 2 : indicatorRadius;
        int color = state == STATE_MOVING_START ? colorPressed : this.color;
        mPaint.setColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(startPosition - indicatorWidth / 2,
                    getPaddingTop() + indicatorRadius,
                    startPosition + indicatorWidth / 2,
                    movableRect.bottom,
                    indicatorWidth,
                    indicatorWidth,
                    mPaint);
        } else {
            canvas.drawRect(startPosition - indicatorWidth / 2,
                    getPaddingTop() + indicatorRadius,
                    startPosition + indicatorWidth / 2,
                    movableRect.bottom,
                    mPaint);
        }
        canvas.drawCircle(startPosition,
                getPaddingTop() + indicatorRadius,
                radius,
                mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        resizeMovableRect();
        convertValueToPosition();
        convertPlayingTimeToCursorPosition();
        refreshCropActionRect();
        refreshPickedRect();
        minRange = minTimeRange * movableRect.width() / (float) total;
        initialized = true;
    }

    /**
     * 重新设置可移动矩形
     */
    private void resizeMovableRect() {
        if (cropEnabled) {
            float[] textBounds = getTextBounds(mTextPaint, "00:00");
            textHeight = textBounds[1];
            movableRect = new Rect((int) (getPaddingLeft() + indicatorRadius + 0.5f),
                    (int) (getPaddingTop() + 2 * indicatorRadius + 0.5f),
                    (int) (mWidth - getPaddingRight() - indicatorRadius + 0.5f),
                    (int) (mHeight - getPaddingBottom() - 2 * indicatorRadius - textHeight - TEXT_INDICATOR_SPACING + 0.5f));
        } else {
            textHeight = 0;
            movableRect = new Rect(getPaddingLeft(),
                    getPaddingTop(),
                    mWidth - getPaddingRight(),
                    mHeight - getPaddingBottom());
        }
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (endActionRect.contains((int) (event.getX() + 0.5f), (int) (event.getY() + 0.5f)) && cropEnabled) {
                state = STATE_MOVING_END;
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
            } else if (startActionRect.contains((int) (event.getX() + 0.5f), (int) (event.getY() + 0.5f)) && cropEnabled) {
                state = STATE_MOVING_START;
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
            } else if (cursorMovable && cursorActionRect.contains((int) (event.getX() + 0.5f), (int) (event.getY() + 0.5f))) {
                state = STATE_MOVING_CURSOR;
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                if (playingTimeChangeListener != null) {
                    playingTimeChangeListener.onPlayingTimeChangeStart();
                }
            } else {
                state = IDLE;
            }
            lastX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = event.getX() - lastX;
            if (dx != 0) {
                if (state == STATE_MOVING_START) {
                    startPosition += dx;
                    if (startPosition < getPaddingLeft() + indicatorRadius) {
                        startPosition = getPaddingLeft() + indicatorRadius;
                    } else if (startPosition > endPosition - minRange) {
                        startPosition = endPosition - minRange;
                    } else if (startPosition > mWidth - getPaddingRight() - indicatorRadius) {
                        startPosition = mWidth - getPaddingRight() - indicatorRadius;
                    }
                    refreshCropActionRect();
                    refreshPickedRect();
                    int oldStart = startTime;
                    convertPositionToValue();
                    //触发事件
                    if (valueChangeListener != null && oldStart != startTime) {
                        valueChangeListener.onStartValueChanging(startTime);
                    }

                    invalidate();
                } else if (state == STATE_MOVING_END) {
                    endPosition += dx;
                    if (endPosition < getPaddingLeft() + indicatorRadius) {
                        endPosition = getPaddingLeft() + indicatorRadius;
                    } else if (endPosition < startPosition + minRange) {
                        endPosition = startPosition + minRange;
                    } else if (endPosition > mWidth - getPaddingRight() - indicatorRadius) {
                        endPosition = mWidth - getPaddingRight() - indicatorRadius;
                    }
                    refreshCropActionRect();
                    int oldEnd = endTime;
                    convertPositionToValue();
                    if (valueChangeListener != null && oldEnd != endTime) {
                        valueChangeListener.onEndValueChanging(endTime);
                    }
                    refreshPickedRect();
                    invalidate();
                } else if (state == STATE_MOVING_CURSOR) {
                    cursorPosition += dx;
                    if (keepInCropArea) {
                        if (cursorPosition > endPosition) {
                            cursorPosition = endPosition;
                        } else if (cursorPosition < startPosition) {
                            cursorPosition = startPosition;
                        }
                    } else {
                        if (cursorPosition < getPaddingLeft() + indicatorRadius) {
                            cursorPosition = getPaddingLeft() + indicatorRadius;
                        } else if (cursorPosition > mWidth - getPaddingRight() - indicatorRadius) {
                            cursorPosition = mWidth - getPaddingRight() - indicatorRadius;
                        }
                    }
                    int oldPlayingTime = playingTime;
                    convertCursorPositionToPlayingTime();
                    refreshCursorActionRect();
                    if (playingTimeChangeListener != null && oldPlayingTime != playingTime) {
                        playingTimeChangeListener.onPlayingTimeChanging(playingTime);
                    }
                    invalidate();
                }
                lastX = event.getX();
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {


            if (state == STATE_MOVING_START) {
                if (valueChangeListener != null) {
                    valueChangeListener.onStartValueChanged(startTime);
                }
            } else if (state == STATE_MOVING_END) {
                if (valueChangeListener != null) {
                    valueChangeListener.onEndValueChanged(endTime);

                }
            } else if (state == STATE_MOVING_CURSOR) {
                if (playingTimeChangeListener != null) {
                    playingTimeChangeListener.onPlayingTimeChanged(playingTime);
                }
            }


            invalidate();
            state = IDLE;

        }

        return true;
    }

    /**
     * 刷新选中区域
     */
    private void refreshPickedRect() {
        float offsetStartRatio = (startPosition - getPaddingLeft() - indicatorRadius) / movableRect.width();
        float offsetEndRatio = (endPosition - getPaddingLeft() - indicatorRadius) / movableRect.width();
        pickedSrcRect.left = (int) (mAudioWaveBitmap.getWidth() * offsetStartRatio + 0.5f);
        pickedSrcRect.right = (int) (mAudioWaveBitmap.getWidth() * offsetEndRatio + 0.5f);
        pickedSrcRect.top = 0;
        pickedSrcRect.bottom = mAudioWaveBitmap.getHeight();

        pickedDstRect.left = (int) (startPosition + 0.5f);
        pickedDstRect.right = (int) (endPosition + 0.5f);
        pickedDstRect.top = movableRect.top;
        pickedDstRect.bottom = movableRect.bottom;

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


    /**
     * 获取文本边界
     *
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

    @Override
    protected void finalize() throws Throwable {
        if (!mAudioWaveBitmap.isRecycled()) {
            mAudioWaveBitmap.recycle();
        }
        if (!mPlayingCursorBitmap.isRecycled()) {
            mPlayingCursorBitmap.recycle();
        }
        super.finalize();
    }

    public interface ValueChangeListener {
        void onStartValueChanging(int newValue);

        void onStartValueChanged(int newValue);

        void onEndValueChanging(int newValue);

        void onEndValueChanged(int newValue);
    }

    public interface PlayingTimeChangeListener {
        void onPlayingTimeChangeStart();

        void onPlayingTimeChanging(int playingTime);

        void onPlayingTimeChanged(int playingTime);
    }
}

