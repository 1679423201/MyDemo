package com.hudun.mydemo.myView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * <pre>
 *      @ClassName MySeekbar
 *      @Author  :YMD
 *      @E-mail  :1679423201@qq.com
 *      @Date 2021/12/21 9:36
 *      @Desc    :
 *      @Version :1.0
 * </pre>
 */
public class MySeekbar extends androidx.appcompat.widget.AppCompatSeekBar {
    public MySeekbar(Context context) {
        super(context);
    }

    public MySeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
