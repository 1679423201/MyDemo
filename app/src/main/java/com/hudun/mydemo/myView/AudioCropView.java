package com.hudun.mydemo.myView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

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
    public AudioCropView(Context context) {
        this(context,null);
    }

    public AudioCropView(Context context, @Nullable AttributeSet attrs) {
        this(context,null,0);
    }

    public AudioCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}

